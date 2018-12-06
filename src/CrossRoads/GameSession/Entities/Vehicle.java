package CrossRoads.GameSession.Entities;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import CrossRoads.Game;
import CrossRoads.SfxManager;
import CrossRoads.GUI.AppWindow;
import CrossRoads.GUI.GameOverDialog;
import CrossRoads.GameSession.Map;
import CrossRoads.GameSession.Elements.Intersection;
import CrossRoads.GameSession.Elements.Road;
import CrossRoads.GameSession.Elements.TrafficLight;
import javafx.scene.shape.Circle;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * Classe astratta che rappresenta l'intelligenza di un veicolo ed il suo rapportarsi con gli altri elementi di gioco
 */
@SuppressWarnings("serial")
public abstract class Vehicle extends JComponent implements Runnable {
	private int scoreValue; // valore punteggio dell'oggetto
	private boolean previousPosition; // indica che posizione occupa il veicolo rispetto un incrocio
	protected boolean actualPosition;
	private int previousSpeed;
	protected int actualSpeed; // velocità del veicolo indica ogni quanto il corpo del ciclo va rieseguito
	// si è deciso di porlo protected (come running) per evitare spreco di risorse
	// visto che viene ripetuto numerose volte
	private String direction, nextDirection; // direzione attuale e direzione futura del veicolo
	private int x;
	private int y;
	private int width;
	private int height;
	private Point center; // centro del veicolo
	private double radius; // raggio del campo visivo del veicolo
	private AffineTransform rotation;
	private Circle fieldOfView;
	private Rectangle areaCovered; // hitbox del veicolo
	protected boolean running;
	private Intersection intersection;
	private String turn;// indica in che direzione deve svoltare il veicolo al prossimo incrocio (L/R)
	private Point turnPoint;	
	private Vehicle vif;
	protected boolean seesVehicles; // da mettere privata oppure spiegare nella tesina
	private BufferedImage image;
	private BufferedImage arrow;
	private ActionListener showMessageTask;
	private Timer showMessageTimer;
	private ActionListener hideArrowTask;
	private Timer hideArrowTimer;
	private ActionListener showArrowTask;
	private boolean showArrow;
	private Timer showArrowTimer;
	private String message;
	private Timer waitingTimer;
	private boolean angry;
	private ActionListener waitingTask;
	private Timer calmDownTimer;
	private ActionListener calmDownTask;
	private Font font=new Font("Arial Black", Font.PLAIN, 12);
	private Color shadow=new Color(0, 0, 0, 0.45f);
	private Clip vehicleCrash;
	private Clip horn;
	private Clip pointUp;
	private BufferedImage angryCloud;
	private static int xOffSet;
	private static int yOffSet;
	
