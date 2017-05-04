package minesweeperGuiPackage;

/*((ki lehetne menteni az utolsó játékot))*/

/**legyen a high scores-nál a táblázat egy felugró ablak, aminek egyetlen gombja van, "Vissza"**/

import java.io.*;
import java.util.Optional;

import javafx.application.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class HighScoresTestDrive extends Application {
	public static void main(String[] args) throws FileNotFoundException {
		launch(args);
	}
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		
		 /*Scene scene = new Scene(paneMain); 
		 primaryStage.setScene(scene);
		 primaryStage.setTitle("Movie Inventory");
		 primaryStage.show();*/
		 
	}
	
	public static void showTable() {
		Label lblHeading = new Label("Legjobb játékosok");
		lblHeading.setFont(new Font("Arial", 20));
		TableView<HighScores> table = new TableView<HighScores>();
		table.setItems(loadData());
		table.setPrefHeight(147);
		
		TableColumn<HighScores, String> colTitle = new TableColumn("Név");
		 colTitle.setMinWidth(300);
		 colTitle.setCellValueFactory(new PropertyValueFactory<HighScores, String>("Name"));
		 colTitle.setSortable(false);
		 
		 TableColumn<HighScores, String> colYear = new TableColumn("Idõ");
		 colYear.setMinWidth(100);
		 colYear.setCellValueFactory(new PropertyValueFactory<HighScores, String>("Time"));
		 colYear.setSortable(false);
		 
		 table.getColumns().addAll(colTitle, colYear); 
		 VBox paneMain = new VBox();
		 paneMain.setSpacing(10);
		 paneMain.setPadding(new Insets(10, 10, 10, 10));
		 paneMain.getChildren().addAll(lblHeading, table);
		 
		 Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Legjobb eredmények");
			alert.setHeaderText(null);
			
			DialogPane dialogPane = alert.getDialogPane();
			Label labelke = new Label("Legjobb játékosok táblázatban");
			HBox myHBox = new HBox(labelke);
			dialogPane.setHeader(paneMain);
			
			ButtonType buttonTypeBack = new ButtonType("Vissza");
			alert.getButtonTypes().setAll(buttonTypeBack);

			Optional<ButtonType> result = alert.showAndWait();
	}


	private static ObservableList<HighScores> loadData() {
		ObservableList<HighScores> data = FXCollections.observableArrayList();
		try {
			File file = new File("data.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ( (line=bufferedReader.readLine())!=null) {
				System.out.println(line);
				String[] items = line.split(","); //meg nem engedett karakter kell legyen majd a név beolvasásánál!!!
				data.add(new HighScores(items[0],items[1]));
			}
			bufferedReader.close();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		return data;
	}
}
