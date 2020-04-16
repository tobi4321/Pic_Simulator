import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.JMenu;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
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
		
		JButton btnSaveChanges = new JButton("Save Changes");
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
				ctr.loadSRCFile();
			}
		});
		mnFile.add(btnLoadSrcFile);
	}

}
