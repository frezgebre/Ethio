# Build Instructions for Ethiopian News Aggregator App

## Quick Start

### Option 1: Using Android Studio (Recommended)
1. Download and install Android Studio from https://developer.android.com/studio
2. Open Android Studio
3. Select "Open an existing project"
4. Navigate to and select the `NewsAggregatorApp` folder
5. Wait for Gradle sync to complete
6. Click the "Run" button (green play icon) or press Shift+F10
7. Select your device/emulator and the app will be installed and launched

### Option 2: Command Line Build
```bash
# Navigate to project directory
cd NewsAggregatorApp

# Make gradlew executable (Linux/Mac)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# The APK will be generated at:
# app/build/outputs/apk/debug/app-debug.apk
```

## Detailed Setup

### Prerequisites
- **Android Studio**: Latest stable version
- **JDK**: Version 17 or later
- **Android SDK**: API level 34 (Android 14)
- **Minimum device**: Android 7.0 (API 24) or later

### Environment Setup
1. **Install Android Studio**:
   - Download from https://developer.android.com/studio
   - Follow installation wizard
   - Install Android SDK and build tools

2. **Configure SDK**:
   - Open Android Studio
   - Go to File → Settings → Appearance & Behavior → System Settings → Android SDK
   - Ensure API 34 is installed
   - Note the SDK path for command line builds

3. **Enable Developer Options** (for device testing):
   - On Android device: Settings → About Phone → Tap "Build Number" 7 times
   - Go back to Settings → Developer Options → Enable "USB Debugging"

### Building APK

#### Method 1: Android Studio GUI
1. Open project in Android Studio
2. Wait for Gradle sync
3. Build → Build Bundle(s) / APK(s) → Build APK(s)
4. APK location will be shown in notification

#### Method 2: Command Line
```bash
# Set environment variables (Linux/Mac)
export ANDROID_HOME=/path/to/android/sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin

# Windows
set ANDROID_HOME=C:\path\to\android\sdk
set PATH=%PATH%;%ANDROID_HOME%\cmdline-tools\latest\bin

# Build
./gradlew assembleDebug
```

### Installation

#### Install on Connected Device
```bash
# Using ADB
adb install app/build/outputs/apk/debug/app-debug.apk

# Or drag and drop APK to device
```

#### Install on Emulator
1. Start Android emulator from Android Studio
2. Drag APK file to emulator window
3. Or use ADB install command

## Troubleshooting

### Common Build Issues

#### 1. Gradle Sync Failed
```bash
# Clean and rebuild
./gradlew clean
./gradlew build
```

#### 2. SDK Not Found
- Verify ANDROID_HOME environment variable
- Check SDK path in Android Studio settings
- Install missing SDK components

#### 3. Build Tools Version
- Update build tools in SDK Manager
- Sync Gradle files after update

#### 4. Dependency Issues
```bash
# Clear Gradle cache
./gradlew --refresh-dependencies
```

### Runtime Issues

#### 1. App Crashes on Launch
- Check device meets minimum API 24 requirement
- Verify internet permission in manifest
- Check logcat for specific error messages

#### 2. No Articles Loading
- Verify internet connectivity
- Check RSS feed URLs are accessible
- Some feeds may be temporarily unavailable

#### 3. WebView Issues
- Ensure device has WebView component installed
- Check if JavaScript is enabled

## Testing

### Unit Testing
```bash
./gradlew test
```

### Instrumented Testing
```bash
./gradlew connectedAndroidTest
```

### Manual Testing Checklist
- [ ] App launches successfully
- [ ] Articles load from RSS feeds
- [ ] Pull-to-refresh works
- [ ] Search functionality works
- [ ] Article opens in WebView
- [ ] Theme toggle works
- [ ] Offline caching works
- [ ] App works in both orientations

## Release Build

For production release:

```bash
# Generate signed APK
./gradlew assembleRelease
```

Note: Requires keystore configuration in `app/build.gradle`

## Performance Tips

- Use release build for better performance
- Test on various device configurations
- Monitor memory usage with Android Profiler
- Test with slow network conditions

## Support

If you encounter issues:
1. Check this document first
2. Search existing issues in project repository
3. Create new issue with detailed error information
4. Include device information and Android version

