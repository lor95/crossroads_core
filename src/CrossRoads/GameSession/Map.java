package CrossRoads.GameSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import CrossRoads.Game;
import CrossRoads.Time;
import CrossRoads.GUI.AppWindow;
import CrossRoads.GameSession.Elements.Road;
import CrossRoads.GameSession.Elements.Intersection;
import CrossRoads.GameSession.Elements.SpawnPointVehicle;
import CrossRoads.GameSession.Elements.SpawnPointPedestrian;
import CrossRoads.GameSession.Elements.KillPoint;
import CrossRoads.GameSession.Elements.TrafficLight;
import CrossRoads.GameSession.Entities.Score;
import CrossRoads.GameSession.Entities.Pedestrian;
import CrossRoads.GameSession.Entities.Vehicle;

/**
 * Classe che genera a schermo tutti gli elementi appartenenti al contesto selezionato nella schermata iniziale. 
 * Tre tipologie di Map possibili definite in base alla stringa difficulty. 
 * Appena Map viene lanciata, comincia a generare gli elementi in senso orario, partendo dagli elementi ad ovest (sinistra nella visualizzazione a schermo), 
 * utilizzando per tutti gli oggetti un indice i, che si incrementa a partire da 1 fino al numero massimo di oggetti.
 */
public class Map {
	
	
	/* offset provvisori */
	private static final int k=31; // offset del kill point
	private static final int s=30; // offset degli spawnPoint
	/*----------------------------------*/
	
	private static String difficulty; // difficoltà selezionata
	
	/* numero di ciascun elemento statico */
	private static int rNumber; // numero di tratti di strada sulla mappa
	private static int iNumber; // numero di incroci sulla mappa
	private static int tlNumber; // numero di TrafficLight sulla mappa
	private static int spvNumber; // numero di SpawnPointVehicle sulla mappa
	private static int sppNumber; // numero di SpawnPointPedestrian sulla mappa
	
	private static Random randObj=new Random(); // oggetto di tipo random, utilizzato per generare numeri
	private static Score score;
	
	/* Liste per ogni entità */
	private static ArrayList<Road> rList=new ArrayList<Road>(); // lista di strade
	private static ArrayList<Intersection> iList=new ArrayList<Intersection>(); // lista di incroci
	private static ArrayList<TrafficLight> tlList=new ArrayList<TrafficLight>(); // lista di semafori
	private static ArrayList<SpawnPointVehicle> spvList=new ArrayList<SpawnPointVehicle>(); // lista di spv
	private static ArrayList<SpawnPointPedestrian> sppList=new ArrayList<SpawnPointPedestrian>(); // lista di spp
	private static ArrayList<KillPoint> kpList=new ArrayList<KillPoint>(); // lista di kpv
	private static List<Vehicle> vList=Collections.synchronizedList(new ArrayList<Vehicle>()); // lista di veicoli
	private static List<Pedestrian> pList=Collections.synchronizedList(new ArrayList<Pedestrian>()); // lista di pedoni
	
	/**
	 * Costruttore della classe Map, in base al parametro difficulty variano le proprietà dei metodi invocati
	 * @param difficulty livello di difficoltà
	 */
	@SuppressWarnings("static-access")
	public Map(String difficulty) {		
		this.difficulty=difficulty; // viene assegnata la difficoltà
		System.out.println(toString());
		Game.setMasterEnabler(true);
		setRNumber(); // viene assegnati il numero di strade
		setINumber(); // viene assegnato il numero di incroci
		setTLNumber(); // viene assegnato il numero di semafori
		setSPVNumber(); // viene assegnato il numero di spawner per veicoli
		setSPPNumber(); // viene assegnato il numero di spawner per pedoni
		spawnR(); // vengono generati tutti i tratti stradali
		spawnI(); // vengono generati tutti gli incroci
		spawnTL(); // vengono generati tutti i semafori
		spawnSPV(); // vengono generati tutti gli spawner per veicoli
		spawnSPP(); // vengono generati tutti gli spawner per pedoni
		spawnKP(); // vengono generati tutti i killer
		score=new Score();
		AppWindow.getGameScreen().add(score); // si aggiunge la schermata del punteggio al JPanel
		
		/* aggiungiamo i palazzi e le relative ombre */
		AppWindow.getBuildings().setBounds(AppWindow.getDMX(), AppWindow.getDMY(),
				AppWindow.getDMWidth(), AppWindow.getDMHeight());
		AppWindow.getBuildings().setVisible(true);
		AppWindow.getGameScreen().add(AppWindow.getBuildings());
		Time.startTime(Map.getRandObj().nextInt(49)); // parte il tempo
		AppWindow.getShadows().setVisible(true);
		AppWindow.getGameScreen().add(AppWindow.getShadows());
	}

