# 🐾 Desk Pet - Cozy Productivity Companion & Virtual Pet

<div align="center">

![Desk Pet Banner](https://img.shields.io/badge/Desk%20Pet-Mobile%20App-ff7597?style=for-the-badge&logo=android&logoColor=white)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20(M3)-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Rive Runtime](https://img.shields.io/badge/Animations-Rive%20Runtime-black?style=for-the-badge&logo=rive&logoColor=white)](https://rive.app)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue?style=for-the-badge)](LICENSE)

<br/>

**Desk Pet** is a cozy, gamified Android productivity companion and virtual pet sanctuary built with modern **Jetpack Compose** and **Rive 2D Vector Animations**. Nurture adorable desk companions, power through Pomodoro focus sessions with procedural ambient soundscapes, track your daily moods, complete quests, and decorate your pet's sanctuary.

</div>

---

## ✨ Key Features

### 🐾 1. Interactive Animated Companions
- **Fluid Rive Animations**: Interactive pets with state-machine-driven animations for idle, breathing, feeding, petting, playing, and sleeping.
- **Multiple Companions**:
  - 🍮 **Mochi**: The soft, squishy jelly blob who loves naps and sweet snacks.
  - 🐕 **Shiba**: The loyal, energetic companion ready to cheer you through study marathons.
  - 🐱 **Calico Cat**: A cozy, purring kitten that loves sunbeams and warm hearths.
  - ⭐ **Golden Mascot**: A cheerful star buddy bringing luck to your productivity.
  - 🐷 **Lucky Piggy**: A playful piggy helping you save focus coins.
- **Pet Care Dynamics**: Feed, pat, groom, and play to level up your pet's happiness, hunger, and energy stats.

---

### ⏱️ 2. Focus Timer & Pomodoro Engine
- **Customizable Cycles**: Set Work, Short Break, and Long Break durations, or run open-ended focus sessions.
- **Productivity Tracking**: Real-time Focus Score calculation, streak counters, and daily minutes logged.
- **Task Association**: Tag specific study topics or work deliverables to each session.
- **Gamified Rewards**: Earn EXP and Coins for every completed focus block to spend in the Cozy Shop.

---

### 🎧 3. Procedural Soothing Soundscape Engine
An in-house, low-latency audio synthesis engine running directly on Android's `AudioTrack` with zero external audio assets required:
- 🌧️ **Gentle Rain**: Soft pitter-patter against a cozy window with subtle distance rumbles.
- ☕ **Lo-Fi Study Cafe**: Warm vinyl crackle and soothing cafe hum.
- 🔥 **Cozy Fireplace**: Ambient hearth snaps, pops, and warm low-end rumble.
- 🍃 **Forest Breeze**: Airy pine tree rustles and organic outdoor atmosphere.
- 🌊 **Starlight Ocean**: Slow, rhythmic tidal wash for anxiety relief and meditation.
- 🌙 **Midnight Chimes**: Resonant, tuned chimes for deep concentration and sleep.

---

### 🏡 4. Sanctuary & Room Customization
- Personalize your pet's environment with unlockable cozy decor:
  - **Wallpapers**: Pastel Sky, Night Sky, Warm Wood, Cozy Grid, and Cafe Brick.
  - **Flooring**: Tatami Mats, Hardwood, Soft Plush Rugs, and Marble.
  - **Furniture & Plants**: Bookshelves, Pothos plants, study desks, fairy lights, and beanbags.

---

### 📋 5. Daily Missions & Quests
- **Daily Quests**: Quick, actionable goals (e.g., complete 2 focus sessions, feed your pet 3 times, log your daily mood).
- **Weekly Challenges**: Long-term milestone quests rewarding rare room decor and massive coin bundles.
- **Level Progression**: Level up your user profile and unlock higher pet tier perks.

---

### 💭 6. Daily Mood Check-In & Wellness
- Track daily emotional states with cute emoji indicators and reflections.
- Visual mood history calendar and emotional wellness streak logging.
- Reflective journaling prompts to decompress before or after deep work.

---

### 🛍️ 7. Cozy Shop
- Exchange in-game coins earned from focus sessions and quests for:
  - 🍓 **Snacks**: Fresh strawberries, onigiri, boba milk tea, warm soup, and pet treats.
  - 🎾 **Toys**: Yarn balls, laser pointers, squeaky toys, and plushies.
  - 🎀 **Accessories**: Bowties, tiny party hats, cozy scarves, and glasses.

---

## 🛠️ Tech Stack & Architecture

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 theming and edge-to-edge system UI.
- **Animation Engine**: [Rive Kotlin Runtime](https://github.com/rive-app/rive-android) (`app.rive:rive-android:10.2.0`) for interactive vector character states.
- **Audio Engine**: Procedural PCM 16-bit audio synthesis running on coroutines and `android.media.AudioTrack`.
- **Database & Persistence**: [Jetpack Room](https://developer.android.com/training/data-storage/room) with SQLite, Kotlin Coroutines, and `Flow` for reactive offline-first data.
- **Authentication**: Android `androidx.credentials` with Google ID and local Guest session support.
- **Networking & Serialization**: Retrofit, OkHttp, Moshi, and Firebase AI / Google Services integration ready.
- **Language & Tooling**: Kotlin 2.0+, KSP (Kotlin Symbol Processing), Gradle Kotlin DSL (`build.gradle.kts`).
- **Architecture**: Clean MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF) via Kotlin `StateFlow`.

---

## 📁 Project Structure

```
Desk_Pet/
├── app/
│   ├── build.gradle.kts                # App-level build configuration & dependencies
│   ├── proguard-rules.pro              # Proguard / R8 optimization rules
│   └── src/main/
│       ├── AndroidManifest.xml         # Android manifest & hardware permissions
│       ├── java/com/example/
│       │   ├── MainActivity.kt         # Single-activity Compose entry & tab routing
│       │   ├── data/
│       │   │   ├── auth/               # User authentication state & session models
│       │   │   ├── db/                 # Room database, entity models, and DAOs
│       │   │   ├── model/              # Domain models (Pet, Task, Mission, Item)
│       │   │   └── repository/         # Data repositories & offline sync logic
│       │   └── ui/
│       │       ├── DeskPetViewModel.kt # Main state management & business logic
│       │       ├── auth/               # AuthViewModel & credential management
│       │       ├── components/         # Reusable Compose components (NavBar, TopBar, Sheets)
│       │       ├── screens/
│       │       │   ├── RoomScreen.kt           # Interactive sanctuary room & pet play
│       │       │   ├── FocusTimerScreen.kt     # Pomodoro timer & ambient sound controls
│       │       │   ├── DailyMissionsScreen.kt  # Missions, quests, and rewards
│       │       │   ├── PetSanctuaryScreen.kt   # Pet switcher & roster manager
│       │       │   ├── CozyShopScreen.kt       # In-game coin shop & item purchases
│       │       │   └── LoginScreen.kt          # Google Sign-In & guest onboarding
│       │       ├── sound/              # Procedural AudioTrack soundscape synthesizer
│       │       └── theme/              # Color palette, Typography, and Shapes
│       └── res/
│           ├── raw/                    # Rive animations (.riv: cat, dog, mascot, mochi, piggy)
│           ├── values/                 # Colors, strings, themes
│           └── mipmap-*/               # Adaptive launcher icons
├── character_assets/                   # Source Rive assets & vector SVGs
├── gradle/                             # Gradle wrapper & version catalogs
├── build.gradle.kts                    # Root project build configuration
├── settings.gradle.kts                 # Subproject inclusion & dependency resolution
├── .env.example                        # Template for API keys & secrets
└── README.md                           # Project documentation
```

---

## 🚀 Getting Started

### Prerequisites
1. **Android Studio**: Android Studio Ladybug (2024.2.1) or newer.
2. **JDK**: Java 17 or Java 21 configured in Android Studio Gradle settings.
3. **Android Device / Emulator**: Android 7.0 (API Level 24) or higher (Target SDK: 36).

### Installation & Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Bilawal13/Desk-Pet---Mobile-App.git
   cd Desk-Pet---Mobile-App
   ```

2. **Set up Environment Variables:**
   Copy the example environment file:
   ```bash
   cp .env.example .env
   ```
   *(Optional)* Fill in your own keys in `.env` if using Gemini AI, Supabase, or Google Sign-In:
   ```env
   GEMINI_API_KEY=your_gemini_api_key
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_ANON_KEY=your_supabase_anon_key
   GOOGLE_WEB_CLIENT_ID=your_client_id.apps.googleusercontent.com
   ```

3. **Open in Android Studio:**
   - Launch Android Studio.
   - Click **Open** and select the root directory of this repository.
   - Allow Gradle to sync dependencies.

4. **Build and Run:**
   - Select your target emulator or physical device.
   - Click **Run (`Shift + F10`)** or build from command line:
     ```bash
     ./gradlew assembleDebug
     ```

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!
Feel free to check the [issues page](https://github.com/Bilawal13/Desk-Pet---Mobile-App/issues) if you would like to report a bug or propose a new feature.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

Distributed under the Apache 2.0 License. See `LICENSE` for more information.

<div align="center">
Made with 💖 for cozy focus and mindful productivity.
</div>
