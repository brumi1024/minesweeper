package hu.bme.minesweeper.level;

import javafx.scene.control.Button;

public class Nothing extends Cell {
    public int step() {
        return numberOfNeighbouringMines();
    }

	@Override
	public void draw() {
		//disable a button-t
		//ha nem 0 szomszedja van, rajzold ra, hogy mennyi van neki;
		//egyebkent ne rajzolj ra semmit
		button.setDisable(true);
		if(step()>0) {
			button.setText("" + step());
		}
	}
}
