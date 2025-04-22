import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Team> total = SofaScoreScraper.fetchTotalStats();
        List<Team> home = SofaScoreScraper.fetchHomeStats();
        List<Team> away = SofaScoreScraper.fetchAwayStats();

        System.out.println("\n Tabela ogólna:");
        total.forEach(Team::printStats);

        System.out.println("\n Tabela domowa:");
        home.forEach(Team::printStats);

        System.out.println("\n Tabela wyjazdowa:");
        away.forEach(Team::printStats);

        //RoundScrap.fetchRoundData(30);

        RoundScrap.saveAllRoundsToCSV(1, 34, "1liga_wyniki.csv");
    }
}
