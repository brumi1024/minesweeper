package hu.bme.minesweeper.gui;

import java.util.*;

import hu.bme.minesweeper.player.Player;
import hu.bme.minesweeper.tcp.Network;
import hu.bme.minesweeper.tcp.SocketListener;
import hu.bme.minesweeper.tcp.TcpClient;
import hu.bme.minesweeper.tcp.TcpServer;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.*;
import hu.bme.minesweeper.level.Board;
import javafx.util.Pair;

public class GUI extends Application {
    Control c;
    Board board = new Board();
    Player thisPlayer, otherPlayer;
    Network networkController;

    Button[][] boardTiles;
    MenuItem menuItemNewGame, menuItemHighScores, menuItemMode, menuItemExit;
    MenuItem menuItemStartServer, menuItemStartClient;
    Menu menuDifficulty, menuStart;
    MenuBar menuBar;
    GridPane gridPane;
    BorderPane borderPane;
    Stage stage;
    Label serverSidePlayer = new Label("Server side player");
    Label clientSidePlayer = new Label("Client side player");

    Label timeElapsedLabel;
    Label flagsLeftLabel;
    String difficulty;
    int numOfFlagsLeft; //ez nem kell, a Board.mineLeft eleme
    StringProperty message;
    boolean hasLostTheGame; //erre majd vigyázni kell, hogy új játék kezdésekor vissza kell állítani false-ra!
    boolean hasWonTheGame;
    boolean isMultiplayer = false;
    int bombRowIndex, bombColIndex;
    short revealedBlocks;

    Image mineImage;
    ImageView[] bombImageViews;
    ImageView mineImageView;

    String timeElapsed = "00:00";

    Timer timer;
    
    /*multiplayerhez*/
    /*multiplayerhez v�ge*/

    public static void main(String[] args) {
        launch(args);
    }

    private void showTable() {
        HighScoresTestDrive.showTable(-1, null);
    }

