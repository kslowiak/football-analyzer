#!/bin/bash

# Kompilacja
javac -cp ".:json-20230227.jar" Main.java SofaScoreScraper.java Team.java

# Uruchomienie
java -cp ".:json-20230227.jar" Main

# Zatrzymanie (żeby było jak pause)
read -p "Press enter to continue..."

