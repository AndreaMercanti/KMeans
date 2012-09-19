package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import database.DBAccess;
import database.DatabaseConnectionException;

import java.util.List;
import java.util.ArrayList;

/**
 * La classe modella una GUI per il server. In particolare essa rappresenta un {@link javax.swing.JFrame}
 * che mostra le diverse operazioni che il client effettuano durante il periodo 
 * connessione - disconnesione.<br>
 * La finestra possede un'area di testo non editabile in cui vengono inserite tutte le operazioni
 * che vengono richieste al server man mano che si verificano.<br>
 * Una seconda area di testo non editabile mostra le operazioni per i songoli client selezionabili
 * attraverso una {@link javax.swing.JComboBox}.<br>
 * Ogni volta che un client si connette al server viene aggiunto un nuovo elemento alla JComboBox 
 * in modo tale da permettere poi la successiva selezione per mostrare le operazioni per quel 
 * singolo client. La selezione pu&ograve avvenire nel caso in cui si connettono almeno 2 client,
 * altrimenti c'&egrave a disposizione l'area di testo "globale" che contiene tutte le operazioni
 * effettuate da quel singolo client.<br>
 * L'avvio della GUI comporter&agrave la visualizzazione di una finestra modale per l'inserimento dei
 * dati relativi alla base di dati. Se i dati inseriti non sono corretti, e quindi non &egrave possibile
 * connettersi al database, per assicurare un utilizzo corretto del sistema da parte dei client, sar&agrave
 * necessario reinserire i dati.
 * 
 * @author Luca Suriano
 *
 */

public class ServerApp extends JFrame{
	/**
	 * Area di testo non editabile contenente tutte le operazioni che avvengono
	 * sul server da qualsiasi client
	 * 
	 * @see javax.swing.JTextArea
	 */
	JTextArea serverOutput = new JTextArea(20, 17);
	
	/**
	 * Area di testo non editabile che contiene tutte le operazioni effettuate da un
	 * singolo client
	 * 
	 * @see javax.swing.JTextArea
	 */
	private JTextArea clientInfo = new JTextArea(20, 25);
	
	/**
	 * Scroll per entrambe le due aree di testo
	 * 
	 * @see javax.swing.JScrollPane
	 */
	private JScrollPane serverScroll;
	
	/**
	 * JComboBox per la selezione delle operazioni relative ad un unico client
	 * 
	 * @see javax.swing.JComboBox
	 */
	private JComboBox comboClient = new JComboBox();
	
	/**
	 * ArrayList contenente tutte le opreazioni effettuate dai client.
	 * L'ArrayList &grave cos&igrave gestito:
	 * Ciascun indice dell'ArrayList equivale all'identificativo del client.
	 * L'identificativo del client &egrave creato dalla classe {@link ServerOneClient} e 
	 * rappresenta un numero intero progressivo.
	 */
	private List<String> operaz = new ArrayList<String>();
		
	/**
	 * Finestra modale per l'inserimento dei dati per permettere l'utilizzo del database 
	 */
	private DatabaseDialog dbDialog;
	
	/**
	 * Dati inseriti all'interno della finestra modale {@link #dbDialog}
	 */
	private List<String> dialogData;
	
	/**
	 * Flag booleano che indica se non &egrave stata effettuta ancora alcuna operazione.
	 * Il flag &egrave utilizzato all'interno dell'ascoltatore associato alla {@link #comboClient}
	 * in modo tale da evitare che vengano richiamate le funzioni al suo interno, non appena
	 * la JComboBox viene creata. Una volta che almeno un'operazione &egrave stata effettuata
	 * il flag diventa false e i metodi presenti nell'ascoltatore possono essere chiamati.
	 */
	boolean start = true;
	
	
	/**
	 * Costruttore della classe che si occupa di assegnare alla finestra un titolo
	 * e di inizializzare il contenuto del JFrame
	 * 
	 * @param nome Titolo della finestra.
	 */
	
	ServerApp(String nome){
		this.setTitle(nome);
		initApp();
	}
	
