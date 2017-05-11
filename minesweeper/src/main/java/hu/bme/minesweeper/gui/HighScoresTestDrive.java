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

import java.io.*;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HighScoresTestDrive extends Application {
    private final static Logger LOGGER = Logger.getLogger(HighScoresTestDrive.class.getName());

    public static void main(String[] args) throws FileNotFoundException {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    private static TableView<HighScores> createTable(String difficulty) {
        TableView<HighScores> table = new TableView<HighScores>();
        table.setItems(loadData(difficulty));
        table.setPrefHeight(218);
        table.setPrefWidth(200);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn numberCol = new TableColumn("#");
        //numberCol.setMinWidth(20);
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

        TableColumn<HighScores, String> colName = new TableColumn();

        if (difficulty == "easy") {
            colName.setText("Easy");
        } else if (difficulty == "medium") {
            colName.setText("Medium");
        } else {
            colName.setText("Hard");
        }

        colName.setMinWidth(100);
        colName.setCellValueFactory(new PropertyValueFactory<HighScores, String>("Name"));
        colName.setSortable(false);

        TableColumn<HighScores, String> colTime = new TableColumn("Time");
        //colTime.setMinWidth(50);
        colTime.setCellValueFactory(new PropertyValueFactory<HighScores, String>("Time"));
        colTime.setSortable(false);

        table.getColumns().addAll(numberCol, colName, colTime);

        return table;
    }

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

    static boolean isNewHighScore(int timeElapsed, String difficulty) {
        try {
            int highScoreTime = DatabaseConnection.readLastValue(difficulty);

            return highScoreTime == 0 || timeElapsed < highScoreTime;
        } catch (SQLException s) {
            LOGGER.log(Level.SEVERE, "Could not read from DB. {0}", s.toString());
            return false;
        }
    }

    static int insertData(HighScores newResult) {
        //ha nem került fel a listára, return -1,
        //ha felkerült, frissítjük a legjobb eredményeket tartalmazó fájlt,
        //és return, hogy hanyadik helyezés lett
        //ezt csak akkor hívjuk meg, ha bekerült a legjobbak közé
        ObservableList<HighScores> data = loadData(newResult.getDifficulty());

        data.add(newResult);

        Comparator<HighScores> highScoresComparator = (hS1, hS2) -> hS1.getSec() > hS2.getSec() ? 1 : hS1.getTime() == hS2.getTime() ? 0 : -1;
        FXCollections.sort(data, highScoresComparator);

        if (data.size() > 8) {
            data.remove(data.size() - 1);
        }

        try {
            DatabaseConnection.writeOne(newResult);
        } catch (SQLException s) {
            LOGGER.log(Level.SEVERE, "Could not write to DB. {0}", s.toString());
        }
        return (data.lastIndexOf(newResult) + 1);

    }
}