	/**
	 * Genera su schermo i Road
	 */
	private static void spawnR() {
		int i;
		for(i=1; i<=rNumber; i++) {
			Road road=new Road(setXCordR(i), setYCordR(i), setOrientation(i));
			rList.add(road);
		}
	}

	/**
	 * Genera su schermo gli Intersection
	 */
	private static void spawnI() {
		int i; // inizializzo un intero
		for(i=1; i<=iNumber; i++) { 
			/* finché l'intero inizializzato a 1 non eguaglia il numero di incroci,
			 * continua a generare incroci */
			Intersection intersection=new Intersection(setXCordI(i), setYCordI(i));
			iList.add(intersection); // aggiungi l'incrocio appena generato alla lista di incroci
		}
	}

	/**
	 * Genera su schermo i TrafficLight
	 */
	private static void spawnTL() { 
		int i;
		for(i=1; i<=tlNumber; i++) {
			TrafficLight trafficLight=new TrafficLight(setXCordTL(i), setYCordTL(i), setLDirection(i));
			tlList.add(trafficLight);
		}
	}

	/**
	 * Genera su schermo gli SpawnPointVehicle
	 */
	private static void spawnSPV() {
		int i;
		for(i=1; i<=spvNumber; i++) {
			SpawnPointVehicle spv= new SpawnPointVehicle(setXCordSPV(i), 
					setYCordSPV(i), setSPVDirection(i));
			spvList.add(spv);
		}
	}

	/**
	 * Genera su schermo gli SpawnPointPedestrian
	 */
	private static void spawnSPP() {
		int i;
		for(i=1; i<=sppNumber; i++) {
			SpawnPointPedestrian spp= new SpawnPointPedestrian(setXCordSPP(i), 
					setYCordSPP(i), setSPPDirection(i));
			sppList.add(spp);
		}
	}
	
	/**
	 * Genera su schermo i KillPoint
	 */
	private static void spawnKP() {
		KillPoint kp1=new KillPoint(-k, 0, 1, AppWindow.getDMHeight()); 
		kpList.add(kp1);
		KillPoint kp2=new KillPoint(0, -k, AppWindow.getDMWidth(), 1);
		kpList.add(kp2);
		KillPoint kp3=new KillPoint(AppWindow.getDMWidth()+k, 0, 1, AppWindow.getDMHeight());
		kpList.add(kp3);
		KillPoint kp4=new KillPoint(0, AppWindow.getDMHeight()+k, AppWindow.getDMWidth(), 1);
		kpList.add(kp4);
	}
	
	/**
	 * Pulisce Map svuotando le liste dinamiche.
	 */
	public static void clear() {
		int i; // variabile contatore
		rList.removeAll(rList);
		iList.removeAll(iList);
		for(i=0; i<tlList.size(); i++) {
			tlList.get(i).clear();
		}
		tlList.removeAll(tlList);
		spvList.removeAll(spvList);
		sppList.removeAll(sppList);
		kpList.removeAll(kpList);
/*		for(int j=0; i<vList.size(); j++)
			System.out.println("1"+vList.get(j));*/
		for(i=0; i<vList.size(); i++) {
			vList.get(i).clear();
		}
/*		for(int j=0; i<vList.size(); j++)
			System.out.println("2"+vList.get(j));*/
		vList.removeAll(vList);
		for(i=0; i<pList.size(); i++) {
			pList.get(i).clear();
		}
		pList.removeAll(pList);
		AppWindow.getGameScreen().remove(score);
	}
	