	/**
	 * Metodo privato richiamato dal costruttore che si occuper&agrave della creazione
	 * della finestra.
	 * Esso assegna alla finestra una dimensione, la rende non ridimensionabile e inizializza
	 * la finestra modale relativa all'inserimento delle informazioni del database.
	 * <p>
	 * La finestra &egrave suddivisa in due parti:
	 * <br>
	 * Nella parte sinistra ci sar&agrave l'area di testo {@link #serverOutput} con le operazioni
	 * effettuate da tutti i client.
	 * <br>
	 * Nella parte destra ci sar&agrave l'area di testo {@link #clientInfo} con le operazioni
	 * effettuate da un singolo client selezionato attraverso la JComboBox {@link #comboClient}.
	 * Il metodo si occuper&agrave inoltre di avviare il server richiamando il metodo
	 * {@link #startServer()}
	 */
	
	private void initApp(){
		this.setSize(new Dimension(740, 550));
		this.getContentPane().setLayout(new FlowLayout());
		this.setResizable(false);
		this.dbDialog = new DatabaseDialog(this, "Impostazioni Database");
		
		
		serverOutput.setEditable(false);
		serverOutput.setLineWrap(true);
		serverOutput.setMargin(new Insets(10,10,10,10));

		JPanel mainLeft = new JPanel();
		JPanel mainRight = new JPanel();


		mainLeft.setPreferredSize(new Dimension(400, 500));
		mainLeft.setLayout(new BorderLayout());
		mainLeft.add(new JLabel("Operazioni dei Clients connessi"), BorderLayout.NORTH);
		this.serverScroll = new JScrollPane(serverOutput);
		mainLeft.add(this.serverScroll, BorderLayout.CENTER);

		mainRight.setLayout(new BorderLayout());		
		JPanel panClients = new JPanel();
		panClients.setLayout(new BoxLayout(panClients, BoxLayout.PAGE_AXIS));
		panClients.add(new JLabel("Seleziona Client:"));
		this.comboClient.setEnabled(false);
		panClients.add(this.comboClient);
		mainRight.add(panClients, BorderLayout.NORTH);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				int scelta = JOptionPane.showConfirmDialog(null, "Sicuro di uscire?\nL'uscita comporterˆ " +
						"la chiusura del server", "KMeans", JOptionPane.YES_NO_OPTION);
				if(scelta == JOptionPane.YES_OPTION)
					System.exit(0);
			}
		});
		
		this.comboClient.addItemListener(new ItemListener(){

			public void itemStateChanged(ItemEvent e) {
				if(start == false){
					if (e.getStateChange() == ItemEvent.SELECTED) {
						getClientActions();
					}
				}
			}

		});

		this.serverScroll = new JScrollPane(clientInfo);
		mainRight.add(serverScroll, BorderLayout.SOUTH);


		add(mainLeft);
		add(mainRight);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		startServer();
	}
	
	/**
	 * Metodo privato che istanziare un oggetto della classte {@link MultiServer} passandogli la
	 * porta (8080) su cui sar&agrave in ascolto e il riferimento a questa finestra.
	 */
	
	private void startServer(){
		MultiServer mserver = new MultiServer(8080, this);
	}
	
	/**
	 * Metodo che si occupa di rendere visibile la finestra modale relativa all'inserimento dei
	 * dati per permettere l'utilizzo del database. Una volta che l'inserimento &egrave avvenuto,
	 * viene effettuato un controllo per la verifica che i dati inseriti siano corretti.
	 * Il metodo infatti richiama il metodo {@link ServerApp#testDB()} che effettua un test di
	 * connessione sul database, per assicurare che i dati inseriti siano corretti.
	 * Nel caso in cui il test di connessione al database va a buon fine, vengono salvati i dati 
	 * (inseriti in {@link #dialogData}), la finestra di dialogo viene chiusa e le risorse associate 
	 * alla finestra vengono distrutte.<br>
	 * Altrimenti la finestra di dialogo per l'inserimento dei dati del dabase verr&agrave visualizzata
	 * nuovamente.
	 */
	
	void setDB(){
		boolean dbFlag = false;
		while(!dbFlag){
			this.dbDialog.setVisible(true);
			this.dialogData = dbDialog.getselectedValues();
			dbFlag = testDB();
			if(!dbFlag){
				this.dbDialog.resetFields();
			}
		}
		JOptionPane.showMessageDialog(this, "Connessione al database avvenuta correttamente!");
		this.dbDialog.dispose();
	}
	
	/**
	 * Il Metodo si occupa di effettuare un test di connessione sulla base di dati. Questo per assicurare
	 * un utilizzo corretto del sistema, in modo tale da permettere al client di poter effettuare operazioni
	 * di scoperta sulla base di dati senza ricevere errori.<br>
	 * Il metodo richiama {@link database.DBAccess#initConnection()} che effettua una connessione al database.
	 * Se la connessione va a buon fine il metodo ritorna <b>true</b> altrimenti ritorna <b>false</b>, e la 
	 * finestra di dialogo per l'inserimento dei dati del database verr&agrave visualizzata nuovamente.
	 * 
	 * @return <b>true</b> se il test va a buon fine <b>false</b> altrimenti
	 */
	
	boolean testDB(){
		boolean res = false;
		
		DBAccess mainDB = new DBAccess(this.dialogData);
		try {
			mainDB.initConnection();
			res = true;
		} catch (DatabaseConnectionException e) {
			JOptionPane.showMessageDialog(this, e.getMessage() + "\nRiprova!");
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
		
		return res;

	}
	
	/**
	 * Metodo che si occupa di aggiungere alla JComboBox {@link #comboClient} l'id del client
	 * appena connesso mostra un messaggio di avvenuta connessione nella JTextArea
	 * {@link #serverOutput}.
	 */

	void clientConnect(int id) {
		this.comboClient.addItem(id);
		this.comboClient.setEnabled(true);
		String servOut = this.serverOutput.getText();
		this.setOutputText("\n----- Connesso - Client id: " + id);
	}
	
	/**
	 * Metodo che si occupa di mostrare un messaggio di avvenuta disconnessione nella JTextArea
	 * {@link #serverOutput} da parte del client identificato da id
	 * 
	 * @param id Identificativo del client
	 */

	void clientDisconnect(int id){
		String servOut = this.serverOutput.getText();
		this.setOutputText("----- Disconnesso - Client id: " + id + "\n");
	}
	
	/**
	 * Tale metodo si occupa di aggiungere l'operazione (op) effettuata dal client identificato
	 * dal paramentro id all'ArrayList {@link #operaz} delle operazioni effettuate dai client
	 * Il metodo inoltre aggiunge l'operazione effettuata con l'identificativo del client
	 * all'area di testo {@link #serverOutput}
	 * 
	 * @param id Identificativo del client
	 * @param op Operazione effettuata
	 */

	void setOp(int id, String op){
		int index = id - 1;
		String inx = "Client " + id + ": ";

		this.operaz.add(index, op);

		for(String item : this.operaz.get(id - 1).split("-"))
			this.setOutputText("\n" + inx + item);
		this.start = false;
	}
	
	/**
	 * Tale metodo che &egrave chiamato dall'ascoltatore associato alla JComboBox {@link #comboClient}
	 * per mostrare nell'area di testo {@link #clientInfo} le operazioni effettuate dal client
	 * con identificativo equivalente a quello inserito nella JComboBox.
	 */
	
	void getClientActions(){
		int id = 0;
		if((Integer)this.comboClient.getSelectedItem() != null){
			id = (Integer)this.comboClient.getSelectedItem();			
			this.clientInfo.setText("Operazione effettuate dal Client " + id + "\n");
			for(String item : this.operaz.get(id - 1).split("-"))
				this.clientInfo.setText(this.clientInfo.getText() + "\n" + item);

		}

		return;
	}
	
	/**
	 * Metodo che ritorna i valori inseriti all'interno della finestra modale relativa alle impostazioni
	 * del database
	 * 
	 * @return {@link #dialogData}
	 */
	
	List<String> getSelectedValues(){
		return this.dialogData;
	}
	
	void setOutputText(String text){
		this.serverOutput.setText(this.serverOutput.getText() + "\n" + text);
	}
	
	/**
	 * Punto di avvio dell'applicazione. Viene creata la GUI del server.
	 */
	
	public static void main(String[] args) {
		ServerApp mainFrame = new ServerApp("KMeans Server");
	}
}