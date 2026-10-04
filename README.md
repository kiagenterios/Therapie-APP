# TherapieApp

Native Android-App — lokale Selbsthilfe-Tools im Bereich CBT, DBT, Achtsamkeit und Mood-Tracking.

> Komplett lokal. Kein Backend, kein Login, keine Cloud. Single-User-App.

## Features (MVP)

| Feature | Beschreibung |
|---|---|
| **Mood-Tracking** | Täglicher Eintrag (1–5 Skala mit Emoji + Notiz). Verlaufsdiagramm der letzten 30 Tage (Vico Chart). |
| **Journal** | Freie Texteinträge mit Tags. Volltext-Suche. CRUD inkl. Details- und Edit-Screen. |
| **CBT / Achtsamkeit / DBT** | 12 vordefinierte Übungen (Box-Breathing, 4-7-8, Body-Scan, 5-4-3-2-1, ACCEPTS, IMPROVE, RADIATE, Radikale Akzeptanz, etc.). Animierte Atem-Sessions mit Timer. |
| **Reminders** | Planbare Erinnerungen pro Feature und Wochentag. WorkManager-basiert, Notifications via NotificationChannel. |

## Tech-Stack

- **Sprache**: Kotlin 2.0
- **UI**: Jetpack Compose (Material 3, Dynamic Color ab Android 12, Edge-to-Edge)
- **Architektur**: MVVM + Repository, Single-Activity
- **DI**: Hilt 2.51
- **DB**: Room 2.6 (KSP statt KAPT)
- **Settings**: DataStore Preferences
- **Charts**: Vico 2.0
- **Reminder-Scheduling**: WorkManager 2.9 (PeriodicWorkRequest)
- **Navigation**: Navigation Compose 2.8 (Type-Safe Routes)
- **Build**: Gradle 8.x mit KTS + Version Catalog

## Konfiguration

| | |
|---|---|
| `minSdk` | 26 (Android 8.0) |
| `targetSdk` | 34 |
| `compileSdk` | 34 |
| Application ID | `com.psych.therapieapp` |
| Package | `com.psych.therapieapp` |

## Projekt-Struktur

```
app/src/main/kotlin/com/psych/therapieapp/
├── TherapieApplication.kt          # @HiltAndroidApp + WorkManager-Provider
├── MainActivity.kt                 # @AndroidEntryPoint + Edge-to-Edge + Splashscreen
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt          # Room-Database (Version 1, alle Entities)
│   │   ├── Converters.kt           # TypeConverters für Date/Time/Enums
│   │   ├── entity/                 # MoodEntry, JournalEntry, JournalTag, Exercise, Reminder
│   │   ├── dao/                    # MoodDao, JournalDao, JournalTagDao, ExerciseDao, ReminderDao
│   │   └── relations/              # JournalEntryWithTags (M:N)
│   ├── repository/                 # je Feature ein Repository (@Singleton)
│   └── seed/                       # ExerciseSeeder – 12 vorgefüllte Übungen
├── di/
│   ├── AppModule.kt                # DataStore
│   └── DatabaseModule.kt           # Room + DAOs
├── ui/
│   ├── MainScreen.kt               # Scaffold + BottomNav
│   ├── theme/                      # Color, Theme, Type (Material 3 mit Dynamic Color)
│   ├── navigation/                 # Type-Safe NavGraph
│   ├── mood/, journal/, exercises/, reminders/
├── worker/                         # ReminderWorker (HiltWorker), ReminderScheduler
└── service/                        # NotificationHelper, PermissionHelper
```

## Aufsetzen

```bash
git clone https://github.com/kiagenterios/TherapieApp.git
cd TherapieApp

# Mit Android Studio öffnen → Gradle Sync → Run
# Oder via CLI:
./gradlew assembleDebug
./gradlew installDebug
```

## Berechtigungen

- `POST_NOTIFICATIONS` (Runtime-Request ab Android 13)
- `RECEIVE_BOOT_COMPLETED` (WorkManager Re-Schedule nach Reboot)
- `WAKE_LOCK` (für WorkManager-Worker)

## Hinweise zur Implementierung

- **Reminders**: `PeriodicWorkRequest` mit 24h-Intervall + `setInitialDelay` bis zur Zielzeit. Im Worker wird der Wochentag geprüft (Reminder.daysOfWeek), Notifications nur an aktiven Tagen.
- **Aquastop für Notifications**: `PendingIntent.FLAG_IMMUTABLE` ist Pflicht. Permission-Request via `ActivityResultContracts.RequestPermission`.
- **Predictive Back**: durch `enableEdgeToEdge()` + Compose Navigation automatisch unterstützt.
- **Dynamic Color**: nur ab Android 12 (Material You). Davor: handgepflegte Theme-Palette.

## Build-Status

Initial-Scaffold via Claude-Code Multi-Agent-Pipeline:
- Scaffold (Gradle, Manifest, Theme, Navigation, MainActivity) → Hauptscope
- Feature *Mood* → Sub-Agent A
- Feature *Journal* → Sub-Agent B
- Feature *Exercises / CBT / DBT* → Sub-Agent C
- Feature *Reminders + WorkManager* → Sub-Agent D
