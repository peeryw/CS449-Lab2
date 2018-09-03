# CS449-Lab2
UMKC COMP-SCI 449 Foundations of Software Engineering Lab2 Assignment
(Continuation of Lab1)



1) Add an application Icon
2) Add menus
  1.Add a menu to the main activity of the application. Include two options, one for resetting the count (“Reset”) and 
    another for displaying information about the application (“About”). Place the reset menu option icon on the action 
    bar with text “Reset” if there is room. Make sure the About menu option always stays in the overflow area.
    Selecting the Reset menu option should reset the count. Selecting the About menu option should display a new activity
    with information about the application. Be sure to include your name in the new activity. 
  Extra Credit:
    Provide a contextual menu using the contextual action mode on the background  of the main activity (or any UI component
    of your choosing) such that when the user long clicks a menu pops up that offers the ball and strike options.
3) Add persistent Storage
    Add a field which counts total outs. The value should persist between sessions (even when user exits the app by pressing
    the back key). The only way to reset the value is by clearing the data for the app through settings.
4) Rotations (Extra Credit)
    Define a layout to be used in landscape mode that positions the buttons side-by-side when device is in landscape mode. The
    strike and ball count should remain unchanged when flipping between portrait and landscape
5) Preferences (Extra Credit)
    Add a menu option for application settings. Selecting settings should bring up a settings activity (Consider using new style
    Preference Fragments ) that allows the user to select whether or not to announce “out” and “walk” audibly using Android’s 
    support for text-to-speech (tts). Preferences should of course persist between sessions. The setting should work. Great the 
    user with an audible “Out” or “Walk” when appropriate.
