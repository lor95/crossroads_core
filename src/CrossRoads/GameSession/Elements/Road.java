package CrossRoads.GameSession.Elements;
import java.awt.Rectangle;

/**
 * Classe che genera a schermo un tratto stradale, definendo in modo circolare (si veda classe Map) tutti gli elementi che lo caratterizzano;
 * estende la classe Rectangle del package AWT di Java
 */
@SuppressWarnings("serial")
public class Road extends Rectangle {
	protected static final int side=90; // larghezza e lunghezza di default di ciascun tratto stradale
	private Rectangle[] lane;
	private String[] laneDirection;
	private Rectangle[] sidewalk;
	
    /**
     * Costruttore di Road (riutilizzato da Intersection), invoca il metodo costruttore della superclasse Rectangle, 
     * per argomenti solo una coppia di coordinate (x,y)
	 * @param cordX coordinata x per la generazione
	 * @param cordY coordinata y per la generazione
     */
	public Road(int cordX, int cordY) { // strada standard (da cui derivano gli incroci)
		super(cordX, cordY, side, side); // richiama il costruttore di Rectangle
		System.out.println(toString());
	}

    /**
     * Costruttore di Road, invoca il metodo costruttore della superclasse Rectangle, 
     * per argomenti una coppia di coordinate (x,y) e la direzione della corsia
	 * @param cordX coordinata x per la generazione
	 * @param cordY coordinata y per la generazione
	 * @param orientation orientamento della corsia
     */
	public Road(int cordX, int cordY, String orientation) {
		super(cordX, cordY, side, side);
		setLanes(cordX, cordY, orientation);
		setOrientation(orientation);
		setSidewalk(cordX, cordY, orientation);
	}
	
	/**
	 * Definisce la posizione e le dimensioni delle corsie, a partire dall'orientamento della corsia e dalla coppia di coordinate di generazione per Road
	 * @param cordX coordinata x per la generazione
	 * @param cordY coordinata y per la generazione
	 * @param orientation orientamento della corsia
	 */
	private void setLanes(int cordX, int cordY, String orientation) {
		// pre: cordX!=null && cordY!=null && orientation!=null
		int dimension=27;
		lane=new Rectangle[2];
		if(orientation.equals("WE")) {
			lane[0]=new Rectangle(cordX, cordY+18, side, dimension); // corsia nord
			lane[1]=new Rectangle(cordX, cordY+side-18-dimension, side, dimension); // corsia sud
		}
		else {
			lane[0]=new Rectangle(cordX+18, cordY, dimension, side); // corsia ovest
			lane[1]=new Rectangle(cordX+side-18-dimension, cordY, dimension, side); // corsia est
		}
	}
	
	/**
	 * Definisce la direzione delle corsie di un tratto stradale, a partire dal suo orientamento
	 * @param orientation orientamento della corsia
	 */
	private void setOrientation(String orientation) {
		// pre: orientation!=null
		laneDirection=new String[2];
		if(orientation.equals("WE")) {
			laneDirection[0]="WEST"; // direzione corsia nord
			laneDirection[1]="EAST"; // direzione corsia sud
		}
		else {
			laneDirection[0]="SOUTH"; // direzione corsia ovest
			laneDirection[1]="NORTH"; // direzione corsia est
		}
		
	}
	
	/**
	 * Definisce la posizione e le dimensioni dei marciapiedi, 
	 * a partire dall'orientamento della corsia e dalla coppia di coordinate di generazione per Road 
	 * @param cordX coordinata x per la generazione
	 * @param cordY coordinata y per la generazione
	 * @param orientation orientamento della corsia
	 */
	private void setSidewalk(int cordX, int cordY, String orientation) {
		// pre: cordX!=null && cordY!=null && orientation!=null
		int dimension=18;
		sidewalk=new Rectangle[2];
		if(orientation.equals("WE")) {
			sidewalk[0]=new Rectangle(cordX, cordY, side, dimension); // marciapiede nord
			sidewalk[1]=new Rectangle(cordX, cordY+side-dimension, side, dimension); // marciapiede sud
		}
		else {
			sidewalk[0]=new Rectangle(cordX, cordY, dimension, side); // marciapiede ovest
			sidewalk[1]=new Rectangle(cordX+side-dimension, cordY, dimension, side); // marciapiede est
		}
	}
	
	/**
	 * Restituisce l'oggetto sidewalk[i], il marciapiede
	 * @param i numero del marciapiede
	 * @return sidewalk[i] marciapiede di Road
	 */
	public Rectangle getSidewalk(int i) {
		// pre: i<sidewalk.length
		return sidewalk[i];
	}
	
	/**
	 * Restituisce l'oggetto lane[i], la corsia
	 * @param i numero della corsia
	 * @return lane[i] corsia di Road
	 */
	public Rectangle getLane(int i) {
		// pre: i<lane.length
		return lane[i];
	}
	
	/**
	 * Restituisce l'oggetto laneDirection[i], la direzione della lane
	 * @param i numero della corsia direzionata
	 * @return laneDirection[i] direzione della corsia
	 */
	public String getLDirection(int i) {
		// pre: i<laneDirection.length
		return laneDirection[i];
	}
	
	/**
	 * Rappresentazione di tipo String dell'oggetto di classe
	 */
	public String toString(){
		return getClass().getName();
	}
	
	/**
	 * Verifica se l'oggetto è uguale ad un oggetto di tipo Road
	 */
	public boolean equals(Road road) {
		// pre: road!=null
		boolean eq=false;
		if(x==road.getX() && y==road.getY()) eq=true;
		return eq;
	}
}