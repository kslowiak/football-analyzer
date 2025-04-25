public class Match {
    private String team1;
    private String team2;
    private int t1goals;
    private int t2goals;

    public Match(Team team1, Team team2, int t1goals, int t2goals) {
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

    public Match(String team1, String team2) {
        this.team1 = team1;
        this.team2 = team2;
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

    public String getTeam1Name() {
        return team1;
    }

    public String getTeam2Name() {
        return team2;
    }

    public void printMatch() {
        System.out.print(team1 + " | Pts: " + t1goals + "\n" + team2 + " | Pts: " + t2goals);
    }
}
