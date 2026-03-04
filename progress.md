# PulseB — Progress vs Spec

Reference: `PulseB_Project_SPEC_LOCKED.md` (v1.0, 2026-03-03)

---

## ✅ Done (matches spec)

### Phase 1 — Foundation (mostly)
- **Room**: `ActivityEntry` entity (id, timestamp, durationMinutes, activityText, wasMissed), `ActivityEntryDao`, `AppDatabase`
- **DataStore**: `SettingsDataStore` with base/current interval, start/end hour, popup duration, sound, vibration
- **Repositories**: `ActivityRepository`, `SettingsRepository` with flows and basic updates
- **Timeline**: `TimelineScreen` + `TimelineViewModel` with live Room data; list of entries with time and activity text
- **Stack**: Compose, no XML layouts; Kotlin; minSdk 26

### Phase 2 — Popup engine (partial)
- **AlarmScheduler**: Uses `setExactAndAllowWhileIdle`; `scheduleNextTrigger()` / `cancel()`
- **AlarmReceiver**: Starts `LoggingForegroundService` on alarm
- **LoggingForegroundService**: Foreground notification, channel "PulseB Logger", high priority, full-screen intent → `PopupActivity`
- **PopupActivity**: `setShowWhenLocked(true)`, `setTurnScreenOn(true)`; Compose `PopupScreen` with header, text field, Log button; normalizes activity with `trim().lowercase()`; broadcasts `ACTION_LOGGED` / `ACTION_MISSED`
- **IntervalEscalationEngine**: Implements `nextInterval(base, current, userLogged)` with 60 min cap and reset on real log

### Phase 3 — Suggestions + data (partial)
- **DAO**: `getTopSuggestions()` — top 4 by count, excludes `activityText = 'untracked'` and `wasMissed = 0`
- **ActivityRepository**: Exposes `getTopSuggestions()`
- **Normalization**: Popup saves activity as lowercased and trimmed

---

## 🔧 Needs fixing

### 1. Popup → service → next alarm (critical)
- **Issue**: PopupActivity broadcasts `ACTION_LOGGED` and `ACTION_MISSED`, but nothing receives them. Service never inserts "Untracked" on dismiss, updates `currentInterval`, schedules next alarm, or stops itself.
- **Fix**: In `LoggingForegroundService` (or a dedicated receiver): register for `ACTION_LOGGED` / `ACTION_MISSED`; on **ACTION_MISSED** insert one "Untracked" entry, read settings, call `IntervalEscalationEngine.nextInterval(..., userLogged = false)`, persist new `currentInterval`, call `AlarmScheduler.scheduleNextTrigger(nextTime)`, then `stopSelf()`. On **ACTION_LOGGED** do the same with `userLogged = true` (reset to base interval). Use `currentInterval` and quiet hours to compute `nextTime`.

### 2. AlarmScheduler not driven by settings
- **Issue**: Scheduling uses a fixed 10s test delay in MainActivity; no DataStore `baseInterval`/`currentInterval` or quiet hours (daily start/end).
- **Fix**: AlarmScheduler (or a use case) should read from SettingsRepository (base/current interval, start/end hour), enforce quiet hours (no trigger outside window), and schedule next trigger at `now + currentInterval` (or next allowed time after quiet hours).

### 3. Popup UI vs spec
- **Issue**: Spec: "What did you do in the last **X** minutes?" (X = current interval); top 4 suggestion **chips**; submit on **Enter**; **auto-dismiss** (e.g. 45s) that counts as miss.
- **Current**: Header is fixed "15 minutes"; no suggestion chips; no Enter handling; no auto-dismiss timer.
- **Fix**: Pass interval minutes into PopupScreen; load suggestions (e.g. from repository/use case) and show as chips; add `KeyEvent` handler for Enter to submit; start a timer (from settings `popupDurationSeconds`) that auto-dismisses and triggers same path as manual dismiss (ACTION_MISSED).

### 4. Timeline vs spec
- **Issue**: Spec: duration window per row; "coverage % for today" at top; group by day when scrolling back; tap entry → edit/delete; muted style for `wasMissed`.
- **Current**: No duration, no coverage %, no day grouping, no tap-to-edit/delete, no muted styling for missed.
- **Fix**: Add duration and coverage (today’s tracked vs configured window); group entries by day; add item click → edit/delete; style rows with `wasMissed == true` muted (e.g. greyed/reduced opacity).

### 5. Permissions (manifest + runtime)
- **Issue**: Spec: POST_NOTIFICATIONS, FOREGROUND_SERVICE, VIBRATE, WAKE_LOCK, SCHEDULE_EXACT_ALARM, USE_EXACT_ALARM (Android 13+), REQUEST_IGNORE_BATTERY_OPTIMIZATIONS. Manifest has SCHEDULE_EXACT_ALARM, FOREGROUND_SERVICE_*, POST_NOTIFICATIONS; USE_EXACT_ALARM commented out; VIBRATE, WAKE_LOCK, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS missing.
- **Fix**: Add to manifest: `VIBRATE`, `WAKE_LOCK`, `USE_EXACT_ALARM` (for 13+), `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`. Request battery optimization exemption at launch if needed.

