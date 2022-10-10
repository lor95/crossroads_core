package CrossRoads;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.derby.jdbc.EmbeddedDriver;
import CrossRoads.GUI.AppWindow;
import CrossRoads.GameSession.Map;
import CrossRoads.GameSession.Entities.Score;

/**
 * Classe che contiene al suo interno metodi volti alla creazione di un DB embedded
 */
public abstract class Database {
	private static SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
	private static SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
	private static SimpleDateFormat lengthFormat=new SimpleDateFormat("mm:ss");
	private static final int placeholder=0; // costante che agisce da placeholder per il valore della colonna position
	private static String playerName;
	private static JTable tblEasy;
	private static JTable tblMedium;
	private static JTable tblHard;
	private static DatabaseMetaData dbmd;
	private static ResultSet rs;
	private static Driver derbyEmbeddedDriver;
	private static Connection conn=null;  // connessione posta a null; se tutto funziona regolarmente, non restituisce mai null, altrimenti dà eccezione
	private static PreparedStatement pstmt; // PreparedStatement è un oggetto che rappresenta uno statement SQL precompilato
	private static Statement stmt=null;
	private static CachedRowSet crsEasy;
	private static CachedRowSet crsMedium;
	private static CachedRowSet crsHard;
	private static ResultSet rsEasy;
	private static ResultSet rsMedium;
	private static ResultSet rsHard;

