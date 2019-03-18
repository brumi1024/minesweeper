package hu.bme.minesweeper.tcp;

public abstract class Network implements SocketListener {
    /**
     * Connected status flag.
     */
    private boolean connected;

    /**
     * A SocketListener instance with which the network notifies the JavaFX UI.
     */
    private SocketListener fxListener;

    /**
     * Create a new Network object based on a given SocketListener.
     * @param s SocketListener param.
     */
    Network(SocketListener s) {
        fxListener = s;
        connected = false;
    }

    /**
     * Connect to the given IP address.
     *
     * @param ip IP address param.
     */
    public abstract void connect(String ip);

    /**
     * Disconnect and destroy the sockets.
     */
    public abstract void disconnect();

    /**
     * Send an object through the connection.
     *
     * @param obj Object to send.
     */
    public abstract void send(Object obj);

    /**
     * Check if the connection is up.
     *
     * @return true if the connection is up.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Set the connection status.
     *
     * @param connected status of the connection. True if it is up and running.
     */
    void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * Message handler method.
     *
     * @param data incoming data param.
     */
    @Override
    public void onMessage(final Object data) {
        javafx.application.Platform.runLater(() -> fxListener.onMessage(data));
    }

    /**
     * Disconnection handler method.
     *
     * @param isClosed closed status flag.
     */
    @Override
    public void onDisconnectedStatus(final boolean isClosed) {
        javafx.application.Platform.runLater(() -> fxListener.onDisconnectedStatus(isClosed));
    }
}
