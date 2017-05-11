package hu.bme.minesweeper.gui;

import java.util.*;

import hu.bme.minesweeper.datamodel.HighScores;
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
import javafx.stage.*;
import hu.bme.minesweeper.level.Board;
import hu.bme.minesweeper.level.Cell;
import javafx.util.Pair;

public class GUI extends Application {
    private Board board = new Board();
    private Player thisPlayer, otherPlayer;
    private Network networkController;

    private MenuItem menuItemMode;
    private MenuItem menuItemExit;
    Menu menuDifficulty;
    MenuItem menuItemNewGame;
    private MenuBar menuBar;
    private GridPane gridPane;
    private BorderPane borderPane;
    private Stage stage;
    private Label serverSidePlayer = new Label("Server side player");
    private Label clientSidePlayer = new Label("Client side player");
    HBox serverBorderBox;
    HBox clientBorderBox;
    VBox serverImageVBox;
    VBox clientImageVBox;

    Label timeElapsedLabel;
    Label flagsLeftLabel;
    private String difficulty;
    private int numOfFlagsLeft; //ez nem kell, a Board.mineLeft eleme
    private StringProperty message;
    private boolean hasLostTheGame; //erre majd vigyazni kell, hogy uj jatek kezdesekor vissza kell allitani false-ra!
    private boolean hasWonTheGame;
    private boolean isMultiplayer = false;
    private short revealedBlocks;
    private short foundMines;
    private ProgressBar serverPB = new ProgressBar();
    private ProgressBar clientPB = new ProgressBar();
    Label serverMinesFoundLabel;
    Label clientMinesFoundLabel;

    private int timeElapsed = 0;

