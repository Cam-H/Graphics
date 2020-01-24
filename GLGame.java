package misc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GLGame {
	public static void main(String[] args){
		JFrame frame = new JFrame("Game of Life");
		Gameloop2 thread1 = new Gameloop2(frame);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		thread1.start();

	}

}

class Gameloop2 extends Thread{
	
	private static volatile JFrame frame;
	
	public Gameloop2(JFrame frame){
		this.frame = frame;
	}
	
	static boolean paused = false, modSpeedRed = false, drawing = true;
	static boolean[][]unit = new boolean[100][60];
	static boolean[][]unit2 = new boolean[100][60];
	static boolean[][]unit3 = new boolean[100][60];
	static int mx = -1, my = -1;
	static int locx = -1, locy = -1, speedRed = 5, unitIndex = 0;
	
	public void run(){
		frame.setSize(1000,700);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.addKeyListener(new baction());
		frame.addMouseListener(new clickaction());
		frame.addMouseMotionListener(new mousemotion());
		frame.setContentPane(new JPanel(){
			public void paintComponent(Graphics g){
				g.fillRect(0, 600, 1000, 700);
				for(int i = 0; i < 100; i++){
					g.drawLine(i*10, 0, i*10, 600);
				}
				for(int i = 0; i < 60; i++){
					g.drawLine(0, i*10, 1000, i*10);
				}
				g.setColor(Color.green);
				for(int i = 0; i < 100; i++){
					for(int j = 0; j < 60; j++){
						if(unit[i][j]){
							g.fillRect(i*10+1,j*10+1,9,9);
						}
					}
				}
				g.setColor(Color.red);
				for(int i = 0; i < 100; i++){
					for(int j = 0; j < 60; j++){
						if(unit2[i][j]){
							g.fillRect(i*10+1,j*10+1,9,9);
						}
					}
				}
				g.setColor(Color.blue);
				for(int i = 0; i < 100; i++){
					for(int j = 0; j < 60; j++){
						if(unit3[i][j]){
							g.fillRect(i*10+1,j*10+1,9,9);
						}
					}
				}
				g.setColor(Color.blue);
				
				if(paused){
					g.setColor(Color.red);
				}else{
					g.setColor(Color.blue);
				}
				g.fillRect(11, 611, 49, 49);
				g.setColor(Color.white);
				
				//Speed Reduction Graphics
				g.drawString("Speed Reduction", 175, 620);
				g.drawRect(70, 625, 300, 25);
				g.fillRect(70+(speedRed*5),625,10,40);
				g.drawString(String.valueOf(speedRed), 220, 640);
				g.drawRect(10, 610, 50, 50);
				
				//Drawing Graphics
				g.drawRect(400, 610, 25, 25);
				g.drawRect(400, 635, 25, 25);
				if(drawing){
					switch(unitIndex){
					case 0:
						g.setColor(Color.green);
						break;
					case 1:
						g.setColor(Color.red);
						break;
					case 2:
						g.setColor(Color.blue);
						break;
					}
					g.fillRect(401, 611, 24, 24);
				}else{
					g.setColor(Color.gray);
					g.fillRect(401, 636, 24, 24);
				}
				g.setColor(Color.white);
				g.drawLine(405, 615, 420, 630);
				g.drawLine(403, 613, 418, 628);
				g.drawRect(408, 640, 9, 15);
				
				//Arrays
				g.drawRect(440, 610, 150, 50);
				g.drawRect(490, 610, 50, 50);
				g.drawRect(600, 605, 180, 60);
				g.drawRect(600, 605, 60, 60);
				g.drawRect(660, 625, 120, 20);//dsa
				g.setColor(Color.gray);
				g.fillRect(601, 606, 59, 59);
				g.fillRect(800, 610, 55, 50);
				g.setColor(Color.green);
				g.fillRect(441, 611, 49, 49);
				g.fillRect(661, 606, 119, 19);
				g.setColor(Color.red);
				g.fillRect(491, 611, 49, 49);
				g.fillRect(661, 626, 119, 19);
				g.setColor(Color.blue);
				g.fillRect(541, 611, 49, 49);
				g.fillRect(661, 646, 119, 19);
				g.setColor(Color.white);
				g.drawOval(440+(50*unitIndex),610,50,50);
				g.drawRect(800, 610, 55, 50);
				g.drawRect(800, 610, 55, 25);
				
				
				



				
				g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
				g.drawString("P", 20, 650);//Pause Text
				g.drawString("Clear", 640, 650);//Clear Text
				
				g.setFont(new Font("TimesRoman", Font.PLAIN, 25));
				g.drawString("Save", 800, 630);
				g.drawString("Load", 800, 655);

				
				
				
			}
		});
		frame.repaint();
		int count = 0;
		while(true){
			locx = (int)frame.getLocation().getX();
			locy = (int)frame.getLocation().getY();
			count++;
			if(!paused && count > speedRed){
				
				unit = checkSurvival(unit, true);
				unit2 = checkSurvival(unit2, true);
				unit3 = checkSurvival(unit3, true);
				
				count = 0;
			}
			
			if(mx != -1){
				if(!drawing){
					unit[mx/10][my/10] = false;
					unit2[mx/10][my/10] = false;
					unit3[mx/10][my/10] = false;
				}
				switch(unitIndex){
				case 0:
					if(drawing){
						unit[mx/10][my/10] = true;
					}
					break;
				case 1:
					if(drawing){
						unit2[mx/10][my/10] = true;
					}
					break;
				case 2:
					if(drawing){
						unit3[mx/10][my/10] = true;
					}
					break;
				}
			}
			
			frame.repaint();

			try {
				Thread.sleep(1000/60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean[][]checkSurvival(boolean[][]unit, boolean allCollision){
	
		boolean[][] buffer = new boolean[100][60];
		
		for(int i = 0; i < buffer.length; i++){
			for(int j = 0; j < 60; j++){
				buffer[i][j] = unit[i][j];
			}
		}
		
		for(int i = 0; i < 100; i++){
			for(int j = 0; j < 60; j++){
				if(checkNeighbours(unit, i, j, allCollision, 0) < 2){
					buffer[i][j] = false;
				}
				if(checkNeighbours(unit, i, j, allCollision, 0) > 3){
					buffer[i][j] = false;
				}
				if(checkNeighbours(unit, i, j, allCollision, 0) == 3){
					buffer[i][j] = true;
				}
			}
				
		}
			
		return buffer;
	}
	public static int checkNeighbours(boolean[][]unit, int i, int j, boolean allCollision, int unitIndex){
		int count = 0;
		try{
			if(unit[i-1][j]){
				count++;
			}
		}catch(Exception e){}
		try{
			if(unit[i-1][j-1]){
				count++;
			}
		}catch(Exception e){}
		try{
			if(unit[i][j-1]){
				count++;
			}
		}catch(Exception e){}
		try{
			if(unit[i+1][j-1]){
				count++;
			}
		}catch(Exception e){}
		try{
			if(unit[i+1][j]){
				count++;
			}
		}catch(Exception e){}
		try{
			if(unit[i+1][j+1]){
				count++;
			}
		}catch(Exception e){}
		try{
			if(unit[i][j+1]){
				count++;
			}
		}catch(Exception e){}
		try{
			if(unit[i-1][j+1]){
				count++;
			}
		}catch(Exception e){}
		return count;
	}
	public static boolean[][] clear(){
		return new boolean[100][60];
	}
	public static void output(boolean[][]unit, String fileName){
		FileOutputStream fileHandle;
		PrintWriter outputFile;
		String line = "";
		
		try {
			fileHandle = new FileOutputStream(fileName);
			outputFile = new PrintWriter(fileHandle);
			
			for(int i = 0; i < 100; i++){
				for(int j = 0; j < 60; j++){
					line = line.concat(String.valueOf(unit[i][j]).concat(" "));
				}
				outputFile.println(line);
				line = "";
			}
			outputFile.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	public static boolean[][] input(boolean[][]unit, String fileName){
		FileInputStream file1 = null;//FileInputStream to be used
		BufferedReader in = null;//BufferedReader to be used
		String line = "";
		int i = 0;
		
		try{//Insures file path if found and initializes the necessary components if so
			file1 = new FileInputStream(fileName);//Sets up FileInputStream
			in = new BufferedReader(new InputStreamReader(file1));//Sets up BufferedReader
			while(in.ready() == true){
			line = in.readLine();
				for(int j = 0; j < 60; j++){
					try{
						unit[i][j] = Boolean.parseBoolean(line.substring(0,line.indexOf(" ")));
						line = line.substring(line.indexOf(" ")+1);
					}catch(Exception e){System.out.println("yyyyy "+i+" "+j);}
				}
				line = "";
				i++;
			}
		}catch(Exception e){
			System.out.println("Xxxxxxx "+i+" "+line);
			in = null;
		}
		
		return unit;
		
	}
}

class mousemotion implements MouseMotionListener{

	@Override
	public void mouseDragged(MouseEvent e) {
		if(e.getXOnScreen()-(Gameloop2.locx)-5 > 70 && e.getXOnScreen()-(Gameloop2.locx)-5 < 375 && Gameloop2.modSpeedRed){
			Gameloop2.speedRed = ((e.getXOnScreen()-(Gameloop2.locx)-70)-5)/5;
		}
		if(e.getXOnScreen()-(Gameloop2.locx)-5 > 0 && e.getXOnScreen()-(Gameloop2.locx)-5 < 1000 && e.getYOnScreen()-(Gameloop2.locy)-25 > 0 && e.getYOnScreen()-(Gameloop2.locy)-25 < 600){
			Gameloop2.mx = (e.getXOnScreen()-(Gameloop2.locx))-5;
			Gameloop2.my = (e.getYOnScreen()-(Gameloop2.locy))-25;
		}
		
	}
	
	@Override
	public void mouseMoved(MouseEvent arg0) {}
}
class clickaction implements MouseListener{

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getXOnScreen()-(Gameloop2.locx)-5 > 10 && e.getXOnScreen()-(Gameloop2.locx)-5 < 60 && e.getYOnScreen()-(Gameloop2.locy)-25 > 610 && e.getYOnScreen()-(Gameloop2.locy)-25 < 660){
			if(Gameloop2.paused){
				Gameloop2.paused = false;
			}else{
				Gameloop2.paused = true;
			}
		}
		if(e.getXOnScreen()-(Gameloop2.locx)-5 > 400 && e.getXOnScreen()-(Gameloop2.locx)-5 < 425 && e.getYOnScreen()-(Gameloop2.locy)-25 > 610 && e.getYOnScreen()-(Gameloop2.locy)-25 < 635){
			Gameloop2.drawing = true;
		}else if(e.getXOnScreen()-(Gameloop2.locx)-5 > 400 && e.getXOnScreen()-(Gameloop2.locx)-5 < 425 && e.getYOnScreen()-(Gameloop2.locy)-25 > 635 && e.getYOnScreen()-(Gameloop2.locy)-25 < 660){
			Gameloop2.drawing = false;
		}
		if(e.getXOnScreen()-(Gameloop2.locx)-5 > 440 && e.getXOnScreen()-(Gameloop2.locx)-5 < 491 && e.getYOnScreen()-(Gameloop2.locy)-25 > 610 && e.getYOnScreen()-(Gameloop2.locy)-25 < 660){
			Gameloop2.unitIndex = 0;
		}else if(e.getXOnScreen()-(Gameloop2.locx)-5 > 490 && e.getXOnScreen()-(Gameloop2.locx)-5 < 541 && e.getYOnScreen()-(Gameloop2.locy)-25 > 610 && e.getYOnScreen()-(Gameloop2.locy)-25 < 660){
			Gameloop2.unitIndex = 1;
		}else if(e.getXOnScreen()-(Gameloop2.locx)-5 > 540 && e.getXOnScreen()-(Gameloop2.locx)-5 < 591 && e.getYOnScreen()-(Gameloop2.locy)-25 > 610 && e.getYOnScreen()-(Gameloop2.locy)-25 < 660){
			Gameloop2.unitIndex = 2;
		}
		
		if(e.getXOnScreen()-(Gameloop2.locx)-5 > 600 && e.getXOnScreen()-(Gameloop2.locx)-5 < 660 && e.getYOnScreen()-(Gameloop2.locy)-25 > 605 && e.getYOnScreen()-(Gameloop2.locy)-25 < 665){
			Gameloop2.unit = Gameloop2.clear();
			Gameloop2.unit2 = Gameloop2.clear();
			Gameloop2.unit3 = Gameloop2.clear();
		}else if(e.getXOnScreen()-(Gameloop2.locx)-5 > 660 && e.getXOnScreen()-(Gameloop2.locx)-5 < 780 && e.getYOnScreen()-(Gameloop2.locy)-25 > 605 && e.getYOnScreen()-(Gameloop2.locy)-25 < 625){
			Gameloop2.unit = Gameloop2.clear();
		}else if(e.getXOnScreen()-(Gameloop2.locx)-5 > 660 && e.getXOnScreen()-(Gameloop2.locx)-5 < 780 && e.getYOnScreen()-(Gameloop2.locy)-25 > 625 && e.getYOnScreen()-(Gameloop2.locy)-25 < 645){
			Gameloop2.unit2 = Gameloop2.clear();
		}else if(e.getXOnScreen()-(Gameloop2.locx)-5 > 660 && e.getXOnScreen()-(Gameloop2.locx)-5 < 780 && e.getYOnScreen()-(Gameloop2.locy)-25 > 645 && e.getYOnScreen()-(Gameloop2.locy)-25 < 665){
			Gameloop2.unit3 = Gameloop2.clear();
		}
		
		
		if(e.getXOnScreen()-(Gameloop2.locx)-5 > 800 && e.getXOnScreen()-(Gameloop2.locx)-5 < 855 && e.getYOnScreen()-(Gameloop2.locy)-25 > 610 && e.getYOnScreen()-(Gameloop2.locy)-25 < 635){
			//save
			Gameloop2.output(Gameloop2.unit, "src\\unit.txt");
			Gameloop2.output(Gameloop2.unit2, "src\\unit2.txt");
			Gameloop2.output(Gameloop2.unit3, "src\\unit3.txt");
		}else if(e.getXOnScreen()-(Gameloop2.locx)-5 > 800 && e.getXOnScreen()-(Gameloop2.locx)-5 < 855 && e.getYOnScreen()-(Gameloop2.locy)-25 > 635 && e.getYOnScreen()-(Gameloop2.locy)-25 < 660){
			//load
			Gameloop2.unit = Gameloop2.input(Gameloop2.unit, "src\\unit.txt");
			Gameloop2.unit2 = Gameloop2.input(Gameloop2.unit2, "src\\unit2.txt");
			Gameloop2.unit3 = Gameloop2.input(Gameloop2.unit3, "src\\unit3.txt");
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if((e.getXOnScreen()-(Gameloop2.locx))-5 < 1000 && (e.getYOnScreen()-(Gameloop2.locy))-25 < 600){
			Gameloop2.mx = (e.getXOnScreen()-(Gameloop2.locx))-5;
			Gameloop2.my = (e.getYOnScreen()-(Gameloop2.locy))-25;
		}
		if(e.getXOnScreen()-(Gameloop2.locx)-5 > 70 && e.getXOnScreen()-(Gameloop2.locx)-5 < 370 && e.getYOnScreen()-(Gameloop2.locy)-25 > 625 && e.getYOnScreen()-(Gameloop2.locy)-25 < 650){
			Gameloop2.modSpeedRed = true;
		}

		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		Gameloop2.mx = -1;
		Gameloop2.my = -1;
		
		Gameloop2.modSpeedRed = false;
		
	}
	
}

class baction implements KeyListener{


	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_P){
			if(Gameloop2.paused == false){
				Gameloop2.paused = true;
			}else{
				Gameloop2.paused = false;
			}
		}		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}
	
}