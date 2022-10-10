package CrossRoads.GameSession.Elements;
import CrossRoads.Game;
import CrossRoads.GameSession.Map;
import CrossRoads.GameSession.Entities.Score;
import CrossRoads.GameSession.Entities.Pedestrian;

/**
 * Classe che crea un punto di Spawn per pedoni
 */
public class SpawnPointPedestrian implements SpawnPoint {
	private Thread thread;
	private int x, y;
	private String direction;
	protected long spawnFrequency;

	/**
	 * Costruttore di SpawnPointPedestrian, crea uno spawner per pedoni, per argomento una coppia di coordinate (x,y) 
	 * e la direzione impressa ai pedoni all'atto della creazione
	 * @param cordX coordinata X del punto di spawn
	 * @param cordY coordinata Y del punto di spawn
	 * @param direction direzione del punto di spawn
	 */
	public SpawnPointPedestrian(int cordX, int cordY, String direction) {
		x=cordX;
		y=cordY;
		this.direction=direction;
		System.out.println(toString());
		initialize();
	}

	/**
	 * Crea ed avvia un oggetto di tipo Thread
	 */
	public void initialize() {
		thread=new Thread(this, "SP"); // genera un nuovo Thread
		thread.start(); // viene richiamato il metodo run()
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		setSpawnFrequency(setLim_INF(), setLim_SUP());
		while(Game.getMasterEnabler() && Map.getSppList().contains(this)){
			try {
				thread.sleep(spawnFrequency);
				setSpawnFrequency(setLim_INF(), setLim_SUP());
				synchronized(Map.getpList()) {
					if(Game.getMasterEnabler() && Map.getSppList().contains(this))
						spawn();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}

	/**
	 * Restituisce la direzione del punto di spawn
	 * @return direction
	 */
	public String getDirection() {
		return direction;
	}

	@Override
	public int setLim_INF() {
		int lim_INF;
		if(Score.getActualScore()<=10) lim_INF=30;
		else if(Score.getActualScore()<=30) lim_INF=29;
		else if(Score.getActualScore()<=40) lim_INF=28;
		else if(Score.getActualScore()<=50) lim_INF=27;
		else if(Score.getActualScore()<=60) lim_INF=26;
		else if(Score.getActualScore()<=70) lim_INF=25;
		else if(Score.getActualScore()<=80) lim_INF=24;
		else lim_INF=23;
		return lim_INF;
	}

	@Override
	public int setLim_SUP() {
		int lim_SUP;
		if(Score.getActualScore()<=15) lim_SUP=250;
		else if(Score.getActualScore()<=25) lim_SUP=240;
		else if(Score.getActualScore()<=35) lim_SUP=230;
		else if(Score.getActualScore()<=45) lim_SUP=220;
		else if(Score.getActualScore()<=55) lim_SUP=210;
		else if(Score.getActualScore()<=65) lim_SUP=200;
		else if(Score.getActualScore()<=75) lim_SUP=190;
		else lim_SUP=180;
		return lim_SUP;
	}

	/**
	 * Calcola ed assegna un valore casuale allo stato spawnFrequency (in ms), dato un intervallo [lim_INF;lim_SUP] 
	 * @param lim_INF estremo inferiore dell'intervallo 
	 * @param lim_SUP estremo superiore dell'intervallo
	 */
	public void setSpawnFrequency(int lim_INF, int lim_SUP) {
		// pre: lim_INF<lim_SUP
		boolean condition=true; // inizializza la condizione del while come vera
		double decimal, partialResult; // valore decimale, risultato parziale
		int integer;
		while(condition) { // finché la condizione è vera
			integer=Map.getRandObj().nextInt(lim_SUP); // numero intero generato pseudorandomicamente
			decimal=Map.getRandObj().nextDouble(); // parte decimale generata pseudorandomicamente
			if (integer>=lim_INF) { // se il risultato intero è maggiore o uguale al limite inferiore 
				partialResult=(integer+decimal)*1000; 
				/* somma la parte intera a quella decimale, ad esempio se la parte intera è 1 e la parte
				 * decimale è pari a 0.7, il risultato è 1.7 (secondi). 
				 * Si moltiplica poi per mille perchè il metodo .sleep() prende 
				 * come parametro un valore long che rappresenta i millisecondi di stop
				 * per il thread. Si nota che in ogni caso partialResult è compreso tra i due limiti
				 * poiché il massimo valore che decimal può assumere è 0.999 e il limite superiore ed
				 * inferiore sono necessariamente diversi */
				spawnFrequency=(long) partialResult;
				condition=false; // esce dal ciclo
			}
		}
	}

	@Override
	public void spawn() {
		Pedestrian p=new Pedestrian(x, y, direction);
		Map.getpList().add(p);
	}
	
	/**
	 * Restituisce un oggetto di tipo Thread
	 * @return thread
	 */
	public Thread getThread() {
		return thread;
	}

	/**
	 * Restituisce la coordinata X del punto di spawn
	 * @return x
	 */
	public int getX() {
		return x;
	}

	/**
	 * Restituisce la coordinata Y del punto di spawn
	 * @return y
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Rappresentazione di tipo String dell'oggetto di classe
	 */
	public String toString() {
		return getClass().getName()+"[PDirection: "+direction+"] has been generated";
	}
	
	/**
	 * Verifica se l'oggetto è uguale ad un oggetto di tipo SpawnPointPedestrian
	 */
	public boolean equals(SpawnPointPedestrian spp) {
		// pre: spp!=null
		boolean eq=false;
		if(x==spp.getX() && x==spp.getY()) eq=true;
		return eq;
	}
}
