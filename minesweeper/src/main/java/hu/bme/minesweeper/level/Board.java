package hu.bme.minesweeper.level;

import java.util.List;

import java.util.*;

public class Board {

    private Set<Integer> mineIndices;
    private int boardWidth; //szelesseg
    private int boardHeight; //magassag
    private int numOfMines; //aknaszam
    private int mineLeft; //maradek bomba (lehet ez inkabb csak gui)
    private boolean isSingle; //egy- vagy tobbjatekos mod
    public ArrayList<Cell> cells; //ebben a listaban lesznek a mezok

    public Board() {
        cells = new ArrayList<>();
    }

    private void setBoardParameters(String difficulty) {
        if (Objects.equals(difficulty, "easy")) {
            this.setBoardSize(5, 5);
            this.setNumOfMines(5);
        }
        if (Objects.equals(difficulty, "medium")) {
            this.setBoardSize(5, 9);
            this.setNumOfMines(7);
        }
        if (Objects.equals(difficulty, "hard")) {
            this.setBoardSize(9, 9);
            this.setNumOfMines(11);
        }
    }

    //matrix: BOARD.HEIGHT X BOARD.WIDTH!!!
    public void createBoards(String difficulty, boolean client, Set<Integer> mineIndicesParam) {
        setBoardParameters(difficulty);

        if (client) {
            mineIndices = mineIndicesParam;
        } else {
            mineIndices = new HashSet<>();
            this.placeMines(numOfMines); //fetoltjuk
        }

        this.createCells(); //megcsinalja a cellakat + be is linkeli a szomszedokat

    }

    public int getBoardWidth() {
        return boardWidth;
    }

    private void setBoardWidth(int boardWidth) {
        this.boardWidth = boardWidth;
    }

    public int getBoardHeight() {
        return boardHeight;
    }

    private void setBoardHeight(int boardHeight) {
        this.boardHeight = boardHeight;
    }

    public int getNumOfMines() {
        return numOfMines;
    }

    private void setNumOfMines(int numOfMines) {
        this.numOfMines = numOfMines;
    }

    public int getMineLeft() {
        return mineLeft;
    }

    public void setMineLeft(int mineLeft) {
        this.mineLeft = mineLeft;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean isSingle) {
        this.isSingle = isSingle;
    }

    private void setBoardSize(int x, int y) {
        setBoardHeight(x);
        setBoardWidth(y);
    }

    public Set<Integer> getMineIndices() {
        return mineIndices;
    }

    private void placeMines(int numOfMines) {
        int j = 0;
        Random rand = new Random();
        int k;

        while (j < numOfMines) {
            k = rand.nextInt(boardHeight * boardWidth);

            if (mineIndices.add(k)) {
                j++;
            }

        }
    }

    private void createCells() {
        for (int i = 0; i < boardHeight * boardWidth; i++) { //height, then width! height = number of rows
            if (mineIndices.contains(i)) {
                cells.add(i, new Mine()); //berakjuk a listaba
            } else {
                cells.add(i, new Nothing()); //berakjuk a listaba
            }
        }

        //fel van toltve elemekkel az ArrayList<Cell> cells; mar csak be kell linkelgetni a szomszedokat

        for (int rowIndex = 0; rowIndex < boardHeight; rowIndex++) {
            for (int colIndex = 0; colIndex < boardWidth; colIndex++) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (rowIndex + i >= 0 && rowIndex + i < boardHeight && colIndex + j >= 0 && colIndex + j < boardWidth) {
                            //ha leteznek ezek a szomszedok
                            if (!((i == 0) && (j == 0))) {
                                cells.get(rowIndex * boardWidth + colIndex).getNeighbours().add(cells.get((rowIndex + i) * boardWidth + (colIndex + j)));
                            }
                        }
                    }
                }
            }
        }
    }

}
