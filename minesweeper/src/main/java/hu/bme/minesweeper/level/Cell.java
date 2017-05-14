package hu.bme.minesweeper.level;

import javafx.scene.control.Button;

import java.util.ArrayList;

public abstract class Cell {
    /**
     * Button instance of the cell.
     */
    Button button;

    /**
     * The field is marked flag.
     */
    private boolean marked = false;

    /**
     * List of neighbouring cells.
     */
    private ArrayList<Cell> neighbours;

    /**
     * Create a new empty Cell.
     */
    Cell() {
        neighbours = new ArrayList<>();
        button = new Button();
    }

    /**
     * Gets the button of the cell.
     *
     * @return Button instance of the cell
     */
    public Button getButton() {
        return button;
    }

    /**
     * Abstract step method. Is overridden in the Mine and Nothing classes.
     *
     * @return a number based on the children
     */
    public abstract int step();

    /**
     * Handles the marking process of the cell.
     */
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

    /**
     * Abstract draw method. Is overridden in the Mine and Nothing classes.
     */
    public abstract void draw();

    /**
     * Check if a Cell is marked.
     *
     * @return true if marked
     */
    public boolean getMarked() {
        return marked;
    }

    /**
     * Get the List of neighbouring cells.
     *
     * @return Cell List
     */
    public ArrayList<Cell> getNeighbours() {
        return neighbours;
    }

    /**
     * Get the number of mines next to the Cell instance.
     *
     * @return number of mines
     */
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
