# ULIM

An Android app for signing in, completing a student form, and saving the submitted information locally.

## Features

- Login screen with a demo account
- Student form for name, date of birth, photo, education, gender, and specialty
- Input validation and a display screen for completed information
- Local persistence with Room
- Romanian, Russian, and English interface options
- Branded adaptive launcher icon

## Tech stack

- Kotlin and Jetpack Compose
- Material 3 and Navigation Compose
- Room database with KSP
- Coil for image loading

## Run the project

1. Clone the repository:

   ```bash
   git clone https://github.com/vtoool/AplicatieULIM.git
   ```

2. Open the cloned folder in Android Studio.
3. Let Gradle sync, select an emulator or connected Android device, and press **Run**.

The project has a minimum SDK of 24 (Android 7.0).

### Terminal build

With a JDK configured for Gradle, run:

```bash
./gradlew assembleDebug
```

The generated debug APK is placed in `app/build/outputs/apk/debug/`.

## Demo login

| Field | Value |
| --- | --- |
| Username | `admin` |
| Password | `admin123` |

> The demo credentials are only for local demonstration and are not suitable for a production login system.
