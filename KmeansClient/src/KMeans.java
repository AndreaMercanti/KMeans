import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jfree.data.general.Dataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYDataset;

/**
 * La classe definisce un'applet eseguibile in un browser web. L'applet &egrave suddivisa in due 
 * sezioni organizzate in tab.
 * <p>
 * La prima identificata dal tab DB con relativa icona, si occupa di effettuare richieste al server
 * per l'esecuzione di un'attivit&agrave di scoperta dei cluster sulla base di dati, eseguendo 
 * l'algoritmo kmeans.
 * In questo caso l'applet rimarr&agrave in attesa dei risultati da parte del server, mostrando (se 
 * tutto procede correttamente) il risultato dell'algoritmo kmeans con il relativo grafico, altrimenti
 * verr&agrave mostrato un messaggio nel caso di errore.<br>
 * La corretta esecuzione dell'algoritmo in questo caso produrr&agrave inoltre su server un file
 * il cui nome corrisponder&agrave: alla tabella specificata nell'apposita casella di testo concatenato
 * al numero di di cluster nella casella di testo vicina.
 * <p>
 * La seconda sezione &egrave identificata dal tab FILE si occupa di effettuare richieste al 
 * server per effettuare una lettura da file contenente attivit&agrave precendenti di scoperta dei
 * cluster sul database. Il nome del file segue la stessa logica precendente e nel caso in cui tale 
 * file esiste verranno mostrati a video rispettivamente, i risultati dell'algoritmo kmeans con annesso 
 * grafico.
 * <p>
 * In entrame le tipologie di richieste nel momento in cui esse vengono effettuate e, nel caso in cui 
 * tutto proceda correttamente, verr&agrave mostrata una finestra di dialogo modale 
 * (di tipo {@link AttributeDialog}) per la scelta del grafico e se necessario degli attributi (relativi
 * al grafico stesso).<br>
 * La creazione del grafico &egrave a carico della classe {@link StreamChart}.
 * <br>
 * <br>
 * Esempi:
 * <br>
 * <br>
 * <br>
 * -Applet con grafico (senza legenda esterna):
 * <br>
 * <br>
 * <center><img src="images/mainapplet.jpg" /></center>
 * <br>
 * <br>
 * <br>
 * -Applet con grafico (con legenda esterna):
 * <br>
 * <br>
 * <center><img src="images/appletlegenda.jpg" /></center>
 * <br>
 * <br>
 * @author Luca Suriano
 * @see AttributeDialog
 * @see StreamChart
 * 
 */


public class KMeans extends JApplet{
	/**
	 * Stream di output che permette al client (l'applet) di inviare le richieste al server
	 */
	private ObjectOutputStream out;
	
	/**
	 * Stream di input che permette al client (l'applet) di ricevere i risultati da parte del server 
	 */
	private ObjectInputStream in;
	
	/**
	 * Contiene il risultato dei dati da inserire all'interno del grafico dell'applet, calcolati
	 * dal server. Pu&ograve essere di tipo differente in base al tipo di grafico scelto nella finestra
	 * di dialogo modale che viene visualizzata.
	 * 
	 * @see org.jfree.data.general.Dataset
	 */
	private Dataset streamDataset;
	
	/**
	 * Inner class privata che estende JPanel. La classe rappresenta il pannello principale
	 * dell'applet che conterr&agrave le due sezioni (tab) principali dell'applet.
	 * Ciascuna &egrave rappresentata dalla classe {@link JPanelCluster} ed esse forniscono
	 * rispettivamente le componenti per l'utilizzo dell'applet.
	 * 
	 * @author Luca Suriano
	 * @see javax.swing.JPanel
	 * @see JPanelCluster
	 *
	 */

	private class TabbedPane extends JPanel{
		
		/**
		 * Tab per l'utilizzo delle funzionalit&agrave su database
		 */
		private JPanelCluster panelDB;
		
		/**
		 * Tab per l'utilizzo delle funzionalit&agrave su file
		 */
		private JPanelCluster panelFile;
		
