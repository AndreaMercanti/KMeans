package mining;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import data.Attribute;
import data.Data;
import data.OutOfRangeSampleSize;
import data.Tuple;

/**
 * La classe KmeansMiner &egrave la classe che si occupa di effettuare la scoperta
 * dei cluster (attraverso l'algoritmo kmeans che contiene). Inoltre la classe si occupa
 * sia di salvare su file le attivit&agrave di scoperta dei cluster e sia di leggere
 * da file attivit&agrave precedentemente effettuate.<br>
 * La classe perci&ograve implementa l'interfaccia {@link java.io.Serializable} in modo
 * da permettere il salvattaggio su file degli oggetti che essa manipola (ovviamente tutte le
 * classi utilizzate implementano l'interfaccia).<br>
 * La logica mantenuta nel salvataggio (e quindi nella lettura) dei file utilizzati &egrave
 * la seguente:
 * <center>nome_tabella_database + numero di cluster da scoprire</center><br>
 * Quindi per esempio se la tabella si chiama playtennis e il numero di cluster che si vuole
 * scoprire &egrave 50 il nome del file salvato sar&agrave:<br>
 * <center>playtennis50</center><br>
 * Tale file sar&agrave utilizzato nel caso in cui si richiama la funzionalit&agrave di lettura 
 * dell'attivit&agrave di scoperta da file piuttosto che dalla base di dati.
 *  
 * @author Luca Suriano
 * @see Cluster
 * @see ClusterSet
 *
 */

public class KmeansMiner implements Serializable{
	
	/**
	 * L'attributo rappresenta l'insieme dei cluster che saranno risultato
	 * dell'attivit&agrave di scoperta oppure saranno il risultato di una lettura
	 * di tale attivit&agrave da file.
	 * 
	 * @see ClusterSet
	 */
	private ClusterSet C;
	
	/**
	 * Il costruttore si occupa di inizializzare il ClusterSet in base
	 * al numero di Cluster che si vuole scoprire.
	 * 
	 * @param k Numero di Cluster da scoprire.
	 * @see Cluster 
	 * @see ClusterSet
	 */
	public KmeansMiner(int k)
	{
		C = new ClusterSet(k);
	}
	
	/**
	 * Il costruttore si occupa di inizializzare il ClusterSet. In questo caso l'inizializzazione
	 * avviene leggendo il file (identificato dal parametro fileName). Il nome del file segue
	 * la logica descritta precedentemente (vedi descrizione della classe).<br>
	 * In questo caso il ClusterSet conterr&agrave l'insieme di Cluster dovuto a precedenti
	 * attivit&agrave di scoperta.
	 * 
	 * @param fileName	Nome del file da leggere.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @see ClusterSet
	 * @see Cluster
	 */
	
	public KmeansMiner(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		 FileInputStream inputFile = new FileInputStream(fileName); 
		 ObjectInputStream objectInput = new ObjectInputStream(inputFile); 
		 C = (ClusterSet)objectInput.readObject();
		 inputFile.close(); 
	}
	
	/**
	 * Il metodo si occupa di effettuare il salvataggio su file (detto anche serializzazione)
	 * delle attivit&agrave di scoperta. Il salvataggio risulter&agrave utile nel caso in cui
	 * si vorranno ottenere precedenti attivit&agrave di scoperta senza interragare la base
	 * di dati.
	 * @param fileName Nome del file da leggere.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	
	public void salva(String fileName) throws FileNotFoundException, IOException
	{
         FileOutputStream outputFile = new FileOutputStream(fileName); 
         ObjectOutputStream objectOutput = new ObjectOutputStream(outputFile); 
         objectOutput.writeObject(C); 
         objectOutput.flush(); 
         outputFile.close(); 
	}
	
	/**
	 * Ritorna il ClusterSet {@link #C}
	 */
	
	public ClusterSet getC(){
		return C;
	}
	
