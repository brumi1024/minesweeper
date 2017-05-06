package minesweeperGuiPackage;



public class Timer {
	
	private long timeElapsed=0; // els� kattint�st�l eltelt id�
	private long timeFirstClick=0; //els� kattint�s ideje

	public String getTimeElapsed() {
		return timeString(timeElapsed);
	}


	public void setTimeFirstClick() {
		this.timeFirstClick = System.currentTimeMillis(); //ezt kell megh�vni j�t�k ind�t�skor
	}

	public void setTimeElapsed() { //ezt az �ra meg�ll�t�sakor (volt itt m�g argumentum, de nem kell)
		this.timeElapsed =System.currentTimeMillis()-this.timeFirstClick;
	}


	String timeString(long l_time)
	 {
	 	long time= l_time; 
	 	long time_s=0; //m�sodperchez
	 	long time_min=0;//perchez
	 	long time_h=0;//�r�hoz
	 	String vissza="";//ez megy majd vissza
	 	if (time>60 && time<3600) //1 percn�l t�bb, de 1 �r�n�l kevesebb
	 	{
	 		time_min = time/60; //ennyi perc
	 		time_s=time%60; //a marad�k a mp
	 	}
	 	else if(time>3600)
	 	{
	 		time_h=time/3600; //ennyi �ra
	 		time_min = (time-time_h*3600)/60; //ennyi perc
	 		time_s=(time-time_h*3600)%60; //a marad�k a mp
	 		 
	 	}
	 	else 
	 	{
	 		time_s=time; //t�bbi �gyis 0
	 	}
	 
	 	
	 	
	 	//ez rakja �ssze a sz�mokat egy string�, pl: 0:00:00
	 	//STRING.FORMAT SZ�MOT K�R, NEM STRINGET!
	 	vissza=vissza.concat(String.valueOf(time_h)).concat(":").concat(String.format("%02d",time_min)).concat(":").concat(String.format("%02d",time_s));
	 	
	 	
	 	return vissza;
	 }
}
