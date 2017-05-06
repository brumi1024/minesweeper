package tcp;

import player.Player;

public abstract class Network {

    Player playerController;

    Network(Player c) {
        playerController = c;
    }

    public abstract void connect(String ip);

    abstract void disconnect();

    public abstract void send(Object obj);

}
