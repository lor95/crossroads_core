package CrossRoads.GameSession.Elements;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javafx.geometry.Point2D;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import CrossRoads.SfxManager;
import CrossRoads.GUI.AppWindow;
import CrossRoads.GameSession.Map;
import CrossRoads.GameSession.Entities.Queue;
import CrossRoads.GameSession.Entities.Score;

/**
 * Classe che rappresenta un semaforo, sia come entità grafico-visiva, sia come entità interattiva
 */
@SuppressWarnings("serial")
public class TrafficLight extends JComponent {
	private int x, y;
	private static int radius;
	private Point2D stop2DPoint;
	private String laneDirection;
	private Color light;
	private boolean red;
	private boolean broken;
	private boolean yellow;
	private JPanel clickBox;
	private ActionListener repairTask;
	private ActionListener breakTask;
	private ActionListener autoBreakTask;
	private ActionListener greenToYellowTask;
	private ActionListener yellowToRedTask;
	private ActionListener showPointTask;
	private Timer repairTimer;
	private Timer breakTimer;
	private Timer autoBreakTimer;
	private Timer greenToYellowTimer;
	private Timer yellowToRedTimer;
	private Timer showPointTimer;
	private String message;
	private String showPoint;
	private Font breakFont;
	private Font showPointFont;
	private int clickCounter;
	private Queue queue;
	private Clip click;
	private Clip brk;
	private Clip repair;
	private boolean playable;
	private Clip repairSuccess;
	