		/**
		 * Il costruttore della classe si occupa di assegnare un layout al pannello che conterr&agrave
		 * le due sezioni gestendole attraverso tab con la classe {@link javax.swing.JTabbedPane}.
		 * I due tab verranno inizializzati con un nome, un'icona, il relativo pannello (di tipo
		 * {@link JPanelCluster} e un tooltip.
		 * Ciascun pannello di tipo {@link JPanelCluster} verr&agrave inizializzato con un bottone
		 * e un ascoltatore. Ciascun ascoltatore corrisponder&agrave alle richieste di un'attivit&agrave
		 * di scoperta dei cluster su database oppure di lettura di una precedente attivit&agrave
		 * di scoperta da file.<br>
		 * Nel caso in cui le richieste al server non vadano a buon fine verr&agrave mostrato un
		 * messaggio a video attraverso una finestra di tipo {@link javax.swing.JOptionPane}
		 * 
		 * @see javax.swing.JTabbedPane
		 * @see JPanelCluster
		 * @see javax.swing.JOptionPane
		 */
		
		TabbedPane() {
			super(new GridLayout(1, 1)); 
			JTabbedPane tabbedPane = new JTabbedPane();
			java.net.URL imgURL = getClass().getResource("img/db.jpg");
			ImageIcon iconDB = new ImageIcon(imgURL);
			
			panelDB = new JPanelCluster("MINE", new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try{
						learningFromDBAction();
					}
					catch (SocketException e1) {
						JOptionPane.showMessageDialog(panelFile, "Errore! - Impossibile Connettersi al Server\n\nDettagli:\n" + e1);
					}
					catch (IOException e1) {
						JOptionPane.showMessageDialog(panelFile, e1);
					} 
					catch (ClassNotFoundException e1) {
						JOptionPane.showMessageDialog(panelFile, e1);
					}
				}
			});
			tabbedPane.addTab("DB", iconDB, panelDB, "Kmeans from Database");

