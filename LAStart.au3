#cs ----------------------------------------------------------------------------

 AutoIt Version: 3.3.14.5
 Author:         Joseph Riascos

 Script Function:
	Join meeting, lazy ass semester like I said

#ce ----------------------------------------------------------------------------

; Script Start - Add your code below here

#include <MsgBoxConstants.au3>
#include <AutoItConstants.au3>

;Grabs command line parameters, 1 is the first, 2 is the second
Local $classCode = $CmdLine[1]
Local $passcode = $CmdLine[2]


Run("C:\Users\josep\AppData\Roaming\Zoom\bin\Zoom.exe")
WinWaitActive("Zoom")
WinSetState("[ACTIVE]", "", @SW_MAXIMIZE)
MouseClick($MOUSE_CLICK_LEFT, 813, 448)
WinWaitActive("[TITLE:Zoom; CLASS:zWaitHostWndClass]", "")
ConsoleWrite($classCode)
Send($classCode)

For $i = 0 to 4 Step +1
   Send("{TAB}")
Next

Send("{ENTER}")

WinWaitActive("Enter meeting passcode")
Send($passcode & "{TAB}{ENTER}")
WinWaitActive("Join Audio")
Send("{TAB}{ENTER}")

