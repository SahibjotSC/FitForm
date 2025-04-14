# FitForm

**FitForm** is an Android application designed to help users track their fitness progress with real-time posture feedback.  
The app combines modern Android development practices with powerful vision-based analysis to assist users during workouts.

## Features

- 🎯 **Real-Time Feedback**: Instant posture analysis with MediaPipe.
- 📊 **Circular Progress Bar**: Visualize workout progress dynamically.
- 🔗 **Seamless Navigation**: Smooth transitions using Android Navigation components.
- 📷 **CameraX Integration**: Reliable camera functionalities.
- 🖼️ **View Binding**: Simplified UI handling and improved code safety.
- 📲 **Multi-Device Compatibility**: Designed to support a wide range of Android devices.

## Requirements

- **Android Studio**: Ladybug | 2024.2.1 Patch 2
- **Minimum SDK**: 25
- **Target SDK**: 34
- **Compile SDK**: 35
- **Java**: Version 17

## Setup Instructions

1. **Clone the repository**
    ```sh
    git clone https://github.com/Manjindergit/FitForm.git
    ```

2. **Open the project** in Android Studio.

3. **Sync Gradle** to download dependencies.

4. **Build and run** on an Android device or emulator (camera permissions required).

## Dependencies

This project relies on the following libraries:

- **UI & Layout**
  - Circular Progress Bar: `com.mikhaellopez:circularprogressbar:3.1.0`
  - Material Components: `com.google.android.material:material`
  - ConstraintLayout: `androidx.constraintlayout:constraintlayout`
  - WindowManager: `androidx.window:window`

- **Core & Utilities**
  - Kotlin Standard Library: `org.jetbrains.kotlin:kotlin-stdlib`
  - AndroidX Core: `androidx.core:core-ktx`
  - AppCompat: `androidx.appcompat:appcompat`
  - Fragment KTX: `androidx.fragment:fragment-ktx`

- **Navigation**
  - Navigation Fragment: `androidx.navigation:navigation-fragment-ktx`
  - Navigation UI: `androidx.navigation:navigation-ui-ktx`

- **Camera & Vision**
  - CameraX Core: `androidx.camera:camera-core`
  - CameraX Camera2: `androidx.camera:camera-camera2`
  - CameraX Lifecycle: `androidx.camera:camera-lifecycle`
  - CameraX View: `androidx.camera:camera-view`
  - MediaPipe Tasks Vision: `com.google.mediapipe:tasks-vision`

- **Testing**
  - JUnit: `junit:junit`
  - Espresso: `androidx.test.espresso:espresso-core`

## License

This project is licensed under the MIT License.  
See the [LICENSE](LICENSE) file for full details.

---

🚀 *FitForm is actively maintained and open to contributions!*
