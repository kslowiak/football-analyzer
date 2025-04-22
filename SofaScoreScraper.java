import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class SofaScoreScraper {

    public static List<Team> fetchTotalStats() {
        return fetchTeams("https://api.sofascore.com/api/v1/unique-tournament/229/season/61452/standings/total"); // index 0 = total
    }

    public static List<Team> fetchHomeStats() {
        return fetchTeams("https://api.sofascore.com/api/v1/unique-tournament/229/season/61452/standings/home"); // index 1 = home
    }

    public static List<Team> fetchAwayStats() {
        return fetchTeams("https://api.sofascore.com/api/v1/unique-tournament/229/season/61452/standings/away"); // index 2 = away
    }


    public static List<Team> fetchTeams(String link) {
        List<Team> teams = new ArrayList<>();

        try {
            String apiUrl = link;

            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(content.toString());
            JSONArray standings = json.getJSONArray("standings")
                    .getJSONObject(0)
                    .getJSONArray("rows");

            for (int i = 0; i < standings.length(); i++) {
                JSONObject row = standings.getJSONObject(i);
                JSONObject teamJson = row.getJSONObject("team");

                String name = teamJson.getString("name");
                int wins = row.getInt("wins");
                int draws = row.getInt("draws");
                int losses = row.getInt("losses");
                int goalsScored = row.getInt("scoresFor");
                int goalsConceded = row.getInt("scoresAgainst");

                Team team = new Team(name);
                for (int w = 0; w < wins; w++) team.setWin();
                for (int d = 0; d < draws; d++) team.setDraw();
                for (int l = 0; l < losses; l++) team.setLoss();
                team.addGoals(goalsScored, goalsConceded);

                teams.add(team);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return teams;
    }
}
