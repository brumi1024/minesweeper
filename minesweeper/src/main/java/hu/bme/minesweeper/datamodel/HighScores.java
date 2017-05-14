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

    /**
     * Create a new HighScores object based on the given parameters.
     *
     * @param name       name of the player
     * @param time       game completion time
     * @param difficulty game difficulty
     */
    public HighScores(String name, int time, String difficulty) {
        this.name = name;
        this.time = time;
        this.difficulty = difficulty;
    }

    /**
     * Get the completion time.
     *
     * @return formatted timestring to show on the UI.
     */
    public String getTime() {
        return convertTime(time);
    }

    /**
     * Get the game's difficulty.
     *
     * @return difficulty string
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * Get the Name of the player.
     *
     * @return name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Get the time in seconds.
     *
     * @return seconds
     */
    public int getSec() {
        return time;
    }

    /**
     * Convert the completion time to human readable format.
     *
     * @param sec completion time in seconds
     * @return formatted timestring
     */
    private String convertTime(int sec) {
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }
}

