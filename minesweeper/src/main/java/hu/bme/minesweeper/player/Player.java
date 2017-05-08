package hu.bme.minesweeper.player;

public class Player {
    private String name;
    private boolean active;
    private boolean server;

    public Player(String name, boolean server) {
        this.name = name;
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }


}
