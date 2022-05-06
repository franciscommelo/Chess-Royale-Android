# CHESS PUZZLES for Android
Main features:
- Play chess offline
- Play online with other users. Create a challenge/lobby, and wait for someone to accept.
- Solve daily chess puzzles obtained from the [lichess API](https://lichess.org/api/puzzle/daily). You can try these puzzles on the browser [here](https://lichess.org/training/daily). These puzzles get saved on the app, along with their states of completion and meta-data, like the ID and date.



## Activity/Screens demonstration


![APP UI DEMO](https://i.imgur.com/PmSC2mm.png)

## Fundamental libraries and technologies used
- [Kotlin serialization](https://github.com/Kotlin/kotlinx.serialization). Used for turning the json string data obtained from the lichess API and convert it to an object
- [Android Volley](https://developer.android.com/training/volley/simple). Used for making a get request do the lichess API
- [Android Room](https://developer.android.com/training/data-storage/room). Used for storing chess puzzles in the phone's local database
- [Firebase](https://developer.android.com/studio/write/firebase?hl=en). Used for playing online. To create games, accept games and pass data between the user's moves and player turns.
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel?hl=en). Used for saving data from an activity when the activity rotates or is when it's running in the background
- [LiveData](https://developer.android.com/topic/libraries/architecture/livedata?hl=en). Used for observing values in the activity's ViewModel and notifying them to the activity


### Firestore

In order for online matchmaking to function, there needs to be the configuration of a Firestore project in the context of an Android application.

To achieve this, follow the next steps:

1.  Create a Firebase project  [here](https://console.firebase.google.com/)
2.  In the center of the project overview page, click the  **Android**  icon or  **Add app**  to launch the setup workflow
3.  Enter your app's package name in the Android package name field (If unchanged, the package name is  `edu.isel.pdm.li51xd.g08.drag`)
4.  Click  **Register app**
5.  Click  **Download google-services.json**  to obtain your Firebase Android config file (`google-services.json`)
6.  Move the config file (`google-services.json`) into the module (app-level) directory of the app 
7.  Create a Firestore database for the project, making sure there is permission to read/write data

A more detailed guide is available  [here](https://firebase.google.com/docs/android/setup).
