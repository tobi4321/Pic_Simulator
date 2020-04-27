import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
/// class Simulator_Window
/**
*  Grafical user interface for Pic Simualtor.
*  
*  REWORK NEEDED
* 
* **/
public class Simulator_Window {

	private JFrame frmMicrocontrollerSimulator;
	private Controller ctr;
	protected TableModel dataModel;
	protected MyPanel panel_segmentCanvas;
	protected JTable table_Code;
	protected DefaultTableModel tbl_code;
	protected DefaultTableModel tbl_memory;
	protected DefaultTableModel tbl_special;
	protected DefaultTableModel tbl_status;
	protected DefaultTableModel tbl_pcl;
	protected JTable table_Memory;
	protected JTable table_special_regs;
	// members to input values into register memory
	private JTextField txtField_input;
	private JButton btn_InputRegister;
	private JComboBox comboBox_File;
	private JComboBox comboBox_SubFile;
	// member for port a
	private JLabel lbl_ra_tris7;
	private JLabel lbl_ra_tris6;
	private JLabel lbl_ra_tris5;
	private JLabel lbl_ra_tris4;
	private JLabel lbl_ra_tris3;
	private JLabel lbl_ra_tris2;
	private JLabel lbl_ra_tris1;
	private JLabel lbl_ra_tris0;
	private JRadioButton rbtn_ra_7;
	private JRadioButton rbtn_ra_6;
	private JRadioButton rbtn_ra_5;
	private JRadioButton rbtn_ra_4;
	private JRadioButton rbtn_ra_3;
	private JRadioButton rbtn_ra_2;
	private JRadioButton rbtn_ra_1;
	private JRadioButton rbtn_ra_0;
	// member for port b
	private JLabel lbl_rb_tris7;
	private JLabel lbl_rb_tris6;
	private JLabel lbl_rb_tris5;
	private JLabel lbl_rb_tris4;
	private JLabel lbl_rb_tris3;
	private JLabel lbl_rb_tris2;
	private JLabel lbl_rb_tris1;
	private JLabel lbl_rb_tris0;
	private JRadioButton rbtn_rb_7;
	private JRadioButton rbtn_rb_6;
	private JRadioButton rbtn_rb_5;
	private JRadioButton rbtn_rb_4;
	private JRadioButton rbtn_rb_3;
	private JRadioButton rbtn_rb_2;
	private JRadioButton rbtn_rb_1;
	private JRadioButton rbtn_rb_0;
	
