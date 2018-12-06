package CrossRoads.GameSession.Entities.Vehicles;
import javax.swing.Timer;
import CrossRoads.Game;
import CrossRoads.GameSession.Map;
import CrossRoads.GameSession.Entities.Vehicle;

/**
 * Classe figlia Bike della classe padre Vehicle
 */
@SuppressWarnings("serial")
public class Bike extends Vehicle {
	private Thread thread;
	
	/**
	 * Costruttore di Bike, per argomento una coppia di coordinate (x,y) e la direzione che il veicolo avrà all'atto della creazione
	 * @param cordX coordinata x per la generazione
	 * @param cordY coordinata y per la generazione
	 * @param vDirection direzione del veicolo
	 */
	public Bike(int cordX, int cordY, String vDirection) {
		super(cordX, cordY, vDirection);
		thread=new Thread(this, "Car");
		thread.start(); // viene richiamato il metodo run()
	}
	
	@SuppressWarnings("static-access")
	public void run() {
		goLeftRight();
		setShowArrowTimer(new Timer(800, getShowArrowTask()));
		getShowArrowTimer().start();
		while(Game.getMasterEnabler()) {
			while(running && Game.getMasterEnabler()) {
				try {
					moveForward();
					scoreDirectionLogic();
					goLeftRight();
					if(seesVehicles) 
						relateToVehicleInFront();
					else
						isVehicleInFront();
					if(actualPosition) {
						checkTurnPoint();
						turn();
					}
					else {
						stopMoveTL();
						seeKP();
					}				
					accident();
					repaint();
					thread.sleep(actualSpeed);
				/*	if(checkIfInLane() || actualPosition) 
						System.out.println(toString()+" is correctly located");	
					else Game.endSession();	 */
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				thread.sleep(actualSpeed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			restartMove();
		}	
	}
	
	public void setProperties() {
		int i=Map.getRandObj().nextInt(Game.getBikes().length);
		setImage(Game.getBike(i));
		actualSpeed=30;
		setPreviousSpeed(actualSpeed);
		setWidth(24);
		setHeight(10);
		setRadius(30);
		setScoreValue(2);
	}
}
