package ModernUI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

public class MButton2 {
	private JButton button;
	private int X, Y, W, H, originX, originY;
	private int BORDER_SIZE = 2;
	private int FONT_SIZE = 18;
	private Font FONT;

	// Basic colors
	private Color clPane = new Color(68, 73, 82);
	private Color clDkPane = new Color(60, 65, 74);
	private Color clBack = new Color(46, 49, 56);
	private Color cl250 = new Color(250, 250, 250);
	private Color cl220 = new Color(220, 220, 220);
	private Color clCrimson = new Color(191, 75, 52, 255);
	private Color clLtBlue = new Color(69, 173, 237, 255);
	private Color clGreen = new Color(111, 201, 81, 255);
	private Color clYellow = new Color(198, 153, 37);
	
	private Color backColor = clLtBlue;
	
	public MButton2(String n, int x, int y, int w, int h, ActionListener al) {
		
		originX = 0;
		originY = 0;
		
		FONT = new Font("Microsoft JhengHei UI Light", Font.BOLD, scale(FONT_SIZE));
		
		X = x;
		Y = y;
		W = w;
		H = h;
		
		button=new JButton();
		button.setText(n);
		button.setOpaque(true);
		button.setFocusPainted(false);
		button.setBounds(scale(originX + X), scale(originY + Y), scale(W), scale(H));
		button.setFont(FONT);
		button.setForeground(cl250);
		button.setBackground(backColor);
		button.setBorder(new LineBorder(cl250, 0));
		button.addActionListener(al);
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(backColor.darker());
				button.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(backColor);
				button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
	}
	
	public int getX() {
		return button.getX();
	}
	
	public int getY() {
		return button.getY();
	}
	
	private int scale(int n) {
		return (int) (n * MFrame.getUiM());
	}
	
	public void setRelativeTo(int xoff, int yoff) {
		originX = xoff;
		originY = yoff;
		reScale();
	}
	
	public void setBackgroundColor(Color cl) {
		backColor = cl;
		button.setBackground(backColor);
	}
	
	
	public void setText(String t) {
		button.setText(t);
	}
	
	public String getText() {
		return button.getText();
	}
	
	public void setFont(Font f) {
		FONT = f;
		button.setFont(FONT);
	}
	
	
	public Font getFont() {
		return FONT;
	}
	
	public void setSize(int w, int h) {
		W = w;
		H = h;
		button.setSize(scale(W) , scale(H));
	}
	
	public void setBounds(int x, int y, int w, int h) {
		X = x;
		Y = y;
		W = w;
		H = h;
		button.setBounds(scale(originX + X), scale(originY + Y), scale(W) , scale(H));
	}
	
	public Rectangle getBounds() {
		return button.getBounds();
	}
	
	public void setVisible(boolean b) {
		button.setVisible(b);
	}
	
	public boolean getVisible() {
		return button.isVisible();
	}
	
	public void setEnabled(boolean b) {
		button.setEnabled(b);
	}
	
	public boolean getEnabled() {
		return button.isEnabled();
	}
	
	public void reScale() {
		button.setFont(FONT);
		button.setBounds(scale(originX + X), scale(originY + Y), scale(W) , scale(H));
	}
	
	public JButton getButton() {
		return button;
	}
}
