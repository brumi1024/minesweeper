package hu.bme.minesweeper.level;

import java.util.*;

import javafx.scene.control.*;

public abstract class Cell {
    private boolean marked = false; //megjelolt-e
    private int[] coordinates;
    private ArrayList<Cell> neighbours;
    protected Button button;
    
    Cell() {
    	neighbours = new ArrayList<Cell>();
    	button = new Button();
    }
    
    public Button getButton() {
    	return button;
    }
    
    public abstract int step();
    
    public void mark() {
    	if(marked) {
    		//remove question mark from button
            button.setStyle("-fx-background-color: #000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);"
                    + " -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; "
                    + "-fx-background-radius: 0,0,0,0; -fx-background-insets: 0,0,0,0;");
            button.setText("");
    	} else {
    		//put a question mark onto the button
            button.setStyle("-fx-background-color: #000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);"
                    + " -fx-text-fill: red; -fx-font-size: 15px; -fx-font-weight: bold; "
                    + "-fx-background-radius: 0,0,0,0; -fx-background-insets: 0,0,0,0;");
            button.setText("?");
    	}
    	marked = !marked;
    }
    
    public abstract void draw();

    public boolean getMarked() {
    	return marked;
    }
    
    public ArrayList<Cell> getNeighbours() {
    	return neighbours;
    }
    
    protected int numberOfNeighbouringMines() {
    	int numOfNeighbours=0;
    	for (Cell myNeighbour: neighbours) {
    		String[] className = myNeighbour.getClass().getName().split("\\.");
    		if(className[className.length-1].equals("Mine")) {
    			numOfNeighbours++;
    		}
    	}
		return numOfNeighbours;
    }

	public void setCoordinates(int[] coordinates) {
		this.coordinates = coordinates;
	}
}
