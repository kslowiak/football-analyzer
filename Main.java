import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        List<Team> total = SofaScoreScraper.fetchTotalStats();
        List<Team> home = SofaScoreScraper.fetchHomeStats();
        List<Team> away = SofaScoreScraper.fetchAwayStats();

        Map<String, Team> teamsByName = new HashMap<>();
        for (Team team : total) {
            teamsByName.put(team.getName(), team);
        }

        System.out.println("\n Tabela ogólna:");
        total.forEach(Team::printStats);

        System.out.println("\n Tabela domowa:");
        home.forEach(Team::printStats);

        System.out.println("\n Tabela wyjazdowa:");
        away.forEach(Team::printStats);

        //RoundScrap.fetchRoundData(30);

        RoundScrap.saveAllRoundsToCSV(1, 34, "1liga_wyniki.csv");

        List<Match> allMatches = new ArrayList<>();
        for (int i = 1; i <= 34; i++) {
            List<Match> roundMatches = RoundScrap.fetchRoundMatches(i, teamsByName);
            allMatches.addAll(roundMatches);
        }

        System.out.println("Liczba meczów: " + allMatches.size());

        SofaScoreScraper.saveAllStandingsToCSV(total, home, away);
    }
}
