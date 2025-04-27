import java.time.LocalDate;

public class Match {
    private Team team1;
    private Team team2;
    private int t1goals;
    private int t2goals;
    private LocalDate date;

    public Match(Team team1, Team team2, int t1goals, int t2goals, LocalDate date) {
        this.t1goals = t1goals;
        this.t2goals = t2goals;
        this.team1 = team1;
        this.team2 = team2;
        this.date = date;
        team1.addGoals(t1goals,t2goals);
        team2.addGoals(t2goals,t1goals);

        if (t1goals > t2goals) {
            team1.setWin();
            team2.setLoss();
        } else if (t1goals == t2goals) {
            team1.setDraw();
            team2.setDraw();
        } else {
            team1.setLoss();
            team2.setWin();
        }

    }

    public Match(Team team1, Team team2, LocalDate date) {
        this.team1 = team1;
        this.team2 = team2;
        this.date = date;
        this.t1goals = -1;
        this.t2goals = -1;
    }


    public void SetResult(int t1goals, int t2goals) {
        this.t1goals = t1goals;
        this.t2goals = t2goals;

        if (t1goals > t2goals) {
            team1.setWin();
            team2.setLoss();
        } else if (t1goals == t2goals) {
            team1.setDraw();
            team2.setDraw();
        } else {
            team1.setLoss();
            team2.setWin();
        }
    }

    public int getTeam1Goals() {
        return t1goals;
    }

    public int getTeam2Goals() {
        return t2goals;
    }

    public Team getTeam1() {
        return team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public LocalDate getDate() {
        return date;
    }


    public void printMatch() {
        System.out.print(team1 + " | Pts: " + t1goals + "\n" + team2 + " | Pts: " + t2goals);
    }
}
