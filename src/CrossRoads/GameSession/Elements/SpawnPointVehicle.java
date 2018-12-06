package CrossRoads.GameSession.Elements;
import CrossRoads.Game;
import CrossRoads.GameSession.Map;
import CrossRoads.GameSession.Entities.Vehicle;
import CrossRoads.GameSession.Entities.Vehicles.*;
import CrossRoads.GameSession.Entities.Score;
import java.util.Random;

/**
 * Classe che crea un punto di Spawn per veicoli
 */
public class SpawnPointVehicle extends SpawnPointPedestrian {
	private String vType; // tipo del veicolo generato (passato come parametro), generato randomicamente
						  // e riassegnato ad iterazioni successive 

	/**
	 * Costruttore di SpawnPointVehicle, invoca il metodo costruttore della superclasse SpawnPointPedestrian, 
	 * crea uno spawner per veicoli, per argomento una coppia di coordinate (x,y) 
	 * e la direzione impressa ai veicoli all'atto della creazione
	 * @param cordX coordinata X del punto di spawn
	 * @param cordY coordinata Y del punto di spawn
	 * @param direction direzione del punto di spawn
	 */
	public SpawnPointVehicle(int cordX, int cordY, String direction) {
		// pre: cordX!=null && cordY!=null && direction!=null
		super(cordX, cordY, direction);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		setSpawnFrequency(setLim_INF(), setLim_SUP());
		while(Game.getMasterEnabler() && Map.getSpvList().contains(this)) { // finché lo spawner è attivo esegui il corpo del ciclo
			try {
				getThread().sleep(spawnFrequency); // gestisco il ciclo di spawn
				setSpawnFrequency(setLim_INF(), setLim_SUP()); // reimposto la frequenza di spawn
				setVType(Map.getRandObj()); // randomizzo il tipo di veicolo da generare
				synchronized(Map.getvList()) {
					if(Game.getMasterEnabler() && Map.getSpvList().contains(this))
						spawn();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void spawn() { // le posizioni di spawn sono computate in base alle loro dimensioni fisiche
		Vehicle v=null;
		if(vType=="Truck")
			v=new Truck(getX(), getY(), getDirection());  // genero un nuovo veicolo di tipo Truck
		else {
			if(getDirection()=="WEST") {
				if(vType=="Car") 
					v=new Car(getX()+22, getY()+1, getDirection()); // genero un nuovo veicolo di tipo Car
				else if(vType=="Bike")
					v=new Bike(getX()+26, getY()+4, getDirection());  // genero un nuovo veicolo di tipo Bike
			}
			else if(getDirection()=="NORTH") {
				if(vType=="Car") 
					v=new Car(getX()+1, getY()+22, getDirection());
				else if(vType=="Bike")
					v=new Bike(getX()+4, getY()+26, getDirection());
			}
			else if(getDirection()=="EAST") {
				if(vType=="Car") 
					v=new Car(getX(), getY()+1, getDirection());
				else if(vType=="Bike")
					v=new Bike(getX(), getY()+4, getDirection());
			}
			else {
				if(vType=="Car") 
					v=new Car(getX()+1, getY(), getDirection());
				else if(vType=="Bike")
					v=new Bike(getX()+4, getY(), getDirection());
			}
		}
		Map.getvList().add(v); // aggiungo questo veicolo in lista
	}

	/**
	 * Calcola e restituisce il limite inferiore dell'intervallo [lim_INF;lim_SUP]
	 * @return lim_INF limite inferiore dell'intervallo
	 */
	@Override
	public int setLim_INF() {
		int lim_INF;
		if(Score.getActualScore()<=10) lim_INF=10;
		else if(Score.getActualScore()<=30) lim_INF=9;
		else if(Score.getActualScore()<=40) lim_INF=8;
		else if(Score.getActualScore()<=50) lim_INF=7;
		else if(Score.getActualScore()<=60) lim_INF=6;
		else if(Score.getActualScore()<=70) lim_INF=5;
		else if(Score.getActualScore()<=80) lim_INF=4;
		else lim_INF=3;
		return lim_INF;
	}
	
	/**
	 * Calcola e restituisce il limite superiore dell'intervallo [lim_INF;lim_SUP]
	 * @return lim_SUP limite superiore dell'intervallo
	 */
	@Override
	public int setLim_SUP() {
		int lim_SUP;
		if(Map.getDifficulty()=="Easy") {
			if(Score.getActualScore()<=15) lim_SUP=30;
			else if(Score.getActualScore()<=25) lim_SUP=28;
			else if(Score.getActualScore()<=35) lim_SUP=26;
			else if(Score.getActualScore()<=45) lim_SUP=24;
			else if(Score.getActualScore()<=55) lim_SUP=22;
			else if(Score.getActualScore()<=65) lim_SUP=20;
			else if(Score.getActualScore()<=75) lim_SUP=18;
			else lim_SUP=15;
		}
		else if(Map.getDifficulty()=="Medium") {
			if(Score.getActualScore()<=15) lim_SUP=55;
			else if(Score.getActualScore()<=25) lim_SUP=50;
			else if(Score.getActualScore()<=35) lim_SUP=45;
			else if(Score.getActualScore()<=45) lim_SUP=40;
			else if(Score.getActualScore()<=55) lim_SUP=35;
			else if(Score.getActualScore()<=65) lim_SUP=30;
			else if(Score.getActualScore()<=75) lim_SUP=25;
			else lim_SUP=20;
		}
		else {
			if(Score.getActualScore()<=15) lim_SUP=85;
			else if(Score.getActualScore()<=25) lim_SUP=80;
			else if(Score.getActualScore()<=35) lim_SUP=75;
			else if(Score.getActualScore()<=45) lim_SUP=65;
			else if(Score.getActualScore()<=55) lim_SUP=55;
			else if(Score.getActualScore()<=65) lim_SUP=45;
			else if(Score.getActualScore()<=75) lim_SUP=40;
			else lim_SUP=30;
		}
		return lim_SUP;
	}
	
	/**
	 * Stabilisce il tipo di veicolo che lo SpawnPointVehicle deve generare
	 * @param n oggetto di tipo Random
	 */
	private void setVType(Random n) {
		// pre: n!=null
		int prob;
		prob=n.nextInt(100);
		if(prob<50) vType="Car"; // 50% di probabilità per car
		else if(prob<80) vType="Bike"; // 30% di probabilità per bike
			 else vType="Truck"; // 20% di probabilità per truck
	} 
	
	/**
	 * Rappresentazione di tipo String dell'oggetto di classe
	 */
	public String toString() {
		return getClass().getName()+"[VDirection: "+getDirection()+"] has been generated";
	}
	
	/**
	 * Verifica se l'oggetto è uguale ad un oggetto di tipo SpawnPointVehicle
	 */
	public boolean equals(SpawnPointVehicle spv) {
		// pre: spv!=null
		boolean eq=false;
		if(getX()==spv.getX() && getY()==spv.getY()) eq=true;
		return eq;
	}
}