package analysis;

import mining.ClusterSet;
import data.Attribute;
import data.Data;

/**
 * La classe &egrave una classe factory. In base al tipo di grafico si occupa di istanziare la 
 * classe correlata.
 * 
 * @author Luca Suriano
 */

public class GraphFactory {

	/**
	 * Il metodo si occupa di istanziare una classe che implementa l'intefaccia ChartI.
	 * <br>Nel caso specifico il parametro opt risulta essere utile in quanto, la richiesta
	 * di una lettura da file da parte di un client non inizializza l'oggetto data 
	 * (che risulter&agrave essere null), per questo motivo &egrave necessario che i
	 * metodi {@link ChartI#setGraph(ClusterSet, Attribute, Attribute, String)}
	 * non lo utilizzino.
	 *
	 * @param tipo Il tipo di grafico da istanziare
	 * @param opt L'operazione richiesta (lettura da file o scoperta da database)
	 * @param data La tabella data
	 * @return Un oggetto istanza di una classe che implementa l'interfaccia ChartI
	 * 
	 * @see data.Data
	 */
	public static ChartI creaGrafico(String tipo, String opt, Data data){
		ChartI mainChart = null;

		if(tipo.equals("scatter")){
			if(opt.equals("file"))
				mainChart = new ScatterGraph();
			else
				mainChart = new ScatterGraph(data);
		} else 

			mainChart = new PieGraph();


		return mainChart;
	}
}