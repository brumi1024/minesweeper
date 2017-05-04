package aknakeresõ;

public abstract class Cell {
	private boolean marked=false; //megjelölt-e
	private int adjacentNum=0;
	
	
	public boolean isMarked() {
		return marked;
	}
	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public int step()
	{
		return getAdjacentNum(); //ha nem akna akkor visszaadja a körülötte lévõ aknákat
	}
	
	public int getAdjacentNum() {
		return adjacentNum;
	}
	public void setAdjacentNum(int adjacentNum) {
		this.adjacentNum = adjacentNum;
	}
	
}
