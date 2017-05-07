package hu.bme.minesweeper.level;

public class Nothing extends Cell {
    public int step() {
        return super.getAdjacentNum();
    }
}
