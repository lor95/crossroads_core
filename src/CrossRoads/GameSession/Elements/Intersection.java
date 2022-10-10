package CrossRoads.GameSession.Elements;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Classe che genera a schermo un incrocio, definendo in modo circolare (si veda classe Map)
 * tutti gli elementi che lo caratterizzano
 */
@SuppressWarnings("serial")
public class Intersection extends Road {
	private Rectangle[] zebraCrossing; // insieme dei rettangoli che rappresentano le strisce pedonali
	private Rectangle[] sidewalk; // insieme dei quadrati che rappresentano i marciapiedi all'interno dell'incrocio
	private Point[] turnPoint; // insieme di punti che permettono ai veicoli di cambiare orientamento nel momento in cui il loro centro coincide con uno di questi
	private String[] dirToLeft; 
	private String[] dirToRight;
	
    /**
     * Costruttore di Intersection, invoca il metodo costruttore della superclasse Road, 
     * genera incroci (con relativi punti di svolta, marciapiedi, etc.) a partire dai parametri passati per argomento
	 * @param cordX coordinata x per la generazione
	 * @param cordY coordinata y per la generazione
     */
	public Intersection(int cordX, int cordY) {
		super(cordX, cordY); // richiama il costruttore della classe padre, Road
		setZebraCrossing(cordX, cordY);
		setSidewalk(cordX, cordY);
		setTurnPoint(cordX, cordY);
		setToLeft();
		setToRight();
	}
	
	/**
	 * Definisce la posizione e le dimensioni degli attraversamenti pedonali, a partire dalla coppia
	 * di coordinate di generazione per Intersection
	 * @param cordX coordinata x per la generazione
	 * @param cordY coordinata y per la generazione
	 */
	private void setZebraCrossing(int cordX, int cordY) {
		// pre: cordX!=null && cordY!=null
		int width=54; // lato lungo della striscia
		int height=18; // lato corto della striscia
		zebraCrossing=new Rectangle[4];
		zebraCrossing[0]=new Rectangle(cordX, cordY+height, height, width); // striscia ovest
		zebraCrossing[1]=new Rectangle(cordX+height, cordY, width, height); // striscia nord
		zebraCrossing[2]=new Rectangle(cordX+height+width, cordY+18, height, width); // striscia est
		zebraCrossing[3]=new Rectangle(cordX+height, cordY+height+width, width, height); // striscia sud
	}
	
	/**
	 * Definisce la posizione e le dimensioni dei marciapiedi, a partire dalla coppia di coordinate
	 * di generazione per Intersection
	 * @param cordX coordinata x per la generazione
	 * @param cordY coordinata y per la generazione
	 */
	private void setSidewalk(int cordX, int cordY) {
		// pre: cordX!=null && cordY!=null
		int side=18; // lato del marciapiede 
		sidewalk=new Rectangle[4];
		sidewalk[0]=new Rectangle(cordX, cordY+(4*side), side, side); // marciapiede in basso a sinistra
		sidewalk[1]=new Rectangle(cordX, cordY, side, side); // marciapiede in alto a sinistra
		sidewalk[2]=new Rectangle(cordX+(4*side), cordY, side, side); // marciapiede in alto a destra
		sidewalk[3]=new Rectangle(cordX+(4*side), cordY+(4*side), side, side); // marciapiede in basso a destra
	}
	
	/**
	 * Definisce la posizione dei punti di svolta, a partire dalla coppia di coordinate di generazione
	 * per Intersection
	 * @param cordX coordinata x per la generazione
	 * @param cordY coordinata y per la generazione
	 */
	private void setTurnPoint(int cordX, int cordY) {
		// pre: cordX!=null && cordY!=null
		turnPoint=new Point[4];
		turnPoint[0]=new Point(cordX+30, cordY+59); // turnPoint basso sinistra
		turnPoint[1]=new Point(cordX+30, cordY+30); // turnPoint alto sinistra
		turnPoint[2]=new Point(cordX+59, cordY+30); // turnPoint alto destra
		turnPoint[3]=new Point(cordX+59, cordY+59); // turnPoint basso destra
	}
	
	/**
	 * Assegna a ciascun turnPoint le condizioni di svolta verso sinistra
	 */
	private void setToLeft() {
		dirToLeft=new String[4];
		dirToLeft[0]="SOUTH"; // per girare a sinistra nel punto 0 devo andare verso nord
		dirToLeft[1]="WEST"; // per girare a sinistra nel punto 1 devo andare verso est
		dirToLeft[2]="NORTH"; // per girare a sinistra nel punto 2 devo andare verso sud
		dirToLeft[3]="EAST"; // per girare a sinistra nel punto 3 devo andare verso ovest
	}
	
	/**
	 * Assegna a ciascun turnPoint le condizioni di svolta verso destra
	 */
	private void setToRight() {
		dirToRight=new String[4];
		dirToRight[0]="EAST"; // per girare a destra nel punto 0 devo andare verso ovest
		dirToRight[1]="SOUTH"; // per girare a destra nel punto 1 devo andare verso nord
		dirToRight[2]="WEST"; // per girare a destra nel punto 2 devo andare verso est
		dirToRight[3]="NORTH"; // per girare a destra nel punto 3 devo andare verso sud
	}	
	
	/**
	 * Restituisce il marciapiede rispettivo al valore i in ingresso
	 * @param i numero di sidewalk
	 * @return sidewalk[i]
	 */
	public Rectangle getSidewalk(int i) {
		// pre: i<sidewalk.length
		return sidewalk[i];
	}
	
	/**
	 * Restituisce l'attraversamento pedonale rispettivo al valore i in ingresso
	 * @param i numero di zebraCrossing
	 * @return zebraCrossing[i]
	 */
	public Rectangle getZebras(int i) {
		// pre: i<zebraCrossing.length
		return zebraCrossing[i];
	}
	
	/**
	 * Restituisce il turnPoint rispettivo al valore i in ingresso
	 * @param i numero di turnPoint
	 * @return turnPoint[i]
	 */
	public Point getTurnPoint(int i) {
		// pre: i<turnPoint.length
		return turnPoint[i];
	}
	
	/**
	 * Restituisce la condizione di svolta a sinistra del turnPoint rispettivo al valore i in ingresso
	 * @param i numero di turnPoint
	 * @return dirToLeft[i]
	 */
	public String getToLeft(int i) {
		// pre: i<dirToLeft.length
		return dirToLeft[i];
	}
	
	/**
	 * Restituisce la condizione di svolta a destra del turnPoint rispettivo al valore i in ingresso
	 * @param i numero di turnPoint
	 * @return dirToRight[i]
	 */
	public String getToRight(int i) {
		// pre: i<dirToRight.length
		return dirToRight[i];
	}
	
	/**
	 * Rappresentazione di tipo String dell'oggetto di classe
	 */
	public String toString(){
		return getClass().getName();
	}
	
	/**
	 * Verifica se l'oggetto è uguale ad un oggetto di tipo Intersection
	 */
	public boolean equals(Intersection inter) {
		// pre: inter!=null
		boolean eq=false;
		if(x==inter.getX() && y==inter.getY()) eq=true;
		return eq;
	}
}
