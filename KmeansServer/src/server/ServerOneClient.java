package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

import mining.KmeansMiner;
import data.Data;
import data.OutOfRangeSampleSize;
import database.DatabaseConnectionException;
import database.EmptySetException;
import database.NoValueException;

import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;

import analysis.ChartData;
import analysis.ChartI;
import analysis.GraphFactory;

/**
 * La classe modella un Thread in grado di ascoltare le richieste da un singolo client.
 * E' istanziata da {@link MultiServer} non appena si verifica un connessione.
 * La classe stabilisce con il client un protocollo di comunicazione che si basa su uno scambio di
 * messaggi (oggetti istanza della classe String).<br>
 * Infatti per ogni operazione che va a buon fine la classe scrive sullo stream di output il messaggio
 * "OK" altrimenti un messaggio relativo all'eccezione.<br>
 * La classe si occupa di effettuare particolari operazioni in base alle richieste da parte del client.
 * Per la gestione delle richieste si veda il metodo {@link #run}
 * 
 * @author Luca Suriano
 */

class ServerOneClient extends Thread{

	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private KmeansMiner kmeans;
	private Data data;

	/**** IDENTIFICANO UN SINGOLO CLIENT ****/
	static private int cntClient = 0;
	int clientId;
	private ServerApp frame;
	private String operaz;
	private boolean flagOp = false; 

	
	/**
	 * Costruttore di classe. Inizializza gli attributi socket, in e out. Associa un 
	 * identificativo al client connesso e inizializza anche l'attributo {@link ServerApp}
	 * e avvia il thread.<br>
	 * Solleva un'eccezione di tipo IOException nel caso in cui risultano esserci dei problemi
	 * nell'ottenere i relativi stream di output e input.
	 * 
	 * @param s 		    Oggetto istanza della classe socket che modella la comunicazione 
	 * 					    con il client e permette di ottenere i relativi stream di output 
	 * 					    e input.
	 * @param app			Oggetto istanza della classe {@link ServerApp}
	 * 
	 * @throws IOException
	 * @see java.net.Socket
	 * @see java.net.OutputStream
	 * @see java.net.InputStream
	 */
	
	ServerOneClient(Socket s, ServerApp app) throws IOException{
		this.socket = s;
		this.in = new ObjectInputStream(socket.getInputStream());
		this.out = new ObjectOutputStream(socket.getOutputStream());
		this.clientId = ++cntClient;
		this.frame = app;

		app.clientConnect(this.clientId);
		super.start();
	}
	
	
	/**
	 * Sovrascrive il metodo run della classe Thread.
	 * Il metodo run si occupa di gestire le richieste da parte del client che sono suddivise in
	 * due categorie:
	 * - Una di scoperta dei cluster sul database
	 * - Una di lettura di un risultato precedente di scoperta da file.
	 * Nel primo caso il client invia il comando 0:<br>
	 * Il metodo letto tale comando effettuer&agrave la creazione dell'oggetto {@link data.Data} 
	 * e ci sar&agrave la modifica del flag {@link #flagOp} a true.<br>
	 * Successivamente il client invia il comando 1:<br>
	 * Il metodo letto tale comando effetuer&agrave la vera e prorpria
	 * attivit&agrave di scoperta che avviene tramite il metodo {@link mining.KmeansMiner#kmeans(Data)}
	 * chiamata in questo caso.
	 * Successivamente il client invia il comando 2:<br>
	 * in questo caso il metodo letto tale comando effetua il salvataggio della scoperta su file.
	 * Infine il client invia il comando 4:<br>
	 * il metodo ora letto tale comando, produrr&agrave il grafico in base alla selezione dei valori
	 * (riguardanti gli attributi e la tipologia di grafico) che avviene su client.<br>
	 * Nel caso una di queste operazioni non va a buon fine, il server si occuper&agrave
	 * di scrivere sullo stream di output il relativo messaggio di errore, viceversa per ogni
	 * operazione che va a buon fine, il server scrive sullo stream di output il messaggio
	 * "OK".<br><br>
	 * Nel caso il cui la richiesta &grave una lettura di un risultato precendente di scoperta su file
	 * , il client invia il comando 3. In questo caso verr&agrave inizializzato l'attributo {@link #kmeans}
	 * attraverso il costruttore {@link mining.KmeansMiner#KmeansMiner(String)}<br>
	 * Per ciascun comando verr&agrave aggiunta una nuova operazione all'attributo {@link #operaz} 
	 * e nel caso in cui non &egave stata effettuata alcuna operazione (e quindi il flag {@link #flagOp}
	 * risulter&agrave essere <i>false</i>) e il client si disconnette, allora l'attributo 
	 * conterr&agrave la stringa "Nessuna Operazione Effettuata".
	 */