### 6. SettingsDataStore / SettingsRepository
- **Issue**: Spec: theme (SYSTEM/LIGHT/DARK), and all settings writable. DataStore has no theme key; only `updateBaseInterval` and `updateCurrentInterval` are implemented.
- **Fix**: Add theme key and ThemeMode (or int); add update methods for start/end hour, popup duration, sound, vibration, theme. Expose via SettingsRepository so AlarmScheduler and UI use one source of truth.

### 7. Service: duration and stop
- **Issue**: On insert from popup, duration is hardcoded (15). Spec ties duration to the **interval** that just fired. Service never stops after handling the popup result.
- **Fix**: When inserting (from Popup or in service on ACTION_MISSED), set `durationMinutes` to the interval that was active for that window. After handling ACTION_LOGGED/ACTION_MISSED and scheduling next alarm, call `stopSelf()`.

### 8. MainActivity
- **Issue**: Schedules a one-off 10s alarm; doesn’t use Settings or startup flow from spec (e.g. schedule next alarm if within quiet hours).
- **Fix**: On launch, if within quiet hours, read settings and schedule next trigger from now using `currentInterval` and quiet hours; remove hardcoded 10s test trigger for production.

---

## ⏳ Pending (not started)

### Phase 4 — Stats screen
- Vico dependency and Compose integration
- Aggregation DAOs (or queries): time per activity (pie), hourly distribution (bar), most frequent by total time (list), tracked vs total window (coverage bar)
- Filters: today / this week / all time
- `GetCoverageUseCase` (and any other stats use cases)
- Stats UI: pie chart, bar chart, ranked list, coverage bar

### Phase 5 — Settings + export
- **Settings screen**: Full UI wired to DataStore (base interval, daily start/end, popup duration, sound, vibration, theme, JSON export, DB backup/restore)
- **JSON export**: Spec format (exportDate, entries with startTime, durationMinutes, activity, wasMissed); share sheet + save to Downloads
- **DB backup/restore**: Export/import full Room DB file from Settings
- **Theme**: Apply SYSTEM/LIGHT/DARK from DataStore to the app (e.g. in root Composable or Activity)

### Architecture (spec §11)
- **Domain layer**: No separate domain models or use cases yet. Spec lists domain `ActivityEntry`, `Settings`, `LogActivityUseCase`, `GetSuggestionsUseCase`, `AutoFillUntrackedUseCase`, `GetCoverageUseCase`. Optional: add these and have ViewModels/service call use cases.
- **Hilt**: Spec says "Hilt"; project uses manual DI (e.g. `AppDatabase.getInstance`, repository construction in MainActivity). Either add Hilt or document manual DI.
- **Navigation**: No bottom nav or tabs; only Timeline. Need navigation to Timeline, Stats, Settings (e.g. NavHost + bottom bar or drawer).

### Other
- **Midnight reset**: Spec: "Midnight rollover → reset to baseInterval". No WorkManager (or similar) to reset `currentInterval` at midnight.
- **Sound/vibration on popup**: Spec: low sound and minimal vibration, configurable. Not implemented in PopupActivity or notification.

---

## Summary table

| Area                         | Status   | Notes                                              |
|-----------------------------|----------|----------------------------------------------------|
| Room + DAO                  | Done     | Suggestions query correct; add stats queries later |
| DataStore                   | Partial  | Missing theme; only 2 update methods              |
| Repositories                | Done     | Activity + Settings present                        |
| AlarmScheduler              | Partial  | Exact alarm ok; no quiet hours, no settings       |
| LoggingForegroundService    | Partial  | Notification + full-screen ok; no broadcast handling |
| PopupActivity/Screen        | Partial  | Lock screen + normalize ok; no X min, chips, Enter, auto-dismiss |
| Timeline                    | Partial  | Live list ok; no coverage %, duration, grouping, edit/delete, muted |
| IntervalEscalationEngine    | Done     | Matches spec                                       |
| Permissions                 | Partial  | Missing VIBRATE, WAKE_LOCK, USE_EXACT_ALARM, battery |
| Stats screen                | Pending  | No Vico, no DAOs, no UI                            |
| Settings screen             | Pending  | No UI                                              |
| Export/backup               | Pending  | No JSON export or DB backup/restore                |
| Theme / dark mode           | Pending  | No theme in DataStore or UI                        |
| Hilt / DI                   | Pending  | Manual DI only                                     |
| Navigation                  | Pending  | Single screen (Timeline)                           |

---

## Recommended order to fix

1. **Service handling ACTION_LOGGED/ACTION_MISSED** (Untracked insert, interval update, schedule next, stopSelf)
2. **AlarmScheduler + settings** (intervals + quiet hours)
3. **Popup** (X minutes, chips, Enter, auto-dismiss)
4. **Permissions**
5. Timeline polish (coverage %, duration, grouping, edit/delete, muted)
6. Stats, Settings, Export, Theme, Navigation, Hilt (as needed)
