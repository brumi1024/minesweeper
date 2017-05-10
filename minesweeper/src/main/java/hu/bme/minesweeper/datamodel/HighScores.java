package hu.bme.minesweeper.datamodel;


import com.j256.ormlite.field.DatabaseField;

public class HighScores {

    private static final String NAME_FIELD_NAME = "name";
    private static final String TIME_FIELD_NAME = "time";
    private static final String DIFFICULTY_FIELD_NAME = "difficulty";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = NAME_FIELD_NAME)
    private String name;

    @DatabaseField(columnName = TIME_FIELD_NAME)
    private int time;

    @DatabaseField(columnName = DIFFICULTY_FIELD_NAME)
    private String difficulty;

    HighScores() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public HighScores(String name, int time, String difficulty) {
        this.name = name;
        this.time = time;
        this.difficulty = difficulty;
    }

    public String getTime() {
        return convertTime(time);
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getName() {
        return name;
    }

    public int getSec() {
        return time;
    }

    private String convertTime(int sec) {
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }
}

