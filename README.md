# Stani Terminator

## Screenshots
<p align="center">
  <img src="screenshots/mainActivity.png" height="500" align="top">
  <img src="screenshots/WorkoutActivity.png" height="500" align="top">
  <img src="screenshots/History.png" height="500" align="top">
</p>

## About the app
This is a simple Android fitness application developed in **Kotlin**.  
It helps users start and track basic workouts, showing a timer, exercises, and motivational quotes.  
The app also saves finished workouts locally and allows the user to view their workout history.

## Main features
- **Two Activities**  
  The main screen lets the user choose between Cardio and Strength workouts and displays a random motivational quote.  
  The second screen shows the workout itself with a timer and a list of exercises.

- **Fragments**  
  The timer and exercise list are separated into two fragments to organize the interface better and make the code easier to maintain.

- **Service**  
  A background service keeps the workout timer running even if the user interacts with other parts of the app.

- **API connection**  
  The app connects to the [ZenQuotes API](https://zenquotes.io/api/random) to display a motivational quote on the main screen.  
  Internet access is required for this feature.

- **SQLite database**  
  Each finished workout is saved with its type, duration, and date.  
  The saved sessions can be viewed later in a simple dialog window.

## How to run
1. Open the project in **Android Studio**.
2. Connect a real Android device or start an emulator.
3. Press **Run** to build and install the app.
4. Make sure the device has an internet connection so the quotes can load.

