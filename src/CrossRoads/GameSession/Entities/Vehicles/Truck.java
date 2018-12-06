package CrossRoads.GameSession.Entities.Vehicles;
import javax.swing.Timer;
import CrossRoads.Game;
import CrossRoads.GameSession.Map;
import CrossRoads.GameSession.Entities.Vehicle;

/**
 * Classe figlia Truck della classe padre Vehicle
 */
@SuppressWarnings("serial")
public class Truck extends Vehicle {
	private Thread thread;
	
	/**
	 * Costruttore di Truck, per argomento una coppia di coordinate (x,y) e la direzione che il veicolo avrà all'atto della creazione
	 * @param cordX coordinata x per la generazione
	 * @param cordY coordinata y per la generazione
	 * @param vDirection direzione del veicolo
	 */
	public Truck(int cordX, int cordY, String vDirection) {
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
				/*  if(checkIfInLane() || actualPosition) 
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
		int i=Map.getRandObj().nextInt(Game.getTrucks().length);
		setImage(Game.getTruck(i));
		actualSpeed=34;
		setPreviousSpeed(actualSpeed);
		setWidth(50);
		setHeight(18);
		setRadius(46);
		setScoreValue(3);
	}
}
