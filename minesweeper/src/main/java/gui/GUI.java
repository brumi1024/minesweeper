package gui;


/**
 * choose who is active in multiplayer
 * anim�ci�: egym�s ut�n jelenjenek meg a bomb�k
 */

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

public class GUI extends Application {
    Control c;
    private Board board = new Board();

    private Button[][] boardTiles;
    private MenuItem menuItemMode;
    private MenuItem menuItemExit;
    private Menu menuStart;
    private MenuBar menuBar;
    private GridPane gridPane;
    private BorderPane borderPane;
    private Stage stage;

    Label timeElapsedLabel;
    Label flagsLeftLabel;
    private String difficulty;
    private int numOfFlagsLeft; //ez nem kell, a Board.mineLeft eleme
    private StringProperty message;
    private boolean hasLostTheGame; //erre majd vigy�zni kell, hogy �j j�t�k kezd�sekor vissza kell �ll�tani false-ra!
    private boolean hasWonTheGame;
    int bombRowIndex, bombColIndex;
    private short revealedBlocks;

    private Image mineImage;
    private ImageView[] bombImageViews;
    ImageView mineImageView;

    private String timeElapsed = "01:05";

    private Timer timer;

    public static void main(String[] args) {
        launch(args); //start() megh�v�dik
    }

    private void showTable() {
        HighScoresTestDrive.showTable(-1, null);
    }

