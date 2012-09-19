package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;


/**
 * La classe MultiServer modella un Thread in grado di ascoltare differenti richieste per
 * differenti client andando quindi a realizzare un server che ascolta pi&ugrave richieste.
 * Per ogni richiesta viene creato un thread a parte. Sar&agrave tale thread ad ascoltare
 * le richieste per un determinato client.
 * 
 * @author Luca Suriano
 * @see java.lang.Thread
 */

class MultiServer extends Thread{
	/**
	 * Porta su cui il server sar&agrave in ascolto.
	 */
	private int PORT;
	
	/**
	 * Riferimento alla GUI del server. Serve per permettere la scrittura, nell'apposita area di testo,
	 * delle operzioni che i client fanno o non fanno.
	 */
	private ServerApp frame;
	
	/**
	 * Costruttore di classe. Inizializza la porta e il riferimento alla GUI del server
	 * ed invoca run()
	 * 
	 * @param port Porta su cui il server sar&agrave in ascolto
	 * @param app Riferimento ad un oggetto istanza della classe ServerApp {@link ServerApp}
	 */
	
	MultiServer(int port, ServerApp app){
		this.frame = app;
		this.PORT = port;
		super.start();
	}
	
	/**
	 * Istanzia un oggetto istanza della classe ServerSocket che si occuper&agrave di gestire le
	 * richieste da parte del client. Ad ogni nuova richiesta connessione si istanzia ServerOneClient.
	 * <br>Se la creazione del server sulla porta {@link #PORT} non &egrave andata a buon fine
	 * verr&agrave visualizzato un messaggio di errore attraverso una {@link javax.swing.JOptionPane}.
	 * Verr&agrave mostrato un messaggio di errore anche nel caso in cui c'&egrave stato un errore
	 * durante l'attesa di connesioni da parte dei client o se non &egrave possibile chiudere il server. 
	 */

	public void run() {

		ServerSocket s = null;

		try{
			this.frame.setDB();
			s = new ServerSocket(this.PORT);

			try {
				this.frame.serverOutput.setText("---- Server Avviato Correttamente! ---- \n Server in attesa di richieste...");
				
				
				while(true) {
					Socket socket = s.accept();
					try{
						new ServerOneClient(socket, this.frame);
					} catch(IOException e) {
						JOptionPane.showMessageDialog(this.frame, e);
					}

				}
			} catch (IOException e){
				JOptionPane.showMessageDialog(this.frame, "Errore nell'attesa di connessioni da parte dei client");
			} finally {
				try {
					s.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this.frame, "\nErrore! - Impossibile chiudere il Server sulla porta: " + this.PORT);
				}
			}
		} catch (IOException e){
			this.frame.serverOutput.setText("Errore! - Impossibile creare il Server sulla porta: " + this.PORT);
		}
	}
}