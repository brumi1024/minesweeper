package tcp;

import player.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpServer extends Network {

    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    public TcpServer(Player c) {
        super(c);
    }

    @Override
    public void connect(String ip) {
        disconnect();
        try {
            serverSocket = new ServerSocket(6569);

            Thread rec = new Thread(new ReceiverThread());
            rec.start();
        } catch (IOException e) {
            Logger.getLogger(TcpServer.class.getName()).log(Level.SEVERE,"Could not listen on port: 6569.", e);
        }
    }

    @Override
    public void send(short index) {
        if (out == null)
            return;
        System.out.println("Sending index: " + index + " to server");
        try {
            out.writeInt(index);
            out.flush();
        } catch (IOException e) {
            Logger.getLogger(TcpServer.class.getName()).log(Level.SEVERE,"Error in sending.", e);
        }
    }

    @Override
    void disconnect() {
        try {
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (clientSocket != null)
                clientSocket.close();
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            Logger.getLogger(TcpServer.class.getName()).log(Level.SEVERE,"Error while closing connection.", e);
        }
    }

    private class ReceiverThread implements Runnable {

        public void run() {
            try {
                Logger.getLogger(TcpServer.class.getName()).log(Level.INFO,"Waiting for client");
                clientSocket = serverSocket.accept();
                Logger.getLogger(TcpServer.class.getName()).log(Level.INFO,"Client connected.");
            } catch (IOException e) {
                Logger.getLogger(TcpServer.class.getName()).log(Level.SEVERE,"Accept failed.", e);
                disconnect();
                return;
            }

            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                out.flush();
            } catch (IOException e) {
                Logger.getLogger(TcpServer.class.getName()).log(Level.SEVERE,"Error while getting streams.", e);
                disconnect();
                return;
            }

            try {
                while (true) {
                    playerController.indexReceived((short) in.readInt());
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                Logger.getLogger(TcpServer.class.getName()).log(Level.SEVERE,"Client disconnected.", ex);
            } finally {
                disconnect();
            }
        }
    }
}
