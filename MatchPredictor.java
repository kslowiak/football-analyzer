import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class MatchPredictor {
    private final List<Match> allMatches;
    private final Map<String, Team> teamsByName;

    public MatchPredictor(List<Match> allMatches, Map<String, Team> teamsByName) {
        this.allMatches = allMatches;
        this.teamsByName = teamsByName;
    }

    public void predictAndSaveResults(String predictedMatchesCsv, String predictedStandingsCsv) {
        // 1. Stwórz kopie drużyn
        Map<String, Team> simulatedTeams = deepCopyTeams(teamsByName);

        // 2. Stwórz kopie meczów na podstawie skopiowanych drużyn
        List<Match> simulatedMatches = deepCopyMatches(allMatches, simulatedTeams);

        // 3. Przewiduj wyniki dla meczów bez rozstrzygnięcia i w przyszłości
        Random random = new Random();
        for (Match match : simulatedMatches) {
            if ((match.getTeam1Goals() == -1 || match.getTeam2Goals() == -1)
                    && match.getDate().isAfter(LocalDate.now())) {
                Team homeTeam = simulatedTeams.get(match.getTeam1().getName());
                Team awayTeam = simulatedTeams.get(match.getTeam2().getName());

                double homeAverage = homeTeam.getGoalsScored() / (double) Math.max(1, homeTeam.getMatches());
                double awayAverage = awayTeam.getGoalsScored() / (double) Math.max(1, awayTeam.getMatches());

                int predictedHomeGoals = (int) Math.round(homeAverage + random.nextGaussian());
                int predictedAwayGoals = (int) Math.round(awayAverage + random.nextGaussian());

                predictedHomeGoals = Math.max(0, predictedHomeGoals);
                predictedAwayGoals = Math.max(0, predictedAwayGoals);

                match.SetResult(predictedHomeGoals, predictedAwayGoals);
            }
        }

        // 4. Przebuduj tabelę na podstawie wszystkich meczów
        rebuildStandings(simulatedTeams, simulatedMatches);

        // 5. Zapisz przewidziane mecze i tabelę
        saveMatchesToCsv(simulatedMatches, predictedMatchesCsv);
        saveStandingsToCsv(simulatedTeams, predictedStandingsCsv);
    }

    private Map<String, Team> deepCopyTeams(Map<String, Team> teams) {
        Map<String, Team> copy = new HashMap<>();
        for (Map.Entry<String, Team> entry : teams.entrySet()) {
            Team t = entry.getValue();
            Team newTeam = new Team(t.getName());
            newTeam.addGoals(t.getGoalsScored(), t.getGoalsConceded());
            // UWAGA: NIE kopiujemy wygranych, remisów i porażek!
            copy.put(entry.getKey(), newTeam);
        }
        return copy;
    }

    private List<Match> deepCopyMatches(List<Match> matches, Map<String, Team> simulatedTeams) {
        List<Match> copy = new ArrayList<>();
        for (Match m : matches) {
            copy.add(new Match(
                simulatedTeams.get(m.getTeam1().getName()),
                simulatedTeams.get(m.getTeam2().getName()),
                m.getTeam1Goals(),
                m.getTeam2Goals(),
                m.getDate()
            ));
        }
        return copy;
    }

    private void rebuildStandings(Map<String, Team> teams, List<Match> matches) {
        // Resetujemy statystyki
        for (Team team : teams.values()) {
            team.resetStats();
        }

        // Liczymy wszystko na nowo na podstawie meczów
        for (Match match : matches) {
            Team home = match.getTeam1();
            Team away = match.getTeam2();
            int homeGoals = match.getTeam1Goals();
            int awayGoals = match.getTeam2Goals();

            if (homeGoals >= 0 && awayGoals >= 0) {
                home.addGoals(homeGoals, awayGoals);
                away.addGoals(awayGoals, homeGoals);

                if (homeGoals > awayGoals) {
                    home.setWin();
                    away.setLoss();
                } else if (homeGoals == awayGoals) {
                    home.setDraw();
                    away.setDraw();
                } else {
                    home.setLoss();
                    away.setWin();
                }
            }
        }
    }

    private void saveMatchesToCsv(List<Match> matches, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.append("HomeTeam;AwayTeam;HomeGoals;AwayGoals\n");
            for (Match match : matches) {
                writer.append(match.getTeam1().getName()).append(";")
                        .append(match.getTeam2().getName()).append(";")
                        .append(String.valueOf(match.getTeam1Goals())).append(";")
                        .append(String.valueOf(match.getTeam2Goals())).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveStandingsToCsv(Map<String, Team> teams, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.append("Drużyna;Punkty;Mecze;Zwycięstwa;Remisy;Porażki;Bramki zdobyte;Bramki stracone\n");

            List<Team> sortedTeams = new ArrayList<>(teams.values());
            sortedTeams.sort(Comparator
                    .comparing(Team::getPoints).reversed()
                    .thenComparing((Team t) -> t.getGoalsScored() - t.getGoalsConceded(), Comparator.reverseOrder())
                    .thenComparing(Team::getGoalsScored, Comparator.reverseOrder())
            );

            for (Team team : sortedTeams) {
                writer.append(team.getName()).append(";")
                        .append(String.valueOf(team.getPoints())).append(";")
                        .append(String.valueOf(team.getMatches())).append(";")
                        .append(String.valueOf(team.getWins())).append(";")
                        .append(String.valueOf(team.getDraws())).append(";")
                        .append(String.valueOf(team.getLosses())).append(";")
                        .append(String.valueOf(team.getGoalsScored())).append(";")
                        .append(String.valueOf(team.getGoalsConceded())).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
