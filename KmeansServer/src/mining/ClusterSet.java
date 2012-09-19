package mining;

import java.io.Serializable;
import data.Data;
import data.Tuple;
import data.OutOfRangeSampleSize;

/**
 * La classe ClusterSet rappresenta un insieme di cluster. Tale insieme &egrave rapprentato
 * dal vettore (attributo della classe) C.<br> 
 * Ciascuna cella del vettore conterr&agrave un cluster che sar&agrave rapprentato da una tupla
 * (centroide) e da un insieme di indici di riga della tabella {@link Data} appartenenti al
 * a ciascun centroide.
 * 
 * @author Luca Suriano
 * 
 * @see Cluster
 * @see data.Data
 *
 */

public class ClusterSet implements Serializable {
	/**
	 * Attributo che rappresenta un vettore di Cluster e rappresenta il ClusterSet
	 */
	private Cluster C[];
	
	/**
	 * Attributo che rappresenta l'indice del vettore {@link #C}.
	 * L'attributo viene incrementato ogni qualvolta viene aggiunto un nuovo cluster.
	 */
	private int i;
	
	/**
	 * Crea il vettore che rappresenta il ClusterSet assegnadogli dimensione k
	 * 
	 * @param k Dimensione del vettore rappresentante il ClusterSet {@link #C}
	 */
	public ClusterSet(int k)
	{
		C = new Cluster[k];
	}
	
	/**
	 * Il metodo si occupa di aggiungere un Cluster al vettore, e di incrementare
	 * l'indice {@link #i} per successive aggiunte.
	 * 
	 * @param c Cluster da aggiungere al vettore.
	 */
	
	void add(Cluster c)
	{
		C[i] = c;
		i++;
	}
	
	/**
	 * Il metodo ritorna un Cluster presente all'interno vel vettore {link #C}
	 * 
	 * @param i Indice corrispondente al Cluster che si vuole ottenere
	 * 
	 * @return Cluster in posizione i del vettore {@link #C}
	 */
	
	public Cluster get(int i)
	{
		return C[i];
	}
	
	/**
	 * Ritorna la dimensione del ClusterSet
	 * 
	 * @return Valore intero rappresentante la dimensione del ClusterSet
	 */
	
	public int getLen(){
		return C.length;
	}
	
	/**
	 * Sceglie i centroidi, crea un cluster per ogni centroide e lo memorizza in C
	 * 
	 * @param data La tabella rappresentata dalla classe {@link data.Data}
	 * 
	 * @throws OutOfRangeSampleSize
	 */
	
	void initializeCentroids(Data data) throws OutOfRangeSampleSize
	{
		
		int centroidIndexes[] = data.sampling(C.length);
		
		for(int i=0; i<centroidIndexes.length; i++)
		{
			Tuple ceontroid = data.getItemSet(centroidIndexes[i]);
			add(new Cluster(ceontroid));
		}
	}
	
	/**
	 * Calcola la distanza tra la tupla riferita da tuple ed il centroide di ciascun 
	 * cluster in C e restituisce il cluster più vicino.
	 * 
	 * @param tuple Tupla da considerare.
	 * 
	 * @return Cluster più vicino alla tupla passata come paramentro.
	 */
	
	Cluster nearestCluster(Tuple tuple)
	{
		int i, ind = 0;
		double minDist = tuple.getDistance(C[0].getCentroid());
		
		for(i=1; i<C.length; i++)
		{
			double currDist = tuple.getDistance(C[i].getCentroid());
			if(minDist > currDist){
				minDist = currDist;
				ind = i;
			}
		}
		
		return get(ind);
	}
	
	/**
	 * Identifica e restituisce il cluster a cui la tupla (rappresentate l'esempio identificato da id) 
	 * appartiene. Se la tupla non è inclusa in nessun cluster restituisce null
	 * 
	 * @param id Indice di una riga della tabella {@link data.Data}
	 * 
	 * @return Cluster a cui appartiene la tupla identificata da id 
	 */
	
	public Cluster currentCluster(int id)
	{
		int i, ind = 0;
		Cluster emp = null;
		
		for(i = 0; i < C.length; i++)
		{
			if(this.get(i).contain(id)){
				ind = i;
				emp = get(ind);
			}
		}
		
		return emp;	
	}
	
	/**
	 * Calcola il nuovo centroide per ciascun cluster in C
	 * 
	 * @param data La tabella rappresentata dalla classe {@link data.Data}
	 */
	
	void updateCentroids(Data data)
	{
		int i;
		
		for(i = 0; i < C.length; i++)
			C[i].computeCentroid(data);
	}
	
	/**
	 * Il metodo si occupa di restituire una stringa formata dalla
	 * concatenzione delle diverse stringhe, ottenute richiamando il metodo
	 * {@link Cluster#toString()} su ciascun Cluster appartenente al vettore
	 * {@link #C}.
	 */
	
	public String toString()
	{
		int i;
		String centroid = new String();
		
		for(i = 0; i < C.length; i++)
		{
			centroid += C[i].toString() + " ";
		}
		
		return centroid;
	}
	
	/**
	 * Il metodo si occupa di restituire una stringa formata dalla
	 * concatenzione delle diverse stringhe, ottenute richiamando il metodo
	 * {@link Cluster#toString(Data)} su ciascun Cluster appartenente al vettore
	 * {@link #C}.
	 * 
	 * @param data La tabella rappresentata dalla classe {@link data.Data}
	 * 
	 */
	
	public String toString(Data data)
	{
		String str="";
		
		for(int i=0;i<C.length;i++)
		{
			if (C[i]!=null){
				str += i + ": " + C[i].toString(data) + "\n";
			}
		}
		
		return str;
	}
}
