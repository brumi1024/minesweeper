package minesweeperGuiPackage;

public class HighScores {
	//private int place;
	//ide lehetne a helyezést is eltárolni, és akkor nem olyan bonyolult a táblázat megjelenítése
	private String name;
	private String time;
	
	/*public String getPlace() {
		return name;
	}*/
	
	HighScores(String name, String time) {
		this.name=name;
		this.time=time;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTime() {
		return time;
	}
	
	/*public void setPlace(int place) {
		this.place=place;
	}*/
	
	public void setName(String name) {
		this.name=name;
	}
	
	public void setTime(String time) {
		this.time=time;
	}
	
	
	
}
