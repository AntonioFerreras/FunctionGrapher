package FunctionGrapher;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import javax.swing.*;
import ModernUI.MFrame;

public class Graph extends JPanel {
	//Used to shorten axis numbers
	private DecimalFormat df = new DecimalFormat("##.##");
	
	//G2 is used since it can do more things
	private Graphics2D g2;
	
	//Bounds of graph
	private int x, y, w, h;
	
	//UI size scale
	private float uiM = MFrame.getUiM();
	
	//Colors
	private Color outlineColor = MFrame.clBack;
	private Color backgroundColor = MFrame.clDkPane;
	
	//Number gap between grid lines
	private double bigTick = 1;
	
	//Strokes
	private int axisStroke = 2;
	private int gridStroke = 1;
	
	//Total range of graph (E.g. range=10, start=-5, end=5)
	private double range = 10;
	
	//Array of points
	private Point2D.Double[] points;
	
	//Other
	private static String PI_CHAR = "\u03C0";
	private boolean representAsMultipleOfPI = false;//Maybe will add this in the future
	
	public Graph(int xpos, int ypos, int width, int height, Point2D.Double[] pts) {
		x = scale(xpos);
		y = scale(ypos);
		w = scale(width+1);//Add one to allow border pixels to be drawn
		h = scale(height+1);
		
		setBackground(backgroundColor);
		setBounds(x, y, w, h);
		
		points = pts;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D) g;
		
		/*Draw grid and big tick numbers*/
		g2.setStroke(new BasicStroke(scaleOnlyUp(gridStroke)));
		g2.setFont(new Font("Consolas", Font.PLAIN, scale(15)));
		
		//Pixel gap between grid lines
		//int spaceBetweenBigTicks = scale(50);
		int spaceBetweenBigTicks = w/10;
		
		//Amount of grid ticks on graph
		int numOfBigTicksInRange = (int)(w/spaceBetweenBigTicks);
		
		//Make sure bigTick is in correct ratio with range
		bigTick = range/numOfBigTicksInRange;
		
		//Go through each grid line
		for(int i = 0; i <= numOfBigTicksInRange; i++) {
			/*Number to be drawn on graph at respective tick*/
			//String xNum = df.format(i*bigTick - range/2);
			//String yNum = df.format(range/2 - i*bigTick);
			
			//Map "i" to its position in range
			String xNum = df.format(map(i, 0, numOfBigTicksInRange, -range/2, range/2));
			String yNum = df.format(map(i, 0, numOfBigTicksInRange, range/2, -range/2));
			
			int fontWidth = (int) g2.getFontMetrics().getStringBounds(xNum, g2).getWidth();
			int fontHeight = (int) g2.getFontMetrics().getStringBounds(xNum, g2).getHeight();	
			
			//Draw lines
			g2.setColor(MFrame.cl120);
			g2.drawLine(0, i*spaceBetweenBigTicks, w, i*spaceBetweenBigTicks);//draw horz
			g2.drawLine(i*spaceBetweenBigTicks, 0, i*spaceBetweenBigTicks, h);//draw vert
			
			//Only draw numbers if not last on axis (avoid being cut-off)
			g2.setColor(MFrame.cl220);
			if(i != 0 && i*bigTick != range) {
				//Draw big tick numbers on X
				if(i != numOfBigTicksInRange/2) {
					if(representAsMultipleOfPI)//Not functioning in this version
						drawCenteredString(toPi(i*bigTick - range/2), i*spaceBetweenBigTicks, h/2+fontHeight);
					else
						drawCenteredString(xNum, i*spaceBetweenBigTicks, h/2+fontHeight);
				}
				
				//Draw big tick numbers on Y
				if(i != numOfBigTicksInRange/2)
					drawRightString(yNum, w/2-scale(7), i*spaceBetweenBigTicks+fontHeight/2);
				else
					drawRightString("0", w/2-scale(7), i*spaceBetweenBigTicks+fontHeight);//Draw zero once
			}
		}
		
		//Draw axis
		g2.setColor(MFrame.cl220);
		g2.setStroke(new BasicStroke(scaleOnlyUp(axisStroke)));
		g2.drawLine(0, h/2, w, h/2);//x
		g2.drawLine(w/2, 0, w/2, h);//y
		
		//Connect them with lines
		connectPoints();
	}
	
	private static String toPi(Double n) {
		//Not functioning in current version
		String out = "";
		if(n == Math.PI*1.0/5.0) {
			out = "1/5*" + PI_CHAR;
		} else {
			out = String.valueOf(n);
		}
		
		return out;
	}
	
	private void drawCenteredString(String s, int XPos, int YPos){
        int stringLen = (int)
        g2.getFontMetrics().getStringBounds(s, g2).getWidth();
        g2.drawString(s, XPos-stringLen/2, YPos);
	}
	
	private void drawRightString(String s, int XPos, int YPos){
        int stringLen = (int)
        g2.getFontMetrics().getStringBounds(s, g2).getWidth();
        g2.drawString(s, XPos-stringLen, YPos);
	}
	
	public void setRange(double range2) {
		range = range2;
	}
	
	private void connectPoints() {
		g2.setColor(MFrame.clLtBlue);
		g2.setStroke(new BasicStroke(scaleOnlyUp(2)));
		for(int i = 0; i < points.length; i++) {
			if(i != 0) {
				int xPreviousLocationOnGraph = (int)map(points[i-1].x, 0-range/2, range/2, 0, w);
				int yPreviousLocationOnGraph = (int)map(points[i-1].y, 0-range/2, range/2, h, 0);
				int xLocationOnGraph = (int)map(points[i].x, 0-range/2, range/2, 0, w);
				int yLocationOnGraph = (int)map(points[i].y, 0-range/2, range/2, h, 0);
				
				//Only connect this point and last if a real number
				if(!Double.isNaN(points[i].y) && !Double.isNaN(points[i-1].y)) {
					g2.drawLine(xPreviousLocationOnGraph, yPreviousLocationOnGraph, xLocationOnGraph, yLocationOnGraph);
				}
			}
		}
	}
	
	private double map(double n, double start1, double stop1, double start2, double stop2) {
		  return ((n-start1)/(stop1-start1))*(stop2-start2)+start2;
	}
	
	
	private int scale(int n) {
		return Math.round(n*uiM);
	}
	
	private int scaleOnlyUp(int n) {
		if(uiM > 1)
			return Math.round(n*uiM);
		else 
			return n;
	}
}
