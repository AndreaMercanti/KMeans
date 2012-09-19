package analysis;

import data.Attribute;
import mining.ClusterSet;

/**
 * L'interfaccia rappresenta un determinato tipo di dati per la creazione di un grafico.
 * Il metodo definito nell'interfaccia si occuper&agrave semplicemente di fornire tali dati
 * relativi ad un determinato tipo di grafico, ma la creazione effettiva dello stesso avverr&agrave 
 * sul client.<br>
 * Su server quindi, avremo solo la creazione dei dati relativi al grafico, tali dati saranno 
 * inviati al client che sar&agrave responsabile di visualizzare il grafico.
 * 
 * @author Luca Suriano
 * 
 */
public interface ChartI {
	
	/**
	 * Il metodo si occupa di creare un oggetto istanza della classe {@link ChartData},
	 * che conterr&agrave tutti i dati necessari per la definizione di un grafico.<br>
	 * Il metodo infatti &egrave responsabile di creare quell'oggetto i cui membri
	 * verranno poi inviati al client.
	 * 
	 * @param data 		Il ClusterSet da considerare per la creazione dei dati relativi
	 * 					al grafico
	 * @param varX 		L'attributo per l'asseX
	 * @param varY 		L'attributo per l'asseY
	 * @param action 	La stringa rappresenta se il client ha richiesto un'operazione di
	 * 					scoperta dei cluster dalla base di dati, oppure se ha richiesto 
	 * 					un'operazione di lettura da file.
	 * 
	 * @return L'oggetto istanza della classe {@link ChartData} che contiene tutti i dati
	 * 		   necessari per la creazione di un grafico. 
	 * 
	 * @see ChartData
	 */
	ChartData setGraph(ClusterSet data, Attribute varX, Attribute varY, String action);
}