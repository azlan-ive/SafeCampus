# SafeCampus 🛡️

**SafeCampus** is a crowdsourced safety application designed to help students stay aware of their surroundings on campus. It allows users to report incidents and view real-time safety information using an interactive map interface, helping the community make safer decisions in real time.

---

## 🚀 Features

- **User Authentication**: Secure sign-up and login system powered by Firebase Authentication.
- **Incident Reporting**: Easy-to-use form to report accidents, crimes, medical emergencies, fires, and more.
- **Real-Time Map**: View campus incidents and static "Campus Locations" (Security Posts, Clinics, Emergency Points) on a Google Map.
- **Location Tracking**: Automatic detection of the user's current location (e.g., UiTM Jasin) using Fused Location Provider.
- **Recent Incidents Dashboard**: A scrollable history of all reported incidents with reporter details and exact timestamps.
- **User Profiles**: Manage personal account information fetched directly from the cloud.
- **Material 3 Design**: A modern, clean, and intuitive user interface using the latest Android design standards.

---

## 🛠️ Tech Stack

- **Platform**: Android (Min SDK 24)
- **Language**: Java
- **Database**: Firebase Realtime Database
- **Authentication**: Firebase Auth
- **Maps & Location**: Google Maps SDK, Google Play Services Location
- **UI Components**: Material Design 3

---

## 📸 Screenshots

| Login | Main Menu | Map View |
|-------|-----------|----------|
| ![Login Screen](https://via.placeholder.com/200x400?text=Login) | ![Main Menu](https://via.placeholder.com/200x400?text=Main+Menu) | ![Map View](https://via.placeholder.com/200x400?text=Map+View) |

---

## 💻 Installation & Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/azlan-sys/SafeCampus.git
   ```
2. **Firebase Setup**:
   - Create a project in the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app with the package name `com.example.userinterface`.
   - Download `google-services.json` and place it in the `app/` directory.
   - Enable **Email/Password** Authentication and **Realtime Database**.
3. **Google Maps API**:
   - Obtain an API Key from the [Google Cloud Console](https://console.cloud.google.com/).
   - Enable **Maps SDK for Android** and **Geocoding API**.
   - Add your key to `AndroidManifest.xml`:
     ```xml
     <meta-data
         android:name="com.google.android.geo.API_KEY"
         android:value="YOUR_API_KEY_HERE" />
     ```
4. **Build and Run**: Open the project in Android Studio and run it on an emulator or a physical device.

---

## 👥 Developers

**Program**: Information System Engineering  
**Course**: ICT 602

- **Muhammad Azlan Bin Muhamad** (2023436396)
- **Muhammad Danial Alimin Bin Zamri** (2023298798)
- **Muhammad Hariz Bin Othaman** (2023239952)
- **Abd Salam Bin Salahhudin** (2023218022)
- **Mirza Arif Bin Aziz** (2023216966)
- **Muhammad Nadzrin Danial Bin Mohd Yusof** (2023637242)

---

## 📄 License

This project is developed for educational purposes as part of the ICT 602 course.

© 2026 SafeCampus
