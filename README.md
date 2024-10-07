
# Modern Notes Taking Application 📝

An intuitive Android application designed to help you organize your thoughts and ideas efficiently. SimpleNotesApp2024 leverages Firebase's capabilities to provide real-time synchronization, cloud storage, and user authentication. This app showcases features like note creation, categorization, marking favorites, and user-friendly settings. All these features are wrapped in a sleek, Material Design-inspired UI to deliver a smooth and satisfying user experience.

## Welcome to our Notes App 📸

![image](https://github.com/user-attachments/assets/bef22c27-af4f-4a4b-af7e-892cee39e59b)

## Features 🌟

- **Create and Manage Notes** ✏️: Easily create, update, and delete your notes with a clean interface designed for quick navigation.
- **Firebase Integration** 🔄: Real-time synchronization of notes across devices using **Firebase Realtime Database** and **Firebase Storage**.
- **User Authentication** 🔐: Secure login and registration using **Firebase Authentication** to manage user access and store data securely.
- **Favorites** ⭐: Mark important notes as favorites for easy access whenever you need them.
- **Image Attachments** 🖼️: Upload and attach images to your notes with seamless Firebase Storage integration.
- **Settings Customization** ⚙️: Adjust application preferences such as themes and profile settings for a personalized experience.
- **Material Design UI** 🎨: Adheres to **Material Design** principles for a modern, responsive, and intuitive user interface.

## Getting Started 🚀

To get SimpleNotesApp2024 up and running on your local machine for development and testing, follow these simple steps.

### Prerequisites 📋

- **Android Studio** (Latest version recommended)
- **Firebase Account** for backend services
- **Min SDK Version**: 21 (Android 5.0 Lollipop)
- **Internet Connection** for Firebase requests

### Installation 🔧

1. **Clone the Repository**:

    ```bash
    git clone https://github.com/pmoschos/SimpleNotesApp2024.git
    ```

2. **Open the Project** in Android Studio.
3. **Add Firebase Configuration**:
    - Download the `google-services.json` from your Firebase console.
    - Place it in the `app/` directory of your project.

4. **Sync the Project** to configure Firebase and other dependencies.
5. **Build and Run** on an emulator or physical device.

## Usage 💡

1. **Launch the App** and sign up or log in using your credentials.
2. **Create a New Note** by clicking the "+" button.
3. **Add Images** to enrich your notes by uploading photos directly.
4. **Organize Your Notes** by marking favorites, adding categories, or deleting unwanted notes.
5. **View Favorite Notes** for quick access to your most important information.
6. **Customize Settings** by visiting the settings page to personalize your experience.

## Architecture Overview 🏗️

The app is designed using the **Model-View-Controller (MVC)** pattern to ensure a clean separation between the logic, UI, and data handling:

- **Activities**: Handle user interaction (`MainActivity`, `DashboardActivity`, etc.).
- **Adapters**: Used to link data to views (`DashCardAdapter`, `NoteAdapter`).
- **Models**: Represent core entities like notes and users (`NoteModel`, `UserModel`).
- **Helpers**: Facilitate interactions with Firebase (`DatabaseHelper`, `ImageHandler`).
- **Utilities**: Handle permission requests (`PermissionHelper`) and request codes (`RequestCode`).

## Core Classes and Their Roles 📜

- **MainActivity**: Entry point of the application, directing users to login or register.
- **LoginActivity** & **RegisterActivity**: Manage user authentication via Firebase.
- **DashboardActivity**: Displays a list of all notes, including a favorite filter option.
- **NoteViewActivity**: Presents individual notes and allows users to view details and make edits.
- **CreateNoteActivity**: Facilitates the creation and modification of notes, including attaching images.
- **SettingsActivity**: Provides customization options for the user interface and profile management.
- **FavoriteNotesActivity**: Filters and displays notes marked as favorites.
- **DatabaseHelper**: Acts as a central point for managing Firebase services such as user, note, and image references.

## Design Highlights 🎨

The app's UI follows the **Material Design Guidelines** with a focus on:

- **Consistent Navigation**: Smooth transitions between activities for easy access.
- **Responsive Layout**: Dynamic adjustment to different screen sizes and orientations.
- **User-Centric Elements**: Buttons, cards, and input fields that facilitate easy interaction.

## Technical Stack 🛠️

- **Firebase**: Backend service for real-time data synchronization and storage.
- **Android SDK**: Core platform for developing Android applications.
- **Java**: Main programming language for developing the application.
- **XML**: Used for designing the app's UI components.

## Stay Updated 📢

Be sure to ⭐ star this repository to stay updated with the latest features, enhancements, and bug fixes!

## Contributing 🤝

We welcome contributions! Feel free to open a pull request or submit issues for bug reports and feature suggestions.

## License 📄

🔐 This project is protected under the [MIT License](https://mit-license.org/).

## Contact 📧

**Panagiotis Moschos** - [pan.moschos86@gmail.com](mailto:pan.moschos86@gmail.com)

🔗 *Note: This is an Android Application and requires Android Studio to build and run.*

---

<h1 align="center">Happy Coding 👨‍💻</h1>

<p align="center">
  Made with ❤️ by Panagiotis Moschos (www.linkedin.com/in/panagiotis-moschos)
</p>