	/**
	 * Costruttore di Vehicle, per argomento una coppia di coordinate (x,y) e la direzione che il veicolo avrà all'atto della creazione
	 * @param cordX coordinata x per la generazione
	 * @param cordY coordinata y per la generazione
	 * @param vDirection direzione del veicolo
	 */
	public Vehicle(int cordX, int cordY, String vDirection) {
		x=cordX;
		y=cordY;
		direction=vDirection; // assegnazione della direzione attuale del veicolo in base alla posizione cardinale dello spawner che l'ha generato
		System.out.println(toString()+" has spawned "+getNegDirection().toLowerCase());
		nextDirection();
		message="";
		previousPosition=false; // il veicolo non si trova inizialmente in un incrocio
		seesVehicles=false;
		setProperties(); // assegnazione delle proprietà del veicolo (velocità larghezza lunghezza valore in punteggio)
		if(direction=="NORTH" || direction=="SOUTH") // assegno il valore di una prima areaCovered
			areaCovered=new Rectangle (x, y, height, width);
		else 
			areaCovered=new Rectangle(x, y, width, height);
		center=new Point((int) areaCovered.getCenterX(), (int) areaCovered.getCenterY()); // centro della hitbox del veicolo
		fieldOfView=new Circle(areaCovered.getCenterX(), areaCovered.getCenterY(), radius);
		running=true;
		showArrow=false;
		angry=false;
		arrow=Game.getArrow();
		angryCloud=Game.getAngryCloud();
		angryCloud.setAccelerationPriority(1);
		setBounds(AppWindow.getDMX(), AppWindow.getDMY(), AppWindow.getDMWidth(), AppWindow.getDMHeight());
		AppWindow.getGameScreen().add(this);
/*		for(int i=0; i<Map.getvList().size(); i++)
			System.out.println("Veicolo "+i+" "+Map.getvList().get(i).toString());
*/		showMessageTask=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				message="";	
				showMessageTimer.stop();
			}
		};
		showArrowTask=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showArrow=true;
				hideArrowTimer=new Timer(1100, hideArrowTask);
				hideArrowTimer.start();
				showArrowTimer.stop();
			}
		};
		hideArrowTask=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showArrow=false;
				showArrowTimer.restart();
				hideArrowTimer.stop();
			}
		};
		waitingTask=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				waitingTimer.stop();
				if(Game.getMasterEnabler())
					hornTrigger();
				angry=true;
				calmDownTimer=new Timer(5000, calmDownTask);
				if(Game.getMasterEnabler())
					calmDownTimer.restart();
			}
		};
		calmDownTask=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				calmDownTimer.stop();
				resetAngryness();
				waitingTimer=new Timer(Map.getRandObj().nextInt(8000), waitingTask); // si arrabbia dopo meno tempo
				if(Game.getMasterEnabler())
					waitingTimer.restart();
			}
		};
	}
	
	@Override
	public abstract void run();
	
	/**
	 * Assegna valori specifici alle proprietà di ogni sottocategoria di Vehicle (velocità, dimensioni, valore in punteggio)
	 */
	public abstract void setProperties();

	/**
	 * Scorre la lista di TrafficLight al fine di trovare il TrafficLight relativo alla coda Queue in cui il veicolo deve inserirsi,
	 * poi inserisce Vehicle
	 */
	private void addInQueue() {
		int i;
		for(i=0; i<Map.getTlList().size(); i++) {
			Queue q=Map.getTlList().get(i).getQueue();
			if(direction==q.getLaneDirection() 
					&& fieldOfView.intersects(q.getX(), q.getY(), q.getWidth(), q.getHeight())) {
				q.updateQueue((int) areaCovered.getX(), (int) areaCovered.getY(), 
						(int) areaCovered.getWidth(), (int) areaCovered.getHeight(), radius);
				waitingTimer=new Timer(Map.getRandObj().nextInt(20000), waitingTask);
				waitingTimer.start();
			}
		}
	}
	
	/**
	 * Metodo addInQueue sovraccaricato, inserisce il veicolo nella coda Queue (vuota) relativa al TrafficLight tl, argomento del metodo
	 * @param tl oggetto di tipo TrafficLight, semaforo (a cui è associato una coda Queue)
	 */
	private void addInQueue(TrafficLight tl) {
		Queue q=tl.getQueue();
		q.updateQueue((int) areaCovered.getX(), (int) areaCovered.getY(), 
				(int) areaCovered.getWidth(), (int) areaCovered.getHeight(), radius);
		waitingTimer=new Timer(Map.getRandObj().nextInt(20000), waitingTask);
		waitingTimer.start();
	}

	/**
	 * Vehicle computa la distanza da un TrafficLight, ne rileva lo stato di funzionamento
	 * e si arresta se "red==true"
	 */
	protected void stopMoveTL() {
		int i;
		for(i=0; i<Map.getTlList().size(); i++) {
			if(areaCovered.intersects(Map.getTlList().get(i).get2DLocation().getX(), 
					Map.getTlList().get(i).get2DLocation().getY(), 1, 1)) {  //hitbox interseca area di valore 1, coordinate di stopPoint2D
				if(Map.getTlList().get(i).getLaneDirection()==direction &&
						Map.getTlList().get(i).isRed()) {
					running=false; // se il semaforo contenuto nel campo visivo è rosso allora ferma
					addInQueue(Map.getTlList().get(i));
				}
			}	                                                         
		}
	}

	/**
	 * Vehicle valuta lo stato di un TrafficLight, se "red==false" riprende a funzionare normalmente
	 */
	protected void restartMove() {
		int i;
		for(i=0; i<Map.getTlList().size(); i++) {
			if(areaCovered.intersects(Map.getTlList().get(i).get2DLocation().getX(), 
					Map.getTlList().get(i).get2DLocation().getY(), 1, 1)) {  //hitbox interseca area di valore 1, coordinate di stopPoint2D
				if(!Map.getTlList().get(i).isRed()) running=true;				
			}
		}
	}
	
	/**
	 * Verifica eventuali incidenti stradali che coinvolgono una determinata istanza di Vehicle
	 */
	protected void accident() {
		int i;
		for(i=0; i<Map.getvList().size() && !equals(Map.getvList().get(i)); i++) {
			Vehicle v=Map.getvList().get(i);
			if(v!=null && areaCovered.intersects(v.areaCovered)) {
				cSoundTrigger();
				GameOverDialog.getLblCause().setText("Vehicle-Vehicle Accident Occurred");
				Game.endSession();
			}
		}
	}

	/**
	 * Metodo necessario a far interagire due veicoli che procedono lungo la stessa direzione
	 */
	protected void relateToVehicleInFront() {
		if(direction==vif.direction 
				&& fieldOfView.intersects(vif.areaCovered.getX(), vif.areaCovered.getY(), 
				vif.areaCovered.getWidth(), vif.areaCovered.getHeight())) { // se i campi visivi interagiscono ed i veicoli procedono nella stessa direzione
			if(!getClass().getName().equals(vif.getClass().getName())
					&& previousSpeed<vif.actualSpeed)					
				actualSpeed=vif.actualSpeed; // adegua le velocità
			if(!vif.running) { // se il veicolo di riferimento si blocca
				int i;
				for(i=0; i<Map.getvList().size(); i++) {
					if(Map.getvList().contains(vif)) {
						running=false;
					}
				}
				addInQueue(); // inserisci in coda
			}
		}
		else {
			actualSpeed=previousSpeed; // accelera
			vif=null;
			seesVehicles=false;
		}
	}
	
	/**
	 * Verifica che un veicolo sia di fronte ad un altro che procede lungo la medesima direzione 
	 */
	protected void isVehicleInFront() {
		int i;
		for(i=0; i<Map.getvList().size(); i++) {
			Vehicle v=Map.getvList().get(i);
			if(!equals(v)) {
				if(direction=="NORTH" && direction==v.direction // se due veicoli procedono nella stessa direzione
						&& areaCovered.getCenterY()>v.areaCovered.getCenterY() // se il centro dei due veicoli verifica una precondizione
						&& fieldOfView.intersects(v.fieldOfView.getBoundsInLocal())) { // se i fieldOfView si intersecano
					vif=v; // v è il veicolo di riferimento
					seesVehicles=true; // questo veicolo vede v
				}
				else if(direction=="EAST" && direction==v.direction 
						&& areaCovered.getCenterX()<v.areaCovered.getCenterX()
						&& fieldOfView.intersects(v.fieldOfView.getBoundsInLocal())) {
					vif=v;
					seesVehicles=true;
				}
				else if(direction=="SOUTH" && direction==v.direction
						&& areaCovered.getCenterY()<v.areaCovered.getCenterY()
						&& fieldOfView.intersects(v.fieldOfView.getBoundsInLocal())) {
					vif=v;
					seesVehicles=true;
				}
				else if(direction=="WEST" && direction==v.direction
						&& areaCovered.getCenterX()>v.areaCovered.getCenterX()
						&& fieldOfView.intersects(v.fieldOfView.getBoundsInLocal())) {
					vif=v;
					seesVehicles=true;
				}
			}
		}	
	}
	
	/**
	 * Verifica l'intersezione del veicolo con un KillPoint
	 */
	protected void seeKP() {
		int i;
		for(i=0; i<Map.getKpList().size(); i++) {
			if(areaCovered.intersects(Map.getKpList().get(i))) killVehicle(this);
		}
	}
	
	/**
	 * Verifica che l'oggetto di tipo Vehicle sia effettivamente nell'area di sua appartenenza, le corsie di una strada
	 * @return inLane booleana che indica se il veicolo è all'interno di una corsia
	 */
	protected boolean checkIfInLane() {
		int i, j;
		boolean inLane=false;
		Road r;
		for(i=0; i<Map.getRList().size(); i++) {
			r=Map.getRList().get(i);
			if(r.intersects(areaCovered)) {
				for(j=0; j<2; j++) { // ogni strada ha 2 corsie
					if(r.getLane(j).intersects(areaCovered) && r.getLDirection(j).equals(direction)) {
						i=Map.getRList().size();
						j=2;
						inLane=true;
					}
				}
			}
		}
		return inLane;
	}

	/**
	 * Seleziona la direzione che il Vehicle prenderà dopo aver superato con
	 * successo un incrocio
	 */
	private void nextDirection() { // serve per le frecce
		int randDir; // numero pseudocasuale generato dall'oggetto di tipo random
		do {
			randDir=Map.getRandObj().nextInt(4);
			if(randDir==0) nextDirection="WEST";
			else if(randDir==1) nextDirection="NORTH";
			else if(randDir==2) nextDirection="EAST";
			else nextDirection="SOUTH";
		} while(getNegDirection()==nextDirection);
		/* la direzione che il veicolo prenderà dopo l'incrocio non può coincidere con l'inversa di quella 
		 * attuale, poiché effettuerebbe un'inversione, non contemplata nelle regole di gioco
		 */		
	}

	/**
	 * (Metodo di supporto a nextDirection()):
	 * ottiene la direzione cardinale opposta alla direzione corrente
	 * @return negDirection
	 */
	private String getNegDirection() {
		String negDirection;
		if(direction=="WEST") negDirection="EAST";
		else if(direction=="NORTH") negDirection="SOUTH";
		else if(direction=="EAST") negDirection="WEST";
		else negDirection="NORTH";
		return negDirection;
	}

	/**
	 * Indica il tipo di svolta che Vehicle deve andare ad effettuare: prende in considerazione
	 * direction e nextDirection, le valuta ed indica se la svolta sarà a destra o a sinistra
	 */
	protected void goLeftRight() {
		if(direction==nextDirection) turn="NO";
		else {
			if((direction=="WEST" && nextDirection=="NORTH")||(direction=="NORTH" && nextDirection=="EAST")||
					(direction=="EAST" && nextDirection=="SOUTH")||(direction=="SOUTH" && nextDirection=="WEST"))
				turn="RIGHT";
			else turn="LEFT";
		}
	}
	
	/**
	 * Individua in che punto il veicolo deve effettuare una determinata svolta
	 */
	protected void checkTurnPoint() { // da sistemare 
		int i;
		if (turn=="LEFT") { // se devo girare a sinistra
			for(i=0; i<4; i++) { // controllo tutte le direzioni
				if(intersection.getToLeft(i)==direction)
					turnPoint=intersection.getTurnPoint(i);
			}
		} else if (turn=="RIGHT") {
			for(i=0; i<4; i++) {
				if(intersection.getToRight(i)==direction)
					turnPoint=intersection.getTurnPoint(i);
			}			
		}
	}
	
	/**
	 * Verifica la coincidenza di center con il turnPoint di riferimento e procede alla svolta effettiva
	 */
	protected void turn() {
		if(turnPoint!=null && center.equals(turnPoint)) {
			rotateVehicle();
			isVehicleInFront();
		}
	}
	
	/**
	 * Ruota fisicamente areaCovered di 90° o -90° attorno al proprio centro, a seconda del tipo di svolta
	 * ed assegna a Vehicle la nuova direzione
	 */
	private void rotateVehicle() {
		Shape rectangle;
		rotation=new AffineTransform();
		if(turn=="RIGHT")
			rotation.rotate(Math.toRadians(90), areaCovered.getCenterX(), areaCovered.getCenterY());
		if(turn=="LEFT")
			rotation.rotate(Math.toRadians(-90), areaCovered.getCenterX(), areaCovered.getCenterY());
		else
			rectangle=areaCovered.getBounds();
		rectangle=rotation.createTransformedShape(areaCovered);
		areaCovered.setBounds(rectangle.getBounds());
		center.setLocation(areaCovered.getCenterX(), areaCovered.getCenterY());
		direction=nextDirection;
	}
	
	/**
	 * Verifica l'uscita di un Vehicle da un Intersection, decidendo in caso affermativo 
	 * la nextDirection e assegnando il relativo punteggio
	 */
	protected void scoreDirectionLogic() {
		int i=0;
		int n=Map.getiList().size(); // inizializzo un intero come il numero di elementi presenti
		// nella lista di incroci, coincide dunque con l'iNumber di Map
		// (sostituibile con Map.getINumber())
		while(i<n) {
			if(Map.getiList().get(i).intersects(getAreaCovered())) {
				if(intersection==null)
					intersection=Map.getiList().get(i); // scorre la lista di incroci, se la hitbox dell'incrocio all'indice i contiene interamente la hitbox del veicolo restituisce true
				actualPosition=true; // il veicolo è dunque dentro l'incrocio
				i=n; // esci dal ciclo
			}
			else {
				actualPosition=false; // il veicolo non è dentro l'incrocio
				i++; // controlla se sia nell'incrocio successivo della lista
			}
		}
		if(actualPosition==false && previousPosition==true) {
			Score.setActualScore(getScore());
			message="+"+scoreValue;
			showMessageTimer=new Timer(1000, showMessageTask);
			showMessageTimer.start();
			pointUpTrigger();
			intersection=null; // annullo l'intersezione
			nextDirection();
		}
		previousPosition=actualPosition; // per effettuare il prossimo controllo è necessario riassegnare la posizione "passata"
	}

	/**
	 * Muove il Vehicle in linea retta all'interno della Map
	 */
	protected void moveForward() {
		if(direction=="WEST") 
			areaCovered.setLocation((int) areaCovered.getX()-1, (int) areaCovered.getY()); // vai verso sinistra
		else if(direction=="NORTH") 
			areaCovered.setLocation((int) areaCovered.getX(), (int) areaCovered.getY()-1); // vai verso l'alto
		else if(direction=="EAST") 
			areaCovered.setLocation((int) areaCovered.getX()+1, (int) areaCovered.getY()); // vai verso destra
		else 
			areaCovered.setLocation((int) areaCovered.getX(), (int) areaCovered.getY()+1); // vai verso il basso

		center.setLocation(areaCovered.getCenterX(), areaCovered.getCenterY()); // aggiorna la posizione del centro
		fieldOfView.setCenterX(areaCovered.getCenterX()); // sposta anche il campo visivo
		fieldOfView.setCenterY(areaCovered.getCenterY());
	}
	
	/**
	 * Triggera il suono di crash
	 */
	private void cSoundTrigger() {
		try {
			vehicleCrash=AudioSystem.getClip();
			vehicleCrash.open(SfxManager.getVCrash());
			vehicleCrash.loop(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Triggera il suono del clacson
	 */
	private void hornTrigger() {
		try {
			horn=AudioSystem.getClip();
			horn.open(SfxManager.getHorn(Map.getRandObj().nextInt(SfxManager.getHorns().length)));
			horn.loop(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void pointUpTrigger() {
		try {
			pointUp=AudioSystem.getClip();
			pointUp.open(SfxManager.getPointUp());
			pointUp.loop(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Costruisce la grafica di un oggetto di tipo Vehicle (incluse frecce direzionali) a seconda della direzione dell'oggetto
	 * @param g oggetto di tipo Graphics
	 */
	public void paintComponent(Graphics g) { // tutti 2 gli offset
		super.paintComponent(g);
		g.setFont(font);
		g.setColor(Color.RED);
		g.drawString(message, (int) (areaCovered.getCenterX()-10), (int) areaCovered.getY()-2);
		Graphics2D g2d=(Graphics2D) g.create();
		g2d.setColor(shadow);
		if(direction=="NORTH") {
			g2d.rotate(Math.toRadians(270), center.getX(), center.getY());
			g2d.fillRoundRect((int) (areaCovered.getX()-(width-height)/2)-xOffSet,
					(int) (areaCovered.getY()+(width-height)/2)+yOffSet, 
					(int) areaCovered.getHeight()-2, (int) areaCovered.getWidth(), 3, 3);
			g2d.drawImage(image, (int) areaCovered.getX()-(width-height)/2, 
					(int) areaCovered.getY()+(width-height)/2,
					(int) areaCovered.getHeight(), (int) areaCovered.getWidth(), null);
			if(turn=="RIGHT" && showArrow) {
				g2d.drawImage(arrow, (int) ((areaCovered.getX()-(width-height)/2)+width-arrow.getWidth()+1), 
						(int) (areaCovered.getY()+(width-height)/2)+height-2,
						(int) arrow.getWidth(), (int) arrow.getHeight(), null);
			}
			else if(turn=="LEFT" && showArrow) {
				g2d.rotate(Math.toRadians(180), 
						(int)(center.getX()), (int) (center.getY()));
				g2d.drawImage(arrow,(int) center.getX()-width/2-1, 
						(int) (areaCovered.getY()+(width-height)/2)+height-2,
						(int) arrow.getWidth(), (int) arrow.getHeight(), null);
			}
		}
		else if(direction=="WEST") {
			g2d.rotate(Math.toRadians(180), center.getX(), center.getY());
			g2d.fillRoundRect((int) areaCovered.getX()-xOffSet, (int) areaCovered.getY()-yOffSet,
					(int) areaCovered.getWidth()-2, (int) areaCovered.getHeight(), 3, 3);
			g2d.drawImage(image, (int) areaCovered.getX(), (int) areaCovered.getY(),
					(int) areaCovered.getWidth(), (int) areaCovered.getHeight(), null);
			if(turn=="RIGHT" && showArrow) {
				g2d.drawImage(arrow, (int) (areaCovered.getX()+width-arrow.getWidth()+1), 
						(int) areaCovered.getY()+height-2,
						(int) arrow.getWidth(), (int) arrow.getHeight(), null);
			}
			else if(turn=="LEFT" && showArrow) {
				g2d.rotate(Math.toRadians(180), 
						areaCovered.getX()+width-(arrow.getWidth())/2, center.getY());
				g2d.drawImage(arrow, (int) (areaCovered.getX()+width-arrow.getWidth()-1), 
						(int) areaCovered.getY()+height-2,
						(int) arrow.getWidth(), (int) arrow.getHeight(), null);
			}
		}
		else if(direction=="SOUTH") {
			g2d.rotate(Math.toRadians(90), center.getX(), center.getY());
			g2d.fillRoundRect((int) (areaCovered.getX()-(width-height)/2)+xOffSet, 
					(int) (areaCovered.getY()+(width-height)/2)-yOffSet,
					(int) areaCovered.getHeight(), (int) areaCovered.getWidth()-1, 3, 3);
			g2d.drawImage(image, (int) areaCovered.getX()-(width-height)/2, 
					(int) areaCovered.getY()+(width-height)/2,
					(int) areaCovered.getHeight(), (int) areaCovered.getWidth(), null);
			if(turn=="RIGHT" && showArrow) {
				g2d.drawImage(arrow, (int) ((areaCovered.getX()-(width-height)/2)+width-arrow.getWidth()+1), 
						(int) (areaCovered.getY()+(width-height)/2)+height-2,
						(int) arrow.getWidth(), (int) arrow.getHeight(), null);
			}
			else if(turn=="LEFT" && showArrow) {
				g2d.rotate(Math.toRadians(180), 
						(int)(center.getX()), (int) (center.getY()));
				g2d.drawImage(arrow,(int) center.getX()-width/2-1, 
						(int) (areaCovered.getY()+(width-height)/2)+height-2,
						(int) arrow.getWidth(), (int) arrow.getHeight(), null);
			}
		}
		else {			
			g2d.fillRoundRect((int) areaCovered.getX()+xOffSet, (int) areaCovered.getY()+yOffSet,
					(int) areaCovered.getWidth(), (int) areaCovered.getHeight()-1, 3, 3);
			g2d.drawImage(image, (int) areaCovered.getX(), (int) areaCovered.getY(),
					(int) areaCovered.getWidth(), (int) areaCovered.getHeight(), null);
			if(turn=="RIGHT" && showArrow) {
				g2d.drawImage(arrow, (int) (areaCovered.getX()+width-arrow.getWidth()+1), 
						(int) areaCovered.getY()+height-2,
						(int) arrow.getWidth(), (int) arrow.getHeight(), null);
			}
			else if(turn=="LEFT" && showArrow) {
				g2d.rotate(Math.toRadians(180), 
						areaCovered.getX()+width-(arrow.getWidth())/2, center.getY());
				g2d.drawImage(arrow, (int) (areaCovered.getX()+width-arrow.getWidth()-1), 
						(int) areaCovered.getY()+height-2,
						(int) arrow.getWidth(), (int) arrow.getHeight(), null);
			}
		}
		if(angry)
			g.drawImage(angryCloud, (int) areaCovered.getCenterX()-10,(int) areaCovered.getY()-20, null);
		{////////////////////////////////////////////////
/*			g.setColor(Color.DARK_GRAY);
			g.drawOval((int) (fieldOfView.getCenterX()-radius), (int) (fieldOfView.getCenterY()-radius),
					(int) (2*radius), (int) (2*radius));
*/		}////////////////////////////////////////////////
	}
	
	public static void setXOffSet(int i) {
		xOffSet=i;
	}
	
	public static void setYOffSet(int i) {
		yOffSet=i;
	}

	/**
	 * Elimina Vehicle. Include istruzioni per bloccare il thread di riferimento, e per assegnare null all'istanza, 
	 * allo scopo di ottimizzare le risorse e permettere al Garbage Collector di intervenire alla deallocazione dell'area di memoria
	 * contenente l'oggetto in ingresso
	 */
	private void killVehicle(Vehicle v) {
		// pre: v!=null
		clear();
		synchronized(Map.getvList()) {
			Map.getvList().remove(v);
		}
		v.running=false;
		v=null; // dereferenzia il veicolo
	}
	
	/**
	 * Ripristina l'arrabbiatura di un veicolo
	 */
	public void resetAngryness() {
		angryCloud=Game.getAngryCloud();
		angry=false;
	}
	
	/**
	 * Rimuove Vehicle dalla schermata di gioco
	 */
	public void clear() {
		AppWindow.getGameScreen().remove(this);
		if(waitingTimer!=null && waitingTimer.isRunning())
			waitingTimer.stop();
		if(calmDownTimer!=null && calmDownTimer.isRunning())
			calmDownTimer.stop();
	}
	
	/**
	 * Restituisce l'oggetto di tipo Timer, waitingTimer
	 * @return waitingTimer
	 */
	public Timer getWaitingTimer() {
		return waitingTimer;
	}
	
	/**
	 * Restituisce l'oggetto di tipo Timer, calmDownTimer
	 * @return calmDownTimer
	 */
	public Timer getCalmDownTimer() {
		return calmDownTimer;
	}
	
	/**
	 * Assegna un oggetto di tipo BufferedImage a image
	 * @param i oggetto di tipo BufferedImage
	 */
	public void setImage(BufferedImage i) {
		// pre: i!=null
		image=i;
	}
	
	/**
	 * Assegna un valore a actualSpeed, la velocità corrente del veicolo
	 * @param speed valore relativo alla velocità
	 */
	public void setActualSpeed(int speed) {
		// pre: speed!=null
		actualSpeed=speed;
	}
	
	/**
	 * Assegna un valore a previousSpeed, la velocità del veicolo (precedente a eventuali modifiche)
	 * @param speed valore relativo alla velocità
	 */
	public void setPreviousSpeed(int speed) {
		// pre: speed!=null
		previousSpeed=speed;
	}
	
	/**
	 * Assegna a width il valore della larghezza
	 * @param w valore relativo alla larghezza
	 */
	public void setWidth(int w) {
		// pre: w!=null
		width=w;
	}
	
	/**
	 * Assegna ad height il valore dell'altezza
	 * @param h valore relativo all'altezza
	 */
	public void setHeight(int h) {
		// pre: h!=null
		height=h;
	}
	
	/**
	 * Assegna a radius la dimensione del raggio
	 * @param r dimensione del raggio
	 */
	public void setRadius(double r) {
		// pre: r!=null
		radius=r;
	}
	
	/**
	 * Assegna un valore intero a scoreValue, il punteggio associato al veicolo
	 * @param score valore del punteggio
	 */
	public void setScoreValue(int score) {
		// pre: score!=null
		scoreValue=score;
	}
	
	/**
	 * Assegna un oggetto di tipo Timer a showArrowTimer
	 * @param timer oggetto di tipo Timer
	 */
	public void setShowArrowTimer(Timer timer) {
		// pre: timer!=null
		showArrowTimer=timer;
	}
	
	/**
	 * Restituisce il valore della velocità attuale di Vehicle
	 * @return actualSpeed la velocità corrente del veicolo
	 */
	public int getActualSpeed() {
		return actualSpeed;
	}
	
	/**
	 * Restituisce l'oggetto boolean running
	 * @return running, parametro che sancisce il movimento o meno del veicolo
	 */
	public boolean getRunning() {
		return running;
	}
	
	/**
	 * Restituisce l'oggetto di tipo Timer showArrowTimer
	 * @return showArrowTimer timer che definisce il periodo di visualizzazione delle frecce direzionali di un veicolo
	 */
	public Timer getShowArrowTimer() {
		return showArrowTimer;
	}

	/**
	 * Restituisce la "HitBox" del veicolo, di tipo Rectangle
	 * @return areaCovered "HitBox" del veicolo
	 */
	public Rectangle getAreaCovered() {
		return areaCovered;
	}

	/**
	 * Restituisce il valore scoreValue
	 * @return scoreValue valore del punteggio
	 */
	public int getScore() {
		return scoreValue;
	}
	
	/**
	 * Restituisce l'oggetto ActionListener showArrowTask
	 * @return showArrowTask è l'azione (task) svolta per mostrare le frecce direzionali del veicolo
	 */
	public ActionListener getShowArrowTask() {
		return showArrowTask;
	}
	
	public Clip getCSound() {
		return vehicleCrash;
	}
	
	/**
	 * Rappresentazione di tipo String dell'oggetto di classe
	 */
	public String toString(){
		return getClass().getSimpleName();
	}

	/**
	 * Verifica l'uguaglianza tra due oggetti di classe
	 * @param v oggetto di tipo Vehicle
	 * @return equal variabile boolean indicante l'uguaglianza o meno tra due oggetti
	 */
	public boolean equals(Vehicle v){
		// pre: v!=null
		boolean equal;
		try {
			if(v.areaCovered==null)
				equal=false;
			else if(areaCovered.getBounds().equals(v.areaCovered.getBounds())) {
				equal=true;
			}
			else
				equal=false;
			return equal;
		} catch (Throwable e) {
        	JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage());
        	throw e;
        }
	}
}
