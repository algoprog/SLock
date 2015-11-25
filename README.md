# SLock
![alt tag](https://lh3.googleusercontent.com/-kRFuv6xrEMM/VlWWGO0o5VI/AAAAAAAAAYo/KF36LKeA8zo/s920-Ic42/slock.png)

SLock is an open source Android lock screen with a gesture recognition algorithm implemented from scratch.


# The algorithm

The task of checking if two gestures match is equivalent to checking the similarity of two sets of points. Before comparing the two sets, we normalize them; we move the sets so that the first point of each set is (0,0) and then we resize each set to fit in a predefined frame. SLock uses a simple point sets similarity measure. Let A be the set of points of the saved gesture and B the set of points of the drawn gesture to unlock the device. To measure the similarity of the two sets we use this formula:

![alt tag](https://lh3.googleusercontent.com/-ZflNgwrO4DU/VlWXcOwKZWI/AAAAAAAAAY4/B6IIvsOFDOA/s413-Ic42/formula.png)

where:

![alt tag](https://lh3.googleusercontent.com/-qZxm8JjpqOM/VlWZa0SwniI/AAAAAAAAAZI/peWVOTDMifs/s187-Ic42/expl.png)

What we get from this formula is a measure of distance between the two sets in pixels. If this distance is above a predefined threshold then the gesture in accepted as correct.


# Known issues

The app has a strange bug possibly related to the lock mechanism that disables the home button. After a few minutes of leaving the device locked (~10min) the app crashes. Anyone who fixes this bug will become a collaborator. Also the app will be uploaded to Google Play as soon as this bug is fixed.

