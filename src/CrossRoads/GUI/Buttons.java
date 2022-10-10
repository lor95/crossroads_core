package CrossRoads.GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.SpringLayout;
import CrossRoads.Game;
import CrossRoads.SfxManager;
import CrossRoads.Time;
import CrossRoads.GameSession.Map;

public abstract class Buttons {
	private static JButton btnStart;
	private static JButton btnDifficulty;
	private static JButton btnLeaderboard;
	private static JButton btnRules;
	private static JButton btnOptions;
	private static JButton btnBack;
	private static JButton btnExit;
	private static String difficulty;
	private static int selector=0;  // variabile contatore usata in if innestati, permette la selezione della difficoltà sull'apposito tasto 
	private static GridBagConstraints gbc_btn;  // constraints del gridBagLayout per posizionare i JButton su menuScreen
	private static Font fntMenu;  // font per i tasti di menuScreen


	private void btnsForMainMenu() {
		fntMenu=new Font("Tahoma", Font.PLAIN, 12);
		difficulty="Easy";
		/*
		 * tasti per menuScreen, la schermata iniziale, secondo il GridBagLayout
		 */
		gbc_btn=new GridBagConstraints();
		gbc_btn.insets=new Insets(5,5,5,5);  // stabilisce la spaziatura tra i vari tasti, comune a tutti
		gbc_btn.fill=GridBagConstraints.HORIZONTAL;  // dimensione orizzontale dei tasti uguale per tutti

		gbc_btn.gridx=0;
		gbc_btn.gridy=0;
		btnStart=new JButton("Start");
		btnStart.setFont(fntMenu);
		btnStart.setBackground(Color.BLACK);
		btnStart.setForeground(Color.WHITE);
		menuScreen.add(btnStart,gbc_btn);
		btnStart.addMouseListener(SfxManager.getBtnSFX());
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { 
				menuMusic.close();
				startSession();
				bkgNoiseTrigger();
			}
		});
		gbc_btn.gridx=0;
		gbc_btn.gridy=1;
		btnDifficulty=new JButton(difficulty);
		btnDifficulty.setFont(fntMenu);
		btnDifficulty.setBackground(Color.BLACK);
		btnDifficulty.setForeground(setColorButton());
		menuScreen.add(btnDifficulty,gbc_btn);
		btnDifficulty.addMouseListener(SfxManager.getBtnSFX());
		btnDifficulty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selector++;
				setDifficulty();
				btnDifficulty.setText(difficulty);
				btnDifficulty.setForeground(setColorButton());
			}
		});
		gbc_btn.gridx=0;
		gbc_btn.gridy=2;
		btnLeaderboard=new JButton("Leaderboard");
		btnLeaderboard.setFont(fntMenu);
		btnLeaderboard.setBackground(Color.BLACK);
		btnLeaderboard.setForeground(Color.WHITE);
		menuScreen.add(btnLeaderboard,gbc_btn);
		btnLeaderboard.addMouseListener(SfxManager.getBtnSFX());
		btnLeaderboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setContentPane(leadbScreen);
				leadbScreen.setVisible(true);
			}
		});
		gbc_btn.gridx=0;
		gbc_btn.gridy=3;
		btnRules=new JButton("Rules");
		btnRules.setFont(fntMenu);
		btnRules.setBackground(Color.BLACK);
		btnRules.setForeground(Color.WHITE);
		menuScreen.add(btnRules,gbc_btn);
		btnRules.addMouseListener(SfxManager.getBtnSFX());
		btnRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setContentPane(rulesScreen);
				rulesScreen.setVisible(true);
			}
		});
		gbc_btn.gridx=0;
		gbc_btn.gridy=4;
		btnOptions=new JButton("Options");
		btnOptions.setFont(fntMenu);
		btnOptions.setBackground(Color.BLACK);
		btnOptions.setForeground(Color.WHITE);
		menuScreen.add(btnOptions,gbc_btn);
		btnOptions.addMouseListener(SfxManager.getBtnSFX());
		btnOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setContentPane(optionsScreen);
				optionsScreen.setVisible(true);
			}
		});
		gbc_btn.gridx=0;
		gbc_btn.gridy=5;
		btnExit=new JButton("Exit");
		btnExit.setFont(fntMenu);
		btnExit.setBackground(Color.BLACK);
		btnExit.setForeground(Color.WHITE);
		menuScreen.add(btnExit,gbc_btn);
		btnExit.addMouseListener(SfxManager.getBtnSFX());
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Game.exitProcedure();
			}
		});
	}

	private void btnBackToLeadb() {
		/*
		 * tasto Back per la schermata Leaderboard
		 */
		btnBack=new JButton("Back");
		btnBack.setFont(fntMenu);
		btnBack.setBackground(Color.BLACK);
		btnBack.setForeground(Color.WHITE);
		leadbScreen.add(btnBack);
		btnBack.addMouseListener(SfxManager.getBtnSFX());
		springLayout.putConstraint(SpringLayout.SOUTH, btnBack, -10, SpringLayout.SOUTH, leadbScreen);
		springLayout.putConstraint(SpringLayout.EAST, btnBack, -10, SpringLayout.EAST, leadbScreen);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				searchBar.setText("");
				setContentPane(background);
				background.add(menuScreen);
				menuScreen.setVisible(true);
			}
		});
	}

	private void btnBackToRules() {
		/*
		 * tasto Back per la schermata Rules
		 */
		btnBack=new JButton("Back");
		btnBack.setFont(fntMenu);
		btnBack.setBackground(Color.BLACK);
		btnBack.setForeground(Color.WHITE);
		rulesScreen.add(btnBack);
		btnBack.addMouseListener(SfxManager.getBtnSFX());
		springLayout.putConstraint(SpringLayout.SOUTH, btnBack, -10, SpringLayout.SOUTH, rulesScreen);
		springLayout.putConstraint(SpringLayout.EAST, btnBack, -10, SpringLayout.EAST, rulesScreen);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setContentPane(background);
				background.add(menuScreen);
				menuScreen.setVisible(true);
			}
		});
	}

	private void btnBackToOpts() {
		/*
		 * tasto Back per la schermata Options
		 */
		btnBack=new JButton("Back");
		btnBack.setFont(fntMenu);
		btnBack.setBackground(Color.BLACK);
		btnBack.setForeground(Color.WHITE);
		optionsScreen.add(btnBack);
		btnBack.addMouseListener(SfxManager.getBtnSFX());
		springLayout.putConstraint(SpringLayout.SOUTH, btnBack, -10, SpringLayout.SOUTH, optionsScreen);
		springLayout.putConstraint(SpringLayout.EAST, btnBack, -10, SpringLayout.EAST, optionsScreen);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setContentPane(background);
				background.add(menuScreen);
				menuScreen.setVisible(true);
			}
		});
	}

	/**
	 * Insieme di istruzioni effettuate per garantire l'inizio di una nuova sessione di gioco
	 */
	public void startSession() {
		matchDate=new Date(Time.getTime());
		matchStartTime=Time.getTime();
		setMap();
		setShadows();
		setBuildings();		
		setContentPane(mapScreen);
		menuScreen.setVisible(false);
		mapScreen.setVisible(true);
		gameScreen.setVisible(true);
		new Map(difficulty);
	}

	/**
	 * Setta la difficoltà tramite il contatore di tipo int, selector, e if innestati
	 */
	public static void setDifficulty() {
		if(selector==0) difficulty="Easy";
		else if(selector==1) difficulty="Medium";
		else if(selector==2) {
			difficulty="Hard";
			selector=-1;
		}
	}

	/**
	 * Setta uno di tre colori per il font del JButton relativo alla difficoltà, a seconda del livello scelto
	 * @return lvlColor
	 */
	public static Color setColorButton() {
		Color lvlColor;
		if(difficulty=="Easy") lvlColor=Color.GREEN;
		else if(difficulty=="Medium") lvlColor=Color.YELLOW;
		else lvlColor=Color.RED;
		return lvlColor;
	}

}
