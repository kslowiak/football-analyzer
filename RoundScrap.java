import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;

public class RoundScrap {


    public static List<Match> fetchRoundMatches(int roundNumber, Map<String, Team> teamsByName) {
        List<Match> matches = new ArrayList<>();
        String url = "https://www.sofascore.com/api/v1/unique-tournament/229/season/61452/events/round/" + roundNumber;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONObject obj = new JSONObject(content.toString());
            JSONArray events = obj.getJSONArray("events");

            for (int i = 0; i < events.length(); i++) {
                JSONObject match = events.getJSONObject(i);
                JSONObject homeTeamJson = match.getJSONObject("homeTeam");
                JSONObject awayTeamJson = match.getJSONObject("awayTeam");

                String homeName = homeTeamJson.getString("name");
                String awayName = awayTeamJson.getString("name");

                // Pobieramy obiekty Team z mapy
                Team homeTeam = teamsByName.get(homeName);
                Team awayTeam = teamsByName.get(awayName);

                // Jeśli którejś drużyny nie ma, pomiń mecz
                if (homeTeam == null || awayTeam == null) {
                    System.out.println("⚠️ Drużyna nie znaleziona w mapie: " + homeName + " vs " + awayName);
                    continue;
                }

                JSONObject homeScoreObj = match.optJSONObject("homeScore");
                JSONObject awayScoreObj = match.optJSONObject("awayScore");

                int homeScore = -1;
                int awayScore = -1;

                if (homeScoreObj != null && awayScoreObj != null &&
                    homeScoreObj.has("current") && awayScoreObj.has("current")) {
                    homeScore = homeScoreObj.getInt("current");
                    awayScore = awayScoreObj.getInt("current");
                }

                long startTimestamp = match.getLong("startTimestamp") * 1000L;
                LocalDate matchDate = Instant.ofEpochMilli(startTimestamp)
                                            .atZone(ZoneId.of("Europe/Warsaw"))
                                            .toLocalDate();

                Match m;
                if (homeScore >= 0 && awayScore >= 0) {
                    m = new Match(homeTeam, awayTeam, homeScore, awayScore, matchDate);
                } else {
                    m = new Match(homeTeam, awayTeam, matchDate);
                }
                matches.add(m);

            }

        } catch (Exception e) {
            System.out.println("Błąd przy rundzie " + roundNumber);
            e.printStackTrace();
        }

        return matches;
    }



    // Pobieranie danych z danej rundy i zapisywanie do pliku
    public static void fetchRoundData(int roundNumber, BufferedWriter writer) {
        String url = "https://www.sofascore.com/api/v1/unique-tournament/229/season/61452/events/round/" + roundNumber;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            parseAndWriteMatches(content.toString(), roundNumber, writer);

        } catch (Exception e) {
            System.out.println("Błąd przy rundzie " + roundNumber);
            e.printStackTrace();
        }
    }

    // Przetwarzanie meczów z JSON i zapisywanie do CSV
    private static void parseAndWriteMatches(String json, int round, BufferedWriter writer) {
        JSONObject obj = new JSONObject(json);
        JSONArray events = obj.getJSONArray("events");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));

        for (int i = 0; i < events.length(); i++) {
            JSONObject match = events.getJSONObject(i);
            JSONObject homeTeam = match.getJSONObject("homeTeam");
            JSONObject awayTeam = match.getJSONObject("awayTeam");

            String home = homeTeam.getString("name");
            String away = awayTeam.getString("name");

            long startTimestamp = match.getLong("startTimestamp") * 1000L;
            String formattedDate = sdf.format(new Date(startTimestamp));

            JSONObject homeScoreObj = match.optJSONObject("homeScore");
            JSONObject awayScoreObj = match.optJSONObject("awayScore");

            int homeScore = -1;  // Ustawiamy domyślną wartość -1, jeśli brak wyniku
            int awayScore = -1;

            // Jeśli istnieje wynik, przypisz go
            if (homeScoreObj != null && awayScoreObj != null &&
                    homeScoreObj.has("current") && awayScoreObj.has("current")) {

                homeScore = homeScoreObj.getInt("current");
                awayScore = awayScoreObj.getInt("current");
            }

            // Zapisujemy wynik do CSV jako dwie osobne kolumny dla gospodarzy i gości
            try {
                writer.write(round + ";" + formattedDate + ";" + home + ";" + away + ";" + homeScore + ";" + awayScore + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Funkcja zapisująca dane wszystkich rund do pliku CSV
    public static void saveAllRoundsToCSV(int startRound, int endRound, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Runda;Data;Gospodarz;Gość;Wynik Gospodarzy;Wynik Gości\n");  // Nagłówki kolumn
            for (int i = startRound; i <= endRound; i++) {
                System.out.println("Przetwarzam rundę: " + i);
                fetchRoundData(i, writer);  // Pobieranie i zapisywanie danych dla każdej rundy
            }
            System.out.println("Zapisano dane do pliku: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
