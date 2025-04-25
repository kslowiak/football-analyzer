import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class RoundScrap {




    public static List<Match> fetchRoundMatches(int roundNumber) {
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
                JSONObject homeTeam = match.getJSONObject("homeTeam");
                JSONObject awayTeam = match.getJSONObject("awayTeam");

                String home = homeTeam.getString("name");
                String away = awayTeam.getString("name");

                JSONObject homeScoreObj = match.optJSONObject("homeScore");
                JSONObject awayScoreObj = match.optJSONObject("awayScore");

                int homeScore = -1;
                int awayScore = -1;

                if (homeScoreObj != null && awayScoreObj != null &&
                    homeScoreObj.has("current") && awayScoreObj.has("current")) {
                    homeScore = homeScoreObj.getInt("current");
                    awayScore = awayScoreObj.getInt("current");
                }

                Match m = new Match(home, away);
                m.SetResult(homeScore, awayScore);
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
