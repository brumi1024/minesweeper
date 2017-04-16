package tcp;

import player.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpClient extends Network {

    private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    public TcpClient(Player c) {
        super(c);
    }

    @Override
    public void connect(String ip) {
        disconnect();
        try {
            socket = new Socket(ip, 6569);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.flush();

            Thread rec = new Thread(new ReceiverThread());
            rec.start();
        } catch (UnknownHostException e) {
            Logger.getLogger(TcpClient.class.getName()).log(Level.SEVERE,
                    "Client connection error: unknown host.", e);
        } catch (IOException ex) {
            Logger.getLogger(TcpClient.class.getName()).log(Level.SEVERE,
                    "Client connection error: error getting streams.", ex);
        }
    }

    @Override
    public void send(Object obj) {
        if (out == null)
            return;
        Logger.getLogger(TcpClient.class.getName()).log(Level.INFO,"Sending index: " + obj + " to server");
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(TcpClient.class.getName()).log(Level.SEVERE,
                    "Client sending error: cannot send index.", ex);
        }
    }

    @Override
    void disconnect() {
        try {
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (socket != null)
                socket.close();
        } catch (IOException ex) {
            Logger.getLogger(TcpClient.class.getName()).log(Level.SEVERE,
                    "Error while closing client connection.", ex);
        }
    }

    private class ReceiverThread implements Runnable {

        public void run() {
            Logger.getLogger(TcpClient.class.getName()).log(Level.INFO,"Waiting for indices...");

            try {
                while (true) {
                    playerController.indexReceived(in.readObject());
                }
            } catch (Exception ex) {
                Logger.getLogger(TcpClient.class.getName()).log(Level.SEVERE,
                        "Server disconnected.", ex);
            } finally {
                disconnect();
            }
        }
    }
}
