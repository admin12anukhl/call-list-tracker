# Android MVP: Call List Tracker (branch: mvp/android-call-tracker)

This branch contains a minimal Android (Kotlin) MVP that listens to call events and records the device location at call time.

Key features in this MVP:
- Foreground Service that registers a PhoneStateListener to detect incoming calls.
- Outgoing call BroadcastReceiver to capture outgoing numbers.
- Uses FusedLocationProvider to capture last known location (falls back to current location request).
- Saves call records (number, type, timestamp, latitude, longitude) in Room database.
- Simple RecyclerView UI to display saved call records.

Notes and limitations:
- Targeting Android 11 (compileSdk/targetSdk 30). Minimum SDK is 23.
- READ_CALL_LOG and outgoing call intents are sensitive on newer Android versions; Play Store policies may require additional justification or making the app the default dialer.
- This MVP focuses on local recording and viewing. No export/upload implemented yet.

Build & run:
- Import this project into Android Studio (use Gradle wrapper if available).
- Grant runtime permissions when prompted: ACCESS_FINE_LOCATION and READ_PHONE_STATE.
- The app starts a foreground service to listen for calls and will record location when a call is detected.

If you want, I can now:
- Add exports (CSV), reverse geocoding for readable addresses, and a proper onboarding/permission flow.
- Improve reliability for outgoing calls on Android 10+ (by making the app the default dialer) and prepare Play Store privacy policy.

