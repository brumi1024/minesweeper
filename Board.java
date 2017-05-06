package aknakeresõ;

import java.util.*;
import java.lang.Math;

public class Board {
	
	private short[][] board; // nxn-es mátrix, az elemek az egyes cellákat jelölik. Itt az elemek csak indexek, a cellatulajdonságokat az mezõk tárolják
	private boolean[][] isMine; //bombák helye
	private int boardWidth; //szélesség 
	private int boardHeight; //magasság
	private int numOfMines; //aknaszám
	private int mineLeft; //maradék bomba
	private boolean isSingle; //egy vagy többjátékos mód
	List<Cell> cells=new ArrayList<Cell>(); //ebben a listában lesznek a mezõk
	
	public void setBoardParameters(String difficulty)
	{
		if(difficulty == "easy")
		{
			this.setBoardSize(5,5);
			this.setNumOfMines(4);		
		}
		if(difficulty == "medium")
		{
			this.setBoardSize(5,9);
			this.setNumOfMines(5);
		}
		if(difficulty == "hard") 
		{
			this.setBoardSize(9,9);
			this.setNumOfMines(10);
		}
	}
	
	public void createBoards(String difficulty) //pályainicializálás
	{
		setBoardParameters(difficulty); //kitölti a pályaadatokat a nehézség alapján
		board=new short[boardWidth][boardHeight]; //tömb inicializálás
		isMine=new boolean[boardWidth][boardHeight]; //tömb inicializálás
		int i=0;
		int j=0;
		for(i=0; i<boardWidth; i++)
		{
			for(j=0; j<boardHeight; j++)
			{
				this.board[i][j]=(short) (j*boardWidth+i); //indexmátrix
			}
		}
		
		for(i=0; i<boardWidth; i++)
		{
			for(j=0; j<boardHeight; j++)
			{
				this.isMine[i][j]=false; //akna mátrix üres
			}
		}
		
		this.placeMines(numOfMines, isMine); //feltöltjük
		this.createCells(); //megcsinálja a cellákat
		
	}
	
	public int getBoardWidth() { 
		return boardWidth;
	}
	private void setBoardWidth(int boardWidth) { 
		this.boardWidth = boardWidth;
	}
	public int getBoardHeight() {
		return boardHeight;
	}
	private void setBoardHeight(int boardHeight) { 
		this.boardHeight = boardHeight;
	}
	public int getNumOfMines() {
		return numOfMines;
	}
	public void setNumOfMines(int numOfMines) { 
		this.numOfMines = numOfMines;
	}
	public int getMineLeft() {
		return mineLeft;
	}
	public void setMineLeft(int mineLeft) { 
		this.mineLeft = mineLeft;
	}
	public boolean isSingle() {
		return isSingle;
	}
	public void setSingle(boolean isSingle) {
		this.isSingle = isSingle;
	}
	
	public void setBoardSize(int x, int y) { 
		setBoardHeight(x);
		setBoardWidth(y);
	}
	
	public void placeMines(int numOfMines, boolean[][] isMine)
	{
		
		
		int j=0; //segédváltozó
		Random rand=new Random();
		int k=0; //segédváltozó
		k=rand.nextInt(boardHeight*boardWidth);  //0-tól a pályaméretig generálunk egy véletlen számot
		k=(int) Math.floor(k); //lefelé kerekítés
		while(j<(numOfMines+1))
		{
			if(this.isMine[(boardHeight/k+1)][boardWidth%k]==false) //megnézzük nem-e foglalt helyre akarunk tenni
			{
				this.isMine[(boardHeight/k+1)][boardWidth%k]=true; //az adott helyet akna lesz
				j++; //számlálót növeljük
			}
		}
	}

	
	public int adjacentMines(int x, int y) //hány szomszédos bomba van
	{
		int adjacentMinesNum=0;
		
		if(x!=0) //nem szélsõ
		{
			if(isMine[x-1][y]==true)
			{
				adjacentMinesNum++;
			}
		}
		
		if(x!=boardHeight) //nem szélsõ
		{
			if(isMine[x+1][y]==true)
			{
				adjacentMinesNum++;
			}
		}
		
		if(y!=0) //nem szélsõ
		{
			if(isMine[x][y-1]==true)
			{
				adjacentMinesNum++;
			}
		}
		
		if(y!=boardWidth) //nem szélsõ
		{
			if(isMine[x][y+1]==true)
			{
				adjacentMinesNum++;
			}
		}
		
		if(x!=0 && y!=0) //nem szélsõ
		{
			if(isMine[x-1][y-1]==true)
			{
				adjacentMinesNum++;
			}
		}
		
		if(x!=boardHeight && y!=0) //nem szélsõ
		{
			if(isMine[x+1][y-1]==true)
			{
				adjacentMinesNum++;
			}
		}
		
		if(x!=0 && y!=boardWidth) //nem szélsõ
		{
			if(isMine[x-1][y+1]==true)
			{
				adjacentMinesNum++;
			}
		}
		
		if(x!=boardHeight && y!=boardWidth) //nem szélsõ
		{
			if(isMine[x+1][y+1]==true)
			{
				adjacentMinesNum++;
			}
		}
		return adjacentMinesNum;
	}
	
	public void createCells()
	{
		for(int i=0; i<boardWidth; i++)
		{
			for(int j=0; j<boardHeight; j++)
			{
				if(this.isMine[i][j]==true)
				{
					Mine newCell= new Mine();
					cells.add(j*boardWidth+1, newCell); //berakjuk a listába
				}
				else 
				{
					Nothing newCell= new Nothing();
					newCell.setAdjacentNum(adjacentMines(i,j)); //rögtön bele is írjuk hogy hány szomszédja van
					cells.add(j*boardWidth+1, newCell); //berakjuk a listába
				}
					
			}
		}
		
	}
	
	
}
