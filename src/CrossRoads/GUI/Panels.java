package CrossRoads.GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import CrossRoads.Database;
import CrossRoads.Game;
import CrossRoads.SfxManager;

public abstract class Panels {
	private static JPanel menuScreen;
	private static JPanel mapScreen;  
	private static JPanel gameScreen;
	private static JPanel leadbScreen;
	private static JPanel rulesScreen; 
	private static JPanel optionsScreen; 
	private static JPanel returnBox;
	private static JLabel returnLabel;
	private static boolean inside;
	private static JLabel background;  // immagine background aggiunta come oggetto JLabel (precisamente come attributo Icon del JLabel)
	private static ImageIcon bg;  // attributo Icon del JLabel background
	private static JLabel drawnMap;  // mappa di gioco aggiunta come oggetto JLabel (precisamente come attributo Icon del JLabel)
	private static ImageIcon dm;  // attributo Icon del JLabel drawnMap
	private static JLabel drawnShadows;
	private static ImageIcon ds;
	private static JLabel drawnBuildings;
	private static ImageIcon db;
	private static Image icon=null;
	private static Image pressedIcon=null;
	private static Image searchButtonIcon=null;
	private static Image pressedSearchButtonIcon=null;
	private static JLabel leadbTitle;  // label per la schermata leadbScreen
	private static JTextField searchBar;
	private static String wantedName;
	private static JButton btnSearch;
	private static int keyCount;
	private static Font fntTable;  // font per le componenti di leadbScreen
	private static Font fntLeadb;  // font per il JLabel leadbTitle
	private static Color menuBkg;  // colore customizzato per il background, valori RGB
	private static Color mapBkg;  // colore customizzato per il background della mappa, valori RGB
	private static JTabbedPane tabbedLevels;
	private static JScrollPane scrollPanelEasy;
	private static JScrollPane scrollPanelMedium;
	private static JScrollPane scrollPanelHard;
	private static GroupLayout menuLayout;  // layout per la prima schermata del JFrame
	private static GridBagLayout gridBagLayout;  // layout di tipo GridBagLayout per la schermata menuScreen e sue componenti
	private static GroupLayout gameLayout;  // layout per la schermata di gioco/mapScreen
	private static SpringLayout springLayout;  // layout (personalizzato successivamente) di tipo SpringLayout per la schermata leadbScreen e sue componenti
	private static MouseListener[] t;  // array di MouseListener per la tabella
	private static MouseListener[] h;  // array di MouseListener per gli header della tabella
	private static DefaultTableCellRenderer centerRenderer;  // oggetto di tipo DefaultTableCellRenderer, customizzato successivamente
	private ButtonGroup btnGrpSound;
	private ButtonGroup btnGrpScreen;

	public void panelsBuilder() {
		fntTable=new Font("Impact", Font.PLAIN, 17); // originariamente Tahoma (Bold), poi Arial (Bold)
		fntLeadb=new Font("Gill Sans Ultra Bold", Font.PLAIN, 50); // solo su piattaforma Windows 
		menuBkg=new Color(119, 119, 119); 
		mapBkg=new Color(0, 180, 30); 
		springLayout=new SpringLayout(); 
		gridBagLayout=new GridBagLayout();
		menuScreenBuilder();
		gameScreenBuilder();
		leadbScreenBuilder();
		rulesScreenBuilder();
		optionsScreenBuilder();
	}