			imgURL = getClass().getResource("img/file.jpg");
			ImageIcon iconFile = new ImageIcon(imgURL);
			panelFile = new JPanelCluster("STORE FROM FILE", new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try{
						learningFromFileAction();
					}
					catch (SocketException e1) {
						JOptionPane.showMessageDialog(panelFile, "Errore! - Impossibile Connettersi al Server\n\nDettagli:\n" + e1);
					} 
					catch (IOException e1) {
						JOptionPane.showMessageDialog(panelFile, e1);
					}
					catch (ClassNotFoundException e1) {
						JOptionPane.showMessageDialog(panelFile, e1);						
					}					
				}
			});
			tabbedPane.addTab("FILE", iconFile, panelFile,"Kmeans from File");

			add(tabbedPane);         

			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		}
		
		/**
		 * Il metodo si occupa di restituire il riferimento all'oggetto istanza della classe Frame
		 * a cui verr&agrave momentaneamente aggiunto l'{@link AttributeDialog}.
		 * 
		 * @return Frame a cui aggiungere l'attribute dialog.
		 */
		
		private Frame findParentFrame(){ 
			Component c = getParent();

			while(true){
				if(c instanceof Frame)
					return (Frame)c;

				c = c.getParent();
			}
		}

		/**
		 * Metodo privato utilizzato nel caso in cui viene richiesta un'attivit&agrave di scoperta
		 * dei cluster sulla base di dati (richiedendo quindi l'utilizzo dell'algoritmo kmeans su 
		 * database).<br>
		 * Tale richiesta &egrave effettuata nell'apposita sezione (tab) dell'applet.<br>
		 * Il metodo si occupa di leggere le due caselle di testo corrispondenti al nome della
		 * tabella del database e al numero di cluster da scoprire.
		 * Se viene inserito un valore minore di 0 oppure un valore maggiore rispetto al numero 
		 * di centroidi generabili dall'insieme di transazioni (presenti nella base di dati),
		 * viene mostrato un messaggio di errore in una {@link javax.swing.JOptionPane}.<br>
		 * Le diverse richieste al server vengono effettuate attraverso una serie di valori
		 * numerici.<br> 
		 * In questo di attivit&agrave di scoperta dei cluster dalla base di dati la sequenza
		 * &egrave la seguente:
		 * <li>Viene inviato il comando 0 e il nome della tabella.<br>
		 * Questo comando corrisponde (sul server) a un'interrogazione sulla base di dati.<br>
		 * In caso di risposta diversa da "OK", viene visualizzato un messaggio di errore in 
		 * una {@link javax.swing.JOptionPane} e termina l'esecuzione del metodo.</li>
		 * <li>Viene inviato il comando 1 e il numero di cluster da scoprire e aspetta la 
		 * risposta del server.<br>
		 * Questo comando corrisponde (sul server) alla vera e propria attivit&agrave di scoperta
		 * attraverso l'algoritmo kmeans.<br>
		 * In caso di risposta diversa da "OK", visualizza un messaggio 
		 * di errore in una {@link javax.swing.JOptionPane} e termina l'esecuzione del metodo.</li>
		 * <li>Quindi viene letto il numero di iterazioni e i cluster cos&igrave come sono trasmessi da 
		 * server e li visualizza nell'area di testo corrispondente all'output del server
		 * {@link JPanelCluster#clusterOutput}.</li>
		 * <li>Viene inviato al server il comando 2 e aspetta la risposta del server.
		 * Questo comando corrisponde (sul server) al salvataggio su file dell'attivit&agrave 
		 * di scoperta per letture successive.<br>
		 * In caso di risposta diversa da "OK", visualizza un messaggio di errore in una 
		 * {@link javax.swing.JOptionPane}  e termina l'esecuzione del metodo</li>
		 * <li>Viene inviato al server il comando 4 e il tipo di azione che si sta effettuando (ovvero
		 * un'attivit&agrave di scoperta dalla base di dati).<br>
		 * Questo comando corrisponde (sul server) alla generazione di tutti quei dati per la creazione
		 * del grafico.<br>
		 * Infatti inviato questo comando viene richiamato il metodo {@link #chartAction(JPanelCluster)}
		 * che utilizzer&agrave i dati restituiti dal server per il grafico che verr&agrave inserito nel
		 * pannello {@link JPanelCluster#panelChart} utile a questo scopo.<br>
		 * Nel caso in cui anche quest'ultima operazine vada a buon fine verr&agrave mostrato un messaggio
		 * ("OK") in una {@link javax.swing.JOptionPane} terminando l'esecuzione del metodo.
		 * </li>
		 * 
		 * @throws SocketException	
		 * 		
		 * @throws IOException			    Sollevata in caso di errori nelle operazioni di lettura dei
		 * 								    risultati dal server.
		 * 
		 * @throws ClassNotFoundException   Sollevata nelle operazioni di lettura dei risultati da file
		 * 									nel caso in cui gli oggetti passati sullo stream sono
		 * 									istanza di una classe non presente su client.
		 */

		private void learningFromDBAction() throws SocketException, IOException, ClassNotFoundException {
			int k = 0;
			String tableName = "";
			String result = "";


			tableName = this.panelDB.tableText.getText();

			if(tableName.equals("")){
				JOptionPane.showMessageDialog(this, "Errore! - Inserire il nome della tabella!");
				return;
			}

			try{
				k = new Integer(panelDB.kText.getText()).intValue();
			}
			catch(NumberFormatException e){
				JOptionPane.showMessageDialog(this, "Errore! - Inserire un valore per k!");
				return;
			}

			out.writeObject(0);
			out.writeObject(tableName);

			result = (String)in.readObject();

			if(!result.equals("OK"))
				JOptionPane.showMessageDialog(this, result);
			else{
				out.writeObject(1);
				out.writeObject(k);

				result = (String)in.readObject();

				if(!result.equals("OK")){
					JOptionPane.showMessageDialog(this, result);
					return;
				} else{


					Integer iter = (Integer)in.readObject();

					panelDB.clusterOutput.setText((String)in.readObject() + "Numero iterate: " + iter + "\n");

					out.writeObject(2);

					result = (String)in.readObject();

					if(!result.equals("OK")){
						JOptionPane.showMessageDialog(this, result);
						return;
					}else{


						out.writeObject(4);
						out.writeObject("db");

						chartAction(this.panelDB);

					}
				}
			}
		}

		/**
		 * Metodo privato utilizzato nel caso in cui viene richiesta la funzione di lettura di un 
		 * determinato file su server. Tale richiesta &egrave effettuata nell'apposita sezione
		 * (tab) dell'applet.<br>
		 * Il metodo si occupa di leggere il nome della tabella e il numero di cluster, la cui
		 * concatenazione va a costituire il nome del file presente su server.<br>
		 * Viene quindi effettuata la richiesta al server (attraverso il comando 3) il quale nel caso 
		 * positivo visualizza, in una JOptionPane, un messaggio che confermi il successo della attività
 		 * ,visualizzando poi il risultato nell'area di testo corrispondente
 		 * {@link JPanelCluster#clusterOutput}.<br>
 		 * Viene inviato al server il comando 4 e il tipo di azione che si sta effettuando (ovvero
		 * un'attivit&agrave di lettura da file).<br>
		 * Questo comando corrisponde (sul server) alla generazione di tutti quei dati per la creazione
		 * del grafico.<br>
		 * Inviato questo comando viene richiamato il metodo {@link #chartAction(JPanelCluster)}
		 * che utilizzer&agrave i dati restituiti dal server per il grafico che verr&agrave inserito nel
		 * pannello {@link JPanelCluster#panelChart} utile a questo scopo.<br>
		 * In caso di risposta diversa da "OK" viene mostrato un messaggio di errore
		 * attraverso una finestra di tipo {@link javax.swing.JOptionPane}.
		 * 
		 * @throws SocketException
		 * 
		 * @throws IOException			    Sollevata in caso di errori nelle operazioni di lettura dei
		 * 								    risultati dal server.
		 * 
		 * @throws ClassNotFoundException   Sollevata nelle operazioni di lettura dei risultati da file
		 * 									nel caso in cui gli oggetti passati sullo stream sono
		 * 									istanza di una classe non presente su client.
		 */
		
		private void learningFromFileAction() throws SocketException, IOException, ClassNotFoundException {
			String tableName = panelFile.tableText.getText();

			String ncluster = panelFile.kText.getText();

			if(tableName.equals("")){
				JOptionPane.showMessageDialog(this, "Errore! - Inserire il nome della tabella!");
				return;
			}

			if(ncluster.equals("")){
				JOptionPane.showMessageDialog(this, "Errore! - Inserire un valore per k!");
				return;
			}

			out.writeObject(3);

			out.writeObject(tableName);
			out.writeObject(ncluster);

			String result = (String)in.readObject();


			if(!result.equals("OK")){
				JOptionPane.showMessageDialog(this, result);
				return;
			} else {
				JOptionPane.showMessageDialog(this, "OK!");
				String kmeansString = (String)in.readObject();
				String newStr = kmeansString.replaceAll("\\)- ", "\\)\n");

				out.writeObject(4);
				out.writeObject("file");
				panelFile.clusterOutput.setText(newStr);
				
				chartAction(this.panelFile);

				return;
			}
		}
		
		/**
		 * Metodo privato chiamato dai due metodi {@link #learningFromDBAction} e 
		 * {@link #learningFromFileAction} per la creazione del grafico.<br>
		 * La creazione del grafico dipende dagli attributi e dal tipo di grafico scelti
		 * nell'apposito {@link AttributeDialog}.
		 * Tale finestra modale verr&agrave creata da questo metodo e verr&agrave inizializzata
		 * con gli attributi letti dal server.<br>
		 * Una volta effettuata la selezione i valori corrispondenti a questa vengo inviati al
		 * server. Come risposta il metodo riceve un dataset il cui riferimento verr&agrave passato
		 * all'attributo {@link #streamDataset}. Tale dataset verr&agrave utilizzato per la creazione 
		 * del grafico che sar&agrave a carico della classe {@link StreamChart}.
		 * Infatti qui viene istanziato un oggetto istanza di tale classe, sul quale vegono
		 * richiamati i metodi {@link StreamChart#createChart} e {@link StreamChart#createChartPanel}
		 * che in base al tipo di grafico selezionato si occuperanno di creare il grafico inserendolo
		 * nel {@link JPanelCluster#panelChart} del {@link JPanelCluster} passato come parametro
		 * , in modo da permettere la sua aggiunta alla applet.
		 * 
		 * @param panelAction 			    Il riferimento ad uno dei due tab. Grazie a questo riferimento 
		 * 								    &egrave possibile aggiungere il grafico nel pannello per il 
		 * 							      	grafico presente in uno dei due tab. 
		 * 
		 * @throws IOException			    Sollevata in caso di errori nelle operazioni di lettura dei
		 * 								    risultati dal server.
		 * 
		 * @throws ClassNotFoundException   Sollevata nelle operazioni di lettura dei risultati da file
		 * 									nel caso in cui gli oggetti passati sullo stream sono
		 * 									istanza di una classe non presente su client.
		 * 
		 * @see AttributeDialog
		 * @see StreamChart
		 * @see JPanelCluster
		 * @see org.jfree.data.general.Dataset
		 */
		
		private void chartAction(JPanelCluster panelAction) throws IOException, ClassNotFoundException{
			String result = "";
			String[] attributes = (String[])in.readObject();

			AttributeDialog attrD = new AttributeDialog(findParentFrame(), attributes);
			attrD.setVisible(true);
			ArrayList valoriSelez = attrD.getselectedValues();

			attrD.dispose();

			out.writeObject(valoriSelez);

			result = (String)in.readObject();

			if(!result.equals("OK")){
				JOptionPane.showMessageDialog(this, result);
				return;
			} else {
				JOptionPane.showMessageDialog(this, "OK!");

				streamDataset = (Dataset)in.readObject();
				

				StreamChart mainChart = new StreamChart();

				panelAction.panelChart.removeAll();

				if(streamDataset instanceof PieDataset){
					int total = (Integer)in.readObject();
					StreamChart.setTotalElem(total);
					panelAction.panelChart.add(mainChart.createChartPanel(mainChart.createChart((PieDataset)streamDataset)));
				}
				else{
					String labelX = (String)in.readObject();
					String labelY = (String)in.readObject();
					Double maxX = (Double)in.readObject();
					Double maxY = (Double)in.readObject();
					panelAction.panelChart.add(mainChart.createChartPanel(mainChart.createChart((XYDataset)streamDataset, labelX, labelY, maxX, maxY)));
				}
			}
		}
	}
	
	/**
	 * Classe privata che modella un JPanel che rappresenta ogni singola sezione (tab) 
	 * che fa parte dell'applet.<br>
	 * La sezione creata verr&agrave poi aggiunta ad un {@link javax.swing.JTabbedPane}.
	 * Ciascuna sezione contiene al suo interno:
	 * <li>due caselle di testo una per il nome della tabella e una per il numero di cluster;</li>
	 * <li>un'area di testo scorrevole non editabile che conterr&agrave il risultato da parte 
	 * del server;</li>
	 * <li>un bottone per l'invio della richiesta al server (che &egrave diversa per ciascun tab);</li>
	 * <li>un pannello che conterr&agrave il relativo grafico.</li>
	 * 
	 * @author Luca Suriano
	 */

	private class JPanelCluster extends JPanel{
		
		/**
		 * Casella di testo in cui verr&agrave inserito il nome della tabella. Tale nome
		 * coincider&agrave sia con il nome della tabella del database sia (concatenato
		 * al contenuto della casella di testo del numero di cluster) al nome del file
		 * creato su server.
		 * 
		 * @see javax.swing.JTextField
		 */
		private JTextField tableText = new JTextField(20);
		
		/**
		 * Casella di testo in cui verr&agrave inserito il numero di cluster che si intende scoprire.
		 * Tale valore sar&agrave concatenato a quello della casella di testo precedente.
		 * 
		 * @see javax.swing.JTextField
		 */
		private JTextField kText = new JTextField(10);
		
		/**
		 * Conterr&agrave l'output del server. Essa sar&agrave un'area di testo non editabile.
		 */
		private JTextArea clusterOutput = new JTextArea(20, 20);
		
		/**
		 * Il bottone che si occuper&agrave di effettuare una particolare richiesta al server.
		 * Al bottone verr&agrave associato un nome e un ascoltatore entrambi passati come 
		 * argomenti al costruttore.
		 * 
		 * @see javax.swing.JButton
		 */
		
		private JButton executeButton;		
		
		/**
		 * Pannello che conterr&agrave il grafico.
		 * 
		 * @see javax.swing.JPanel
		 */
		private JPanel panelChart = new JPanel();
		
		/**
		 * Il costruttore si occupa di creare ciascuna sezione (tab) andandole a suddividere in due
		 * parti.<br>
		 * Nella parte sinistra ci saranno:
		 * <li>Le due texfield per la lettura del nome della tabella e il numero di cluster</li>
		 * <li>L'area di testo scorrevole non editabile contenente l'output del server</li>
		 * <li>Il bottone per l'invio della richiesta al server</li>
		 * Il bottone avr&agrave nome pari al parametro <i>buttonName</i> e l'ascoltatore che si 
		 * occuper&agrave di gestire una particolare richiesta rappresentato dal parametro
		 * <i>a</i>.<br>
		 * Nella parte destra ci sar&agrave il pannello contenente il relativo grafico e quando
		 * necessario anche la legenda con alcune funzionalit&agrave utili per 
		 * gestire al meglio la visualizzazione del grafico stesso.
		 *  
		 * @param buttonName 	Nome per il bottone {@link #executeButton}
		 * @param a 			Ascoltatore da assegnare al bottone {@link #executeButton}
		 */
		
		JPanelCluster(String buttonName, java.awt.event.ActionListener a){
			super(new FlowLayout());
			JScrollPane outputScroll = new JScrollPane(clusterOutput);
			JPanel mainLeft = new JPanel();
			JPanel mainRight = new JPanel();


			mainLeft.setPreferredSize(new Dimension(450, 600));
			mainLeft.setLayout(new BorderLayout());
			mainRight.setLayout(new BorderLayout());


			JPanel upPanel = new JPanel();
			JPanel centralPanel = new JPanel();
			JPanel downPanel = new JPanel();

			upPanel.add(new JLabel("Tabel"));
			upPanel.add(this.tableText);
			upPanel.add(new JLabel("k"));
			upPanel.add(this.kText);


			clusterOutput.setEditable(false);
			clusterOutput.setLineWrap(true);

			centralPanel.setLayout(new BorderLayout());
			centralPanel.add(outputScroll, BorderLayout.CENTER);



			downPanel.setLayout(new FlowLayout());
			downPanel.add(this.executeButton = new JButton(buttonName));


			mainLeft.add(upPanel, BorderLayout.NORTH);
			mainLeft.add(centralPanel, BorderLayout.CENTER);
			mainLeft.add(downPanel, BorderLayout.SOUTH);


			panelChart.setPreferredSize(new Dimension(700, 650));
			mainRight.add(panelChart, BorderLayout.LINE_END);

			add(mainLeft);
			add(mainRight);

			this.executeButton.addActionListener(a);
		}
	}
	
	/**
	 * Metodo sovrascritto della classe JApplet.
	 * Il metodo si occupa di creare l'applet andando a inserire al suo interno le due sezioni
	 * (tab) per la gestione delle richieste da parte dell'utente.
	 * Viene inoltre stabilita una connessione al server e quindi si ottengono i relativi
	 * stream di output e di input, andando a inizializzare i due attributi {@link #in} e
	 * {@link #out}.<br>
	 * Nel caso in cui non &egrave possibile stabilire una connessione con il server viene
	 * mostrato un messaggio di errore. Sar&agrave quindi necessario riaggiornare la pagina del
	 * browser per ritentare una connessione.
	 */

	public void init(){
		Container apCont = this.getContentPane();
		apCont.setLayout(new BoxLayout(apCont, BoxLayout.Y_AXIS));

		String ip = getParameter("ip");
		String portpar = getParameter("port");

		int port = Integer.parseInt(portpar);		

		
		try{
			
			InetAddress addr = InetAddress.getByName(ip);
			Socket socket = new Socket(addr, port);
			
			TabbedPane tb = new TabbedPane();
			this.getContentPane().add(tb);
			
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
			
		} catch(IOException e){
			JOptionPane.showMessageDialog(this, "Impossibile Connttersi al Server!");
			apCont.add(Box.createRigidArea(new Dimension(20, 20)));
			apCont.add(new JLabel("Impossibile Connttersi al Server!"));
			apCont.add(Box.createRigidArea(new Dimension(20, 20)));
			apCont.add(new JLabel("Non è possibile eseguire alcuna operazione."));
			apCont.add(Box.createRigidArea(new Dimension(20, 20)));
			apCont.add(new JLabel("Riaggiornare la pagina per tentare nuovamente una nuova connessione."));
		}
	}
}