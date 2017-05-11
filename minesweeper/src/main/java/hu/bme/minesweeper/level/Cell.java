package hu.bme.minesweeper.level;

import javafx.scene.control.Button;

import java.util.ArrayList;

public abstract class Cell {
    Button button;
    private boolean marked = false;
    private ArrayList<Cell> neighbours;

    Cell() {
        neighbours = new ArrayList<>();
        button = new Button();
    }

    public Button getButton() {
        return button;
    }

    public abstract int step();

    public void mark() {
        if (marked) {
            //remove question mark from button
            button.setStyle("-fx-background-color: #000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);"
                    + " -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; "
                    + "-fx-background-radius: 0,0,0,0; -fx-background-insets: 0,0,0,0;");
            button.setText("");
        } else {
            //put a question mark onto the button
            button.setStyle("-fx-background-color: #000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);"
                    + " -fx-text-fill: red; -fx-font-size: 15px; -fx-font-weight: bold; "
                    + "-fx-background-radius: 0,0,0,0; -fx-background-insets: 0,0,0,0;");
            button.setText("?");
        }
        marked = !marked;
    }

    public abstract void draw();

    public boolean getMarked() {
        return marked;
    }

    public ArrayList<Cell> getNeighbours() {
        return neighbours;
    }

    int numberOfNeighbouringMines() {
        int numOfNeighbours = 0;
        for (Cell myNeighbour : neighbours) {
            if (myNeighbour instanceof Mine) {
                numOfNeighbours++;
            }
        }
        return numOfNeighbours;
    }
}
