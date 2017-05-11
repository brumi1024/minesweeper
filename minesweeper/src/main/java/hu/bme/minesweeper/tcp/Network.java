package hu.bme.minesweeper.tcp;

public abstract class Network implements SocketListener {
    private boolean connected;
    private SocketListener fxListener;

    Network(SocketListener s) {
        fxListener = s;
        connected = false;
    }

    public abstract void connect(String ip);

    public abstract void disconnect();

    public abstract void send(Object obj);

    public boolean isConnected() {
        return connected;
    }

    void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void onMessage(final Object data) {
        javafx.application.Platform.runLater(() -> fxListener.onMessage(data));
    }

    @Override
    public void onDisconnectedStatus(final boolean isClosed) {
        javafx.application.Platform.runLater(() -> fxListener.onDisconnectedStatus(isClosed));
    }
}