	/**
	 * Assegna una coordinata x alla Road in base al suo numero identificativo
	 * @param i numero di Road
	 * @return cordX coordinata x
	 */
	private static int setXCordR(int i) {
		// pre: i!=null
		int cordX=0;
		if(difficulty.equals("Easy")) {
			if(i==1) cordX=0; // coordinata x strada ovest ovest
			else if(i==2) cordX=90; // coordinata x ovest
			else if(i<=4 || i>=7) cordX=180; // coordinate x nord e sud
			else if(i==5) cordX=270; // coordinata x est
			else if(i==6) cordX=360; // coordinata x est est
		}
		else if(difficulty.equals("Medium")) {
			if(i==1) cordX=0; // coordinata x ovest ovest
			else if(i==2) cordX=90; // coordinata x ovest
			else if(i<=4 || i==13 || i==14) cordX=180; // coordinate x nord ovest
			else if(i==5) cordX=270; //coordinata x centro ovest
			else if(i==6) cordX=360; // coordinata x centro est
			else if(i<=8 || i==11 || i==12) cordX=450; // coordinate x nord est
			else if(i==9) cordX=540; // coordinata x est
			else if(i==10) cordX=630; // coordinata x est est
		}
		else {				 
			if(i==1 || i==21) cordX=0; // coordinate x nord ovest ovest e sud ovest ovest
			else if(i==2 || i==22) cordX=90; // coordinata x nord ovest e sud ovest
			else if(i<=4 || i==19 || i==20 || i>=23) cordX=180; // coordinata x nord nord ovest e sud sud ovest e centro ovest
			else if(i==5 || i==17) cordX=270; // coordinata x nord centro ovest e sud centro ovest
			else if(i==6 || i==18) cordX=360; // coordinata x nord centro est e sud centro est
			else if(i<=8 || i==11 || i==12 || i==15 || i==16) cordX=450; // coordinata x nord nord est e centro est e sud sud est
			else if(i==9 || i==13) cordX=540; // coordinata x nord est e sud est
			else if(i==10 || i==14) cordX=630; // coordinata x nord est est e sud est est
		}
		return cordX;
	}
	
	/**
	 * Assegna una coordinata y alla Road in base al suo numero identificativo
	 * @param i numero di Road
	 * @return cordX coordinata y
	 */
	private static int setYCordR(int i) {
		// pre: i!=null
		int cordY=0;
		if(difficulty.equals("Easy")) {
			if(i<=2 || i==5 || i==6) cordY=180; // coordinata y strada ovest ed est
			else if(i==3) cordY=0; // coordinata y nord nord
			else if(i==4) cordY=90; // coordinata y nord 
			else if(i==7) cordY=270; // coordinata y sud
			else if(i==8) cordY=360; // coordinata y sud sud
		}
		else if(difficulty.equals("Medium")) {
			if(i<=2 || i==5 || i==6 || i==9 || i==10) cordY=180; // coordinata y ovest
			else if(i==3 || i==7) cordY=0; // coordinate y nord nord ovest e nord nord est
			else if(i==4 || i==8) cordY=90; // coordinata y nord ovest e nord est
			else if(i==11 || i==13) cordY=270; // coordinata y sud est e sud ovest
			else if(i==12 || i==14) cordY=360; // coordinata y sud sud est e sud sud ovest
		}
		else {				 
			if(i<=2 || i==5 || i==6 || i==9 || i==10) cordY=180; 
			else if(i==3 || i==7) cordY=0;  
			else if(i==4 || i==8) cordY=90;
			else if(i==11 || i==23) cordY=270;
			else if(i==12 || i==24) cordY=360; 
			else if(i<=14 || i==17 || i==18 || i==21 || i==22) cordY=450;
			else if(i==15 || i==19) cordY=540;
			else if(i==16 || i==20) cordY=630;
		}
		return cordY;
	}
	
	/**
	 * Assegna l'orientamento alla Road che i veicoli devono seguire, in base al numero identificativo della Road
	 * @param i numero di Road
	 * @return orientation orientamento di Road
	 */
	private static String setOrientation(int i) {
		// pre: i!=null
		String orientation=null;
		if(difficulty.equals("Easy")) {
			if(i<=2 || i==5 || i==6) orientation="WE";
			else if(i<=4 || i>=7) orientation="NS";
		}
		else if(difficulty.equals("Medium")) {
			if(i<=2 || i==5 || i==6 || i==9 || i==10) orientation="WE";
			else if(i<=4 || i==7 || i==8 || i>=11) orientation="NS";
		}
		else {				 
			if(i<=2 || i==5 || i==6 || i==9 || i==10 || 
					i==13 || i==14 || i==17 || i==18 || i==21 || i==22) orientation="WE"; 
			else if(i<=4 || i==7 || i==8 || i==11 || i==12 || 
					i==15 || i==16 || i==19 || i==20 || i>=23) orientation="NS"; 
		}
		return orientation;
	}

