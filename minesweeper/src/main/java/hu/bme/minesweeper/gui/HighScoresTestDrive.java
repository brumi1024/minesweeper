package hu.bme.minesweeper.gui;


import hu.bme.minesweeper.datamodel.DatabaseConnection;
import hu.bme.minesweeper.datamodel.HighScores;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HighScoresTestDrive is the class responsible for displaying
 * the database containing the high scores, and updating it's
 * content.
 */
public class HighScoresTestDrive extends Application {
    private final static Logger LOGGER = Logger.getLogger(HighScoresTestDrive.class.getName());

    /**
     * Main method.
     */
    public static void main(String[] args) throws FileNotFoundException {
        launch(args);
    }

    /**
     * Creates a table containing columns for the number of the high
     * scores, tha player names and the time. The height is set to
     * display 8 high scores.
     *
     * @param difficulty the difficulty level of the results displayed
     *                   in this table
     * @return the created table
     */
    private static TableView<HighScores> createTable(String difficulty) {
        TableView<HighScores> table = new TableView<HighScores>();
        table.setItems(loadData(difficulty));
        table.setPrefHeight(218);
        table.setPrefWidth(200);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn numberCol = new TableColumn("#");
        numberCol.setCellValueFactory(new Callback<CellDataFeatures<HighScores, HighScores>, ObservableValue<HighScores>>() {
            @Override
            public ObservableValue<HighScores> call(CellDataFeatures<HighScores, HighScores> p) {
                return new ReadOnlyObjectWrapper(p.getValue());
            }
        });

        numberCol.setCellFactory(new Callback<TableColumn<HighScores, HighScores>, TableCell<HighScores, HighScores>>() {
            @Override
            public TableCell<HighScores, HighScores> call(TableColumn<HighScores, HighScores> param) {
                return new TableCell<HighScores, HighScores>() {
                    @Override
                    protected void updateItem(HighScores item, boolean empty) {
                        super.updateItem(item, empty);

                        if (this.getTableRow() != null && item != null) {
                            setText((this.getTableRow().getIndex() + 1) + "");
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });

        TableColumn<HighScores, String> colName = new TableColumn<>();

        if (Objects.equals(difficulty, "easy")) {
            colName.setText("Easy");
        } else if (Objects.equals(difficulty, "medium")) {
            colName.setText("Medium");
        } else {
            colName.setText("Hard");
        }

        colName.setMinWidth(100);
        colName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        colName.setSortable(false);

        TableColumn<HighScores, String> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(new PropertyValueFactory<>("Time"));
        colTime.setSortable(false);

        table.getColumns().addAll(numberCol, colName, colTime);

        return table;
    }

    /**
     * Displays three tables for the three difficulty levels
     * in a popup window.
     *
     * @param place      the place of the last result, so that the player
     *                   can see his/her results focused on
     * @param difficulty the difficulty level of the last result
     */
    static void showTable(int place, String difficulty) {

        TableView easyTable = createTable("easy");
        TableView mediumTable = createTable("medium");
        TableView hardTable = createTable("hard");

        if (place > -1) {
            if (Objects.equals(difficulty, "easy")) {
                easyTable.requestFocus();
                easyTable.getSelectionModel().select(place);
                easyTable.getFocusModel().focus(place);
            } else if (Objects.equals(difficulty, "medium")) {
                mediumTable.requestFocus();
                mediumTable.getSelectionModel().select(place);
                mediumTable.getFocusModel().focus(place);
            } else if (Objects.equals(difficulty, "hard")) {
                hardTable.requestFocus();
                hardTable.getSelectionModel().select(place);
                hardTable.getFocusModel().focus(place);
            }
        }


        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.getChildren().addAll(easyTable, mediumTable, hardTable);

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("High Scores");
        alert.setHeaderText(null);

        DialogPane dialogPane = alert.getDialogPane();

        dialogPane.setHeader(hBox);

        ButtonType buttonTypeBack = new ButtonType("Back");
        alert.getButtonTypes().setAll(buttonTypeBack);

        alert.showAndWait();
    }

    /**
     * Loads the data from the database to an <code>ObservableList</code>.
     *
     * @param difficulty the difficulty level
     * @return the data loaded to an <code>ObservableList</code>
     */
    private static ObservableList<HighScores> loadData(String difficulty) {
        ObservableList<HighScores> data = FXCollections.observableArrayList();
        try {
            if (DatabaseConnection.isConnected()) {
                data.setAll(DatabaseConnection.readAll(difficulty));
            }
        } catch (SQLException s) {
            LOGGER.log(Level.SEVERE, "Could read from DB. {0}", s.toString());
        }

        return data;
    }

    /**
     * Checks whether the database contains a certain time result with
     * the given difficulty level.
     *
     * @param timeElapsed the elapsed time
     * @param difficulty  the difficulty level of the played game
     *                    of the destination rectangle in pixels
     * @return <code>true</code> if the result is on the list;
     * <code>false</code> otherwise.
     */
    static boolean isOnTheList(int timeElapsed, String difficulty) {
        try {
            return DatabaseConnection.readCount(difficulty) < 8 ||
                    timeElapsed < DatabaseConnection.readLastTime(difficulty);
        } catch (SQLException s) {
            LOGGER.log(Level.SEVERE, "Could not read from DB. {0}", s.toString());
            return false;
        }
    }

    /**
     * It places the new result in the database according to the difficulty
     * and the elapsed time. It checks whether it's a new high scores, and
     * indicates the reached place.
     *
     * @param newResult the new Result made by the player
     * @return -1 if the time result wasn't a new high score;
     * place in the high scores list otherwise
     */
    static int insertData(HighScores newResult) {
        ObservableList<HighScores> data = loadData(newResult.getDifficulty());

        data.add(newResult);

        if (data.size() > 8) {
            data.remove(data.size() - 1);
        }
        try {
            if (DatabaseConnection.readCount(newResult.getDifficulty()) > 7) {
                DatabaseConnection.deleteLastOne(newResult.getDifficulty());
            }
            DatabaseConnection.writeOne(newResult);
        } catch (SQLException s) {
            LOGGER.log(Level.SEVERE, "Could not write to DB. {0}", s.toString());
        }
        return (data.lastIndexOf(newResult) + 1);

    }

    /**
     * This method doesn't need to do anything.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
