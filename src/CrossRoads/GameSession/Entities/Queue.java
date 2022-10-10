package CrossRoads.GameSession.Entities;
import java.awt.Rectangle;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import CrossRoads.Game;
import CrossRoads.SfxManager;
import CrossRoads.GUI.AppWindow;
import CrossRoads.GUI.GameOverDialog;
import CrossRoads.GameSession.Map;

/**
 * Classe che definisce una coda stradale, le funzionalità e il suo rapporto con il resto delle altre entità quali veicoli e semafori;
 * estende la classe Rectangle del package AWT di Java
 */
@SuppressWarnings("serial")
public class Queue extends Rectangle {
	private int x, y;
	private int defaultX, defaultY;
	private String laneDirection;
	private final static int defaultSize=0; 
	private Rectangle defaultDimension;
	private Clip queueEx;
	private Rectangle mapLimits;
	
	/**
	 * Costruttore di Queue, crea una coda a partire da una coppia di coordinate (x,y) e dalla direzione della corsia, passate come argomento
	 * @param cordX coordinata x per la generazione
	 * @param cordY coordinata y per la generazione
	 * @param direction direzione della corsia
	 */
	public Queue(int cordX, int cordY, String direction) {
		super(cordX, cordY, defaultSize, defaultSize);
		mapLimits=new Rectangle(0, 0, AppWindow.getDMWidth(), AppWindow.getDMHeight());
		defaultDimension=getBounds();
		defaultX=cordX;
		defaultY=cordY;
		laneDirection=direction;
	}
	
	/**
	 * Azzera le dimensioni di una coda, qualora si sia sciolta (i veicoli reiniziano a moversi)
	 */
	public void releaseVehicles() {
		if(!defaultDimension.equals(getBounds())) {
			int i;
			for(i=0; i<Map.getvList().size(); i++) {
				if(intersects(Map.getvList().get(i).getAreaCovered())) {
					Map.getvList().get(i).running=true;
					Map.getvList().get(i).resetAngryness();
					if(Map.getvList().get(i).getWaitingTimer()!=null && 
							Map.getvList().get(i).getWaitingTimer().isRunning()) {
						Map.getvList().get(i).getWaitingTimer().stop();
					}
					if(Map.getvList().get(i).getCalmDownTimer()!=null && 
							Map.getvList().get(i).getCalmDownTimer().isRunning()) {
						Map.getvList().get(i).getCalmDownTimer().stop();
					}
				}
			}
			setBounds(defaultX, defaultY, defaultSize, defaultSize);
			x=defaultX;
			y=defaultY;
		}
	}
	
	/**
	 * Aggiorna le dimensioni della Queue, variabili ogniqualvolta un veicolo si aggiunga a Queue
	 * @param cordX coordinata x
	 * @param cordY coordinata y
	 * @param newWidth variabile per la nuova larghezza della coda
	 * @param newHeight variabile per la nuova altezza della coda
	 * @param radius raggio del campo visivo
	 */
	public void updateQueue(int cordX, int cordY, int newWidth, int newHeight, double radius) {
		// pre: cordX!=null %% cordY!=null && newWidth!=null && newHeight!=null && radius!=null
		if(laneDirection=="EAST" || laneDirection=="WEST") {
			if(getBounds().equals(defaultDimension)) {
				setBounds(cordX, cordY, newWidth, newHeight);
				x=cordX;
				y=cordY;
			}
			else {
				setBounds(setCordX(cordX), setCordY(cordY), setWidth(newWidth, radius)+width, newHeight);
			}
		}
		else {
			if(getBounds().equals(defaultDimension)) {
				setBounds(cordX, cordY, newWidth, newHeight);
				x=cordX;
				y=cordY;
			}
			else {
				setBounds(setCordX(cordX), setCordY(cordY), newWidth, setHeight(newHeight, radius)+height);
			}			
		}		
		int i;
		for(i=0; i<Map.getiList().size(); i++) { // se la coda interseca un incrocio, termina il gioco
			if(intersects(Map.getiList().get(i)))
			{
				GameOverDialog.getLblCause().setText("Queue Exceeded Street Limits");
				qSoundTrigger();
				Game.endSession();
			}
		} 
		if(!mapLimits.contains(getBounds())) { // se la coda esce dalla mappa
			GameOverDialog.getLblCause().setText("Queue Exceeded Map Limits");
			qSoundTrigger();
			Game.endSession();
		}
	}
	
	private void qSoundTrigger() {
		try {
			queueEx=AudioSystem.getClip();
			queueEx.open(SfxManager.getQueueExceeded());
			queueEx.loop(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Restituisce l'oggetto di tipo String laneDirection
	 * @return laneDirection direzione della corsia
	 */
	public String getLaneDirection() {
		return laneDirection;
	}
	
	/**
	 * Assegna un nuovo valore alla coordinata x di Queue, corrispondente al valore della coordinata x del veicolo che vi si aggiunge
	 * @param cordX coordinata x del veicolo che si aggiunge alla coda nulla
	 * @return cordX coordinata x di Queue
	 */
	private int setCordX(int cordX) {
		// pre: cordX!=null
		if(cordX>x) cordX=x;
		return cordX;
	}
	
	/**
	 * Assegna un nuovo valore alla coordinata y di Queue, corrispondente al valore della coordinata y del veicolo che vi si aggiunge
	 * @param cordY coordinata y del veicolo che si aggiunge alla coda nulla
	 * @return cordY coordinata y di Queue
	 */
	private int setCordY(int cordY) {
		// pre: cordY!=null
		if(cordY>y) cordY=y;
		return cordY;
	}
	
	/**
	 * Assegna un nuovo valore alla larghezza della Queue; la nuova dimensione è relativa alla distanza 
	 * che intercorre dalla coda del veicolo in questione al punto focale più lontano che può raggiungere lo stesso veicolo
	 * @param w larghezza del veicolo
	 * @param r distanza tra coda del veicolo e punto focale
	 * @return w nuova larghezza di Queue
	 */
	private int setWidth(int w, double r) { 
		// pre: w!=null && r!=null
		w=(int) (w/2+r);
		return w;
	}
	
	/**
	 * Assegna un nuovo valore all'altezza della Queue; la nuova dimensione è relativa alla distanza 
	 * che intercorre dalla coda del veicolo in questione al punto focale più lontano che può raggiungere lo stesso veicolo
	 * @param h altezza del veicolo
	 * @param r distanza tra coda del veicolo e punto focale
	 * @return h nuova altezza di Queue
	 */
	private int setHeight(int h, double r) {
		// pre: h!=null && r!=null
		h=(int) (h/2+r);
		return h;
	}
	
	/**
	 * Rappresentazione di tipo String dell'oggetto di classe
	 */
	public String toString(){
		return getClass().getName();
	}
	
	/**
	 * Verifica se l'oggetto è uguale ad un oggetto di tipo Queue
	 */
	public boolean equals(Queue queue) {
		// pre: queue!=null
		boolean eq=false;
		if(getX()==queue.getX() && getY()==queue.getY()) eq=true;
		return eq;
	}
}