	/**
	 * Costruttore di TrafficLight, per argomento una coppia di coordinate (x,y) e la direzione della corsia su cui il semaforo agisce
	 * @param cordX coordinata x per la generazione
	 * @param cordY coordinata y per la generazione
	 * @param lDirection direzione della corsia
	 */
	public TrafficLight(int cordX, int cordY, String lDirection) {
		setBounds(AppWindow.getDMX(), AppWindow.getDMY(), AppWindow.getDMWidth(), AppWindow.getDMHeight());
		breakFont=new Font("Arial", Font.BOLD, 12);
		showPointFont=new Font("Arial Black", Font.PLAIN, 12);
		x=cordX;
		y=cordY;
		radius=8;
		laneDirection=lDirection;
		System.out.println(toString());
		set2DLocation();
		queue=new Queue((int) stop2DPoint.getX(), (int) stop2DPoint.getY(), laneDirection); // coda
		red=true;
		setLightColor();
		message="";
		showPoint="";
		broken=false;
		playable=true;
		clickBox=new JPanel(); // pannello interattivo
		clickBox.setBounds(AppWindow.getDMX()+x-(2*radius), 
		AppWindow.getDMY()+y-(2*radius), 32, 32); // posizionamento grafico coincidente con quello del semaforo
		AppWindow.getGameScreen().add(this);
		AppWindow.getGameScreen().add(clickBox);
		clickBox.setOpaque(false);
		clickBox.addMouseListener(new MouseAdapter() { // mouse listener
			@SuppressWarnings("static-access")
			public void mousePressed(MouseEvent changeStatus) {
				try {
					if (!broken && changeStatus.getButton()==changeStatus.BUTTON1) {
						triggerClick();
						breakTimer=new Timer(500, breakTask); // 500 ms per contare i click (superati quelli il semaforo si rompe)
						breakTimer.restart();
						clickCounter=changeStatus.getClickCount();
						if (red) { // se il semaforo è rosso
							red=false; // diventa verde
							queue.releaseVehicles();
							yellow=false; 
							setLightColor(); // aggiorna il colore
							greenToYellowTimer=new Timer(10000, greenToYellowTask);
							greenToYellowTimer.restart(); // fai partire il timer per diventare giallo
						} else if(!red) { // se il semaforo è verde
							red=true;  // diventa rosso
							if(greenToYellowTimer!=null && greenToYellowTimer.isRunning()) 
								greenToYellowTimer.stop(); // se stava andando a giallo bloccalo
							else if(yellowToRedTimer!=null && yellowToRedTimer.isRunning()) { // se giallo e stava andando a rosso
								// la condizione "yellowToRedTimer!=null" è necessaria altrimenti potrebbe verificarsi NullPointerException
								red=false; // diventa verde
								yellow=false;
								setLightColor(); // aggiorna il colore
								greenToYellowTimer.restart(); // fai ripartire il timer per diventare giallo
								yellowToRedTimer.stop(); // ferma il timer per andare a rosso
							}
							setLightColor(); // in ogni caso aggiorna il colore
						} 
					} else if (broken && changeStatus.getButton()==changeStatus.BUTTON3) {
						if(playable)
							triggerRepair();
						playable=false;
						repairTimer=new Timer(15000, repairTask); // fai partire il timer per ripararlo
						message="REPAIRING..."; 
						repairTimer.start();
					}
				} catch (Throwable e) {
					JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage());
					throw e;
				}
			}
		});
		repairTask=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (broken) {
					broken=false;
					red=true;
					if(Score.getActualScore()>=10) {
						showPoint="+2";
						showPointTimer=new Timer(1000, showPointTask);
						if(showPointTimer.isRunning())
							showPointTimer.stop();
						showPointTimer.restart();
						Score.setActualScore(2);
					}
					message="";
					setLightColor();
					playable=true;
					triggerRepairSuccess();
					if(autoBreakTimer.isRunning()) autoBreakTimer.stop();
					autoBreakTimer=new Timer(setAutoBreakTime(), autoBreakTask);
					autoBreakTimer.restart(); // comincia a romperti di nuovo
					repairTimer.stop();
				}
			}
		};
		breakTask=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!broken && clickCounter>=2) {
					if(repairTimer!=null && repairTimer.isRunning())
						repairTimer.stop();
					broken=true;
					red=false;
					queue.releaseVehicles();
					Score.setActualScore(-12);
					showPoint="-12";
					showPointTimer=new Timer(1000, showPointTask);
					if(showPointTimer.isRunning())
						showPointTimer.stop();
					showPointTimer.restart();
					clickCounter=0;
					setLightColor();
					triggerBrk();
					autoBreakTimer.stop();
					if(greenToYellowTimer!=null && greenToYellowTimer.isRunning())
						greenToYellowTimer.stop();
					if(yellowToRedTimer!=null && yellowToRedTimer.isRunning())
						yellowToRedTimer.stop();
					breakTimer.stop();
				}		
			}
		};
		autoBreakTask=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			broken=true;
			playable=true;
			triggerBrk();
			red=false;
			queue.releaseVehicles();
			setLightColor();
			if(greenToYellowTimer!=null && greenToYellowTimer.isRunning()) 
				greenToYellowTimer.stop(); // se stava andando a giallo non farlo andare
			else if(yellowToRedTimer!=null && yellowToRedTimer.isRunning())
				yellowToRedTimer.stop(); // se stava andando a rosso non farlo andare
			autoBreakTimer.stop();
			}
		};
		greenToYellowTask=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				red=false;
				yellow=true;
				setLightColor();
				yellowToRedTimer=new Timer(5000, yellowToRedTask);
				yellowToRedTimer.restart();
				greenToYellowTimer.stop();
			}
		};
		yellowToRedTask=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				yellow=false;
				red=true;
				setLightColor();
				yellowToRedTimer.stop();
			}
		};
		showPointTask=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showPoint="";
				showPointTimer.stop();
			}
		};
		startBreaking();
	}
	
	/**
	 * Definisce l'inizio di un guasto automatico del semaforo, 
	 * assegnando all'oggetto di tipo Timer autoBreakTimer il tempo di durata e il task relativi, 
	 * e avviando infine tale Timer
	 */
	private void startBreaking() {
		autoBreakTimer=new Timer(setAutoBreakTime(), autoBreakTask);
		autoBreakTimer.start();
	}

	/**
	 * Definisce e restituisce un valore pseudo-randomico per l'oggetto di tipo Timer autoBreakTimer
	 * @return randTime valore pseudo-randomico del timer
	 */
	private int setAutoBreakTime() {
		int randTime=0;
		while(randTime<90000) { // min 1,5 minuti
			randTime=Map.getRandObj().nextInt(600000); // max 10 minuti
		}
		return randTime;
	}

	/**
	 * Definisce le coordinate dei vari punti di fermata per i veicoli, a seconda delle direzioni delle corsie 
	 */
	public void set2DLocation() {
		if(laneDirection=="WEST") stop2DPoint=new Point2D(x, y+38);
		else if(laneDirection=="NORTH") stop2DPoint=new Point2D(x-38, y);
		else if(laneDirection=="EAST") stop2DPoint=new Point2D(x, y-38);
		else if(laneDirection=="SOUTH") stop2DPoint=new Point2D(x+38, y);
	}
	
	/**
	 * Restituisce il punto in cui i veicoli devono fermarsi, ogniqualvolta "red==true"
	 * @return stop2DPoint
	 */
	public Point2D get2DLocation() {
		return stop2DPoint;
	}

	/**
	 * Restituisce l'oggetto di tipo String laneDirection, indicante la direzione ddella corsia (lane)
	 * @return laneDirection la direzione della corsia
	 */
	public String getLaneDirection() {
		return laneDirection;
	}
	
	/**
	 * Restituisce l'oggetto di tipo Queue queue, la coda relativa ad un semaforo
	 * @return queue la coda associata a sun semaforo
	 */
	public Queue getQueue() {
		return queue;
	}

	/**
	 * Restituisce la boolean isRed, che stabilisce se un semaforo è rosso o meno
	 * @return red variabile boolean indicante se un semaforo è rosso meno
	 */
	public boolean isRed() {
		return red;
	}

	/**
	 * Assegna un colore alla proprietà light, a seconda delle condizioni boolean del TrafficLight
	 */
	public void setLightColor() {
		if (yellow) {
			light=Color.YELLOW;
		} else {
			if (red) {
				light=Color.RED;
			} else {
				light=Color.GREEN;
			}
		}
	}
	
	/**
	 * Calcola e assegna valori alla coordinata x di TrafficLight a seconda della direzione della corsia
	 * @return cord coordinata x di TrafficLight
	 */
	private int setXCord() {
		int cord;
		if(laneDirection=="WEST" || laneDirection=="NORTH")
			cord=x+radius+4;
		else
			cord=x-(10*radius)+1;
		return cord;
	}
	
	private void drawRect2(Graphics g, int width, int height) {
		if(laneDirection=="WEST" || laneDirection=="NORTH") {
			g.setColor(Color.BLACK);
			g.fillRect(setXCord()-13, y-radius, 10, height);
			g.setColor(Color.ORANGE);
			g.drawRect(setXCord()-13, y-radius, width+10, height);
		}
		else {
			g.setColor(Color.BLACK);
			g.fillRect(setXCord()+width-3, y-radius, 10, height);
			g.setColor(Color.ORANGE);
			g.drawRect(setXCord()-3, y-radius, width+10, height);
		}
	}
	
	private void triggerClick() {
		try {
			click=AudioSystem.getClip();
			click.open(SfxManager.getTlCLick());
			click.loop(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void triggerBrk() {
		try {
			brk=AudioSystem.getClip();
			brk.open(SfxManager.getTlBreak());
			brk.loop(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void triggerRepair() {
		try {
			repair=AudioSystem.getClip();
			repair.open(SfxManager.getTlRepair());
			repair.loop(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void triggerRepairSuccess() {
		try {
			repairSuccess=AudioSystem.getClip();
			repairSuccess.open(SfxManager.getTlRepairSuccess());
			repairSuccess.loop(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Costruisce la grafica di un oggetto di tipo TrafficLight
	 * @param g oggetto di tipo Graphics
	 */
	public void paintComponent(Graphics g) {
		final int stdBreakWidth=74;
		final int stdBreakHeight=16;
		super.paintComponent(g);
		g.setFont(breakFont);
		if (broken) {
			if(!message.equals("")) {
				g.setColor(Color.BLACK);
				g.fillRect(setXCord()-3, y-radius, stdBreakWidth, stdBreakHeight);
				drawRect2(g, stdBreakWidth, stdBreakHeight);
			}
			g.setColor(Color.BLACK);
			g.fillOval(x-radius, y-radius, 2*radius, 2*radius);
			g.setColor(Color.YELLOW);
			g.drawString("!", x, y+radius-3);
			g.setColor(Color.ORANGE);
			g.drawOval(x-radius, y-radius, 2*radius, 2*radius);
		}
		else {
			g.setColor(light);
			g.fillOval(x-radius, y-radius, 2*radius, 2*radius);
			g.setColor(Color.BLACK);
			g.drawOval(x-radius, y-radius, 2*radius, 2*radius);
		}
		g.setColor(Color.ORANGE);
		g.drawString(message, setXCord(), y+radius-3);
		g.setFont(showPointFont);
		if(showPoint.length()>2) g.setColor(Color.BLUE);
		else g.setColor(Color.RED);
		g.drawString(showPoint, x-10, y-10);
//		g.drawRect((int)queue.getX(),(int) queue.getY(), (int)queue.getWidth(),(int) queue.getHeight()); 
	}
	
	/**
	 * Rimuove TrafficLight come entità grafica, e rimuove i JPanel di interazione ad esso associati
	 */
	public void clear() {
		queue=null;
		AppWindow.getGameScreen().remove(this);
		AppWindow.getGameScreen().remove(clickBox);
		if(repairTimer!=null && repairTimer.isRunning())
			repairTimer.stop();
		if(autoBreakTimer!=null && autoBreakTimer.isRunning())
			autoBreakTimer.stop();
		if(greenToYellowTimer!=null && greenToYellowTimer.isRunning())
			greenToYellowTimer.stop();
		if(yellowToRedTimer!=null && yellowToRedTimer.isRunning())
			yellowToRedTimer.stop();
	}
	
	/**
	 * Rappresentazione di tipo String dell'oggetto di classe
	 */
	public String toString() {
		return getClass().getName()+"[laneDirection: "+laneDirection+"] has been generated";
	}
	
	/**
	 * Verifica se l'oggetto è uguale ad un oggetto di tipo TrafficLight
	 */
	public boolean equals(TrafficLight tl) {
		// pre: tl!=null
		boolean eq=false;
		if(x==tl.getX() && y==tl.getY()) eq=true;
		return eq;
	}
}