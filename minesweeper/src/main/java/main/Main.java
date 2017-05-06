package main;

import player.Player;
import tcp.Network;
import tcp.TcpClient;
import tcp.TcpServer;

public class Main {
    public static void main(String[] args) {
        Player p = new Player();

        if (args[0] != null && args[0].equals("server")) {
            Network a = new TcpServer(p);
            a.connect("localhost");
        } else {
            Network a = new TcpClient(p);
            a.connect("localhost");
            a.send((short) 25);
        }
    }
}
