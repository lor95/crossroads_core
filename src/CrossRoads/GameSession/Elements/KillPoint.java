package CrossRoads.GameSession.Elements;
import java.awt.Rectangle;

/**
 * Classe che genera a schermo un KillPoint, nient'altro che un'istanza di java.awt.Rectangle
 * personalizzata
 */
@SuppressWarnings("serial")
public class KillPoint extends Rectangle {
	
	/**
	 * Costruttore di KillPoint, invoca il metodo costruttore della superclasse Rectangle, 
	 * per argomento una coppia di coordinate (x,y) e le dimensioni larghezza e altezza
	 * @param cordX coordinata x
	 * @param cordY coordinata y
	 * @param width larghezza
	 * @param height altezza
	 */
	public KillPoint(int cordX, int cordY, int width, int height) {
		// pre: cordX!=null && cordY!=null && width!=null && height!=null
		super(cordX, cordY, width, height);
	}
	
	/**
	 * Rappresentazione di tipo String dell'oggetto di classe
	 */
	public String toString(){
		return getClass().getName();
	}
	
	/**
	 * Verifica se l'oggetto è uguale ad un oggetto di tipo KillPoint
	 */
	public boolean equals(KillPoint kp) {
		// pre: kp!=null
		boolean eq=false;
		if(x==kp.getX() && y==kp.getY()) eq=true;
		return eq;
	}
}