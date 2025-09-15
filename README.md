# Chat Application

## Overview

This is a simple real-time chat application built with Jetpack Compose, Room, and Firebase Firestore.

## Architecture & Design Decisions

**Modularization (app, core and feature library modules):**
- UI (Jetpack Compose) → displays chat lists and conversations.
- ViewModel (AndroidX Lifecycle) → manages state, listens for Firestore updates, and triggers database sync.

**Data Layer:**
- Room Database for offline caching and unsynced messages.
- Firestore for real-time communication.

**Dependency Injection:** Hilt modules provide Room, FirebaseFirestore, FirebaseAuth, and FirebaseMessaging.

**Offline-First Approach:** Messages are stored locally first, then synced to Firestore in the background.

## Features Implemented

- User Authentication (Firebase Auth).
- Chat List Screen showing all active chats.
- Chat Detail Screen with real-time messages (sending and receiving).
- Offline Support: Messages are saved in Room and synced when back online.
- Firestore Sync (syncPendingMessages) ensures reliability.
- Push Notifications Ready (via Firebase Messaging, basic setup included).

## Extras

- Added unsynced message retry mechanism (pending messages sync automatically).
- Material 3 UI with Compose.
- Hilt DI for cleaner, testable code.

## Known Issues / Improvements

- Typing Indicator not yet implemented.
- Message Read Receipts missing.
- Push Notifications only partially integrated (device tokens stored, but not yet hooked into Firestore chat logic).
- Better Error Handling could be added for network and database sync failures.
- UI Polish (avatars, message bubbles, timestamps) could be improved with more time.

## Screenshots
<img width="250" height="1200" alt="Screenshot_20250915_155051" src="https://github.com/user-attachments/assets/779f07dc-4284-4e38-a83d-b11f944f10f4" />
<img width="250" height="1200" alt="Screenshot_20250915_155128" src="https://github.com/user-attachments/assets/8e39a2a2-60f5-4290-bb28-a67be21c1eba" />
<img width="250" height="1200" alt="Screenshot_20250915_155141" src="https://github.com/user-attachments/assets/c66e7dda-7a4f-4621-abdd-bc3a519f4a1d" />

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Firebase project setup
- Google Services JSON file configured

### Installation
1. Clone the repository
2. Open in Android Studio
3. Add your `google-services.json` file
4. Build and run the project

## Dependencies

- Jetpack Compose
- Room Database
- Firebase Firestore
- Firebase Auth
- Firebase Messaging
- Hilt for Dependency Injection
- Material Design 3

## Building

To build this project, simply open it in Android Studio and click "Run" or use the command line:

```bash
./gradlew assembleDebug
