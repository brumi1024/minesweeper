package gui;

import java.util.Objects;

class Board {

	/*short width = 6;
    short height = 4;
	short numberOfMines = 7;
	short[][] isMine = {{1,0,1,0,0,0},{0,1,0,0,0,1},{0,0,0,1,0,0},{0,1,0,0,0,1}};
	short[][] numberOfNeighbours = {{1,3,1,1,1,1},{2,1,3,2,2,0},{2,2,3,0,3,2},{1,0,2,1,2,0}};*/

    short width;
    short height;
    short numberOfMines;
    short[][] isMine;
    short[][] numberOfNeighbours;

    void generateNewBoard(String difficulty) {
        if (Objects.equals(difficulty, "easy")) {
            this.width = 5;
            this.height = 5;
            this.numberOfMines = 4;
            short[][] isMine = {{0, 0, 0, 1, 0}, {0, 1, 0, 0, 0}, {0, 0, 0, 0, 1}, {0, 0, 0, 0, 0}, {0, 1, 0, 0, 0}};
            short[][] numberOfNeighbours = {{1, 1, 2, 0, 1}, {1, 0, 2, 2, 2}, {1, 1, 1, 1, 0}, {1, 1, 1, 1, 1}, {1, 0, 1, 0, 0}};
            this.isMine = isMine;
            this.numberOfNeighbours = numberOfNeighbours;
        }
        if (Objects.equals(difficulty, "medium")) {
            this.width = 9;
            this.height = 5;
            this.numberOfMines = 5;
            short[][] isMine = {{0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 1, 0, 0, 0, 0, 0}, {1, 0, 0, 0, 0, 1, 0, 1, 0}, {0, 0, 0, 0, 0, 0, 0, 1, 0}};
            short[][] numberOfNeighbours = {{0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 1, 1, 1, 0, 0, 0, 0}, {1, 1, 1, 0, 2, 1, 2, 1, 1}, {0, 1, 1, 1, 2, 0, 3, 1, 2}, {1, 1, 0, 0, 1, 1, 3, 1, 2}};
            this.isMine = isMine;
            this.numberOfNeighbours = numberOfNeighbours;
        }
        if (Objects.equals(difficulty, "hard")) {
            this.width = 9;
            this.height = 9;
            this.numberOfMines = 10;
            short[][] isMine = {{0, 0, 0, 1, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 1, 0}, {0, 0, 0, 0, 1, 0, 0, 0, 0}, {1, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 1, 0, 0, 0, 0, 0}, {0, 0, 1, 0, 0, 1, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 1, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 1, 0, 1, 0, 0, 0, 0}};
            short[][] numberOfNeighbours = {{0, 0, 1, 0, 1, 0, 1, 1, 1}, {0, 0, 1, 2, 2, 1, 1, 0, 1}, {1, 1, 0, 1, 0, 1, 1, 1, 1}, {0, 1, 1, 2, 2, 1, 0, 0, 0}, {1, 2, 2, 1, 2, 1, 1, 0, 0}, {0, 1, 1, 2, 2, 0, 2, 1, 1}, {0, 1, 1, 1, 1, 1, 2, 0, 1}, {0, 1, 1, 2, 1, 1, 1, 1, 1}, {0, 1, 0, 2, 0, 1, 0, 0, 0}};
            this.isMine = isMine;
            this.numberOfNeighbours = numberOfNeighbours;
        }

    }

}

