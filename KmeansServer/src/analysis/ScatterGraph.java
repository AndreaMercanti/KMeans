package analysis;

import java.util.List;

import mining.ClusterSet;

import data.Data;
import data.Attribute;
import data.DiscreteAttribute;
import data.ContinuousAttribute;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * La classe si occupa di modellare i dati relativi ad un grafico ad assi cartesiani.
 * Il metodo {@link ChartI#setGraph(ClusterSet, Attribute, Attribute, String)} implementato nella
 * classe si occupa di istanziare l'ogetto della classe {@link ChartData}, relativo ai dati
 * utili per un grafico ad assi. La carattestica principale &egrave l'utilizzo di un
 * {@link org.jfree.data.xy.XYSeriesCollection} che modella appunto un dataset per un grafico
 * ad, contenuto nell'oggetto restituito dal metodo. Il grafico ad assi si basa inoltre sugli
 * attributi scelti dal client e letti dal server grazie al sistema di stream I/O
 * 
 * @author Luca Suriano
 * 
 * @see ChartData
 * @see ChartData#dataset
 * @see org.jfree.data.xy.XYSeriesCollection
 */

public class ScatterGraph implements ChartI {

	/**
	 * La tabella Data
	 * 
	 * @see data.Data
	 */
	private Data table;
	
	/** 
	 * Lista completa per i valori relativi all'asse x
	 * 
	 * @see java.util.List
	 */
	private List xValues;
	
	/**
	 * Lista completa per i valori relativi all'asse y
	 * 
	 *  @see java.util.List
	 */
	private List yValues;
	
	/** 
	 * Etichetta relativa ad un asse 
	 */
	private String labels;

	/**
	 * Il costruttore si occupa di istanziare la classe, inizializzando l'oggetto
	 * istanza della classe Data {@link #table} grazie al parametro passato.
	 * Il costruttore &grave chiamato solo nel caso in cui il client decide di effettuare
	 * un'attivita di scoperta dalla base di dati.
	 *
	 * @param tab Riferimento ad un oggetto istanza della classe {@link data.Data}
	 * @see data.Data
	 */
	ScatterGraph(Data tab){
		this.table = tab;
		this.labels = "";
	}

	/**
	 * Il costruttore a zero argomenti verr&agrave richiamato nel caso in cui il client
	 * decida di effettuare una richiesta di lettura da file relativa ad attivit&agrave 
	 * precedenti di scoperta.
	 */
	ScatterGraph(){
		this.labels = "";
	}

	/**
	 * Il metodo di supporto per la classe, si occupa di creare un oggetto istanza della
	 * classe {@link org.jfree.data.xy.XYSeries}. Tale oggetto &grave la componente principale
	 * del dataset {@link org.jfree.data.xy.XYSeriesCollection} e descrive un un'unica componente
	 * del dataset. Il metodo principalmente considera i parametri xValues e yValues, che
	 * rappresentano i dati relativi ad un asse per un solo Cluster, e aggiunge ciascuno
	 * di questa ad una {@link org.jfree.data.xy.XYSeries} assegnadogli un nome.
	 * Tale nome verr&agrave poi visualizzato nella legenda del grafico lato client.
	 *
	 * @param xValues I valori relativi all'asse x per un Cluster
	 * @param yValues I valori relativi all'asse y per un Cluster
	 * @param name Il nome da assegnare alla {@link org.jfree.data.xy.XYSeries}
	 * @return La {@link org.jfree.data.xy.XYSeries} contenente i valori relativi al Cluster
	 * @see mining.Cluster
	 * @see analysis.ClusterData
	 */
	private XYSeries createSeries(ClusterData xValues, ClusterData yValues, String name) {
		XYSeries clustSer = new XYSeries(name);
		
		for(int i = 0; i < xValues.getListSize(); i++)
			clustSer.add(xValues.getCValue(i), yValues.getCValue(i));

		return clustSer;
	}
	
	/**
	 * Il metodo si occupa di richiamare il metodo appropriato il base al tipo
	 * di Attribute passato come parametro.<br>
	 * Principalmente il metodo si occupa di creare l'etichetta per il grafico
	 * richiamando il metodo {@link ChartF#getAxeLabels(Attribute)} e passare il risultato
	 * al membro {@link #labels}. In seguito richiamare il metodo 
	 * {@link ScatterGraph#getCentList(DiscreteAttribute, ClusterSet)} oppure 
	 * {@link ScatterGraph#getCentList(ContinuousAttribute, ClusterSet)} in base
	 * al tipo di Attribute specificato come parametro.<br>
	 * La presenza del metodo &egrave dovuta al fatto che se il client decide di effettuare
	 * un'operazione di lettura da file a quel punto la tabella {@link #table} non
	 * sar&agrave disponibile, quindi gli unici dati per creare la lista dei valori
	 * per l'asse sono nel ClusterSet specificato come parametro. Tale operazione
	 * sar&agrave effettuata dai metodi che vengono richiamati all'interno del metodo.
	 * 
	 * @param attr L'attributo per cui effetuare le operazioni
	 * @param data Il ClusterSet da analizzare.
	 * 
	 * @return La lista contenente i valori per l'asse in base all'attributo e al
	 * 		   ClusterSet specificati.
	 * 
	 * @see data.Attribute
	 * @see mining.ClusterSet
	 */
	private List getCentList(Attribute attr, ClusterSet data){
		List retList;
		if(attr instanceof DiscreteAttribute){
			this.labels = ChartF.getAxeLabels(attr);
			retList = getCentList((DiscreteAttribute)attr, data);
		} else{
			this.labels = attr.getName();
			retList = getCentList((ContinuousAttribute)attr, data);
		}
		
		return retList;
			
	}
	
	/**
	 * Il metodo si occupa semplicemente di richiamare il metodo 
	 * {@link ChartF#getCentDisValues(ClusterSet, Attribute)} 
	 * per ottenere la lista dei valori per l'asse relativa all'attributo
	 * specificato.
	 *
	 * @param attr L'attributo discreto di cui effettuare l'analisi
	 * @param data Il ClusterSet da considerare per l'analisi
	 * @return La lista dei valori per l'asse relativa ai parametri specificati
	 */
	private List<Integer> getCentList(DiscreteAttribute attr, ClusterSet data){
		return ChartF.getCentDisValues(data, attr);
	}
	
	/**
	 * Il metodo si occupa semplicemente di richiamare il metodo 
	 * {@link ChartF#getCentContValues(ClusterSet, Attribute)} 
	 * per ottenere la lista dei valori per l'asse relativa all'attributo
	 * specificato.
	 *
	 * @param attr L'attributo continuo di cui effettuare l'analisi
	 * @param data Il ClusterSet da considerare per l'analisi
	 * @return La lista dei valori per l'asse relativa ai parametri specificati
	 */
	
	private List<Double> getCentList(ContinuousAttribute attr, ClusterSet data){
		return ChartF.getCentContValues(data, attr);
	}
	
	/**
	 * Il metodo si occupa di richiamare il metodo appropriato il base al tipo
	 * di Attribute passato come parametro.<br>
	 * Principalmente il metodo si occupa di creare l'etichetta per il grafico
	 * richiamando il metodo {@link ChartF#getAxeLabels(Attribute)} e passare il risultato
	 * al membro {@link #labels}. In serguito richiamare il metodo 
	 * {@link #getDisList(DiscreteAttribute)} oppure 
	 * {@link #getContList(ContinuousAttribute)} in base
	 * al tipo di Attribute specificato come parametro.
	 * 
	 * @param attr L'attributo per cui effetuare le operazioni
	 * 
	 * @return La lista contenente i valori per l'asse in base all'attributo
	 * 		   specificato.
	 * 
	 * @see data.Attribute
	 */
	
	private List getList(Attribute attr){
		List retList;
		
		if(attr instanceof DiscreteAttribute){
			this.labels = ChartF.getAxeLabels(attr);
			retList = getDisList((DiscreteAttribute)attr);
		} else {
			this.labels = attr.getName();
			retList = getContList((ContinuousAttribute)attr);
		}
		
		return retList;
	}
	
	/**
	 * Il metodo si occupa semplicemente di richiamare il metodo 
	 * {@link ChartF#getAxisDisValues(Data, Attribute)} 
	 * per ottenere la lista dei valori per l'asse relativa all'attributo
	 * specificato.
	 *
	 * @param attr L'attributo discreto di cui effettuare l'analisi
	 * 
	 * @return La lista dei valori per l'asse.
	 */
	private List<Integer> getDisList(DiscreteAttribute attr){
		return ChartF.getAxisDisValues(this.table, attr);
	}
	
	/**
	 * Il metodo si occupa semplicemente di richiamare il metodo 
	 * {@link ChartF#getAxisCntValues(Data, Attribute)} 
	 * per ottenere la lista dei valori per l'asse relativa all'attributo
	 * specificato.
	 *
	 * @param attr L'attributo continuo di cui effettuare l'analisi
	 * 
	 * @return La lista dei valori per l'asse.
	 */
	private List<Double> getContList(ContinuousAttribute attr){
		return ChartF.getAxisCntValues(this.table, attr);
	}

	/**
	 * Il metodo si occupa di creare l'insieme di dati (e quindi restituire l'oggetto istanza
	 * della classe {@link ChartData}) relativi ad un grafico ad assi cartesiani.<br>
	 * Il metodo verifica anzitutto che operazione viene richiesta. Quindi se il parametro
	 * action riguarda un'operazione di lettura da file oppure di scoperta dei cluster da
	 * database.<br>
	 * Nel primo caso non essendo disponibile la tabella {@link #table}, &egrave
	 * necessario richiamare il metodo {@link #getCentList(Attribute, ClusterSet)} e successivamente
	 * creare una {@link org.jfree.data.xy.XYSeries} per ogni elemento del ClusterSet
	 * contenente i valori per l'asse x e per l'asse y, presenti nelle liste ottenute dalle
	 * chiamate al metodo {@link #getCentList(Attribute, ClusterSet)}. Infine viene creato l'oggetto
	 * istanza della classe {@link ChartData} che verr&agrave restitutito.<br>
	 * Nel secondo caso essendo disponibile la tabella {@link #table} verr&agrave richiamato
	 * il metodo {@link #getList(Attribute)} ritornando prima la lista completa dell'asse x e
	 * infine la lista completa dell'asse y e verranno create le etichette. La lista creata,
	 * viene utilizzata da un oggetto istanza della classe {@link ClusterData} che si
	 * occuper&agrave di organizzarla in base al cluster. Il cluster passato a tale oggetto
	 * sar&agrave letto in base al parametro data passato al metodo. Infine viene creata una 
	 * {@link org.jfree.data.xy.XYSeries} utilizzando il metodo 
	 * {@link #createSeries(ClusterData, ClusterData, String)} relativa ad un unico cluster.<br>
	 * Tale processo viene ripetututo per ogni cluster presente all'interno
	 * del clusterset e ogni {@link org.jfree.data.xy.XYSeries} creata viene aggiunta al dataset
	 * di tipo {@link org.jfree.data.xy.XYSeriesCollection} che verr&agrave utilizzato
	 * per istanziare l'oggetto di classe {@link ChartData} da restituire.<br>
	 * Gli attributi specificati come parametri sono passati dal client che li specifica
	 * attraverso una finestra di dialogo modale.
	 * 
	 * @param data Il ClusterSet da considerare.
	 * @param varX L'attributo da considerare per l'asseX
	 * @param varY L'attributo da considerare per l'asseY
	 * @param action La richiesta che il client ha effettuato (lettura da file o scoperta da 
	 * 				 database).
	 * 
	 * @see mining.ClusterSet
	 * @see data.Attribute
	 * @see data.Data
	 * @see org.jfree.data.xy.XYSeries
	 * @see org.jfree.data.xy.XYSeriesCollection
	 * @see ChartData
	 */
	public ChartData setGraph(ClusterSet data, Attribute varX, Attribute varY, String action) {
		ClusterData xCData, yCData;
		ChartData mainData = null;
		String yLabel = "", xLabel = "";

		XYSeriesCollection dataset = new XYSeriesCollection();
		
		if(action.equals("file")){
			XYSeries xySer = null;
			
			this.xValues = this.getCentList(varX, data);
			xLabel = this.labels;
			this.yValues = this.getCentList(varY, data);
			yLabel = this.labels;
			
			for(int j = 0; j < data.getLen(); j++){
				xySer = new XYSeries("Cluster" + j);
				xySer.add((Number)xValues.get(j), (Number)yValues.get(j));
				dataset.addSeries(xySer);
			}

			mainData = new ChartData(dataset, xLabel, yLabel);

		} else{
			
			this.xValues = this.getList(varX);
			xLabel = this.labels;
			this.yValues = this.getList(varY);
			yLabel = this.labels;
			
			
			for(int i = 0; i < data.getLen(); i++){
				xCData = new ClusterData(xValues, data.get(i));
				yCData = new ClusterData(yValues, data.get(i));
				dataset.addSeries(this.createSeries(xCData, yCData, "Cluster: " + i + " - Centroid: " + i + "    "));
			}

			mainData = new ChartData(dataset, xLabel, yLabel);
			
		}
		
		return mainData;
	}
}