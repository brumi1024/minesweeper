package hu.bme.minesweeper.level;

import java.util.*;

public class Board {

    /**
     * List of Cells.
     */
    public ArrayList<Cell> cells;

    /**
     * Set of mine indices.
     */
    private Set<Integer> mineIndices;
    /**
     * Number of columns on the board.
     */
    private int boardWidth;
    /**
     * Number of rows on the board.
     */
    private int boardHeight;
    /**
     * Number of mines on the board.
     */
    private int numOfMines;

    /**
     * Create a new empty Board.
     */
    public Board() {
        cells = new ArrayList<>();
    }

    /**
     * Set the board parameters based on difficulty.
     *
     * @param difficulty desired difficulty string
     */
    private void setBoardParameters(String difficulty) {
        if (Objects.equals(difficulty, "easy")) {
            this.setBoardSize(5, 8);
            this.setNumOfMines(5);
        }
        if (Objects.equals(difficulty, "medium")) {
            this.setBoardSize(8, 8);
            this.setNumOfMines(9);
        }
        if (Objects.equals(difficulty, "hard")) {
            this.setBoardSize(8, 15);
            this.setNumOfMines(19);
        }
    }

    /**
     * Manages the board creation.
     *
     * @param difficulty desired difficulty
     * @param client client mode flag. In this mode the board is copied from the server
     * @param mineIndicesParam in client mode this contains the indices of the mines
     */
    public void createBoards(String difficulty, boolean client, Set<Integer> mineIndicesParam) {
        setBoardParameters(difficulty);

        if (client) {
            mineIndices = mineIndicesParam;
        } else {
            mineIndices = new HashSet<>();
            this.placeMines(numOfMines);
        }

        this.createCells();

    }

    /**
     * Get the number of columns in the board.
     *
     * @return number of columns
     */
    public int getBoardWidth() {
        return boardWidth;
    }

    /**
     * Set the number of columns in the board.
     *
     * @param boardWidth Number of columns
     */
    private void setBoardWidth(int boardWidth) {
        this.boardWidth = boardWidth;
    }

    /**
     * Get the number of rows in the board.
     *
     * @return number of rows
     */
    public int getBoardHeight() {
        return boardHeight;
    }

    /**
     * Set the number of rows in the board.
     *
     * @param boardHeight Number of rows
     */
    private void setBoardHeight(int boardHeight) {
        this.boardHeight = boardHeight;
    }

    /**
     * Get the number of mines.
     *
     * @return number of mines on the board
     */
    public int getNumOfMines() {
        return numOfMines;
    }

    /**
     * set the number of mines.
     *
     * @param numOfMines desired number of mines on the board
     */
    private void setNumOfMines(int numOfMines) {
        this.numOfMines = numOfMines;
    }

    /**
     * Set the board's size.
     * @param x desired height
     * @param y desired width
     */
    private void setBoardSize(int x, int y) {
        setBoardHeight(x);
        setBoardWidth(y);
    }

    /**
     * Get the Set of mine indices.
     *
     * @return Set of the field indices containing mines
     */
    public Set<Integer> getMineIndices() {
        return mineIndices;
    }

    /**
     * Places the mines randomly on the board.
     *
     * @param numOfMines desired number of mines
     */
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

    /**
     * Initialize the cells.
     */
    private void createCells() {
        for (int i = 0; i < boardHeight * boardWidth; i++) { //height, then width! height = number of rows
            if (mineIndices.contains(i)) {
                cells.add(i, new Mine());
            } else {
                cells.add(i, new Nothing());
            }
        }

        for (int rowIndex = 0; rowIndex < boardHeight; rowIndex++) {
            for (int colIndex = 0; colIndex < boardWidth; colIndex++) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (rowIndex + i >= 0 && rowIndex + i < boardHeight && colIndex + j >= 0 && colIndex + j < boardWidth) {
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
