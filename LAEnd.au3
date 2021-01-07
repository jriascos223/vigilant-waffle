#cs ----------------------------------------------------------------------------

 AutoIt Version: 3.3.14.5
 Author:         Joseph Riascos

 Script Function:
	Template AutoIt script.

#ce ----------------------------------------------------------------------------

; Script Start - Add your code below here

#include <MsgBoxConstants.au3>
#include <AutoItConstants.au3>



WinWaitActive("[TITLE:Zoom Meeting; CLASS:ZPContentViewWndClass]", "")
WinSetState("[ACTIVE]", "", @SW_MAXIMIZE)
MouseClick($MOUSE_CLICK_LEFT, 1882, 1013)
Sleep("2000")
MouseClick($MOUSE_CLICK_LEFT, 1765, 958)

