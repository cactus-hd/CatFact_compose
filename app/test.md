```markdown
<div align="center">

<img src="app/src/main/res/drawable/cargif.gif" alt="Pixel cat" width="160"/>

# 🐱 CatFact

**A tiny pixel-art Android app that serves you random cat facts — one tap at a time.**

[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-EEA2B8)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-6650a4)](https://github.com/cactus-hd/CatFact_compose/pulls)

</div>

---

```
/\_/\     "Did you know?"
( o.o )    Cats spend 70% of their lives sleeping.
> ^ <     ...which honestly sounds great.
```

---

## ✨ What it does

A cat-themed Android app built with **Jetpack Compose** that fetches a random cat fact
from a public API and shows it in a cozy pixel-art card. Tap refresh, get a new fact.
Simple, cute, done.

<div align="center">
<img src="app/src/main/res/drawable/catbox.png" alt="Pixel cat box" width="80"/>
</div>

## 🎮 Features

- 🐾 **Random cat facts** fetched from a REST API via Retrofit
- 🎨 **Pixel-art UI** — custom pixel font, purple palette, animated cat GIF
- ✨ **Animated fact text** — smooth fade + slide whenever the fact changes
- ⏳ **Loading state** — the refresh button shows a spinner and disables itself while fetching
- 🚨 **Error state** — friendly in-card message with a **Retry** button (no more silent Toasts)
- 📋 **Long-press to copy** the fact to your clipboard (with a Snackbar confirmation)
- 📤 **Share button** — send the fact to your friends
- 💜 **Speech bubble** — an occasional gentle reminder to star the repo

## 🛠️ Tech stack

| Layer        | What's used                                      |
|--------------|--------------------------------------------------|
| Language     | Kotlin                                           |
| UI           | Jetpack Compose + Material 3                     |
| Networking   | Retrofit 2 + Gson                                |
| Images       | Coil (with animated GIF support)                 |
| Async        | Kotlin Coroutines (`lifecycleScope`)             |
| Architecture | Single-Activity, Compose-first                   |

## 🚀 Getting started

```bash
# clone it
git clone https://github.com/cactus-hd/CatFact_compose.git
cd CatFact_compose

# open in Android Studio (Ladybug or newer) and hit Run ▶️
```

Requirements: **Android Studio Ladybug+**, **minSdk 24+**, **JDK 17**.

## 📁 Project structure

```
app/
├── src/main/
│   ├── java/com/amir/catfact/
│   │   ├── MainActivity.kt       # Compose UI + state
│   │   ├── model/CatFacts.kt     # data model
│   │   └── utils/RetrofitInstance.kt
│   └── res/
│       ├── drawable/             # cargif.gif, catbox.png
│       └── font/pixel.ttf
```

## 🤝 Contributing

Found a bug? Got a cuter GIF? Have a fact-worthy idea?
Open an issue or send a PR — **all cats welcome**. 🐈

<div align="center">

<img src="app/src/main/res/drawable/catbox.png" alt="catbox" width="60"/>

**If you liked this project, drop a ⭐ — it makes the pixel cat purr.**

Made with 💜 and way too much ☕

</div>
```