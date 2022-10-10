package CrossRoads;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import CrossRoads.GUI.*;
import CrossRoads.GameSession.Map;

/**
 * Classe che definisce e inizializza le principali funzionalità dell'applicazione
 */
public class Game {
	private static boolean masterEnabler;
	private static long matchEndTime;
	private static BufferedImage[] angryCloud=new BufferedImage[8];
	private static BufferedImage[] car=new BufferedImage[14]; // numero di risorse con lo stesso nome in gfx
	private static BufferedImage[] bike=new BufferedImage[7];
	private static BufferedImage[] truck=new BufferedImage[7];
	private static BufferedImage arrow;

	/**
	 * Inizializza il package grafico Nimbus introdotto in Java SE 6. Se non permesso dal sistema operativo su cui agisce la JVM, si usa il look di default.
	 * Si implementa l'interfaccia Runnable in quanto un Thread deve creare l'interfaccia grafica dell'applicazione.
	 */
	public Game() {
		loadResources();
		SfxManager.setBtnSFX();
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName()); // imposta il Nimbus LookAndFeel
				}
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AppWindow.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(AppWindow.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(AppWindow.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.getLogger(AppWindow.class.getName()).log(Level.SEVERE, null, ex);
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new AppWindow(); // genera la finestra dell'applicazione
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}); 
	}  
	
	/**
	 * Invoca metodi di Database per stabilire una connessione al DB, creare una table, e richiama il costruttore Game, 
	 * inizializzatore delle componenti grafiche
	 * @param args
	 */
    public static void main(String args[]) {
        try{
			Database.createConnection();
			Database.createTable("player");
			new Game(); // metodo costruttore
        } catch (Throwable e) {
        	JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage());
        	throw e;
        }
    }
    
    /**
     * Determina le azioni successive alla conclusione di una sessione di gioco;
     * tra queste il passaggio ad una schermata popup, la "pulizia" della mappa di gioco, etc.
     */
	public static void endSession() {
    	masterEnabler=false;
    	matchEndTime=Time.getTime();
    	Time.stopTime();
    	GameOverDialog.setTextScore();
        AppWindow.getDialog().setVisible(true);
        AppWindow.getDialog().setAlwaysOnTop(true);  // il dialog sempre sopra AppWindow, non si perde il focus se l'utente clicca su AppWindow che è in background
		GameOverDialog.getGameOverPrompt().setVisible(true);
        AppWindow.getDialog().setContentPane(GameOverDialog.getGameOverPrompt()); 
        AppWindow.getBkgNoise().close();
    	AppWindow.menuMusicTrigger();
    	Map.clear(); // pulisci AppWindow.gameScreen
    }
    
    public static void stopSession() {
    	masterEnabler=false;
    	Time.stopTime();
    	Map.clear();
    }
    
    /**
     * Carica le risorse grafiche necessarie dai relativi path 
     */
    private void loadResources() {
        try {
        	int i;
        	
        	/* graphics */
        	for(i=1; i<=angryCloud.length; i++) {
        		angryCloud[i-1]=ImageIO.read(getClass().getResource("Resources/gfx/misc/angryCloud"+i+".png"));
        	}
        	for(i=1; i<=car.length; i++) {
        		car[i-1]=ImageIO.read(getClass().getResource("Resources/gfx/cars/car"+i+".png"));
        	}
        	for(i=1; i<=bike.length; i++) {
        		bike[i-1]=ImageIO.read(getClass().getResource("Resources/gfx/bikes/bike"+i+".png"));
        	}
        	for(i=1; i<=truck.length; i++) {
        		truck[i-1]=ImageIO.read(getClass().getResource("Resources/gfx/trucks/truck"+i+".png"));
        	}
        	arrow=ImageIO.read(getClass().getResource("Resources/gfx/misc/arrow.png"));
        	
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage());
		}
    }
    
    /**
     * Stabilisce la procedura di chiusura dell'applicazione
     */
    public static void exitProcedure() {
		Database.closeConnection();
		System.exit(0); // chiudi JVM
	}
    
    /**
     * Assegna un valore booleano passato per argomento alla proprietà masterEnabler
     * @param boolVar
     */
    public static void setMasterEnabler(boolean boolVar) {
    	masterEnabler=boolVar;
    }
    
    /**
     * Restituisce la variabile booleana masterEnabler
     * @return masterEnabler
     */
    public static boolean getMasterEnabler() {
    	return masterEnabler;
    }
    
    /**
     * Restituisce l'array di tipo BufferedImage, car
     * @return car[]
     */
    public static BufferedImage[] getCars() {
    	return car;
    }
    
    /**
     * Restituisce l'array di tipo BufferedImage, bike
     * @return bike[]
     */
    public static BufferedImage[] getBikes() {
    	return bike;
    }
    
    /**
     * Restituisce l'array di tipo BufferedImage, truck
     * @return truck[]
     */
    public static BufferedImage[] getTrucks() {
    	return truck;
    }
    
    public static BufferedImage getAngryCloud() {
		return angryCloud[Map.getRandObj().nextInt(angryCloud.length)];    	
    }
    
    /**
     * Restituisce l'oggetto di tipo BufferedImage, car
     * @param i
     * @return car[i]
     */
	public static BufferedImage getCar(int i) {
		return car[i];
	}
	
    /**
     * Restituisce l'oggetto di tipo BufferedImage, bike
     * @param i
     * @return bike[i]
     */
	public static BufferedImage getBike(int i) {
		return bike[i];
	}
	
    /**
     * Restituisce l'oggetto di tipo BufferedImage, truck
     * @param i
     * @return truck[i]
     */
	public static BufferedImage getTruck(int i) {
		return truck[i];
	}
	
    /**
     * Restituisce l'oggetto di tipo BufferedImage, arrow
     * @return arrow
     */
	public static BufferedImage getArrow() {
		return arrow;
	}
	
    /**
     * Restituisce l'attributo della data/ora di fine della sessione di gioco
     * @return matchEndTime
     */
	public static long getEndTime() {
		return matchEndTime;
	}
	
	/**
	 * Rappresentazione di tipo String dell'oggetto di classe
	 */
	public String toString(){
		return getClass().getName();
	}
	
	/**
	 * Verifica se l'oggetto è uguale ad un oggetto di tipo Game
	 */
	public boolean equals(Game game) {
		// pre: game!=null
		return (this==game);
	}
}