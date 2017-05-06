package aknakeres�;

import java.util.*;
import java.lang.Math;

public class Board {
	
	private short[][] board; // nxn-es m�trix, az elemek az egyes cell�kat jel�lik. Itt az elemek csak indexek, a cellatulajdons�gokat az mez�k t�rolj�k
	private boolean[][] isMine; //bomb�k helye
	private int boardWidth; //sz�less�g 
	private int boardHeight; //magass�g
	private int numOfMines; //aknasz�m
	private int mineLeft; //marad�k bomba
	private boolean isSingle; //egy vagy t�bbj�t�kos m�d
	List<Cell> cells=new ArrayList<Cell>(); //ebben a list�ban lesznek a mez�k
	
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
	
	public void createBoards(String difficulty) //p�lyainicializ�l�s
	{
		setBoardParameters(difficulty); //kit�lti a p�lyaadatokat a neh�zs�g alapj�n
		board=new short[boardWidth][boardHeight]; //t�mb inicializ�l�s
		isMine=new boolean[boardWidth][boardHeight]; //t�mb inicializ�l�s
		int i=0;
		int j=0;
		for(i=0; i<boardWidth; i++)
		{
			for(j=0; j<boardHeight; j++)
			{
				this.board[i][j]=(short) (j*boardWidth+i); //indexm�trix
			}
		}
		
		for(i=0; i<boardWidth; i++)
		{
			for(j=0; j<boardHeight; j++)
			{
				this.isMine[i][j]=false; //akna m�trix �res
			}
		}
		
		this.placeMines(numOfMines, isMine); //felt�ltj�k
		this.createCells(); //megcsin�lja a cell�kat
		
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
		
		
		int j=0; //seg�dv�ltoz�
		Random rand=new Random();
		int k=0; //seg�dv�ltoz�
		k=rand.nextInt(boardHeight*boardWidth);  //0-t�l a p�lyam�retig gener�lunk egy v�letlen sz�mot
		k=(int) Math.floor(k); //lefel� kerek�t�s
		while(j<(numOfMines+1))
		{
			if(this.isMine[(boardHeight/k+1)][boardWidth%k]==false) //megn�zz�k nem-e foglalt helyre akarunk tenni
			{
				this.isMine[(boardHeight/k+1)][boardWidth%k]=true; //az adott helyet akna lesz
				j++; //sz�ml�l�t n�velj�k
			}
		}
	}

	
	public int adjacentMines(int x, int y) //h�ny szomsz�dos bomba van
	{
		int adjacentMinesNum=0;
		
		if(x!=0) //nem sz�ls�
		{
			if(isMine[x-1][y]==true)
			{
				adjacentMinesNum++;
			}
		}
		
		if(x!=boardHeight) //nem sz�ls�
		{
			if(isMine[x+1][y]==true)
			{
				adjacentMinesNum++;
			}
		}
		
		if(y!=0) //nem sz�ls�
		{
			if(isMine[x][y-1]==true)
			{
				adjacentMinesNum++;
			}
		}
		
		if(y!=boardWidth) //nem sz�ls�
		{
			if(isMine[x][y+1]==true)
			{
				adjacentMinesNum++;
			}
		}
		
		if(x!=0 && y!=0) //nem sz�ls�
		{
			if(isMine[x-1][y-1]==true)
			{
				adjacentMinesNum++;
			}
		}
		
		if(x!=boardHeight && y!=0) //nem sz�ls�
		{
			if(isMine[x+1][y-1]==true)
			{
				adjacentMinesNum++;
			}
		}
		
		if(x!=0 && y!=boardWidth) //nem sz�ls�
		{
			if(isMine[x-1][y+1]==true)
			{
				adjacentMinesNum++;
			}
		}
		
		if(x!=boardHeight && y!=boardWidth) //nem sz�ls�
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
					cells.add(j*boardWidth+1, newCell); //berakjuk a list�ba
				}
				else 
				{
					Nothing newCell= new Nothing();
					newCell.setAdjacentNum(adjacentMines(i,j)); //r�gt�n bele is �rjuk hogy h�ny szomsz�dja van
					cells.add(j*boardWidth+1, newCell); //berakjuk a list�ba
				}
					
			}
		}
		
	}
	
	
}
