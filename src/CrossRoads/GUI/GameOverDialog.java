package CrossRoads.GUI;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import CrossRoads.Database;
import CrossRoads.Game;
import CrossRoads.SfxManager;
import CrossRoads.GameSession.Map;
import CrossRoads.GameSession.Entities.Score;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JTextField;

/**
 * Classe che estende JDialog, al fine di generare una sorta di menu secondario con cui interagire, 
 * che appaia solo alla fine di una sessione di gioco al di sopra del frame principale dell'applicazione
 */
@SuppressWarnings("serial")
public class GameOverDialog extends JDialog {
	private static JPanel gameOverPrompt;
	private static JPanel gameOverBtnPanel;
	private static JPanel scorePrompt;
	private static JPanel scoreBtnPanel;
	private static JButton btnProceed;
	private static JButton btnSave;
	private static JButton btnCancel;
	private static JButton btnExit;
	private static JLabel lblGameOver;
	private static JLabel lblCause;
	private static JLabel lblProceed;
	private static JLabel lblInsert;
	private static JLabel lblSave;
	private static JLabel lblCancel;
	private static JLabel lblMaxChar;
	private static JLabel lblExit;
	private static JLabel lblYourScore;
	private static JLabel gameOverBkg;
	private static JLabel scoreBkg;
	private JTextField nameField;
	private static ImageIcon gb;
	private static ImageIcon sb;
	private static int keyCount;
	private static String name;
	private static Font fnt40;
	private static Font fnt25;
	private static Font fnt20;
	