	/**
	 * Assegna una coordinata x all'Intersection in base al suo numero identificativo
	 * @param i numero di Intersection
	 * @return cordX coordinata x di Intersection
	 */
	private static int setXCordI(int i) {
		// pre: i!=null
		int cordX=0;
		if(difficulty.equals("Easy")) cordX=180; // coordinata x incrocio
		else if(difficulty.equals("Medium")) {
			if(i==1) cordX=180; // coordinata x incrocio sx
			else if(i==2) cordX=450; // coordinata x incrocio dx			 
		}
		else {				 
			if(i<=2) cordX=180; // coordinata x incrocio sx down / sx up 
			// NB valide solo se setBounds elementi == 0, 0, width, height
			else if(i>=3) cordX=450; // coordinata x incrocio dx up / dx down
		}
		return cordX;
	}

	/**
	 * Assegna una coordinata y all'Intersection in base al suo numero identificativo
	 * @param i numero di Intersection
	 * @return cordY coordinata y di Intersection
	 */
	private static int setYCordI(int i) {
		// pre: i!=null
		int cordY=0;
		if(difficulty.equals("Easy") || difficulty.equals("Medium")) cordY=180; // coordinata y incrocio
		else {				 
			if(i==1 || i==4) cordY=450; // coordinate y incrocio sx down / dx down
			else if(i==2 || i==3) cordY=180; // coordinata y incrocio sx up/ dx up
		}
		return cordY;
	}

	/**
	 * Assegna una coordinata x al TrafficLight in base al suo numero identificativo
	 * @param i numero di TrafficLight
	 * @return cordX coordinata x di TrafficLight
	 */
	private static int setXCordTL(int i) {
		// pre: i!=null
		int cordX=0;
		if(difficulty.equals("Easy")) {
			if(i<=2) cordX=172; 
			else if(i<=4) cordX=277;
		}
		else if(difficulty.equals("Medium")) {
			if(i<=2) cordX=172; 
			else if(i<=4) cordX=277;
			else if(i<=6) cordX=442;
			else if(i<=8) cordX=547; 
		}
		else {				 
			if(i<=2 || i==5 || i==6) cordX=172; 
			else if(i<=4 || i==7 || i==8) cordX=277;
			else if(i<=10 || i==13 || i==14) cordX=442; 
			else if(i<=12 || i==15 || i==16) cordX=547;
		}
		return cordX;
	}

	/**
	 * Assegna una coordinata y al TrafficLight in base al suo numero identificativo
	 * @param i numero di TrafficLight
	 * @return cordY coordinata y di TrafficLight
	 */
	private static int setYCordTL(int i) {
		// pre: i!=null
		int cordY=0;
		if(difficulty.equals("Easy")) {
			if(i==1 || i==4) cordY=277;
			else if(i==2 || i==3) cordY=172;
		}
		else if(difficulty.equals("Medium")) {
			if(i==1 || i==4 || i==5 || i==8) cordY=277; 
			else if(i<=3 || i<=7) cordY=172;
		}
		else {				 
			if(i==1 || i==4 || i==13 || i==16) cordY=547; 
			else if(i<=3 || i==14 || i==15) cordY=442; 
			else if(i==5 || i==8 || i==9 || i==12) cordY=277;
			else if(i<=11) cordY=172;
		}
		return cordY;
	}

	/**
	 * Assegna la direzione dei veicoli che il TrafficLight deve fermare
	 * @param i numero di TrafficLight
	 * @return lDirection direzione dei veicoli
	 */
	private static String setLDirection(int i) {
		// pre: i!=null
		String lDirection="";
		if(i==1 || i==5 || i==9 || i==13) lDirection="EAST";
		else if(i==2 || i==6 || i==10 || i==14) lDirection="SOUTH";
		else if(i==3 || i==7 || i==11 || i==15) lDirection="WEST";
		else if(i==4 || i==8 || i==12 || i==16) lDirection="NORTH";
		return lDirection;
	}

