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
import java.io.IOException;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.SwingConstants;
import javax.swing.Box;
import javax.swing.border.EmptyBorder;
import javax.swing.ListSelectionModel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import java.awt.ComponentOrientation;
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
	protected JTextArea txtArea_Console;

	
	// radio buttons for the IO Panels
	protected JRadioButton rb_io_1;
	protected JRadioButton rb_io_2;
	protected JRadioButton rb_io_3;
	protected JRadioButton rb_io_4;
	protected JRadioButton rb_io_5;
	protected JRadioButton rb_io_6;
	protected JRadioButton rb_io_7;
	protected JRadioButton rb_io_8;
	// members to input values into register memory
	private JTextField txtField_input;
	private JButton btn_InputRegister;
	private JComboBox comboBox_File;
	private JComboBox comboBox_SubFile;
	
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
				
				ctr.memory.set_SRAM(fileNumber+subFileNumber, Integer.parseInt(input));
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
		gbl_panel_IO.rowHeights = new int[] {10, 10, 10, 10, 10, 10, 10, 30, 120, 30, 30, 30,30};
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
		gbc_panel_AnalogOut.gridy = 0;
		panel_IO.add(panel_AnalogOut, gbc_panel_AnalogOut);
		
		JLabel lblAnalogOut = new JLabel("Analog OUT   ");
		lblAnalogOut.setFont(new Font("Tahoma", Font.BOLD, 15));
		panel_AnalogOut.add(lblAnalogOut);
		
		rb_io_1 = new JRadioButton("");
		rb_io_1.setEnabled(false);
		rb_io_1.setForeground(Color.RED);
		panel_AnalogOut.add(rb_io_1);
		
		rb_io_2 = new JRadioButton("");
		rb_io_2.setEnabled(false);
		rb_io_2.setForeground(Color.RED);
		panel_AnalogOut.add(rb_io_2);
		
		rb_io_3 = new JRadioButton("");
		rb_io_3.setEnabled(false);
		rb_io_3.setForeground(Color.RED);
		panel_AnalogOut.add(rb_io_3);
		
		rb_io_4 = new JRadioButton("");
		rb_io_4.setEnabled(false);
		rb_io_4.setForeground(Color.RED);
		panel_AnalogOut.add(rb_io_4);
		
		rb_io_5 = new JRadioButton("");
		rb_io_5.setEnabled(false);
		rb_io_5.setForeground(Color.RED);
		panel_AnalogOut.add(rb_io_5);
		
		rb_io_6 = new JRadioButton("");
		rb_io_6.setEnabled(false);
		rb_io_6.setForeground(Color.RED);
		panel_AnalogOut.add(rb_io_6);
		
		rb_io_7 = new JRadioButton("");
		rb_io_7.setEnabled(false);
		rb_io_7.setForeground(Color.RED);
		panel_AnalogOut.add(rb_io_7);
		
		rb_io_8 = new JRadioButton("");
		rb_io_8.setSelected(true);
		rb_io_8.setEnabled(false);
		rb_io_8.setForeground(Color.RED);
		panel_AnalogOut.add(rb_io_8);
		
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
		gbc_panel_AnalogIn.gridy = 2;
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
		gbc_panel_7Segment.gridy = 4;
		panel_IO.add(panel_7Segment, gbc_panel_7Segment);
		panel_7Segment.setLayout(new BorderLayout(0, 0));
		
		JLabel lblSegment = new JLabel("  7 - Segment  ");
		panel_7Segment.add(lblSegment, BorderLayout.NORTH);
		lblSegment.setFont(new Font("Tahoma", Font.BOLD, 15));
		
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
		
		panel_segmentCanvas = new MyPanel();
		panel_segmentCanvas.setPreferredSize(new Dimension(180, 105));
		panel_segmentCanvas.setMinimumSize(new Dimension(180, 105));
		panel_7Segment.add(panel_segmentCanvas, BorderLayout.CENTER);
		panel_segmentCanvas.setBackground(Color.WHITE);
		
		
		Box lowerArea = Box.createHorizontalBox();
		lowerArea.setAlignmentY(Component.CENTER_ALIGNMENT);
		verticalBox.add(lowerArea);
		
		Box verticalConsole = Box.createVerticalBox();
		lowerArea.add(verticalConsole);
		frmMicrocontrollerSimulator.getContentPane().setLayout(new BoxLayout(frmMicrocontrollerSimulator.getContentPane(), BoxLayout.X_AXIS));
		
		JPanel panel_Console = new JPanel();
		verticalConsole.add(panel_Console);
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
		
		JButton btnSaveFile = new JButton("Save File");
		btnSaveFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnSaveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
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
		
		JButton btnDebuggerStoppen = new JButton("Debugger stoppen");
		btnDebuggerStoppen.setFont(new Font("Tahoma", Font.PLAIN, 13));
		debug_menu.add(btnDebuggerStoppen);
		
		JButton button = new JButton("->");
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
		mnSimulator.add(btnHelp);
		

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
		  this.panel_segmentCanvas.setChars(c1,c2,c3,c4);
		  this.panel_segmentCanvas.repaint();
	  }
	  public void setSpecialData(Object obj,int row_index,int col_index) 
	  {
		  this.table_special_regs.getModel().setValueAt(obj, row_index, col_index);
	  }
}
