package mining;
import java.io.Serializable;
import java.util.*;
import data.Data;
import data.Tuple;

/**
 * La classe si occupa di modellare un singolo Cluster.<br>
 * Ciascun cluster &egrave rappresentato da una tupla (Centroide) e da un insieme
 * di interi rappresentanti le righe della tabella {@link data.Data} appartenenti al
 * cluster.<br>
 * Il centroide &egrave modellato usando la classe {@link data.Tuple}, mentre l'insieme
 * delle tuple appartenenti al cluster &egrave modellato usando un insieme {@link java.util.Set}
 * 
 * @author Luca Suriano
 * @see data.Data
 * @see java.util.Set
 */

public class Cluster implements Serializable {
	
	/**
	 * Attributo rappresentante la tupla centroide del Cluster.
	 */
	private Tuple centroid;
	
	/**
	 * Insieme delle righe della tabella Data appartenenti al Cluster.
	 */
	
	private Set<Integer> clusteredData;
	
	/**
	 * Il costruttore si occupa di inizializzare i dati membro. In particolare
	 * verr&agrave inizializzata la tupla centroide (attraverso il parametro)
	 * e verr&agrave inizializzato l'insieme {@link #clusteredData}
	 * 
	 * @param centroid Tupla rappresentante il centroide del Cluster
	 */

	Cluster(Tuple centroid){
		this.centroid = centroid;
		clusteredData = new HashSet<Integer>();	
	}

	/**
	 * Ritorna il centroide per questo cluster.
	 */
	public Tuple getCentroid(){
		return centroid;
	}
	
	/**
	 * Ritorna il valore rappresentante la riga della tabella {@link data.Data}
	 * in posizione id.
	 * 
	 * @param id Posizione nell'insieme {@link #clusteredData} del valore da restituire.
	 * 
	 * @return Valore presente all'interno dell'insieme {@link #clusteredData}
	 */

	public int getClusterValue(int id){
		int i = 0;
		int res = 0;

		for(Integer item : clusteredData){
			if(id == i){
				res = item;
				break;
			}else
				i++;
		}

		return res;
	}
	
	/**
	 * Ritorna la dimensione dell'insieme contenente le righe della tabella {@link data.Data}
	 * appartenenti a questo Cluster.
	 */
	
	public int getClusterSize(){
		return clusteredData.size();
	}

	/**
	 * Calcola il centroide per questo cluster utilizzando il metodo
	 * {@link data.Item#update(Data, Set)}
	 * 
	 * @param data La tabella {@link data.Data} su cui effettuare il calcolo.
	 */
	

	void computeCentroid(Data data){
		for(int i=0;i<centroid.getLength();i++){
			centroid.get(i).update(data, clusteredData);	
		}
	}

	/**
	 * Aggiunge un elemento all'insieme {@link #clusteredData}.
	 * L'aggiunta avviene solo nel caso in cui tale elemento identificato da id,
	 * non &egrave presente nell'insieme.<br>
	 * Ci&ograve &egrave vero solo nel caso in cui la tupla identificata da id ha cambiato cluster.
	 * Quindi l'aggiunta all'insieme {@link #clusteredData} si verifica solo in questo caso.
	 * 
	 * @param id Identificativo rappresentante la riga della tabella {@link data.Data}
	 * 			 appertenente al cluster
	 * 
	 */
	boolean addData(int id){
		return clusteredData.add(id);

	}

	/**
	 * Il metodo verifica verifica se una transazione &egrave clusterizzata 
	 * (ovvero se &egrave presente nell'insieme {@link #clusteredData}).
	 * 
	 * Ritorna vero se la transazione &egrave clusterizzata, falso altrimenti.
	 */
	boolean contain(int id){
		boolean res = false;

		Iterator<Integer> clusIt = clusteredData.iterator();

		while(clusIt.hasNext() && res == false){
			Object item = clusIt.next();
			if(item instanceof Integer && item.equals(id))
				res = true;
		}

		return res;
	}

	/**
	 * Il metodo elimina la tupla identificata da id dall'insieme {@link #clusteredData} nel
	 * momento in cui questa cambia cluster.
	 */
	void removeTuple(int id){
		clusteredData.remove(id);
	}
	
	/**
	 * Il metodo si occupa di creare (e ritornare) una stringa contenente il centroide 
	 * di questo cluster.
	 */

	public String toString(){
		String str="Centroid=(";
		for(int i=0;i<centroid.getLength();i++)
			str+= " " + centroid.get(i) + " ";
		str+=")-";
		return str;

	}
	
	/**
	 * Il metodo si occupa di creare una stringa contenente il centroide di questo cluster.
	 * E per questo cluster si occupa inoltre di mostrare tutte le tuple in esso clusterizzate e
	 * la distanza massima.<br>
	 * Ci&ograve avviene utilizzando il riferimento passato come parametro.
	 * 
	 * @param data La tabella {@link data.Data} su cui vengono ottenute le tuple clusterizzate
	 * 			   in questo cluster
	 */

	public String toString(Data data){
		String str = "Centroid = (";

		for(int i=0; i<centroid.getLength(); i++)
			str += " " + centroid.get(i) + " ";

		str += ")\nExamples:\n";

		for(Integer itg : clusteredData){
			str+="[";

			for(int j=0; j<data.getNumberOfExplanatorySet(); j++)
				str += data.getAttributeValue(itg, j) + " ";
			str+="] dist="+getCentroid().getDistance(data.getItemSet(itg))+"\n";

		}

		str += "AvgDistance=" + getCentroid().avgDistance(data, clusteredData) + "\n";
		return str;
	}

}
