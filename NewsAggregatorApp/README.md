# Ethiopian News Aggregator App

A clean, modern Android app that aggregates Ethiopian news from multiple RSS feeds with offline caching, Material UI design, and comprehensive features.

## Features

### Core Features
- **Combined News Feed**: Aggregates articles from multiple Ethiopian news sources
- **Offline Caching**: Stores latest 20-30 articles per source for offline reading
- **Material Design UI**: Clean, modern interface following Material Design principles
- **Pull-to-Refresh**: Easy refresh functionality with swipe gesture
- **WebView Integration**: Internal browser for reading full articles
- **Search Functionality**: Find articles by keyword across title, description, and source
- **Theme Toggle**: Switch between light and dark modes
- **Source Filtering**: Enable/disable specific news sources (planned feature)

### News Sources
The app aggregates news from the following Ethiopian sources:

1. **VOA Amharic** - https://amharic.voanews.com/api/zy--yl-vomx-tpetyqqv
2. **Addis Standard** - https://addisstandard.com/feed/
3. **Aiga News** - https://aiganews.com/feed
4. **Ethiopia Insight** - https://ethiopia-insight.com/feed
5. **New Business Ethiopia** - https://newbusinessethiopia.com/feed
6. **Maleda Times** - https://maledatimes.com/feed
7. **Ethiopia Nege** - https://ethiopianege.com/feed
8. **Debteraw** - https://debteraw.com/feed

## Technical Architecture

### Technologies Used
- **Language**: Kotlin
- **UI Framework**: Android Views with Material Components
- **Database**: Room (SQLite)
- **RSS Parsing**: RSS-Parser library by prof18
- **Architecture**: Repository pattern with coroutines
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)

### Project Structure
```
app/
├── src/main/java/com/example/newsaggregator/
│   ├── MainActivity.kt                 # Main activity with news list
│   ├── ArticleActivity.kt             # WebView activity for reading articles
│   ├── adapter/
│   │   └── NewsAdapter.kt             # RecyclerView adapter for news list
│   ├── data/
│   │   ├── NewsArticle.kt             # Data model for news articles
│   │   ├── NewsRepository.kt          # Repository for data operations
│   │   ├── RssFeedParser.kt          # RSS feed parsing logic
│   │   └── database/
│   │       ├── AppDatabase.kt         # Room database configuration
│   │       └── NewsArticleDao.kt      # Data access object
│   └── res/
│       ├── layout/                    # XML layout files
│       ├── values/                    # Colors, strings, themes
│       └── menu/                      # Menu resources
```

### Key Components

#### 1. Data Layer
- **NewsArticle**: Entity class representing a news article
- **NewsRepository**: Handles data operations and caching
- **RssFeedParser**: Parses RSS feeds using RSS-Parser library
- **AppDatabase**: Room database for offline storage
- **NewsArticleDao**: Database access operations

#### 2. UI Layer
- **MainActivity**: Main screen displaying news list with search and refresh
- **ArticleActivity**: WebView for reading full articles
- **NewsAdapter**: RecyclerView adapter with article cards

#### 3. Features Implementation
- **Offline Caching**: Articles stored in Room database
- **Pull-to-Refresh**: SwipeRefreshLayout implementation
- **Search**: Real-time filtering of articles
- **Theme Toggle**: SharedPreferences with AppCompatDelegate
- **WebView**: Custom WebViewClient for internal browsing

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17 or later
- Android SDK with API 34
- Minimum device: Android 7.0 (API 24)

### Building the App

1. **Clone or download the project files**
2. **Open in Android Studio**:
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the NewsAggregatorApp folder

3. **Sync Gradle files**:
   - Android Studio will automatically sync Gradle files
   - Wait for the sync to complete

4. **Build the APK**:
   ```bash
   ./gradlew assembleDebug
   ```
   Or use Android Studio: Build → Build Bundle(s) / APK(s) → Build APK(s)

5. **Install on device**:
   - Connect Android device via USB with debugging enabled
   - Run the app from Android Studio, or
   - Install APK manually: `adb install app-debug.apk`

### Command Line Build (Alternative)
```bash
# Set Android SDK path
export ANDROID_HOME=/path/to/android/sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools

# Build debug APK
./gradlew assembleDebug

# APK will be generated at: app/build/outputs/apk/debug/app-debug.apk
```

## App Usage

### First Launch
1. Open the app
2. The app will automatically fetch latest articles from all sources
3. Articles are displayed in chronological order (newest first)

### Navigation
- **Tap article**: Opens full article in internal WebView
- **Pull down**: Refresh articles from RSS feeds
- **Search icon**: Search articles by keyword
- **Menu (⋮)**: Access theme toggle and other options
- **Back button in WebView**: Navigate back or return to main screen

### Features Usage
- **Search**: Tap search icon, type keywords to filter articles
- **Theme Toggle**: Menu → Toggle Theme (switches between light/dark mode)
- **Refresh**: Pull down on article list or use refresh button
- **Offline Reading**: Previously loaded articles available without internet

## Permissions

The app requires minimal permissions:
- **INTERNET**: To fetch RSS feeds
- **ACCESS_NETWORK_STATE**: To check network connectivity

## Performance Optimizations

- **Efficient Caching**: Only stores latest articles per source
- **Background Processing**: RSS parsing on background threads
- **Memory Management**: RecyclerView with ViewHolder pattern
- **Network Optimization**: Conditional refresh based on connectivity

## Future Enhancements

- Source filtering dialog implementation
- Push notifications for breaking news
- Article bookmarking
- Social sharing functionality
- Amharic language support for UI
- Image caching for article thumbnails
- Export/import settings

## Troubleshooting

### Common Issues
1. **Build Errors**: Ensure Android SDK and build tools are properly installed
2. **Network Issues**: Check internet connectivity for RSS feeds
3. **Empty List**: Some RSS feeds may be temporarily unavailable
4. **WebView Issues**: Ensure JavaScript is enabled in WebView settings

### Debug Information
- Check logcat for RSS parsing errors
- Verify RSS feed URLs are accessible
- Ensure minimum SDK requirements are met

## License

This project is open source and available under the MIT License.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## Contact

For questions or support, please open an issue in the project repository.

---

**Note**: This app is designed for educational and informational purposes. News content is sourced from respective RSS feeds and remains the property of the original publishers.

