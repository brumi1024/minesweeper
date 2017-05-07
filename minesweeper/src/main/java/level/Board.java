package level;

import java.util.List;

import java.util.*;
import java.lang.Math;

public class Board {

    private boolean[][] minesMatrix; //bomb�k helye
    private int boardWidth; //sz�less�g
    private int boardHeight; //magass�g
    private int numOfMines; //aknasz�m
    private int mineLeft; //marad�k bomba
    private boolean isSingle; //egy vagy t�bbj�t�kos m�d
    private List<Cell> cells; //ebben a list�ban lesznek a mez�k

    public Board() {
        cells = new ArrayList<>();
    }

    private void setBoardParameters(String difficulty) {
        if (Objects.equals(difficulty, "easy")) {
            this.setBoardSize(5, 5);
            this.setNumOfMines(4);
        }
        if (Objects.equals(difficulty, "medium")) {
            this.setBoardSize(5, 9);
            this.setNumOfMines(5);
        }
        if (Objects.equals(difficulty, "hard")) {
            this.setBoardSize(9, 9);
            this.setNumOfMines(10);
        }
    }

    public void createBoards(String difficulty) //p�lyainicializ�l�s
    {
        setBoardParameters(difficulty); //kit�lti a p�lyaadatokat a neh�zs�g alapj�n
        short[][] board = new short[boardWidth][boardHeight];
        minesMatrix = new boolean[boardWidth][boardHeight]; //t�mb inicializ�l�s

        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {
                board[i][j] = (short) (j * boardWidth + i); //indexm�trix
            }
        }

        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {
                this.minesMatrix[i][j] = false; //akna m�trix �res
            }
        }

        this.placeMines(numOfMines); //felt�ltj�k
        this.createCells(); //megcsin�lja a cell�kat

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

    private void placeMines(int numOfMines) {

        int j = 0; //seg�dv�ltoz�
        Random rand = new Random();
        int k = (int) Math.floor(rand.nextInt(boardHeight * boardWidth));  //0-t�l a p�lyam�retig gener�lunk egy v�letlen sz�mot
        while (j < (numOfMines + 1)) {
            if (!this.minesMatrix[k / boardHeight + 1][boardWidth % k]) //megn�zz�k nem-e foglalt helyre akarunk tenni
            {
                this.minesMatrix[k / boardHeight + 1][boardWidth % k] = true; //az adott helyet akna lesz
                j++; //sz�ml�l�t n�velj�k
            }
        }
    }


    private int adjacentMines(int x, int y) {  //h�ny szomsz�dos bomba van
        int adjacentMinesNum = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (x + i >= 0 && x + i < boardHeight && y + j >= 0 && y + j < boardWidth) {
                    if (minesMatrix[x + i][y + j]) {
                        adjacentMinesNum++;
                    }
                }
            }
        }

        return adjacentMinesNum;
    }

    private void createCells() {
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {
                if (this.minesMatrix[i][j]) {
                    Mine newCell = new Mine();
                    cells.add(j * boardWidth + 1, newCell); //berakjuk a list�ba
                } else {
                    Nothing newCell = new Nothing();
                    newCell.setAdjacentNum(adjacentMines(i, j)); //r�gt�n bele is �rjuk hogy h�ny szomsz�dja van
                    cells.add(j * boardWidth + 1, newCell); //berakjuk a list�ba
                }

            }
        }

    }


}
