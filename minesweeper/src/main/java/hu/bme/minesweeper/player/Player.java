package hu.bme.minesweeper.player;

public class Player {
    private int MineMarked = 0; // number of marked mines
    public String name;

    public int getMineMarked() {
        return MineMarked;
    }

    public void setMineMarked(int mineMarked) {
        MineMarked = mineMarked;
    }

}
