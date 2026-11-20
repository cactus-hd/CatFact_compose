<div align="center">

# 🐱 CatFact 🐾
### *Pixel Edition — Random Cat Facts in Retro Style!*

<img src="app/src/main/res/drawable/cargif.gif" width="220" alt="Pixel Animated Cat"/>

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF.svg?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-4285F4.svg?style=for-the-badge&logo=android)](https://developer.android.com/jetpack/compose)
[![API](https://img.shields.io/badge/Min%20SDK-33%2B-green.svg?style=for-the-badge&logo=android)](https://android.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)

</div>

---

## 🎮 About The Project

**CatFact** is a retro pixel-styled Android application built with **Jetpack Compose** and modern Android standards. It fetches random, fascinating cat trivia from the Cat Facts API and presents them with nostalgic pixel typography, vibrant colors, and animated GIFs! 🐾

<div align="center">
  <img src="app/src/main/res/drawable/catbox.png" width="120" alt="Catbox Icon"/>
</div>

---

## ✨ Features

- 👾 **Retro Pixel Aesthetic**: Customized with custom pixel typography (`pixel.ttf`) and retro color schemes.
- 🎬 **Animated GIF Support**: Powered by **Coil 2.x** with native `ImageDecoderDecoder` to display smooth retro cat animations.
- ⚡ **Animated Content Transitions**: Uses Compose `AnimatedContent` (`fadeIn` + `slideInVertically`) for smooth text transitions when fetching a new fact.
- 📋 **Long-Press to Copy**: Long-press on the cat fact card to instantly copy the fact to your clipboard.
- 📤 **One-Tap Share**: Share cat facts with your friends via Android's native share sheet with one tap.
- 🛡️ **Graceful Loading & Error Handling**: Displays a loader during API requests and a retro Error Card with a **Retry 🔄** button if offline.
- ⭐ **Interactive GitHub Speech Bubble**: Animated speech bubble popping up to invite users to star the repo on GitHub!

---

## 🛠️ Tech Stack & Libraries

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Networking**: [Retrofit 2](https://square.github.io/retrofit/) + Gson Converter
- **Image Loading & GIF Decoding**: [Coil](https://coil-kt.github.io/coil/) (`coil-compose`, `coil-gif`)
- **Concurrency**: Kotlin Coroutines (`Dispatchers.IO`, `Dispatchers.Main`)
- **Architecture**: Modern Android ComponentActivity + Lifecycle Scope

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1+) or newer
- JDK 17 / Kotlin 2.2+
- Android Device or Emulator running Android 13 (API 33) or higher

### Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/cactus-hd/CatFact_compose.git
   ```
2. **Open the project** in Android Studio.
3. **Sync Gradle** dependencies.
4. **Build and Run** the `:app` configuration on your device or emulator! 📱

---


## 🤝 Contributing

Contributions, issues, and feature requests are welcome!  
Feel free to check out the [issues page](https://github.com/cactus-hd/CatFact_compose/issues).

---

## ⭐ Show Your Support

If you enjoyed this project, please give it a **⭐ Star** on GitHub!

<div align="center">
 
</div>
