package hu.bme.minesweeper.tcp;

public interface SocketListener {
    void onMessage(Object data);

    void onDisconnectedStatus(boolean isClosed);
}
