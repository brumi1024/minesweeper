package hu.bme.minesweeper.level;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Mine extends Cell {

    /**
     * ImageView of the mine.
     */
    private ImageView mineImageView;

    /**
     * Create a new Mine and initialize the ImageView.
     */
    Mine() {
        Image mineImage = new Image("images/flower2.png");
        mineImageView = new ImageView(mineImage);
        mineImageView.setFitWidth(15);
        mineImageView.setPreserveRatio(true);
        mineImageView.setCache(true);
    }

    /**
     * Handles the step on a Mine Cell.
     *
     * @return -1
     */
    public int step() {
        return -1;
    }

    /**
     * Do the drawing tasks.
     */
    @Override
    public void draw() {
        button.setPadding(new Insets(5));
        button.setGraphic(mineImageView);
    }
}
