# MyWallet 💸

> **Languages:**  
> [🇬🇧 English](../README.md) | [🇮🇩 Bahasa Indonesia](./docs/README_ID.md) | [🇨🇳 简体中文](./docs/README_ZH.md) | [🇯🇵 日本語](./docs/README_JA.md)

MyWallet is a modern, personal finance tracker for Android built entirely with Kotlin. It leverages a secure Firebase backend to provide real-time data syncing, smart financial insights, and helpful home-screen widgets.

This app is designed to give you a clear, high-level overview of your financial health, help you track your spending, and motivate you to save for your goals.

## ✨ Features

### Core App Features
* **Core Financial Tracking:** Log any income or expense with details like date, description, amount (in Rupiah), and a custom category.
* **Transaction History:** View all your past entries, sorted into "Recent Income" and "Recent Expenses" lists.
* **Real-time Dashboard:** A command center showing your **Current Balance** (total income minus total expense). The balance turns red if you're in the negative.
* **Time-Based Filtering:** Filter your entire Dashboard to see data for "All Time," "This Month," or "This Year."
* **Visual Charts:**
    * **Expense Breakdown:** A doughnut chart that visually answers, "Where is my money going?" by grouping expenses by category.
    * **Income vs. Expense:** A bar chart for a direct visual comparison of your total income and expenses for the selected time period.
* **Smart Wishlist:**
    * Add items you want to save for, along with their price.
    * **Affordability Check:** Instantly compares the item's price to your current balance.
    * **Savings Timeline:** If you can't afford an item, the app analyzes your average monthly savings and provides a suggestion like, "At your current savings rate, you can get this in about 5 months."
    * **Checklist:** Mark items as "completed" or "purchased" with a checkbox, which moves them to the bottom of the list with a strikethrough.
* **Secure Cloud Data:** All data is saved securely to a private Firebase Firestore database, linked to a unique, anonymous user account created on first launch.
* **Theme Switcher:** An in-app settings page lets you instantly switch between **Light Mode**, **Dark Mode**, or follow the **System Default**.

### Home Screen Widget Features
* **Stats Widget:** A resizable widget that displays your Current Balance, Total Income, and Total Expense directly on your home screen.
* **Wishlist Widget:** A resizable widget that shows your next *incomplete* wishlist item and its smart affordability status (e.g., "You can afford this now!" or "Est. 3 months remaining").
* **Quick Add Widget:** A simple, resizable button widget ("+ Add Transaction") that, when tapped, opens a dedicated screen to quickly add a new income or expense without opening the full app.

## 🛠 Tech Stack
* **Language:** 100% [Kotlin](https://kotlinlang.org/)
* **Backend:** [Firebase](https://firebase.google.com/)
    * **Authentication:** Anonymous login for secure, unique user accounts.
    * **Database:** [Cloud Firestore](https://firebase.google.com/products/firestore) for real-time data storage and syncing.
* **Architecture:**
    * [MVVM](https://developer.android.com/topic/architecture) (Model-View-ViewModel)
    * [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) (ViewModel, LiveData)
* **UI & Navigation:**
    * [Android Navigation Component](https://developer.android.com/guide/navigation) for single-activity navigation.
    * [ViewBinding](https://developer.android.com/topic/libraries/view-binding)
    * Material Components (Buttons, Cards, Text Fields)
* **Charts:** [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
* **Concurrency:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) for background tasks (like Firebase calls).
* **Widgets:** [AppWidgets](https://developer.android.com/guide/topics/appwidgets)

## 🚀 How to Build

This project is built manually using the Gradle wrapper.

1.  **Clone the repository.**
2.  **Firebase Setup:**
    * Create a new project in the [Firebase Console](https://console.firebase.google.com/).
    * Add an Android app with the package name `com.dzadafa.mywallet`.
    * Download the `google-services.json` file provided by Firebase and place it in the `MyWallet/app/` directory.
    * In the Firebase Console, go to **Authentication** -> **Sign-in method** and **enable** the "Anonymous" provider.
    * Go to **Firestore Database** and create a new database.
3.  **Build the App:**
    * Connect an Android device or start an emulator.
    * Run `gradlew installDebug` from the project's root directory.
