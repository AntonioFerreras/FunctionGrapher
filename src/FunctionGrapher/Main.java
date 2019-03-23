package FunctionGrapher;

import javax.script.ScriptEngineManager;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;

import ModernUI.MButton1;
import ModernUI.MButton2;
import ModernUI.MFrame;
import ModernUI.MOptionPane;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Main {
	private static MFrame frame;
	private static JPanel p, splittingP, splittingP2;

	private static Graph graph;

	private static float uiM = 1.0f;
	private static int w, h;
	private static int graphWidth = 500;
	private static String PI_CHAR = "\u03C0";

	public static void main(String[] args) {
		int reply = JOptionPane.showConfirmDialog(null, "Allow the app to change size to match your display resolution? "
													+ "\n(Prevents window from being too small or big.)"
													, "App Scale:", JOptionPane.YES_NO_OPTION);
		
		if(reply == JOptionPane.YES_OPTION) {
			int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
			
			//Set UIM to ratio of screenHeight to 1080p
			uiM = screenHeight/1080f;
			
			//Round uiM to the first decimal place
			uiM = (Math.round(uiM/0.1f))*0.1f;
		}	
		
		// Customize default JOptionPane Colours and fonts
		UIManager.put("Label.font", new Font("Microsoft JhengHei UI", Font.BOLD, scale(15)));
		UIManager.put("Button.font", new Font("Microsoft JhengHei UI", Font.BOLD, scale(15)));
		UIManager.put("OptionPane.background", MFrame.clPane);
		UIManager.put("Panel.background", MFrame.clPane);
		UIManager.put("OptionPane.messageForeground", MFrame.cl250);

		// Variables
		String expression = "x";
		double xRange = 20;
		double yRange = xRange;
		double precision = 0.01;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// create frame
				frame = new MFrame("Function Grapher - Antonio Ferreras", 1150, 580, uiM, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						reCalcComponents();
					}

				});
				frame.makeNotResizable();

				w = frame.getCurrentWidth();
				h = frame.getCurrentHeight();

				// Main content panel
				p = new JPanel();
				p.setBounds(scale(15), scale(40), scale(w - 30), scale(h - 60));
				p.setLayout(null);
				p.setBackground(MFrame.clPane);
				frame.add(p);

				// Splits the frame up
				splittingP = new JPanel();
				splittingP.setBounds(scale(graphWidth + 20), 0, scale(15), scale(h));
				splittingP.setBackground(MFrame.clBack);
				p.add(splittingP);

				// Splits the frame up AGAIN
				splittingP2 = new JPanel();
				splittingP2.setBounds(scale(graphWidth + 390), 0, scale(15), scale(h));
				splittingP2.setBackground(MFrame.clBack);
				p.add(splittingP2);
				
				JPanel horzSplittingP = new JPanel();
				horzSplittingP.setBounds(scale(graphWidth+20), scale(80), scale(370), scale(15));
				horzSplittingP.setBackground(MFrame.clBack);
				p.add(horzSplittingP);

				// f(x) label
				JLabel label = new JLabel("f(x)= ");
				label.setBounds(scale(560), scale(25), scale(50), scale(35));
				label.setForeground(MFrame.cl250);
				label.setFont(new Font("Microsoft Tai Le", Font.PLAIN, scale(20)));
				p.add(label);

				// Edit equation
				JTextField txtFunc = new JTextField();
				txtFunc.setBounds(scale(605), scale(25), scale(250), scale(35));
				txtFunc.setForeground(MFrame.cl250);
				txtFunc.setBackground(MFrame.clDkPane);
				txtFunc.setCaretColor(MFrame.cl250);
				txtFunc.setSelectionColor(MFrame.cl250);
				txtFunc.setFont(new Font("Microsoft Tai Le", Font.PLAIN, scale(18)));
				txtFunc.setText(expression);
				txtFunc.setFont(new Font("Consolas", Font.PLAIN, scale(16)));
				p.add(txtFunc);

				// Number buttons
				MButton1[] numButtons = new MButton1[9];
				int k = 0;
				int yStart = 120;
				int y = yStart;
				for (int i = 0; i < numButtons.length; i++) {
					if (i == 3) {
						y = yStart + 36;
						k = 0;
					} else if (i == 6) {
						y = yStart + 36 * 2;
						k = 0;
					} else if (i == 9) {
						y = yStart + 36 * 3;
						k = 0;
					}
					int num = i;
					numButtons[i] = new MButton1(String.valueOf(i + 1), 50 + k * 36, y, 35, 35, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							txtFunc.setText(txtFunc.getText() + (num + 1));
						}
					});
					numButtons[i].setRelativeTo(graphWidth, 0);
					p.add(numButtons[i].getButton());
					k++;
				}
				MButton1 zeroButton = new MButton1("0", 86, yStart + 36 * 3, 35, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + "0");
					}
				});
				zeroButton.setRelativeTo(graphWidth, 0);
				p.add(zeroButton.getButton());

				MButton1 sinButton = new MButton1("sin(a)", 50, 340, 65, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
//						String a = JOptionPane.showInputDialog("What is a?    sin(a)");
						txtFunc.setText(txtFunc.getText() + "sin(");
					}
				});
				sinButton.setRelativeTo(graphWidth, 0);
				p.add(sinButton.getButton());

				MButton1 cosButton = new MButton1("cos(a)", 120, 0, 65, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + "cos(");
					}
				});
				cosButton.setRelativeTo(graphWidth, unScale(sinButton.getY()));
				p.add(cosButton.getButton());

				MButton1 tanButton = new MButton1("tan(a)", 190, 0, 65, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + "tan(");
					}
				});
				tanButton.setRelativeTo(graphWidth, unScale(sinButton.getY()));
				p.add(tanButton.getButton());

				MButton1 moreButton = new MButton1("More...", 260, 0, 65, 35, new ActionListener() {
					boolean isOpen = false;

					public void actionPerformed(ActionEvent e) {
						if (!isOpen) {
							isOpen = true;
							JPanel panel = new JPanel();
							panel.setSize(scale(380), scale(330));
							panel.setBackground(MFrame.clPane);

							JTextArea txtDesc = new JTextArea("abs(): absolute value\n" + "acos() - arc cosine\n"
									+ "asin() - arc sine\n" + "atan() - arc tangent\n" + "cbrt() - cubic root\n"
									+ "ceil() - nearest upper integer\n" + "cosh() - hyperbolic cosine\n"
									+ "exp() - euler's number raised to the power (e^x)\n"
									+ "floor() - nearest lower integer\n" + "log() - logarithmus naturalis (base e)\n"
									+ "log10() - logarithm (base 10)\n" + "log2() - logarithm (base 2)\n"
									+ "sinh() - hyperbolic sine\n" + "tanh() - hyperbolic tangent\n");
							txtDesc.setBackground(MFrame.clPane);
							txtDesc.setForeground(MFrame.cl250);
							txtDesc.setEditable(false);
							txtDesc.setBorder(new LineBorder(MFrame.cl250, 0));
							txtDesc.setFont(new Font("Microsoft Tai Le", Font.PLAIN, scale(17)));
							panel.add(txtDesc);

							MOptionPane oPane = new MOptionPane("Other Functions", panel);
							oPane.getFrame().setAlwaysOnTop(true);
							oPane.setCloseAction(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									isOpen = false;
									oPane.getFrame().dispose();
								}
							});
						}
					}
				});
				moreButton.setRelativeTo(graphWidth, unScale(sinButton.getY()));
				p.add(moreButton.getButton());

				MButton1 xButton = new MButton1("x", 50, 400, 35, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + "x");
					}
				});
				xButton.setRelativeTo(graphWidth, 0);
				xButton.setFont(new Font("Cambria", Font.BOLD, scale(22)));
				p.add(xButton.getButton());

				MButton1 piButton = new MButton1(PI_CHAR, 90, 0, 35, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + PI_CHAR);
					}
				});
				piButton.setRelativeTo(graphWidth, unScale(xButton.getY()));
				piButton.setFont(new Font("Cambria", Font.BOLD, scale(22)));
				p.add(piButton.getButton());

				MButton1 eButton = new MButton1("e", 130, 0, 35, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + "e");
					}
				});
				eButton.setRelativeTo(graphWidth, unScale(xButton.getY()));
				eButton.setFont(new Font("Cambria", Font.BOLD, scale(22)));
				p.add(eButton.getButton());

				Font operatorFont = new Font("Arial", Font.BOLD, scale(24));

				MButton2 addButton = new MButton2("+", 190, yStart, 35, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + " + ");
					}
				});
				addButton.setFont(operatorFont);
				addButton.setBackgroundColor(MFrame.clGreen);
				addButton.setRelativeTo(graphWidth, 0);
				p.add(addButton.getButton());

				MButton2 subtractButton = new MButton2("-", 230, yStart, 35, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + " -");
					}
				});
				subtractButton.setFont(operatorFont);
				subtractButton.setBackgroundColor(MFrame.clYellow);
				subtractButton.setRelativeTo(graphWidth, 0);
				p.add(subtractButton.getButton());

				MButton2 multiplyButton = new MButton2("*", 190, yStart + 40, 35, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + "*");
					}
				});
				multiplyButton.setFont(operatorFont);
				multiplyButton.setRelativeTo(graphWidth, 0);
				p.add(multiplyButton.getButton());

				MButton2 divideButton = new MButton2("/", 230, yStart + 40, 35, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + "/");
					}
				});
				divideButton.setFont(operatorFont);
				divideButton.setRelativeTo(graphWidth, 0);
				p.add(divideButton.getButton());

				MButton1 openBracketButton = new MButton1("(", 190, yStart + 80, 35, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + "(");
					}
				});
				openBracketButton.setFont(operatorFont);
				openBracketButton.setRelativeTo(graphWidth, 0);
				p.add(openBracketButton.getButton());

				MButton1 closeBracketButton = new MButton1(")", 230, yStart + 80, 35, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + ")");
					}
				});
				closeBracketButton.setFont(operatorFont);
				closeBracketButton.setRelativeTo(graphWidth, 0);
				p.add(closeBracketButton.getButton());

				MButton1 sqrtButton = new MButton1("SQRT", 270, yStart, 65, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + "sqrt(");
					}
				});
				sqrtButton.setRelativeTo(graphWidth, 0);
				p.add(sqrtButton.getButton());

				MButton1 absButton = new MButton1("abs", 270, yStart + 80, 65, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + "abs(");
					}
				});
				absButton.setRelativeTo(graphWidth, 0);
				p.add(absButton.getButton());

				MButton1 signumButton = new MButton1("sign", 270, yStart + 120, 65, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + "signum(");
					}
				});
				signumButton.setRelativeTo(graphWidth, 0);
				p.add(signumButton.getButton());
				
				MButton1 logButton = new MButton1("log", 190, yStart + 120, 75, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + "log(");
					}
				});
				logButton.setRelativeTo(graphWidth, 0);
				p.add(logButton.getButton());

				MButton1 powButton = new MButton1("^", 270, yStart + 40, 65, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText(txtFunc.getText() + "^");
					}
				});
				powButton.setFont(operatorFont);
				powButton.setRelativeTo(graphWidth, 0);
				p.add(powButton.getButton());

				MButton2 delButton = new MButton2("DEL", 285, -105, 65, 35, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (!txtFunc.getText().equals(""))
							txtFunc.setText(txtFunc.getText().substring(0, txtFunc.getText().length() - 1));
					}
				});
				delButton.setRelativeTo(graphWidth, h);
				delButton.setBackgroundColor(MFrame.clCrimson);
				p.add(delButton.getButton());

				// Label for precision
				JLabel lblPrecision = new JLabel("Precision:");
				lblPrecision.setBounds(scale(graphWidth + 415), scale(10), scale(100), scale(25));
				lblPrecision.setForeground(MFrame.cl250);
				lblPrecision.setFont(new Font("Microsoft Tai Le", Font.PLAIN, scale(18)));
				p.add(lblPrecision);

				// Entry for precision
				JTextField txtPrecision = new JTextField();
				txtPrecision.setBounds(scale(graphWidth + 415), scale(35), scale(180), scale(35));
				txtPrecision.setForeground(MFrame.cl250);
				txtPrecision.setBackground(MFrame.clDkPane);
				txtPrecision.setCaretColor(MFrame.cl250);
				txtPrecision.setSelectionColor(MFrame.cl250);
				txtPrecision.setFont(new Font("Microsoft Tai Le", Font.PLAIN, scale(18)));
				txtPrecision.setText(String.valueOf(precision));
				txtPrecision.setFont(new Font("Consolas", Font.PLAIN, scale(16)));
				p.add(txtPrecision);

				// Label for range
				JLabel lblRange = new JLabel("Graph range:");
				lblRange.setBounds(scale(graphWidth + 415), scale(95), scale(140), scale(25));
				lblRange.setForeground(MFrame.cl250);
				lblRange.setFont(new Font("Microsoft Tai Le", Font.PLAIN, scale(18)));
				p.add(lblRange);
				
				// Label for xRange
				JLabel lblXRange = new JLabel("x:");
				lblXRange.setBounds(scale(graphWidth + 415), scale(125), scale(25), scale(25));
				lblXRange.setForeground(MFrame.cl250);
				lblXRange.setFont(new Font("Microsoft Tai Le", Font.BOLD, scale(20)));
				p.add(lblXRange);

				// Entry for xRange
				JTextField txtXRange = new JTextField();
				txtXRange.setBounds(scale(graphWidth + 435), scale(120), scale(160), scale(35));
				txtXRange.setForeground(MFrame.cl250);
				txtXRange.setBackground(MFrame.clDkPane);
				txtXRange.setCaretColor(MFrame.cl250);
				txtXRange.setSelectionColor(MFrame.cl250);
				txtXRange.setFont(new Font("Microsoft Tai Le", Font.PLAIN, scale(18)));
				txtXRange.setText(String.valueOf(xRange));
				txtXRange.setFont(new Font("Consolas", Font.PLAIN, scale(16)));
				p.add(txtXRange);
				
				// Label for xRange
				JLabel lblYRange = new JLabel("y:");
				lblYRange.setBounds(scale(graphWidth + 415), scale(170), scale(25), scale(25));
				lblYRange.setForeground(MFrame.cl250);
				lblYRange.setFont(new Font("Microsoft Tai Le", Font.BOLD, scale(20)));
				p.add(lblYRange);

				// Entry for xRange
				JTextField txtYRange = new JTextField();
				txtYRange.setBounds(scale(graphWidth + 435), scale(165), scale(160), scale(35));
				txtYRange.setForeground(MFrame.cl250);
				txtYRange.setBackground(MFrame.clDkPane);
				txtYRange.setCaretColor(MFrame.cl250);
				txtYRange.setSelectionColor(MFrame.cl250);
				txtYRange.setFont(new Font("Microsoft Tai Le", Font.PLAIN, scale(18)));
				txtYRange.setText(String.valueOf(yRange));
				txtYRange.setFont(new Font("Consolas", Font.PLAIN, scale(16)));
				p.add(txtYRange);
				
				//label for soom
				JLabel lblZoom = new JLabel("Zoom:");
				lblZoom.setBounds(scale(graphWidth + 415), scale(220), scale(140), scale(25));
				lblZoom.setForeground(MFrame.cl250);
				lblZoom.setFont(new Font("Microsoft Tai Le", Font.PLAIN, scale(18)));
				p.add(lblZoom);
				
				ActionListener updateAction = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								//Remove old graph to make space for new!
								p.remove(graph);
								
								String expression = txtFunc.getText();
								double precision = 0.01, xRange = 20, yRange = 20;
								boolean okayToGraph = true;
								
								// Make sure the range and precision input is valid
								try {
									precision = Double.parseDouble(txtPrecision.getText());
									txtPrecision.setForeground(MFrame.cl250);
									lblPrecision.setForeground(MFrame.cl250);
								} catch (NumberFormatException e) {
									txtPrecision.setForeground(MFrame.clCrimson);
									lblPrecision.setForeground(MFrame.clCrimson);
									okayToGraph = false;
								}

								try {
									xRange = Double.parseDouble(txtXRange.getText());
									txtXRange.setForeground(MFrame.cl250);
									lblRange.setForeground(MFrame.cl250);
									lblXRange.setForeground(MFrame.cl250);
								} catch (NumberFormatException e) {
									txtXRange.setForeground(MFrame.clCrimson);
									lblRange.setForeground(MFrame.clCrimson);
									lblXRange.setForeground(MFrame.clCrimson);
									okayToGraph = false;
								}
								
								try {
									yRange = Double.parseDouble(txtYRange.getText());
									txtYRange.setForeground(MFrame.cl250);
									lblYRange.setForeground(MFrame.cl250);
								} catch (NumberFormatException e) {
									txtYRange.setForeground(MFrame.clCrimson);
									lblRange.setForeground(MFrame.clCrimson);
									lblYRange.setForeground(MFrame.clCrimson);
									okayToGraph = false;
								}
								// Only build graph is valid input
								if (okayToGraph) {
									try {
										buildGraph(expression, xRange, yRange, precision);
										txtFunc.setForeground(MFrame.cl250);
										label.setForeground(MFrame.cl250);
									} catch (Exception e) {
										txtFunc.setForeground(MFrame.clCrimson);
										label.setForeground(MFrame.clCrimson);
									}
								}
							}
						}).start();
					}
				};

				double zoomFactor = 0.2d;
				//Zoom buttons
				MButton1 btnZoomIn = new MButton1("+Zoom in", 415, 250, 95, 35, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						//Get current range
						double xRange = Double.parseDouble(txtXRange.getText());
						double yRange = Double.parseDouble(txtYRange.getText());
						
						//Do zooming
						xRange *= 1.0-zoomFactor;
						yRange *= 1.0-zoomFactor;
						
						//Update range entry fields
						txtXRange.setText(String.valueOf(Math.round(xRange)));
						txtYRange.setText(String.valueOf(Math.round(yRange)));
						
						//Update graph
						updateAction.actionPerformed(null);
					}
				});
				btnZoomIn.setRelativeTo(graphWidth, 0);
				p.add(btnZoomIn.getButton());
				
				MButton1 btnZoomOut = new MButton1("-Zoom out", 515, 250, 95, 35, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						//Get current range
						double xRange = Double.parseDouble(txtXRange.getText());
						double yRange = Double.parseDouble(txtYRange.getText());
						
						//Do zooming
						xRange *= 1.0+zoomFactor;
						yRange *= 1.0+zoomFactor;
						
						//Update range entry fields
						txtXRange.setText(String.valueOf(Math.round(xRange)));
						txtYRange.setText(String.valueOf(Math.round(yRange)));
						
						//Update graph
						updateAction.actionPerformed(null);
					}
				});
				btnZoomOut.setRelativeTo(graphWidth, 0);
				p.add(btnZoomOut.getButton());
				
				// Button to update graph
				MButton2 btnUpdate = new MButton2("Graph it!", 50, -105, 110, 35, updateAction);
				btnUpdate.setRelativeTo(graphWidth, h);
				p.add(btnUpdate.getButton());
				
				KeyAdapter enterListener = new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						if(e.getKeyCode() == KeyEvent.VK_ENTER) {
							updateAction.actionPerformed(null);
						}
					}
				};
				txtFunc.addKeyListener(enterListener);
				txtPrecision.addKeyListener(enterListener);
				txtXRange.addKeyListener(enterListener);
				txtYRange.addKeyListener(enterListener);

				MButton2 btnClear = new MButton2("Clear", 165, -105, 110, 35, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						txtFunc.setText("");
					}
				});
				btnClear.setBackgroundColor(MFrame.clCrimson);
				btnClear.setRelativeTo(graphWidth, h);
				p.add(btnClear.getButton());
				
				MButton1 btnFindPoint = new MButton1("Find a point", 415, -105, 110, 35, new ActionListener() {
					DecimalFormat dc = new DecimalFormat("##.####");
					
					@Override
					public void actionPerformed(ActionEvent e) {
						String expression = txtFunc.getText();
						double input = Double.NaN;
						try {
							input = Double.parseDouble(JOptionPane.showInputDialog("Enter the X Coordinate!"));
						} catch(Exception e1) {
							JOptionPane.showMessageDialog(null, "Invalid Coordinate, try again.");
						}
						
						//If inputted valid number
						if(!Double.isNaN(input)) {
							// Get y value of function with x as input
							double output = evaluate(expression, input);
							
							if(!Double.isNaN(output))
								JOptionPane.showMessageDialog(null, "(" + dc.format(input) + ", " + dc.format(output)  + ")");
							else
								JOptionPane.showMessageDialog(null, "(" + dc.format(input)  + ", UNDEFINED)");
						}
					}
				});
				btnFindPoint.setRelativeTo(graphWidth, h);
				p.add(btnFindPoint.getButton());
				
				MButton2 btnCredit = new MButton2("?", 575, -105, 35, 35, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JPanel panel = new JPanel();
						panel.setBackground(MFrame.clPane);
						panel.setSize(scale(500), scale(200));
						
						JLabel text = new JLabel("<html><br/>Function Grapher was made using the library exp4j, <br/><br/>"
								+ "written by Frank Asseg and licensed under Apache License 2.0.</html>", SwingConstants.CENTER);
						text.setFont(new Font("Microsoft Tai Le", Font.PLAIN, scale(17)));
						text.setForeground(MFrame.cl250);
						
						panel.add(text);
						
						new MOptionPane("Info", panel);
					}
				});
				btnCredit.setFont(new Font("Microsoft JhengHei UI Light", Font.BOLD, scale(24)));
				btnCredit.setRelativeTo(graphWidth, h);
				p.add(btnCredit.getButton());

				// make frame visible
				frame.setVisible(true);
				
				//Build graph for the first time
				new Thread(new Runnable() {
					@Override
					public void run() {
						buildGraph(expression, xRange, yRange, precision);
					}
				}).start();
			}
		});
	}

	private static void buildGraph(String expression, double xRange, double yRange, double precision) {
		double start = 0 - xRange / 2;
		double end = xRange / 2;
		int numOfPoints = (int) (xRange / precision);

		// Create array of points
		Point2D.Double[] points = new Point2D.Double[numOfPoints];

		int point = 0;

		// Calculate y values for graph
		for (double x = start; x <= end; x += precision) {
			double almostZero = Double.MIN_VALUE;
			double input = x;
			double y;
			if(x == 0) {
				//To avoid divison by 0, use effectively zero instead
				input = almostZero;
			}
			// Get y value of function with x as input
			double output = evaluate(expression, input);
			
			if(output > yRange) {//If number is out of xRange
				y = Double.NaN;				//then replace with NaN
			} else {
				y = output;	//Otherwise use the normal output
			}
		
			

			// Add current x and y to a point in the array
			if (point < points.length) {// Prevent from going out of bounds
				points[point] = new Point2D.Double(x, y);
				point++;
			}
		}

		// Graph
		graph = new Graph(10, 10, graphWidth, graphWidth, points);
		graph.setxRange(xRange, yRange);
		p.add(graph);

		// Re-draw frame
		frame.getFrame().repaint();
	}

	private static double evaluate(String expression, double value) {
		Expression e = new ExpressionBuilder(expression).variables("x", PI_CHAR, "e").build().setVariable("x", value)
				.setVariable(PI_CHAR, Math.PI).setVariable("e", Math.E);
		return e.evaluate();
	}

	private static int scale(int n) {
		return (int) (n * uiM);
	}

	private static int unScale(int n) {
		return (int) (n / uiM);
	}

	private static void reCalcComponents() {
		w = frame.getCurrentWidth();
		h = frame.getCurrentHeight();

		p.setBounds(scale(15), scale(40), scale(w - 30), scale(h - 60));
		splittingP.setBounds(scale(graphWidth + 5), 0, scale(10), scale(h));
		splittingP.setBounds(scale(graphWidth + 20), 0, scale(15), scale(h));
	}
}
