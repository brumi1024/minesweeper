package hu.bme.minesweeper.level;

public class Nothing extends Cell {
    /**
     * Handles the step on a not Mine Cell.
     *
     * @return number of neighbouring mines
     */
    public int step() {
        return numberOfNeighbouringMines();
    }

    /**
     * Do the drawing tasks.
     */
    @Override
    public void draw() {
        button.setDisable(true);
        if (step() > 0) {
            button.setText("" + step());
        }
    }
}