	/**
	 * Crea una connessione tramite driver embedded della libreria Derby
	 */
	public static void createConnection() {   
		try {
			derbyEmbeddedDriver=new EmbeddedDriver();
			DriverManager.registerDriver(derbyEmbeddedDriver);
			conn=DriverManager.getConnection("jdbc:derby:leaddb;create=true");  // viene creata una connessione al database Derby di nome leaddb
			conn.setAutoCommit(false);  // quando la modalità autocommit è true, tutti gli statement SQL sono eseguiti come transazioni individuali
			if (conn!=null) System.out.println("Connection established");
		} catch (SQLException e) {
			if (((e.getErrorCode()==40000) && ("XJ040".equals(e.getSQLState())))) {
				System.out.println("Another JVM claimed the connection; current JVM has been terminated");
				System.exit(0);  // se avviamo per errore una seconda volta l'applicazione, questa fallisce a connettersi perché già la prima JVM vi è connessa; catch dell'errore e spegnimento conseguente
			} else {
				JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage());
			}
		}
	}

	/**
	 * Stabilisce se la table PLAYER esiste, altrimenti la crea; inoltre richiama funzioni per creare le JTable col contenuto dei ResultSet in cache
	 */
	public static void createTable(String tableName) {
		try {
			conn=DriverManager.getConnection("jdbc:derby:leaddb");
			stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			if (conn!=null) {
				dbmd=conn.getMetaData();
				rs=dbmd.getTables(null, null, tableName.toUpperCase(), null);  // il nome della table è salvato nei metadata in maiuscolo
				if(rs.next())
				{
					System.out.println("Table "+rs.getString("TABLE_NAME")+" already exists");
				}
				else
				{
					stmt.executeUpdate("create table player (id integer primary key not null generated always as identity (start with 1, increment by 1),   "
							+ "position integer not null, name varchar(10) not null, score integer not null, duration varchar(5) not null, date varchar(10) not null, "
							+ "time varchar(5) not null, level varchar(6) not null)");
					System.out.println("Table has been created correctly");
				}
				tblEasy=new JTable();
				tblMedium=new JTable();
				tblHard=new JTable();
				crsEasy=RowSetProvider.newFactory().createCachedRowSet();
				crsMedium=RowSetProvider.newFactory().createCachedRowSet();
				crsHard=RowSetProvider.newFactory().createCachedRowSet();
				resultSetToCache(rsEasy, crsEasy, "Easy");  // se la table player è vuota (perché creata), i crs sono privi di info; se una table è recuperata, i rs eseguono la query su quanto c'è e si salva il risultato nei crs
				resultSetToCache(rsMedium, crsMedium, "Medium");
				resultSetToCache(rsHard, crsHard, "Hard");
				crsToTableModel(crsEasy, tblEasy);
				crsToTableModel(crsMedium, tblMedium);
				crsToTableModel(crsHard, tblHard);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}

	/**
	 * Salva i vari dati della sessione di gioco, che saranno visualizzabili da apposite JTable
	 */
	public static void fillTable() {
		try {
			conn=DriverManager.getConnection("jdbc:derby:leaddb");
			Date match_date=AppWindow.getDate();
			Timestamp match_time=new Timestamp(AppWindow.getStartTime());
			Timestamp match_length=new Timestamp(Game.getEndTime()-AppWindow.getStartTime());
			String level=Map.getDifficulty();
			pstmt=conn.prepareStatement("insert into player (position, name, score, duration, date, time, level) values (?,?,?,?,?,?,?)");  // '?' è un placeholder; i valori sono assegnati successivamente
			pstmt.setInt(1, placeholder);
			pstmt.setString(2, playerName);
			pstmt.setInt(3, Score.getActualScore());
			pstmt.setString(4, lengthFormat.format(match_length));
			pstmt.setString(5, dateFormat.format(match_date));
			pstmt.setString(6, timeFormat.format(match_time));
			pstmt.setString(7, level);
			pstmt.executeUpdate();
			crsEasy=RowSetProvider.newFactory().createCachedRowSet();  // si ricreano i crs perché saranno riempiti da nuovi ResultSet
			crsMedium=RowSetProvider.newFactory().createCachedRowSet();
			crsHard=RowSetProvider.newFactory().createCachedRowSet();
			resultSetToCache(rsEasy, crsEasy, "Easy");
			resultSetToCache(rsMedium, crsMedium, "Medium");
			resultSetToCache(rsHard, crsHard, "Hard");
			crsToTableModel(crsEasy, tblEasy);
			crsToTableModel(crsMedium, tblMedium);
			crsToTableModel(crsHard, tblHard);
			conn.commit();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}

	/**
	 * Chiude lo statement, la connessione, e infine tutto il motore Derby
	 */
	public static void closeConnection() {   
		try {
			conn=DriverManager.getConnection("jdbc:derby:leaddb");
			stmt.close();  // di conseguenza sono chiusi implicitamanete i ResultSet associati a tale Statement
			conn.close();
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
		} catch (SQLException e) {  // quando il motore Derby è spento, si verifica una SQLException; la catturiamo e viene riferito se lo spegnimento è avvenuto normalmente o no
			if (((e.getErrorCode()==50000) && ("XJ015".equals(e.getSQLState())))) {
				System.out.println("Derby shut down normally");
			} else {
				System.err.println("Derby did not shut down normally"); 
				System.err.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Il ResultSet è salvato in memoria locale grazie all'apposito oggetto Java CachedRowSetImpl 
	 */
	public static CachedRowSet resultSetToCache(ResultSet rs, CachedRowSet crs, String difficulty) {
		try {
			rs=stmt.executeQuery("select position, name, score, duration, date, time from player where level='"+difficulty+"' order by score desc");
			crs.populate(rs);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		return crs;
	}
	
	/**
	 * Il ResultSet è salvato in memoria locale grazie all'apposito oggetto Java CachedRowSetImpl. Questo ResultSet è ristretto a un determinato valore del campo "name"
	 */
	private static CachedRowSet restrictedResultSetToCache(ResultSet rs, CachedRowSet crs, String difficulty) {
		try {
			rs=stmt.executeQuery("select position, name, score, duration, date, time from player where name='"+AppWindow.getWantedName()+"' and level='"+difficulty+"' order by score desc");
			crs.populate(rs);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		return crs;
	}
	
	/**
	 * Attua metodi che creano JTable complete di tutti i dati salvati nel DB
	 */
	/*public static void completeJTables() {
		try {
			crsEasy=new CachedRowSetImpl();
			crsMedium=new CachedRowSetImpl();
			crsHard=new CachedRowSetImpl();
			resultSetToCache(rsEasy, crsEasy, "Easy");
			resultSetToCache(rsMedium, crsMedium, "Medium");
			resultSetToCache(rsHard, crsHard, "Hard");
			crsToTableModel(crsEasy, tblEasy);
			crsToTableModel(crsMedium, tblMedium);
			crsToTableModel(crsHard, tblHard);
			getTblEasy();
			getTblMedium();
			getTblHard();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}*/
	
	/**
	 * Attua metodi che creano JTable contenenti solo i valori corrispondenti ad uno specifico "player name"
	 */
	public static void restrictedJTables() {
		try {
			crsEasy=RowSetProvider.newFactory().createCachedRowSet();
			crsMedium=RowSetProvider.newFactory().createCachedRowSet();
			crsHard=RowSetProvider.newFactory().createCachedRowSet();
			restrictedResultSetToCache(rsEasy, crsEasy, "Easy");
			restrictedResultSetToCache(rsMedium, crsMedium, "Medium");
			restrictedResultSetToCache(rsHard, crsHard, "Hard");
			crsToTableModel(crsEasy, tblEasy);
			crsToTableModel(crsMedium, tblMedium);
			crsToTableModel(crsHard, tblHard);
			getTblEasy();
			getTblMedium();
			getTblHard();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}
	
	/**
	 * I metadata di ciascun ResultSet in cache (CachedRowSetImpl) costruiscono il modello di una JTable. Si aggiungono tante righe alla JTable quanti gli elementi nel CachedRowSetImpl.
	 * @param crs è il CachedRowSetImpl (ovvero ResultSet in cache)
	 * @param table è la JTable
	 */
	public static void crsToTableModel(CachedRowSet crs, JTable table) {
		try {
			DefaultTableModel tableModel=new DefaultTableModel();  // crea un modello per la JTable
			ResultSetMetaData metaData=crs.getMetaData();  // recupera i metadata dal CachedRowSetImpl
			int columnCount=metaData.getColumnCount();  // ottiene il numero di colonne dai metadata
			for (int columnIndex=1; columnIndex<=columnCount; columnIndex++) {  
				tableModel.addColumn(metaData.getColumnLabel(columnIndex));  // prende i nomi delle colonne dai metadata e aggiunge il numero di colonne al modello per JTable
			}
			Object[] row=new Object[columnCount];  // crea un array di tipi Object di dimensione pari al numero di colonne
			while (crs.next()){  // scorre il ResultSet
				for (int i=0; i<columnCount; i++) {
					if(i==0) row[i]=crs.getRow();  // al posto del valore placeholder di position stampiamo alla prima colonna il numero di riga (ordinato nel modo dato dalla query)
					else row[i]=crs.getObject(i+1);  // assegna un Object dalla colonna con indice specifico del ResultSet all'array row di Object (esclusa la prima colonna)
				}
				tableModel.addRow(row);  // aggiunge una riga al modello per JTable con l'array di Object per argomento
				System.out.printf("%s %d %s %s %s\n", crs.getString(2), crs.getInt(3), crs.getString(4), crs.getString(5), crs.getString(6));
			}
			table.setModel(tableModel);  // assegna il modello per JTable ad una JTable
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}
	
	/**
	 * Restituisce la JTable relativa al livello "Easy"
	 * @return tblEasy
	 */
	public static JTable getTblEasy() {
		return tblEasy;
	}

	/**
	 * Restituisce la JTable relativa al livello "Medium"
	 * @return tblMedium
	 */
	public static JTable getTblMedium() {
		return tblMedium;
	}

	/**
	 * Restituisce la JTable relativa al livello "Hard"
	 * @return tblHard
	 */
	public static JTable getTblHard() {
		return tblHard;
	}
	
	/**
	 * Restituisce il ResultSet relativo al livello "Easy"
	 * @return rsEasy
	 */
	public static ResultSet getRSEasy() {
		return rsEasy;
	}
	
	/**
	 * Restituisce il ResultSet relativo al livello "Medium"
	 * @return rsMedium
	 */
	public static ResultSet getRSMedium() {
		return rsMedium;
	}
	
	/**
	 * Restituisce il ResultSet relativo al livello "Hard"
	 * @return rsHard
	 */
	public static ResultSet getRSHard() {
		return rsHard;
	}
	
	/**
	 * Restituisce il CachedRowSetImpl relativo al livello "Easy"
	 * @return crsEasy
	 */
	public static CachedRowSet getCRSEasy() {
		return crsEasy;
	}
	
	/**
	 * Restituisce il CachedRowSetImpl relativo al livello "Medium"
	 * @return crsMedium
	 */
	public static CachedRowSet getCRSMedium() {
		return crsMedium;
	}
	
	/**
	 * Restituisce il CachedRowSetImpl relativo al livello "Hard"
	 * @return crsHard
	 */
	public static CachedRowSet getCRSHard() {
		return crsHard;
	}

	/**
	 * Assegna la String name, passata come argomento a questo metodo, all'attributo playerName
	 */
	public static void setName(String name) {
		playerName=name;
	}
	
	/**
	 * Rappresentazione di tipo String dell'oggetto di classe
	 */
	public String toString(){
		return getClass().getName();
	}
	
	/**
	 * Verifica se l'oggetto è uguale ad un oggetto di tipo Database
	 */
	public boolean equals(Database database) {
		// pre: database!=null
		return (this==database);
	}
}

