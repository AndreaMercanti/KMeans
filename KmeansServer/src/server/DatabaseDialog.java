package server;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * La classe modella una finestra modale per la lettura dei dati che verranno
 * successivamente utilizzati per la connessione al database.
 * La finestra prevede 3 campi di testo che rappresentano rispettivamente porta, nome utente
 * e password.<br>
 * La classe &egrave di supporto per permettere l'utilizzo del database in ambienti con 
 * impostazioni differenti.
 * 
 * @author Luca Suriano
 *
 */

public class DatabaseDialog extends JDialog {
	
	/**
	 * Textfield che conterr&agrave la porta su cui &egrave attivo il servizio del DBMS 
	 * 
	 * @see javax.JTextField
	 */
	private JTextField port;
	
	/**
	 * Textfield che conterr&agrave il nome utente
	 * 
	 * @see javax.JTextField
	 */
	private JTextField user;
	
	/**
	 * Textfield che conterr&agrave la password
	 * 
	 * @see javax.JTextField
	 */
	private JTextField pwd;
	
	/**
	 * Pannello che conterr&agrave ogni singola coppia label - textfield a cui verr&agrave
	 * associato un FlowLayout
	 * 
	 * @see javax.swing.FlowLayout
	 */
	private JPanel listPanel;
	
	/**
	 * ArrayList che conterr&agrave i valori inseriti
	 * 
	 * @see java.util.ArrayList
	 */
	private ArrayList<String> values;
	
	/**
	 * Il costruttore della classe si occupa di inizializzare la finestra in modalit&agrave
	 * modale assegnarla al frame specificato e fornire per questa un titolo.<br>
	 * La finestra viene resa non ridimensionabile, viene assegnata inoltre una dimensione e
	 * viene inserito un bottone. A tale bottone corrisponder&agrave un'azione che effettuer&agrave
	 * il salvataggio dei dati e la chiusura della finestra.
	 * La distruzione delle risorse associate alla finestra avviene esternamente visto che la chiusura
	 * comporta semplicemente il nascondere la finestra.
	 * 
	 * @param frame Rappresenta il frame a cui associare la finestra.
	 * @param name Rappresenta il titolo da associare alla finestra
	 */
	
	public DatabaseDialog(Frame frame, String name) {
		super(frame, name, true);

		JButton btnSend = new JButton("OK");

		super.setLocationRelativeTo(frame);
		super.setLocation(this.getX() - 150, this.getY() - 125);

		this.addWindowListener(new WindowAdapter(){

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});
		this.setSize(new Dimension(300, 250));
		this.setResizable(false);

		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

		this.port = new JTextField(10);
		this.user = new JTextField(10);
		this.pwd = new JTextField(10);

		listPanel = new JPanel(new FlowLayout());
		listPanel.add(new JLabel("Port:"));
		listPanel.add(this.port);
		add(listPanel);

		listPanel = new JPanel(new FlowLayout());
		listPanel.add(new JLabel("Username:"));
		listPanel.add(this.user);
		add(listPanel);

		listPanel = new JPanel(new FlowLayout());
		listPanel.add(new JLabel("Password:"));
		listPanel.add(this.pwd);
		add(listPanel);

		btnSend.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(port.getText().equals("") ||
						user.getText().equals("") || pwd.getText().equals(""))
					showMessage();
				else {
					values = new ArrayList<String>();
					values.add(port.getText());	
					values.add(user.getText());
					values.add(pwd.getText());
					chiudi();
				}
			}
		});

		listPanel = new JPanel(new FlowLayout());
		listPanel.add(btnSend);
		add(listPanel);

	}
	
	/**
	 * Visualizza un messaggio di errore nel caso di un mancato inserimento di dati
	 * all'interno di almeno una textfield presente nella finestra.
	 */

	private void showMessage(){
		JOptionPane.showMessageDialog(this, "Errore! - Inserire tutti i valori!");
	}
	
	/**
	 * Si occupa di nascondere la finestra senza distruggere le risorse ad assegnate.
	 */

	private void chiudi(){
		this.setVisible(false);
	}
	
	/**
	 * Si occupa di ritornare l'insieme dei valori inseriti all'interno della finestra.
	 * 
	 * @return {@link #values}
	 */

	ArrayList<String> getselectedValues(){
		return this.values;
	}
	
	/**
	 * Si occupa di ripulire le textfield presenti all'interno della finestra.
	 */
	
	void resetFields(){
		this.user.setText("");
		this.pwd.setText("");
	}
}