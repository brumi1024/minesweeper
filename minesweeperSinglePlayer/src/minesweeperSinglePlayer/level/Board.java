package minesweeperSinglePlayer.level;

import java.util.List;

import java.util.*;
import java.lang.Math;

public class Board {

    public boolean[][] minesMatrix; //bombï¿½k helye
    private int boardWidth; //szï¿½lessï¿½g
    private int boardHeight; //magassï¿½g
    private int numOfMines; //aknaszï¿½m
    private int mineLeft; //maradï¿½k bomba
    private boolean isSingle; //egy vagy tï¿½bbjï¿½tï¿½kos mï¿½d
    public List<Cell> cells; //ebben a listï¿½ban lesznek a mezï¿½k
    public short[][] board;

    public Board() {
        cells = new ArrayList<>();
    }

    private void setBoardParameters(String difficulty) {
        if (difficulty == "easy") {
            this.setBoardSize(5, 5);
            this.setNumOfMines(4);
        }
        if (difficulty == "medium") {
            this.setBoardSize(5, 9);
            this.setNumOfMines(5);
        }
        if (difficulty == "hard") {
            this.setBoardSize(9, 9);
            this.setNumOfMines(10);
        }
    }

    //mátrix: BOARD.HEIGHT X BOARD.WIDTH!!!
    public void createBoards(String difficulty) //pï¿½lyainicializï¿½lï¿½s
    {
        setBoardParameters(difficulty); //kitï¿½lti a pï¿½lyaadatokat a nehï¿½zsï¿½g alapjï¿½n
        short[][] board = new short[boardHeight][boardWidth];
        minesMatrix = new boolean[boardHeight][boardWidth]; //tï¿½mb inicializï¿½lï¿½s
        int i = 0;
        int j = 0;
        for (i = 0; i < boardHeight; i++) {
            for (j = 0; j < boardWidth; j++) {
                board[i][j] = (short) (i * boardWidth + j); //indexmï¿½trix
            }
        }

        for (i = 0; i < boardHeight; i++) {
            for (j = 0; j < boardWidth; j++) {
                this.minesMatrix[i][j] = false; //akna mï¿½trix ï¿½res
            }
        }

        this.placeMines(numOfMines, minesMatrix); //feltï¿½ltjï¿½k
        this.createCells(); //megcsinï¿½lja a cellï¿½kat

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

    private void placeMines(int numOfMines, boolean[][] isMine) {


        int j = 0; //segï¿½dvï¿½ltozï¿½
        Random rand = new Random();
        int k=0;
        
        while (j < numOfMines) {
        	k = rand.nextInt(boardHeight * boardWidth); //0-tï¿½l a pï¿½lyamï¿½retig generï¿½lunk egy vï¿½letlen szï¿½mot
        	int colIndex = k%boardWidth;
        	int rowIndex = k/boardWidth;
        	

            if ((!this.minesMatrix[rowIndex][colIndex])) //megnï¿½zzï¿½k nem-e foglalt helyre akarunk tenni
            {
                this.minesMatrix[rowIndex][colIndex] = true; //az adott helyet akna lesz
                j++; //szï¿½mlï¿½lï¿½t nï¿½veljï¿½k
            }
        }
    }


    private int adjacentMines(int x, int y) //hï¿½ny szomszï¿½dos bomba van
    {
        int adjacentMinesNum = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (x + i >= 0 && x + i < boardHeight && y + j >= 0 && y + j < boardWidth) { //itt már jó
                    if (minesMatrix[x + i][y + j]) {
                        adjacentMinesNum++;
                    }
                }
            }
        }

        return adjacentMinesNum;
    }

    private void createCells() {
        for (int i = 0; i < boardHeight; i++) { //height, then width! height = number of rows
            for (int j = 0; j < boardWidth; j++) {
                if (this.minesMatrix[i][j]) {
                    Mine newCell = new Mine();
                    cells.add(i * boardWidth + j, newCell); //berakjuk a listï¿½ba
                } else {
                    Nothing newCell = new Nothing();
                    newCell.setAdjacentNum(adjacentMines(i, j)); //rï¿½gtï¿½n bele is ï¿½rjuk hogy hï¿½ny szomszï¿½dja van
                    cells.add(i * boardWidth + j, newCell); //berakjuk a listï¿½ba
                }

            }
        }

    }


}