	/**
	 * Assegna una coordinata x allo SpawnPointVehicle in base al suo numero identificativo
	 * @param i numero di SpawnPointVehicle
	 * @return cordX coordinata x di SpawnPointVehicle
	 */
	private static int setXCordSPV(int i) {
		// pre: i!=null
		int cordX=0;
		if(difficulty.equals("Easy")) {
			if(i==1) cordX=0-s; // coordinata x spawner ovest
			else if(i==2) cordX=201; // coordinata x spawner nord
			else if(i==3) cordX=400+s; // coordinata x spawner est
			else if(i==4) cordX=230; // coordinata x spawner sud
		}
		else if(difficulty.equals("Medium")) {
			if(i==1) cordX=0-s; // coordinata x spawner ovest
			else if(i==2) cordX=201; // coordinata x spawner nord-ovest
			else if(i==3) cordX=471; // coordinata x spawner nord-est
			else if(i==4) cordX=670+s; // coordinata x spawner est
			else if(i==5) cordX=500; // coordinata x spawner sud-est
			else if(i==6) cordX=230; // coordinata x spawner sud-ovest
		}
		else {				 
			if(i<=2) cordX=0-s; // coordinate x spawner ovest
			else if(i==3) cordX=201; // coordinata x spawner nord-ovest
			else if(i==4) cordX=471; // coordinata x spawner nord-est
			else if(i<=6) cordX=670+s; // coordinata x spawner est
			else if(i==7) cordX=500; // coordinata x spawner sud-est
			else if(i==8) cordX=230; // coordinata x spawner sud-ovest
		}
		return cordX;
	}

	/**
	 * Assegna una coordinata y allo SpawnPointVehicle in base al suo numero identificativo
	 * @param i numero di SpawnPointVehicle
	 * @return cordY coordinata y di SpawnPointVehicle
	 */
	private static int setYCordSPV(int i) {
		// pre: i!=null
		int cordY=0;
		if(difficulty.equals("Easy")) {
			if(i==1) cordY=230; // coordinata y spawner ovest
			else if(i==2) cordY=0-s; // coordinata y spawner nord
			else if(i==3) cordY=201; // coordinata y spawner est
			else if(i==4) cordY=400+s; // coordinata y spawner sud
		}
		else if(difficulty.equals("Medium")) {
			if(i==1) cordY=230; // coordinata y spawner ovest
			else if(i<=3) cordY=0-s; // coordinate y spawner nord
			else if(i==4) cordY=201; // coordinata y spawner est
			else if(i<=6) cordY=400+s; // coordinate y spawner sud
		}
		else {				 
			if(i==1) cordY=500; // coordinate y spawner sud-ovest
			else if(i==2) cordY=230; // coordinata y spawner nord-ovest
			else if(i<=4) cordY=0-s; // coordinate y spawner nord
			else if(i==5) cordY=201; // coordinata y spawner nord-est
			else if(i==6) cordY=471; // coordinata y spawner sud-est
			else if(i<=8) cordY=670+s; // coordinata y spawner sud
		}
		return cordY;
	}

	/**
	 * Assegna una direzione allo SpawnPointVehicle in base al suo numero identificativo
	 * @param i numero di SpawnPointVehicle
	 * @return direction direzione dello SpawnPointVehicle
	 */
	private static String setSPVDirection(int i) {
		// pre: i!=null
		String direction=null;
		if(difficulty=="Easy") {
			if(i==1) direction="EAST";
			else if(i==2) direction="SOUTH";
			else if(i==3) direction="WEST";
			else if(i==4) direction="NORTH";
		}
		else if(difficulty=="Medium") {
			if(i==1) direction="EAST";
			else if(i<=3) direction="SOUTH";
			else if(i==4) direction="WEST";
			else direction="NORTH";
		}
		else {
			if(i<=2) direction="EAST";
			else if(i<=4) direction="SOUTH";
			else if(i<=6) direction="WEST";
			else direction="NORTH";
		}
		return direction;
	}

