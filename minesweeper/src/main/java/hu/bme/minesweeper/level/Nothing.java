package hu.bme.minesweeper.level;

public class Nothing extends Cell {
    public int step() {
        return numberOfNeighbouringMines();
    }

    @Override
    public void draw() {
        button.setDisable(true);
        if (step() > 0) {
            button.setText("" + step());
        }
    }
}
