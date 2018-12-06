package CrossRoads.GameSession.Entities;
import javafx.scene.shape.Circle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JComponent;
import CrossRoads.Game;
import CrossRoads.SfxManager;
import CrossRoads.GUI.AppWindow;
import CrossRoads.GUI.GameOverDialog;
import CrossRoads.GameSession.Map;
import CrossRoads.GameSession.Elements.Intersection;
import CrossRoads.GameSession.Elements.KillPoint;
import CrossRoads.GameSession.Elements.Road;

/**
 * Classe che definisce l'entità pedone ed il suo rapportarsi con gli altri elementi di gioco
 */
@SuppressWarnings("serial")
public class Pedestrian extends JComponent implements Runnable {
	private Thread thread;
	private int x, y;
	private static final double radius=5;
	private static final double fovRadius=7; 
	private boolean running;
	private Circle fieldOfView;
	private Vehicle vif;
	private Color c;
	private Color shadow;
	private String direction;
	private long speed;
	private Circle areaCovered;
	private Clip pedestrianCrash;
	
	/**
	 * Costruttore di Pedestrian, per argomento una coppia di coordinate (x,y) e la direzione che il pedone avrà all'atto della creazione
	 * @param cordX coordinata x per la generazione
	 * @param cordY coordinata y per la generazione
	 * @param pDirection direzione del pedone
	 */
	public Pedestrian(int cordX, int cordY, String pDirection) {
		thread=new Thread(this, "Pedestrian");
		x=cordX;
		y=cordY;
		running=true;
		c=new Color(randColor(), randColor(), randColor());
		shadow=new Color(0, 0, 0, 0.55f); // ombra in trasparenza
		setSpeed();
		direction=pDirection;
		System.out.println(toString()+" has spawned towards "+direction.toLowerCase());
		areaCovered=new Circle((double) x, (double) y, radius);
		fieldOfView=new Circle((double) x, (double) y, fovRadius);
		setBounds(AppWindow.getDMX(), AppWindow.getDMY(), AppWindow.getDMWidth(), AppWindow.getDMHeight());
		AppWindow.getGameScreen().add(this);
		thread.start();	
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		while(Game.getMasterEnabler()) {
			while(running && Game.getMasterEnabler()) {
				try {
					stopMoving();
					moveForward();
					seeKP();
					thread.sleep(speed);
				/*	if(checkIfCorrectlyLocated()) 
						System.out.println(toString()+" is correctly located");	
					else Game.endSession();  */
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				thread.sleep(speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			startMoving();
			accident(); 
			/* da sistemare perché un pedone così facendo potrebbe essere colpito da un veicolo 
			quando in realtà non dovrebbe, perché già passato, da modificare la condizione di stop */
		}		
	}
	
	private void stopMoving() {
		int i;
		Vehicle v;
		for(i=0; i<Map.getvList().size(); i++){
			v=Map.getvList().get(i);
			if(fieldOfView.intersects(v.getAreaCovered().getX(), v.getAreaCovered().getY(),
					v.getAreaCovered().getWidth(), v.getAreaCovered().getHeight()))  {
//				System.out.println("Stop at ("+x+";"+y+")");
				vif=v;
				running=false;
			}
		}
	}
	
	private void startMoving() {
		if(vif!=null && !fieldOfView.intersects(vif.getAreaCovered().getX(), vif.getAreaCovered().getY(),
				vif.getAreaCovered().getWidth(), vif.getAreaCovered().getHeight())) {
			vif=null;
			running=true;
		}
/*		else if(vif==null)
			running=true;*/
	}
	
	/**
	 * Restituisce un valore randomico tra 0-255, utile per ottenere colori random
	 * @return c valore randomico
	 */
	private int randColor() {
		int c=0;
		c=Map.getRandObj().nextInt(255);
		return c;
	}

	/**
	 * Definisce il valore della variabile speed, la velocità con cui si sposta un oggetto di tipo Pedestrian sulla mappa  
	 */
	private void setSpeed() {
		int intValue;
		double doubleValue;
		intValue=0;
		while(intValue<70)
			intValue=Map.getRandObj().nextInt(90);
		doubleValue=Map.getRandObj().nextDouble();
		speed=(long) (intValue+doubleValue);
	}

	/**
	 * Definisce la meccanica di movimento di un oggetto di tipo Pedestrian sulla mappa;
	 * semplici incrementi o decrementi unitari del valore della coordinata relativa all'asse su cui avviene lo spostamento
	 */
	private void moveForward() {
		if(direction=="WEST") {
			areaCovered.setCenterX(x-1); // vai verso sinistra
			areaCovered.setCenterY(y);
			fieldOfView.setCenterX(x-1); 
			fieldOfView.setCenterY(y);
		}
		else if(direction=="NORTH") {
			areaCovered.setCenterX(x);
			areaCovered.setCenterY(y-1); // vai verso l'alto
			fieldOfView.setCenterX(x); 
			fieldOfView.setCenterY(y-1);
		}
		else if(direction=="EAST") {
			areaCovered.setCenterX(x+1); // vai verso destra
			areaCovered.setCenterY(y); 
			fieldOfView.setCenterX(x+1); 
			fieldOfView.setCenterY(y);
		}
		else {
			areaCovered.setCenterX(x);
			areaCovered.setCenterY(y+1); // vai verso il basso
			fieldOfView.setCenterX(x); 
			fieldOfView.setCenterY(y+1);
		}
		x=(int) areaCovered.getCenterX();
		y=(int) areaCovered.getCenterY();
	}	
	
	/**
	 * Stabilisce se è avvenuto un contatto veicolo-pedone, con conseguente conclusione della sessione di gioco
	 */
	private void accident() {
		int i;
		for(i=0; i<Map.getvList().size(); i++) {
			Vehicle v=Map.getvList().get(i);
			if(areaCovered.intersects(v.getAreaCovered().getX(), v.getAreaCovered().getY(), 
					v.getAreaCovered().getWidth(), v.getAreaCovered().getHeight())) {
				cSoundTrigger();
				GameOverDialog.getLblCause().setText("Vehicle-Pedestrian Accident Occurred");
				Game.endSession();
			}
		}
	}
	
	/**
	 * Stabilisce se Pedestrian interseca un KillPoint, con conseguente "uccisione"
	 */
	private void seeKP() {
		int i;
		for(i=0; i<Map.getKpList().size(); i++) {
			KillPoint kp=Map.getKpList().get(i);
			if(areaCovered.intersects(kp.getX(), kp.getY(), kp.getWidth(), kp.getHeight()))
				killPedestrian(this);
		}
	}
	
	/**
	 * Verifica che l'oggetto di tipo Pedestrian sia effettivamente nell'area di sua appartenenza, 
	 * il marciapiede di una strada o le strisce di attraversamento in un incrocio
	 * @return correct variabile boolean che indica se il pedone è all'interno di un marciapiede o sopra le strisce pedonali
	 */
	@SuppressWarnings("unused")
	private boolean checkIfCorrectlyLocated() {
		int j, k;
		Road r;
		Intersection i;
		boolean correct=false;
		for(j=0; j<Map.getRList().size(); j++) { // verifica tra le strade
			r=Map.getRList().get(j);
			if(areaCovered.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight())) {
				for(k=0; k<2; k++) { // 2 marciapiedi
					Rectangle s;
					s=r.getSidewalk(k);
					if(areaCovered.intersects(s.getX(), s.getY(), s.getWidth(), s.getHeight())) {
						j=Map.getRList().size();
						k=2;
						correct=true;
					}
				}
			}			
		}
		if(!correct) { // non è in una strada, verifica se si trova in un incrocio
			for(j=0; j<Map.getiList().size(); j++) {
				i=Map.getiList().get(j);
				if(areaCovered.intersects(i.getX(), i.getY(), i.getWidth(), i.getHeight())) {
					for(k=0; k<4; k++) { // 4 marciapiedi e 4 attraversamenti pedonali
						Rectangle l;
						l=i.getSidewalk(k);
						if(areaCovered.intersects(l.getX(), l.getY(), l.getWidth(), l.getHeight())) {
							j=Map.getiList().size();
							k=4;
							correct=true;
						}
						else { // non si trova nei marciapiedi, verifica gli attraversamenti pedonali
							l=i.getZebras(k);
							if(areaCovered.intersects(l.getX(), l.getY(), l.getWidth(), l.getHeight())) {
								j=Map.getiList().size();
								k=4;
								correct=true;
							}
						}
					}
				}
			}
		}
		return correct;		
	}
	
	private void cSoundTrigger() {
		try {
			pedestrianCrash=AudioSystem.getClip();
			pedestrianCrash.open(SfxManager.getPCrash());
			pedestrianCrash.loop(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Costruisce la grafica di un oggetto di tipo Pedestrian
	 * @param g oggetto di tipo Graphics
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(shadow);
		g.fillOval((int) (areaCovered.getCenterX()-radius)+2, (int) (areaCovered.getCenterY()-radius)+2,
				(int) (2*radius), (int) (2*radius)); // disegna l'ombra del pedone
		g.setColor(c);
		g.fillOval((int) (areaCovered.getCenterX()-radius), (int) (areaCovered.getCenterY()-radius),
				(int) (2*radius), (int) (2*radius)); // disegna il pedone
		g.setColor(Color.DARK_GRAY);
		g.drawOval((int) (areaCovered.getCenterX()-radius), (int) (areaCovered.getCenterY()-radius),
				(int) (2*radius), (int) (2*radius)); // disegna il contorno del pedone
/*		g.drawOval((int) (fieldOfView.getCenterX()-fovRadius), (int) (fieldOfView.getCenterY()-fovRadius),
				(int) (2*fovRadius), (int) (2*fovRadius));
*/	}
	
	/**
	 * Elimina Pedestrian. Include istruzioni per bloccare il thread di riferimento, e per assegnare null all'istanza, 
	 * allo scopo di ottimizzare le risorse e permettere al Garbage Collector di intervenire alla deallocazione dell'area di memoria
	 * contenente l'oggetto in ingresso
	 * @param p oggetto di tipo Pedestrian
	 */
	private void killPedestrian(Pedestrian p) {
		// pre: v!=null
		clear();
		synchronized(Map.getvList()) {
			Map.getvList().remove(p);
		}
		p=null; // dereferenzia il pedone
	}
	
	/**
	 * Rimuove Pedestrian dalla schermata di gioco
	 */
	public void clear() {
		AppWindow.getGameScreen().remove(this);
	}
	
	/**
	 * Rappresentazione di tipo String dell'oggetto di classe
	 */
	public String toString(){
		return getClass().getSimpleName();
	}
	
	/**
	 * Verifica se l'oggetto è uguale ad un oggetto di tipo Pedestrian
	 */
	public boolean equals(Pedestrian ped) {
		// pre: ped!=null
		boolean eq=false;
		if(getX()==ped.getX() && getY()==ped.getY()) eq=true;
		return eq;
	}
}
