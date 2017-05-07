package hu.bme.minesweeper.gui;

import java.util.*;

import javafx.application.*;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import hu.bme.minesweeper.level.Board;

/**
 * többjátékosnál kijelölni, hogy épp ki aktív
 * animáció: egymás után jelenjenek meg a bombák
 */

public class GUI extends Application {
    Control c;
    Board board = new Board();

    Button[][] boardTiles;
    MenuItem menuItemNewGame, menuItemHighScores, menuItemMode, menuItemExit;
    MenuItem menuItemStartServer, menuItemStartClient;
    Menu menuDifficulty, menuStart;
    MenuBar menuBar;
    GridPane gridPane;
    BorderPane borderPane;
    Stage stage;

    Label timeElapsedLabel;
    Label flagsLeftLabel;
    String difficulty;
    int numOfFlagsLeft; //ez nem kell, a Board.mineLeft eleme
    StringProperty message;
    boolean hasLostTheGame; //erre majd vigyázni kell, hogy új játék kezdésekor vissza kell állítani false-ra!
    boolean hasWonTheGame;
    int bombRowIndex, bombColIndex;
    short revealedBlocks;

    Image mineImage;
    ImageView[] bombImageViews;
    ImageView mineImageView;

    String timeElapsed = "00:00";

    Timer timer;


    public static void main(String[] args) {
        launch(args); //start() meghívódik
    }

    private void showTable() {
        HighScoresTestDrive.showTable(-1, null);
    }

