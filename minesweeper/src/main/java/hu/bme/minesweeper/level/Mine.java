package hu.bme.minesweeper.level;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Mine extends Cell {
	
	private ImageView mineImageView;
	
	Mine() {
		Image mineImage = new Image("images/flower2.png");
		mineImageView = new ImageView(mineImage);
	    mineImageView.setFitWidth(15);
	    mineImageView.setPreserveRatio(true);
	    mineImageView.setCache(true);
	}
	
    public int step() {
        return -1;
    }

	@Override
	public void draw() {
	    button.setPadding(new Insets(5));
	    button.setGraphic(mineImageView);
	}
}
