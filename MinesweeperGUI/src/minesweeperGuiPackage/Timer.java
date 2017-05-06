package minesweeperGuiPackage;



public class Timer {
	
	private long timeElapsed=0; // elsõ kattintástól eltelt idõ
	private long timeFirstClick=0; //elsõ kattintás ideje

	public String getTimeElapsed() {
		return timeString(timeElapsed);
	}


	public void setTimeFirstClick() {
		this.timeFirstClick = System.currentTimeMillis(); //ezt kell meghívni játék indításkor
	}

	public void setTimeElapsed() { //ezt az óra megállításakor (volt itt még argumentum, de nem kell)
		this.timeElapsed =System.currentTimeMillis()-this.timeFirstClick;
	}


	String timeString(long l_time)
	 {
	 	long time= l_time; 
	 	long time_s=0; //másodperchez
	 	long time_min=0;//perchez
	 	long time_h=0;//órához
	 	String vissza="";//ez megy majd vissza
	 	if (time>60 && time<3600) //1 percnél több, de 1 óránál kevesebb
	 	{
	 		time_min = time/60; //ennyi perc
	 		time_s=time%60; //a maradék a mp
	 	}
	 	else if(time>3600)
	 	{
	 		time_h=time/3600; //ennyi óra
	 		time_min = (time-time_h*3600)/60; //ennyi perc
	 		time_s=(time-time_h*3600)%60; //a maradék a mp
	 		 
	 	}
	 	else 
	 	{
	 		time_s=time; //többi úgyis 0
	 	}
	 
	 	
	 	
	 	//ez rakja össze a számokat egy stringé, pl: 0:00:00
	 	//STRING.FORMAT SZÁMOT KÉR, NEM STRINGET!
	 	vissza=vissza.concat(String.valueOf(time_h)).concat(":").concat(String.format("%02d",time_min)).concat(":").concat(String.format("%02d",time_s));
	 	
	 	
	 	return vissza;
	 }
}
