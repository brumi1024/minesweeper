package hu.bme.minesweeper.player;

public class Player {
    /**
     * Name of the player.
     */
    private String name;

    /**
     * Flag to monitor whose turn is it in multiplayer.
     */
    private boolean active;

    /**
     * Flag to monitor if the player is the server in a specific instance.
     */
    private boolean server;

    /**
     * Number of found mines in multiplayer.
     */
    private int points;

    /**
     * Create a player based on the given properties.
     *
     * @param name   name of the player
     * @param server is server flag
     */
    public Player(String name, boolean server) {
        this.name = name;
        this.server = server;
        this.points = 0;
    }

    /**
     * Name getter method.
     *
     * @return name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Name setter method.
     *
     * @param name desired name of the player.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks if the player is on turn.
     *
     * @return true if the player has to make a move.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the players move status.
     *
     * @param active must be true if the player is on turn.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Checks if the player is the server.
     *
     * @return true if the player is server.
     */
    public boolean isServer() {
        return server;
    }

    /**
     * Gets the players points.
     *
     * @return number of points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Sets the players points.
     *
     * @param points desired number of points
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Increments the players points by one.
     */
    public void increasePoints() {
        this.points++;
    }


}
