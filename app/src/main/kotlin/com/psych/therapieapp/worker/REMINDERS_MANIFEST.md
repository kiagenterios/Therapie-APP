# Reminders / Notifications - AndroidManifest-Hinweise

## Permissions

In `app/src/main/AndroidManifest.xml` (innerhalb von `<manifest>`, vor `<application>`) eintragen:

```xml
<!-- Pflicht ab API 33 (Android 13): Laufzeit-Permission, wird via PermissionHelper angefragt. -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Optional: WorkManager reicht ueblicherweise. Falls spaeter ein AlarmManager-basierter
     Exact-Alarm genutzt werden soll (z.B. fuer minutengenaue Erinnerungen bei Doze-Mode),
     entweder USE_EXACT_ALARM (Android 13+) oder SCHEDULE_EXACT_ALARM hinzufuegen.
     Aktuell NICHT noetig, da PeriodicWorkRequest (24h Intervall, initialDelay) verwendet wird. -->
<!-- <uses-permission android:name="android.permission.USE_EXACT_ALARM" /> -->
<!-- <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" /> -->

<!-- Empfohlen, damit Reminders nach Reboot via WorkManager-Persistenz weiterlaufen.
     WorkManager triggert sich nach Boot selbst neu, sobald das System bereit ist. -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

## Hilt + WorkManager

Stellt sicher, dass in `app/build.gradle.kts` folgende Abhaengigkeiten vorhanden sind:

```kotlin
implementation("androidx.work:work-runtime-ktx:2.9.0")
implementation("androidx.hilt:hilt-work:1.2.0")
kapt("androidx.hilt:hilt-compiler:1.2.0")
```

In der Application-Klasse (`@HiltAndroidApp`) `Configuration.Provider` implementieren:

```kotlin
@HiltAndroidApp
class TherapieApp : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
```

und im Manifest den Default-Initializer von WorkManager entfernen (siehe Hilt-Doku):

```xml
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data
        android:name="androidx.work.WorkManagerInitializer"
        android:value="androidx.startup"
        tools:node="remove" />
</provider>
```

## Laufzeit-Berechtigung

Die UI ruft `rememberNotificationPermissionState()` aus
`com.psych.therapieapp.service.PermissionHelper` und triggert beim Speichern eines
Reminders ggf. den Permission-Request. Auf API < 33 ist die Berechtigung implizit erteilt.
