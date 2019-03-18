package hu.bme.minesweeper.main;

import hu.bme.minesweeper.datamodel.DatabaseConnection;
import hu.bme.minesweeper.gui.GUI;
import javafx.application.Application;

public class Main {
    /**
     * Main method.
     * @param args Command line args
     */
    public static void main(String[] args) {
        DatabaseConnection.initDatabase();

        new Thread(() -> Application.launch(GUI.class)).start();
    }
}
