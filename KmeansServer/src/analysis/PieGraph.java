package analysis;

import mining.ClusterSet;
import data.Attribute;
import org.jfree.data.general.DefaultPieDataset;

/**
 * La classe si occupa di modellare i dati relativi ad un grafico a torta.
 * Il metodo {@link ChartI#setGraph(ClusterSet, Attribute, Attribute, String)} implementato nella
 * classe si occupa di istanziare l'ogetto della classe {@link ChartData}, relativo ai dati
 * utili per un grafico a torta. La carattestica principale &egrave l'utilizzo di un
 * {@link org.jfree.data.general.DefaultPieDataset} che modella appunto un dataset per un grafico
 * a torta, contenuto nell'oggetto restituito dal metodo.
 * 
 * @author Luca Suriano
 * @see ChartData
 * @see ChartData#dataset
 * @see org.jfree.data.general.DefaultPieDataset
 */

public class PieGraph implements ChartI{

	/**
	 * Metodo che si occupa di calcolare la percentuale. Ritorna il valore in percentuale
	 * che val rappresenta rispetto alla totalit&agrave dei valori tot.
	 *
	 * @param val Valore di cui calcolare la percentuale
	 * @param tot Totalit&agrave dei valori
	 * 
	 * @return La percentuale calcolata di val rispetto a tot
	 */
	private double getPerc(double val, double tot){
		return (100*val)/tot;
	}

	/**
	 * Il metodo si occupa di ottenere la totalit&agrave dei valori presenti nel ClusterSet.
	 * Il metodo esegue una sommatoria di tutti i dati presenti in ciascun Cluster
	 *
	 * @param data ClusterSet di cui effettuare il calcolo
	 * @return Totalit&agrave dei valori presenti nel ClusterSet
	 * 
	 * @see mining.Cluster
	 * @see mining.Cluster#getClusterSize()
	 * @see mining.ClusterSet
	 */
	private double getTot(ClusterSet data){
		double tot = 0;

		for(int i = 0; i < data.getLen(); i++){
			tot += data.get(i).getClusterSize();
		}

		return tot;
	}

	/**
	 * Il metodo si occupa di creare l'oggeto ChartData. L'obiettivo principale della creazione
	 * di questo oggetto,  dovuto al fatto che le sue componenti saranno inviate poi al client
	 * per la corretta visualizzazione del grafico a torta.<br>
	 * Il metodo principalmente si occupa di calcolare la percentuale che ciascun cluster
	 * , contenuto all'interno del {@link mining.ClusterSet} data, rappresenta (attraverso il 
	 * metodo {@link #getPerc} e aggiungere tale valore al dataset 
	 * ({@link org.jfree.data.general.DefaultPieDataset}) che verr&agrave utilizzato dal client 
	 * per la visualizzazione del grafico. Il dataset creato viene utilizzato poi dall'oggetto 
	 * ChartData che verr&agrave restituito dal metodo.
	 * 
	 * @see ChartData
	 * @see mining.ClusterSet
	 * @see #getPerc
	 * @see org.jfree.data.general.DefaultPieDataset
	 * @see org.jfree.data.general.DefaultPieDataset#setValue(Comparable, double)
	 * 
	 */
	public ChartData setGraph(ClusterSet data, Attribute varX, Attribute varY, String action) {

		ChartData mainData = null;

		DefaultPieDataset dataset = new DefaultPieDataset();


		double tot = this.getTot(data);

		for(int i = 0; i < data.getLen(); i++){
			double clPerc = getPerc(data.get(i).getClusterSize(), tot);
			dataset.setValue("Cluster " + i, clPerc);
		}

		mainData = new ChartData(dataset);

		return mainData;
	}
}