	/**
	 * Il metodo (utilizzato da {@link server.ServerOneClient} nel comando di creazione
	 * del grafico) &egrave utile per poter ritornare l'attributo della tupla centroide 
	 * all'indice ind (specificato come parametro).<br>
	 * Il metodo &egrave stato creato in quanto nel caso in cui il client richiede
	 * una lettura da file (la tabella {@link server.ServerOneClient#data} non sar&agrave 
	 * inizializzata) l'unico modo per ottenere un attributo per la creazione del
	 * grafico, &egrave quello di leggerlo dai centroidi ottenuti nell'attivit&agrave di 
	 * lettura da file (ovviamente tali attributi sono equivalenti per ciascun centroide)
	 * 
	 * @param ind Indice dell'attributo da ottenere.
	 * 
	 * @return Oggetto istanza di una sottoclasse di {@link data.Attribute}.
	 */
	
	public Attribute getCAttrib(int ind){
		return this.C.get(0).getCentroid().get(ind).getAttribute();
	}
	
	/**
	 * Il metodo (utilizzato da {@link server.ServerOneClient} nel comando di creazione
	 * del grafico) &egrave utile per poter ritornare un'array di stringhe contenente
	 * i nomi di tutti gli attributi che fanno parte della tupla centroide.<br>
	 * Il metodo &egrave stato creato in quanto nel caso in cui il client richiede
	 * una lettura da file (la tabella {@link server.ServerOneClient#data} non sar&agrave 
	 * inizializzata) l'unico modo per ottenere i nomi degli attributi &egrave leggerli
	 * da una singola tupla (centroide).<br>
	 * I nomi degli attributi qui ottenuti saranno utilizzati dal client, che specificher&agrave
	 * per quali attributi ottenere il grafico.
	 * 
	 * @return Array di stringhe contenente i nomi degli attributi.
	 */
	
	public String[] kmAttributesString(){
		
		Tuple cent = C.get(0).getCentroid();
		String[] attributes = new String[cent.getLength()];

		for(int i = 0; i < cent.getLength(); i++)
			attributes[i] = cent.get(i).getAttribute().getName();
		
		return attributes;
		
	}
	
	/**
	 * Il metodo esegue l'algoritmo k-means eseguendo i seguenti passi:<br>
	 * 1. Scelta casuale di centroidi per k clusters assegnandoli al ClusterSet.<br>
	 * 2. Assegnazione di ciascuna riga della matrice in data al cluster avente 
	 * 	  centroide pi&ugrave vicino all'esempio (utilizzando il metodo 
	 * 	  {@link ClusterSet#nearestCluster(Tuple)}).
	 * 3. Calcolo dei nuovi centroidi per ciascun cluster (utilizzando il metodo
	 * 	  {@link ClusterSet#updateCentroids(Data)}<br>
	 * 4. Ripete i passi 2 e 3 finch&egravé due iterazioni consecuitive non restituiscono 
	 * 	  centroidi uguali.<br>
	 * Il metodo propaga l'eccezione OutOfRangeSampleSize nel caso in cui il numero
	 * k di cluster da scoprire risulta essere maggiore delle tuple presenti nella
	 * tabella {@link data.Data} oppure se tale numero &egrave 0.
	 * 
	 * @param data	La tabella {@link data.Data} che costituisce l'insieme di dati
	 * 				su cui eseguire l'algoritmo.
	 * 
	 * @return Numero di iterazioni eseguite
	 * @throws OutOfRangeSampleSize
	 * 
	 * @see data.OutOfRangeSampleSize
	 * @see Cluster
	 * @see ClusterSet
	 */
	
	public int kmeans(Data data) throws OutOfRangeSampleSize{
		int numberOfIterations = 0;
		
		C.initializeCentroids(data);
		boolean changedCluster=false;
		do{
			numberOfIterations++;
			
			changedCluster = false;
			for(int i=0; i<data.getNumberOfExamples(); i++){
				Cluster nearestCluster = C.nearestCluster(data.getItemSet(i));
				Cluster oldCluster = C.currentCluster(i);
				boolean currentChange=nearestCluster.addData(i);
				if(currentChange)
					changedCluster=true;
				if(currentChange && oldCluster!=null)
					oldCluster.removeTuple(i);
			}
			
			C.updateCentroids(data);

		}while(changedCluster);
		
		return numberOfIterations;
	}
}