	/**
	 * Assegna una coordinata x allo SpawnPointPedestrian in base al suo numero identificativo
	 * @param i numero di SpawnPointPedestrian
	 * @return cordX coordinata x di SpawnPointPedestrian
	 */
	private static int setXCordSPP(int i) {
		// pre: i!=null
		int cordX=0;
		if(difficulty.equals("Easy")) {
			if(i<=2) cordX=6; // coordinata x spawner ovest
			else if(i==3) cordX=189; // coordinata x spawner nord ovest
			else if(i==4) cordX=260; // coordinata x spawner nord est
			else if(i<=6) cordX=444; // coordinata x spawner est
			else if(i==7) cordX=260; // coordinata x spawner sud est
			else if(i==8) cordX=189; // coordinata x spawner sud ovest
		}
		else if(difficulty.equals("Medium")) {
			if(i<=2) cordX=6; // coordinata x spawner ovest
			else if(i==3) cordX=189; // coordinata x spawner nord ovest ovest
			else if(i==4) cordX=260; // coordinata x spawner nord nord ovest
			else if(i==5) cordX=459; // coordinata x spawner nord nord est
			else if(i==6) cordX=530; // coordinata x spawner nord est est
			else if(i<=8) cordX=714; // coordinata x spawner est
			else if(i==9) cordX=530; // coordinata x spawner sud est est
			else if(i==10) cordX=459; // coordinata x spawner sud sud est
			else if(i==11) cordX=261; // coordinata x spawner sud sud ovest
			else if(i==12) cordX=189; // coordinata x spawner sud ovest ovest
		}
		else {				 
			if(i<=4) cordX=6;
			else if(i==5) cordX=189;
			else if(i==6) cordX=260;
			else if(i==7) cordX=459;
			else if(i==8) cordX=530;
			else if(i<=12) cordX=714;
			else if(i==13) cordX=530;
			else if(i==14) cordX=459;
			else if(i==15) cordX=260;
			else if(i==16) cordX=189;
		}
		return cordX;
	}

	/**
	 * Assegna una coordinata y allo SpawnPointPedestrian in base al suo numero identificativo
	 * @param i numero di SpawnPointPedestrian
	 * @return cordY coordinata y di SpawnPointPedestrian
	 */
	private static int setYCordSPP(int i) {
		// pre: i!=null
		int cordY=0;
		if(difficulty.equals("Easy")) {
			if(i==1) cordY=260; // coordinata x spawner ovest
			else if(i==2) cordY=189; // coordinata x spawner nord ovest
			else if(i<=4) cordY=6; // coordinata x spawner nord est
			else if(i==5) cordY=189; // coordinata x spawner est
			else if(i==6) cordY=260; // coordinata x spawner sud est
			else if(i<=8) cordY=444; // coordinata x spawner sud ovest
		}
		else if(difficulty.equals("Medium")) {
			if(i==1) cordY=260; // coordinata x spawner sud ovest
			else if(i==2) cordY=189; // coordinata x spawner nord ovest
			else if(i<=6) cordY=6; // coordinata x spawner nord 
			else if(i==7) cordY=189; // coordinata x spawner nord nord est
			else if(i==8) cordY=260; // coordinata x spawner sud est
			else if(i<=12) cordY=444; // coordinata x spawner sud			
		}
		else {				 
			if(i==1) cordY=530;
			else if(i==2) cordY=459;
			else if(i==3) cordY=260;
			else if(i==4) cordY=189;
			else if(i<=8) cordY=6;
			else if(i==9) cordY=189;
			else if(i==10) cordY=260;
			else if(i==11) cordY=459;
			else if(i==12) cordY=530;
			else if(i<=16) cordY=714;
		}
		return cordY;
	}

	/**
	 * Assegna una direzione allo SpawnPointPedestrian in base al suo numero identificativo
	 * @param i numero di SpawnPointPedestrian
	 * @return direction direzione dello SpawnPointPedestrian
	 */
	private static String setSPPDirection(int i) {
		// pre: i!=null
		String direction=null;
		if(difficulty=="Easy") {
			if(i<=2) direction="EAST";
			else if(i<=4) direction="SOUTH";
			else if(i<=6) direction="WEST";
			else if(i<=8) direction="NORTH";
		}
		if(difficulty=="Medium") {
			if(i<=2) direction="EAST";
			else if(i<=6) direction="SOUTH";
			else if(i<=8) direction="WEST";
			else direction="NORTH";
		}
		if(difficulty=="Hard") {
			if(i<=4) direction="EAST";
			else if(i<=8) direction="SOUTH";
			else if(i<=12) direction="WEST";
			else direction="NORTH";
		}
		return direction;
	}
	