	public void run(){
		String dbTable = "";
		int iterfromtable = 0;
		int numIter = 0;

		try {
			while(true){
				Object input = in.readObject();
				int sceltaClient = (Integer)input;

				switch(sceltaClient)
				{
				case 0:
					if(this.operaz == null)
						this.operaz = "Connessione al Database";
					else
						this.operaz = this.operaz + "Connessione al Database";
					dbTable = (String)in.readObject();
					try{
						this.data = new Data(dbTable, this.frame.getSelectedValues());
					} 
					catch(SQLException e){
						this.out.writeObject("Errore! - Nome Tabella Errato!");
						break;
					}
					catch (EmptySetException e) {
						this.out.writeObject(e.getMessage());
						break;
					}
					catch (DatabaseConnectionException e) {
						this.out.writeObject(e.getMessage());
						break;
					}
					catch (NoValueException e) {
						this.out.writeObject(e.getMessage());
						break;
					}

					this.flagOp = true;
					this.out.writeObject("OK");

					break;

				case 1:
					iterfromtable = (Integer)in.readObject();
					this.operaz = this.operaz + "-" + "Esecuzione KMeans (k = " + iterfromtable + ")";
					this.kmeans = new KmeansMiner(iterfromtable);

					try{
						numIter = this.kmeans.kmeans(this.data);
					}catch (OutOfRangeSampleSize e) {
						this.out.writeObject(e.getMessage());
						break;
					}

					this.out.writeObject("OK");
					this.out.writeObject(numIter);
					this.out.writeObject(kmeans.getC().toString(this.data));

					break;

				case 2:
					this.operaz = this.operaz + "-" + "Salvataggio File";
					try{
						this.kmeans.salva(dbTable + iterfromtable);
					}
					catch(IOException e){
						this.out.writeObject("Errore! - Salvataggio del file non riuscito!");
						break;
					}

					this.out.writeObject("OK");
					break;

				case 3:
					String filetableName = (String)this.in.readObject();
					String iterate = (String)in.readObject();

					if(this.operaz == null)
						this.operaz = "Esecuzione KMeans da File (k = " + iterate + ")";
					else
						this.operaz = this.operaz + "-" + "Esecuzione KMeans da File (k = " + iterate + ")";


					try{
						kmeans = new KmeansMiner(filetableName + iterate);
					}
					catch(FileNotFoundException e){
						this.out.writeObject("Errore! - File Non Trovato!");
						break;
					}
					
					this.out.writeObject("OK");
					this.out.writeObject(kmeans.getC().toString());

					break;

				case 4:
					String chartType = "";
					int attrX, attrY;
					ChartData chartData = null;
					String[] attributes = null;
					String action = (String)in.readObject();

					if(action.equals("file"))
						attributes = this.kmeans.kmAttributesString();
					else
						attributes = this.data.getAttributesString();

					this.out.writeObject(attributes);

					ArrayList values = (ArrayList)this.in.readObject();

					attrX = (Integer)values.get(0);
					attrY = (Integer)values.get(1);
					chartType = (String)values.get(2);

					ChartI mainChart =	GraphFactory.creaGrafico(chartType, action, this.data);

					if(action.equals("file"))
						chartData = mainChart.setGraph(kmeans.getC(), this.kmeans.getCAttrib(attrX), this.kmeans.getCAttrib(attrY), action);
					else	
						chartData = mainChart.setGraph(kmeans.getC(), this.data.getAttributeSchema(attrX), this.data.getAttributeSchema(attrY), action);

					this.operaz = this.operaz + "-" + "Visualizzazione Grafico Tipo( " + chartType + " )\n";

					this.out.writeObject("OK");
					if(chartType.equals("scatter")){
						XYDataset mainDataset = (XYDataset)chartData.getDataset();
						this.out.writeObject(mainDataset);
						this.out.writeObject(chartData.getLabelX());
						this.out.writeObject(chartData.getLabelY());
						this.out.writeObject(DatasetUtilities.findMaximumDomainValue(mainDataset));
						this.out.writeObject(DatasetUtilities.findMaximumRangeValue(mainDataset));
					} else{
						this.out.writeObject(chartData.getDataset());
						if(data != null)
							this.out.writeObject(this.data.getNumberOfExamples());
						else
							this.out.writeObject(this.kmeans.getC().getLen());
					}

					this.frame.setOp(this.clientId, this.operaz);

					break;

				}
			}
		} catch (IOException e) {
			if(!this.flagOp){
				this.frame.setOp(this.clientId, "Nessuna Operazione Effettuata\n");
			}
			this.frame.clientDisconnect(this.clientId);
		} catch (ClassNotFoundException e) {
			this.frame.setOutputText(e.getMessage());
		} finally{
			try {
				this.socket.close();
			} catch (IOException e) {
				this.frame.setOutputText(e.getMessage());
			}
		}	
	}
}