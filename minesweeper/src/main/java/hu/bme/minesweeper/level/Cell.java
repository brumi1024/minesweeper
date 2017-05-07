package hu.bme.minesweeper.level;

public abstract class Cell {
    private boolean marked = false; //megjel�lt-e
    private int adjacentNum = 0;

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    int getAdjacentNum() {
        return adjacentNum;
    }

    void setAdjacentNum(int adjacentNum) {
        this.adjacentNum = adjacentNum;
    }

    public int step() {
        return getAdjacentNum(); //ha nem akna akkor visszaadja a k�r�l�tte l�v� akn�kat
    }

}
