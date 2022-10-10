package CrossRoads.GameSession.Entities;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.Timer;
import CrossRoads.Game;
import CrossRoads.GUI.AppWindow;

/**
 * Classe che calcola e rappresenta graficamente il punteggio di una sessione di gioco
 */
@SuppressWarnings("serial")
public class Score extends JComponent {
	private static int actualScore;
	private static final int maxScore=Integer.MAX_VALUE-3;
	private static Font fntScore;
	private static Font fntAddedScore;
	private static String scoreAdded;
	private static boolean showPoint;
	private static Timer hidePointTimer;
	private static ActionListener hidePointTask;
	private static String daytime; /////////////////////////////////////////////
	

	/**
	 * Costruttore di Score, inizializza il punteggio a zero, posiziona e dimensiona il componente su schermo
	 */
	public Score() {
		fntScore=new Font("Calibri", Font.BOLD, 25);
		fntAddedScore=new Font("Calibri", Font.BOLD+Font.ITALIC, 20);
		showPoint=false;
		scoreAdded="";
		actualScore=0;
		setBounds(AppWindow.getDMX(), AppWindow.getDMY(), AppWindow.getDMWidth(), AppWindow.getDMHeight());
		hidePointTask=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showPoint=false;
				hidePointTimer.stop();
			}
		};
	}

	/**
	 * Aggiorna e assegna un nuovo valore alla variabile actualScore
	 * @param s punteggio da aggiungere
	 */
	public static void setActualScore(int s) {
		actualScore+=s;
		showScoreAdded(s);
		if(actualScore<0) actualScore=0; // ripristina la condizione di partenza del punteggio
		else if(actualScore>=maxScore) {
			Game.endSession();
		}
	}
	
	private static void showScoreAdded(int s) {
		showPoint=true;
		scoreAdded=""+s;
		if(hidePointTimer!=null && hidePointTimer.isRunning())
			hidePointTimer.stop();
		hidePointTimer=new Timer(1000, hidePointTask);
		hidePointTimer.start();
	}

	/**
	 * Restituisce l'oggetto actualScore
	 * @return actualScore punteggio corrente
	 */
	public static int getActualScore() {
		return actualScore;
	}
	
	private static String blankSpace() {
		String blank="";
		int i;
		for(i=0; i<(6+(""+actualScore).length()); i++) {
			blank=blank+"  ";
		}
		return blank;
	}

	/**
	 * Costruisce la grafica di un oggetto di tipo Score
	 * @param g oggetto di tipo Graphics
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setFont(fntAddedScore);
		if(showPoint) {
			if(scoreAdded.length()>1) {
				g.setColor(Color.DARK_GRAY);
				g.drawString(blankSpace()+scoreAdded, 11, 41);
				g.setColor(Color.BLUE);
				g.drawString(blankSpace()+scoreAdded, 10, 40);
			}
			else {
				g.setColor(Color.DARK_GRAY);
				g.drawString(blankSpace()+"+"+scoreAdded, 11, 41);
				g.setColor(Color.RED);
				g.drawString(blankSpace()+"+"+scoreAdded, 10, 40); 
			}
		}
//		g.drawString(daytime, 2, 20);/////////////////////////////////////////////////
		g.setFont(fntScore);
		g.setColor(Color.DARK_GRAY);
		g.drawString("Score: "+actualScore, 12, 62);
		g.setColor(Color.YELLOW);
		g.drawString("Score: "+actualScore, 10, 60);
		repaint();
	}
	
	/* provvisorio per la transizione */
	public static void setdaytime(String t) {
		daytime=t;
	}
	
	/**
	 * Calcola una descrizione testuale del punteggio
	 */
	public String toString() {
		return "Score: "+actualScore;
	}
	
	/**
	 * Verifica se l'oggetto è uguale ad un oggetto di tipo Score
	 */
	public boolean equals(Score score) {
		// pre: score!=null
		boolean eq=false;
		if(getX()==score.getX() && getY()==score.getY()) eq=true;
		return eq;
	}
}