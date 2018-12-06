package CrossRoads;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;

public abstract class SfxManager {
	private static MouseAdapter btnSFX;
	private static Clip btnMousePress;
	private static Clip btnMouseEntered;
	private static boolean insideBtn;
	private static AudioInputStream[] horn=new AudioInputStream[8];
	private static InputStream[] hStream=new BufferedInputStream[8];

	/* suoni principali */
	
	/**
     * Restituisce il suono del menu principale di gioco
     * @return
     */
	public static AudioInputStream getMenuMusic() {
    	AudioInputStream menuMusic=null;
    	InputStream iStream=(Game.class.getResourceAsStream("Resources/sfx/main/menuMusic.wav"));
    	InputStream vCStream=new BufferedInputStream(iStream);
    	try {
			menuMusic=AudioSystem.getAudioInputStream(vCStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return menuMusic;
    }
	
	/**
     * Restituisce il suono di background di sessione
     * @return
     */
	public static AudioInputStream getBkgNoise() {
    	AudioInputStream bkgNoise=null;
    	InputStream iStream=(Game.class.getResourceAsStream("Resources/sfx/main/bkgNoise.wav"));
    	InputStream vCStream=new BufferedInputStream(iStream);
    	try {
			bkgNoise=AudioSystem.getAudioInputStream(vCStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return bkgNoise;
    }
	
	/**
	 * Imposta il MouseAdapter adottato per i tasti del menu principale
	 */
	public static void setBtnSFX() {
		btnSFX=new MouseAdapter() { 
			@SuppressWarnings("static-access")
			public void mouseReleased(MouseEvent e) {
				if(e.getButton()==e.BUTTON1 && insideBtn) {
					try {
						btnMousePress=AudioSystem.getClip();
						InputStream iStream=getClass().getClassLoader().
								getResourceAsStream("CrossRoads/Resources/sfx/btn/menubtnPress.wav");
						InputStream bInput=new BufferedInputStream(iStream);
						AudioInputStream btnStream=AudioSystem.getAudioInputStream(bInput);
						btnMousePress.open(btnStream);
						btnMousePress.loop(0);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			public void mouseEntered(MouseEvent e) {
				insideBtn=true;
				try {
					btnMouseEntered=AudioSystem.getClip();
					InputStream iStream=getClass().getClassLoader().
							getResourceAsStream("CrossRoads/Resources/sfx/btn/menubtnEnter.wav");
					InputStream bInput=new BufferedInputStream(iStream);
					AudioInputStream btnStream=AudioSystem.getAudioInputStream(bInput);
					btnMouseEntered.open(btnStream);
					btnMouseEntered.loop(0);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			public void mouseExited(MouseEvent e) {
				insideBtn=false;
			}
		};
	}
	
	public static MouseAdapter getBtnSFX() {
		return btnSFX;
	}
	
	/* suoni veicoli */
	
	/**
	 * Restituisce il suono di punto ottenuto per Vehicle
	 * @return
	 */
	public static AudioInputStream getPointUp() {
    	AudioInputStream pointUp=null;
    	InputStream iStream=(Game.class.getResourceAsStream("Resources/sfx/vehicles/pointUp.wav"));
    	InputStream vCStream=new BufferedInputStream(iStream);
    	try {
			pointUp=AudioSystem.getAudioInputStream(vCStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return pointUp;
    }
	
	/**
	 * Restituisce il suono di crash di un Vehicle
	 * @return
	 */
	public static AudioInputStream getVCrash() {
    	AudioInputStream vCrash=null;
    	InputStream iStream=(Game.class.getResourceAsStream("Resources/sfx/gameover/vehicleCrash.wav"));
    	InputStream vCStream=new BufferedInputStream(iStream);
		try {
			vCrash=AudioSystem.getAudioInputStream(vCStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return vCrash;
    }
	
	/**
     * Restituisce l'array di tipo AudioInputStream, horn
     * @return horn[]
     */
    public static AudioInputStream[] getHorns() {
    	return horn;
    }
    
    /**
     * Restituisce l'oggetto di tipo AudioInputStream, horn
     * @param i
     * @return horn[i]
     */
    public static AudioInputStream getHorn(int i) {
    	restoreHorn(i);
    	return horn[i];
    }
    
    
    /**
     * Ripristina un clacson
     * @param i
     */
    private static void restoreHorn(int i) {
    	InputStream iStream=(Game.class.
				getResourceAsStream("Resources/sfx/vehicles/horn"+(i+1)+".wav"));
		hStream[i]=new BufferedInputStream(iStream);
		try {
			horn[i]=AudioSystem.getAudioInputStream(hStream[i]);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	/* suoni pedoni */
    
    /**
     * Restituisce il suono di crash di un Pedestrian
     * @return
     */
    public static AudioInputStream getPCrash() {
    	AudioInputStream pCrash=null;
    	InputStream iStream=(Game.class.getResourceAsStream("Resources/sfx/gameover/pedestrianCrash.wav"));
    	InputStream vCStream=new BufferedInputStream(iStream);
		try {
			pCrash=AudioSystem.getAudioInputStream(vCStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return pCrash;
    }
    
    /* suoni coda */
    
    public static AudioInputStream getQueueExceeded() {
    	AudioInputStream queueEx=null;
    	InputStream iStream=(Game.class.getResourceAsStream("Resources/sfx/gameover/queueExceeded.wav"));
    	InputStream vCStream=new BufferedInputStream(iStream);
		try {
			queueEx=AudioSystem.getAudioInputStream(vCStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return queueEx;
    }
    
    /* suoni semaforo */
    
    /**
     * Restituisce il suono di click di un TrafficLight
     * @return
     */
    public static AudioInputStream getTlCLick() {
    	AudioInputStream tlClick=null;
    	InputStream iStream=(Game.class.getResourceAsStream("Resources/sfx/tl/tlClick.wav"));
    	InputStream vCStream=new BufferedInputStream(iStream);
    	try {
			tlClick=AudioSystem.getAudioInputStream(vCStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return tlClick;
    }
    
    /**
     * Restituisce il suono di rottura di un TrafficLight
     * @return
     */
    public static AudioInputStream getTlBreak() {
    	AudioInputStream tlBreak=null;
    	InputStream iStream=(Game.class.getResourceAsStream("Resources/sfx/tl/tlBreak.wav"));
    	InputStream vCStream=new BufferedInputStream(iStream);
    	try {
			tlBreak=AudioSystem.getAudioInputStream(vCStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return tlBreak;
    }
    
    /**
     * Restituisce il suono di riparazione del TrafficLight
     * @return
     */
    public static AudioInputStream getTlRepair() {
    	AudioInputStream tlRepair=null;
    	InputStream iStream=(Game.class.getResourceAsStream("Resources/sfx/tl/tlRepair.wav"));
    	InputStream vCStream=new BufferedInputStream(iStream);
    	try {
			tlRepair=AudioSystem.getAudioInputStream(vCStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return tlRepair;
    }
    
    /**
     * Restituisce il suono di riparazione effettuato di TrafficLight
     * @return
     */
    public static AudioInputStream getTlRepairSuccess() {
    	AudioInputStream tlRepairSuccess=null;
    	InputStream iStream=(Game.class.getResourceAsStream("Resources/sfx/tl/tlRepairSuccess.wav"));
    	InputStream vCStream=new BufferedInputStream(iStream);
    	try {
			tlRepairSuccess=AudioSystem.getAudioInputStream(vCStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return tlRepairSuccess;
    }
    
}
