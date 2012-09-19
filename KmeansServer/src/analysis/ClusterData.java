package analysis;

import java.util.ArrayList;
import java.util.List;

import mining.Cluster;

/**
 * La classe si occupa di suddividere tutti i valori per i due assi in gruppi.
 * Ogni gruppo di valori coincide ai valori per gli assi del grafico, per ciascun Cluster.
 * <br>
 * La classe &egrave di supporto alla classe {@link ScatterGraph}, per la creazione dei
 * dati relativi al grafico.
 * 
 *  @author Luca Suriano
 *  @see ScatterGraph
 *  @see mining.Cluster
 *  @see java.util.List
 */
class ClusterData {

	/**
	 * I valori da raggruppare
	 */
	List<Number> values;
	
	/**
	 * La lista dei valori relativi ad un solo Cluster
	 */
	List<Number> clustIndexes;
	
	/**
	 * Il Cluster da considerare per il raggruppamento
	 */
	private Cluster mainClust;

	/**
	 * Il costruttore si occupa di inizializzare i membri della classe e richiamare il metodo
	 * {@link ClusterData#createClustList()} che si occuper&agrave opportunamente di raggruppare
	 * i dati da restituire.
	 *
	 * @param val La lista dei valori da raggruppare
	 * @param cluster Il Cluster da considerare per il raggruppamento
	 */
	ClusterData(List<Number> val, Cluster cluster){
		this.values = val;
		this.clustIndexes = new ArrayList<Number>();
		this.mainClust = cluster;
		createClustList();
	}

	/**
	 * Il metodo si occupa di effettuare il ragruppamento. Principalmente il metodo considera
	 * il Cluster (contenente le righe della tabella {@link data.Data} ad esso appartenenti)
	 * e per ogni valore in esso contenuto, preleva il corrispondente dalla lista completa 
	 * dei valori da raggrupare e lo inserisce nella lista {@link #clustIndexes}.<br>
	 * Infatti la lista dei valori da raggruparre {@link #values} avr&agrave la stessa dimensione
	 * della tabella {@link data.Data} e ciascun elemento nella lista, corrisponde a una riga
	 * della tabella. Quindi leggendo ciascuno dei valori appartenenti ad un Cluster si ottiene
	 * l'indice da cui effettuare la lettura nella lista {@link #values}. Il valore cos&igrave
	 * ottenuto viene inserito all'interno della lista {@link #clustIndexes}.<br>
	 * tutti i valori degli assi appartenenti al Cluster {@link #mainClust}.<br>
	 * Il grafico cos&igrave utilizzando questi dati, pu&ograve rappresentare indistintamente ogni
	 * cluster invece di avere un'insieme di punti senza alcun significato.
	 * 
	 *  @see Cluster
	 *  @see Cluster#getClusterValue(int)
	 *  @see data.Data
	 */
	private void createClustList(){
		int clusterSize = this.mainClust.getClusterSize();
		for(int i = 0; i < clusterSize; i++){
			int val = mainClust.getClusterValue(i);
			this.clustIndexes.add(values.get(val));
		}		
		return;
	}	

	/**
	 * Ritorna la dimensione della lista dei valori raggruppati.
	 *
	 * @return La dimensione della lista
	 */
	int getListSize(){
		return this.clustIndexes.size();
	}

	/**
	 * Ritorna il valore in posizione id della lista dei valori raggruppati.
	 *
	 * @param ind L'indice del valore da leggere
	 * @return Il valore letto
	 */
	Number getCValue(int ind){
		return this.clustIndexes.get(ind);
	}
}