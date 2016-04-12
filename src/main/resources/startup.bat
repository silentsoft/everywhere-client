@echo off
setlocal enabledelayedexpansion

set AGENT_HOME=%~dp0

cd %AGENT_HOME%proc
start /min update.bat "program arguments" ^& exit