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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
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
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JToggleButton;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.Cursor;
import javax.swing.JSlider;
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

	private SevenSegmentPanel panel_segmentCanvas;
	private JTable table_Code;
	private DefaultTableModel tbl_code;
	private DefaultTableModel tbl_memory;
	private DefaultTableModel tbl_special;
	private DefaultTableModel tbl_stack;
	private JTable table_Memory;
	private JTable table_special_regs;
	// members to input values into register memory
	private JTextField txtField_input;
	private JButton btn_InputRegister;
	private JComboBox<String> comboBox_File;
	private JComboBox<String> comboBox_SubFile;
	// member for port a
	private JLabel lbl_ra_tris4;
	private JLabel lbl_ra_tris3;
	private JLabel lbl_ra_tris2;
	private JLabel lbl_ra_tris1;
	private JLabel lbl_ra_tris0;
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
	private JComboBox<String> comboBox_quarzFrequency;
	// label for displaying the cycle time since program start
	private JLabel lblOperationalTime;
	private JTable table_Stack;
	
	// member for wdte sleep and mclr
	private JRadioButton tglbtnSleeping;
	private JToggleButton tglbtnMclr;
	private JTextField sliderText;
	private JSlider slider;



	/**
	 * The main function of the program. 
	 * Launching the application and creating the GUI.
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
	 * The constructor, creating the Controller, initializing the Memory and starting the Memory update thread.
	 * Create the application.
	 * @throws UnsupportedEncodingException 
	 */
	public Simulator_Window() throws UnsupportedEncodingException {
		ctr = new Controller(this);
		initialize();
		ctr.inizializeMemory();
		ctr.startMemoryUpdateThread();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws UnsupportedEncodingException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() throws UnsupportedEncodingException {
		

        InputStream url = Simulator_Window.class.getResourceAsStream("/resources/pic.png");
		BufferedImage img = null;
		try {
		    img = ImageIO.read(url);
		} catch (IOException e) {
		}
		
		frmMicrocontrollerSimulator = new JFrame();
		frmMicrocontrollerSimulator.setResizable(false);
		frmMicrocontrollerSimulator.setMaximumSize(new Dimension(1300, 730));
		frmMicrocontrollerSimulator.setTitle("PicSimulator 666");
		frmMicrocontrollerSimulator.setIconImage(img);
		frmMicrocontrollerSimulator.setBounds(100, 100, 1551, 864);
		frmMicrocontrollerSimulator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		Box verticalBox = Box.createVerticalBox();
		verticalBox.setAlignmentY(0.0f);
		verticalBox.setBorder(UIManager.getBorder("InternalFrame.border"));
		frmMicrocontrollerSimulator.getContentPane().add(verticalBox);
		
		Box upperArea = Box.createHorizontalBox();
		upperArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		upperArea.setAlignmentY(0.5f);
		verticalBox.add(upperArea);
		
		Box verticalMemoryView = Box.createVerticalBox();
		verticalMemoryView.setAlignmentY(1.0f);
		verticalMemoryView.setMinimumSize(new Dimension(320, 750));
		verticalMemoryView.setPreferredSize(new Dimension(320, 750));
		verticalMemoryView.setMaximumSize(new Dimension(320, 800));
		upperArea.add(verticalMemoryView);
		
		JPanel panel_MemoryView = new JPanel();
		panel_MemoryView.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_MemoryView.setAlignmentY(Component.TOP_ALIGNMENT);
		verticalMemoryView.add(panel_MemoryView);
		GridBagLayout gbl_panel_MemoryView = new GridBagLayout();
		gbl_panel_MemoryView.columnWidths = new int[] {150};
		gbl_panel_MemoryView.rowHeights = new int[] {40, 430, 130, 180};
		gbl_panel_MemoryView.columnWeights = new double[]{0.0};
		gbl_panel_MemoryView.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		panel_MemoryView.setLayout(gbl_panel_MemoryView);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		inputPanel.setMinimumSize(new Dimension(140, 40));
		GridBagConstraints gbc_inputPanel = new GridBagConstraints();
		gbc_inputPanel.insets = new Insets(0, 0, 5, 0);
		gbc_inputPanel.fill = GridBagConstraints.BOTH;
		gbc_inputPanel.gridx = 0;
		gbc_inputPanel.gridy = 0;
		panel_MemoryView.add(inputPanel, gbc_inputPanel);
		FlowLayout flowLayout = (FlowLayout) inputPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		
		btn_InputRegister = new JButton("Input");
		btn_InputRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String input = txtField_input.getText();
				
				if(input.matches(".*\\d.*")) 
				{
					int fileNumber = Integer.parseInt(comboBox_File.getSelectedItem().toString(), 16);
					int subFileNumber = Integer.parseInt(comboBox_SubFile.getSelectedItem().toString(),16);
					System.out.println("Input: "+input+" into "+(fileNumber+subFileNumber));
					ctr.getMemory().set_SRAMDIRECT((fileNumber+subFileNumber), Integer.parseInt(input));
				}else 
				{
					ctr.showError("Input Error", "The Text you have inserted is not a valid number");
				}

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
		gbc_panel_Memeory.insets = new Insets(0, 0, 5, 0);
		gbc_panel_Memeory.gridx = 0;
		gbc_panel_Memeory.gridy = 1;
		panel_MemoryView.add(panel_Memeory, gbc_panel_Memeory);
		panel_Memeory.setMinimumSize(new Dimension(310, 430));
		panel_Memeory.setMaximumSize(new Dimension(310, 430));
		panel_Memeory.setLayout(new BorderLayout(0, 0));
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
		gbc_panel_specialreg.insets = new Insets(0, 0, 5, 0);
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
		table_Stack.setBounds(51, 28, 200, 140);
		table_Stack.setFont(new Font("Tahoma", Font.BOLD, 14));
		table_Stack.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table_Stack.setEnabled(false);
		table_Stack.setModel(tbl_stack);
		table_Stack.getColumnModel().getColumn(0).setMinWidth(100);
		table_Stack.getColumnModel().getColumn(1).setMinWidth(100);
		
		TableColumn stackCol0 = null;
		stackCol0 = table_Stack.getColumnModel().getColumn(0);
		stackCol0.setMaxWidth(40);
		stackCol0.setResizable(false);
		
		TableColumn stackCol1 = null;
		stackCol1 = table_Stack.getColumnModel().getColumn(1);
		stackCol1.setMaxWidth(80);
		stackCol1.setResizable(false);

		
		JLabel lbl_stack = new JLabel("Stack");
		lbl_stack.setBounds(122, 0, 42, 19);
		lbl_stack.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_stack.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		lbl_stack.setFont(new Font("Tahoma", Font.BOLD, 15));
		panel_Stack.add(lbl_stack);
		panel_Stack.add(table_Stack);
		
		

		
		String header[] = new String[] { " ", "ProgramCounter", "ProgramCode", "LineCount","Label","MnemonicCode"};	
		tbl_code = new DefaultTableModel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			boolean[] canEdit = new boolean[]{
                    true, false, false, false, false,false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
		};
	    tbl_code.setColumnIdentifiers(header);


		Box verticalCodeViewer = Box.createVerticalBox();
		verticalCodeViewer.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		verticalCodeViewer.setMaximumSize(new Dimension(2000, 800));
		verticalCodeViewer.setMinimumSize(new Dimension(500, 100));
		upperArea.add(verticalCodeViewer);
		

		JPanel panel_Code = new JPanel();
		panel_Code.setPreferredSize(new Dimension(10, 600));
		panel_Code.setMaximumSize(new Dimension(32767, 800));
		verticalCodeViewer.add(panel_Code);
		panel_Code.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setPreferredSize(new Dimension(2, 600));
		scrollPane_3.setMaximumSize(new Dimension(32767, 600));
		panel_Code.add(scrollPane_3, BorderLayout.CENTER);
		scrollPane_3.setViewportBorder(UIManager.getBorder("TableHeader.cellBorder"));
		table_Code = new JTable();
		table_Code.setEnabled(false);
		table_Code.addMouseListener(new java.awt.event.MouseAdapter() {
		    @Override
		    public void mouseClicked(java.awt.event.MouseEvent evt) {
		        int row = table_Code.rowAtPoint(evt.getPoint());
		        int col = table_Code.columnAtPoint(evt.getPoint());
		        if (col == 0 ) {
		          System.out.println("Row: "+row);
		          ctr.setBreakPoint(row);
		        }
		    }
		});
		table_Code.setFont(new Font("Tahoma", Font.PLAIN, 13));
		table_Code.setSelectionBackground(Color.ORANGE);
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
		gbl_panel_IO.rowHeights = new int[] {30, 30, 30, 90, 90, 180, 30, 30, 250};
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
				
				if(ctr.isProcessorRunning() == false) 
				{
					ctr.startSimu(true);
				}else 
				{
					// deactivate debugging if thread is already running
					ctr.getProcessor().setDebugging(false);
					ctr.getProcessor().setContinueDebug(true);
				}
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
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctr.resetButton();
			}
		});
		btnReset.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel_Control.add(btnReset);
		
		JPanel panel_Slider = new JPanel();
		panel_Slider.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_Slider = new GridBagConstraints();
		gbc_panel_Slider.insets = new Insets(0, 0, 5, 0);
		gbc_panel_Slider.fill = GridBagConstraints.BOTH;
		gbc_panel_Slider.gridx = 0;
		gbc_panel_Slider.gridy = 7;
		panel_IO.add(panel_Slider, gbc_panel_Slider);
		
		JLabel lblNewLabel = new JLabel("Simulation speed:");
		panel_Slider.add(lblNewLabel);
		
		slider = new JSlider();
		slider.setValue(500);
		slider.setMaximum(999);
		slider.setMinimum(1);
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				sliderText.setText("" + slider.getValue());
				ctr.setSimulationSpeed(1000 - slider.getValue());
			}
		});
		panel_Slider.add(slider);
		panel_Slider.setToolTipText("The speed of the simulation. A higher value will run the simulation faster.");
		
		sliderText = new JTextField();
		sliderText.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				slider.setValue(Integer.parseInt(sliderText.getText()));
				ctr.setSimulationSpeed(1000 - slider.getValue());
			}
		});
		panel_Slider.add(sliderText);
		sliderText.setColumns(10);
		
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
					ctr.setControlPortSelect(0);
				}else if(comboBox_2.getSelectedIndex() == 1) 
				{
					ctr.setControlPortSelect(1);
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
					ctr.setDataPortSelect(0);
				}else if(comboBox_3.getSelectedIndex() == 1) 
				{
					ctr.setDataPortSelect(1);
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
		          boolean selected = buttonModel.isSelected();  

				if(selected) 
		          {
		        	  ctr.setSevenSegmentActive(true);
		          }else 
		          {
		        	  ctr.setSevenSegmentActive(false);
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
				ctr.setOperationalTime(0.0);
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
		
		JPanel panel_WDTSleep = new JPanel();
		GridBagConstraints gbc_panel_WDTSleep = new GridBagConstraints();
		gbc_panel_WDTSleep.fill = GridBagConstraints.BOTH;
		gbc_panel_WDTSleep.gridx = 0;
		gbc_panel_WDTSleep.gridy = 6;
		panel_IO.add(panel_WDTSleep, gbc_panel_WDTSleep);
		panel_WDTSleep.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		tglbtnMclr = new JToggleButton("MCLR");
		tglbtnMclr.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
		            if(((JToggleButton)e.getSource()).isSelected()) 
		            {
			        	 ctr.setMclr(true);
		            }else 
		            {
			        	 ctr.setMclr(false);
		            }
	         }
	      });
		panel_WDTSleep.add(tglbtnMclr);
		
		JToggleButton tglbtnWatchdogEnable = new JToggleButton("Watchdog Enable");
		tglbtnWatchdogEnable.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
	            if(((JToggleButton)e.getSource()).isSelected()) 
	            {
	            	ctr.setWDTE(true);
	            }else 
	            {
	            	ctr.setWDTE(false);
	            }
	         }
	      });
		panel_WDTSleep.add(tglbtnWatchdogEnable);

		
		tglbtnSleeping = new JRadioButton("Sleeping");
		tglbtnSleeping.setEnabled(false);
		panel_WDTSleep.add(tglbtnSleeping);
		
		
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
		btnLoadFile.setMaximumSize(new Dimension(110, 23));
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
		btnSaveFile.setMaximumSize(new Dimension(110, 23));
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
		btnStopSimulation.setMaximumSize(new Dimension(125, 23));
		btnStopSimulation.setHorizontalTextPosition(SwingConstants.CENTER);
		btnStopSimulation.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnStopSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctr.stopSimu();
			}
		});
		
		JButton btnStartSimulation = new JButton("Start Simulation");
		btnStartSimulation.setMaximumSize(new Dimension(125, 23));
		btnStartSimulation.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnStartSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctr.startSimu(false);
			}
		});
		run_menu.add(btnStartSimulation);
		run_menu.add(btnStopSimulation);
		
		
		debug_menu = new JMenu("Debug");
		debug_menu.setHorizontalTextPosition(SwingConstants.CENTER);
		debug_menu.setHorizontalAlignment(SwingConstants.CENTER);
		debug_menu.setMnemonic(KeyEvent.VK_A);
		debug_menu.getAccessibleContext().setAccessibleDescription("Compile and Debug");
		menuBar.add(debug_menu);
		
		JButton btnCompile = new JButton("Compile");
		btnCompile.setMaximumSize(new Dimension(140, 23));
		btnCompile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//ctr.compileCode();
			}
		});
		btnCompile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		debug_menu.add(btnCompile);
		
		JButton btnDebuggerStarten = new JButton("Debugger starten");
		btnDebuggerStarten.setMaximumSize(new Dimension(140, 23));
		btnDebuggerStarten.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnDebuggerStarten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctr.startSimu(true);
			}
		});
		debug_menu.add(btnDebuggerStarten);
		
		JButton btnDebuggerStoppen = new JButton("Debugger stoppen");
		btnDebuggerStoppen.setMaximumSize(new Dimension(140, 23));
		btnDebuggerStoppen.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnDebuggerStoppen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctr.stopSimu();
			}
		});
		debug_menu.add(btnDebuggerStoppen);
		
		JButton btnNext_1 = new JButton("Next");
		btnNext_1.setMaximumSize(new Dimension(140, 23));
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
		btnOpenMnemonic.setMaximumSize(new Dimension(115, 23));
		btnOpenMnemonic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctr.openMnemonicView();
			}
		});
		mnSimulator.add(btnOpenMnemonic);
		
		JButton btnIoServer = new JButton("IO Server");
		btnIoServer.setMaximumSize(new Dimension(115, 23));
		btnIoServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			    JLabel ipAdress = new JLabel();
			    JLabel port = new JLabel();

			    InetAddress inetAddress;
				try {
					inetAddress = InetAddress.getLocalHost();
				    ipAdress.setText(inetAddress.getHostAddress());
				    port.setText(Integer.toString(ctr.getServer().getServerPort()));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			    
			    JPanel myPanel = new JPanel();
			    myPanel.add(new JLabel("IP:"));
			    myPanel.add(ipAdress);
			    
			    InputStream url = Simulator_Window.class.getResourceAsStream("/resources/clipboard.jpg");
			    BufferedImage img = null;
			    try 
			    {
			    	img = ImageIO.read(url);
			    }catch(Exception e) 
			    {
			    	
			    }
			    ImageIcon icon = new ImageIcon(img);
			    JButton copyAdress = new JButton(icon);
			    copyAdress.addActionListener(new ActionListener() {
			    	public void actionPerformed(ActionEvent arg0) 
			    	{
			    		StringSelection selection = new StringSelection(ipAdress.getText());
			    		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			    		clipboard.setContents(selection, selection);
			    	}
			    });
			    myPanel.add(copyAdress);
			    
			    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
			    myPanel.add(new JLabel("Port:"));
			    myPanel.add(port);
			    
			    JButton copyPort = new JButton(icon);
			    copyPort.addActionListener(new ActionListener() {
			    	public void actionPerformed(ActionEvent arg0) 
			    	{
			    		StringSelection selection = new StringSelection(port.getText());
			    		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			    		clipboard.setContents(selection, selection);
			    	}
			    });
			    
			    myPanel.add(copyPort);
			    
			    int result = JOptionPane.showConfirmDialog(null, myPanel,
			            "Connect the server with this values", JOptionPane.CLOSED_OPTION);
			        if (result == JOptionPane.CLOSED_OPTION) {
			          System.out.println("Port value: " + port.getText());
			        }
			}
		});
		mnSimulator.add(btnIoServer);
		
		JButton btnInfo = new JButton("Info");
		btnInfo.setMaximumSize(new Dimension(115, 23));
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		        InputStream url = Simulator_Window.class.getResourceAsStream("/resources/pic.png");
				BufferedImage img = null;
				try {
				    img = ImageIO.read(url);
				} catch (IOException e) {
				}
				Icon icon = new ImageIcon(img);
				final JComponent[] inputs = new JComponent[] {
				        new JLabel("                       PicSimulator666                      \n"),
				        new JLabel("This Software was developed by Toni Einecker & Tobias B�hler\n"),
				        new JLabel("                        Copyright 2020                      ")
				};
				int result = JOptionPane.showConfirmDialog(null, inputs, "Info", JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,icon);
				if (result == JOptionPane.OK_OPTION) {

				} else {
				    System.out.println("User canceled / closed the dialog, result = " + result);
				}
			}
		});
		mnSimulator.add(btnInfo);
		
		JButton btnHelp = new JButton("Help");
		btnHelp.setMaximumSize(new Dimension(115, 23));
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					InputStream url = Simulator_Window.class.getResourceAsStream("/resources/Dokumentation_PIC_Simulator.pdf");
					Path path = Files.createTempFile("doku", ".pdf");
					try (FileOutputStream out = new FileOutputStream(path.toFile())) {
				        byte[] buffer = new byte[1024]; 
				        int len; 
				        while ((len = url.read(buffer)) != -1) { 
				            out.write(buffer, 0, len); 
				        }
				    } catch (Exception e) {
				    }
					Desktop.getDesktop().open(path.toFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		mnSimulator.add(btnHelp);
		

	}
	
	/**
	 * Method to input data in the memory table.
	 * @param obj The object to set.
	 * @param row_index The row of the cell.
	 * @param col_index The column of the cell.
	 */
	  public void SetData(Object obj, int row_index, int col_index){
		  table_Memory.getModel().setValueAt(obj,row_index,col_index);
		  table_Memory.setModel(tbl_memory);
		  frmMicrocontrollerSimulator.repaint();
	  }
	  
	  /**
	   * Method to get data from a cell from the memory table.
	   * @param row The row of the cell.
	   * @param col The column of the cell.
	   * @return The value of the cell.
	   */
	  public String getData(int row, int col) 
	  {
		  String out =  (String) table_Memory.getModel().getValueAt(row, col);
		  return out;
	  }
	  
	  /**
	   * Method to set the value of the 4 digits of the 7-Segment display.
	   * @param c1 The value of the first digit.
	   * @param c2 The value of the second digit.
	   * @param c3 The value of the third digit.
	   * @param c4 The value of the fourth digit.
	   */
	  public void setSegment(int c1, int c2, int c3, int c4) 
	  {
		  this.panel_segmentCanvas.setChars(c1,c2,c3,c4);
		  this.panel_segmentCanvas.repaint();
	  }
	  
	  /**
	   * Method to get the data of a cell from the special register table.
	   * @param obj The table object.
	   * @param row_index The row of the cell.
	   * @param col_index The column of the cell.
	   */
	  public void setSpecialData(Object obj,int row_index,int col_index) 
	  {
		  table_special_regs.getModel().setValueAt(obj, row_index, col_index);
	  }
	  
	  /**
	   * Method to set the value of a cell from the stack table.
	   * @param obj The table object.
	   * @param row_index The row of the cell.
	   * @param col_index The column of the cell.
	   */
	  public void setStackData(Object obj,int row_index,int col_index) 
	  {
		  table_Stack.getModel().setValueAt(obj, row_index, col_index);
	  }
	  
	  /**
	   * Method to open the help data.
	   */
	  public void openHelp() {

			try {
			  File helpFile = new File("out/html/annotated.html");
			  Desktop.getDesktop().browse(helpFile.toURI());
			  //help.setPage(helpFile.toURI().toURL());
			}catch (IOException e) {
				ctr.showError("File Open Error", e.getMessage());
			} 
	  }
	  
	  /**
	   * Method to get the value of the port A button on the gui.
	   * @return The value of port A.
	   */
	  protected int getPortA() 
	  {
		  int ra = 0;
		  
		  ra = ra + 0x00; // Read as zero
		  ra = ra + 0x00; // Read as zero
		  ra = ra + 0x00; // Read as zero
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
	  
	  /**
	   * Method to set the radio buttons on the gui for port A.
	   * @param ra The value of port A.
	   */
	  protected void setPortA(int ra) 
	  {
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
	  
	  /**
	   * Method to get the value of the port B button on the gui.
	   * @return The value of port B.
	   */
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
	  
	  /**
	   * Method to set the radio buttons on the gui for port B.
	   * @param ra The value of port B.
	   */
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
	  
	  /**
	   * Method to set the labels for tris A, displaying if the bits of port A are inputs or outputs.
	   * @param tris The value of tris A.
	   */
	  protected void setTrisA(int tris) 
	  {
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
	  
	  /**
	   * Method to set the labels for tris B, displaying if the bits of port B are inputs or outputs.
	   * @param tris The value of tris B.
	   */
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
	  
	  /**
	   * Method to clear the highlight of the active row from the code view table.
	   */
	  protected void clearRowHiglights() {
		  table_Code.removeRowSelectionInterval(0, this.table_Code.getRowCount() - 1);
	  }
	  /**
	   * Method to highlight a row in the code view table.
	   * @param row The row to highlight.
	   */
	  protected void highlightRow(int row) 
	  {
		  table_Code.removeRowSelectionInterval(0, this.table_Code.getRowCount() - 1);
		  table_Code.addRowSelectionInterval(row, row);
	  }
	  
	  /**
	   * Method to highlight a cell in the memory table.
	   * @param x The row of the cell.
	   * @param y The column of the cell.
	   */
	  protected void highlightCell(int x, int y) 
	  {
		  try {
			table_Memory.addRowSelectionInterval(x, x);
			table_Memory.addColumnSelectionInterval(y, y);
		  }catch(NullPointerException e) {
			  // Exception occures in the initialization and can therefore be ignored.
		  }
	  }
	  
	  /**
	   * Method to remove a highlight from a cell of the memory table.
	   * @param x The row of the cell.
	   * @param y The column of the cell.
	   */
	  protected void removeHighlightCell(int x, int y) 
	  {
		  table_Memory.removeRowSelectionInterval(x, x);
		  table_Memory.removeColumnSelectionInterval(y, y);
	  }
	  
	  /**
	   * Getter for 7 Segment Panel.
	   * @return Reference to SevenSegmentPanel object.
	   */
	  protected SevenSegmentPanel getSevenSegmentPanel() 
	  {
		  return panel_segmentCanvas;
	  }
	  
	  /**
	   * Getter for access to  code view table.
	   * @return Reference to object of code table.
	   */
	  protected JTable getTableCode() 
	  {
		  return table_Code;
	  }
	  
	  /**
	   * Getter for access to the TableModel of the code table.
	   * @return Reference to code table model.
	   */
	  protected DefaultTableModel getTblCodeModel() 
	  {
		  return tbl_code;
	  }
	  
	  /**
	   * Getter for access to the TableModel of the memory table.
	   * @return Reference to memory table model.
	   */
	  protected DefaultTableModel getTblMemoryModel() 
	  {
		  return tbl_memory;
	  }
	  
	  /**
	   * Getter for access to the TableModel of the special register table.
	   * @return Reference to special register table model.
	   */
	  protected DefaultTableModel getTblSpecialModel() 
	  {
		  return tbl_special;
	  }
	  
	  /**
	   * Getter for access to the TableModel of the stack table.
	   * @return Reference to stack table model.
	   */
	  protected DefaultTableModel getTblStackModel() 
	  {
		  return tbl_stack;
	  }
	  
	  /**
	   * Getter for access to the memory table.
	   * @return Reference to code table.
	   */
	  protected JTable getTableMemory() 
	  {
		  return table_Memory;
	  }
	  
	  /**
	   * Getter for access to the special register table.
	   * @return Reference to special register table.
	   */
	  protected JTable getTableSpecialReg() 
	  {
		  return table_special_regs;
	  }
	  
	  /**
	   * Method to update the operational time on the GUI.
	   * @param opTime Operational time to display.
	   */
	  protected void updateOperationalTime(double opTime) 
	  {
		  this.lblOperationalTime.setText(opTime+"ms");
	  }
	/**
	 * Returns the reference to toggle button sleeping
	 * @return the tglbtnSleeping
	 */
	protected JToggleButton getTglbtnSleeping() {
		return tglbtnSleeping;
	}
	/**
	 * @return the tglbtnMclr
	 */
	protected JToggleButton getTglbtnMclr() {
		return tglbtnMclr;
	}
	/**
	 * 
	 * @return
	 */
	protected JTextField getSliderText() {
		return this.sliderText;
	}
	/**
	 * 
	 * @return
	 */
	protected JSlider getSlider() {
		return this.slider;
	}
}