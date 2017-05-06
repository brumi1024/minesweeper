package minesweeperGuiPackage;

/*((ki lehetne menteni az utols� j�t�kot))*/

/**legyen a high scores-n�l a t�bl�zat egy felugr� ablak, aminek egyetlen gombja van, "Vissza"**/

import java.io.*;
import java.util.Comparator;
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
		Label lblHeading = new Label("Legjobb j�t�kosok");
		lblHeading.setFont(new Font("Arial", 20));
		TableView<HighScores> table = new TableView<HighScores>();
		table.setItems(loadData());
		table.setPrefHeight(147);
		
		TableColumn<HighScores, String> colTitle = new TableColumn("N�v");
		 colTitle.setMinWidth(300);
		 colTitle.setCellValueFactory(new PropertyValueFactory<HighScores, String>("Name"));
		 colTitle.setSortable(false);
		 
		 TableColumn<HighScores, String> colYear = new TableColumn("Id�");
		 colYear.setMinWidth(100);
		 colYear.setCellValueFactory(new PropertyValueFactory<HighScores, String>("Time"));
		 colYear.setSortable(false);
		 
		 table.getColumns().addAll(colTitle, colYear); 
		 VBox paneMain = new VBox();
		 paneMain.setSpacing(10);
		 paneMain.setPadding(new Insets(10, 10, 10, 10));
		 paneMain.getChildren().addAll(lblHeading, table);
		 
		 Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Legjobb eredm�nyek");
			alert.setHeaderText(null);
			
			DialogPane dialogPane = alert.getDialogPane();
			Label labelke = new Label("Legjobb j�t�kosok t�bl�zatban");
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
				String[] items = line.split(","); //meg nem engedett karakter kell legyen majd a n�v beolvas�s�n�l!!!
				data.add(new HighScores(items[0],items[1]));
			}
			bufferedReader.close();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		return data;
	}
	
	public static int insertData(HighScores newResult) {
		//ha nem ker�lt fel a list�ra, return -1,
		//ha felker�lt, friss�tj�k a legjobb eredm�nyeket tartalmaz� f�jlt,
		//�s return, hogy hanyadik helyez�s lett
		ObservableList<HighScores> data = loadData();
		if(compareTimes(newResult.getTime(), data.get(data.size()-1).getTime()) >= 0) {
			System.out.println("Sajnos nem ker�lt�l be a legjobbak k�z�");
			return -1;
		} else {
			System.out.println("Beker�lt�l a legjobbak k�z�!");
			data.add(newResult);
			
			Comparator<HighScores> highScoresComparator = new Comparator<HighScores>() {
				@Override
				public int compare(HighScores hS1, HighScores hS2) {
					return compareTimes(hS1.getTime(), hS2.getTime());
				}
			};
			FXCollections.sort(data, highScoresComparator);

			if(data.size()>8) {
				data.remove(data.size()-1);
			}
			
			try {
				File file = new File("data.txt");
				FileWriter fileWriter = new FileWriter(file);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				String line;
				for (HighScores highScoreItem : data) {
					line = highScoreItem.getName() + "," + highScoreItem.getTime();
					bufferedWriter.write(line);
					bufferedWriter.newLine();
				}
				bufferedWriter.close();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			return (data.lastIndexOf(newResult)+1);
		}
		
	}
	
	public static int compareTimes(String time1, String time2) {
		//return -1, ha time1<time2
		//return 1, ha time1>time2
		//return 0, ha a k�t id� megegyezik
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
}
