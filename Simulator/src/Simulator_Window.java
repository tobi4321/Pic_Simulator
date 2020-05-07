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
import java.util.EventObject;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.JToggleButton;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.GridLayout;
import java.awt.Cursor;
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
	protected SevenSegmentPanel panel_segmentCanvas;
	protected JTable table_Code;
	protected DefaultTableModel tbl_code;
	protected DefaultTableModel tbl_memory;
	protected DefaultTableModel tbl_special;
	protected DefaultTableModel tbl_status;
	protected DefaultTableModel tbl_pcl;
	protected DefaultTableModel tbl_stack;
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
	// label for displaying the cycle time since program start
	protected JLabel lblOperationalTime;
	private JTable table_Stack;
	
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
		frmMicrocontrollerSimulator.getContentPane().setMaximumSize(new Dimension(1300, 800));
		frmMicrocontrollerSimulator.setMaximumSize(new Dimension(1300, 800));
		frmMicrocontrollerSimulator.setTitle("MicroController Simulator");
		frmMicrocontrollerSimulator.setBounds(100, 100, 1550, 920);
		frmMicrocontrollerSimulator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		Box verticalBox = Box.createVerticalBox();
		verticalBox.setBorder(UIManager.getBorder("InternalFrame.border"));
		frmMicrocontrollerSimulator.getContentPane().add(verticalBox);
		
		Box upperArea = Box.createHorizontalBox();
		upperArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		upperArea.setAlignmentY(0.5f);
		verticalBox.add(upperArea);
		
		Box verticalMemoryView = Box.createVerticalBox();
		verticalMemoryView.setAlignmentY(1.0f);
		verticalMemoryView.setMinimumSize(new Dimension(320, 800));
		verticalMemoryView.setPreferredSize(new Dimension(320, 800));
		verticalMemoryView.setMaximumSize(new Dimension(320, 800));
		upperArea.add(verticalMemoryView);
		
		JPanel panel_MemoryView = new JPanel();
		verticalMemoryView.add(panel_MemoryView);
		GridBagLayout gbl_panel_MemoryView = new GridBagLayout();
		gbl_panel_MemoryView.columnWidths = new int[] {150};
		gbl_panel_MemoryView.rowHeights = new int[] {40, 450, 130, 180};
		gbl_panel_MemoryView.columnWeights = new double[]{0.0};
		gbl_panel_MemoryView.rowWeights = new double[]{0.0, 0.0, 0.0};
		panel_MemoryView.setLayout(gbl_panel_MemoryView);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		inputPanel.setMinimumSize(new Dimension(140, 40));
		GridBagConstraints gbc_inputPanel = new GridBagConstraints();
		gbc_inputPanel.fill = GridBagConstraints.BOTH;
		gbc_inputPanel.insets = new Insets(0, 0, 0, 5);
		gbc_inputPanel.gridx = 0;
		gbc_inputPanel.gridy = 0;
		panel_MemoryView.add(inputPanel, gbc_inputPanel);
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
		txtField_input.setPreferredSize(new Dimension(80, 25));
		txtField_input.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtField_input.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		txtField_input.setSize(new Dimension(80, 25));
		txtField_input.setMaximumSize(new Dimension(80, 25));
		inputPanel.add(txtField_input);
		txtField_input.setColumns(7);
		
		comboBox_File = new JComboBox();
		comboBox_File.setFont(new Font("Tahoma", Font.PLAIN, 18));
		comboBox_File.setModel(new DefaultComboBoxModel(new String[] {"00", "08", "10", "18", "20", "28", "30", "38", "40", "48", "50", "58", "60", "68", "70", "78", "80", "88", "90", "98", "A0", "A8", "B0", "B8", "C0", "C8", "D0", "D8", "E0", "E8", "F0", "F8"}));
		inputPanel.add(comboBox_File);
		
		comboBox_SubFile = new JComboBox();
		comboBox_SubFile.setModel(new DefaultComboBoxModel(new String[] {"00", "01", "02", "03", "04", "05", "06", "07"}));
		comboBox_SubFile.setFont(new Font("Tahoma", Font.PLAIN, 18));
		inputPanel.add(comboBox_SubFile);
		
		JPanel panel_Memeory = new JPanel();
		panel_Memeory.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_Memeory = new GridBagConstraints();
		gbc_panel_Memeory.insets = new Insets(0, 0, 0, 5);
		gbc_panel_Memeory.gridx = 0;
		gbc_panel_Memeory.gridy = 1;
		panel_MemoryView.add(panel_Memeory, gbc_panel_Memeory);
		panel_Memeory.setPreferredSize(new Dimension(310, 700));
		panel_Memeory.setMinimumSize(new Dimension(310, 500));
		panel_Memeory.setMaximumSize(new Dimension(310, 700));
		panel_Memeory.setLayout(new BorderLayout(0, 0));
		

	
		
		JLabel lblMemoryView = new JLabel("Memory View");
		panel_Memeory.add(lblMemoryView, BorderLayout.NORTH);
		lblMemoryView.setFont(new Font("Tahoma", Font.BOLD, 15));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(320, 700));
		scrollPane.setMaximumSize(new Dimension(320, 700));
		scrollPane.setMinimumSize(new Dimension(200, 49));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_Memeory.add(scrollPane, BorderLayout.CENTER);
		
		tbl_memory = new DefaultTableModel();
		tbl_memory.setColumnIdentifiers(new Object[] {"","00","01","02","03","04","05","06","07"});	
		
		table_Memory = new JTable();
		table_Memory.setModel(tbl_memory);
		table_Memory.setEnabled(false);
		table_Memory.setCellSelectionEnabled(true);
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
		GridBagConstraints gbc_panel_specialreg = new GridBagConstraints();
		gbc_panel_specialreg.gridx = 0;
		gbc_panel_specialreg.gridy = 2;
		panel_MemoryView.add(panel_specialreg, gbc_panel_specialreg);
		panel_specialreg.setPreferredSize(new Dimension(320, 300));
		panel_specialreg.setMinimumSize(new Dimension(310, 120));
		panel_specialreg.setBorder(new LineBorder(new Color(0, 0, 0)));
				
		tbl_special = new DefaultTableModel();
		tbl_special.setColumnIdentifiers(new Object[] {"Register", "Hex-Wert","Bin-Wert"});
				
		table_special_regs = new JTable();
		table_special_regs.setEnabled(false);
		table_special_regs.setModel(tbl_special);
		panel_specialreg.setLayout(new BorderLayout(0, 0));
				
				
		JLabel lblSpecialregister = new JLabel("Special-Register");
		lblSpecialregister.setFont(new Font("Tahoma", Font.BOLD, 15));
		panel_specialreg.add(lblSpecialregister, BorderLayout.NORTH);
				
		panel_specialreg.add(table_special_regs);
		table_special_regs.setFont(new Font("Tahoma", Font.PLAIN, 13));
		table_special_regs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_special_regs.setCellSelectionEnabled(true);
		
		
		JPanel panel_Stack = new JPanel();
		panel_Stack.setMinimumSize(new Dimension(310, 180));
		panel_Stack.setLayout(null);
		GridBagConstraints gbc_panel_stack = new GridBagConstraints();
		gbc_panel_stack.gridx = 0;
		gbc_panel_stack.gridy = 3;
		panel_MemoryView.add(panel_Stack,gbc_panel_stack);
		
		tbl_stack = new DefaultTableModel();
		tbl_stack.setColumnIdentifiers(new Object[] {"Nr","Adresse"});
		
		table_Stack = new JTable();
		table_Stack.setBounds(90, 0, 120, 129);
		table_Stack.setFont(new Font("Tahoma", Font.BOLD, 12));
		table_Stack.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table_Stack.setEnabled(false);
		table_Stack.setModel(tbl_stack);
		
		TableColumn stackCol0 = null;
		stackCol0 = table_Stack.getColumnModel().getColumn(0);
		stackCol0.setMaxWidth(40);
		stackCol0.setResizable(false);
		
		TableColumn stackCol1 = null;
		stackCol1 = table_Stack.getColumnModel().getColumn(1);
		stackCol1.setMaxWidth(80);
		stackCol1.setResizable(false);

		
		JLabel lbl_stack = new JLabel("Stack");
		lbl_stack.setBounds(5, 5, 42, 19);
		lbl_stack.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_stack.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		lbl_stack.setFont(new Font("Tahoma", Font.BOLD, 15));
		panel_Stack.add(lbl_stack);
		panel_Stack.add(table_Stack);
		
		

		
		String header[] = new String[] { " ", "ProgramCounter", "ProgramCode", "LineCount","Label","MnemonicCode"};	
		tbl_code = new DefaultTableModel();
	    tbl_code.setColumnIdentifiers(header);


		Box verticalCodeViewer = Box.createVerticalBox();
		verticalCodeViewer.setMinimumSize(new Dimension(500, 300));
		upperArea.add(verticalCodeViewer);
		

		JPanel panel_Code = new JPanel();
		verticalCodeViewer.add(panel_Code);
		panel_Code.setLayout(new BorderLayout(0, 0));
		
		JLabel lblCodeViewer = new JLabel("Code Viewer");
		panel_Code.add(lblCodeViewer, BorderLayout.NORTH);
		lblCodeViewer.setAlignmentY(Component.TOP_ALIGNMENT);
		lblCodeViewer.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setPreferredSize(new Dimension(2, 700));
		scrollPane_3.setMaximumSize(new Dimension(32767, 750));
		panel_Code.add(scrollPane_3, BorderLayout.CENTER);
		scrollPane_3.setViewportBorder(UIManager.getBorder("TableHeader.cellBorder"));
		table_Code = new JTable();
		table_Code.setFont(new Font("Tahoma", Font.PLAIN, 13));
		table_Code.setSelectionBackground(Color.ORANGE);
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
		verticalIO.setMinimumSize(new Dimension(350, 800));
		verticalIO.setPreferredSize(new Dimension(380, 800));
		verticalIO.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		verticalIO.setMaximumSize(new Dimension(400, 800));
		upperArea.add(verticalIO);
		
		
		JPanel panel_IO = new JPanel();
		panel_IO.setAlignmentX(0.0f);
		panel_IO.setAlignmentY(Component.TOP_ALIGNMENT);
		panel_IO.setBorder(new LineBorder(new Color(0, 0, 0)));
		verticalIO.add(panel_IO);
		GridBagLayout gbl_panel_IO = new GridBagLayout();
		gbl_panel_IO.columnWidths = new int[] {130};
		gbl_panel_IO.rowHeights = new int[] {30, 30, 30, 90, 90, 180, 140, 140, 100};
		gbl_panel_IO.columnWeights = new double[]{1.0};
		gbl_panel_IO.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 1.0, 0.0};
		panel_IO.setLayout(gbl_panel_IO);
		
		JPanel panel_Control = new JPanel();
		panel_Control.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_Control = new GridBagConstraints();
		gbc_panel_Control.insets = new Insets(0, 0, 5, 0);
		gbc_panel_Control.fill = GridBagConstraints.BOTH;
		gbc_panel_Control.gridx = 0;
		gbc_panel_Control.gridy = 0;
		panel_IO.add(panel_Control, gbc_panel_Control);
		
		JButton btnDebug = new JButton("Debug");
		btnDebug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctr.startSimu(true);
			}
		});
		btnDebug.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel_Control.add(btnDebug);
		
		JButton btnGo = new JButton("Go");
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctr.startSimu(false);
			}
		});
		btnGo.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel_Control.add(btnGo);
		
		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctr.continueDebugStep();
			}
		});
		btnNext.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel_Control.add(btnNext);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctr.stopSimu();
			}
		});
		btnStop.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel_Control.add(btnStop);
		
		JButton btnReset = new JButton("Reset");
		btnReset.setForeground(Color.BLACK);
		btnReset.setBackground(new Color(255, 0, 0));
		btnReset.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel_Control.add(btnReset);
		
		JPanel panel_RA = new JPanel();
		panel_RA.setOpaque(false);
		panel_RA.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_RA.setLayout(null);
		GridBagConstraints gbc_panel_RA = new GridBagConstraints();
		gbc_panel_RA.fill = GridBagConstraints.BOTH;
		gbc_panel_RA.insets = new Insets(0, 0, 5, 0);
		gbc_panel_RA.gridx = 0;
		gbc_panel_RA.gridy = 3;
		panel_IO.add(panel_RA, gbc_panel_RA);
		
		JLabel lblAnalogOut = new JLabel("Port A");
		lblAnalogOut.setBounds(6, 6, 71, 19);
		lblAnalogOut.setFont(new Font("Tahoma", Font.BOLD, 15));
		panel_RA.add(lblAnalogOut);
		
		JLabel lblBits = new JLabel("Bits");
		lblBits.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblBits.setBounds(115, 6, 46, 25);
		panel_RA.add(lblBits);
		
		JLabel lblTris = new JLabel("Tris A");
		lblTris.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTris.setBounds(115, 32, 46, 19);
		panel_RA.add(lblTris);
		
		JLabel lblPortA = new JLabel("Port A");
		lblPortA.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblPortA.setBounds(115, 53, 46, 32);
		panel_RA.add(lblPortA);
		
		JLabel lbl_ra7 = new JLabel("7");
		lbl_ra7.setBounds(171, 10, 14, 14);
		panel_RA.add(lbl_ra7);
		
		JLabel lbl_ra6 = new JLabel("6");
		lbl_ra6.setBounds(195, 10, 14, 14);
		panel_RA.add(lbl_ra6);
		
		JLabel lbl_ra5 = new JLabel("5");
		lbl_ra5.setBounds(219, 10, 14, 14);
		panel_RA.add(lbl_ra5);
		
		JLabel lbl_ra4 = new JLabel("4");
		lbl_ra4.setBounds(243, 10, 14, 14);
		panel_RA.add(lbl_ra4);
		
		JLabel lbl_ra3 = new JLabel("3");
		lbl_ra3.setBounds(267, 10, 14, 14);
		panel_RA.add(lbl_ra3);
		
		JLabel lbl_ra2 = new JLabel("2");
		lbl_ra2.setBounds(291, 10, 14, 14);
		panel_RA.add(lbl_ra2);
		
		JLabel lbl_ra1 = new JLabel("1");
		lbl_ra1.setBounds(315, 10, 14, 14);
		panel_RA.add(lbl_ra1);
		
		JLabel lbl_ra0 = new JLabel("0");
		lbl_ra0.setBounds(339, 10, 14, 14);
		panel_RA.add(lbl_ra0);
		
		lbl_ra_tris7 = new JLabel("I");
		lbl_ra_tris7.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_ra_tris7.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris7.setBounds(171, 35, 14, 14);
		panel_RA.add(lbl_ra_tris7);
		
		lbl_ra_tris6 = new JLabel("I");
		lbl_ra_tris6.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_ra_tris6.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris6.setBounds(195, 35, 14, 14);
		panel_RA.add(lbl_ra_tris6);
		
		lbl_ra_tris5 = new JLabel("I");
		lbl_ra_tris5.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_ra_tris5.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris5.setBounds(219, 35, 14, 14);
		panel_RA.add(lbl_ra_tris5);
		
		lbl_ra_tris4 = new JLabel("I");
		lbl_ra_tris4.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_ra_tris4.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris4.setBounds(243, 35, 14, 14);
		panel_RA.add(lbl_ra_tris4);
		
		lbl_ra_tris3 = new JLabel("I");
		lbl_ra_tris3.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_ra_tris3.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris3.setBounds(267, 35, 14, 14);
		panel_RA.add(lbl_ra_tris3);
		
		lbl_ra_tris2 = new JLabel("I");
		lbl_ra_tris2.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_ra_tris2.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris2.setBounds(291, 35, 14, 14);
		panel_RA.add(lbl_ra_tris2);
		
		lbl_ra_tris1 = new JLabel("I");
		lbl_ra_tris1.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_ra_tris1.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris1.setBounds(315, 35, 14, 14);
		panel_RA.add(lbl_ra_tris1);
		
		lbl_ra_tris0 = new JLabel("I");
		lbl_ra_tris0.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_ra_tris0.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl_ra_tris0.setBounds(339, 35, 14, 14);
		panel_RA.add(lbl_ra_tris0);
		
		rbtn_ra_7 = new JRadioButton("");
		rbtn_ra_7.setBounds(167, 53, 21, 23);
		panel_RA.add(rbtn_ra_7);
		
		rbtn_ra_6 = new JRadioButton("");
		rbtn_ra_6.setBounds(188, 53, 21, 23);
		panel_RA.add(rbtn_ra_6);
		
		rbtn_ra_5 = new JRadioButton("");
		rbtn_ra_5.setBounds(212, 53, 21, 23);
		panel_RA.add(rbtn_ra_5);
		
		rbtn_ra_4 = new JRadioButton("");
		rbtn_ra_4.setBounds(236, 53, 21, 23);
		panel_RA.add(rbtn_ra_4);
		
		rbtn_ra_3 = new JRadioButton("");
		rbtn_ra_3.setBounds(260, 53, 21, 23);
		panel_RA.add(rbtn_ra_3);
		
		rbtn_ra_2 = new JRadioButton("");
		rbtn_ra_2.setBounds(284, 53, 21, 23);
		panel_RA.add(rbtn_ra_2);
		
		rbtn_ra_1 = new JRadioButton("");
		rbtn_ra_1.setBounds(308, 53, 21, 23);
		panel_RA.add(rbtn_ra_1);
		
		rbtn_ra_0 = new JRadioButton("");
		rbtn_ra_0.setBounds(332, 53, 21, 23);
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
		gbc_panel_Quarzfrequenz.gridy = 1;
		panel_IO.add(panel_Quarzfrequenz, gbc_panel_Quarzfrequenz);
		panel_Quarzfrequenz.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel lblQuarzFrequenz = new JLabel("Quarz Freq");
		lblQuarzFrequenz.setHorizontalAlignment(SwingConstants.LEFT);
		lblQuarzFrequenz.setPreferredSize(new Dimension(140, 20));
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
		btnSetFrequency.setPreferredSize(new Dimension(130, 23));
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
		gbc_panel_RB.gridy = 4;
		panel_IO.add(panel_RB, gbc_panel_RB);
		
		JLabel lblAnalogIn = new JLabel("Port B");
		lblAnalogIn.setBounds(6, 7, 72, 19);
		lblAnalogIn.setFont(new Font("Tahoma", Font.BOLD, 15));
		panel_RB.add(lblAnalogIn);
		
		JLabel label = new JLabel("Bits");
		label.setFont(new Font("Tahoma", Font.PLAIN, 12));
		label.setBounds(116, 0, 46, 25);
		panel_RB.add(label);
		
		JLabel lblTrisB = new JLabel("Tris B");
		lblTrisB.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTrisB.setBounds(116, 26, 46, 19);
		panel_RB.add(lblTrisB);
		
		JLabel lblPortB = new JLabel("Port B");
		lblPortB.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblPortB.setBounds(116, 47, 46, 32);
		panel_RB.add(lblPortB);
		
		JLabel lbl_rb7 = new JLabel("7");
		lbl_rb7.setBounds(172, 4, 14, 14);
		panel_RB.add(lbl_rb7);
		
		JLabel lbl_rb6 = new JLabel("6");
		lbl_rb6.setBounds(196, 4, 14, 14);
		panel_RB.add(lbl_rb6);
		
		JLabel lbl_rb5 = new JLabel("5");
		lbl_rb5.setBounds(220, 4, 14, 14);
		panel_RB.add(lbl_rb5);
		
		JLabel lbl_rb4 = new JLabel("4");
		lbl_rb4.setBounds(244, 4, 14, 14);
		panel_RB.add(lbl_rb4);
		
		JLabel lbl_rb3 = new JLabel("3");
		lbl_rb3.setBounds(268, 4, 14, 14);
		panel_RB.add(lbl_rb3);
		
		JLabel lbl_rb2 = new JLabel("2");
		lbl_rb2.setBounds(292, 4, 14, 14);
		panel_RB.add(lbl_rb2);
		
		JLabel lbl_rb1 = new JLabel("1");
		lbl_rb1.setBounds(316, 4, 14, 14);
		panel_RB.add(lbl_rb1);
		
		JLabel lbl_rb0 = new JLabel("0");
		lbl_rb0.setBounds(340, 4, 14, 14);
		panel_RB.add(lbl_rb0);
		
		lbl_rb_tris7 = new JLabel("I");
		lbl_rb_tris7.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_rb_tris7.setAlignmentX(0.5f);
		lbl_rb_tris7.setBounds(172, 29, 14, 14);
		panel_RB.add(lbl_rb_tris7);
		
		lbl_rb_tris6 = new JLabel("I");
		lbl_rb_tris6.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_rb_tris6.setAlignmentX(0.5f);
		lbl_rb_tris6.setBounds(196, 29, 14, 14);
		panel_RB.add(lbl_rb_tris6);
		
		lbl_rb_tris5 = new JLabel("I");
		lbl_rb_tris5.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_rb_tris5.setAlignmentX(0.5f);
		lbl_rb_tris5.setBounds(220, 29, 14, 14);
		panel_RB.add(lbl_rb_tris5);
		
		lbl_rb_tris4 = new JLabel("I");
		lbl_rb_tris4.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_rb_tris4.setAlignmentX(0.5f);
		lbl_rb_tris4.setBounds(244, 29, 14, 14);
		panel_RB.add(lbl_rb_tris4);
		
		lbl_rb_tris3 = new JLabel("I");
		lbl_rb_tris3.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_rb_tris3.setAlignmentX(0.5f);
		lbl_rb_tris3.setBounds(268, 29, 14, 14);
		panel_RB.add(lbl_rb_tris3);
		
		lbl_rb_tris2 = new JLabel("I");
		lbl_rb_tris2.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_rb_tris2.setAlignmentX(0.5f);
		lbl_rb_tris2.setBounds(292, 29, 14, 14);
		panel_RB.add(lbl_rb_tris2);
		
		lbl_rb_tris1 = new JLabel("I");
		lbl_rb_tris1.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_rb_tris1.setAlignmentX(0.5f);
		lbl_rb_tris1.setBounds(316, 29, 14, 14);
		panel_RB.add(lbl_rb_tris1);
		
		lbl_rb_tris0 = new JLabel("I");
		lbl_rb_tris0.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_rb_tris0.setAlignmentX(0.5f);
		lbl_rb_tris0.setBounds(340, 29, 14, 14);
		panel_RB.add(lbl_rb_tris0);
		
		rbtn_rb_7 = new JRadioButton("");
		rbtn_rb_7.setBounds(168, 47, 21, 23);
		panel_RB.add(rbtn_rb_7);
		
		rbtn_rb_6 = new JRadioButton("");
		rbtn_rb_6.setBounds(189, 47, 21, 23);
		panel_RB.add(rbtn_rb_6);
		
		rbtn_rb_5 = new JRadioButton("");
		rbtn_rb_5.setBounds(213, 47, 21, 23);
		panel_RB.add(rbtn_rb_5);
		
		rbtn_rb_4 = new JRadioButton("");
		rbtn_rb_4.setBounds(237, 47, 21, 23);
		panel_RB.add(rbtn_rb_4);
		
		rbtn_rb_3 = new JRadioButton("");
		rbtn_rb_3.setBounds(261, 47, 21, 23);
		panel_RB.add(rbtn_rb_3);
		
		rbtn_rb_2 = new JRadioButton("");
		rbtn_rb_2.setBounds(285, 47, 21, 23);
		panel_RB.add(rbtn_rb_2);
		
		rbtn_rb_1 = new JRadioButton("");
		rbtn_rb_1.setBounds(309, 47, 21, 23);
		panel_RB.add(rbtn_rb_1);
		
		rbtn_rb_0 = new JRadioButton("");
		rbtn_rb_0.setBounds(333, 47, 21, 23);
		panel_RB.add(rbtn_rb_0);
		
		JPanel panel_7Segment = new JPanel();
		panel_7Segment.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_7Segment = new GridBagConstraints();
		gbc_panel_7Segment.fill = GridBagConstraints.BOTH;
		gbc_panel_7Segment.insets = new Insets(0, 0, 5, 0);
		gbc_panel_7Segment.gridx = 0;
		gbc_panel_7Segment.gridy = 5;
		panel_IO.add(panel_7Segment, gbc_panel_7Segment);
		GridBagLayout gbl_panel_7Segment = new GridBagLayout();
		gbl_panel_7Segment.columnWidths = new int[] {375};
		gbl_panel_7Segment.rowHeights = new int[] {20, 80, 20};
		gbl_panel_7Segment.columnWeights = new double[]{0.0};
		gbl_panel_7Segment.rowWeights = new double[]{0.0, 0.0, 0.0};
		panel_7Segment.setLayout(gbl_panel_7Segment);
		
		JLabel lblSegment = new JLabel("  7 - Segment  ");
		GridBagConstraints gbc_lblSegment = new GridBagConstraints();
		gbc_lblSegment.fill = GridBagConstraints.BOTH;
		gbc_lblSegment.insets = new Insets(0, 0, 5, 0);
		gbc_lblSegment.gridx = 0;
		gbc_lblSegment.gridy = 0;
		panel_7Segment.add(lblSegment, gbc_lblSegment);
		lblSegment.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		panel_segmentCanvas = new SevenSegmentPanel();
		panel_segmentCanvas.setMaximumSize(new Dimension(310, 105));
		panel_segmentCanvas.setPreferredSize(new Dimension(310, 105));
		GridBagConstraints gbc_panel_segmentCanvas = new GridBagConstraints();
		gbc_panel_segmentCanvas.fill = GridBagConstraints.BOTH;
		gbc_panel_segmentCanvas.insets = new Insets(0, 0, 5, 0);
		gbc_panel_segmentCanvas.gridx = 0;
		gbc_panel_segmentCanvas.gridy = 1;
		panel_7Segment.add(panel_segmentCanvas, gbc_panel_segmentCanvas);
		panel_segmentCanvas.setOpaque(false);
		panel_segmentCanvas.setBackground(Color.WHITE);
		panel_segmentCanvas.setLayout(null);
		
		JPanel panel_segmentOptions = new JPanel();
		GridBagConstraints gbc_panel_segmentOptions = new GridBagConstraints();
		gbc_panel_segmentOptions.fill = GridBagConstraints.BOTH;
		gbc_panel_segmentOptions.gridx = 0;
		gbc_panel_segmentOptions.gridy = 2;
		panel_7Segment.add(panel_segmentOptions, gbc_panel_segmentOptions);
		panel_segmentOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				if(comboBox_2.getSelectedIndex() == 0) 
				{
					ctr.controlPortSelect = 0;
				}else if(comboBox_2.getSelectedIndex() == 1) 
				{
					ctr.controlPortSelect = 1;
				}
			}
		});
		comboBox_2.setModel(new DefaultComboBoxModel(new String[] {"Port A", "Port B"}));
		panel_segmentOptions.add(comboBox_2);
		
		JComboBox comboBox_3 = new JComboBox();
		comboBox_3.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				if(comboBox_3.getSelectedIndex() == 0) 
				{
					ctr.dataPortSelect = 0;
				}else if(comboBox_3.getSelectedIndex() == 1) 
				{
					ctr.dataPortSelect = 1;
				}
			}
		});
		comboBox_3.setModel(new DefaultComboBoxModel(new String[] {"Port A", "Port B"}));
		panel_segmentOptions.add(comboBox_3);
		
		JToggleButton tglbtnActivate = new JToggleButton("Activate");
		panel_segmentOptions.add(tglbtnActivate);
		tglbtnActivate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
		          ButtonModel buttonModel = abstractButton.getModel();
		          //boolean armed = buttonModel.isArmed();
		          //boolean pressed = buttonModel.isPressed();
		          boolean selected = buttonModel.isSelected();  
				System.out.println("7-Segment: "+selected);
				if(selected) 
		          {
		        	  ctr.sevenSegmentActive = true;
		          }else 
		          {
		        	  ctr.sevenSegmentActive = false;
		          }
			}
		});
		
		JPanel panel_Time = new JPanel();
		panel_Time.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_Time = new GridBagConstraints();
		gbc_panel_Time.insets = new Insets(0, 0, 5, 0);
		gbc_panel_Time.fill = GridBagConstraints.BOTH;
		gbc_panel_Time.gridx = 0;
		gbc_panel_Time.gridy = 2;
		panel_IO.add(panel_Time, gbc_panel_Time);
		GridBagLayout gbl_panel_Time = new GridBagLayout();
		gbl_panel_Time.columnWidths = new int[] {80, 180, 90};
		gbl_panel_Time.rowHeights = new int[] {30};
		gbl_panel_Time.columnWeights = new double[]{0.0, 0.0, 0.0};
		gbl_panel_Time.rowWeights = new double[]{0.0};
		panel_Time.setLayout(gbl_panel_Time);
		
		JButton btnZurcksetzen = new JButton("Zur\u00FCcksetzen");
		btnZurcksetzen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctr.operationalTime = 0.0;
				ctr.updateOperationalTime();
			}
		});
		
		JLabel lblLaufzeit = new JLabel("Laufzeit");
		lblLaufzeit.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblLaufzeit.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblLaufzeit = new GridBagConstraints();
		gbc_lblLaufzeit.fill = GridBagConstraints.BOTH;
		gbc_lblLaufzeit.insets = new Insets(0, 0, 0, 5);
		gbc_lblLaufzeit.gridx = 0;
		gbc_lblLaufzeit.gridy = 0;
		panel_Time.add(lblLaufzeit, gbc_lblLaufzeit);
		
		lblOperationalTime = new JLabel("0.0us");
		lblOperationalTime.setHorizontalAlignment(SwingConstants.CENTER);
		lblOperationalTime.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblOperationalTime = new GridBagConstraints();
		gbc_lblOperationalTime.fill = GridBagConstraints.BOTH;
		gbc_lblOperationalTime.insets = new Insets(0, 0, 0, 5);
		gbc_lblOperationalTime.gridx = 1;
		gbc_lblOperationalTime.gridy = 0;
		panel_Time.add(lblOperationalTime, gbc_lblOperationalTime);
		GridBagConstraints gbc_btnZurcksetzen = new GridBagConstraints();
		gbc_btnZurcksetzen.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnZurcksetzen.gridx = 2;
		gbc_btnZurcksetzen.gridy = 0;
		panel_Time.add(btnZurcksetzen, gbc_btnZurcksetzen);
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
		
		JButton btnNext_1 = new JButton("Next");
		btnNext_1.setSize(new Dimension(121, 23));
		btnNext_1.setMinimumSize(new Dimension(121, 23));
		btnNext_1.setPreferredSize(new Dimension(121, 23));
		btnNext_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctr.continueDebugStep();
			}
		});
		debug_menu.add(btnNext_1);
		btnNext_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
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
	  public void setStackData(Object obj,int row_index,int col_index) 
	  {
		  table_Stack.getModel().setValueAt(obj, row_index, col_index);
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
	  
	  protected void highlightCell(int x, int y) 
	  {
		  try {
			table_Memory.addRowSelectionInterval(x, x);
			table_Memory.addColumnSelectionInterval(y, y);
		  }catch(NullPointerException e) {
			  // Exception occures in the initialization and can therefore be ignored.
		  }
	  }
	  protected void removeHighlightCell(int x, int y) 
	  {
		  table_Memory.removeRowSelectionInterval(x, x);
		  table_Memory.removeColumnSelectionInterval(y, y);
	  }
}