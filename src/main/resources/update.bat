@echo off
setlocal enabledelayedexpansion

set applicationparameters=%~1

taskkill /fi "Imagename eq EverywhereClient.exe" | find /I "EverywhereClient.exe" > nul && (
echo EverywhereClient is running, kill process
taskkill /IM "EverywhereClient.exe" /F) || (echo EverywhereClient is not running)

echo Starting to EverywhereClient

for %%? in ("%~dp0..") do set parent=%%~f?\

set AGENT_HOME=%parent%
set UPDATE_HOME=%AGENT_HOME%updates

cd %AGENT_HOME%

set jredirectory=%AGENT_HOME%java
set libdirectory=%AGENT_HOME%lib

set applicationclass=org.silentsoft.everywhere.client.update.App

rem -----------------------------------------------------------------------------------------
rem ---------------------- P O P U L A T E  J A V A  C L A S S P A T H ----------------------
rem -----------------------------------------------------------------------------------------

set javaclasspath=%AGENT_HOME%

pushd %libdirectory%
	for %%i in (*.jar) do (
		set javaclasspath=!javaclasspath!;!libdirectory!\%%i
	)
popd

rem -----------------------------------------------------------------------------------------
rem ---------------------- C A L L  U P D A T E  A P P L I C A T I O N ----------------------
rem -----------------------------------------------------------------------------------------

"%jredirectory%\jre\bin\java.exe" -classpath "%javaclasspath%" %applicationclass%

rem -----------------------------------------------------------------------------------------
rem ------------------------------- U P D A T E  I T  S E L F -------------------------------
rem -----------------------------------------------------------------------------------------

if exist "%UPDATE_HOME%" (
	pushd "%UPDATE_HOME%"
		for /d %%R in (*) do (
			set optiondirectory=!UPDATE_HOME!\%%R\options
			if exist "!optiondirectory!" (
				pushd "!optiondirectory!"
					set deployoptioncount=0
					for %%i in (*.deploy) do (
						set deployoptionvalue=%%~ni
						set /a deployoptioncount+=1
					)

					set includejreoptioncount=0
					for %%i in (*.includeJRE) do (
						set includejreoptionvalue=%%~ni
						set /a includejreoptioncount+=1
					)
				popd

				rd "!optiondirectory!" /s /q

				if !deployoptioncount!==1 if !includejreoptioncount!==1 (
					if "!deployoptionvalue!"=="clean" (
						pushd "!AGENT_HOME!"
							for %%i in ("!AGENT_HOME!"\*) do (
								if not "%%~nxi"=="unins000.dat" if not "%%~nxi"=="unins000.exe" (
									del "%%~dpnxi" /q
								)
							)

							for /d %%i in ("!AGENT_HOME!"\*) do (
								if not "%%~nxi"=="java" if not "%%~nxi"=="updates" if not "%%~nxi"=="log" (
									rd "%%~dpnxi" /s /q
								)
							)

							if "!includejreoptionvalue!"=="y" (
								rd "!jredirectory!" /s /q
							)
						popd
					)
				)

				set rootdirectory=!UPDATE_HOME!\%%R
				pushd "!rootdirectory!"
					for /r %%S in (*) do (
						set update=%%~dpnxS
						set target=!update:*updates=!
						set target=!target:~8!
						set target=!AGENT_HOME!!target!

						for /f "delims=" %%i in ("!target!") do (
							set "targetpath=%%~dpi"
						)

						if not "!targetpath!"=="" (
							if not exist "!targetpath!" (
								mkdir "!targetpath!"
							)
						)

						move /Y "!update!" "!target!"
					)
				popd

				rd "!rootdirectory!" /s /q
			)
		)
	popd

	rd "%UPDATE_HOME%" /s /q
)

rem -----------------------------------------------------------------------------------------
rem ---------------------- P O P U L A T E  J A V A  C L A S S P A T H ----------------------
rem -----------------------------------------------------------------------------------------

pushd %libdirectory%
	for %%i in (*.jar) do (
		set javaclasspath=!javaclasspath!;!libdirectory!\%%i
	)
popd

rem -----------------------------------------------------------------------------------------
rem ---------------------- C A L L  C L I E N T  A P P L I C A T I O N ----------------------
rem -----------------------------------------------------------------------------------------

set applicationclass=org.silentsoft.everywhere.client.application.App
start "" "%jredirectory%\jre\bin\EverywhereClient.exe" -classpath "%javaclasspath%" %applicationclass% %applicationparameters%
