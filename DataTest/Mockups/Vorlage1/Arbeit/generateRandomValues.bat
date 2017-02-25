SET counter=1
SET /A randomNumber=%random% %%100
SET originalFile="D:\Documents\1_Studium\1.2_Master\MA\Thesis\DataTest\Mockups\Vorlage1\Arbeit\sharedStrings.xml"
SET newFile="D:\Documents\1_Studium\1.2_Master\MA\Thesis\DataTest\Mockups\Vorlage1\Arbeit\generated\sharedStrings%counter_%.xml"
copy %originalFile% %newFile%

FindReplace "Max Mustermann" "Horst Seehofer" %newFile%
pause
::set str=teh cat in teh hat
::echo.%str%
::set str=%str:teh=the%
::echo.%str%


:FindReplace <findstr> <replstr> <file>
set tmp="%temp%\tmp.txt"
If not exist %temp%\_.vbs call :MakeReplace
for /f "tokens=*" %%a in ('dir "%3" /s /b /a-d /on') do (
  for /f "usebackq" %%b in (`Findstr /mic:"%~1" "%%a"`) do (
    echo(&Echo Replacing "%~1" with "%~2" in file %%~nxa
    <%%a cscript //nologo %temp%\_.vbs "%~1" "%~2">%tmp%
    if exist %tmp% move /Y %tmp% "%%~dpnxa">nul
  )
)
del %temp%\_.vbs
exit /b

:MakeReplace
>%temp%\_.vbs echo with Wscript
>>%temp%\_.vbs echo set args=.arguments
>>%temp%\_.vbs echo .StdOut.Write _
>>%temp%\_.vbs echo Replace(.StdIn.ReadAll,args(0),args(1),1,-1,1)
>>%temp%\_.vbs echo end with