    //felugr� ablakhoz megjelen�t�se
    private String showDialog(String title, String text, String[] buttons) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);


        ButtonType buttonTypeYes = new ButtonType(buttons[0]);
        ButtonType buttonTypeNo = new ButtonType(buttons[1]);
        //itt t�bbet is fel lehet sorolni

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonTypeYes) {
            return buttons[0];
        } else {
            return buttons[1];
        }

    }

    /*l�trehoz k�r egy �j board-ot, �s megjelen�ti az �j mez�ket a t�bl�n*/
    private void startNewGame(String difficulty) {
        timer = new Timer();
        numOfFlagsLeft = board.numberOfMines;
        revealedBlocks = 0;
        hasLostTheGame = false;
        hasWonTheGame = false;
        board.generateNewBoard(difficulty); //konstruktor?
        boardTiles = null;
        boardTiles = new Button[board.height][board.width];

        numOfFlagsLeft = board.numberOfMines;

        gridPane = null;
        gridPane = new GridPane();
        gridPane.setHgap(1);
        gridPane.setVgap(1);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
        for (int i = 0; i < board.height; i++) {
            for (int j = 0; j < board.width; j++) {
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

        //inicializ�l�s
        difficulty = "easy";
        board.generateNewBoard("easy");

        mineImage = new Image("file:/C:/Users/Juh�sz%20Alexandra/workspace/MinesweeperAlexandra/bin/images/flower2.png");
        ImageView mineImageView = new ImageView();
        mineImageView.setImage(mineImage);
        mineImageView.setFitWidth(12);
        mineImageView.setPreserveRatio(true);
        mineImageView.setSmooth(true);
        mineImageView.setCache(true);

		/*creating multiplayer layout: left side and right side*/
        Label serverSidePlayer = new Label("Szerver oldali j�t�kos");
        Label clientSidePlayer = new Label("Kliens oldali j�t�kos");
        ProgressBar serverPB = new ProgressBar();
        ProgressBar clientPB = new ProgressBar();
        serverPB.setProgress(0.6666);
        clientPB.setProgress(0);
        Label serverMinesFoundLabel = new Label(" " + "2");
        Label clientMinesFoundLabel = new Label(" " + "2");
        serverMinesFoundLabel.setStyle("-fx-font-weight: bold");
        clientMinesFoundLabel.setStyle("-fx-font-weight: bold");
        Image serverImage = new Image("file:/C:/Users/Juh�sz%20Alexandra/workspace/MinesweeperAlexandra/bin/images/serverPerson.png");
        Image clientImage = new Image("file:/C:/Users/Juh�sz%20Alexandra/workspace/MinesweeperAlexandra/bin/images/clientPerson.png");
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
		
		/*menuStart: a multiplayer m�d szerver �s kliens ind�t�s�nak men�je*/
        menuStart = new Menu("Start");

        MenuItem menuItemStartServer = new MenuItem("Szerver ind�t�sa");
        menuItemStartServer.setOnAction(e ->
        {
            //elt�rolni, hogy �n vagyok a szerver
            System.out.println("szerver vagyok");
            //szerver ind�t�sa
        });
        MenuItem menuItemStartClient = new MenuItem("Kliens ind�t�sa");
        menuItemStartClient.setOnAction(e ->
        {
            //elt�rolni, hogy �n vagyok a kliens
            System.out.println("kliens vagyok");
            //kliens ind�t�sa
        });

        menuStart.getItems().addAll(menuItemStartServer, menuItemStartClient);
		/*end of: menuStart: a multiplayer m�d szerver �s kliens ind�t�s�nak men�je*/

        stage = primaryStage;

        Label timeElapsedLabel = new Label(timeElapsed);
		/*a numOfFlagsLeft v�ltoz� hozz�k�t�se a minesLeftLabel-hez*/
		/*ez a friss�t�s iz� pont csak az eltelt id�nek kell majd*/
        Label flagsLeftLabel = new Label(Integer.toString(numOfFlagsLeft));
        message = new SimpleStringProperty();
        flagsLeftLabel.textProperty().bind(message);
        message.set(Integer.toString(numOfFlagsLeft));


        startNewGame("easy");

        //hBox contains the south of the scene: the elapsed time and the number of remaining flags
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        Region spacer = new Region(); //hogy bal, ill. jobb sz�lre ker�ljenek az eltelt id� �s a megmaradt flagek
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hBox.getChildren().addAll(timeElapsedLabel, spacer, flagsLeftLabel);
		
		/*men� kialak�t�sa*/
        menuBar = new MenuBar();
        Menu menuGame = new Menu("J�t�k");
        Menu menuHelp = new Menu("S�g�");

        //create the menuItems and add listeners
        MenuItem menuItemNewGame = new MenuItem("�j j�t�k");
        menuItemNewGame.setOnAction(e -> {
            startNewGame(difficulty);
            borderPane.setCenter(gridPane);
            stage.sizeToScene();
        });

        MenuItem menuItemHighScores = new MenuItem("Legjobb eredm�nyek");
        menuItemHighScores.setOnAction(e -> {
            showTable();
        });

        RadioMenuItem menuItemEasy = new RadioMenuItem("K�nny�");
        RadioMenuItem menuItemMedium = new RadioMenuItem("K�zepes");
        RadioMenuItem menuItemHard = new RadioMenuItem("Neh�z");
        ToggleGroup difficultyGroup = new ToggleGroup();
        menuItemEasy.setToggleGroup(difficultyGroup);
        menuItemMedium.setToggleGroup(difficultyGroup);
        menuItemHard.setToggleGroup(difficultyGroup);

        menuItemEasy.setSelected(true);

        Menu menuDifficulty = new Menu("Neh�zs�gi szint");
        menuDifficulty.getItems().addAll(menuItemEasy, menuItemMedium, menuItemHard);

        menuDifficulty.setOnAction(e ->
        {
            if ((menuItemEasy.isSelected()) && (difficulty != "easy")) {
                difficulty = "easy";
                startNewGame("easy");
                borderPane.setCenter(gridPane);
                stage.sizeToScene();
            }
            if ((menuItemMedium.isSelected()) && (difficulty != "medium")) {
                difficulty = "medium";
                startNewGame("medium");
                borderPane.setCenter(gridPane);
                stage.sizeToScene();
            }
            if ((menuItemHard.isSelected()) && (difficulty != "hard")) {
                difficulty = "hard";
                startNewGame("hard");
                borderPane.setCenter(gridPane);
                stage.sizeToScene();
            }
        });


        menuItemMode = new MenuItem("V�lt�s t�bbj�t�kos m�dra");
        menuItemMode.setOnAction(new MenuItemHandler());

        menuItemExit = new MenuItem("Kil�p�s");
        menuItemExit.setOnAction(new MenuItemHandler());

        menuGame.getItems().addAll(menuItemNewGame, menuItemHighScores, menuDifficulty, menuItemMode, new SeparatorMenuItem(), menuItemExit);
        menuBar.getMenus().addAll(menuGame);
		
		/*egyj�t�kos m�d layoutja*/
        borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setCenter(gridPane);
        borderPane.setBottom(hBox);

        Scene scene = new Scene(borderPane);

		/*a men�gomb hat�s�ra v�lt�s az egyj�t�kos- �s a t�bbj�t�kos m�d layoutja k�z�tt*/
        menuItemMode.setOnAction(e ->
        {
            if (menuItemMode.getText() == "V�lt�s egyj�t�kos m�dra") {
                menuItemMode.setText("V�lt�s t�bbj�t�kos m�dra");
                menuBar.getMenus().remove(menuStart);
                borderPane.setLeft(null);
                borderPane.setRight(null);
                borderPane.setBottom(hBox);
            } else if (menuItemMode.getText() == "V�lt�s t�bbj�t�kos m�dra") {
                menuItemMode.setText("V�lt�s egyj�t�kos m�dra");
                menuBar.getMenus().addAll(menuStart);
                borderPane.setLeft(serverVBox);
                borderPane.setRight(clientVBox);
                borderPane.setBottom(null);
            }
            stage.sizeToScene();
        });

        borderPane.setStyle("-fx-background-image: url(\"file:/C:/Users/Juh�sz%20Alexandra/workspace/MinesweeperAlexandra/bin/images/orange_background2.jpg\"); -fx-background-position: center center; -fx-background-repeat: stretch;");


        primaryStage.setScene(scene);
        primaryStage.setTitle("Aknakeres�");
        primaryStage.show();

        stage.setOnCloseRequest(e -> {
            if (!hasLostTheGame && (!hasWonTheGame)) {
                String[] options = {"Igen", "Nem"};
                String choice = showDialog("Kil�p�si sz�nd�k meger�s�t�se", "Biztos kil�psz?", options);
                if (choice == "Igen") {
                    stage.close();
                }
                if (choice == "Nem") {
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
        if (board.numberOfNeighbours[rowIndex][colIndex] != 0)
            boardTiles[rowIndex][colIndex].setText("" + board.numberOfNeighbours[rowIndex][colIndex]);
        boardTiles[rowIndex][colIndex].setDisable(true);
        if (board.numberOfNeighbours[rowIndex][colIndex] == 0) {
            if (inBounds(rowIndex - 1, colIndex, board.height, board.width) && (boardTiles[rowIndex - 1][colIndex].isDisabled() == false))
                revealBlock(rowIndex - 1, colIndex);
            if (inBounds(rowIndex, colIndex + 1, board.height, board.width) && (boardTiles[rowIndex][colIndex + 1].isDisabled() == false))
                revealBlock(rowIndex, colIndex + 1);
            if (inBounds(rowIndex + 1, colIndex, board.height, board.width) && (boardTiles[rowIndex + 1][colIndex].isDisabled() == false))
                revealBlock(rowIndex + 1, colIndex);
            if (inBounds(rowIndex, colIndex - 1, board.height, board.width) && (boardTiles[rowIndex][colIndex - 1].isDisabled() == false))
                revealBlock(rowIndex, colIndex - 1);

            if (inBounds(rowIndex - 1, colIndex - 1, board.height, board.width) && (boardTiles[rowIndex - 1][colIndex - 1].isDisabled() == false))
                revealBlock(rowIndex - 1, colIndex - 1);
            if (inBounds(rowIndex - 1, colIndex + 1, board.height, board.width) && (boardTiles[rowIndex - 1][colIndex + 1].isDisabled() == false))
                revealBlock(rowIndex - 1, colIndex + 1);
            if (inBounds(rowIndex + 1, colIndex + 1, board.height, board.width) && (boardTiles[rowIndex + 1][colIndex + 1].isDisabled() == false))
                revealBlock(rowIndex + 1, colIndex + 1);
            if (inBounds(rowIndex + 1, colIndex - 1, board.height, board.width) && (boardTiles[rowIndex + 1][colIndex - 1].isDisabled() == false))
                revealBlock(rowIndex + 1, colIndex - 1);
        }
    }

    //ha r�kattintottunk valamelyik cell�ra
    private class ButtonClickedHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            //melyik eg�rgombbal kattintottunk a cell�ra?
            boolean buttonType = false; // false = left click; true = right click;
            if (event.getButton() == MouseButton.SECONDARY) {
                buttonType = true;
            }
            //melyik cell�ra kattintottunk?
            String coordsString = ((Button) event.getSource()).getId();
            String[] coords = coordsString.split("-");
            int i = Integer.parseInt(coords[0]);
            int j = Integer.parseInt(coords[1]);

            if (!boardTiles[i][j].isDisable() && (!hasLostTheGame) && (!hasWonTheGame)) {

                if (buttonType) { //ha jobb gombbal kattintottunk m�g nem felfedett mez�re
                    //ha m�r van ott k�rd�jel, vegy�k le
                    if (boardTiles[i][j].getText() == "?") {
                        numOfFlagsLeft++;
                        boardTiles[i][j].setStyle("-fx-background-color: #000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);"
                                + " -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; "
                                + "-fx-background-radius: 0,0,0,0; -fx-background-insets: 0,0,0,0;");
                        boardTiles[i][j].setText("");
                    } else if (numOfFlagsLeft > 0) { //egy�bk�nt �rjunk r� egy k�rd�jelet
                        numOfFlagsLeft--;
                        boardTiles[i][j].setStyle("-fx-background-color: #000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);"
                                + " -fx-text-fill: red; -fx-font-size: 15px; -fx-font-weight: bold; "
                                + "-fx-background-radius: 0,0,0,0; -fx-background-insets: 0,0,0,0;");
                        boardTiles[i][j].setText("?");
                    }
                    message.set(Integer.toString(numOfFlagsLeft)); //lehet ez �gy f�l�sleges propertyvel
                    //igen, az, de bennehagyom; majd ilyen lesz j� az id�h�z, mert az folyton v�ltozik,
                    //�s folyton friss�teni kell
                } else {
                    if (boardTiles[i][j].getText() != "?") { //ha k�rd�jel van m�r ott, nem ny�lunk semmihez
                        //ha norm�lis kattint�s t�rt�nt bal eg�rgombbal
                        //ha az els� katt t�rt�nt, ind�tsd el a timert
                        if (revealedBlocks == 0) {
                            System.out.println("most kellene ind�tani a timert");
                            timer.setTimeFirstClick();
                        }

                        if (board.isMine[i][j] != 1) {
                            boardTiles[i][j].setDisable(true);
                        }

                        if (board.isMine[i][j] == 1) {
                            hasLostTheGame = true;
                            bombImageViews = new ImageView[board.numberOfMines]; //csak akkora t�mb�t, amekkora t�nyleg kell
                            //csak hogy indexelek benne? bombImageViewsIndex++

                            int bombImageViewsIndex = 0;
                            for (int k = 0; k < board.numberOfMines; k++) {
                                bombImageViews[k] = new ImageView(mineImage);
                                bombImageViews[k].setFitWidth(15);
                                bombImageViews[k].setPreserveRatio(true);
                                bombImageViews[k].setSmooth(true);
                                bombImageViews[k].setCache(true);
                            }
                            bombImageViewsIndex = 0; //!
                            for (int rowIndex = 0; rowIndex < board.height; rowIndex++) {
                                for (int colIndex = 0; colIndex < board.width; colIndex++) {

                                    if (board.isMine[rowIndex][colIndex] == 1) {
                                        if (boardTiles[rowIndex][colIndex].getText() == "?") {
                                            boardTiles[rowIndex][colIndex].setText("");
                                        }

                                        boardTiles[rowIndex][colIndex].setPadding(new Insets(5));

                                        boardTiles[rowIndex][colIndex].setGraphic(bombImageViews[bombImageViewsIndex]);
                                        bombImageViewsIndex++;
                                    }
                                }
                            }
                            //v�rni kellene kicsit, miel�tt felugrik az ablak
                            String[] options = {"Igen", "Nem"};
                            String choice = showDialog("Vesztett�l.", "Sajnos vesztett�l.\nSzeretn�l �j j�t�kot kezdeni?", options);
                            if (choice == "Igen") {
                                startNewGame(difficulty);
                                borderPane.setCenter(gridPane);
                                stage.sizeToScene();
                            }


                        } else {

                            revealBlock(i, j);
                            if ((board.width * board.height - revealedBlocks) == board.numberOfMines) {
                                timer.setTimeElapsed();
                                System.out.println(timer.getTimeElapsed());
                                hasWonTheGame = true;
                                if (!HighScoresTestDrive.isNewHighScore(timeElapsed, difficulty)) {
                                    //nyert, de nem ker�lt be a legjobbak k�z�
                                    String[] options = {"Igen", "Nem"};
                                    String choice = showDialog("Nyert�l!", "Gratul�lunk! Nyert�l.\n"
                                            + "Szeretn�l �j j�t�kot kezdeni?", options);
                                    if (choice == "Igen") {
                                        startNewGame(difficulty);
                                        borderPane.setCenter(gridPane);
                                        stage.sizeToScene();
                                    } else {
                                        //nem szeretn�k �j j�t�kot kezdeni
                                    }
                                } else {
                                    //beker�lt a legjobbak k�z�

                                    //itt el lehetne t�rolni a legut�bbi megadott nevet
                                    TextInputDialog dialog = new TextInputDialog();
                                    dialog.setTitle("Toplista");
                                    dialog.setHeaderText("Gratul�lunk! Felker�lt�l a toplist�ra.");
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
                    } //end_ha nem volt ott m�r k�rd�jel eleve
                } //end_if/else (buttonType)
            }
        }

    }

    private class MenuItemHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            System.out.println(((MenuItem) event.getSource()).getText());
            if (event.getSource() == menuItemMode) {
                if (menuItemMode.getText() == "V�lt�s egyj�t�kos m�dra") {
                    menuItemMode.setText("V�lt�s t�bbj�t�kos m�dra");
                    menuBar.getMenus().remove(menuStart);
                } else if (menuItemMode.getText() == "V�lt�s t�bbj�t�kos m�dra") {
                    menuItemMode.setText("V�lt�s egyj�t�kos m�dra");
                    menuBar.getMenus().addAll(menuStart);
                }
            }
            if (event.getSource() == menuItemExit) {
                if ((!hasLostTheGame) && (!hasWonTheGame)) {
                    String[] options = {"Igen", "Nem"};
                    String choice = showDialog("Kil�p�s", "Biztos kil�psz?", options);
                    if (choice == "Igen") {
                        stage.close();
                    }
                }
            }
        }


    }

}

