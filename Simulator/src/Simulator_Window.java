import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;

import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.JMenuBar;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.border.LineBorder;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.SwingConstants;
import javax.swing.Box;
import javax.swing.border.EmptyBorder;
import javax.swing.ListSelectionModel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class Simulator_Window {

	private JFrame frmMicrocontrollerSimulator;
	private Controller ctr;
    public JTextArea txtrMovxe;
	public TableModel dataModel;
	public MyPanel panel_4;
	public JTable table_Code;
	public DefaultTableModel tbl_code;
	public DefaultTableModel tbl_memory;
	public DefaultTableModel tbl_special;
	public DefaultTableModel tbl_status;
	public DefaultTableModel tbl_pcl;
	public JTable table_Memory;
	public JTable table_special_regs;
	public JTextArea txtArea_Console;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
		frmMicrocontrollerSimulator.getContentPane().add(verticalBox);
		
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setAlignmentY(0.5f);
		verticalBox.add(horizontalBox);
		
		Box verticalBox_1 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_1);
		
		JPanel panel_Memeory = new JPanel();
		verticalBox_1.add(panel_Memeory);
		panel_Memeory.setLayout(new BorderLayout(0, 0));
		

	
		
		JLabel lblMemoryView = new JLabel("Memory View");
		panel_Memeory.add(lblMemoryView, BorderLayout.NORTH);
		lblMemoryView.setFont(new Font("Tahoma", Font.BOLD, 15));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_Memeory.add(scrollPane, BorderLayout.CENTER);
		
		tbl_memory = new DefaultTableModel();
		tbl_memory.setColumnIdentifiers(new Object[] {"","00","01","02","03","04","05","06","07"});		
		
		table_Memory = new JTable();
		table_Memory.setModel(tbl_memory);
		scrollPane.setViewportView(table_Memory);
		
		String header[] = new String[] { " ", " ", "Adresse","Code","Label"};	
		tbl_code = new DefaultTableModel();
	    tbl_code.setColumnIdentifiers(header);

		TableColumn column = null;
		for (int i = 0; i < 9; i++) {
		    column = table_Memory.getColumnModel().getColumn(i);
		    if (i < 9 ) {
		        column.setPreferredWidth(32);
		        column.setMaxWidth(32);
		        column.setResizable(false);
		    }
		}

		Box verticalBox_2 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_2);
		
		JPanel panel_Mnemonic = new JPanel();
		panel_Mnemonic.setMinimumSize(new Dimension(800,400));
		verticalBox_2.add(panel_Mnemonic);
		panel_Mnemonic.setLayout(new BorderLayout(0, 0));
		
		JLabel lblMnemonicEditor = new JLabel("Mnemonic Editor");
		panel_Mnemonic.add(lblMnemonicEditor, BorderLayout.NORTH);
		lblMnemonicEditor.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		txtrMovxe = new JTextArea();
		panel_Mnemonic.add(txtrMovxe, BorderLayout.CENTER);
		txtrMovxe.setFont(new Font("Monospaced", Font.PLAIN, 18));
		txtrMovxe.setText("             MOVF 0x1e,0;\r\n             GOTO m2;\r\nm1:          ADDWF 0x3e,1;\r\nm2:          NOP;\r\n             BCF 0x1e,5;");
		
		Box verticalBox_4 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_4);
		

		
		JPanel panel_Code = new JPanel();
		verticalBox_4.add(panel_Code);
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
		for (int i = 0; i < 4; i++) {
		    column = table_Code.getColumnModel().getColumn(i);
		    if (i < 2 ) {
		        column.setPreferredWidth(32);
		        column.setMaxWidth(32);
		        column.setMinWidth(32);
		        column.setResizable(false);
		    }else if(i == 2) 
		    {
		        column.setPreferredWidth(60);
		        column.setMaxWidth(60);
		        column.setResizable(false);
		    }
		}
		scrollPane_3.setViewportView(table_Code);
		
		Box verticalBox_3 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_3);
		

		
		JPanel panel_IO = new JPanel();
		panel_IO.setBorder(new EmptyBorder(4, 4, 4, 4));
		verticalBox_3.add(panel_IO);
		GridBagLayout gbl_panel_IO = new GridBagLayout();
		gbl_panel_IO.columnWidths = new int[] {130, 0};
		gbl_panel_IO.rowHeights = new int[] {30, 50, 30, 50, 30, 165, 30, 0, 120, 10};
		gbl_panel_IO.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_IO.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		panel_IO.setLayout(gbl_panel_IO);
		
		JPanel panel_AnalogOut = new JPanel();
		panel_AnalogOut.setBorder(new LineBorder(new Color(0, 0, 0)));
		FlowLayout fl_panel_AnalogOut = (FlowLayout) panel_AnalogOut.getLayout();
		fl_panel_AnalogOut.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_AnalogOut = new GridBagConstraints();
		gbc_panel_AnalogOut.fill = GridBagConstraints.BOTH;
		gbc_panel_AnalogOut.insets = new Insets(0, 0, 5, 0);
		gbc_panel_AnalogOut.gridx = 0;
		gbc_panel_AnalogOut.gridy = 1;
		panel_IO.add(panel_AnalogOut, gbc_panel_AnalogOut);
		
		JLabel lblAnalogOut = new JLabel("Analog OUT   ");
		lblAnalogOut.setFont(new Font("Tahoma", Font.BOLD, 15));
		panel_AnalogOut.add(lblAnalogOut);
		
		JRadioButton radioButton0 = new JRadioButton("");
		radioButton0.setEnabled(false);
		radioButton0.setForeground(Color.RED);
		panel_AnalogOut.add(radioButton0);
		
		JRadioButton radioButton1 = new JRadioButton("");
		radioButton1.setEnabled(false);
		radioButton1.setForeground(Color.RED);
		panel_AnalogOut.add(radioButton1);
		
		JRadioButton radioButton2 = new JRadioButton("");
		radioButton2.setEnabled(false);
		radioButton2.setForeground(Color.RED);
		panel_AnalogOut.add(radioButton2);
		
		JRadioButton radioButton3 = new JRadioButton("");
		radioButton3.setEnabled(false);
		radioButton3.setForeground(Color.RED);
		panel_AnalogOut.add(radioButton3);
		
		JRadioButton radioButton4 = new JRadioButton("");
		radioButton4.setEnabled(false);
		radioButton4.setForeground(Color.RED);
		panel_AnalogOut.add(radioButton4);
		
		JRadioButton radioButton5 = new JRadioButton("");
		radioButton5.setEnabled(false);
		radioButton5.setForeground(Color.RED);
		panel_AnalogOut.add(radioButton5);
		
		JRadioButton radioButton6 = new JRadioButton("");
		radioButton6.setEnabled(false);
		radioButton6.setForeground(Color.RED);
		panel_AnalogOut.add(radioButton6);
		
		JRadioButton radioButton7 = new JRadioButton("");
		radioButton7.setSelected(true);
		radioButton7.setEnabled(false);
		radioButton7.setForeground(Color.RED);
		panel_AnalogOut.add(radioButton7);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Port 1", "Port 2", "Port 3", "Port 4"}));
		panel_AnalogOut.add(comboBox);
		
		JPanel panel_AnalogIn = new JPanel();
		panel_AnalogIn.setBorder(new LineBorder(new Color(0, 0, 0)));
		FlowLayout fl_panel_AnalogIn = (FlowLayout) panel_AnalogIn.getLayout();
		fl_panel_AnalogIn.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_AnalogIn = new GridBagConstraints();
		gbc_panel_AnalogIn.fill = GridBagConstraints.BOTH;
		gbc_panel_AnalogIn.insets = new Insets(0, 0, 5, 0);
		gbc_panel_AnalogIn.gridx = 0;
		gbc_panel_AnalogIn.gridy = 3;
		panel_IO.add(panel_AnalogIn, gbc_panel_AnalogIn);
		
		JLabel lblAnalogIn = new JLabel("Analog IN      ");
		lblAnalogIn.setFont(new Font("Tahoma", Font.BOLD, 15));
		panel_AnalogIn.add(lblAnalogIn);
		
		JRadioButton radioButton = new JRadioButton("");
		panel_AnalogIn.add(radioButton);
		
		JRadioButton radioButton_1 = new JRadioButton("");
		panel_AnalogIn.add(radioButton_1);
		
		JRadioButton radioButton_2 = new JRadioButton("");
		panel_AnalogIn.add(radioButton_2);
		
		JRadioButton radioButton_3 = new JRadioButton("");
		panel_AnalogIn.add(radioButton_3);
		
		JRadioButton radioButton_4 = new JRadioButton("");
		panel_AnalogIn.add(radioButton_4);
		
		JRadioButton radioButton_5 = new JRadioButton("");
		panel_AnalogIn.add(radioButton_5);
		
		JRadioButton radioButton_6 = new JRadioButton("");
		panel_AnalogIn.add(radioButton_6);
		
		JRadioButton radioButton_7 = new JRadioButton("");
		panel_AnalogIn.add(radioButton_7);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"Port 1", "Port 2", "Port 3", "Port 4"}));
		panel_AnalogIn.add(comboBox_1);
		
		JPanel panel_7Segment = new JPanel();
		panel_7Segment.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_7Segment = new GridBagConstraints();
		gbc_panel_7Segment.fill = GridBagConstraints.BOTH;
		gbc_panel_7Segment.insets = new Insets(0, 0, 5, 0);
		gbc_panel_7Segment.gridx = 0;
		gbc_panel_7Segment.gridy = 5;
		panel_IO.add(panel_7Segment, gbc_panel_7Segment);
		panel_7Segment.setLayout(new BorderLayout(0, 0));
		
		JLabel lblSegment = new JLabel("  7 - Segment  ");
		panel_7Segment.add(lblSegment, BorderLayout.NORTH);
		lblSegment.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		JPanel panel_5 = new JPanel();
		panel_7Segment.add(panel_5, BorderLayout.SOUTH);
		panel_5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setModel(new DefaultComboBoxModel(new String[] {"Port 1", "Port 2", "Port 3", "Port 4"}));
		panel_5.add(comboBox_2);
		
		JComboBox comboBox_3 = new JComboBox();
		comboBox_3.setModel(new DefaultComboBoxModel(new String[] {"Port 1", "Port 2", "Port 3", "Port 4"}));
		panel_5.add(comboBox_3);
		
		JButton btnActive = new JButton("Active");
		panel_5.add(btnActive);
		
		panel_4 = new MyPanel();
		panel_7Segment.add(panel_4, BorderLayout.CENTER);
		panel_4.setBackground(Color.WHITE);
		
		JPanel panel_specialreg = new JPanel();
		panel_specialreg.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_specialreg = new GridBagConstraints();
		gbc_panel_specialreg.fill = GridBagConstraints.BOTH;
		gbc_panel_specialreg.gridx = 0;
		gbc_panel_specialreg.gridy = 7;
		panel_IO.add(panel_specialreg, gbc_panel_specialreg);
		FlowLayout fl_panel_specialreg = new FlowLayout(FlowLayout.CENTER, 5, 5);
		panel_specialreg.setLayout(fl_panel_specialreg);
		
		
		tbl_special = new DefaultTableModel();
	    String[][] rowData = {
	    	    { "W-Reg", "00","00000000" }, { "FSR", "00","00000000" }, { "PCL", "30","00110000" },
	    	    { "PCLATH", "00","00000000" }, {"PC", "0030","00000000"} ,{ "Status", "C0","10100000" }
	    	    };

	    	    String[] columnNames =  {
	    	      "Register", "Hex-Wert","Bin-Wert"
	    	    };
		
		JLabel lblSpecialregister = new JLabel("Special-Register");
		lblSpecialregister.setFont(new Font("Tahoma", Font.BOLD, 15));
		panel_specialreg.add(lblSpecialregister);
		table_special_regs = new JTable(rowData,columnNames);
		panel_specialreg.add(table_special_regs);
		//table_special_regs.setModel(tbl_special);
		table_special_regs.setFont(new Font("Tahoma", Font.PLAIN, 13));
		table_special_regs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_special_regs.setCellSelectionEnabled(true);
		
		
		//table_special_regs = new JTable(rowData_status,columnNames_status);
		//table_status_reg.setModel(tbl_status);
		//panel_specialreg.add(table_status_regs);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		horizontalBox_1.setAlignmentY(Component.CENTER_ALIGNMENT);
		verticalBox.add(horizontalBox_1);
		
		Box verticalBox_5 = Box.createVerticalBox();
		horizontalBox_1.add(verticalBox_5);
		frmMicrocontrollerSimulator.getContentPane().setLayout(new BoxLayout(frmMicrocontrollerSimulator.getContentPane(), BoxLayout.X_AXIS));
		
		JPanel panel_Console = new JPanel();
		verticalBox_5.add(panel_Console);
		panel_Console.setLayout(new BorderLayout(0, 0));
		
		JLabel lblConsole = new JLabel("Console");
		panel_Console.add(lblConsole, BorderLayout.NORTH);
		lblConsole.setHorizontalAlignment(SwingConstants.LEFT);
		lblConsole.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		txtArea_Console = new JTextArea();
		txtArea_Console.setEditable(false);
		txtArea_Console.setForeground(Color.BLACK);
		txtArea_Console.setText("");
		txtArea_Console.setFont(new Font("Calibri", Font.PLAIN, 15));
		txtArea_Console.setRows(10);
		panel_Console.add(txtArea_Console, BorderLayout.CENTER);
		
		
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
				ctr.startSimu();
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
		debug_menu.add(btnDebuggerStarten);
		
		JButton button = new JButton("->");
		button.setFont(new Font("Tahoma", Font.PLAIN, 13));
		menuBar.add(button);
		
		JButton button_1 = new JButton("<-");
		button_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		menuBar.add(button_1);
		
		JButton btnDebuggerStoppen = new JButton("Debugger stoppen");
		btnDebuggerStoppen.setFont(new Font("Tahoma", Font.PLAIN, 13));
		debug_menu.add(btnDebuggerStoppen);
		
		file_menu = new JMenu("File");
		file_menu.setMnemonic(KeyEvent.VK_A);
		file_menu.getAccessibleContext().setAccessibleDescription("Open and Saving Files");
		menuBar.add(file_menu);
		
		JButton btnLoadFile = new JButton("Load File");
		btnLoadFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnLoadFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Create a file chooser
				final JFileChooser fc = new JFileChooser();
				//In response to a button click:
				int returnVal = fc.showOpenDialog(btnLoadFile);
				 if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
			            ctr.loadFile(file);
			            //This is where a real application would open the file.
			            System.out.println("Opening: " + file.getName());
			        } else {
			            System.out.println("Open command cancelled by user.");
			        }
				
			}
		});
		file_menu.add(btnLoadFile);
		
		JButton btnSaveFile = new JButton("Save File");
		btnSaveFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnSaveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		file_menu.add(btnSaveFile);
		

	}
	  public void SetData(Object obj, int row_index, int col_index){
		  table_Memory.getModel().setValueAt(obj,row_index,col_index);
		  System.out.println("Value is added");
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
		  this.panel_4.setChars(c1,c2,c3,c4);
		  this.panel_4.repaint();
	  }
}
