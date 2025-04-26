public class Team {
    private String name;
    private int wins;
    private int draws;
    private int losses;
    private int goalsScored;
    private int goalsConceded;

    public Team(String name) {
        this.name = name;
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
        this.goalsScored = 0;
        this.goalsConceded = 0;
    }

    public void recordMatch(int goalsFor, int goalsAgainst) {
        this.goalsScored += goalsFor;
        this.goalsConceded += goalsAgainst;

        if (goalsFor > goalsAgainst) {
            this.wins++;
        } else if (goalsFor == goalsAgainst) {
            this.draws++;
        } else {
            this.losses++;
        }
    }


    public int getDraws() {
        return draws;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getPoints() {
        return wins * 3 + draws;
    }

    public int getMatches() {
        return wins + draws + losses;
    }

    public String getName() {
        return name;
    }

    public int getGoalsScored() {
        return goalsScored;
    }

    public int getGoalsConceded() {
        return goalsConceded;
    }


    public Void setWin() {
        this.wins++;
        return null;
    }

    public Void setLoss() {
        this.losses++;
        return null;
    }

    public Void setDraw() {
        this.draws++;
        return null;
    }

    public void addGoals(int goalsFor, int goalsAgainst) {
        this.goalsScored += goalsFor;
        this.goalsConceded += goalsAgainst;
    }


    public void printStats() {
        System.out.println(name + " | Pts: " + getPoints() + " | Matches: " + getMatches() +
                " | W: " + wins + " | D: " + draws + " | L: " + losses +
                " | GF: " + goalsScored + " | GA: " + goalsConceded);
    }
}
