package CrossRoads;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import CrossRoads.GUI.AppWindow;
import CrossRoads.GameSession.Entities.Score;
import CrossRoads.GameSession.Entities.Vehicle;

/**
 * Classe che rappresenta il Tempo
 */
public class Time {
	private static int sessionActualTime;
	
	/*
	 * orari:
	 * 0 > 00:00 |  8 > 04:00 | 16 > 08:00 | 24 > 12:00 | 32 > 16:00 | 40 > 20:00
	 * 2 > 01:00 | 10 > 05:00 | 18 > 09:00 | 26 > 13:00 | 34 > 17:00 | 42 > 21:00
	 * 4 > 02:00 | 12 > 06:00 | 20 > 10:00 | 28 > 14:00 | 36 > 18:00 | 44 > 22:00
	 * 6 > 03:00 | 14 > 07:00 | 22 > 11:00 | 30 > 15:00 | 38 > 19:00 | 46 > 23:00
	 */
	
	/*
	 * sole sorge alle 6, tramonta alle 20
	 * -> day: 12 <= x <= 38
	 * -> night: x < 12, x > 38
	 */
	
	private static Timer halfHourTimer; 
	private static ActionListener halfHourTask=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			/*--------------*/
			if(sessionActualTime>=12 && sessionActualTime<=38)
				Score.setdaytime("Day");
			else
				Score.setdaytime("Night");
			/*--------------*/
			if(sessionActualTime==48)
				sessionActualTime=0; // nuova giornata
			else 
				sessionActualTime++;
			shiftShadow();
		}
	};
	
	public static void startTime(int i) {
		sessionActualTime=i;
		shiftShadow();
		halfHourTimer=new Timer(4000, halfHourTask); // decidere ogni quanti secondi passa mezz'ora in gioco
		halfHourTimer.start();
	}
	public static long getTime() {
		return System.currentTimeMillis();
	}
	public static int getActualTime() {
		return sessionActualTime;
	}
	public static void stopTime() {
		halfHourTimer.stop();
	}	
	private static void shiftShadow() { // gestione grafica
		if(sessionActualTime<=12) {
			AppWindow.getShadows().setBounds(AppWindow.getDMX(), AppWindow.getDMY(), 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(0);
			Vehicle.setYOffSet(0);
			Score.setdaytime("Night");
		}			
		else if(sessionActualTime<=14) {
			AppWindow.getShadows().setBounds(AppWindow.getDMX()-3, AppWindow.getDMY()-3, 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(-2);
			Vehicle.setYOffSet(-2);
			Score.setdaytime("Day");
		}			
		else if(sessionActualTime<=16) {
			AppWindow.getShadows().setBounds(AppWindow.getDMX()-3, AppWindow.getDMY()-2, 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(-2);
			Vehicle.setYOffSet(-2);
		}			
		else if(sessionActualTime<=18) {
			AppWindow.getShadows().setBounds(AppWindow.getDMX()-2, AppWindow.getDMY()-2, 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(-2);
			Vehicle.setYOffSet(-2);
		}
		else if(sessionActualTime<=20) {
			AppWindow.getShadows().setBounds(AppWindow.getDMX()-2, AppWindow.getDMY()-1, 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(-2);
			Vehicle.setYOffSet(-1);
		}
		else if(sessionActualTime<=22) {
			AppWindow.getShadows().setBounds(AppWindow.getDMX()-1, AppWindow.getDMY()-1, 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(-1);
			Vehicle.setYOffSet(-1);
		}
		else if(sessionActualTime<=24) {
			AppWindow.getShadows().setBounds(AppWindow.getDMX()-1, AppWindow.getDMY(), 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(-1);
			Vehicle.setYOffSet(0);
		}
		else if(sessionActualTime<=26) {
			AppWindow.getShadows().setBounds(AppWindow.getDMX(), AppWindow.getDMY(), 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(0);
			Vehicle.setYOffSet(0);
		}
		else if(sessionActualTime<=28) {
			AppWindow.getShadows().setBounds(AppWindow.getDMX(), AppWindow.getDMY()+1, 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(0);
			Vehicle.setYOffSet(1);
		}			
		else if(sessionActualTime<=30) {
			AppWindow.getShadows().setBounds(AppWindow.getDMX()+1, AppWindow.getDMY()+1, 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(1);
			Vehicle.setYOffSet(1);
		}
		else if(sessionActualTime<=32) {
			AppWindow.getShadows().setBounds(AppWindow.getDMX()+1, AppWindow.getDMY()+2, 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(1);
			Vehicle.setYOffSet(2);
		}			
		else if(sessionActualTime<=34) {
			AppWindow.getShadows().setBounds(AppWindow.getDMX()+2, AppWindow.getDMY()+2, 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(2);
			Vehicle.setYOffSet(2);
		}
		else if(sessionActualTime<=36) {
			AppWindow.getShadows().setBounds(AppWindow.getDMX()+2, AppWindow.getDMY()+3, 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(2);
			Vehicle.setYOffSet(2);
		}
		else if(sessionActualTime<=38) {
			AppWindow.getShadows().setBounds(AppWindow.getDMX()+3, AppWindow.getDMY()+3, 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(2);
			Vehicle.setYOffSet(2);
		}
		else {
			AppWindow.getShadows().setBounds(AppWindow.getDMX(), AppWindow.getDMY(), 
					AppWindow.getDMWidth(), AppWindow.getDMHeight());
			Vehicle.setXOffSet(0);
			Vehicle.setYOffSet(0);
			Score.setdaytime("Night");
		}			
	}
}
