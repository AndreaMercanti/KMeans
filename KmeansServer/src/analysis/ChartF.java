package analysis;

import java.util.ArrayList;
import java.util.List;

import data.Data;
import data.Attribute;
import data.Tuple;
import data.DiscreteAttribute;

import mining.ClusterSet;

/**
 * La classe si occupa di fornire un insieme di funzionalit&agrave utili per la creazione dei
 * grafici. Essa infatti contiene un insieme di metodi statici che verranno richiamati quando
 * necessario dalle classi {@link PieGraph} e {@link ScatterGraph} per ottenere tutti i dati
 * necessari (ovvero un oggetto istanza della classe {@link ChartData})da restituire al client 
 * che andr&agrave poi a visualizzare il grafico correttamente.
 * 
 * @author Luca Suriano
 * @see PieGraph
 * @see ScatterGraph
 * @see ChartData
 */
class ChartF {

	/**
	 * Il metodo di classe analizza la tupla passata come parametro considerando un suo valore
	 * all'indice dato dall'attributo specificato come parametro. Tale valore poi viene
	 * confrontato con quelli presenti nell'attributo restituendo un intero.
	 * Esempio:
	 * Tupla - (sunny, 20.5, high, weak, no)
	 * L'attributo specifcato come parametro ha indice 0 e possiede i valori (cloudy, mixed,
	 * overcast, sunny). essendo sunny = all'ultimo dei valori viene restituito l'intero 3.
	 * Se fosse stato uguale a overcast 2, a mixed 1 e a cloudy 0.<br>
	 * <u>Tale funzione &egrave utilizzata nel caso di un attributo discreto.</u>
	 *
	 * @param dataTup the data tup
	 * @param varAxe the var axe
	 * @return the int
	 */
	private static int checkVal(Tuple dataTup, Attribute varAxe){
		Object val = dataTup.get(varAxe.getIndex()).getValue();
		int grData = 0;

		for(String item : (DiscreteAttribute)varAxe){
			if(item.equals(val)){
				break;
			}else{
				grData++;
			}
		}

		return grData;
	}

	/**
	 * Il metodo si occupa di creare la lista contenente i valori per un asse in base
	 * ai parametri passati (nel caso di un attributo discreto).<br> 
	 * Il metodo principalmente considera ciascuna tupla (ricavandole utilizzando il metodo
	 * {@link data.Data#getItemSet(int)}), passando ognuna di queste al metodo 
	 * {@link #checkVal(Tuple, Attribute)} che si occuper&agrave
	 * di restituire il valore corretto da inserire all'interno della lista.
	 *
	 * @param data La tabella {@link data.Data} da considerare
	 * @param varAxe L'attributo da considerare per l'ottenimento dei valori
	 * @return La lista contenente i valori per l'asse relativi all'attributo specificato
	 */
	static List<Integer> getAxisDisValues(Data data, Attribute varAxe)
	{
		int len = data.getNumberOfExamples();
		List<Integer> axeValues = new ArrayList<Integer>();

		for(int i = 0; i < len; i++){
			Tuple dataTup = data.getItemSet(i);
			axeValues.add(checkVal(dataTup, varAxe));
		}

		return axeValues;
	}

	/**
	 * Il metodo si occupa di creare la lista contenente i valori per un asse in base
	 * ai parametri passati (nel caso di un attributo discreto).<br>
	 * Il metodo principalmente considera ciascun centroide appartenente ai Cluster 
	 * del ClusterSet specificato (ricavandoli utilizzando il metodo
	 * {@link mining.Cluster#getCentroid()})<br>
	 * Essendo il Centroide un tupla ({@link mining.Cluster}), tale tupla viene passata 
	 * al metodo {@link #checkVal(Tuple, Attribute)} che si occuper&agrave di restituire 
	 * il valore corretto da inserire all'interno della lista.
	 *
	 * @param data Il {@link mining.ClusterSet} da considerare
	 * @param varAxe L'attributo da considerare per l'ottenimento dei valori
	 * @return La lista contenente i valori per l'asse relativi all'attributo specificato
	 */
	static List<Integer> getCentDisValues(ClusterSet data, Attribute varAxe){
		List<Integer> axeValues = new ArrayList<Integer>();
		int len = data.getLen();

		for(int i = 0; i < len; i++){
			Tuple dataTup = data.get(i).getCentroid();
			axeValues.add(checkVal(dataTup, varAxe));
		}

		return axeValues;
	}

	/**
	 * Il metodo si occupa di creare la lista contenente i valori per un asse in base
	 * ai parametri passati (nel caso di un attributo continuo).<br>
	 * Il metodo principalmente considera ciascuna tupla (ricavandole attraverso il metodo
	 * {@link data.Data#getItemSet(int)}) e per ciascuna di queste considera l'attributo
	 * specificato ricavandone il valore che questo assume nella tabella (utilizzando il
	 * metodo {@link data.Item#getValue()}).
	 * I valori ottenuti vengono cos&igrave aggiunti alla lista da restituire dal metodo.
	 *
	 * @param data Il {@link mining.ClusterSet} da considerare
	 * @param varAxe L'attributo da considerare per l'ottenimento dei valori
	 * @return La lista contenente i valori per l'asse relativi all'attributo specificato
	 */
	
	static List<Double> getAxisCntValues(Data data, Attribute varAxe){
		List<Double> contValues = new ArrayList<Double>();
		int len = data.getNumberOfExamples();


		for(int i = 0; i < len; i++)
		{
			Tuple dataTup = data.getItemSet(i);
			Object val = dataTup.get(varAxe.getIndex()).getValue();
			contValues.add((Double)val);
		}

		return contValues;
	}

	/**
	 * Il metodo si occupa di creare la lista contenente i valori per un asse in base
	 * ai parametri passati (nel caso di un attributo continuo).<br>
	 * Il metodo principalmente considera ciascun centroide appartenente ai Cluster 
	 * del ClusterSet specificato (ricavandoli utilizzando il metodo
	 * {@link mining.Cluster#getCentroid()}).<br>
	 * Essendo il Centroide un tupla ({@link mining.Cluster}), per ciascuna di queste considera 
	 * l'attributo specificato ricavandone il valore che questo assume (utilizzando il
	 * metodo {@link data.Item#getValue()}). Tale valore viene infine aggiunto alla
	 * lista da restituire.
	 *
	 * @param data Il {@link mining.ClusterSet} da considerare
	 * @param varAxe L'attributo da considerare per l'ottenimento dei valori
	 * @return La lista contenente i valori per l'asse relativi all'attributo specificato
	 */
	static List<Double> getCentContValues(ClusterSet data, Attribute varAxe){
		List<Double> contValues = new ArrayList<Double>();
		int len = data.getLen();


		for(int i = 0; i < len; i++){
			Tuple dataTup = data.get(i).getCentroid();
			Object val = dataTup.get(varAxe.getIndex()).getValue();
			contValues.add((Double)val);
		}

		return contValues;
	}

	/**
	 * Il metodo di classe restituisce una stringa utile per la creazione dell'etichetta
	 * per un asse. La stringa restituita dal metodo sar&agrave cos&igrave formata:<br>
	 * (Nome_Attributo) : 0 Valore_Attribute 1 Valore_Attribute ...
	 * 
	 * @param attr L'attributo da considerare
	 * @return La stringa rappresentante un etichetta.
	 */
	static String getAxeLabels(Attribute attr){
		int i = 0;
		String xValues = "(" + attr.getName() + ") ";


		for(String item : (DiscreteAttribute)attr){
			xValues = xValues + i + ": " + item + " ";
			i++;
		}


		return xValues;
	}

}