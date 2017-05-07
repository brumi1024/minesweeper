package hu.bme.minesweeper.gui;

/*((ki lehetne menteni az utolsó játékot))*/

/*legyen a high scores-nál a táblázat egy felugró ablak, aminek egyetlen gombja van, "Vissza"**/

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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.util.Comparator;
import java.util.Objects;

public class HighScoresTestDrive extends Application {


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

        TableColumn<HighScores, String> colTitle = new TableColumn();

        if (difficulty == "easy") {
            colTitle.setText("Könnyû");
        } else if (difficulty == "medium") {
            colTitle.setText("Közepes");
        } else {
            colTitle.setText("Nehéz");
        }

        colTitle.setMinWidth(100);
        colTitle.setCellValueFactory(new PropertyValueFactory<HighScores, String>("Name"));
        colTitle.setSortable(false);

        TableColumn<HighScores, String> colYear = new TableColumn("Idõ");
        //colYear.setMinWidth(50);
        colYear.setCellValueFactory(new PropertyValueFactory<HighScores, String>("Time"));
        colYear.setSortable(false);

        table.getColumns().addAll(numberCol, colTitle, colYear);

        return table;
    }

    static void showTable(int place, String difficulty) {
        Label easyLabel = new Label("Könnyû");
        easyLabel.setFont(new Font("Arial", 14));
        Label mediumLabel = new Label("Közepes");
        mediumLabel.setFont(new Font("Arial", 14));
        Label hardLabel = new Label("Nehéz");
        hardLabel.setFont(new Font("Arial", 14));

        HBox labelBox = new HBox(easyLabel, mediumLabel, hardLabel);

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
        alert.setTitle("Legjobb eredmények");
        alert.setHeaderText(null);

        DialogPane dialogPane = alert.getDialogPane();

        VBox container = new VBox(labelBox, hBox);
        dialogPane.setHeader(hBox);

        ButtonType buttonTypeBack = new ButtonType("Vissza");
        alert.getButtonTypes().setAll(buttonTypeBack);

        alert.showAndWait();
    }


    private static ObservableList<HighScores> loadData(String difficulty) {
        ObservableList<HighScores> data = FXCollections.observableArrayList();
        try {
            File file = checkDifficulty(difficulty);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] items = line.split(","); //meg nem engedett karakter kell legyen majd a név beolvasásánál!!!
                data.add(new HighScores(items[0], items[1]));
            }
            bufferedReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    static boolean isNewHighScore(String timeElapsed, String difficulty) {
        ObservableList<HighScores> data = loadData(difficulty);
        return compareTimes(timeElapsed, data.get(data.size() - 1).getTime()) < 0;
    }

    static int insertData(HighScores newResult, String difficulty) {
        //ha nem került fel a listára, return -1,
        //ha felkerült, frissítjük a legjobb eredményeket tartalmazó fájlt,
        //és return, hogy hanyadik helyezés lett
        //ezt csak akkor hívjuk meg, ha bekerült a legjobbak közé
        ObservableList<HighScores> data = loadData(difficulty);

        data.add(newResult);

        Comparator<HighScores> highScoresComparator = new Comparator<HighScores>() {
            @Override
            public int compare(HighScores hS1, HighScores hS2) {
                return compareTimes(hS1.getTime(), hS2.getTime());
            }
        };
        FXCollections.sort(data, highScoresComparator);

        if (data.size() > 8) {
            data.remove(data.size() - 1);
        }

        try {
            File file = checkDifficulty(difficulty);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            String line;
            for (HighScores highScoreItem : data) {
                line = highScoreItem.getName() + "," + highScoreItem.getTime();
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (data.lastIndexOf(newResult) + 1);

    }

    private static int compareTimes(String time1, String time2) {
        //return -1, ha time1<time2
        //return 1, ha time1>time2
        //return 0, ha a két idõ megegyezik
        String[] timeMinSec1 = time1.split(":");
        String[] timeMinSec2 = time2.split(":");

        int min1 = Integer.parseInt(timeMinSec1[0]);
        int sec1 = Integer.parseInt(timeMinSec1[1]);
        int min2 = Integer.parseInt(timeMinSec2[0]);
        int sec2 = Integer.parseInt(timeMinSec2[1]);

        if (min1 > min2) return 1;
        if (min1 < min2) return -1;
        if (sec1 > sec2) return 1;
        if (sec1 < sec2) return -1;
        return 0;
    }

    private static File checkDifficulty(String difficulty) throws NullPointerException {
        ClassLoader loader = GUI.class.getClassLoader();
        switch (difficulty) {
            case "easy":
                return new File(loader.getResource("highScoresEasy.txt").getFile());
            case "medium":
                return new File(loader.getResource("highScoresMedium.txt").getFile());
            case "hard":
                return new File(loader.getResource("highScoresHard.txt").getFile());
            default:
                return new File(loader.getResource("highScoresEasy.txt").getFile());
        }
    }
}
