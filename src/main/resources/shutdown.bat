@echo off
setlocal enabledelayedexpansion

taskkill /fi "Imagename eq EverywhereClient.exe" | find /I "EverywhereClient.exe" > nul && (
echo Terminate EverywhereClient
taskkill /IM "EverywhereClient.exe" /F || (echo Terminate target is not running)

echo successfully terminated !