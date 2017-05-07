package minesweeperSinglePlayer.level;

public class Nothing extends Cell {
	public int step()
	{
		return super.getAdjacentNum();
	}
}
