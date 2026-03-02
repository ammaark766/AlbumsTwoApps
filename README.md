# AlbumsTwoApps (App A + App B)

This project demonstrates two Android applications communicating using a ContentProvider.

App A (:appa)
- Owns and stores the album data using Room.
- Exposes the data through a ContentProvider.

App B (:appb)
- Consumes App A's ContentProvider using ContentResolver.
- Then Displays albums in a RecyclerView.
- Supports Add, Edit, and Delete operations.


------------------------------------------------------------

BUILD CONFIGURATION

Compile SDK: 36  
Target SDK: 36  
Minimum SDK: 24  
Java Version: 11  
Test Runner: androidx.test.runner.AndroidJUnitRunner  

App A applicationId: com.example.albumstwoapps  
ContentProvider authority: com.example.albumstwoapps.provider  
App B applicationId: com.example.appb  

------------------------------------------------------------

BUILD & INSTALL INSTRUCTIONS

1) Install App A (REQUIRED FIRST)

App A must be installed because it first hosts the ContentProvider.

./gradlew :appa:installDebug

2) Install App B

./gradlew :appb:installDebug

You can also run either module directly from Android Studio.

------------------------------------------------------------

HOW TO USE THE APPLICATIONS

App A:
App A mainly exists to host the ContentProvider and Room database.
No UI interaction is required.

App B:
When launching App B:

- View albums: The list loads automatically.
- Add album: Tap Add -> Enter Title and Artist -> Save.
- Edit album: Tap an album -> Modify fields -> Save.
- Delete album: Long press an album -> Confirm deletion.

All ops in App B write to App A through the ContentProvider.

------------------------------------------------------------

AUTOMATED INSTRUMENTED TESTS

These tests run on an emulator or connected device.

Run App A tests:

./gradlew :appa:connectedAndroidTest

Open report:

open appa/build/reports/androidTests/connected/index.html

Run App B tests (App A must be installed first):

./gradlew :appa:installDebug :appb:connectedAndroidTest

Open report:

open appb/build/reports/androidTests/connected/debug/index.html

Run everything:

./gradlew :appa:installDebug :appb:installDebug :appa:connectedAndroidTest :appb:connectedAndroidTest


------------------------------------------------------------

SUBMISSION NOTES

This repository includes:
- Full source code for both applications.
- Clear build and test instructions.
- Instrumented tests for App A and App B.
