import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class MnemonicView extends JFrame {

	private JPanel contentPane;

	private Controller ctr;
	
	protected JTextArea txtArea_mnemonic;
	/**
	 * Create the frame.
	 */
	public MnemonicView(Controller pCtr) {
		
		ctr = pCtr;
		
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 587, 757);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane((Component) null);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		txtArea_mnemonic = new JTextArea();
		scrollPane.setViewportView(txtArea_mnemonic);
		txtArea_mnemonic.setText("             MOVLW 0x1f;\r\n             MOVF 0x1e,1;\r\n             MOVLW 0x05;\r\nm1\r\n          ADDWF 0x1e,1;\r\nm2\r\n          NOP;\r\n             GOTO m1;");
		txtArea_mnemonic.setFont(new Font("Monospaced", Font.PLAIN, 18));
		
		JMenuBar menuBar = new JMenuBar();
		scrollPane.setColumnHeaderView(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JButton btnSaveChanges = new JButton("Compile Changes");
		btnSaveChanges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctr.saveMnemonicCode(txtArea_mnemonic.getText());
				ctr.closeMnemonicWindow();
			}
		});
		mnFile.add(btnSaveChanges);
		
		JButton btnLoadSrcFile = new JButton("Load SRC File");
		btnLoadSrcFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				final JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("SRC File","src");
				fc.addChoosableFileFilter(filter);
				//In response to a button click:
				int returnVal = fc.showOpenDialog(btnLoadSrcFile);
				 if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
			            ctr.loadSRCFile(file);
			            //This is where a real application would open the file.
			            System.out.println("Opening: " + file.getName());
			        } else {
			            System.out.println("Open command cancelled by user.");
			        }

			}
		});
		mnFile.add(btnLoadSrcFile);
		
		JButton btnSaveSrcFile = new JButton("Save SRC File");
		btnSaveSrcFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc_save = new JFileChooser();
				fc_save.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("SRC File","src");
				fc_save.addChoosableFileFilter(filter);
				fc_save.setDialogTitle("Specify a file to save");   
				File fileToSave; 
				int userSelection = fc_save.showSaveDialog(btnSaveSrcFile);
				 
				if (userSelection == JFileChooser.APPROVE_OPTION) {
				    fileToSave = fc_save.getSelectedFile();
				    
				    ctr.saveSRCFile(fileToSave);
				    
				    //ctr.saveSRCFile(fileToSave);
				    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
				}
			}
		});
		mnFile.add(btnSaveSrcFile);
	}

}
