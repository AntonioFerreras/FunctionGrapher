package ModernUI;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
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

public class MOptionPane {

	private JFrame frame;
	private JPanel backp;
	private MButton2 btnX;
	private int X, Y, frameX, frameY;
	private float uiM = 1.0f;
	private boolean maximized = false;
	private ActionListener closeAction;

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

	public MOptionPane(String n, JPanel p) {
		/*
		 * Size of panel is already scaled
		 */
		uiM = MFrame.getUiM();
		int w = p.getWidth()+scale(30);
		int h = p.getHeight()+scale(40+15);
		
		// Create Frame
		frame = new JFrame(n);
		frame.setSize(w, h);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setLocationRelativeTo(null);
		//frame.setAlwaysOnTop(true);
		
		// Create background panel
		backp = new JPanel();
		backp.setBounds(0, 0, w * 60, h * 60);
		backp.setBackground(clBack);
		backp.setLayout(null);
		frame.add(backp);

		// Create panel tat allows user to move window
		JPanel topp = new JPanel();
		topp.setBounds(0, 0, w * 60, scale(40));
		topp.setBackground(clBack);
		topp.setLayout(null);
		backp.add(topp);
		
		JLabel lblTitle = new JLabel(n);
		lblTitle.setFont(new Font("Microsoft Tai Le", Font.PLAIN, scale(16)));
		lblTitle.setForeground(cl250);
		lblTitle.setBounds(scale(15), scale(5), scale(200), scale(35));
		topp.add(lblTitle);


		MouseListener ml = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated metod stub
				X = e.getX();
				Y = e.getY();
			}
		};
		// Used for moving window
		MouseMotionListener mml = new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated metod stub
					frameX = frame.getX();
					frame.getY();
					frame.setLocation(e.getXOnScreen() - X, e.getYOnScreen() - Y);

			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated metod stub
			}
		};
		topp.addMouseListener(ml);
		topp.addMouseMotionListener(mml);

		closeAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		};
		
		// Create Close button
		btnX = new MButton2("x", -55, 10, 40, 20, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeAction.actionPerformed(null);
				
			}
		});
		btnX.setFont(new Font("Microsoft Tai Le", Font.BOLD, scale(18)));
		//btnX.setFont(new Font("Arial Unicode MS", Font.BOLD, scale(19)));
		btnX.setBackgroundColor(clCrimson);
		topp.add(btnX.getButton());
		btnX.setRelativeTo(unScale(w), 0);
		
		
		p.setLocation(scale(15), topp.getHeight());
		backp.add(p);
		
		frame.setVisible(true);
	}
	
	private int scale(int n) {
		return (int) (n * uiM);
	}

	private int unScale(int n) {
		return (int) (n / uiM);
	}

	public void setCloseAction(ActionListener action) {
		closeAction = action;
	}
	
	public JFrame getFrame() {
		return frame;
	}

	public JPanel getBackP() {
		return backp;
	}
}