    private Timer timer;

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
    private void startNewGame(String difficulty, Set<Integer> mineIndices) {
        timer = new Timer();
        revealedBlocks = 0;
        foundMines = 0;
        hasLostTheGame = false;
        hasWonTheGame = false;
        serverPB.setProgress(0);
        clientPB.setProgress(0);
        
        board = new Board();
        board.createBoards(difficulty, (isMultiplayer && !thisPlayer.isServer()), mineIndices);

        numOfFlagsLeft = board.getNumOfMines();
        message.set(Integer.toString(numOfFlagsLeft));

        gridPane = null;
        gridPane = new GridPane();
        gridPane.setHgap(1);
        gridPane.setVgap(1);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));

        for (int i = 0; i < (board.getBoardHeight() * board.getBoardWidth()); i++) {
            board.cells.get(i).getButton().setDisable(false);
            board.cells.get(i).getButton().setId("" + i);
            board.cells.get(i).getButton().setStyle("-fx-background-color: #000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);"
                    + " -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;"
                    + " -fx-background-radius: 0,0,0,0; -fx-background-insets: 0,0,0,0;");
            board.cells.get(i).getButton().setMinWidth(30);
            board.cells.get(i).getButton().setMinHeight(30);
            board.cells.get(i).getButton().setOnMouseClicked(new ButtonClickedHandler());
        }

        for (int i = 0; i < board.getBoardHeight(); i++) {
            for (int j = 0; j < board.getBoardWidth(); j++) {
                gridPane.add(board.cells.get(i * board.getBoardWidth() + j).getButton(), j, i);
            }
        }

        if (isMultiplayer) {
        	serverMinesFoundLabel.setText("0");
        	clientMinesFoundLabel.setText("0");
            if (thisPlayer.isServer()) {
                networkController.send(new Pair<>("level", new Pair<>(difficulty, board.getMineIndices())));
                thisPlayer.setActive(true);
            } else {
                thisPlayer.setActive(false);
            }

            changeBorder();
            
            thisPlayer.setPoints(0);
            otherPlayer.setPoints(0);
        }
        stage.sizeToScene();
    }

    private void initializeGUI(Stage primaryStage) {
        Image mineImage = new Image("images/flower2.png");
        ImageView mineImageView = new ImageView();
        mineImageView.setImage(mineImage);
        mineImageView.setFitWidth(12);
        mineImageView.setPreserveRatio(true);
        mineImageView.setSmooth(true);
        mineImageView.setCache(true);

		/*creating multiplayer layout: left side and right side*/

        serverMinesFoundLabel = new Label(" " + "0");
        clientMinesFoundLabel = new Label(" " + "0");
             
        serverPB = new ProgressBar();
        clientPB = new ProgressBar();
        serverPB.setProgress(0);
        clientPB.setProgress(0);
        
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

        /***server kore border***/
        serverBorderBox = new HBox(serverImageView);
        clientBorderBox = new HBox(clientImageView);

        serverImageVBox = new VBox(serverBorderBox, serverSidePlayer);
        clientImageVBox = new VBox(clientBorderBox, clientSidePlayer);

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


        MenuItem menuItemStartClient = new MenuItem("Start client");
        menuItemStartClient.setOnAction(e ->
        {
        	

        });

        stage = primaryStage;

        Label timeElapsedLabel = new Label(convertTime(timeElapsed));
        Label flagsLeftLabel = new Label(Integer.toString(numOfFlagsLeft));
        message = new SimpleStringProperty();
        flagsLeftLabel.textProperty().bind(message);
        message.set(Integer.toString(numOfFlagsLeft));

        startNewGame("easy", new HashSet<>());

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
            startNewGame(difficulty, new HashSet<>());
            borderPane.setCenter(gridPane);
            stage.sizeToScene();
        });

        MenuItem menuItemHighScores = new MenuItem("High Scores");
        menuItemHighScores.setOnAction(e -> showTable());

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
                startNewGame("easy", new HashSet<>());
                borderPane.setCenter(gridPane);
                stage.sizeToScene();
            }
            if ((menuItemMedium.isSelected()) && (!Objects.equals(difficulty, "medium"))) {
                difficulty = "medium";
                startNewGame("medium", new HashSet<>());
                borderPane.setCenter(gridPane);
                stage.sizeToScene();
            }
            if ((menuItemHard.isSelected()) && (!Objects.equals(difficulty, "hard"))) {
                difficulty = "hard";
                startNewGame("hard", new HashSet<>());
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
                borderPane.setLeft(null);
                borderPane.setRight(null);
                borderPane.setBottom(hBox);
                isMultiplayer = false;

                if (networkController != null && networkController.isConnected()) {
                    networkController.disconnect();
                }
            } else if (Objects.equals(menuItemMode.getText(), "Multiplayer")) {
            	//kliens vagy szerver?
                menuItemMode.setText("Single player");
                borderPane.setBottom(null);
                isMultiplayer = true;
                
            	String[] options = {"I'm gonna be a server!", "I'm gonna be a client!"};
                String choice = showDialog("Starting a multiplayer game", "Are you gonna be a server or a client?", options);
                if (Objects.equals(choice, "I'm gonna be a server!")) {
                	handleStartServer();
                    borderPane.setLeft(serverVBox);
                    borderPane.setRight(clientVBox);
                }
                if (Objects.equals(choice, "I'm gonna be a client!")) {
                    handleStartClient();
                    borderPane.setLeft(clientVBox);
                    borderPane.setRight(serverVBox);
                    //kliens nem kezdhet uj jatekot es nem valtoztathat nehezseget
                    menuItemNewGame.setDisable(true);
                    menuDifficulty.setDisable(true);
                }
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

    @Override
    public void start(Stage primaryStage) {
        difficulty = "easy";
        initializeGUI(primaryStage);
    }

    String convertTime(int sec) {
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }

    private void createWaitAlert(String ipAddress) {
        String contentText = thisPlayer.isServer() ? "Waiting for connection..." : "Connecting...";
        Alert alert = new Alert(AlertType.INFORMATION, contentText, ButtonType.CANCEL);

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                networkController.connect(ipAddress);

                while (!networkController.isConnected()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {
                    }

                    if (isCancelled()) {
                        networkController.disconnect();
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

            if (thisPlayer.isServer()) {
                thisPlayer.setActive(true);

                startNewGame("easy", new HashSet<>());
                borderPane.setCenter(gridPane);
                stage.sizeToScene();
            } else {
                thisPlayer.setActive(false);
            }
            
            changeBorder();
        });

        new Thread(task).start();
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

    private void revealBlock(int index) {
        revealedBlocks++;
        board.cells.get(index).draw();
        if (board.cells.get(index).step() == 0) {
            for (Cell element : board.cells.get(index).getNeighbours()) { //minden szomszedra
                //ha nem disabled
                if (!board.cells.get(board.cells.indexOf(element)).getButton().isDisabled()) {
                    revealBlock(board.cells.indexOf(element));
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
                    handleOtherPlayerMovement((int) incomingData.getValue());
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
                    handleOtherPlayerMovement((int) incomingData.getValue());
                    break;
                case "level": //
                    startNewGame((String) ((Pair) incomingData.getValue()).getKey(),
                            ((HashSet<Integer>) ((Pair) incomingData.getValue()).getValue()));
                    borderPane.setCenter(gridPane);
                    stage.sizeToScene();
                    break;
            }
        }
    }

    private void handleOtherPlayerMovement(int clickedIndex) {
        if (board.cells.get(clickedIndex).step() == -1) {
            Cell activeCell = board.cells.get(clickedIndex);
            otherPlayer.increasePoints();
            if(otherPlayer.isServer()) serverMinesFoundLabel.setText(Integer.toString(otherPlayer.getPoints()));
            else clientMinesFoundLabel.setText(Integer.toString(otherPlayer.getPoints()));
            activeCell.draw();
            activeCell.getButton().setDisable(true);

            if (++foundMines >= 3) {
                thisPlayer.setActive(true);
                foundMines = 0;
            }
        } else {
            thisPlayer.setActive(true);
            foundMines = 0;
        }
        
        if(otherPlayer.isServer()) {
        	serverPB.setProgress(((double)foundMines)/3);
        } else {
        	clientPB.setProgress(((double)foundMines)/3);
        }
        
        changeBorder();

        revealBlock(clickedIndex);
        handleMultiplayerGameEnding();
    }

    private void handleMultiplayerGameEnding() {
        //ha van 9 akna, de valaki megtalalt 5-ot, akkor ne jatsszuk tovabb; csak paratlan szamu akna lehet
        if ((thisPlayer.getPoints() > (board.getNumOfMines() / 2)) || (otherPlayer.getPoints() > (board.getNumOfMines() / 2))) {

            timer.setTimeElapsed();
            timeElapsed = timer.getTimeElapsed();

            hasWonTheGame = thisPlayer.getPoints() > otherPlayer.getPoints();
            if (thisPlayer.isServer()) {
                String[] options = new String[]{"Yes", "No"};

                String choice = hasWonTheGame ? showDialog("Game over", "Congratulations! You won.\n"
                        + "Would you like to start a new game?", options) :
                        showDialog("Game over", otherPlayer.getName() + " won.\n"
                                + "Would you like to start a new game?", options);
                if (Objects.equals(choice, "Yes")) {
                    startNewGame(difficulty, new HashSet<>());
                    borderPane.setCenter(gridPane);
                    stage.sizeToScene();
                }
            } else {
                String contentText = hasWonTheGame ? "Congratulations! You won.\n" : otherPlayer.getName() + " won.\n";
                Alert alert = new Alert(AlertType.INFORMATION, contentText, ButtonType.CANCEL);
                alert.setHeaderText(null);
                alert.setTitle("Game over");
                alert.show();

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
            //which cell was clicked?
            int clickedIndex = Integer.parseInt(((Button) event.getSource()).getId());

            if (isMultiplayer) {
                if (thisPlayer.isActive()) {
                    networkController.send(new Pair<>("move", clickedIndex));
                    handleMultiplayerClick(clickedIndex);
                }
            } else {
                handleSinglePlayerClick(buttonType, clickedIndex);
            }
        }
    }

    private void handleSinglePlayerClick(boolean buttonType, int clickedIndex) {

        if (!board.cells.get(clickedIndex).getButton().isDisable() && (!hasLostTheGame) && (!hasWonTheGame)) {
            if (buttonType) { //ha jobb gombbal kattintottunk meg nem felfedett mezore
                board.cells.get(clickedIndex).mark();
                if (board.cells.get(clickedIndex).getMarked()) {
                    numOfFlagsLeft--;
                } else {
                    numOfFlagsLeft++;
                }
                message.set(Integer.toString(numOfFlagsLeft));
            } else {

                if (!board.cells.get(clickedIndex).getMarked()) { //ha kerdojel van mar ott, nem nyulunk semmihez
                    //ha normalis kattintas a bal egergombbal
                    //ha az elso katt tortent, inditsd el a timert
                    if (revealedBlocks == 0) {
                        timer.setTimeFirstClick();
                    }

                    if (board.cells.get(clickedIndex).step() == -1) {
                        hasLostTheGame = true;

                        for (Cell element : board.cells) {
                            if (element.step() == -1) {
                                if (element.getMarked())
                                    element.mark(); //ha volt kerdojel, leszedjuk, akna megy helyette
                                element.draw();
                            }
                        }

                        String[] options = {"Yes", "No"};
                        String choice = showDialog("Lost", "You lost.\nWould you like to start a new game?", options);
                        if (Objects.equals(choice, "Yes")) {
                            startNewGame(difficulty, new HashSet<>());
                            borderPane.setCenter(gridPane);
                            stage.sizeToScene();
                        }

                    } else {
                        revealBlock(clickedIndex);
                        if ((board.getBoardWidth() * board.getBoardHeight() - revealedBlocks) == board.getNumOfMines()) {
                            timer.setTimeElapsed();
                            timeElapsed = timer.getTimeElapsed();

                            hasWonTheGame = true;
                            if (!HighScoresTestDrive.isNewHighScore(timeElapsed, difficulty)) {
                                //nyert, de nem kerult be a legjobbak koze
                                String[] options = {"Yes", "No"};
                                String choice = showDialog("You won!", "Congratulations! You won.\n"
                                        + "Would you like to start a new game?", options);
                                if (Objects.equals(choice, "Yes")) {
                                    startNewGame(difficulty, new HashSet<>());
                                    borderPane.setCenter(gridPane);
                                    stage.sizeToScene();
                                }
                            } else {
                                TextInputDialog dialog = new TextInputDialog();
                                dialog.setTitle("Best");
                                dialog.setHeaderText("Congratulations! It's a new record.");
                                dialog.setGraphic(null);
                                dialog.setContentText("Your name:");

                                Optional<String> result = dialog.showAndWait();
                                if (result.isPresent()) {
                                    int place = HighScoresTestDrive.insertData(new HighScores(result.get(), timeElapsed, difficulty));
                                    HighScoresTestDrive.showTable(place - 1, difficulty);
                                }
                            }
                        }
                    }
                } //end_ha nem volt ott mar ott kerdojel eleve
            } //end_if/else (buttonType)
        }
    }

    private void handleMultiplayerClick(int clickedIndex) {
        if (!board.cells.get(clickedIndex).getButton().isDisable() && (!hasWonTheGame)) {
            if (revealedBlocks == 0) {
                timer.setTimeFirstClick();
            }

            if (board.cells.get(clickedIndex).step() == -1) {
                thisPlayer.increasePoints();
                if(thisPlayer.isServer()) serverMinesFoundLabel.setText(Integer.toString(thisPlayer.getPoints())); /**modositott**/
                else clientMinesFoundLabel.setText(Integer.toString(thisPlayer.getPoints()));

                if (++foundMines >= 3) {
                    thisPlayer.setActive(false);
                    foundMines = 0;
                }
            } else {
                foundMines = 0;
                thisPlayer.setActive(false);

            }
            revealBlock(clickedIndex);
            handleMultiplayerGameEnding();

            if(thisPlayer.isServer()) {
            	serverPB.setProgress(((double)foundMines)/3);
            } else {
            	clientPB.setProgress(((double)foundMines)/3);
            }

            changeBorder();
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
    
	public void handleStartServer() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Starting server");
        dialog.setHeaderText("Starting server");
        dialog.setGraphic(null);
        dialog.setContentText("Your name:");
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText("Start server...");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            thisPlayer = new Player(result.get(), true);
            serverSidePlayer.setText(thisPlayer.getName());
            networkController = new TcpServer(new FxSocketListener());
            createWaitAlert("");
        }
        if (thisPlayer.isActive()) { //ha amIActive && amIServer
            clientBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 0;");
            serverBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 3;");
        }
	}
	
	public void handleStartClient() {
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

        Platform.runLater(IP::requestFocus);

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

            createWaitAlert(ipAddress);
        });
        if (thisPlayer.isActive()) { //ha amIActive && amIClient
            clientBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 3;");
            serverBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 0;");
        }
	}
	
	public void changeBorder() {
		//amiRE tesszuk, az az aktiv most, az elozot levesszuk 0-ra
		if(thisPlayer.isActive()) {
			if(thisPlayer.isServer()) {
				clientBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 0;");
		        serverBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 3;");
			} else {
				clientBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 3;");
		        serverBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 0;");
			}
		} else {
			if(otherPlayer.isServer()) {
				clientBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 0;");
		        serverBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 3;");
			} else {
				clientBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 3;");
		        serverBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 0;");
			}
				
		}
		
	}

}