	/**
	 * Costruttore di GameOverDialog, crea un JDialog customizzato con JLabel e JPanel
	 * @param appWindow è il frame genitore del JDialog, ovvero il frame definito dalla classe AppWindow è il possessore del JDialog
	 */
	public GameOverDialog(AppWindow appWindow) {
		keyCount=0;
		setTitle("Game Over");
		setUndecorated(true); 
		setSize(640, 480);
		setResizable(false);
		fnt40=new Font("Tahoma", Font.BOLD, 40);
		fnt25=new Font("Tahoma", Font.BOLD, 25);
		fnt20=new Font("Tahoma", Font.BOLD, 20);
		getContentPane().setLayout(null);
		{
			scorePrompt=new JPanel();
			scorePrompt.setVisible(false);
			scorePrompt.setBounds(0, 0, 640, 480);
			getContentPane().add(scorePrompt);
			scorePrompt.setLayout(null);
			{
				lblInsert=new JLabel("Insert Your Name");
				lblInsert.setHorizontalAlignment(SwingConstants.CENTER);
				lblInsert.setForeground(Color.RED);
				lblInsert.setFont(fnt40);
				lblInsert.setBounds(0, 10, 640, 40);
				scorePrompt.add(lblInsert);
			}
			{
				lblMaxChar=new JLabel("(max 10 characters)");
				lblMaxChar.setHorizontalAlignment(SwingConstants.CENTER);
				lblMaxChar.setForeground(Color.RED);
				lblMaxChar.setFont(fnt20);
				lblMaxChar.setBounds(0, 60, 640, 20);
				scorePrompt.add(lblMaxChar);
			}
			{
				scoreBtnPanel=new JPanel();
				scoreBtnPanel.setOpaque(false);
				scoreBtnPanel.setBounds(0, 435, 640, 33);
				scorePrompt.add(scoreBtnPanel);
				scoreBtnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
				{
					btnSave=new JButton("Save");
					btnSave.setForeground(Color.GREEN);
					btnSave.setBackground(Color.BLACK);
					btnSave.setActionCommand("Proceed");
					scoreBtnPanel.add(btnSave);
					btnSave.addMouseListener(SfxManager.getBtnSFX());
					btnSave.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							setVisible(false);
							name=nameField.getText();
							if(name.equals("")) 
								name="unknown";
							Database.setName(name);
							Database.fillTable();
							AppWindow.setTableGFX(Database.getTblEasy());  //vengono settate le proprietà relative alla grafica per ciascuna JTable
							AppWindow.setTableGFX(Database.getTblMedium());
							AppWindow.setTableGFX(Database.getTblHard());
							appWindow.setContentPane(AppWindow.getleadbScreen());
							AppWindow.getleadbScreen().setVisible(true);
							focusOnTab(Map.getDifficulty());
						}
					});
				}
				{
					btnCancel=new JButton("Cancel");
					btnCancel.setForeground(Color.GREEN);
					btnCancel.setBackground(Color.BLACK);
					btnCancel.setActionCommand("Cancel");
					scoreBtnPanel.add(btnCancel);
					btnCancel.addMouseListener(SfxManager.getBtnSFX());
					btnCancel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							setVisible(false);
							appWindow.setContentPane(AppWindow.getBkg());
							AppWindow.getBkg().add(AppWindow.getMenuScreen());
							AppWindow.getMenuScreen().setVisible(true); 
						}
					});
				}
				{
					btnExit=new JButton("Exit");
					btnExit.setForeground(Color.GREEN);
					btnExit.setBackground(Color.BLACK);
					btnExit.setActionCommand("Proceed");
					scoreBtnPanel.add(btnExit);
					btnExit.addMouseListener(SfxManager.getBtnSFX());
					btnExit.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							Game.exitProcedure();
						}
					});
				}
			}
			{
				lblSave=new JLabel("Click \"Save\" to proceed to the leaderboard");
				lblSave.setVerticalAlignment(SwingConstants.BOTTOM);
				lblSave.setHorizontalAlignment(SwingConstants.CENTER);
				lblSave.setForeground(Color.YELLOW);
				lblSave.setFont(fnt25);
				lblSave.setBounds(0, 170, 640, 25);
				scorePrompt.add(lblSave);
			}
			{
				lblCancel=new JLabel("Click \"Cancel\" to return to main menu");
				lblCancel.setVerticalAlignment(SwingConstants.BOTTOM);
				lblCancel.setHorizontalAlignment(SwingConstants.CENTER);
				lblCancel.setForeground(Color.YELLOW);
				lblCancel.setFont(fnt25);
				lblCancel.setBounds(1, 220, 640, 25);
				scorePrompt.add(lblCancel);
			}
			{
				lblExit=new JLabel("Click \"Exit\" to close CrossRoads");
				lblExit.setVerticalAlignment(SwingConstants.BOTTOM);
				lblExit.setHorizontalAlignment(SwingConstants.CENTER);
				lblExit.setForeground(Color.YELLOW);
				lblExit.setFont(fnt25);
				lblExit.setBounds(0, 270, 640, 25);
				scorePrompt.add(lblExit);
			}
			{
				nameField=new JTextField();
				nameField.setFocusable(true);
				nameField.addKeyListener(new KeyListener() {
					public void keyTyped(KeyEvent e) {}
                    @SuppressWarnings("static-access")
					public void keyReleased(KeyEvent e) {
                    	if(e.getKeyCode()==e.VK_ENTER)
                    		btnSave.doClick();
                    	if(nameField.getText().length()>10) {
                    		nameField.setEditable(false);
                    	}
                    	keyCount=nameField.getText().length();
                    }
                    @SuppressWarnings("static-access")
                    public void keyPressed(KeyEvent e) {
                        if(e.getKeyCode()==e.VK_BACK_SPACE || e.getKeyCode()==e.VK_DELETE) {
                        	keyCount--;
                        	if(keyCount<0) keyCount=0;
                        	else if(!nameField.isEditable()) nameField.setEditable(true);
                        }
                        else keyCount++; // necessario se qualcuno decide di tener premuto un tasto
                        if(keyCount>10) {
                    		keyCount=10;
                    		nameField.setEditable(false);
                    	}
                        else if(nameField.getText().length()!=keyCount) {
                        	nameField.setEditable(true);
                        }
                    }
                });
				nameField.addMouseListener(new MouseAdapter() {
					public void mouseReleased(MouseEvent selectText) {
						if(nameField.getSelectedText()!=null)
							keyCount-=nameField.getSelectedText().length();
					}
				});
				nameField.setHorizontalAlignment(SwingConstants.CENTER);
				nameField.setFont(new Font("Tahoma", Font.BOLD, 16));
				nameField.setSelectedTextColor(Color.YELLOW);
				nameField.setCaretColor(Color.ORANGE);
				nameField.setBackground(Color.BLACK);
				nameField.setForeground(Color.ORANGE);
				nameField.setBounds(213, 100, 214, 32);
				scorePrompt.add(nameField);
			}
			scoreBkg=new JLabel();
			scoreBkg.setBounds(0, 0, 640, 480);
			sb=new ImageIcon(getClass().getClassLoader().getResource("CrossRoads/Resources/gfx/menu/savescore.png"));
			scoreBkg.setIcon(sb);
			scoreBkg.setHorizontalAlignment(SwingConstants.CENTER);
			scoreBkg.setVerticalAlignment(SwingConstants.CENTER);
			scorePrompt.add(scoreBkg);
		}
		gameOverPrompt=new JPanel();
		gameOverPrompt.setBounds(0, 0, 640, 480);
		getContentPane().add(gameOverPrompt);
		gameOverPrompt.setLayout(null);
		{
			lblGameOver=new JLabel("Game Over");
			lblGameOver.setBounds(0, 10, 640, 40);
			lblGameOver.setHorizontalAlignment(SwingConstants.CENTER);
			lblGameOver.setFont(fnt40);
			lblGameOver.setForeground(Color.RED);
			gameOverPrompt.add(lblGameOver);
		}
		{
			lblCause=new JLabel();
			lblCause.setBounds(0, 60, 640, 20);
			lblCause.setHorizontalAlignment(SwingConstants.CENTER);
			lblCause.setFont(fnt20);
			lblCause.setForeground(Color.RED);
			gameOverPrompt.add(lblCause);
		}
		{
			lblYourScore=new JLabel();
			lblYourScore.setHorizontalAlignment(SwingConstants.CENTER);
			lblYourScore.setForeground(Color.ORANGE);
			lblYourScore.setFont(fnt25);
			lblYourScore.setBounds(0, 100, 640, 25);
			gameOverPrompt.add(lblYourScore);
		}
		{
			gameOverBtnPanel=new JPanel();
			gameOverBtnPanel.setBounds(0, 435, 640, 32);
			gameOverPrompt.add(gameOverBtnPanel);
			gameOverBtnPanel.setOpaque(false);
			gameOverBtnPanel.setBackground(Color.DARK_GRAY);
			gameOverBtnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			{
				btnProceed=new JButton("Proceed");
				btnProceed.setBackground(Color.BLACK);
				btnProceed.setForeground(Color.GREEN);
				btnProceed.setActionCommand("Proceed");
				gameOverBtnPanel.add(btnProceed);
				btnProceed.addMouseListener(SfxManager.getBtnSFX());
				btnProceed.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						gameOverPrompt.setVisible(false);
						setContentPane(scorePrompt);
						scorePrompt.setVisible(true); 
					}
				});
			}
			{
				btnCancel=new JButton("Cancel");
				btnCancel.setForeground(Color.GREEN);
				btnCancel.setBackground(Color.BLACK);
				btnCancel.setActionCommand("Cancel");
				gameOverBtnPanel.add(btnCancel);
				btnCancel.addMouseListener(SfxManager.getBtnSFX());
				btnCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						setVisible(false);
						appWindow.setContentPane(AppWindow.getBkg());
						AppWindow.getBkg().add(AppWindow.getMenuScreen());
						AppWindow.getMenuScreen().setVisible(true); 
					}
				});
			}
			{
				btnExit=new JButton("Exit");
				btnExit.setForeground(Color.GREEN);
				btnExit.setBackground(Color.BLACK);
				btnExit.setActionCommand("Proceed");
				gameOverBtnPanel.add(btnExit);
				btnExit.addMouseListener(SfxManager.getBtnSFX());
				btnExit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						Game.exitProcedure();
					}
				});
			}
		}
		{
			lblProceed=new JLabel("Click \"Proceed\" to save your score");
			lblProceed.setVerticalAlignment(SwingConstants.BOTTOM);
			lblProceed.setHorizontalAlignment(SwingConstants.CENTER);
			lblProceed.setForeground(Color.YELLOW);
			lblProceed.setFont(fnt25);
			lblProceed.setBounds(0, 170, 640, 25);
			gameOverPrompt.add(lblProceed);
		}
		{
			lblCancel=new JLabel("Click \"Cancel\" to return to main menu");
			lblCancel.setVerticalAlignment(SwingConstants.BOTTOM);
			lblCancel.setHorizontalAlignment(SwingConstants.CENTER);
			lblCancel.setForeground(Color.YELLOW);
			lblCancel.setFont(fnt25);
			lblCancel.setBounds(0, 220, 640, 25);
			gameOverPrompt.add(lblCancel);
		}
		{
			lblExit=new JLabel("Click \"Exit\" to close CrossRoads");
			lblExit.setVerticalAlignment(SwingConstants.BOTTOM);
			lblExit.setHorizontalAlignment(SwingConstants.CENTER);
			lblExit.setForeground(Color.YELLOW);
			lblExit.setFont(fnt25);
			lblExit.setBounds(0, 270, 640, 25);
			gameOverPrompt.add(lblExit);
		}
		gameOverBkg=new JLabel();
		gameOverBkg.setBounds(0, 0, 640, 480);
		gb=new ImageIcon(getClass().getClassLoader().getResource("CrossRoads/Resources/gfx/menu/gameover.png"));
		gameOverBkg.setIcon(gb);
		gameOverBkg.setHorizontalAlignment(SwingConstants.CENTER);
		gameOverBkg.setVerticalAlignment(SwingConstants.CENTER);
		gameOverPrompt.add(gameOverBkg);
	}
	
	/**
	 * Mostra la tabella di riferimento della giusta difficoltà, dopo aver salvato un risultato
	 * @param difficulty
	 */
	private static void focusOnTab(String difficulty) {
		if(difficulty.equals("Easy")) 
			AppWindow.getTabs().setSelectedIndex(0);
		else if(difficulty.equals("Medium"))
			AppWindow.getTabs().setSelectedIndex(1);
		else if(difficulty.equals("Hard"))
			AppWindow.getTabs().setSelectedIndex(2);
	}

	/**
	 * Restituisce il JPanel gameOverPrompt
	 * @return gameOverPrompt
	 */
	public static JPanel getGameOverPrompt() {
		return gameOverPrompt;
	}
	
	/**
	 * Assegna un Text customizzato al JLabel lblYourScore
	 */
	public static void setTextScore() {
		lblYourScore.setText("Your Score: "+Score.getActualScore());
	}
	
	/**
	 * Restituisce il JLabel lblCause
	 * @return lblCause
	 */
	public static JLabel getLblCause() {
		return lblCause;
	}
	
	/**
	 * Rappresentazione di tipo String dell'oggetto di classe
	 */
	public String toString(){
		return getClass().getName();
	}
	
	/**
	 * Verifica se l'oggetto è uguale ad un oggetto di tipo GameOverDialog
	 */
	public boolean equals(GameOverDialog dialog) {
		// pre: dialog!=null
		return (this==dialog);
	}
}
