package aknakeres�;

public abstract class Cell {
	private boolean marked=false; //megjel�lt-e
	private int adjacentNum=0;
	
	
	public boolean isMarked() {
		return marked;
	}
	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public int step()
	{
		return getAdjacentNum(); //ha nem akna akkor visszaadja a k�r�l�tte l�v� akn�kat
	}
	
	public int getAdjacentNum() {
		return adjacentNum;
	}
	public void setAdjacentNum(int adjacentNum) {
		this.adjacentNum = adjacentNum;
	}
	
}
