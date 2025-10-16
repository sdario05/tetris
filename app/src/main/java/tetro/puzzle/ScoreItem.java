package tetro.puzzle;

public class ScoreItem {
    private String name;
    private int score;

    public ScoreItem() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ScoreItem{name='" + name + "', score=" + score + "}";
    }
}
