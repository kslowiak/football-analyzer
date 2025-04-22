@echo off
javac -cp ".;json-20230227.jar" Main.java SofaScoreScraper.java Team.java
java -cp ".;json-20230227.jar" Main
pause
