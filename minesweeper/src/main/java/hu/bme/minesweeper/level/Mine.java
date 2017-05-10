package hu.bme.minesweeper.level;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Mine extends Cell {
    public int step() {
        return -1;
    }

	@Override
	public void draw() {
		Image mineImage = new Image("images/flower2.png");
		ImageView mineImageView = new ImageView(mineImage);
	    mineImageView.setFitWidth(15);
	    mineImageView.setPreserveRatio(true);
	    mineImageView.setSmooth(true);
	    mineImageView.setCache(true);
	    button.setPadding(new Insets(5));
	    button.setGraphic(mineImageView);
	}
}