	/**
	 * Assegna un valore ad rNumber, in base alla difficoltà selezionata
	 */
	public static void setRNumber() {
		if(difficulty.equals("Easy")) rNumber=8;
		else if(difficulty.equals("Medium")) iNumber=14;
		else iNumber=24; 
	}

	/**
	 * Assegna un valore ad iNumber, in base alla difficoltà selezionata
	 */
	public static void setINumber() {
		if(difficulty.equals("Easy")) iNumber=1; // 1 incrocio se difficoltà facile
		else if(difficulty.equals("Medium")) iNumber=2; // 2 incroci se difficoltà media
		else iNumber=4; // 4 incroci altrimenti
	}

	/**
	 * Assegna un valore a tlNumber, in base alla difficoltà selezionata
	 */
	public static void setTLNumber() {
		if(difficulty.equals("Easy")) tlNumber=4;
		else if(difficulty.equals("Medium")) tlNumber=8;
		else tlNumber=16;
	}

	/**
	 * Assegna un valore a spvNumber, in base alla difficoltà selezionata
	 */
	public static void setSPVNumber() {
		if(difficulty.equals("Easy")) spvNumber=4;
		else if(difficulty.equals("Medium")) spvNumber=6;
		else spvNumber=8;
	}

	/**
	 * Assegna un valore a sppNumber, in base alla difficoltà selezionata
	 */
	public static void setSPPNumber() {
		if(difficulty.equals("Easy")) sppNumber=8;
		else if(difficulty.equals("Medium")) sppNumber=12;
		else sppNumber=16;
	}

	/**
	 * Restituisce un oggetto di tipo Random
	 * @return randObj oggetto di tipo Random
	 */
	public static Random getRandObj() {
		return randObj;
	}
	
	/**
	 * Restituisce il valore della difficoltà selezionata
	 * @return difficulty difficoltà selezionata
	 */
	public static String getDifficulty(){
		return difficulty; 
	}

	/**
	 * Restituisce il numero di incroci generati
	 * @return iNumber numero di Intersection
	 */
	public static int getINumber() {
		return iNumber;
	}
	
	/**
	 * Restituisce l'insieme degli oggetti di tipo Road
	 * @return rList ArrayList di Road
	 */
	public static ArrayList<Road> getRList() {
		return rList;
	}

	/**
	 * Restituisce l'insieme degli oggetti di tipo Intersection
	 * @return iList ArrayList di Intersection
	 */
	public static ArrayList<Intersection> getiList() {
		return iList;
	}

	/**
	 * Restituisce l'insieme degli oggetti di tipo TrafficLight
	 * @return tlList ArrayList di TrafficLight
	 */
	public static ArrayList<TrafficLight> getTlList() {
		return tlList;
	}

	/**
	 * Restituisce l'insieme degli oggetti di tipo Vehicle
	 * @return vList List di Vehicle
	 */
	public static List<Vehicle> getvList() {
		return vList;
	}

	/**
	 * Restituisce l'insieme degli oggetti di tipo Pedestrian
	 * @return pList List di Pedestrian
	 */
	public static List<Pedestrian> getpList() {
		return pList;
	}
	
	/**
	 * Restituisce l'insieme degli oggetti di tipo SpawnPointVehicle
	 * @return spvList ArrayList di SpawnPointVehicle
	 */
	public static ArrayList<SpawnPointVehicle> getSpvList() {
		return spvList;
	}
	
	/**
	 * Restituisce l'insieme degli oggetti di tipo SpawnPointPedestrian
	 * @return sppList ArrayList di SpawnPointPedestrian
	 */
	public static ArrayList<SpawnPointPedestrian> getSppList() {
		return sppList;
	}

	/**
	 * Restituisce l'insieme degli oggetti di tipo KillPoint
	 * @return kpList ArrayList di KillPoint
	 */
	public static ArrayList<KillPoint> getKpList() {
		return kpList;
	}

	/**
	 * Rappresentazione di tipo String dell'oggetto di classe
	 */
	public String toString(){
		return "Map Difficulty: "+difficulty;
	}
	
	/**
	 * Verifica se l'oggetto è uguale ad un oggetto di tipo Map
	 */
	public boolean equals(Map map) {
		// pre: map!=null
		return (this==map);
	}
}