    //show popup window
    private String showDialog(String title, String text, String[] buttons) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);


        ButtonType buttonTypeYes = new ButtonType(buttons[0]);
        ButtonType buttonTypeNo = new ButtonType(buttons[1]);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonTypeYes) {
            return buttons[0];
        } else {
            return buttons[1];
        }

    }

    /*create a board, show the cells in a gridpane*/
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

        //initializating
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
        serverImageView.setStyle("-fx-background-radius: 5; -fx-background-color: green");
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

        /***server k�r� border***/
        HBox serverBorderBox = new HBox(serverImageView);
        HBox clientBorderBox = new HBox(clientImageView);

        VBox serverImageVBox = new VBox(serverBorderBox, serverSidePlayer);
        VBox clientImageVBox = new VBox(clientBorderBox, clientSidePlayer);

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

        menuStart = new Menu("Start");

        menuItemStartServer = new MenuItem("Start server");
        menuItemStartServer.setOnAction((ActionEvent e) ->
        {
            //I am going to be a SERVER
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Starting server");
            dialog.setHeaderText("Starting server");
            dialog.setGraphic(null);
            dialog.setContentText("Your name:");
            Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setText("Start server...");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                Alert alert = new Alert(AlertType.INFORMATION, "Waiting for connection", ButtonType.CANCEL);

                thisPlayer = new Player(result.get(), true);
                serverSidePlayer.setText(thisPlayer.getName());

                networkController = new TcpServer(new FxSocketListener());

                Task<Void> task = new Task<Void>() {
                    @Override
                    public Void call() throws Exception {
                        networkController.connect("");

                        while (!networkController.isConnected()) {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException ignored) {
                            }

                            if (isCancelled()) {
                                networkController.disconnect();
                                System.out.print("cancel");
                                break;
                            }
                        }
                        return null;
                    }
                };

                task.setOnRunning(e1 -> {
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    Optional<ButtonType> cancelled = alert.showAndWait();

                    if (cancelled.get() == ButtonType.CANCEL) {
                        task.cancel();
                    }

                });

                task.setOnSucceeded(e1 -> {
                    alert.hide();

                    networkController.send(new Pair<>("name", thisPlayer.getName()));
                });

                new Thread(task).start();
            }
            if (thisPlayer.isActive()) { //ha amIActive && amIServer
                clientBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 0;");
                serverBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 3;");
            }

        });
        menuItemStartClient = new MenuItem("Start client");
        menuItemStartClient.setOnAction(e ->
        {
            //I am going to be a CLIENT
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Client connecting");
            dialog.setGraphic(null);
            ButtonType loginButtonType = new ButtonType("Connecting...", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField IP = new TextField();
            IP.setPromptText("IP address:");
            TextField name = new TextField();
            name.setPromptText("Your name:");

            grid.add(new Label("IP address:"), 0, 0);
            grid.add(IP, 1, 0);
            grid.add(new Label("Your name:"), 0, 1);
            grid.add(name, 1, 1);

            dialog.getDialogPane().setContent(grid);

            Platform.runLater(() -> IP.requestFocus());

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return new Pair<>(IP.getText(), name.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(pair -> {
                String ipAddress = pair.getKey();

                thisPlayer = new Player(pair.getValue(), false);
                clientSidePlayer.setText(thisPlayer.getName());

                networkController = new TcpClient(new FxSocketListener());
                networkController.connect(ipAddress);
            });
            if (thisPlayer.isActive()) { //ha amIActive && amIClient
                clientBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 3;");
                serverBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 0;");
            }
        });

        menuStart.getItems().addAll(menuItemStartServer, menuItemStartClient);


        stage = primaryStage;

        Label timeElapsedLabel = new Label(timeElapsed);
        Label flagsLeftLabel = new Label(Integer.toString(numOfFlagsLeft));
        message = new SimpleStringProperty();
        flagsLeftLabel.textProperty().bind(message);
        message.set(Integer.toString(numOfFlagsLeft));


        startNewGame("easy");

        //hBox contains the south of the scene: the elapsed time and the number of remaining flags
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        Region spacer = new Region(); //so that elapsed time goes to the left, remaining flags go to the right
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hBox.getChildren().addAll(timeElapsedLabel, spacer, flagsLeftLabel);

        menuBar = new MenuBar();
        Menu menuGame = new Menu("Game");

        //create the menuItems and add listeners
        menuItemNewGame = new MenuItem("New Game");
        menuItemNewGame.setOnAction(e -> {
            startNewGame(difficulty);
            borderPane.setCenter(gridPane);
            stage.sizeToScene();
        });

        menuItemHighScores = new MenuItem("High Scores");
        menuItemHighScores.setOnAction(e -> {
            showTable();
        });

        RadioMenuItem menuItemEasy = new RadioMenuItem("Easy");
        RadioMenuItem menuItemMedium = new RadioMenuItem("Medium");
        RadioMenuItem menuItemHard = new RadioMenuItem("Hard");
        ToggleGroup difficultyGroup = new ToggleGroup();
        menuItemEasy.setToggleGroup(difficultyGroup);
        menuItemMedium.setToggleGroup(difficultyGroup);
        menuItemHard.setToggleGroup(difficultyGroup);

        menuItemEasy.setSelected(true);

        menuDifficulty = new Menu("Difficulty level");
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


        menuItemMode = new MenuItem("Multiplayer");
        menuItemMode.setOnAction(new MenuItemHandler());

        menuItemExit = new MenuItem("Exit");
        menuItemExit.setOnAction(new MenuItemHandler());

        menuGame.getItems().addAll(menuItemNewGame, menuItemHighScores, menuDifficulty, menuItemMode, new SeparatorMenuItem(), menuItemExit);
        menuBar.getMenus().addAll(menuGame);

		/*layout of single player game*/
        borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setCenter(gridPane);
        borderPane.setBottom(hBox);

        Scene scene = new Scene(borderPane);

		/*change between single- and multiplayer*/
        menuItemMode.setOnAction(e ->
        {
            if (Objects.equals(menuItemMode.getText(), "Single player")) {
                menuItemMode.setText("Multiplayer");
                menuBar.getMenus().remove(menuStart);
                borderPane.setLeft(null);
                borderPane.setRight(null);
                borderPane.setBottom(hBox);
                isMultiplayer = false;

                if (networkController != null && networkController.isConnected()) {
                    networkController.disconnect();
                }
            } else if (Objects.equals(menuItemMode.getText(), "Multiplayer")) {
                menuItemMode.setText("Single player");
                menuBar.getMenus().addAll(menuStart);
                borderPane.setLeft(serverVBox);
                borderPane.setRight(clientVBox);
                borderPane.setBottom(null);
                isMultiplayer = true;
            }
            stage.sizeToScene();
        });

        borderPane.setStyle("-fx-background-image: url(\"images/orange_background2.jpg\"); -fx-background-position: center center; -fx-background-repeat: stretch;");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Minesweeper");
        primaryStage.show();

        stage.setOnCloseRequest(e -> {
            if (!hasLostTheGame && (!hasWonTheGame)) {
                String[] options = {"Yes", "No"};
                String choice = showDialog("Exit", "Are you sure you want to exit?", options);
                if (Objects.equals(choice, "Yes")) {
                    stage.close();
                }
                if (Objects.equals(choice, "No")) {
                    e.consume();
                }
            }
        });

    }

    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(Object data) {
            if (data != null && thisPlayer.isServer()) {
                handleServerData(data);
            } else if (data != null && !thisPlayer.isServer()) {
                handleClientData(data);
            }
        }
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
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (inBounds(rowIndex + i, colIndex + j, board.getBoardHeight(), board.getBoardWidth())
                            && (!boardTiles[rowIndex + i][colIndex + j].isDisabled()))
                        revealBlock(rowIndex + i, colIndex + j);
                }
            }
        }
    }

    private void handleServerData(Object data) {
        if (data instanceof Pair) {
            Pair<String, Object> incomingData = (Pair<String, Object>) data;
            switch (incomingData.getKey()) {
                case "name":
                    otherPlayer = new Player((String) incomingData.getValue(), false);
                    clientSidePlayer.setText(otherPlayer.getName());
                    break;
                case "move":
                    revealBlock(((int[]) incomingData.getValue())[0], ((int[]) incomingData.getValue())[1]);
                    break;
            }

        }
    }

    private void handleClientData(Object data) {
        if (data instanceof Pair) {
            Pair<String, Object> incomingData = (Pair<String, Object>) data;
            switch (incomingData.getKey()) {
                case "name":
                    otherPlayer = new Player((String) incomingData.getValue(), true);
                    serverSidePlayer.setText(otherPlayer.getName());
                    break;
                case "move":
                    revealBlock(((int[]) incomingData.getValue())[0], ((int[]) incomingData.getValue())[1]);
                    break;
                case "level": // TODO palyadatok
                    revealBlock(((int[]) incomingData.getValue())[0], ((int[]) incomingData.getValue())[1]);
                    break;
            }
        }
    }


    //when clicked on a cell
    private class ButtonClickedHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            //which mouse button?
            boolean buttonType = false; // false = left click; true = right click;
            if (event.getButton() == MouseButton.SECONDARY) {
                buttonType = true;
            }
            //which cell?
            String coordsString = ((Button) event.getSource()).getId();
            String[] coords = coordsString.split("-");
            int i = Integer.parseInt(coords[0]);
            int j = Integer.parseInt(coords[1]);

            if (!boardTiles[i][j].isDisable() && (!hasLostTheGame) && (!hasWonTheGame)) {
                if (isMultiplayer) {
                    networkController.send(new Pair<>("move", new int[]{i, j}));
                }

                if (buttonType) { //ha jobb gombbal kattintottunk meg nem felfedett mezore
                    //ha mar van ott kerdojel, vegyuk le
                    if (boardTiles[i][j].getText() == "?") {
                        numOfFlagsLeft++;
                        boardTiles[i][j].setStyle("-fx-background-color: #000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);"
                                + " -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; "
                                + "-fx-background-radius: 0,0,0,0; -fx-background-insets: 0,0,0,0;");
                        boardTiles[i][j].setText("");
                    } else if (numOfFlagsLeft > 0) { //egyebkent irjunk ra kerdojelet
                        numOfFlagsLeft--;
                        boardTiles[i][j].setStyle("-fx-background-color: #000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);"
                                + " -fx-text-fill: red; -fx-font-size: 15px; -fx-font-weight: bold; "
                                + "-fx-background-radius: 0,0,0,0; -fx-background-insets: 0,0,0,0;");
                        boardTiles[i][j].setText("?");
                    }
                    message.set(Integer.toString(numOfFlagsLeft));
                } else {
                    if (!Objects.equals(boardTiles[i][j].getText(), "?")) { //ha kerdojel van mar ott, nem nyulunk semmihez
                        //ha normalis kattintas a bal egergombbal
                        //ha az elso katt tortent, inditsd el a timert
                        if (revealedBlocks == 0) {
                            timer.setTimeFirstClick();
                        }

                        if (!board.minesMatrix[i][j]) {
                            boardTiles[i][j].setDisable(true);
                        }

                        if (board.minesMatrix[i][j]) {
                            hasLostTheGame = true;
                            bombImageViews = new ImageView[board.getNumOfMines()];
                            //csak hogy indexelek benne? bombImageViewsIndex++

                            int bombImageViewsIndex = 0;
                            for (int k = 0; k < board.getNumOfMines(); k++) {
                                bombImageViews[k] = new ImageView(mineImage);
                                bombImageViews[k].setFitWidth(15);
                                bombImageViews[k].setPreserveRatio(true);
                                bombImageViews[k].setSmooth(true);
                                bombImageViews[k].setCache(true);
                            }
                            bombImageViewsIndex = 0;
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

                            String[] options = {"Yes", "No"};
                            String choice = showDialog("Lost", "You lost.\nWould you like to start a new game?", options);
                            if (Objects.equals(choice, "Yes")) {
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
                                    //nyert, de nem kerult be a legjobbak koze
                                    String[] options = {"Yes", "No"};
                                    String choice = showDialog("You won!", "Congratulations! You won.\n"
                                            + "Would you like to start a new game?", options);
                                    if (choice == "Yes") {
                                        startNewGame(difficulty);
                                        borderPane.setCenter(gridPane);
                                        stage.sizeToScene();
                                    } else {

                                    }
                                } else {
                                    //bekerult a legjobbak koze

                                    TextInputDialog dialog = new TextInputDialog();
                                    dialog.setTitle("Best");
                                    dialog.setHeaderText("Congratulations! It's a new record.");
                                    dialog.setGraphic(null);
                                    dialog.setContentText("Your name:");

                                    Optional<String> result = dialog.showAndWait();
                                    if (result.isPresent()) {
                                        int place = HighScoresTestDrive.insertData(new HighScores(result.get(), timeElapsed), difficulty);
                                        HighScoresTestDrive.showTable(place - 1, difficulty);
                                    }
                                }
                            }
                        }
                    } //end_ha nem volt ott mar ott kerdojel eleve
                } //end_if/else (buttonType)
            }
        }

    }

    private class MenuItemHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (event.getSource() == menuItemExit) {
                if ((!hasLostTheGame) && (!hasWonTheGame)) {
                    String[] options = {"Yes", "No"};
                    String choice = showDialog("Exit", "Are you sure you want to exit?", options);
                    if (Objects.equals(choice, "Yes")) {
                        stage.close();
                    }
                }
            }
        }


    }

}