	/**
	 * Definisce le componenti della schermata menu principale
	 */
	private void menuScreenBuilder() {
		/*
		 * background aggiunto come propriet� Icon dell'oggetto JLabel
		 */
		background=new JLabel();
		bg=new ImageIcon(getClass().getClassLoader().getResource("CrossRoads/Resources/gfx/menu/background.png"));
		background.setIcon(bg);
		background.setVerticalAlignment(JLabel.CENTER);
		background.setHorizontalAlignment(JLabel.CENTER);
		getBgWidth();
		getBgHeight();

		/*
		 * menuScreen pannello principale contenente immagine di sfondo e vari JButton
		 */
		menuScreen=new JPanel(gridBagLayout);
		menuScreen.setOpaque(false);
		menuLayout=new GroupLayout(AppWindow.getFrame());  // layout customizzato di tipo GroupLayout; per argomento prende il contentPane primario del JFrame che agisce da contenitore ospitante
		AppWindow.getFrame().(menuLayout);  
		menuLayout.setHorizontalGroup(
				menuLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(menuScreen, GroupLayout.Alignment.CENTER, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(menuLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(background, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		menuLayout.setVerticalGroup(
				menuLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(menuScreen, GroupLayout.Alignment.CENTER, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(menuLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(menuLayout.createSequentialGroup()
								.addComponent(background, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								)) 
				);
	}


	/**
	 * Definisce le componenti della schermata di gioco
	 */
	private void gameScreenBuilder() {
		/*
		 * Mappa grafica, drawnMap, definita come ImageIcon di un JLabel
		 */
		drawnMap=new JLabel();
		drawnMap.setVerticalAlignment(SwingConstants.CENTER);
		drawnMap.setHorizontalAlignment(SwingConstants.CENTER);
		drawnShadows=new JLabel();
		drawnShadows.setVerticalAlignment(SwingConstants.CENTER);
		drawnShadows.setHorizontalAlignment(SwingConstants.CENTER);
		drawnBuildings=new JLabel();
		drawnBuildings.setVerticalAlignment(SwingConstants.CENTER);
		drawnBuildings.setHorizontalAlignment(SwingConstants.CENTER);		

		/*
		 * gameScreen pannello su cui "spawneranno" i nostri oggetti quali veicoli e semafori
		 */
		gameScreen=new JPanel();
		gameScreen.setLayout(null);
		gameScreen.setVisible(false);
		gameScreen.setOpaque(false);

		/*
		 * mapScreen pannello adibito a visualizzare la mappa di gioco nella sua interezza, includendo gameScreen
		 */
		mapScreen=new JPanel();
		mapScreen.setVisible(false);
		mapScreen.setOpaque(true);
		mapScreen.setBackground(mapBkg);
		gameLayout=new GroupLayout(mapScreen);  //layout customizzato di tipo GroupLayout; per argomento prende il JPanel mapScreen che agisce da contenitore ospitante
		gameLayout.setHorizontalGroup(
				gameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(gameScreen, GroupLayout.Alignment.CENTER, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(gameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(drawnMap, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		gameLayout.setVerticalGroup(
				gameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(gameScreen, GroupLayout.Alignment.CENTER, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(gameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(gameLayout.createSequentialGroup()
								.addComponent(drawnMap, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								)) 
				);
		mapScreen.setLayout(gameLayout);
	}

	/**
	 * Definisce le componenti della schermata Leaderboard
	 */
	private void leadbScreenBuilder() {
		/*
		 * leadbScreen agisce da schermata di visualizzazione delle statistiche di gioco tramite JTable
		 */
		leadbScreen=new JPanel(springLayout);
		leadbScreen.setVisible(false);
		leadbScreen.setBackground(menuBkg);

		searchBar=new JTextField();
		searchBar.setFocusable(true);
		searchBar.setHorizontalAlignment(SwingConstants.CENTER);
		searchBar.setFont(new Font("Tahoma", Font.BOLD, 16));
		searchBar.setSelectedTextColor(Color.YELLOW);
		searchBar.setCaretColor(Color.ORANGE);
		searchBar.setBackground(Color.DARK_GRAY);
		searchBar.setForeground(Color.ORANGE);
		springLayout.putConstraint(SpringLayout.NORTH, searchBar, 50, SpringLayout.NORTH, leadbScreen);
		springLayout.putConstraint(SpringLayout.WEST, searchBar, 700, SpringLayout.WEST, leadbScreen);
		springLayout.putConstraint(SpringLayout.SOUTH, searchBar, 80, SpringLayout.NORTH, leadbScreen);
		springLayout.putConstraint(SpringLayout.EAST, searchBar, 860, SpringLayout.WEST, leadbScreen);
		leadbScreen.add(searchBar);
		searchBar.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			@SuppressWarnings("static-access")
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==e.VK_ENTER)
					btnSearch.doClick();
				if(searchBar.getText().length()>10) {
					searchBar.setEditable(false);
				}
				keyCount=searchBar.getText().length();
			}
			@SuppressWarnings("static-access")
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==e.VK_BACK_SPACE || e.getKeyCode()==e.VK_DELETE) {
					keyCount--;
					if(keyCount<0) keyCount=0;
					else if(!searchBar.isEditable()) searchBar.setEditable(true);
				}
				else keyCount++; // necessario se qualcuno decide di tener premuto un tasto
				if(keyCount>10) {
					keyCount=10;
					searchBar.setEditable(false);
				}
				else if(searchBar.getText().length()!=keyCount) {
					searchBar.setEditable(true);
				}
			}
		});
		searchBar.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent selectText) {
				if(searchBar.getSelectedText()!=null)
					keyCount-=searchBar.getSelectedText().length();
			}
		});

		/*
		 * tasto Search per leadbScreen da mettere in una funzione a parte, perch� la gerarchia in questo caso 
		 * � fondamentale
		 */
		btnSearch=new JButton();
		btnSearch.addMouseListener(SfxManager.getBtnSFX());
		springLayout.putConstraint(SpringLayout.NORTH, btnSearch, 50, SpringLayout.NORTH, leadbScreen);
		springLayout.putConstraint(SpringLayout.WEST, btnSearch, 860, SpringLayout.WEST, leadbScreen);
		springLayout.putConstraint(SpringLayout.SOUTH, btnSearch, 80, SpringLayout.NORTH, leadbScreen);
		springLayout.putConstraint(SpringLayout.EAST, btnSearch, 910, SpringLayout.WEST, leadbScreen);
		leadbScreen.add(btnSearch);
		try {
			searchButtonIcon = ImageIO.read(getClass().getClassLoader().getResource(
					"CrossRoads/Resources/gfx/misc/searchButton.png"));
			pressedSearchButtonIcon=ImageIO.read(getClass().getClassLoader().getResource(
					"CrossRoads/Resources/gfx/misc/pressedSearchButton.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		btnSearch.setIcon(new ImageIcon(searchButtonIcon));
		btnSearch.setPressedIcon(new ImageIcon(pressedSearchButtonIcon));
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				wantedName=searchBar.getText();
				/*if(wantedName.equals("") || wantedName.equals("*") || wantedName.equals("all") || wantedName.equals("ALL") || wantedName.equals("All")) {
					Database.completeJTables();
					setTableGFX(Database.getTblEasy());
					setTableGFX(Database.getTblMedium());
					setTableGFX(Database.getTblHard());
				}
				else {*/
				Database.restrictedJTables();
				setTableGFX(Database.getTblEasy());
				setTableGFX(Database.getTblMedium());
				setTableGFX(Database.getTblHard());
				//}
			}
		});

		tabbedLevels=new JTabbedPane(JTabbedPane.TOP);
		tabbedLevels.setFont(fntTable);
		springLayout.putConstraint(SpringLayout.NORTH, tabbedLevels, 50, SpringLayout.NORTH, leadbScreen);
		springLayout.putConstraint(SpringLayout.WEST, tabbedLevels, 50, SpringLayout.WEST, leadbScreen);
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedLevels, 670, SpringLayout.NORTH, leadbScreen);
		springLayout.putConstraint(SpringLayout.EAST, tabbedLevels, 910, SpringLayout.WEST, leadbScreen);
		leadbScreen.add(tabbedLevels, springLayout);

		leadbTitle=new JLabel("Leaderboard");
		springLayout.putConstraint(SpringLayout.NORTH, leadbTitle, -40, SpringLayout.NORTH, tabbedLevels);
		springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, leadbTitle, 0, SpringLayout.HORIZONTAL_CENTER, leadbScreen);
		leadbTitle.setForeground(Color.BLACK);
		leadbTitle.setFont(fntLeadb);
		leadbScreen.add(leadbTitle);

		t=new MouseListener[2];  // array di MouseListener per la tabella 
		h=new MouseListener[2];  // array di MouseListener per gli header della tabella
		centerRenderer=new DefaultTableCellRenderer();

		t=Database.getTblEasy().getMouseListeners();
		h=Database.getTblEasy().getTableHeader().getMouseListeners();
		Database.getTblEasy().removeMouseListener(t[1]);  // non si pu� pi� modificare il contenuto della singola cella
		Database.getTblEasy().getTableHeader().removeMouseListener(h[1]);  // non si pu� pi� selezionare l'header della tabella

		t=Database.getTblMedium().getMouseListeners();
		h=Database.getTblMedium().getTableHeader().getMouseListeners();
		Database.getTblMedium().removeMouseListener(t[1]);
		Database.getTblMedium().getTableHeader().removeMouseListener(h[1]);

		t=Database.getTblHard().getMouseListeners();
		h=Database.getTblHard().getTableHeader().getMouseListeners();
		Database.getTblHard().removeMouseListener(t[1]);
		Database.getTblHard().getTableHeader().removeMouseListener(h[1]);

		setTableGFX(Database.getTblEasy());  // vengono settate le propriet� relative alla grafica comuni a ciascuna JTable
		setTableGFX(Database.getTblMedium());
		setTableGFX(Database.getTblHard());

		/*
		 * oggetti JScrollPane, associati tramite appositi tab alle tabelle che ciascuno visualizza
		 */
		scrollPanelEasy=new JScrollPane(Database.getTblEasy());
		tabbedLevels.addTab("Easy", null, scrollPanelEasy, null);
		scrollPanelMedium=new JScrollPane(Database.getTblMedium());
		tabbedLevels.addTab("Medium", null, scrollPanelMedium, null);
		scrollPanelHard=new JScrollPane(Database.getTblHard());
		tabbedLevels.addTab("Hard", null, scrollPanelHard, null);
	}

	/**
	 * Definisce le componenti della schermata di visualizzazione delle regole di gioco
	 */
	private void rulesScreenBuilder() {
		/*
		 * rulesScreen agisce da schermata di visualizzazione delle regole di gioco
		 */
		rulesScreen=new JPanel(springLayout);
		rulesScreen.setVisible(false);
		rulesScreen.setBackground(menuBkg);
	}

	/**
	 * Definisce le componenti della schermata delle opzioni
	 */
	private void optionsScreenBuilder() {
		/*
		 * optionsScreen funge da schermata per la customizzazione di opzioni
		 */
		optionsScreen=new JPanel(springLayout);
		optionsScreen.setVisible(false);
		optionsScreen.setBackground(menuBkg);

		/*
		 * Crea i contenuti di optionsScreen
		 */
		btnGrpSound=new ButtonGroup();
		btnGrpScreen=new ButtonGroup();

		JCheckBox chckbxSoundOn=new JCheckBox("Sound On");
		chckbxSoundOn.setForeground(Color.YELLOW);
		chckbxSoundOn.setFont(fntTable);
		btnGrpSound.add(chckbxSoundOn);
		optionsScreen.add(chckbxSoundOn);

		JCheckBox chckbxSoundOff=new JCheckBox("Sound Off");
		chckbxSoundOff.setForeground(Color.YELLOW);
		chckbxSoundOff.setFont(fntTable);
		chckbxSoundOff.setSelected(true);
		springLayout.putConstraint(SpringLayout.WEST, chckbxSoundOn, 0, SpringLayout.WEST, chckbxSoundOff);
		springLayout.putConstraint(SpringLayout.SOUTH, chckbxSoundOn, -6, SpringLayout.NORTH, chckbxSoundOff);
		btnGrpSound.add(chckbxSoundOff);
		optionsScreen.add(chckbxSoundOff);

		JCheckBox chckbxFullOn=new JCheckBox("1280x800");
		chckbxFullOn.setForeground(Color.YELLOW);
		chckbxFullOn.setFont(fntTable);
		springLayout.putConstraint(SpringLayout.WEST, chckbxSoundOff, 0, SpringLayout.WEST, chckbxFullOn);
		springLayout.putConstraint(SpringLayout.SOUTH, chckbxSoundOff, -38, SpringLayout.NORTH, chckbxFullOn);
		chckbxFullOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//			setExtendedState(JFrame.MAXIMIZED_BOTH);
				AppWindow.getFrame().resizeBy(1280, 800);
			}
		});
		btnGrpScreen.add(chckbxFullOn);
		optionsScreen.add(chckbxFullOn);

		JCheckBox chckbxFullOff=new JCheckBox("960x720");
		chckbxFullOff.setForeground(Color.YELLOW);
		chckbxFullOff.setFont(fntTable);
		chckbxFullOff.setSelected(true);
		springLayout.putConstraint(SpringLayout.WEST, chckbxFullOn, 0, SpringLayout.WEST, chckbxFullOff);
		springLayout.putConstraint(SpringLayout.SOUTH, chckbxFullOn, -6, SpringLayout.NORTH, chckbxFullOff);
		springLayout.putConstraint(SpringLayout.NORTH, chckbxFullOff, 280, SpringLayout.NORTH, optionsScreen);
		springLayout.putConstraint(SpringLayout.EAST, chckbxFullOff, -416, SpringLayout.EAST, optionsScreen);
		chckbxFullOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//				setExtendedState(JFrame.NORMAL); 
				AppWindow.getFrame().resizeBy(960, 720);
			}
		});
		btnGrpScreen.add(chckbxFullOff);
		optionsScreen.add(chckbxFullOff);

		/*
		 * pannello return per la schermata gameScreen
		 */
		returnBox=new JPanel();
		returnLabel=new JLabel();
		returnBox.setOpaque(false);
		gameScreen.add(returnBox);
		try {
			icon = ImageIO.read(getClass().getClassLoader().getResource(
					"CrossRoads/Resources/gfx/misc/backButton.png"));
			pressedIcon=ImageIO.read(getClass().getClassLoader().getResource(
					"CrossRoads/Resources/gfx/misc/pressedBackButton.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		returnLabel.setIcon(new ImageIcon(icon));
		returnBox.setBounds(10, 10, icon.getWidth(null), icon.getHeight(null)); // da modificare in seguito
		returnBox.add(returnLabel);
		returnBox.addMouseListener(new MouseAdapter() { // mouse listener
			@SuppressWarnings("static-access")
			public void mousePressed(MouseEvent e) {
				if(e.getButton()==e.BUTTON1)
					returnLabel.setIcon(new ImageIcon(pressedIcon));
			}
			@SuppressWarnings("static-access")
			public void mouseReleased(MouseEvent e) {
				if(inside && e.getButton()==e.BUTTON1) {
					returnLabel.setIcon(new ImageIcon(icon));
					bkgNoise.close();
					Game.stopSession();
					AppWindow.getFrame().setContentPane(background);
					background.add(menuScreen);
					menuScreen.setVisible(true);
					menuMusicTrigger();
				}
				else
					returnLabel.setIcon(new ImageIcon(icon));
			}
			public void mouseEntered(MouseEvent e) { // se si esce dal confine del pannello il click viene disabilitato
				inside=true;
			}
			public void mouseExited(MouseEvent e) {
				inside=false;
			}
		});	    
	}

	/**
	 * Setta varie propriet� grafiche ad una JTable
	 */
	public static void setTableGFX(JTable table) {
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for(int i=0; i<table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setResizable(false);
		}
		for(int i=0; i<table.getColumnCount(); i++){
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);  // centra le scritte delle colonne
		}
		table.getTableHeader().setFont(getFntTable());
		table.setFont(getFntTable());		
		table.setGridColor(Color.BLACK);
		table.setShowVerticalLines(false);
		table.setRowHeight(20);
		table.setFillsViewportHeight(true);
		table.getTableHeader().setBackground(Color.BLACK);
		table.getTableHeader().setForeground(Color.ORANGE);
		table.setCellSelectionEnabled(false);
		table.setBackground(Color.DARK_GRAY);
		table.setForeground(Color.YELLOW);
	}

	public static JLabel getShadows() {
		return drawnShadows;
	}

	public static JLabel getBuildings() {
		return drawnBuildings;
	}

	/**
	 * Setta l'immagine della mappa a seconda del livello di gioco
	 */
	public void setMap() {
		if(difficulty=="Easy") dm=
				new ImageIcon(getClass().getClassLoader().getResource("CrossRoads/Resources/gfx/maps/mapEasy.png"));
		else if(difficulty=="Medium") dm=
				new ImageIcon(getClass().getClassLoader().getResource("CrossRoads/Resources/gfx/maps/mapMedium.png"));
		else dm=
				new ImageIcon(getClass().getClassLoader().getResource("CrossRoads/Resources/gfx/maps/mapHard.png"));
		drawnMap.setIcon(dm);
	}

	public void setShadows() {
		if(difficulty=="Easy") ds=
				new ImageIcon(getClass().getClassLoader().getResource("CrossRoads/Resources/gfx/maps/mapEasyShadows.png"));
		else if(difficulty=="Medium") ds=
				new ImageIcon(getClass().getClassLoader().getResource("CrossRoads/Resources/gfx/maps/mapMediumShadows.png"));
		else ds=
				new ImageIcon(getClass().getClassLoader().getResource("CrossRoads/Resources/gfx/maps/mapHardShadows.png"));
		drawnShadows.setIcon(ds);
	}

	public void setBuildings() {
		if(difficulty=="Easy") db=
				new ImageIcon(getClass().getClassLoader().getResource("CrossRoads/Resources/gfx/maps/mapEasyBuildings.png"));
		else if(difficulty=="Medium") db=
				new ImageIcon(getClass().getClassLoader().getResource("CrossRoads/Resources/gfx/maps/mapMediumBuildings.png"));
		else db=
				new ImageIcon(getClass().getClassLoader().getResource("CrossRoads/Resources/gfx/maps/mapHardBuildings.png"));
		drawnBuildings.setIcon(db);
	}

	/**
	 * Restituisce la larghezza dell'ImageIcon bg, Icon del JLabel background
	 * @return bg.getIconWidth()
	 */
	public static int getBgWidth() {
		return bg.getIconWidth();
	}

	/**
	 * Restituisce l'altezza dell'ImageIcon bg, Icon del JLabel background
	 * @return bg.getIconHeight()
	 */
	public static int getBgHeight() {
		return bg.getIconHeight();
	}

	/**
	 * Restituisce l'oggetto customizzato fntTable di tipo Font
	 * @return fntTable
	 */
	public static Font getFntTable() {
		return fntTable;
	}

	/**
	 * Restituisce la larghezza dell'ImageIcon dm, Icon del JLabel drawnMap
	 * @return dm.getIconWidth()
	 */
	public static int getDMWidth() {
		return dm.getIconWidth();
	}

	/**
	 * Restituisce l'altezza dell'ImageIcon dm, Icon del JLabel drawnMap
	 * @return dm.getIconHeight()
	 */
	public static int getDMHeight() {
		return dm.getIconHeight();
	}

	/**
	 * Restituisce il JPanel gameScreen
	 * @return gameScreen
	 */
	public static JPanel getGameScreen() {
		return gameScreen;
	}

	/**
	 * Restituisce il JPanel leadbScreen
	 * @return leadbScreen
	 */
	public static JPanel getleadbScreen() {
		return leadbScreen;
	}

	/**
	 * Restituisce il JPanel menuScreen
	 * @return menuScreen
	 */
	public static JPanel getMenuScreen() {
		return menuScreen;
	}

	/**
	 * Restituisce il JLabel background
	 * @return background
	 */
	public static JLabel getBkg() {
		return background;
	}


	/**
	 * Restituisce il JTabbedPane tabbedLevels
	 * @return tabbedLevels
	 */
	public static JTabbedPane getTabs() {
		return tabbedLevels;
	}
}


