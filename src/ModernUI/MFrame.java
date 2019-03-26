package ModernUI;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MFrame {

	private JFrame frame;
	private JPanel backp, topp, botp, rightp, leftp, botrightp, botleftp;
	private JLabel lblTitle;
	private MButton2 btnMinimize, btnMaximize, btnX;
	private int X, Y, effectiveScreenWidth, effectiveScreenHeight, frameX, frameY;
	private int WINDOW_WIDTH, START_WINDOW_WIDTH, MINIMUM_WINDOW_WIDTH;//Unscaled
	private int WINDOW_HEIGHT, START_WINDOW_HEIGHT, MINIMUM_WINDOW_HEIGHT;//Unscaled
	private boolean maximized = false;
	private static float uiM;
	private ActionListener uiReCalc, closeAction;

	// Basic colors
	public static Color clPane = new Color(68, 73, 82);
	public static Color clDkPane = new Color(60, 65, 74);
	public static Color clBack = new Color(46, 49, 56);
	public static Color cl250 = new Color(250, 250, 250);
	public static Color cl220 = new Color(220, 220, 220);
	public static Color cl120 = new Color(120, 120, 120);
	public static Color clCrimson = new Color(191, 75, 52, 255);
	public static Color clLtBlue = new Color(69, 173, 237, 255);
	public static Color clGreen = new Color(111, 201, 81, 255);
	public static Color clYellow = new Color(198, 153, 37);

	public MFrame(String n, int w, int h, float uim, ActionListener action) {
		setUiM(uim);
		uiReCalc = action;
		
		effectiveScreenWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
		effectiveScreenHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;

		START_WINDOW_WIDTH = w;
		START_WINDOW_HEIGHT = h;

		WINDOW_WIDTH = START_WINDOW_WIDTH;
		WINDOW_HEIGHT = START_WINDOW_HEIGHT;

		MINIMUM_WINDOW_WIDTH = 760;
		MINIMUM_WINDOW_HEIGHT = 200;
		
		closeAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};

		// Create Frame
		frame = new JFrame(n);
		frame.setSize(scale(START_WINDOW_WIDTH), scale(START_WINDOW_HEIGHT));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);

		// Create background panel
		backp = new JPanel();
		backp.setBounds(0, 0, START_WINDOW_WIDTH * 60, START_WINDOW_HEIGHT * 60);
		backp.setBackground(clBack);
		backp.setLayout(null);
		frame.add(backp);
		
		//Center Frame on screen
		frame.setLocationRelativeTo(null);

		// Create panel that allows user to move window
		topp = new JPanel();
		topp.setBounds(0, 0, START_WINDOW_WIDTH * 60, scale(40));
		topp.setBackground(clBack);
		topp.setLayout(null);
		backp.add(topp);
		
		//Window title
		lblTitle = new JLabel(n);
		lblTitle.setFont(new Font("Microsoft Tai Le", Font.PLAIN, scale(16)));
		lblTitle.setForeground(cl250);
		lblTitle.setBounds(scale(15), scale(5), scale(310), scale(35));
		topp.add(lblTitle);

		botp = new JPanel();
		botp.setBounds(scale(15), scale(START_WINDOW_HEIGHT - 7), scale(START_WINDOW_WIDTH - 30), scale(7));
		botp.setBackground(clBack);
		botp.setLayout(null);
		botp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				botp.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				X = e.getX();
				Y = e.getY();
			}
		});
		botp.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				frame.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
				
				int resizedHeight = Math.abs(Y - (e.getYOnScreen() - frame.getLocationOnScreen().y));
				
				if (maximized == false && resizedHeight > scale(MINIMUM_WINDOW_HEIGHT)) {
					//Set frame height to new, resized height
					frame.setSize(scale(WINDOW_WIDTH), resizedHeight);
					//update window height variable
					WINDOW_HEIGHT = unScale(resizedHeight);
					START_WINDOW_HEIGHT = WINDOW_HEIGHT;
					reCalcComponents();
				}
			}
		});
		backp.add(botp);

		rightp = new JPanel();
		rightp.setBounds(scale(START_WINDOW_WIDTH - 5), scale(15), scale(5), scale(START_WINDOW_HEIGHT - 30));
		rightp.setBackground(clBack);
		rightp.setLayout(null);
		rightp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				rightp.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				X = e.getX();
				Y = e.getY();
			}
		});
		rightp.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				frame.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
				
				int resizedWidth = Math.abs(X - (e.getXOnScreen() - frame.getLocationOnScreen().x));
				
				if (maximized == false && resizedWidth > scale(MINIMUM_WINDOW_WIDTH)) {
					//Update frame width
					frame.setSize(resizedWidth, scale(WINDOW_HEIGHT));
					
					WINDOW_WIDTH = unScale(resizedWidth);
					START_WINDOW_WIDTH = WINDOW_WIDTH;
					
					//recalculate component coordinates
					reCalcComponents();
				}
			}
		});
		backp.add(rightp);

		leftp = new JPanel();
		leftp.setBounds(0, scale(15), scale(5), scale(START_WINDOW_HEIGHT - 30));
		leftp.setBackground(clBack);
		leftp.setLayout(null);
		leftp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				leftp.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				X = 0;
				Y = e.getY();
			}
		});
		leftp.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				frame.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				
				int resizedWidth = frame.getWidth() + (X - (e.getXOnScreen() - frame.getLocationOnScreen().x));
				
				if (maximized == false && resizedWidth > scale(MINIMUM_WINDOW_WIDTH)) {
					//Update size and location of frame
					frame.setSize(resizedWidth, scale(WINDOW_HEIGHT));
					frame.setLocation(e.getXOnScreen(), frame.getY());
					
					//update window width variable
					WINDOW_WIDTH = unScale(resizedWidth);
					START_WINDOW_WIDTH = WINDOW_WIDTH;
					
					//update component coordinates
					reCalcComponents();
				}
			}
		});
		backp.add(leftp);

		botrightp = new JPanel();
		botrightp.setBounds(scale(START_WINDOW_WIDTH - 15), scale(START_WINDOW_HEIGHT - 15), scale(15), scale(15));
		botrightp.setBackground(clBack);
		botrightp.setLayout(null);
		botrightp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				botrightp.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				X = e.getX();
				Y = e.getY();
			}
		});
		botrightp.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				frame.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
				
				//Pixel size of frame when mouse is dragged
				int resizedWidth = Math.abs(X - (e.getXOnScreen() - frame.getLocationOnScreen().x));
				int resizedHeight = Math.abs(Y - (e.getYOnScreen() - frame.getLocationOnScreen().y));
				
				if(!maximized) {
					if (resizedWidth > scale(MINIMUM_WINDOW_WIDTH)) {
						//update frame width and variables
						frame.setSize(resizedWidth, scale(WINDOW_HEIGHT));
						WINDOW_WIDTH = unScale(resizedWidth);
						START_WINDOW_WIDTH = WINDOW_WIDTH;
						
						//Recalculate component coordinates
						reCalcComponents();
					}
					
					if (resizedHeight > scale(MINIMUM_WINDOW_HEIGHT)) {
						//update frame width and variables
						frame.setSize(scale(WINDOW_WIDTH), resizedHeight);
						WINDOW_HEIGHT = unScale(resizedHeight);
						START_WINDOW_HEIGHT = WINDOW_HEIGHT;
						
						//Recalculate component coordinates
						reCalcComponents();
					}
				}
			}
		});
		backp.add(botrightp);
		
		botleftp = new JPanel();
		botleftp.setBounds(0, scale(START_WINDOW_HEIGHT - 15), scale(15), scale(15));
		botleftp.setBackground(clBack);
		botleftp.setLayout(null);
		botleftp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				botleftp.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				X = 0;
				Y = e.getY();
			}
		});
		botleftp.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				frame.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
				if (maximized == false) {
					int resizedWidth = frame.getWidth() + (X - (e.getXOnScreen() - frame.getLocationOnScreen().x));
					if(resizedWidth > scale(MINIMUM_WINDOW_WIDTH)) {
						//Update frame size and location
						frame.setSize(resizedWidth, scale(WINDOW_HEIGHT));
						frame.setLocation(e.getXOnScreen(), frame.getY());
						
						//Update variables
						WINDOW_WIDTH = unScale(resizedWidth);
						WINDOW_HEIGHT = unScale(frame.getHeight());
						
						START_WINDOW_WIDTH = WINDOW_WIDTH;
						START_WINDOW_HEIGHT = WINDOW_HEIGHT;
						
						//Update component coordinates
						reCalcComponents();
					}
					
					int resizedHeight = Math.abs(Y - (e.getYOnScreen() - frame.getLocationOnScreen().y));
					if (resizedHeight > scale(MINIMUM_WINDOW_HEIGHT)) {
						//Update frame size
						frame.setSize(frame.getWidth(), resizedHeight);
						
						//Update variables
						WINDOW_WIDTH = unScale(frame.getWidth());
						WINDOW_HEIGHT = unScale(frame.getHeight());
						
						START_WINDOW_WIDTH = WINDOW_WIDTH;
						START_WINDOW_HEIGHT = WINDOW_HEIGHT;
						
						//Update component coordinates
						reCalcComponents();
					}
				}
			}
		});
		backp.add(botleftp);

		MouseListener ml = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				X = e.getX();
				Y = e.getY();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				//Keep top of window visible
				if (frameY < 0)
					frame.setLocation(frameX, 0);
				
				//Snap window to side of screen
				if(e.getXOnScreen() == 0) {
					frame.setSize((int)(effectiveScreenWidth/2), effectiveScreenHeight);
					frame.setLocation(0,0);
					
					WINDOW_WIDTH = unScale(frame.getWidth());
					WINDOW_HEIGHT = unScale(frame.getHeight());
					
					reCalcComponents();
				} else if (e.getXOnScreen() == effectiveScreenWidth-1) {
					frame.setSize((int)(effectiveScreenWidth/2), effectiveScreenHeight);
					frame.setLocation((int)(effectiveScreenWidth/2),0);
					
					WINDOW_WIDTH = unScale(frame.getWidth());
					WINDOW_HEIGHT = unScale(frame.getHeight());
					
					reCalcComponents();
				}
			}
		};
		
		// Used for moving window
		MouseMotionListener mml = new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (maximized == false) {
					frameX = frame.getX();
					frameY = frame.getY();
					//Move window
					frame.setLocation(e.getXOnScreen() - X, e.getYOnScreen() - Y);
				}
			}
		};
		topp.addMouseListener(ml);
		topp.addMouseMotionListener(mml);

		// Create Close button
		btnX = new MButton2("x", -55, 10, 40, 20, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeAction.actionPerformed(null);
			}
		});
		btnX.setFont(new Font("Microsoft Tai Le", Font.BOLD, scale(18)));
		btnX.setBackgroundColor(clCrimson);
		topp.add(btnX.getButton());
		btnX.setRelativeTo(START_WINDOW_WIDTH, 0);

		// Create maximize button
		btnMaximize = new MButton2("\u25A1", -105, 10, 40, 20, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (maximized == false) {
					frame.setLocation(0, 0);
					frame.setSize(effectiveScreenWidth, effectiveScreenHeight);
					WINDOW_WIDTH = unScale(frame.getWidth());
					WINDOW_HEIGHT = unScale(frame.getHeight());
					reCalcComponents();
					maximized = true;
				} else {
					frame.setLocation(0, 0);
					frame.setSize(scale(START_WINDOW_WIDTH), scale(START_WINDOW_HEIGHT));
					WINDOW_WIDTH = unScale(frame.getWidth());
					WINDOW_HEIGHT = unScale(frame.getHeight());
					reCalcComponents();
					maximized = false;
				}
			}
		});
		btnMaximize.setRelativeTo(START_WINDOW_WIDTH, 0);
		btnMaximize.setBackgroundColor(clGreen);
		btnMaximize.setFont(new Font("Consolas", Font.BOLD, scale(22)));
		topp.add(btnMaximize.getButton());

		// Create minimize button
		btnMinimize = new MButton2("-", -155, 10, 40, 20, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setState(Frame.ICONIFIED);
			}
		});
		btnMinimize.setRelativeTo(START_WINDOW_WIDTH, 0);
		btnMinimize.setFont(new Font("Microsoft Tai Le", Font.BOLD, scale(38)));
		btnMinimize.setBackgroundColor(clYellow);
		topp.add(btnMinimize.getButton());
	}

	private int scale(int n) {
		return (int) (n * getUiM());
	}

	private int unScale(int n) {
		return (int) (n / getUiM());
	}
	
	public void makeNotResizable() {
		topp.remove(btnMaximize.getButton());
		btnMinimize.setBounds(-105, 10, 40, 20);
		backp.remove(botp);
		backp.remove(rightp);
		backp.remove(leftp);
		backp.remove(botrightp);
		backp.remove(botleftp);
	}
	
	public void setCloseAction(ActionListener action) {
		closeAction = action;
	}

	public JFrame getFrame() {
		return frame;
	}
	
	public void setTitle(String text) {
		lblTitle.setText(text);
	}

	public JPanel getBackP() {
		return backp;
	}

	public int getStartWidth() {
		return START_WINDOW_WIDTH;
	}

	public int getCurrentWidth() {
		return WINDOW_WIDTH;
	}

	public int getCurrentHeight() {
		return WINDOW_HEIGHT;
	}

	public static float getUiM() {
		return uiM;
	}

	public void setUiM(float uiM) {
		this.uiM = uiM;
	}
	
	public void add(JComponent c) {
		backp.add(c);
	}
	
	public void setVisible(boolean v) {
		frame.setVisible(v);
	}

	private void reCalcComponents() {
		uiReCalc.actionPerformed(null);
		btnX.setRelativeTo(WINDOW_WIDTH, 0);
		btnMaximize.setRelativeTo(WINDOW_WIDTH, 0);
		btnMinimize.setRelativeTo(WINDOW_WIDTH, 0);
		botp.setBounds(scale(15), scale(WINDOW_HEIGHT - 7), scale(WINDOW_WIDTH - 30), scale(7));
		rightp.setBounds(scale(WINDOW_WIDTH - 5), scale(15), scale(5), scale(WINDOW_HEIGHT - 30));
		leftp.setBounds(0, scale(15), scale(5), scale(WINDOW_HEIGHT - 30));
		botrightp.setBounds(scale(WINDOW_WIDTH - 15), scale(WINDOW_HEIGHT - 15), scale(15), scale(15));
		botleftp.setBounds(0, scale(WINDOW_HEIGHT - 15), scale(15), scale(15));
	}
}
