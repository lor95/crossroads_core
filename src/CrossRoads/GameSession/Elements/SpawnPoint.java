package CrossRoads.GameSession.Elements;

/**
 * Interfaccia che presenta il modello di funzionamento di uno spawner
 */
public interface SpawnPoint extends Runnable {
	/**
	 * Metodo per generare entità
	 */
	public void spawn();
	
	/**
	 * Metodo per restituire il limite inferiore dell'intervallo di generazione
	 * @return lim_INF
	 */
	public int setLim_INF();
	
	/**
	 * Metodo per restituire il limite superiore dell'intervallo di generazione
	 * @return lim_SUP
	 */
	public int setLim_SUP();
	
	/**
	 * Metodo per calcolare la frequenza di spawn attuale
	 * @param lim_INF limite inferiore
	 * @param lim_SUP limite superiore
	 */
	public void setSpawnFrequency(int lim_INF, int lim_SUP);	
}