	// member to set the Quarz Frequency
	protected JComboBox comboBox_quarzFrequency;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
            // Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Simulator_Window window = new Simulator_Window();
					window.frmMicrocontrollerSimulator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Simulator_Window() {
		ctr = new Controller(this);
		initialize();
		ctr.inizializeMemory();
		ctr.startMemoryUpdateThread();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() {
		
		frmMicrocontrollerSimulator = new JFrame();
		frmMicrocontrollerSimulator.setTitle("MicroController Simulator");
		frmMicrocontrollerSimulator.setBounds(100, 100, 1550, 920);
		frmMicrocontrollerSimulator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		Box verticalBox = Box.createVerticalBox();
		verticalBox.setBorder(UIManager.getBorder("InternalFrame.border"));
		frmMicrocontrollerSimulator.getContentPane().add(verticalBox);
		
		Box upperArea = Box.createHorizontalBox();
		upperArea.setAlignmentY(0.5f);
		verticalBox.add(upperArea);
		
		Box verticalMemoryView = Box.createVerticalBox();
		verticalMemoryView.setMinimumSize(new Dimension(320, 700));
		verticalMemoryView.setPreferredSize(new Dimension(350, 700));
		verticalMemoryView.setMaximumSize(new Dimension(350, 1000));
		upperArea.add(verticalMemoryView);
		
		JPanel inputPanel = new JPanel();
		verticalMemoryView.add(inputPanel);
		FlowLayout flowLayout = (FlowLayout) inputPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		
		btn_InputRegister = new JButton("Input");
		btn_InputRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String input = txtField_input.getText();
				
				int fileNumber = Integer.parseInt(comboBox_File.getSelectedItem().toString(), 16);
				int subFileNumber = Integer.parseInt(comboBox_SubFile.getSelectedItem().toString(),16);
				System.out.println("Input: "+input+" into "+(fileNumber+subFileNumber));
				ctr.memory.set_SRAMDIRECT((fileNumber+subFileNumber), Integer.parseInt(input));
			}
		});
		btn_InputRegister.setFont(new Font("Tahoma", Font.PLAIN, 16));
		inputPanel.add(btn_InputRegister);
		
		txtField_input = new JTextField();
		txtField_input.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtField_input.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		txtField_input.setSize(new Dimension(40, 39));
		txtField_input.setMaximumSize(new Dimension(40, 39));
		inputPanel.add(txtField_input);
		txtField_input.setColumns(8);
		
		comboBox_File = new JComboBox();
		comboBox_File.setFont(new Font("Tahoma", Font.PLAIN, 18));
		comboBox_File.setModel(new DefaultComboBoxModel(new String[] {"00", "08", "10", "18", "20", "28", "30", "38", "40", "48", "50", "58", "60", "68", "70", "78", "80", "88", "90", "98", "A0", "A8", "B0", "B8", "C0", "C8", "D0", "D8", "E0", "E8", "F0", "F8"}));
		inputPanel.add(comboBox_File);
		
		comboBox_SubFile = new JComboBox();
		comboBox_SubFile.setModel(new DefaultComboBoxModel(new String[] {"00", "01", "02", "03", "04", "05", "06", "07"}));
		comboBox_SubFile.setFont(new Font("Tahoma", Font.PLAIN, 18));
		inputPanel.add(comboBox_SubFile);
		
		JPanel panel_Memeory = new JPanel();
		verticalMemoryView.add(panel_Memeory);
		panel_Memeory.setLayout(new BorderLayout(0, 0));
		

	
		
		JLabel lblMemoryView = new JLabel("Memory View");
		panel_Memeory.add(lblMemoryView, BorderLayout.NORTH);
		lblMemoryView.setFont(new Font("Tahoma", Font.BOLD, 15));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setMinimumSize(new Dimension(200, 49));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_Memeory.add(scrollPane, BorderLayout.CENTER);
		
		tbl_memory = new DefaultTableModel();
		tbl_memory.setColumnIdentifiers(new Object[] {"","00","01","02","03","04","05","06","07"});		
		
		table_Memory = new JTable();
		table_Memory.setModel(tbl_memory);
		scrollPane.setViewportView(table_Memory);
		
		TableColumn column = null;
		for (int i = 0; i < 9; i++) {
		    column = table_Memory.getColumnModel().getColumn(i);
		    if (i < 9 ) {
		        column.setPreferredWidth(32);
		        column.setMaxWidth(32);
		        column.setResizable(false);
		    }
		}
		
		
		JPanel panel_specialreg = new JPanel();
		panel_specialreg.setPreferredSize(new Dimension(10, 150));
		panel_specialreg.setMinimumSize(new Dimension(200, 150));
		verticalMemoryView.add(panel_specialreg);
		panel_specialreg.setBorder(new LineBorder(new Color(0, 0, 0)));
		FlowLayout fl_panel_specialreg = new FlowLayout(FlowLayout.CENTER, 5, 5);
		panel_specialreg.setLayout(fl_panel_specialreg);
		
		tbl_special = new DefaultTableModel();
		tbl_special.setColumnIdentifiers(new Object[] {"Register", "Hex-Wert","Bin-Wert"});
		
		
		table_special_regs = new JTable();
		table_special_regs.setModel(tbl_special);
		
		
		JLabel lblSpecialregister = new JLabel("Special-Register");
		lblSpecialregister.setFont(new Font("Tahoma", Font.BOLD, 15));
		panel_specialreg.add(lblSpecialregister);

		panel_specialreg.add(table_special_regs);
		table_special_regs.setFont(new Font("Tahoma", Font.PLAIN, 13));
		table_special_regs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_special_regs.setCellSelectionEnabled(true);
		
		String header[] = new String[] { " ", "ProgramCounter", "ProgramCode", "LineCount","Label","MnemonicCode"};	
		tbl_code = new DefaultTableModel();
	    tbl_code.setColumnIdentifiers(header);


		
		Box verticalCodeViewer = Box.createVerticalBox();
		verticalCodeViewer.setMinimumSize(new Dimension(500, 800));
		upperArea.add(verticalCodeViewer);
		

		
		JPanel panel_Code = new JPanel();
		verticalCodeViewer.add(panel_Code);
		panel_Code.setLayout(new BorderLayout(0, 0));
		
		JLabel lblCodeViewer = new JLabel("Code Viewer");
		panel_Code.add(lblCodeViewer, BorderLayout.NORTH);
		lblCodeViewer.setAlignmentY(Component.TOP_ALIGNMENT);
		lblCodeViewer.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		JScrollPane scrollPane_3 = new JScrollPane();
		panel_Code.add(scrollPane_3, BorderLayout.CENTER);
		scrollPane_3.setViewportBorder(UIManager.getBorder("TableHeader.cellBorder"));
		table_Code = new JTable();
		table_Code.setModel(tbl_code);
		table_Code.setEnabled(false);
		for (int i = 0; i < 5; i++) {
		    column = table_Code.getColumnModel().getColumn(i);
		    if (i == 0) {
		        column.setPreferredWidth(30);
		        column.setMaxWidth(30);
		        column.setMinWidth(30);
		        column.setResizable(false);
		    }else if(i > 0 && i < 4 ) {
		        column.setPreferredWidth(80);
		        column.setMaxWidth(80);
		        column.setMinWidth(60);
		    }else if(i == 4) {
		        column.setPreferredWidth(100);
		        column.setMaxWidth(110);
		        column.setResizable(false);
		    }
		}
		
		scrollPane_3.setViewportView(table_Code);
		
		Box verticalIO = Box.createVerticalBox();
		verticalIO.setMaximumSize(new Dimension(350, 800));
		upperArea.add(verticalIO);
		
		
		JPanel panel_IO = new JPanel();
		panel_IO.setBorder(new EmptyBorder(4, 4, 4, 4));
		verticalIO.add(panel_IO);
		GridBagLayout gbl_panel_IO = new GridBagLayout();
		gbl_panel_IO.columnWidths = new int[] {130, 0};
		gbl_panel_IO.rowHeights = new int[] {30, 90, 90, 180, 30, 30, 300};
		gbl_panel_IO.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_IO.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0};
		panel_IO.setLayout(gbl_panel_IO);
		
		JPanel panel_RA = new JPanel();
		panel_RA.setOpaque(false);
		panel_RA.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_RA.setLayout(null);
		GridBagConstraints gbc_panel_RA = new GridBagConstraints();
		gbc_panel_RA.fill = GridBagConstraints.BOTH;
		gbc_panel_RA.insets = new Insets(0, 0, 5, 0);
		gbc_panel_RA.gridx = 0;
		gbc_panel_RA.gridy = 1;
		panel_IO.add(panel_RA, gbc_panel_RA);
		
		JLabel lblAnalogOut = new JLabel("Port A");
		lblAnalogOut.setBounds(6, 6, 46, 19);
		lblAnalogOut.setFont(new Font("Tahoma", Font.BOLD, 15));
		panel_RA.add(lblAnalogOut);
		
		JLabel lblBits = new JLabel("Bits");
		lblBits.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblBits.setBounds(62, 6, 46, 25);
		panel_RA.add(lblBits);
		
		JLabel lblTris = new JLabel("Tris A");
		lblTris.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTris.setBounds(62, 32, 46, 19);
		panel_RA.add(lblTris);
		
		JLabel lblPortA = new JLabel("Port A");
		lblPortA.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblPortA.setBounds(62, 53, 46, 32);
		panel_RA.add(lblPortA);
		
		JLabel lbl_ra7 = new JLabel("7");
		lbl_ra7.setBounds(118, 10, 14, 14);
		panel_RA.add(lbl_ra7);
		
		JLabel lbl_ra6 = new JLabel("6");
		lbl_ra6.setBounds(142, 10, 14, 14);
		panel_RA.add(lbl_ra6);
		
		JLabel lbl_ra5 = new JLabel("5");
		lbl_ra5.setBounds(166, 10, 14, 14);
		panel_RA.add(lbl_ra5);
		
		JLabel lbl_ra4 = new JLabel("4");
		lbl_ra4.setBounds(190, 10, 14, 14);
		panel_RA.add(lbl_ra4);
		
		JLabel lbl_ra3 = new JLabel("3");
		lbl_ra3.setBounds(214, 10, 14, 14);
		panel_RA.add(lbl_ra3);
		
		JLabel lbl_ra2 = new JLabel("2");
		lbl_ra2.setBounds(238, 10, 14, 14);
		panel_RA.add(lbl_ra2);
		
		JLabel lbl_ra1 = new JLabel("1");
		lbl_ra1.setBounds(262, 10, 14, 14);
		panel_RA.add(lbl_ra1);
		
		JLabel lbl_ra0 = new JLabel("0");
		lbl_ra0.setBounds(286, 10, 14, 14);
		panel_RA.add(lbl_ra0);
		
		lbl_ra_tris7 = new JLabel("I");
		lbl_ra_tris7.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris7.setBounds(118, 35, 14, 14);
		panel_RA.add(lbl_ra_tris7);
		
		lbl_ra_tris6 = new JLabel("I");
		lbl_ra_tris6.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris6.setBounds(142, 35, 14, 14);
		panel_RA.add(lbl_ra_tris6);
		
		lbl_ra_tris5 = new JLabel("I");
		lbl_ra_tris5.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris5.setBounds(166, 35, 14, 14);
		panel_RA.add(lbl_ra_tris5);
		
		lbl_ra_tris4 = new JLabel("I");
		lbl_ra_tris4.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris4.setBounds(190, 35, 14, 14);
		panel_RA.add(lbl_ra_tris4);
		
		lbl_ra_tris3 = new JLabel("I");
		lbl_ra_tris3.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris3.setBounds(214, 35, 14, 14);
		panel_RA.add(lbl_ra_tris3);
		
		lbl_ra_tris2 = new JLabel("I");
		lbl_ra_tris2.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris2.setBounds(238, 35, 14, 14);
		panel_RA.add(lbl_ra_tris2);
		
		lbl_ra_tris1 = new JLabel("I");
		lbl_ra_tris1.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris1.setBounds(262, 35, 14, 14);
		panel_RA.add(lbl_ra_tris1);
		
		lbl_ra_tris0 = new JLabel("I");
		lbl_ra_tris0.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris0.setBounds(286, 35, 14, 14);
		panel_RA.add(lbl_ra_tris0);
		
		rbtn_ra_7 = new JRadioButton("");
		rbtn_ra_7.setBounds(114, 53, 21, 23);
		panel_RA.add(rbtn_ra_7);
		
		rbtn_ra_6 = new JRadioButton("");
		rbtn_ra_6.setBounds(135, 53, 21, 23);
		panel_RA.add(rbtn_ra_6);
		
		rbtn_ra_5 = new JRadioButton("");
		rbtn_ra_5.setBounds(159, 53, 21, 23);
		panel_RA.add(rbtn_ra_5);
		
		rbtn_ra_4 = new JRadioButton("");
		rbtn_ra_4.setBounds(183, 53, 21, 23);
		panel_RA.add(rbtn_ra_4);
		
		rbtn_ra_3 = new JRadioButton("");
		rbtn_ra_3.setBounds(207, 53, 21, 23);
		panel_RA.add(rbtn_ra_3);
		
		rbtn_ra_2 = new JRadioButton("");
		rbtn_ra_2.setBounds(231, 53, 21, 23);
		panel_RA.add(rbtn_ra_2);
		
		rbtn_ra_1 = new JRadioButton("");
		rbtn_ra_1.setBounds(255, 53, 21, 23);
		panel_RA.add(rbtn_ra_1);
		
		rbtn_ra_0 = new JRadioButton("");
		rbtn_ra_0.setBounds(279, 53, 21, 23);
		panel_RA.add(rbtn_ra_0);
		
		JPanel panel_Quarzfrequenz = new JPanel();
		panel_Quarzfrequenz.setBorder(new LineBorder(new Color(0, 0, 0)));
		FlowLayout flowLayout_1 = (FlowLayout) panel_Quarzfrequenz.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_Quarzfrequenz = new GridBagConstraints();
		gbc_panel_Quarzfrequenz.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_Quarzfrequenz.insets = new Insets(0, 0, 5, 0);
		gbc_panel_Quarzfrequenz.fill = GridBagConstraints.BOTH;
		gbc_panel_Quarzfrequenz.gridx = 0;
		gbc_panel_Quarzfrequenz.gridy = 0;
		panel_IO.add(panel_Quarzfrequenz, gbc_panel_Quarzfrequenz);
		panel_Quarzfrequenz.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel lblQuarzFrequenz = new JLabel("Quarz Freq");
		lblQuarzFrequenz.setHorizontalAlignment(SwingConstants.LEFT);
		lblQuarzFrequenz.setPreferredSize(new Dimension(172, 20));
		lblQuarzFrequenz.setMinimumSize(new Dimension(64, 14));
		lblQuarzFrequenz.setMaximumSize(new Dimension(64, 14));
		lblQuarzFrequenz.setFont(new Font("Tahoma", Font.BOLD, 15));
		panel_Quarzfrequenz.add(lblQuarzFrequenz);
		
		comboBox_quarzFrequency = new JComboBox();
		comboBox_quarzFrequency.setPreferredSize(new Dimension(80, 20));
		comboBox_quarzFrequency.setMinimumSize(new Dimension(80, 20));
		comboBox_quarzFrequency.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		comboBox_quarzFrequency.setModel(new DefaultComboBoxModel(new String[] {"500kHz", "1MHz", "2MHz", "3MHz", "4MHz"}));
		panel_Quarzfrequenz.add(comboBox_quarzFrequency);
		
		JButton btnSetFrequency = new JButton("Set Frequency");
		btnSetFrequency.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctr.updateFrequency(comboBox_quarzFrequency.getSelectedItem().toString());
			}
		});
		panel_Quarzfrequenz.add(btnSetFrequency);
		
		JPanel panel_RB = new JPanel();
		panel_RB.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_RB.setLayout(null);
		GridBagConstraints gbc_panel_RB = new GridBagConstraints();
		gbc_panel_RB.fill = GridBagConstraints.BOTH;
		gbc_panel_RB.insets = new Insets(0, 0, 5, 0);
		gbc_panel_RB.gridx = 0;
		gbc_panel_RB.gridy = 2;
		panel_IO.add(panel_RB, gbc_panel_RB);
		
		JLabel lblAnalogIn = new JLabel("Port B");
		lblAnalogIn.setBounds(6, 7, 46, 19);
		lblAnalogIn.setFont(new Font("Tahoma", Font.BOLD, 15));
		panel_RB.add(lblAnalogIn);
		
		JLabel label = new JLabel("Bits");
		label.setFont(new Font("Tahoma", Font.PLAIN, 12));
		label.setBounds(58, 0, 46, 25);
		panel_RB.add(label);
		
		JLabel lblTrisB = new JLabel("Tris B");
		lblTrisB.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTrisB.setBounds(58, 26, 46, 19);
		panel_RB.add(lblTrisB);
		
		JLabel lblPortB = new JLabel("Port B");
		lblPortB.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblPortB.setBounds(58, 47, 46, 32);
		panel_RB.add(lblPortB);
		
		JLabel lbl_rb7 = new JLabel("7");
		lbl_rb7.setBounds(114, 4, 14, 14);
		panel_RB.add(lbl_rb7);
		
		JLabel lbl_rb6 = new JLabel("6");
		lbl_rb6.setBounds(138, 4, 14, 14);
		panel_RB.add(lbl_rb6);
		
		JLabel lbl_rb5 = new JLabel("5");
		lbl_rb5.setBounds(162, 4, 14, 14);
		panel_RB.add(lbl_rb5);
		
		JLabel lbl_rb4 = new JLabel("4");
		lbl_rb4.setBounds(186, 4, 14, 14);
		panel_RB.add(lbl_rb4);
		
		JLabel lbl_rb3 = new JLabel("3");
		lbl_rb3.setBounds(210, 4, 14, 14);
		panel_RB.add(lbl_rb3);
		
		JLabel lbl_rb2 = new JLabel("2");
		lbl_rb2.setBounds(234, 4, 14, 14);
		panel_RB.add(lbl_rb2);
		
		JLabel lbl_rb1 = new JLabel("1");
		lbl_rb1.setBounds(258, 4, 14, 14);
		panel_RB.add(lbl_rb1);
		
		JLabel lbl_rb0 = new JLabel("0");
		lbl_rb0.setBounds(282, 4, 14, 14);
		panel_RB.add(lbl_rb0);
		
		lbl_rb_tris7 = new JLabel("I");
		lbl_rb_tris7.setAlignmentX(0.5f);
		lbl_rb_tris7.setBounds(282, 29, 14, 14);
		panel_RB.add(lbl_rb_tris7);
		
		lbl_rb_tris6 = new JLabel("I");
		lbl_rb_tris6.setAlignmentX(0.5f);
		lbl_rb_tris6.setBounds(258, 29, 14, 14);
		panel_RB.add(lbl_rb_tris6);
		
		lbl_rb_tris5 = new JLabel("I");
		lbl_rb_tris5.setAlignmentX(0.5f);
		lbl_rb_tris5.setBounds(234, 29, 14, 14);
		panel_RB.add(lbl_rb_tris5);
		
		lbl_rb_tris4 = new JLabel("I");
		lbl_rb_tris4.setAlignmentX(0.5f);
		lbl_rb_tris4.setBounds(210, 29, 14, 14);
		panel_RB.add(lbl_rb_tris4);
		
		lbl_rb_tris3 = new JLabel("I");
		lbl_rb_tris3.setAlignmentX(0.5f);
		lbl_rb_tris3.setBounds(186, 29, 14, 14);
		panel_RB.add(lbl_rb_tris3);
		
		lbl_rb_tris2 = new JLabel("I");
		lbl_rb_tris2.setAlignmentX(0.5f);
		lbl_rb_tris2.setBounds(162, 29, 14, 14);
		panel_RB.add(lbl_rb_tris2);
		
		lbl_rb_tris1 = new JLabel("I");
		lbl_rb_tris1.setAlignmentX(0.5f);
		lbl_rb_tris1.setBounds(138, 29, 14, 14);
		panel_RB.add(lbl_rb_tris1);
		
		lbl_rb_tris0 = new JLabel("I");
		lbl_rb_tris0.setAlignmentX(0.5f);
		lbl_rb_tris0.setBounds(114, 29, 14, 14);
		panel_RB.add(lbl_rb_tris0);
		
		rbtn_rb_7 = new JRadioButton("");
		rbtn_rb_7.setBounds(110, 47, 21, 23);
		panel_RB.add(rbtn_rb_7);
		
		rbtn_rb_6 = new JRadioButton("");
		rbtn_rb_6.setBounds(131, 47, 21, 23);
		panel_RB.add(rbtn_rb_6);
		
		rbtn_rb_5 = new JRadioButton("");
		rbtn_rb_5.setBounds(155, 47, 21, 23);
		panel_RB.add(rbtn_rb_5);
		
		rbtn_rb_4 = new JRadioButton("");
		rbtn_rb_4.setBounds(179, 47, 21, 23);
		panel_RB.add(rbtn_rb_4);
		
		rbtn_rb_3 = new JRadioButton("");
		rbtn_rb_3.setBounds(203, 47, 21, 23);
		panel_RB.add(rbtn_rb_3);
		
		rbtn_rb_2 = new JRadioButton("");
		rbtn_rb_2.setBounds(227, 47, 21, 23);
		panel_RB.add(rbtn_rb_2);
		
		rbtn_rb_1 = new JRadioButton("");
		rbtn_rb_1.setBounds(251, 47, 21, 23);
		panel_RB.add(rbtn_rb_1);
		
		rbtn_rb_0 = new JRadioButton("");
		rbtn_rb_0.setBounds(275, 47, 21, 23);
		panel_RB.add(rbtn_rb_0);
		
		JPanel panel_7Segment = new JPanel();
		panel_7Segment.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_7Segment = new GridBagConstraints();
		gbc_panel_7Segment.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_7Segment.insets = new Insets(0, 0, 5, 0);
		gbc_panel_7Segment.gridx = 0;
		gbc_panel_7Segment.gridy = 3;
		panel_IO.add(panel_7Segment, gbc_panel_7Segment);
		panel_7Segment.setLayout(new BorderLayout(0, 0));
		
		JLabel lblSegment = new JLabel("  7 - Segment  ");
		panel_7Segment.add(lblSegment, BorderLayout.NORTH);
		lblSegment.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		panel_segmentCanvas = new MyPanel();
		panel_segmentCanvas.setMaximumSize(new Dimension(310, 105));
		panel_segmentCanvas.setPreferredSize(new Dimension(310, 105));
		panel_7Segment.add(panel_segmentCanvas, BorderLayout.CENTER);
		panel_segmentCanvas.setOpaque(false);
		panel_segmentCanvas.setBackground(Color.WHITE);
		panel_segmentCanvas.setLayout(null);
		
		JPanel panel_segmentOptions = new JPanel();
		panel_7Segment.add(panel_segmentOptions, BorderLayout.SOUTH);
		panel_segmentOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setModel(new DefaultComboBoxModel(new String[] {"Port 1", "Port 2", "Port 3", "Port 4"}));
		panel_segmentOptions.add(comboBox_2);
		
		JComboBox comboBox_3 = new JComboBox();
		comboBox_3.setModel(new DefaultComboBoxModel(new String[] {"Port 1", "Port 2", "Port 3", "Port 4"}));
		panel_segmentOptions.add(comboBox_3);
		
		JButton btnActive = new JButton("Active");
		panel_segmentOptions.add(btnActive);
		frmMicrocontrollerSimulator.getContentPane().setLayout(new BoxLayout(frmMicrocontrollerSimulator.getContentPane(), BoxLayout.X_AXIS));
		
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		menuBar.setMargin(new Insets(0, 0, 5, 0));
		JMenu file_menu,debug_menu,run_menu;
		
		frmMicrocontrollerSimulator.setJMenuBar(menuBar);
		
		//Menu for Run actions
		run_menu = new JMenu("Run");
		run_menu.setMnemonic(KeyEvent.VK_A);
		run_menu.getAccessibleContext().setAccessibleDescription(
		        "Start and Stop Application");
		
		file_menu = new JMenu("File");
		file_menu.setMnemonic(KeyEvent.VK_A);
		file_menu.getAccessibleContext().setAccessibleDescription("Open and Saving Files");
		menuBar.add(file_menu);
		
		JButton btnLoadFile = new JButton("Load LST File");
		btnLoadFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnLoadFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Create a file chooser
				final JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("LST File","lst");
				fc.addChoosableFileFilter(filter);
				//In response to a button click:
				int returnVal = fc.showOpenDialog(btnLoadFile);
				 if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
			            try {
							ctr.loadFile(file);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			            //This is where a real application would open the file.
			            System.out.println("Opening: " + file.getName());
			        } else {
			            System.out.println("Open command cancelled by user.");
			        }
				
			}
		});
		file_menu.add(btnLoadFile);
		
		JButton btnSaveFile = new JButton("Save LST File");
		btnSaveFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnSaveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc_save = new JFileChooser();
				fc_save.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("LST File","lst");
				fc_save.addChoosableFileFilter(filter);
				fc_save.setDialogTitle("Specify a file to save");   
				File fileToSave; 
				int userSelection = fc_save.showSaveDialog(btnSaveFile);
				 
				if (userSelection == JFileChooser.APPROVE_OPTION) {
				    fileToSave = fc_save.getSelectedFile();
				    
				    ctr.saveLSTFile(fileToSave);
				    
				    //ctr.saveSRCFile(fileToSave);
				    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
				}
				
			}
		});
		file_menu.add(btnSaveFile);
		menuBar.add(run_menu);
		
		
		
		JButton btnStopSimulation = new JButton("Stop Simulation");
		btnStopSimulation.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnStopSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctr.stopSimu();
			}
		});
		
		JButton btnStartSimulation = new JButton("Start Simulation");
		btnStartSimulation.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnStartSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctr.startSimu(false);
			}
		});
		run_menu.add(btnStartSimulation);
		run_menu.add(btnStopSimulation);
		
		
		debug_menu = new JMenu("Debug");
		debug_menu.setMnemonic(KeyEvent.VK_A);
		debug_menu.getAccessibleContext().setAccessibleDescription("Compile and Debug");
		menuBar.add(debug_menu);
		
		JButton btnCompile = new JButton("Compile");
		btnCompile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctr.compileCode();
			}
		});
		btnCompile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		debug_menu.add(btnCompile);
		
		JButton btnDebuggerStarten = new JButton("Debugger starten");
		btnDebuggerStarten.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnDebuggerStarten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctr.startSimu(true);
			}
		});
		debug_menu.add(btnDebuggerStarten);
		
		JButton btnDebuggerStoppen = new JButton("Debugger stoppen");
		btnDebuggerStoppen.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnDebuggerStoppen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctr.stopSimu();
			}
		});
		debug_menu.add(btnDebuggerStoppen);
		
		JButton button = new JButton("->");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctr.continueDebugStep();
			}
		});
		debug_menu.add(button);
		button.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JButton button_1 = new JButton("<-");
		debug_menu.add(button_1);
		button_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JMenu mnSimulator = new JMenu("Simulator");
		menuBar.add(mnSimulator);
		
		JButton btnOpenMnemonic = new JButton("Open Mnemonic");
		btnOpenMnemonic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctr.openMnemonicView();
			}
		});
		mnSimulator.add(btnOpenMnemonic);
		
		JButton btnInfo = new JButton("Info");
		mnSimulator.add(btnInfo);
		
		JButton btnHelp = new JButton("Help");
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openHelp();
			}
		});
		mnSimulator.add(btnHelp);
		

	}
	  public void SetData(Object obj, int row_index, int col_index){
		  table_Memory.getModel().setValueAt(obj,row_index,col_index);
		  table_Memory.setModel(tbl_memory);
		  frmMicrocontrollerSimulator.repaint();
	  }
	  public String getData(int row, int col) 
	  {
		  String out =  (String) table_Memory.getModel().getValueAt(row, col);
		  return out;
	  }
	  public void setSegment(int c1, int c2, int c3, int c4) 
	  {
		  this.panel_segmentCanvas.setChars(c1,c2,c3,c4);
		  this.panel_segmentCanvas.repaint();
	  }
	  public void setSpecialData(Object obj,int row_index,int col_index) 
	  {
		  table_special_regs.getModel().setValueAt(obj, row_index, col_index);
	  }
	  public void openHelp() {
			// TODO Auto-generated method stub

			try {
			  File helpFile = new File("out/html/annotated.html");
			  Desktop.getDesktop().browse(helpFile.toURI());
			  //help.setPage(helpFile.toURI().toURL());
			}catch (IOException e) {
				ctr.showError("File Open Error", e.getMessage());
			} 
	  }
	  protected int getPortA() 
	  {
		  int ra = 0;
		  if(this.rbtn_ra_7.isSelected()) 
		  {
			  ra = ra + 0x80;
		  }
		  if(this.rbtn_ra_6.isSelected()) 
		  {
			  ra = ra + 0x40;
		  }
		  if(this.rbtn_ra_5.isSelected()) 
		  {
			  ra = ra + 0x20;
		  }
		  if(this.rbtn_ra_4.isSelected()) 
		  {
			  ra = ra + 0x10;
		  }
		  if(this.rbtn_ra_3.isSelected()) 
		  {
			  ra = ra + 0x08;
		  }
		  if(this.rbtn_ra_2.isSelected()) 
		  {
			  ra = ra + 0x04;
		  }
		  if(this.rbtn_ra_1.isSelected()) 
		  {
			  ra = ra + 0x02;
		  }
		  if(this.rbtn_ra_0.isSelected()) 
		  {
			  ra = ra + 0x01;
		  }
		  return ra;
	  }
	  protected void setPortA(int ra) 
	  {
		  if((ra & 0x80)>> 7  == 1 ) 
		  {
			  this.rbtn_ra_7.setSelected(true);
		  }else 
		  {
			  this.rbtn_ra_7.setSelected(false);
		  }
		  if((ra & 0x40)>> 6 == 1 ) 
		  {
			  this.rbtn_ra_6.setSelected(true);
		  }else 
		  {
			  this.rbtn_ra_6.setSelected(false);
		  }
		  if((ra & 0x20)>> 5 == 1 ) 
		  {
			  this.rbtn_ra_5.setSelected(true);
		  }else 
		  {
			  this.rbtn_ra_5.setSelected(false);
		  }
		  if((ra & 0x10)>> 4 == 1  ) 
		  {
			  this.rbtn_ra_4.setSelected(true);
		  }else 
		  {
			  this.rbtn_ra_4.setSelected(false);
		  }
		  if((ra & 0x08)>> 3 == 1 ) 
		  {
			  this.rbtn_ra_3.setSelected(true);
		  }else 
		  {
			  this.rbtn_ra_3.setSelected(false);
		  }
		  if((ra & 0x04)>> 2 == 1 ) 
		  {
			  this.rbtn_ra_2.setSelected(true);
		  }else 
		  {
			  this.rbtn_ra_2.setSelected(false);
		  }
		  if((ra & 0x02)>> 1 == 1  ) 
		  {
			  this.rbtn_ra_1.setSelected(true);
		  }else 
		  {
			  this.rbtn_ra_1.setSelected(false);
		  }
		  if((ra & 0x01) == 1  ) 
		  {
			  this.rbtn_ra_0.setSelected(true);
		  }else 
		  {
			  this.rbtn_ra_0.setSelected(false);
		  }
	  }
	  protected int getPortB() 
	  {
		  int rb = 0;
		  
		  if(this.rbtn_rb_7.isSelected()) 
		  {
			  rb = rb + 0x80;
		  }
		  if(this.rbtn_rb_6.isSelected()) 
		  {
			  rb = rb + 0x40;
		  }
		  if(this.rbtn_rb_5.isSelected()) 
		  {
			  rb = rb + 0x20;
		  }
		  if(this.rbtn_rb_4.isSelected()) 
		  {
			  rb = rb + 0x10;
		  }
		  if(this.rbtn_rb_3.isSelected()) 
		  {
			  rb = rb + 0x08;
		  }
		  if(this.rbtn_rb_2.isSelected()) 
		  {
			  rb = rb + 0x04;
		  }
		  if(this.rbtn_rb_1.isSelected()) 
		  {
			  rb = rb + 0x02;
		  }
		  if(this.rbtn_rb_0.isSelected()) 
		  {
			  rb = rb + 0x01;
		  }
		  return rb;
	  }
	  protected void setPortB(int rb) 
	  {
		  if((rb & 0x80)>> 7  == 1) 
		  {
			  this.rbtn_rb_7.setSelected(true);
		  }else
		  {
			this.rbtn_rb_7.setSelected(false);
		  }

		  
		  if((rb & 0x40)>> 6 == 1) 
		  {
			  this.rbtn_rb_6.setSelected(true);
		  }else 
		  {
			  this.rbtn_rb_6.setSelected(false);
		  }
		  
		  if((rb & 0x20)>> 5 == 1 ) 
		  {
			  this.rbtn_rb_5.setSelected(true);
		  }else 
		  {
			  this.rbtn_rb_5.setSelected(false);
		  }
		  
		  if((rb & 0x10)>> 4 == 1 ) 
		  {
			  this.rbtn_rb_4.setSelected(true);
		  }else 
		  {
			  this.rbtn_rb_4.setSelected(false);
		  }
		  
		  if((rb & 0x08)>> 3 == 1) 
		  {
			  this.rbtn_rb_3.setSelected(true);
		  }else 
		  {
			  this.rbtn_rb_3.setSelected(false);
		  }
		  
		  if((rb & 0x04)>> 2 == 1) 
		  {
			  this.rbtn_rb_2.setSelected(true);
		  }else 
		  {
			  this.rbtn_rb_2.setSelected(false);
		  }
		  
		  if((rb & 0x02)>> 1 == 1) 
		  {
			  this.rbtn_rb_1.setSelected(true);
		  }else 
		  {
			  this.rbtn_rb_1.setSelected(false);
		  }
		  
		  if((rb & 0x01) == 1) 
		  {
			  this.rbtn_rb_0.setSelected(true);
		  }else 
		  {
			  this.rbtn_rb_0.setSelected(false);
		  }
	  }
	  protected void setTrisA(int tris) 
	  {
		  if((tris & 0x80)>> 7  == 1) 
		  {
			  lbl_ra_tris7.setText("I");
			  this.rbtn_ra_7.setEnabled(true);
		  }else 
		  {
			  lbl_ra_tris7.setText("O");
			  this.rbtn_ra_7.setEnabled(false);
		  }
		  if((tris & 0x40)>> 6 == 1) 
		  {
			  lbl_ra_tris6.setText("I");
			  this.rbtn_ra_6.setEnabled(true);
		  }else 
		  {
			  lbl_ra_tris6.setText("O");
			  this.rbtn_ra_6.setEnabled(false);
		  }
		  if((tris & 0x20)>> 5 == 1) 
		  {
			  lbl_ra_tris5.setText("I");
			  this.rbtn_ra_5.setEnabled(true);
		  }else 
		  {
			  lbl_ra_tris5.setText("O");
			  this.rbtn_ra_5.setEnabled(false);
		  }
		  if((tris & 0x10)>> 4 == 1) 
		  {
			  lbl_ra_tris4.setText("I");
			  this.rbtn_ra_4.setEnabled(true);
		  }else 
		  {
			  lbl_ra_tris4.setText("O");
			  this.rbtn_ra_4.setEnabled(false);
		  }
		  if((tris & 0x08)>> 3 == 1) 
		  {
			  lbl_ra_tris3.setText("I");
			  this.rbtn_ra_3.setEnabled(true);
		  }else 
		  {
			  lbl_ra_tris3.setText("O");
			  this.rbtn_ra_3.setEnabled(false);
		  }
		  if((tris & 0x04)>> 2 == 1) 
		  {
			  lbl_ra_tris2.setText("I");
			  this.rbtn_ra_2.setEnabled(true);
		  }else 
		  {
			  lbl_ra_tris2.setText("O");
			  this.rbtn_ra_2.setEnabled(false);
		  }
		  if((tris & 0x02)>> 1 == 1) 
		  {
			  lbl_ra_tris1.setText("I");
			  this.rbtn_ra_1.setEnabled(true);
		  }else 
		  {
			  lbl_ra_tris1.setText("O");
			  this.rbtn_ra_1.setEnabled(false);
		  }
		  if((tris & 0x01) == 1) 
		  {
			  lbl_ra_tris0.setText("I");
			  this.rbtn_ra_0.setEnabled(true);
		  }else 
		  {
			  lbl_ra_tris0.setText("O");
			  this.rbtn_ra_0.setEnabled(false);
		  }
	  }
	  protected void setTrisB(int tris) 
	  {
		  if((tris & 0x80)  >> 7 == 1) 
		  {
			  lbl_rb_tris7.setText("I");
			  this.rbtn_rb_7.setEnabled(true);
		  }else 
		  {
			  lbl_rb_tris7.setText("O");
			  this.rbtn_rb_7.setEnabled(false);
		  }
		  if((tris & 0x40) >> 6 == 1) 
		  {
			  lbl_rb_tris6.setText("I");
			  this.rbtn_rb_6.setEnabled(true);
		  }else 
		  {
			  lbl_rb_tris6.setText("O");
			  this.rbtn_rb_6.setEnabled(false);
		  }
		  if((tris & 0x20) >> 5 == 1) 
		  {
			  lbl_rb_tris5.setText("I");
			  this.rbtn_rb_5.setEnabled(true);
		  }else 
		  {
			  lbl_rb_tris5.setText("O");
			  this.rbtn_rb_5.setEnabled(false);
		  }
		  if((tris & 0x10) >> 4 == 1) 
		  {
			  lbl_rb_tris4.setText("I");
			  this.rbtn_rb_4.setEnabled(true);
		  }else 
		  {
			  lbl_rb_tris4.setText("O");
			  this.rbtn_rb_4.setEnabled(false);
		  }
		  if((tris & 0x08) >> 3 == 1) 
		  {
			  lbl_rb_tris3.setText("I");
			  this.rbtn_rb_3.setEnabled(true);
		  }else 
		  {
			  lbl_rb_tris3.setText("O");
			  this.rbtn_rb_3.setEnabled(false);
		  }
		  if((tris & 0x04) >> 2 == 1) 
		  {
			  lbl_rb_tris2.setText("I");
			  this.rbtn_rb_2.setEnabled(true);
		  }else 
		  {
			  lbl_rb_tris2.setText("O");
			  this.rbtn_rb_2.setEnabled(false);
		  }
		  if((tris & 0x02) >> 1 == 1) 
		  {
			  lbl_rb_tris1.setText("I");
			  this.rbtn_rb_1.setEnabled(true);
		  }else 
		  {
			  lbl_rb_tris1.setText("O");
			  this.rbtn_rb_1.setEnabled(false);
		  }
		  if((tris & 0x01) == 1) 
		  {
			  lbl_rb_tris0.setText("I");
			  this.rbtn_rb_0.setEnabled(true);
		  }else 
		  {
			  lbl_rb_tris0.setText("O");
			  this.rbtn_rb_0.setEnabled(false);
		  }
	  }
	  protected void highlightRow(int row) 
	  {
		  table_Code.removeRowSelectionInterval(0, this.table_Code.getRowCount() - 1);
		  table_Code.addRowSelectionInterval(row, row);
	  }
}
