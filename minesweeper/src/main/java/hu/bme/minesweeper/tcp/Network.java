package hu.bme.minesweeper.tcp;

import hu.bme.minesweeper.player.Player;

public abstract class Network {

    Player playerController;

    Network(Player c) {
        playerController = c;
    }

    public abstract void connect(String ip);

    abstract void disconnect();

    public abstract void send(Object obj);

}
