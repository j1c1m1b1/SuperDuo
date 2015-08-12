# SuperDuo
Repository for hosting the Super Duo project apps in the Android Developer Nanodegree

#Application Improvements
##Alexandria
1. The application has a lot of wasted whitespace, both in landscape and portrait mode.
2. The list of books doesn't display an empty view, if the user haven't added any books yet, the app should notifiy that information to the user and invite them to add one, even more if the app should start in the add/scan book fragment.
3. The search EditText should implement an autocomplete feature, if the text doesn't match the any book, the user should be notified of this.
4. The app doesn't contemplate any connection status case, if the app is disconnected from the internet and the user searches a book the app immediately crashes.
5. The app crashes when trying to add a book with the ISBN code, however the book is added to thew list of books.
6. If the scan functionality is not implemented it shouldn't be presented as an option in the application.
7. In the book detail fragment the app crashes when rotating the device to landscape mode.
8. Beacuse the app is using a relative view to display the book detail content, if the book subtitle is too long the text of the subtitle and the description will overlap, preventing the users to read both of the texts.
9. The app doesn't listen to the databse change events, so if the user deletes a book it will appear in the book list if the app isn't restarted.
10. The settings activity shows only one option, wasting memory in the handling of a whole activity lifecycle to edit just one option. I suggest to show the dialog to change the startscreen directly in the menu item, avoiding a bad user experience.
11. If some of the book contents are missing, the app should provide some placeholder text indicating the situation to the user.
12. The about app section should be shown in a dialog to improve the experience to the user.

##Football Scores
