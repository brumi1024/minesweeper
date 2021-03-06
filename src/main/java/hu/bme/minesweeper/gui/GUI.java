/**
 * GUI is the class responsible for displaying the graphical user
 * interface for the minesweeper game. It also acts as the control.
 * GUI was written using JavaFX.
 */

package hu.bme.minesweeper.gui;

import hu.bme.minesweeper.datamodel.HighScores;
import hu.bme.minesweeper.level.Board;
import hu.bme.minesweeper.level.Cell;
import hu.bme.minesweeper.player.Player;
import hu.bme.minesweeper.tcp.Network;
import hu.bme.minesweeper.tcp.SocketListener;
import hu.bme.minesweeper.tcp.TcpClient;
import hu.bme.minesweeper.tcp.TcpServer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class GUI extends Application {
    private Board board = new Board();
    private Player thisPlayer, otherPlayer;
    private Network networkController;

    private MenuItem menuItemMode;
    private MenuItem menuItemExit;
    private Menu menuDifficulty;
    private MenuItem menuItemNewGame;
    private GridPane gridPane;
    private BorderPane borderPane;
    private Stage stage;
    private Label serverSidePlayer = new Label("Server side player");
    private Label clientSidePlayer = new Label("Client side player");
    private HBox serverBorderBox;
    private HBox clientBorderBox;

    private String difficulty;
    private int numOfFlagsLeft;
    private StringProperty message;
    private StringProperty elapsedTimeString;
    private boolean hasLostTheGame;
    private boolean hasWonTheGame;
    private boolean isMultiplayer = false;
    private short revealedBlocks;
    private short foundMines;
    private ProgressBar serverPB = new ProgressBar();
    private ProgressBar clientPB = new ProgressBar();
    private Label serverMinesFoundLabel;
    private Label clientMinesFoundLabel;
    private AnimationTimer animationTimer;

    private int timeElapsed = 0;

    /**
     * Lauches the application by calling the <code>start</code> method.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Displays the table containing the high scores be calling the
     * static <code>showTable</code> method of class HighScoresTestDrive.
     */
    private void showTable() {
        HighScoresTestDrive.showTable(-1, null);
    }

    /**
     * Shows a popup dialog window with two options to choose from.
     *
     * @param title   the title of the window
     * @param text    the text displayed in the window
     * @param buttons the text to be displayed on the buttons
     * @return the chosen option
     */
    private String showDialog(String title, String text, String[] buttons) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);


        ButtonType buttonTypeYes = new ButtonType(buttons[0]);
        ButtonType buttonTypeNo = new ButtonType(buttons[1]);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == buttonTypeYes) {
            return buttons[0];
        } else {
            return buttons[1];
        }
    }

    /**
     * This method is called when starting a new game. It initializes
     * certain variables, creates a new <code>Board</code> according to the
     * specified difficulty, and whether it's a new single- or a multiplayer
     * game. It places the <code>Buttons</code> representing the <code>Cells</code>
     * in a <code>GridPane</code>.
     *
     * @param difficulty  the difficulty level of the new game
     * @param mineIndices the indices of the mines to be placed
     */
    private void startNewGame(String difficulty, Set<Integer> mineIndices) {

        animationTimer.stop();
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

    /**
     * Initializes the graphical layout for both the single- and the
     * multiplayer game. Its sets the layout for singleplayer. It defines what
     * methods are to be called when certain <code>MenuItems</code> are
     * clicked on.
     *
     * @param primaryStage the top-level JavaFX container constructed by
     *                     the platform
     */
    private void initializeGUI(Stage primaryStage) {
        initializeTimer();

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

        /*server kore border*/
        serverBorderBox = new HBox(serverImageView);
        clientBorderBox = new HBox(clientImageView);

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

        stage = primaryStage;

        Label timeElapsedLabel = new Label(convertTime(timeElapsed));
        Label flagsLeftLabel = new Label(Integer.toString(numOfFlagsLeft));
        message = new SimpleStringProperty();
        flagsLeftLabel.textProperty().bind(message);
        message.set(Integer.toString(numOfFlagsLeft));

        elapsedTimeString = new SimpleStringProperty();
        timeElapsedLabel.textProperty().bind(elapsedTimeString);
        elapsedTimeString.set(convertTime(timeElapsed));

        startNewGame("easy", new HashSet<>());

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hBox.getChildren().addAll(timeElapsedLabel, spacer, flagsLeftLabel);

        MenuBar menuBar = new MenuBar();
        Menu menuGame = new Menu("Game");

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
                animationTimer.stop();
                startNewGame("easy", new HashSet<>());
                borderPane.setCenter(gridPane);
                stage.sizeToScene();
            }
            if ((menuItemMedium.isSelected()) && (!Objects.equals(difficulty, "medium"))) {
                difficulty = "medium";
                animationTimer.stop();
                startNewGame("medium", new HashSet<>());
                borderPane.setCenter(gridPane);
                stage.sizeToScene();
            }
            if ((menuItemHard.isSelected()) && (!Objects.equals(difficulty, "hard"))) {
                difficulty = "hard";
                animationTimer.stop();
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
                stage.setTitle("Minesweeper");
                menuItemMode.setText("Multiplayer");
                borderPane.setLeft(null);
                borderPane.setRight(null);
                borderPane.setBottom(hBox);
                isMultiplayer = false;

                menuItemNewGame.setDisable(false);
                menuDifficulty.setDisable(false);

                if (networkController != null && networkController.isConnected()) {
                    networkController.disconnect();
                }
            } else if (Objects.equals(menuItemMode.getText(), "Multiplayer")) {
                animationTimer.stop();
                menuItemMode.setText("Single player");
                borderPane.setBottom(null);
                isMultiplayer = true;

                String[] options = {"I'm gonna be a server!", "I'm gonna be a client!"};
                String choice = showDialog("Starting a multiplayer game", "Are you gonna be a server or a client?", options);
                if (Objects.equals(choice, "I'm gonna be a server!")) {
                    if (handleStartServer()) {
                        stage.setTitle("Minesweeper - SERVER");
                        borderPane.setLeft(serverVBox);
                        borderPane.setRight(clientVBox);
                    }

                }
                if (Objects.equals(choice, "I'm gonna be a client!")) {
                    if (handleStartClient()) {
                        stage.setTitle("Minesweeper - CLIENT");
                        borderPane.setLeft(clientVBox);
                        borderPane.setRight(serverVBox);
                        menuItemNewGame.setDisable(true);
                        menuDifficulty.setDisable(true);
                    }
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

    /**
     * The method called by the <code>launch</code> method in <code>main</code>.
     *
     * @param primaryStage the top-level JavaFX container constructed by
     *                     the platform
     */
    @Override
    public void start(Stage primaryStage) {
        difficulty = "easy";
        initializeGUI(primaryStage);
    }

    /**
     * Converts time given in seconds to a minutes:seconds format.
     *
     * @param sec time given in seconds
     * @return String time in a minutes:seconds format
     */
    private String convertTime(int sec) {
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }

    /**
     * Creates an alert while trying to connect to the other party.
     *
     * @param ipAddress Other players IP address. It is ignored in server mode.
     */
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
                        menuItemMode.fire();
                        break;
                    }
                }
                return null;
            }
        };

        task.setOnRunning(e1 -> {
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.showAndWait();

            final Button cancel = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            cancel.addEventFilter(ActionEvent.ACTION, event -> task.cancel());
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

    /**
     * Called when the user - changing to multiplayer mode - clicks on
     * the 'I'm gonna be a server!' button. Creates a popup window,
     * prompts the user for a name. When successful, a new
     * <code>TcpServer</code> and a new <code>Player</code> is created,
     * and the program waits for a client to connect.
     *
     * @return <code>true</code> if the user has succesfully
     * given a username;
     * <code>false</code> otherwise.
     */
    private boolean handleStartServer() {
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
            otherPlayer = new Player("", false);
            networkController = new TcpServer(new FxSocketListener());
            createWaitAlert("");

            if (thisPlayer.isActive()) { //ha amIActive && amIServer
                clientBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 0;");
                serverBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 3;");
            }

            return true;
        } else {
            menuItemMode.fire();
            return false;
        }
    }

    /**
     * Called when the user, changing to multiplayer mode, clicks on
     * the 'I'm gonna be a client!' button. Creates a popup window,
     * prompts the user for a name and for the ip address of the server.
     * When successful, a new <code>TcpClient</code> and a new <code>Player</code>
     * is created.
     *
     * @return <code>true</code> if the user has succesfully
     * given a username;
     * <code>false</code> otherwise.
     */
    private boolean handleStartClient() {
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

        if (!result.isPresent()) {
            menuItemMode.fire();
            return false;
        } else {
            result.ifPresent(pair -> {
                String ipAddress = pair.getKey();
                thisPlayer = new Player(pair.getValue(), false);
                clientSidePlayer.setText(thisPlayer.getName());
                otherPlayer = new Player("", true);
                networkController = new TcpClient(new FxSocketListener());

                createWaitAlert(ipAddress);

                if (thisPlayer.isActive()) { //ha amIActive && amIClient
                    clientBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 3;");
                    serverBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 0;");
                }
            });

            return true;
        }
    }

    /**
     * When in multiplayer mode, the active <code>Player</code> should always have a
     * green border around the user picture, indicating that it's his/her turn.
     */
    private void changeBorder() {
        //amiRE tesszuk, az az aktiv most, az elozot levesszuk 0-ra
        if (thisPlayer.isActive()) {
            if (thisPlayer.isServer()) {
                clientBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 0;");
                serverBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 3;");
            } else {
                clientBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 3;");
                serverBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 0;");
            }
        } else {
            if (otherPlayer.isServer()) {
                clientBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 0;");
                serverBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 3;");
            } else {
                clientBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 3;");
                serverBorderBox.setStyle("-fx-border-color: limegreen; -fx-border-width: 0;");
            }

        }
    }

    /**
     * When clicked on a <code>Cell</code>, if the <code>Cell</code> wasn't a
     * <code>Mine</code>, it reveals certain <code>Cell</code>s surrounding the
     * clicked one. It uses an iterative algorithm, and the revealed block has
     * elements with zero neighbouring mines on the inside, and is bounded by
     * elements with nonzero neighbouring mines.
     *
     * @param index the index of the <code>Cell</code> that was clicked on
     */
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

    /**
     * Handles the incoming data on server side. The data is a Pair, and it can be a name or a move.
     *
     * @param data Incoming data param.
     */
    private void handleServerData(Object data) {
        if (data instanceof Pair) {
            Pair<String, Object> incomingData = (Pair<String, Object>) data;
            switch (incomingData.getKey()) {
                case "name":
                    otherPlayer.setName((String) incomingData.getValue());
                    clientSidePlayer.setText(otherPlayer.getName());
                    break;
                case "move":
                    handleOtherPlayerMovement((int) incomingData.getValue());
                    break;
            }
        }
    }

    /**
     * Handles the incoming data on client side. The data is a Pair,  it can be a name, a move or the board's params.
     *
     * @param data Incoming data param.
     */
    private void handleClientData(Object data) {
        if (data instanceof Pair) {
            Pair<String, Object> incomingData = (Pair<String, Object>) data;
            switch (incomingData.getKey()) {
                case "name":
                    otherPlayer.setName((String) incomingData.getValue());
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

    /**
     * This method is called when the other player makes a move in multiplayer mode. The method refreshes
     * the displayed data. If the other player has clicked on a mine, the method makes sure that the player
     * can only reveal three mines in a row.
     *
     * @param clickedIndex the index of the <code>Cell</code> clicked by the other player
     */
    private void handleOtherPlayerMovement(int clickedIndex) {
        if (board.cells.get(clickedIndex).step() == -1) {
            Cell activeCell = board.cells.get(clickedIndex);
            otherPlayer.increasePoints();
            if (otherPlayer.isServer()) serverMinesFoundLabel.setText(Integer.toString(otherPlayer.getPoints()));
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

        if (otherPlayer.isServer()) {
            serverPB.setProgress(((double) foundMines) / 3);
        } else {
            clientPB.setProgress(((double) foundMines) / 3);
        }

        changeBorder();

        revealBlock(clickedIndex);
        handleMultiplayerGameEnding();
    }

    /**
     * Handles the end phase of a multiplayer game. The game is over when the outcome is sure based
     * on the number of revealed mines. It notifies the user about who won, and the server side player
     * is offered to start a new game.
     */
    private void handleMultiplayerGameEnding() {
        if ((thisPlayer.getPoints() > (board.getNumOfMines() / 2)) || (otherPlayer.getPoints() > (board.getNumOfMines() / 2))) {

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

    /**
     * This method is responsible for the control of the singleplayer game. The player
     * can mark a <code>Cell</code> with the right mouse button, or click on one with
     * the left mouse button. The first left mouse click starts a timer, which stops
     * when the game is over. When the game is over, the player is notified whether
     * he/she has won/lost the game.
     * <p>
     * If the player has made a new high score, the <code>showDialog</code> method is
     * called.
     *
     * @param buttonType   with which mouse button has the player clicked the
     *                     <code>Button</code>
     * @param clickedIndex the index of the <code>Button</code> clicked by the player
     */
    private void handleSinglePlayerClick(boolean buttonType, int clickedIndex) {

        if (!board.cells.get(clickedIndex).getButton().isDisable() && (!hasLostTheGame) && (!hasWonTheGame)) {
            if (buttonType) {
                board.cells.get(clickedIndex).mark();
                if (board.cells.get(clickedIndex).getMarked()) {
                    numOfFlagsLeft--;
                } else {
                    numOfFlagsLeft++;
                }
                message.set(Integer.toString(numOfFlagsLeft));
            } else {

                if (!board.cells.get(clickedIndex).getMarked()) {
                    if (revealedBlocks == 0) {
                        animationTimer.start();
                    }

                    if (board.cells.get(clickedIndex).step() == -1) {
                        hasLostTheGame = true;
                        animationTimer.stop();

                        for (Cell element : board.cells) {
                            if (element.step() == -1) {
                                if (element.getMarked())
                                    element.mark();
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
                            int winTime = timeElapsed;
                            animationTimer.stop(); //animationTimer lenullazza

                            hasWonTheGame = true;
                            if (!HighScoresTestDrive.isOnTheList(winTime, difficulty)) {
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
                                dialog.setHeaderText("Congratulations! You've made it to the toplist");
                                dialog.setGraphic(null);
                                dialog.setContentText("Your name:");

                                Optional<String> result = dialog.showAndWait();
                                if (result.isPresent()) {
                                    int place = HighScoresTestDrive.insertData(new HighScores(result.get(), winTime, difficulty));
                                    HighScoresTestDrive.showTable(place - 1, difficulty);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * This method is responsible for the control of the multiplayer game. It checks the field for mines, adjacent mines
     * and counts the found mines in one round.
     *
     * @param clickedIndex index of the clicked field.
     */
    private void handleMultiplayerClick(int clickedIndex) {
        if (!board.cells.get(clickedIndex).getButton().isDisable() && (!hasWonTheGame)) {

            if (board.cells.get(clickedIndex).step() == -1) {
                thisPlayer.increasePoints();
                if (thisPlayer.isServer()) serverMinesFoundLabel.setText(Integer.toString(thisPlayer.getPoints()));
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

            if (thisPlayer.isServer()) {
                serverPB.setProgress(((double) foundMines) / 3);
            } else {
                clientPB.setProgress(((double) foundMines) / 3);
            }

            changeBorder();
        }
    }


    /**
     * This method is called when the player clicks with the left mouse
     * button on a <code>Button</code>. It measures the time elapsed between
     * this first click and the current time. It updates the <code>timeElapsedLabel</code>'s
     * <code>SimpleStringProperty</code>.
     */
    private void initializeTimer() {
        animationTimer = new AnimationTimer() {
            private long firstClickTimestamp;

            @Override
            public void start() {
                firstClickTimestamp = System.currentTimeMillis();
                super.start();
            }

            @Override
            public void stop() {
                timeElapsed = 0;
                elapsedTimeString.set(convertTime(timeElapsed));
                super.stop();
            }

            @Override
            public void handle(long timestamp) {
                long now = System.currentTimeMillis();
                timeElapsed = (int) ((now - firstClickTimestamp) / 1000 + 1);
                elapsedTimeString.set(convertTime(timeElapsed));
            }
        };
    }


    /**
     * A Socketlistener interface to handle the network data.
     */
    class FxSocketListener implements SocketListener {

        /**
         * Calls the appropriate methods on incoming data.
         *
         * @param data Incoming data param.
         */
        @Override
        public void onMessage(Object data) {
            if (data != null && thisPlayer.isServer()) {
                handleServerData(data);
            } else if (data != null && !thisPlayer.isServer()) {
                handleClientData(data);
            }
        }

        /**
         * Handles the disconnect event.
         *
         * @param isClosed A closed status flag.
         */
        @Override
        public void onDisconnectedStatus(boolean isClosed) {
            if (isClosed) {
                Alert alert = new Alert(AlertType.INFORMATION,
                        "Connection to the other party is lost.", ButtonType.OK);
                alert.setHeaderText(null);
                alert.setTitle("Connection error");

                Optional<ButtonType> ack = alert.showAndWait();

                if (ack.get() == ButtonType.OK) {
                    menuItemMode.fire();
                }


            }
        }
    }

    /**
     * Handles the <code>Button</code>s being clicked with the mouse.
     */
    private class ButtonClickedHandler implements EventHandler<MouseEvent> {

        /**
         * Recognises which <code>Button</code> was clicked with which
         * mouse button, and calls the appropriate handling function.
         * When in multiplayer mode, it sends the clickedIndex
         * to the other player.
         *
         * @param event an event triggered by a mouseclick
         */
        @Override
        public void handle(MouseEvent event) {
            boolean buttonType = false; // false = left click; true = right click;
            if (event.getButton() == MouseButton.SECONDARY) {
                buttonType = true;
            }
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

    /**
     * Handles when the <code>MenuItem</code> Exit is clicked. It propmts the
     * player whether he/she is sure about his/her choice to exit the game.
     */
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