    //felugró ablakhoz megjelenítése
    private String showDialog(String title, String text, String[] buttons) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);


        ButtonType buttonTypeYes = new ButtonType(buttons[0]);
        ButtonType buttonTypeNo = new ButtonType(buttons[1]);
        //itt többet is fel lehet sorolni

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonTypeYes) {
            return buttons[0];
        } else {
            return buttons[1];
        }

    }

    /*létrehoz kér egy új board-ot, és megjeleníti az új mezõket a táblán*/
    private void startNewGame(String difficulty) {
        timer = new Timer();
        revealedBlocks = 0;
        hasLostTheGame = false;
        hasWonTheGame = false;
        board.createBoards(difficulty);
        boardTiles = null;
        boardTiles = new Button[board.getBoardHeight()][board.getBoardWidth()];

        numOfFlagsLeft = board.getNumOfMines();
        message.set(Integer.toString(numOfFlagsLeft));

        gridPane = null;
        gridPane = new GridPane();
        gridPane.setHgap(1);
        gridPane.setVgap(1);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
        for (int i = 0; i < board.getBoardHeight(); i++) {
            for (int j = 0; j < board.getBoardWidth(); j++) {
                boardTiles[i][j] = new Button();
                boardTiles[i][j].setDisable(false);
                boardTiles[i][j].setId(i + "-" + j);
                boardTiles[i][j].setStyle("-fx-background-color: #000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);"
                        + " -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;"
                        + " -fx-background-radius: 0,0,0,0; -fx-background-insets: 0,0,0,0;");
                boardTiles[i][j].setMinWidth(30);
                boardTiles[i][j].setMinHeight(30);
                boardTiles[i][j].setOnMouseClicked(new ButtonClickedHandler());
                gridPane.add(boardTiles[i][j], j, i);
            }
        }
        stage.sizeToScene();
    }


    @Override
    public void start(Stage primaryStage) {

        //inicializálás
        difficulty = "easy";
        board.createBoards(difficulty);

        mineImage = new Image("images/flower2.png");
        ImageView mineImageView = new ImageView();
        mineImageView.setImage(mineImage);
        mineImageView.setFitWidth(12);
        mineImageView.setPreserveRatio(true);
        mineImageView.setSmooth(true);
        mineImageView.setCache(true);

		/*creating multiplayer layout: left side and right side*/
        Label serverSidePlayer = new Label("Szerver oldali játékos");
        Label clientSidePlayer = new Label("Kliens oldali játékos");
        ProgressBar serverPB = new ProgressBar();
        ProgressBar clientPB = new ProgressBar();
        serverPB.setProgress(0.6666);
        clientPB.setProgress(0);
        Label serverMinesFoundLabel = new Label(" " + "2");
        Label clientMinesFoundLabel = new Label(" " + "2");
        serverMinesFoundLabel.setStyle("-fx-font-weight: bold");
        clientMinesFoundLabel.setStyle("-fx-font-weight: bold");
        Image serverImage = new Image("images/serverPerson.png");
        Image clientImage = new Image("images/clientPerson.png");
        ImageView serverImageView = new ImageView(serverImage);
        serverImageView.setFitWidth(100);
        serverImageView.setPreserveRatio(true);
        ImageView clientImageView = new ImageView(clientImage);
        clientImageView.setFitWidth(100);
        clientImageView.setPreserveRatio(true);

        ImageView mineImageViewServer = new ImageView(mineImage);
        mineImageViewServer.setFitWidth(15);
        mineImageViewServer.setPreserveRatio(true);

        ImageView mineImageViewClient = new ImageView(mineImage);
        mineImageViewClient.setFitWidth(15);
        mineImageViewClient.setPreserveRatio(true);

        VBox serverImageVBox = new VBox(serverImageView, serverSidePlayer);
        VBox clientImageVBox = new VBox(clientImageView, clientSidePlayer);

        HBox serverMineFound = new HBox(mineImageViewServer, serverMinesFoundLabel);
        HBox clientMineFound = new HBox(mineImageViewClient, clientMinesFoundLabel);

        VBox serverVBox = new VBox();
        serverVBox.setPadding(new Insets(30));
        serverVBox.setSpacing(20);
        serverVBox.getChildren().addAll(serverImageVBox, serverPB, serverMineFound);

        VBox clientVBox = new VBox();
        clientVBox.setPadding(new Insets(30));
        clientVBox.setSpacing(20);
        clientVBox.getChildren().addAll(clientImageVBox, clientPB, clientMineFound);
		/*end of: creating multiplayer layout: left side and right side*/

		/*menuStart: a multiplayer mód szerver és kliens indításának menüje*/
        menuStart = new Menu("Start");

        menuItemStartServer = new MenuItem("Szerver indítása");
        menuItemStartServer.setOnAction(e ->
        {
            //eltárolni, hogy én vagyok a szerver
            System.out.println("szerver vagyok");
            //szerver indítása
        });
        menuItemStartClient = new MenuItem("Kliens indítása");
        menuItemStartClient.setOnAction(e ->
        {
            //eltárolni, hogy én vagyok a kliens
            System.out.println("kliens vagyok");
            //kliens indítása
        });

        menuStart.getItems().addAll(menuItemStartServer, menuItemStartClient);
		/*end of: menuStart: a multiplayer mód szerver és kliens indításának menüje*/

        stage = primaryStage;

        Label timeElapsedLabel = new Label(timeElapsed);
		/*a numOfFlagsLeft változó hozzákötése a minesLeftLabel-hez*/
		/*ez a frissítõs izé pont csak az eltelt idõnek kell majd*/
        Label flagsLeftLabel = new Label(Integer.toString(numOfFlagsLeft));
        message = new SimpleStringProperty();
        flagsLeftLabel.textProperty().bind(message);
        message.set(Integer.toString(numOfFlagsLeft));


        startNewGame("easy");

        //hBox contains the south of the scene: the elapsed time and the number of remaining flags
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        Region spacer = new Region(); //hogy bal, ill. jobb szélre kerüljenek az eltelt idõ és a megmaradt flagek
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hBox.getChildren().addAll(timeElapsedLabel, spacer, flagsLeftLabel);

		/*menü kialakítása*/
        menuBar = new MenuBar();
        Menu menuGame = new Menu("Játék");
        Menu menuHelp = new Menu("Súgó");

        //create the menuItems and add listeners
        menuItemNewGame = new MenuItem("Új játék");
        menuItemNewGame.setOnAction(e -> {
            startNewGame(difficulty);
            borderPane.setCenter(gridPane);
            stage.sizeToScene();
        });

        menuItemHighScores = new MenuItem("Legjobb eredmények");
        menuItemHighScores.setOnAction(e -> {
            showTable();
        });

        RadioMenuItem menuItemEasy = new RadioMenuItem("Könnyû");
        RadioMenuItem menuItemMedium = new RadioMenuItem("Közepes");
        RadioMenuItem menuItemHard = new RadioMenuItem("Nehéz");
        ToggleGroup difficultyGroup = new ToggleGroup();
        menuItemEasy.setToggleGroup(difficultyGroup);
        menuItemMedium.setToggleGroup(difficultyGroup);
        menuItemHard.setToggleGroup(difficultyGroup);

        menuItemEasy.setSelected(true);

        menuDifficulty = new Menu("Nehézségi szint");
        menuDifficulty.getItems().addAll(menuItemEasy, menuItemMedium, menuItemHard);

        menuDifficulty.setOnAction(e ->
        {
            if ((menuItemEasy.isSelected()) && (!Objects.equals(difficulty, "easy"))) {
                difficulty = "easy";
                startNewGame("easy");
                borderPane.setCenter(gridPane);
                stage.sizeToScene();
            }
            if ((menuItemMedium.isSelected()) && (!Objects.equals(difficulty, "medium"))) {
                difficulty = "medium";
                startNewGame("medium");
                borderPane.setCenter(gridPane);
                stage.sizeToScene();
            }
            if ((menuItemHard.isSelected()) && (!Objects.equals(difficulty, "hard"))) {
                difficulty = "hard";
                startNewGame("hard");
                borderPane.setCenter(gridPane);
                stage.sizeToScene();
            }
        });


        menuItemMode = new MenuItem("Váltás többjátékos módra");
        menuItemMode.setOnAction(new MenuItemHandler());

        menuItemExit = new MenuItem("Kilépés");
        menuItemExit.setOnAction(new MenuItemHandler());

        menuGame.getItems().addAll(menuItemNewGame, menuItemHighScores, menuDifficulty, menuItemMode, new SeparatorMenuItem(), menuItemExit);
        menuBar.getMenus().addAll(menuGame);

		/*egyjátékos mód layoutja*/
        borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setCenter(gridPane);
        borderPane.setBottom(hBox);

        Scene scene = new Scene(borderPane);

		/*a menügomb hatására váltás az egyjátékos- és a többjátékos mód layoutja között*/
        menuItemMode.setOnAction(e ->
        {
            if (Objects.equals(menuItemMode.getText(), "Váltás egyjátékos módra")) {
                menuItemMode.setText("Váltás többjátékos módra");
                menuBar.getMenus().remove(menuStart);
                borderPane.setLeft(null);
                borderPane.setRight(null);
                borderPane.setBottom(hBox);
            } else if (Objects.equals(menuItemMode.getText(), "Váltás többjátékos módra")) {
                menuItemMode.setText("Váltás egyjátékos módra");
                menuBar.getMenus().addAll(menuStart);
                borderPane.setLeft(serverVBox);
                borderPane.setRight(clientVBox);
                borderPane.setBottom(null);
            }
            stage.sizeToScene();
        });

        borderPane.setStyle("-fx-background-image: url(\"images/orange_background2.png\"); -fx-background-position: center center; -fx-background-repeat: stretch;");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Aknakeresõ");
        primaryStage.show();

        stage.setOnCloseRequest(e -> {
            if (!hasLostTheGame && (!hasWonTheGame)) {
                String[] options = {"Igen", "Nem"};
                String choice = showDialog("Kilépési szándék megerõsításe", "Biztos kilépsz?", options);
                if (Objects.equals(choice, "Igen")) {
                    stage.close();
                }
                if (Objects.equals(choice, "Nem")) {
                    e.consume();
                }
            }
        });

    }

    private boolean inBounds(int rowIndex, int colIndex, int rowUpperBound, int colUpperBound) {
        return (rowIndex >= 0) && (colIndex >= 0) && (rowIndex < rowUpperBound) && (colIndex < colUpperBound);
    }

    private void revealBlock(int rowIndex, int colIndex) {
        revealedBlocks++;
        int listIndex = rowIndex * board.getBoardWidth() + colIndex;

        if (board.cells.get(listIndex).step() != 0)
            boardTiles[rowIndex][colIndex].setText("" + board.cells.get(listIndex).step());
        boardTiles[rowIndex][colIndex].setDisable(true);
        if (board.cells.get(listIndex).step() == 0) {
            if (inBounds(rowIndex - 1, colIndex, board.getBoardHeight(), board.getBoardWidth()) && (!boardTiles[rowIndex - 1][colIndex].isDisabled()))
                revealBlock(rowIndex - 1, colIndex);
            if (inBounds(rowIndex, colIndex + 1, board.getBoardHeight(), board.getBoardWidth()) && (!boardTiles[rowIndex][colIndex + 1].isDisabled()))
                revealBlock(rowIndex, colIndex + 1);
            if (inBounds(rowIndex + 1, colIndex, board.getBoardHeight(), board.getBoardWidth()) && (!boardTiles[rowIndex + 1][colIndex].isDisabled()))
                revealBlock(rowIndex + 1, colIndex);
            if (inBounds(rowIndex, colIndex - 1, board.getBoardHeight(), board.getBoardWidth()) && (!boardTiles[rowIndex][colIndex - 1].isDisabled()))
                revealBlock(rowIndex, colIndex - 1);

            if (inBounds(rowIndex - 1, colIndex - 1, board.getBoardHeight(), board.getBoardWidth()) && (!boardTiles[rowIndex - 1][colIndex - 1].isDisabled()))
                revealBlock(rowIndex - 1, colIndex - 1);
            if (inBounds(rowIndex - 1, colIndex + 1, board.getBoardHeight(), board.getBoardWidth()) && (!boardTiles[rowIndex - 1][colIndex + 1].isDisabled()))
                revealBlock(rowIndex - 1, colIndex + 1);
            if (inBounds(rowIndex + 1, colIndex + 1, board.getBoardHeight(), board.getBoardWidth()) && (!boardTiles[rowIndex + 1][colIndex + 1].isDisabled()))
                revealBlock(rowIndex + 1, colIndex + 1);
            if (inBounds(rowIndex + 1, colIndex - 1, board.getBoardHeight(), board.getBoardWidth()) && (!boardTiles[rowIndex + 1][colIndex - 1].isDisabled()))
                revealBlock(rowIndex + 1, colIndex - 1);
        }
    }

    //ha rákattintottunk valamelyik cellára
    private class ButtonClickedHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            //melyik egérgombbal kattintottunk a cellára?
            boolean buttonType = false; // false = left click; true = right click;
            if (event.getButton() == MouseButton.SECONDARY) {
                buttonType = true;
            }
            //melyik cellára kattintottunk?
            String coordsString = ((Button) event.getSource()).getId();
            String[] coords = coordsString.split("-");
            int i = Integer.parseInt(coords[0]);
            int j = Integer.parseInt(coords[1]);

            if (!boardTiles[i][j].isDisable() && (!hasLostTheGame) && (!hasWonTheGame)) {

                if (buttonType) { //ha jobb gombbal kattintottunk még nem felfedett mezõre
                    //ha már van ott kérdõjel, vegyük le
                    if (boardTiles[i][j].getText() == "?") {
                        numOfFlagsLeft++;
                        boardTiles[i][j].setStyle("-fx-background-color: #000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);"
                                + " -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; "
                                + "-fx-background-radius: 0,0,0,0; -fx-background-insets: 0,0,0,0;");
                        boardTiles[i][j].setText("");
                    } else if (numOfFlagsLeft > 0) { //egyébként írjunk rá egy kérdõjelet
                        numOfFlagsLeft--;
                        boardTiles[i][j].setStyle("-fx-background-color: #000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);"
                                + " -fx-text-fill: red; -fx-font-size: 15px; -fx-font-weight: bold; "
                                + "-fx-background-radius: 0,0,0,0; -fx-background-insets: 0,0,0,0;");
                        boardTiles[i][j].setText("?");
                    }
                    message.set(Integer.toString(numOfFlagsLeft)); //lehet ez így fölösleges propertyvel
                    //igen, az, de bennehagyom; majd ilyen lesz jó az idõhöz, mert az folyton változik,
                    //és folyton frissíteni kell
                } else {
                    if (!Objects.equals(boardTiles[i][j].getText(), "?")) { //ha kérdõjel van már ott, nem nyúlunk semmihez
                        //ha normális kattintás történt bal egérgombbal
                        //ha az elsõ katt történt, indítsd el a timert
                        if (revealedBlocks == 0) {
                            timer.setTimeFirstClick();
                        }

                        if (!board.minesMatrix[i][j]) {
                            boardTiles[i][j].setDisable(true);
                        }

                        if (board.minesMatrix[i][j]) {
                            hasLostTheGame = true;
                            bombImageViews = new ImageView[board.getNumOfMines()]; //csak akkora tömböt, amekkora tényleg kell
                            //csak hogy indexelek benne? bombImageViewsIndex++

                            int bombImageViewsIndex = 0;
                            for (int k = 0; k < board.getNumOfMines(); k++) {
                                bombImageViews[k] = new ImageView(mineImage);
                                bombImageViews[k].setFitWidth(15);
                                bombImageViews[k].setPreserveRatio(true);
                                bombImageViews[k].setSmooth(true);
                                bombImageViews[k].setCache(true);
                            }
                            bombImageViewsIndex = 0; //!
                            for (int rowIndex = 0; rowIndex < board.getBoardHeight(); rowIndex++) {
                                for (int colIndex = 0; colIndex < board.getBoardWidth(); colIndex++) {

                                    if (board.minesMatrix[rowIndex][colIndex]) {
                                        if (Objects.equals(boardTiles[rowIndex][colIndex].getText(), "?")) {
                                            boardTiles[rowIndex][colIndex].setText("");
                                        }

                                        boardTiles[rowIndex][colIndex].setPadding(new Insets(5));

                                        boardTiles[rowIndex][colIndex].setGraphic(bombImageViews[bombImageViewsIndex]);
                                        bombImageViewsIndex++;
                                    }
                                }
                            }
                            //várni kellene kicsit, mielõtt felugrik az ablak
                            String[] options = {"Igen", "Nem"};
                            String choice = showDialog("Vesztettél.", "Sajnos vesztettél.\nSzeretnél új játékot kezdeni?", options);
                            if (Objects.equals(choice, "Igen")) {
                                startNewGame(difficulty);
                                borderPane.setCenter(gridPane);
                                stage.sizeToScene();
                            }


                        } else {

                            revealBlock(i, j);
                            if ((board.getBoardWidth() * board.getBoardHeight() - revealedBlocks) == board.getNumOfMines()) {
                                timer.setTimeElapsed();
                                timeElapsed = timer.getTimeElapsed();

                                String[] minSec = timeElapsed.split(":");
                                timeElapsed = minSec[1] + ":" + minSec[2];
                                hasWonTheGame = true;
                                if (!HighScoresTestDrive.isNewHighScore(timeElapsed, difficulty)) {
                                    //nyert, de nem került be a legjobbak közé
                                    String[] options = {"Igen", "Nem"};
                                    String choice = showDialog("Nyertél!", "Gratulálunk! Nyertél.\n"
                                            + "Szeretnél új játékot kezdeni?", options);
                                    if (choice == "Igen") {
                                        startNewGame(difficulty);
                                        borderPane.setCenter(gridPane);
                                        stage.sizeToScene();
                                    } else {
                                        //nem szeretnék új játékot kezdeni
                                    }
                                } else {
                                    //bekerült a legjobbak közé

                                    //itt el lehetne tárolni a legutóbbi megadott nevet
                                    TextInputDialog dialog = new TextInputDialog();
                                    dialog.setTitle("Toplista");
                                    dialog.setHeaderText("Gratulálunk! Felkerültél a toplistára.");
                                    dialog.setGraphic(null);
                                    dialog.setContentText("Add meg a neved:");

                                    // Traditional way to get the response value.
                                    Optional<String> result = dialog.showAndWait();
                                    if (result.isPresent()) {
                                        int place = HighScoresTestDrive.insertData(new HighScores(result.get(), timeElapsed), difficulty);
                                        HighScoresTestDrive.showTable(place - 1, difficulty);
                                    }
                                }
                            }
                        }
                    } //end_ha nem volt ott már kérdõjel eleve
                } //end_if/else (buttonType)
            }
        }

    }

    private class MenuItemHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (event.getSource() == menuItemMode) {
                if (Objects.equals(menuItemMode.getText(), "Váltás egyjátékos módra")) {
                    menuItemMode.setText("Váltás többjátékos módra");
                    menuBar.getMenus().remove(menuStart);
                } else if (Objects.equals(menuItemMode.getText(), "Váltás többjátékos módra")) {
                    menuItemMode.setText("Váltás egyjátékos módra");
                    menuBar.getMenus().addAll(menuStart);
                }
            }
            if (event.getSource() == menuItemExit) {
                if ((!hasLostTheGame) && (!hasWonTheGame)) {
                    String[] options = {"Igen", "Nem"};
                    String choice = showDialog("Kilépés", "Biztos kilépsz?", options);
                    if (Objects.equals(choice, "Igen")) {
                        stage.close();
                    }
                }
            }
        }


    }

}

