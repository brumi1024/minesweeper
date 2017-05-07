package hu.bme.minesweeper.main;

import hu.bme.minesweeper.player.Player;
import hu.bme.minesweeper.tcp.Network;
import hu.bme.minesweeper.tcp.TcpClient;
import hu.bme.minesweeper.tcp.TcpServer;

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
