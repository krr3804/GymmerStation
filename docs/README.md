# Functions

---
## Login window
- If user is registered -> login with ID and PW
- If not, [Register] button -> [Register] window
- If login is successful -> [Main] window
- [Exit] button -> program stop

## Main window
- [Create Program] button -> [Create Program] window
- [Load Program] button -> [Load Program] window
- [Performance Archive] button -> [Performance Archive] window
- [Exit] button

## [Create Program] window
- Program details
  - Name
  - Purpose
  - Length
  - [Add Division],[Remove Division] -> add or remove division quantity
  - Double-click on specific division number -> [Add Exercise] window
- Save button
- Exit button

## [Add Exercise] window
- Show list of exercises
- Add Exercise
  - Exercise name
  - Sets
  - Rest time
  - Weight Type
- [Save] button
- [Return] -> return to [Create Program] window

## [Load Program] window
- Select program
- [Start Program] button -> [Program Information] window
- [Edit Program] button -> [Program Edit] window
- [Delete Program] button -> delete selected program
- [Return] button -> return to main window 

## [Program Information] window
- Shows selected program detail
- [Start] button -> [Program Operation] window -> start program on current week and division
- [Exit] button -> return to [Load Program] window

## [Program operation] window
- shows exercise detail
- [Start] button -> start stopwatch -> switch to [Pause] button
- [Done] button -> [Rest] window
- [Pause] button -> [Pause] window

## [Rest] window
- Rest time in 'mm:ss' format
- When 'Time left' becomes 00:00, automatically start the next exercise

## [Pause] window
- [Resume] button
- [Save and Exit] button
- [Exit without saving] button

## [Program Edit] window
- Shows information of selected program in [Create Program] window format 
- [Save] button -> save the changes
- [Return] button -> return to [Load Program] window

## [Performance Archive] window
- Shows list of programs in progress
- Shows list of programs terminated
  - Conditions of termination
    1. Reaches its total length
    2. Physically terminated by user
    3. Deleted while in progress
- Select the program archive to be shown
- [Return] button -> return to main window
- [Delete] button -> delete the selected program archive
- [View] button -> [Archive view] window

## [Archive View] window
- [Week - Division] tab
  - Exercise Name
  - Set
  - Reps
  - Weight
  - Rest
  - Time consumed
- [Return] button -> return to [Performance Archive] window

---
# Validation Checks & Error Message

---
## Common validations
- No item selected validation
  - When any action button is pressed without item selection
  - [ERROR MESSAGE] No Item Selected!

## Program Management validations
- Input blank validation
  - When, trying to save with blank cells
  - [ERROR MESSAGE] "Cell name" is blank!
- Input mismatch validation(Number)
  - When non-numeric value is typed in text fields requiring numeric value  
  - [ERROR MESSAGE] "Cell name" must be numeric!
  - When value given is less than or equal to 0
  - [ERROR MESSAGE] "Cell name" must be no less than 0!
- Input mismatch validation(Rest Time)
  - When, trying to save program with rest time cell equal to '00:00'
  - [ERROR MESSAGE] Rest Time is 00:00!
- Duplicate exercises in division validation
  - When, trying to save exercise already in the list
  - [ERROR MESSAGE] Exercise already in the list!
- No division found validation
  - When, trying to save without any division
  - [ERROR MESSAGE] NO Division Found!
- No exercise found validation
  - When, trying to add division quantity in existing division list containing empty exercise list
  - When [Save] button is pressed with a division containing empty exercise list
  - [ERROR MESSAGE] No Exercise Found In Division "Division number"!

---
# Information & Confirmation Alert

---
## Information Alert
- Program save alert
  - [MESSAGE] Program Saved!
- Program complete alert
  - [MESSAGE] Program Completed!
- Program delete alert
  - [MESSAGE] Program Deleted!
- Program edit alert
  - [MESSAGE] Program Edited!
- Program Operation Data delete alert
  - [MESSAGE] Data Deleted!
- Exercise save alert
  - [MESSAGE] Exercise Saved!
- Exercise delete alert
  - [MESSAGE] Exercise Deleted!

## Confirmation Alert
- Data unsaved alert
  - Target Windows : create-program, edit-program, exercise-form, pause
  - When trying to exit without data saved
  - [MESSAGE] Are You Sure To Exit Without Saving?
- Close Program by window close request
  - Target Windows: main, create-program, load-program, edit-program, exercise-form, program-information, performance-archive-list, performance-archive
  - When trying to close program through window close request action
  - [MESSAGE] Are You Sure To Exit?
- Close Program while operating alert
  - Target Windows : program-operation, rest-time, pause
  - When trying to close program in operation through 'exit' button in 'pause' window or forceful closing of window
  - [MESSAGE] Program Still In Progress, Are You Sure To Exit?
- Delete alert
  - Target Windows : load-program, exercise-form, performance-archive-list
  - When trying to delete data
  - [MESSAGE] Are You Sure To Delete "Data Name"
- Delete division alert
  - Target Windows : create-program, edit-program
  - When trying to delete division containing more than one exercise
  - [MESSAGE] Selected Division Is Not Empty, Are You Sure To Delete?