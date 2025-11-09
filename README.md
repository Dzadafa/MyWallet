# MyWallet 💸

> **Languages:** > [🇬🇧 English](../README.md) | [🇮🇩 Bahasa Indonesia](./docs/README_ID.md) | [🇨🇳 简体中文](./docs/README_ZH.md) | [🇯🇵 日本語](./docs/README_JA.md)

<p align="center">
  <img src="./app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="MyWallet App Icon" width="150">
</p>

MyWallet is a modern, offline-first personal finance tracker for Android built entirely with Kotlin. It uses a local Room database to keep your data fast, private, and always available, while providing smart financial insights and helpful home-screen widgets.

---

## ✨ Features

### 📱 Core App Features

* 📊 **Real-time Dashboard:** See your Current Balance, Total Income, and Total Expense, all filterable by time (All Time, This Month, This Year).
* 📈 **Visual Charts:**
    * A **Doughnut Chart** to see exactly where your money is going.
    * A **Bar Chart** to compare your total Income vs. Expense.
* ✍️ **Log Everything:** Easily track income and expenses with date, description, amount, and category.
* ⭐ **Smart Wishlist:**
    * Add items you want to save for.
    * Get instant **affordability checks** against your balance.
    * See a **savings timeline** (e.g., "Est. 5 months") based on your habits.
* ✏️ **Edit & Delete:**
    * Tap the settings icon on any transaction to edit or delete it.
    * Tap the settings icon on a wishlist item to edit it.
    * Mark a wishlist item "complete," and the edit icon cleverly turns into a delete button.
* 🔔 **Notifications:** Get an instant local notification every time you add a new transaction.
* ⏰ **Daily Reminders:** Set a custom daily reminder (default 9:00 PM) so you never forget to log your finances.
* 🎨 **Theme Settings:** Instantly switch between **Light**, **Dark**, or **System Default** themes.
* 🔒 **Offline & Private:** All data is saved securely to a private, on-device Room database. No account or internet connection required.

### 🏠 Home Screen Widgets

* 💰 **Stats Widget:** Your balance, income, and expense totals at a glance. Updates instantly when you make changes in the app.
* 🛒 **Wishlist Widget:** A scrollable, interactive list. You can check off your goals directly from your home screen.
* ➕ **Quick Add Widget:** A simple button that opens a dedicated screen to quickly add a new transaction without opening the full app.

---

## 🛠 Tech Stack

* **Language:** 100% **Kotlin**
* **Architecture:** **MVVM** (ViewModel, LiveData)
* **Database:** **Room** (for local, on-device storage)
* **UI:** **ViewBinding**, **Android Navigation Component**, Material Components
* **Concurrency:** **Kotlin Coroutines**
* **Async:** `AlarmManager` & `BroadcastReceiver` (for daily reminders)
* **Charts:** **MPAndroidChart**
* **Widgets:** **AppWidgets** with `RemoteViews` & `ListView`

---

## 🚀 How to Build

This project uses the Gradle wrapper.

1.  Clone the repository.
2.  Connect an Android device or start an emulator.
3.  Run `gradlew installDebug` from the project's root directory.

---

## 🔮 Future Roadmap (v2.0)

* 🎨 **Material 3 UI:** Migrating the entire app to the modern "Material You" design system.
* 🔔 **Push Notifications:** Adding Firebase Cloud Messaging (FCM) for important alerts.
* ⚙️ **Remote Config:** Using Firebase Remote Config to show dynamic in-app modals or messages.
