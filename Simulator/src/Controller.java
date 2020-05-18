import java.io.*;

import javax.swing.table.TableColumn;

/// class Controller
/**
*  This class is the heart of this Simulator.
*  It is the connection between all classes. Each interaction by a user on the GUI is executed in the controller class.
*  Here are objects of processor, memory, timer, interrupt and watchdog
* **/
public class Controller {
	
	/// Object of main gui.
	private Simulator_Window gui;
	/// Object of mnemonic editor gui.
	private MnemonicView mnemonicWindow;
	/// Object of a error dialog.
	private ErrorDialog errorView;
	/// Object of io server.
	private IOServer server;
	
	/// Processor object used to work each code step
	private Processor proc;
	/// memory object used to store the data of microprocessor
	private Memory memory;
	/// Parser Object to parse Mnemonic Code into Binary Code
	private MnemonicParser parser;
	/// Timer object to check timer pin and do timer operations
	private Timer tmr0;
	/// Interrupt object to check interrupt flags and if needed set pc to 0004
	private Interrupt isr;
	/// Object of the Watchdog.
	private Watchdog wtd;
	/// EEPROM Memory instance to save data into txt file
	private EEProm eeprom;
	
	private boolean processorRunning = false;
	/// Displaying if the code is compiled.
	private boolean isCompiled = false;
	/// The data model to initialize the data table.
	private String[][] tableData = new String[32][9];
	
	private int[][] tableHighlight = new int[32][9];
	/// An array holding the jumper line number and the mnemonic code.
	/**
	 * The jumpers are holding the mnemonic code line with an ':' and the program counter appended.
	 * They are listed starting at 0.
	 */
	private String[] jumpers = new String[512];


	/// Amount of jumper marks in the code.
	private int jumpersCount = 0;
	
	/// An array holding the EQUs.
	/**
	 * One entry holds the "original" before the EQU, followed by an ':' appended with the value after the EQU.
	 */
	private String[] equ = new String[256];
	/// Amount of EQUs in the code.
	private int equCount = 0;
	/// The mnemonic code.
	private String[] mnemonicLines;
	/// A list of the program counter as key with the dedicated code line as value
	private int[] programCounterList = new int[1024];

	/// A list of breakpoints
	private boolean[] breakPointList = new boolean[1024];
	

	/// The length of the compiled code
	private int codeLength = 0;
	/// The Quartz frequency 
	private int frequency = 500;


	/// Signals that the next cycle is a nop
	private boolean isNopCycle = false;
	
	/// 
	private boolean writeActive = false;
	
	/// time since simulator program started
	private double operationalTime = 0.0;
	


	/// Bool to indicate if 7 segment is actiavted or not
	private boolean sevenSegmentActive = false;


	/// Bool to indicate the controlPort for 7Seg
	/**
	 *  1 indicates port b
	 *  0 indicates port a
	 */
	private int controlPortSelect = 0;


	/// Bool to indicate the dataPort for 7Seg
	/**
	 *  0 indicates port a
	 *  1 indicates port b
	 */
	private int dataPortSelect = 1;
	
	/**
	*  The Constructor, creating a new Memory and MnemonicParser.
	*  @param pGui Is an Object of {@link Simulator_Window}
	* **/
	public Controller(Simulator_Window pGui) 
	{
		this.gui = pGui;
		memory = new Memory(this);
		parser = new MnemonicParser(this);
		tmr0 = new Timer(this);
		isr = new Interrupt(this);
		server = new IOServer(this);
		eeprom = new EEProm(this);
		wtd = new Watchdog(this);
		
	}
	
	/**
	*  Method to initialize the Memory.
	*  
	* **/
	public void inizializeMemory() 
	{
		String[] numbers = new String[256];
		int[][] data = new int[32][8];
		for(int i = 0; i< 256; i++) {
			numbers[i] = Integer.toHexString(i);
			data[i/8][i%8] = 0;
			tableData[i/8][i%8+1] = Integer.toString( data[i/8][i%8]);
		}
		for(int i = 0; i < 32; i++) {
			tableData[i][0] = Integer.toHexString(i*8);
			this.gui.getTblMemoryModel().addRow(new Object[] {tableData[i][0],"0","0","0","0","0","0","0","0"});
		}
		// initialization of special register table
		this.gui.getTblSpecialModel().addRow(new Object[] { "W-Reg", "00","00000000" });
		this.gui.getTblSpecialModel().addRow(new Object[] { "FSR", "00","00000000" });
		this.gui.getTblSpecialModel().addRow(new Object[] { "PCL", "30","00110000" });
		this.gui.getTblSpecialModel().addRow(new Object[] { "PCLATH", "00","00000000" });
		this.gui.getTblSpecialModel().addRow(new Object[] {"PC", "0030","00000000"});
		this.gui.getTblSpecialModel().addRow(new Object[] { "Status", "C0","10100000" });
		
		for(int i = 0; i < 8; i++) 
		{
			this.gui.getTblStackModel().addRow(new Object[] {i,""});
		}
	}
	/**
	 * Starts a thread to cyclic update the memory table.
	 */
	public void startMemoryUpdateThread() {
		memory.start();
	}
	
	/**
	*  Method to input a value into a specific cell of the memory table.
	*  @param value is an String which is put in the cell
	*  @param x is an integer referencing to the column
	*  @param y is an integer referencing to the row + 1, because the first row are the labels.
	* **/
	protected void updateMemoryTable(String value, int x, int y) 
	{
		while (value.length() < 2) {
			value = "0" + value;
		}
		gui.SetData(value, x, y+1);
	}
	
	/**
	 * Method to input a value into a specific cell of the special register table.
	 * @param value is an String which is put in the cell
	 * @param x is an integer referencing to the column
	 * @param y is an integer referencing to the row
	 */
	protected void updateSpecialRegTable(String value, int x, int y) 
	{
		// When editing the binary values display 8 digits.
		if(y == 2) {
			while (value.length() < 8) {
				value = 0 + value;
			}
		}
		gui.setSpecialData(value, x, y);
	}
	/**
	 * Method to update the stack panel with the data in memory variable stack 
	 */
	protected void updateStackPanel(String value, int y) 
	{
		gui.setStackData(value, y, 1);
	}
	
	protected void setBreakPoint(int row) 
	{
		for(int i = 0; i < programCounterList.length; i++) 
		{
			if(programCounterList[i]-1 == row) 
			{
				if(breakPointList[i] == false) 
				{
					breakPointList[i] = true;
					this.gui.getTblCodeModel().setValueAt("O", row, 0);
				}else 
				{
					breakPointList[i] = false;
					this.gui.getTblCodeModel().setValueAt(" ", row, 0);
				}
			}
		}
	}
	
	/**
	*  Try to open a new {@link MnemonicView} and display it. 
	*  Then try to load the Mnemonic code from the code view table.
	*  @exception Exception Is catched and printed.
	* **/
	public void openMnemonicView()
	{
		try {
			mnemonicWindow = new MnemonicView(this);
			mnemonicWindow.setVisible(true);
			loadMnemonicFromTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	*  Method to create a new {@link ErrorDialog} with variable title and text and display it.
	*  @param title is a String and the title of the dialog
	*  @param text is a String and the text of the dialog
	* **/
	public void showError(String title,String text) 
	{
		errorView = new ErrorDialog();
		errorView.setVisible(true);
		errorView.lbl_ErrorTitle.setText(title);
		errorView.lbl_ErrorText.setText(text);
	}
	/**
	 * this method will update the seven segment panel 
	 * there are a control input and a data input needed
	 * these inputs can be choosen (Port a and Port b)
	 */
	protected void update7Segment() 
	{
		int controlPort = 0;
		int dataPort = 1;
		if(this.sevenSegmentActive) 
		{
			if(this.controlPortSelect == 0) 
			{
				controlPort = this.memory.get_MemoryDIRECT(0x05);
			}else if(this.controlPortSelect == 1)
			{
				controlPort = this.memory.get_MemoryDIRECT(0x06);
			}
			
			if(this.dataPortSelect == 0) 
			{
				dataPort = this.memory.get_MemoryDIRECT(0x05);
			}else if(this.dataPortSelect == 1)
			{
				dataPort = this.memory.get_MemoryDIRECT(0x06);
			}
			System.out.println("set7Segment: "+controlPort+" data: "+dataPort);
			this.gui.getSevenSegmentPanel().set7Segment(controlPort, dataPort);
		}
	}
	// used to update the operationTime panel in gui
	protected void updateOperationalTime() 
	{
		gui.updateOperationalTime(this.operationalTime);
		//System.out.println("Operation time: " + this.operationalTime);
	}
	/**
	 * add the cycle time to the operationalTime and round to 3 decimal digits
	 */
	protected void countCycleTime(int frequency) 
	{
		double d = Math.pow(10, 6);
	    this.operationalTime = this.operationalTime + (4.0/(frequency));
	    this.operationalTime = Math.round(this.operationalTime * d) / d;
	}
	/**
	*  Method to load the program code from the code view table.
	*  If labels are found in the 5th column they are added.
	*  Otherwise the blank mnemonic code from the 6th column is checked for EQUs. If it contains EQUs the line is added, otherwise the line is added with a space as prefix.
	*  Then the mnemonic code string is loaded into the mnemonic editor.
	* **/
	protected void loadMnemonicFromTable() 
	{
		String mnemonic = "";
		// iterate over the code view table
		for (int i = 0; i < gui.getTblCodeModel().getRowCount(); i++) {
			// check for Labels and add if found
			String label = gui.getTblCodeModel().getValueAt(i, 4).toString();
			if(!label.isEmpty()) {
				mnemonic = mnemonic + label + "\n";
			}
			else {
				// else check if line is an EQU and add it, otherwise add it with an space as prefix
				String code = gui.getTblCodeModel().getValueAt(i, 5).toString();
				if (code.contains("EQU")) {
					mnemonic = mnemonic + code + "\n";
				}
				else {
					mnemonic = mnemonic + "  " + code + "\n";
				}
			}
		}
		// add and repaint.
		mnemonicWindow.getTxtArea_mnemonic().setText(mnemonic);
		mnemonicWindow.getTxtArea_mnemonic().repaint();
	}

	/**
	*  Method to start the simulation.
	*  If the code is compiled ({@link isCompiles}) a new {@link Processor} will be created and started.
	*  Otherwise and an error window will be displayed.
	*  @see showError
	* **/
	public void startSimu(boolean debugging) {
		System.out.println("Simulation started...");
		if (this.isCompiled) {
			if (!this.processorRunning) {
				proc = new Processor(this, debugging);
				proc.start();
				this.processorRunning = true;
			}
			else {
				this.showError("Start Simulation Error", "Simulation is already running.");
			}
		}
		else {
			this.showError("Start Simulation Error", "Code isnt compiled. Please compile first and try it again.");
		}

	}
	
	/**
	*  Method to stop the Simulation. The active processor thread will be stopped via {@link stopThread}.
	* **/
	public void stopSimu() {
		System.out.println("Simulation stopped...");
		this.processorRunning = false;
		proc.stopThread();
	}
	
	/**
	*  Method to stop the Simulation. The active processor thread will be stopped via {@link stopThread}.
	* **/
	public void continueDebugStep() {
		if (processorRunning) {
			proc.continueDebugStep();
		}else {
			this.showError("Next Step Degubber", "Debugger is not running. Please start debugging before continuing to the next line.");
		}
		
	}

	/**
	 * Method to check if there are any jump marks in the uncompiled code. 
	 * Found marks are added to jumpers list with "'label'+':'+'codeline'".
	 * @param pCode is a String array which holds the program code
	 * @return the given program code
	 * **/
	public String[] searchJumperMarks(String[] pCode) 
	{
		// iterate over the uncompiled code.
		for (int i = 0; i < pCode.length; i++) {
			if ((pCode[i].charAt(0) != ' ') && (pCode[i].contains("EQU") == false)) {
				// add found jumper mark and increment jumper count
				jumpers[this.jumpersCount] = pCode[i] + ":" + (i + 1);				
				jumpersCount++;
				
				System.out.println("JumperMark found: "+pCode[i]+" at Line "+i+" to pc: "+this.memory.programmcounter);
			}
		}
		return pCode;
	}
	
	/**
	 * Method to search the EQU marks in the uncompiled code.
	 * @param pCode is a String array which holds the program code
	 * @return the given program code
	 * **/
	public String[] searchEQUMarks(String[] pCode) {
		// iterate over the uncompiled code
		for(int i = 0; i < pCode.length; i++) 
		{
			if(pCode[i].contains("EQU")) 
			{
				String beforeToken = ""; //value left (before) of the EQU
				String afterToken  = ""; //value right (after) of the EQU
				String[] tokenParts;
				tokenParts = pCode[i].split(" ");

				if(tokenParts.length > 2) 
				{
					for(int j = 0; j < tokenParts.length; j++) 
					{
						if(tokenParts[j].equals("EQU")) 
						{
							beforeToken = tokenParts[j-1];
							afterToken  = tokenParts[j+1];
							equ[this.equCount] = beforeToken + ":" + afterToken;
							this.equCount++;
							System.out.println("EQU found in Line " + i + " (" + pCode[i] + ")");
						}
					}
				}else {
					System.out.println("No valid EQU: "+pCode[i]);
				}
			}
		}
		return pCode;
	}
	
	/**
	 * Method to get the EQU value.
	 * When no matching EQU is found an empty string will be returned.
	 * @param equName is the name of the EQU as a String.
	 * @return The EQU equivilant as a String.
	 * **/
	public String getEQUValue(String equName) 
	{
		String out = "";
		// iterate over the EQU list
		for(int i = 0; i < this.equCount; i++) {
			String[] j = equ[i].split(":");
			if(j[0].equals(equName)) 
			{
				out = j[1];
			}
		}
		if(out.equals("")) 
		{
			out = equName;
		}
		return out;
	}
	

	
	/**
	 * Method to set the 4 values of the 7-Segment display.
	 * @param c1 is the first integer number (most left).
	 * @param c2 is the second integer number.
	 * @param c3 is the third integer number.
	 * @param c4 is the fourth integer number (most right).
	 * **/
	public void setSegment(int c1,int c2, int c3, int c4) 
	{
		this.gui.setSegment(c1, c2, c3, c4);
	}
	
	/**
	 * Method to load a file.
	 * The code table and program counter list will be cleared.
	 * @param pFile is the File to be loaded.
	 * **/
	public void loadFile(File pFile) throws IOException 
	{
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(pFile));
			
		} catch (FileNotFoundException e) {


			e.printStackTrace();
		}
		String st; 

    	
    	// initialisieren der grafischen Elemente
		clearCodeTable();
		clearProgramCounterList();

    	// einlesen der einzelnen Zeilen inklusive equ und labels
		while ((st = br.readLine()) != null) { 
	    	String label = "";
			
			String code = parser.hexToBinary(st.substring(5, 9));
			for(int i = code.length(); i<14; i++) 
			{
				code = "0" + code;
			}
			// erkennen des Labels
			if(st.charAt(27) != ' ') 
			{ // marke erkannt
				int l_index = 27;
				while(st.charAt(l_index) != ' ') 
				{
					label = label + st.charAt(l_index);
					l_index++;
				}
			}
			if(!label.isEmpty()) 
			{
				gui.getTblCodeModel().addRow(new Object[] {" ",st.substring(0, 4),st.substring(5, 9),st.substring(20, 25),label,""});
			}else 
			{
				gui.getTblCodeModel().addRow(new Object[] {" ",st.substring(0, 4),st.substring(5, 9),st.substring(20, 25),label, st.substring(36)});
			}

			if(!st.substring(0, 4).equals("    ")) 
			{
				this.codeLength = Integer.parseInt(st.substring(0, 4),16);
				memory.getProgramMemory()[codeLength] = Integer.parseInt(st.substring(5, 9), 16);
				programCounterList[codeLength] = Integer.parseInt(st.substring(20, 25));
			}

		}
		this.setColumnWidth();
		this.isCompiled = true;
	}
	
	/**
	 * Method to save the mnemonic code of the opened editor.
	 * If the code is not compiled, {@link compileCode} will be called.
	 * @param text is the File to be loaded.
	 * **/
	public void saveMnemonicCode(String text) {
		String[] splittedMnemonic = text.replaceAll("\\r", "").split("\\n");
		mnemonicLines = new String[splittedMnemonic.length];
		mnemonicLines = splittedMnemonic;
		this.isCompiled = false;
		this.compileCode();
	}
	
	/**
	 * Method to set the arrow which displays the active step to a new row.
	 * @param newC the new Counter row
	 * **/
	public void setCodeViewCounter(int newC) 
	{
		// first column is used for breakpoints
		/*
		 *  for(int i = 0; i < this.gui.tbl_code.getRowCount(); i++) {
			this.gui.tbl_code.setValueAt(" ", i, 0);
			}
			this.gui.tbl_code.setValueAt("->", newC-1, 0);
		 * 
		 * 
		 * */

		this.gui.highlightRow(newC - 1);
	}
	
	/**
	 * Method to get get the amount of jumpers.
	 * @return The number of jump marks as integer
	 * **/
	protected int getJumpersCount() 
	{
		return this.jumpersCount;
	}
	
	/**
	 * Method to compile the code
	 * needs to be reworked
	 * **/
	public void compileCode() {
		// local program counter variable 
		int pc = 0;
		
		if(!this.isCompiled) 
		{
			// delete all memory from last code
			this.memory.clearProgMem();
			
	    	// initialisieren der grafischen Elemente
			clearCodeTable();
			clearProgramCounterList();
			
			// suche nach variablen marken , speichern der wertpaare
			this.mnemonicLines = this.searchEQUMarks(this.mnemonicLines);
			//suche nach jumper marken
			this.mnemonicLines = this.searchJumperMarks(this.mnemonicLines);
			
			for(int j = 0; j <this.mnemonicLines.length;j++) 
			{
				String proceed;
				// comments should be deleted
				if(this.mnemonicLines[j].contains(";")) 
				{
					proceed = this.mnemonicLines[j].substring(0, this.mnemonicLines[j].indexOf(";"));
				}else 
				{
					proceed = this.mnemonicLines[j];
				}
				
				// needs check if empty lines with multiple spaces exist and delete these spaces 
				if(proceed.isEmpty() || (proceed.length() < 5 &&  proceed.startsWith(" "))) 
				{
					// empty line 
					gui.getTblCodeModel().addRow(new Object[]{"","    ", "    ", j+1 , "",""});
				}else if(proceed.contains("org") || proceed.contains("device 16")) 
				{
					// org statement should change the pc
					String[] tmp = proceed.split(" ");
					for(int i = 0; i < tmp.length;i++) 
					{
						if(tmp[i].equals("org")) 
						{
							if(i+1 < tmp.length) 
							{
								int newPC = Integer.parseInt(tmp[i+1]);
								pc = newPC;
							}
						}
					}
					gui.getTblCodeModel().addRow(new Object[]{"","    ", "    ", j+1 , "",mnemonicLines[j]});
				}else if(proceed.contains("EQU")) 
				{
					// EQU should not affect any variable
					gui.getTblCodeModel().addRow(new Object[]{"","    ", "    ", j+1 , "",mnemonicLines[j]});
				}else if(proceed.charAt(0) != ' ') 
				{
					// if the first char in a line is unequal to space it is a label
					// pay attention that the EQU is checked before, because EQU has unequal first character too
					
					
					gui.getTblCodeModel().addRow(new Object[]{"","    ", "    ", j+1 , proceed,""});
				}else if(this.mnemonicLines[j].charAt(0) == ' ') 
				{
					
					// normal code line, executed by parser 
					System.out.println("Controller: "+proceed);
					String binaryCode = parser.fromMnemToHex(proceed, j).toString();
					
					gui.getTblCodeModel().addRow(new Object[]{"",Integer.toHexString(pc), Integer.toHexString(Integer.parseInt(binaryCode, 2)), j+1 , "",mnemonicLines[j]});
					this.memory.getProgramMemory()[pc] = Integer.parseInt(binaryCode, 2);
					
					programCounterList[pc] = j+1;
					// pc needs to be incremented
					pc++;
				}
			}
			this.isCompiled = true;
		}else {
			System.out.println("Mnemonic-Code is already compiled...");
		}
	}
	
	/**
	 * Method to set the column width of the code view table
	 * **/
	public void setColumnWidth() 
	{
		TableColumn column = null;
		for (int i = 0; i < 3; i++) {
		    column = gui.getTableCode().getColumnModel().getColumn(i);
		    if (i < 2 ) {
		        column.setPreferredWidth(3); //third column is bigger
		        column.setResizable(false);
		    } else {
		        column.setPreferredWidth(100);
		    }
		}
	}
	
	/**
	 * Method to initialize the labels of all tables.
	 * **/
	public void inizializeTables() 
	{
		gui.getTblCodeModel().setColumnIdentifiers	(new Object[] {" ", "ProgramCounter", "ProgramCode", "LineCount","Label","MnemonicCode"});
		gui.getTblMemoryModel().setColumnIdentifiers	(new Object[] {"","00","01","02","03","04","05","06","07"});
		gui.getTblSpecialModel().setColumnIdentifiers(new Object[]{"Register", "Hex-Wert", "Bin-Wert"});
	}

	/**
	 * This method selects the command which must be executed
	 * @param command the command to execute as a String
	 * **/
	public void executeCommand(int line) 
	{
		int precommand 	= (line >> 12) 	& 0x0003;
		int command 	= (line >> 8) 	& 0x000F;
		int payload 	= line 			& 0x00FF;
		if (precommand == 0) {		// Byte Oriented File Register Operations
			int d = payload >> 7;
			int f = payload & 0b01111111;
			// Check for indirect adressing
			if (f == 0x00 || f == 0x80) {
				f = this.memory.get_MemoryDIRECT(0x04);
			}
			
			
			switch(command) {
			case 0b0111:
				this.addwf(d, f);
				break;
			case 0b0101:
				this.andwf(d, f);
				break;
			case 0b0001:
				if (d == 1) {
					this.clrf(f);
				}else {
					this.clrw();
				}
				break;
			case 0b1001:
				this.comf(d, f);
				break;
			case 0b0011:
				this.decf(d, f);
				break;
			case 0b1011:
				this.decfsz(d, f);
				break;
			case 0b1010:
				this.incf(d, f);
				break;
			case 0b1111:
				this.incfsz(d, f);
				break;
			case 0b0100:
				this.iorwf(d, f);
				break;
			case 0b1000:
				this.movf(d, f);
				break;
			case 0b0000:
				if 		((payload >> 7) == 1) {
					this.movwf(f);
				}
				else if (payload == 0b01100100) {
					this.clrwdt();
				}
				else if	(payload == 0b00001001) {
					this.retfie();
				}
				else if	(payload == 0b00001000) {
					this._return();
				}
				else if (payload == 0b01100011) {
					this.sleep();
				}
				else {
					this.nop();
				}
				break;
			case 0b1101:
				this.rlf(d, f);
				break;
			case 0b1100:
				this.rrf(d, f);
				break;
			case 0b0010:
				this.subwf(d, f);
				break;
			case 0b1110:
				this.swapf(d, f);
				break;
			case 0b0110:
				this.xorwf(d, f);
				break;
			default:
				System.out.println("There is no command for the inserted string: " + line);
				break;
			}
		}
		else if (precommand == 1) {	// Bit-Oriented File Register Operations
			int b = (line >> 7) & 0x0007;
			int f = line  		& 0x007F;
			// Check for indirect adressing
			if (f == 0x00 || f == 0x80) {
				f = this.memory.get_MemoryDIRECT(0x04);
			}
			
			switch(command >> 2) {
			case 0b00:
				this.bcf(b, f);
				break;
			case 0b01:
				this.bsf(b, f);
				break;
			case 0b10:
				this.btfsc(b, f);
				break;
			case 0b11:
				this.btfss(b, f);
				break;
			}
		}
		else if (precommand == 2) {	// Literal and control operations
			int k = line & 0x07FF;
			
			if ((command >> 3) == 0) {
				this.call(k);
			}
			else {
				this._goto(k);
			}
		}
		else if (precommand == 3) {
			int k = line & 0x00FF;
			if ((command >> 1) == 7) {
				this.addlw(k);
			}
			else if (command == 0b1001) {
				this.andlw(k);
			}
			else if (command == 0b1000) {
				this.iorlw(k);
			}
			else if ((command >> 2) == 0)  {
				this.movlw(k);
			}
			else if ((command >> 2) == 1)  {
				this.retlw(k);
			}
			else if ((command >> 1) == 6)  {
				this.sublw(k);
			}
			else if (command == 0b1010)  {
				this.xorlw(k);
			}
		}
		else {
			System.out.println("There is no command for the inserted string: " + line);
		}
		
	}
	
	//
	// BYTE-ORIENTED FILE REGISTER OPERATIONS
	//
	
	/**
	 * This method executes the ADDWF command.
	 * Add the contents of the W register with register f. If d is 0 the result is stored in the W register. 
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void addwf(int d, int f) 
	{
		int w_in = memory.get_WREGISTER();
		int f_in = memory.get_Memory(f);
		
		int result = f_in+w_in;
		if(result > 255) 
		{
			this.memory.set_CARRYFLAG(1);
			result = result - 256;
		}else 
		{
			this.memory.set_CARRYFLAG(0);
		}
		this.checkZeroFlag(result);
		this.checkDCFlag(w_in, f_in);
		
		if(d == 0)
		{
			memory.set_WREGISTER(result);
		}else if(d == 1) 
		{
			memory.set_SRAM(f,result);
		}
	}
	
	/**
	 * This method executes the ANDWF command.
	 * AND the W register with register 'f'. 
	 * If 'd' is 0 the result is stored in the W register. If 'd' is 1 the result is stored back in register 'f'.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void andwf(int d, int f) 
	{
		int w_in = memory.get_WREGISTER();
		int f_in = memory.get_Memory(f);

		
		int result = w_in & f_in;
		
		this.checkZeroFlag(result);

		if(d == 0 )
		{
			memory.set_WREGISTER(result);
		}else if(d == 1) 
		{
			memory.set_SRAM(f, result);
		}
	}
	
	/**
	 * This method executes the CLRF command.
	 * The contents of register f are cleared and the Z bit is set.
	 * @param f The file register location as String
	 * **/
	private void clrf(int f) 
	{
		memory.set_SRAM(f, 0);
		if(f != 3 && f != 131) 
		{
			this.checkZeroFlag(0);
		}
	}
	
	/**
	 * This method executes the CLRW command.
	 * W register is cleared. Zero bit (Z) is set.
	 * **/
	private void clrw() 
	{
		memory.set_WREGISTER(0);
		this.checkZeroFlag(0);
	}
	
	/**
	 * This method executes the COMF command.
	 * The contents of register f are complemented. 
	 * If d is 0 the result is stored in W. If d is 1 the result is stored back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void comf(int d, int f) 
	{
		int in = memory.get_Memory(f);
		
		// write in to w reg???????????????????????
		
		int out = 255 - in;
		
		this.checkZeroFlag(out);
		
		if(d == 0) 
		{
			memory.set_WREGISTER(out);
		}else if(d == 1) 
		{
			memory.set_SRAM(f, out);
		}
	}
	
	/**
	 * This method executes the DECF command.
	 * Decrement register f. 
	 * If d is 0 the result is stored in the W register. If d is 1 the result is stored back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void decf(int d, int f) 
	{
		int in = memory.get_Memory(f);
		if(in == 0) 
		{
			in = 255;
		}else {
			in--;
		}
		
		this.checkZeroFlag(in);
		
		if(d == 0) 
		{
			memory.set_WREGISTER(in);
		}else if(d == 1) 
		{
			memory.set_SRAM(f, in);
		}
	}
	
	/**
	 * This method executes the DECFSZ command.
	 * The contents of register f are decremented. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is placed back in register f.
	 * If the result is 1, the next instruction, is executed. If the result is 0, then a NOP is executed instead making it a 2TCY instruction.	 
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void decfsz(int d, int f) 
	{
		int in = memory.get_Memory(f);
		if(in == 0) 
		{
			in = 255;
		}else {
			in--;
			if(in == 0) 
			{
				//this.memory.programmcounter++;
				this.memory.set_PROGRAMMCOUNTER(this.memory.get_PROGRAMMCOUNTER() + 1);
				this.setNopCycle(true);
			}
		}
		if(d == 0) 
		{
			memory.set_WREGISTER(in);
		}else if(d == 1) 
		{
			memory.set_SRAM(f, in);
		}
		
	}
	
	/**
	 * This method executes the INCF command.
	 * The contents of register f are incremented. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is placed back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void incf(int d, int f) 
	{
		int in = memory.get_Memory(f);
		if(in == 255) 
		{
			in = 0;
		}else {
			in++;
		}
		
		this.checkZeroFlag(in);
		
		if(d == 0) 
		{
			memory.set_WREGISTER(in);
		}else if(d == 1) 
		{
			memory.set_SRAM(f, in);
		}
	}
	
	/**
	 * This method executes the INCFSZ command.
	 * The contents of register f are incremented. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is placed back in register f.
	 * If the result is 1, the next instruction is executed. If the result is 0, a NOP is executed instead making it a 2TCY instruction.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void incfsz(int d, int f) 
	{
		int in = memory.get_Memory(f);
		if(in == 255) 
		{
			in = 0;
			//this.memory.programmcounter++;
			this.memory.set_PROGRAMMCOUNTER(this.memory.get_PROGRAMMCOUNTER() + 1);
			this.setNopCycle(true);
		}else {
			in++;
		}
		if(d == 0) 
		{
			memory.set_WREGISTER(in);
		}else if(d == 1) 
		{
			memory.set_SRAM(f, in);
		}
	}
	
	/**
	 * This method executes the IORWF command.
	 * Inclusive OR the W register with register f. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is placed back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void iorwf(int d, int f) 
	{
		int w_in = memory.get_WREGISTER();
		int f_in = memory.get_Memory(f);
		
		int result = w_in | f_in;

		// Check not zeroflag
		this.checkZeroFlag(result);
		
		if(d == 0)
		{
			memory.set_WREGISTER(result);
		}else if(d == 1) 
		{
			memory.set_SRAM(f, result);
		}
	}
	
	/**
	 * This method executes the MOVF command.
	 * The contents of register f is moved to a destination dependant upon the status of d. 
	 * If d = 0, destination is W register. If d = 1, the destination is file register f itself.
	 * d = 1 is useful to test a file register since status flag Z is affected.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void movf(int d, int f) 
	{
		int f_in = this.memory.get_Memory(f);
		
		this.checkZeroFlag(f_in);
		if(d == 0)
		{
			memory.set_WREGISTER(f_in);
		}else if(d == 1) 
		{
			memory.set_SRAM(f, f_in);
		}
	}
	
	/**
	 * This method executes the MOVWF command.
	 * Move data from W register to register 'f'.
	 * @param f The file register location as String
	 * **/
	private void movwf(int f) 
	{
		int in = memory.get_WREGISTER();
		memory.set_SRAM(f, in);
	}
	
	/**
	 * This method executes the NOP command.
	 * No operation.
	 * **/
	private void nop() 
	{
		// do nothing
	}
	
	/**
	 * This method executes the RLF command.
	 * The contents of register f are rotated one bit to the left through the Carry Flag. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is stored back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void rlf(int d, int f) 
	{
		int in = memory.get_Memory(f);
		int carry = memory.get_CARRYFLAG();
		
		if ((in & 128) == 128) {
			memory.set_CARRYFLAG(1);
		}
		else {
			memory.set_CARRYFLAG(0);
		}
		int result = ((in << 1) & 0xFF) | (carry&1);
		if(d == 0) 
		{
			memory.set_WREGISTER(result);
		}else
		{
			memory.set_SRAM(f, result);
		}
	}
	
	/**
	 * This method executes the RRF command.
	 * The contents of register f are rotated one bit to the right through the Carry Flag. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is stored back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void rrf(int d, int f) 
	{
		int in = memory.get_Memory(f);
		int carry = memory.get_CARRYFLAG();
		
		if ((in & 1) == 1) {
			memory.set_CARRYFLAG(1);
		}
		else {
			memory.set_CARRYFLAG(0);
		}
		int result = (in >> 1) | (carry << 7);
		
		if(d == 0) 
		{
			memory.set_WREGISTER(result);
		}else if(d == 1) 
		{
			memory.set_SRAM(f, result);
		}
	}
	
	/**
	 * This method executes the SUBWF command.
	 * Subtract (2s complement method) W register from register 'f'. 
	 * If 'd' is 0 the result is stored in the W register. If 'd' is 1 the result is stored back in register 'f'.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void subwf(int d, int f) 
	{
		// TODO: WTF alles fucking falsch mit dc und carry flaggg
		int w_in = memory.get_WREGISTER();
		int f_in = memory.get_Memory(f);
		int result;
		if(w_in > f_in) 
		{
			result = 256 - (w_in-f_in);
		}else {
			result = f_in - w_in;
		}
		
		if (f_in - w_in < 0) {
			this.memory.set_CARRYFLAG(0);
		}else {
			this.memory.set_CARRYFLAG(1);
		}
		
		if (f_in - w_in > 15) {
			this.memory.set_DCFLAG(1);
		}else {
			this.memory.set_DCFLAG(0);
		}
		this.checkZeroFlag(result);
		
		if(d == 0)
		{
			memory.set_WREGISTER(result);
		}else if(d == 1) 
		{
			memory.set_SRAM(f, result);
		}
	}
	
	/**
	 * This method executes the SWAPF command.
	 * The upper and lower nibbles of register 'f' are exchanged. 
	 * If 'd' is 0 the result is placed in W register. If 'd' is 1 the result is placed in register 'f'.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void swapf(int d, int f) 
	{
		int f_in = memory.get_Memory(f);
		int result = ((f_in & 0x0F) << 4) | ((f_in & 0xF0) >> 4);

		if(d == 0)
		{
			memory.set_WREGISTER(result);
		}else if(d == 1) 
		{
			memory.set_SRAM(f, result);
		}
	}
	
	/**
	 * This method executes the XORWF command.
	 * Exclusive OR the contents of the W register with register 'f'. 
	 * If 'd' is 0 the result is stored in the W register. If 'd' is 1 the result is stored back in register 'f'.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void xorwf(int d, int f) 
	{
		int w_in = memory.get_WREGISTER();
		int f_in = memory.get_Memory(f);
		
		int result = f_in ^ w_in;
		
		this.checkZeroFlag(result);
		
		if(d == 0)
		{
			memory.set_WREGISTER(result);
		}else if(d == 1) 
		{
			memory.set_SRAM(f, result);
		}
	}
	
	//
	//START OF BIT-ORIENTED FILE REGISTER OPERATIONS
	//
	
	/**
	 * This method executes the BCF command.
	 * Bit b in register f is cleared
	 * @param b The bit as String
	 * @param f The file register location as String
	 * **/
	private void bcf(int b, int f) 
	{
		memory.set_SRAM(f, b, 0);
	}
	
	/**
	 * This method executes the BSF command.
	 * Bit b in register f is set.
	 * @param b The bit as String
	 * @param f The file register location as String
	 * **/
	private void bsf(int b, int f) 
	{
		memory.set_SRAM(f, b, 1);
	}
	
	/**
	 * This method executes the STFSC command.
	 * If bit b in register f is 1 then the next instruction is executed. 
	 * If bit b, in register f, is 0 then the next instruction is discarded, and a NOP is executed instead, making this a 2TCY instruction.
	 * @param b The bit as String
	 * @param f The file register location as String
	 * **/
	private void btfsc(int b, int f) 
	{
		int in = memory.get_Memory(f, b);
		if(in == 0) 
		{
			//this.memory.programmcounter++;
			this.memory.set_PROGRAMMCOUNTER(this.memory.get_PROGRAMMCOUNTER() + 1);
			this.setNopCycle(true);
		}
	}
	
	/**
	 * This method executes the BTFSS command.
	 * If bit b in register f is 0 then the next instruction is executed. 
	 * If bit b is 1, then the next instruction is discarded and a NOP is executed instead, making this a 2TCY instruction.
	 * @param b The bit as String
	 * @param f The file register location as String
	 * **/
	private void btfss(int b, int f) 
	{
		int in = memory.get_Memory(f, b);
		if(in == 1) 
		{
			//this.memory.programmcounter++;
			this.memory.set_PROGRAMMCOUNTER(this.memory.get_PROGRAMMCOUNTER() + 1);
			this.setNopCycle(true);
		}
	}
	
	//
	// BEGIN OF LITERAL AND CONTROL OPERATIONS
	//
	
	/**
	 * This method executes the ADDLW command.
	 * The contents of the W register are added to the eight bit literal k and the result is placed in the W register
	 * @param k The Literal as String.
	 * **/
	private void addlw(int k) 
	{
		int in = memory.get_WREGISTER();
		int result = in + k;

		if(result > 255) 
		{
			this.memory.set_CARRYFLAG(1);
			result = result - 256;
		}else 
		{
			this.memory.set_CARRYFLAG(0);
		}
		this.checkZeroFlag(result);
		this.checkDCFlag(in, k);
		
		memory.set_WREGISTER(in + k);
	}
	
	/**
	 * This method executes the ANDLW command.
	 * The contents of W register are ANDed with the eight bit literal 'k'. 
	 * The result is placed in the W register.
	 * @param k The Literal as String.
	 * **/
	private void andlw(int k) 
	{    
		int w_in = memory.get_WREGISTER();
		int result = w_in & k;
		
		this.checkZeroFlag(result);
		
		memory.set_WREGISTER(result);
	}
	
	/**
	 * This method executes the CALL command.
	 * Call Subroutine.
	 * @param k The Literal as String.
	 * **/
	private void call(int k) 
	{
		memory.pushToStack(this.memory.programmcounter);
		this.memory.programmcounter = k-1;
		this.setNopCycle(true);
	}
	
	/**
	 * This method executes the CLRWDT command.
	 * Clears the Watchdog timer.
	 * **/
	private void clrwdt() 
	{
		// TODO: CLRWDT implementieren
	}
	
	/**
	 * This method executes the GOTO command.
	 * Sets the {@link programmCounter} to the new position 'k'.
	 * The function goto is a basic java function, therefore _goto is used.
	 * @param k the position as String
	 * **/
	private void _goto(int k) 
	{
		this.memory.programmcounter = k-1;
		this.setNopCycle(true);
	}
	
	/**
	 * This method executes the IORLW command.
	 * The contents of the W register is ORed with the eight bit literal 'k'. 
	 * The result is placed in the W register.
	 * @param k the position as String
	 * **/
	private void iorlw(int k) 
	{
		int w_in = memory.get_WREGISTER();
		int result = w_in | k;
		
		this.checkZeroFlag(result);

		memory.set_WREGISTER(result);
	}
	
	/**
	 * This method executes the IORLW command.
	 * The literal 'l' is loaded to the W register.
	 * @param l the literal as String
	 * **/
	private void movlw(int k) 
	{
		memory.set_WREGISTER(k);
	}
	
	/**
	 * This method executes the RETFIE command.
	 * Return from Interrupt
	 * **/
	private void retfie() 
	{
		memory.set_GIE(1);
		this.memory.programmcounter = memory.popFromStack();
		this.setNopCycle(true);
	}
	
	/**
	 * This method executes the RETLW command.
	 * Returns with the Literal 'k' in the W Register
	 * @param k the literal as String
	 * **/
	private void retlw(int k) 
	{
		memory.set_WREGISTER(k);
		this.memory.programmcounter = memory.popFromStack();
		this.setNopCycle(true);
	}
	
	// Attention the function return is a basic java function
	/**
	 * This method executes the RETURN command.
	 * Returns from subroutine.
	 * **/
	private void _return() 
	{
		this.memory.programmcounter = memory.popFromStack();
		this.setNopCycle(true);
	}
	
	/**
	 * This method executes the SLEEP command.
	 * **/
	private void sleep() 
	{
		// TODO: Sleep implementieren.
		
		
		this.memory.set_PD(0);
		this.memory.set_TO(1);
		this.getWatchdog().reset();
		
		this.proc.setInSleep(true);
		this.gui.rdbtn_sleep.setSelected(true);
	}
	
	/**
	 * This method executes the SUBLW command.
	 * The W register is subtracted from the literal k.
	 * The result is placed in the W register.
	 * @param k The literal as String
	 * **/
	private void sublw(int k) 
	{ 
		int w_in = memory.get_WREGISTER();
		int result;
		if(w_in > k) 
		{
			result = 256 - (w_in - k);
			this.memory.set_CARRYFLAG(0);
			
		}else {
			result = k - w_in;
			this.memory.set_CARRYFLAG(1);
		}
		this.checkZeroFlag(result);
		this.checkDCFlag(w_in, k);
		memory.set_WREGISTER(result);
	}
	
	/**
	 * This method executes the XORLW command.
	 * The contents of the W register are XORed with the eight bit literal k. 
	 * The result is placed in the W register
	 * @param k The literal as String
	 * **/
	private void xorlw(int k) 
	{   // eventuell hier noch nullen auffüllen
		int w_in = memory.get_WREGISTER();
		int result = w_in ^ k;
		
		this.checkZeroFlag(result);

		memory.set_WREGISTER(result);
	}
	
	//
	// end of commands
	//


	/**
	 * Method to close the Window of the Mnemonic editor.
	 * **/

	public void closeMnemonicWindow() {

		this.mnemonicWindow.dispose();
	}
	
	/**
	 * Method to clear the code view Table.
	 * **/
	public void clearCodeTable() 
	{
		int rows = gui.getTblCodeModel().getRowCount();
    	for(int i = 0; i<rows; i++) 
    	{
        	gui.getTblCodeModel().removeRow(0);  		
    	}
	}
	
	/**
	 * Method to clear the {@link programCounterList}
	 * **/
	private void clearProgramCounterList() 
	{
		for(int i = 0; i< this.programCounterList.length; i++) 
		{
			this.programCounterList[i] = 0;
		}
	}
	
	/**
	 * Method to refresh the Analog IOs.
	 * The selected Port of the analog output is read and written to the digital output.
	 * The analog input is read and written to the selected Port.
	 * **/
	public void refreshIO() {
		int trisA = this.memory.get_MemoryDIRECT(0x85);
		int trisB = this.memory.get_MemoryDIRECT(0x86);
		// set the Tris register from Memory
		gui.setTrisA(trisA);
		gui.setTrisB(trisB);

		// read value from ports and save to memory
		int ra;
		int rb;
		
		if(this.server.getTcpClient() != null && this.server.getTcpClient().isConnected()) 
		{
			ra = this.server.getPortA();
			rb = this.server.getPortB();
		}else 
		{
			ra = gui.getPortA();
			rb = gui.getPortB();
		}

		for(int i = 0; i < 8; i++) {
			if((trisA & 1) == 1) {
				this.memory.set_SRAMDIRECT(0x05, i, ra & 1);
			}
			if((trisB & 1) == 1) {
				this.memory.set_SRAMDIRECT(0x06, i, rb & 1);
			}
			trisA = trisA >> 1;
    		trisB = trisB >> 1;
			ra 	  = ra >> 1;
			rb 	  = rb >> 1;
		}
		// update Port register from Memory
		gui.setPortA(this.memory.get_MemoryDIRECT(0x05));
		gui.setPortB(this.memory.get_MemoryDIRECT(0x06));
		
		this.server.setPortA(this.memory.get_MemoryDIRECT(0x05));
		this.server.setPortB(this.memory.get_MemoryDIRECT(0x06));
	}
	
	/**
	 * Method to update the selected quarz frequency.
	 * @param selectedItem The selected Item from the drop down menu.
	 * **/
	public void updateFrequency(String selectedItem) {
		switch(selectedItem) 
		{
			case "500kHz":
				this.frequency = 500;
				break;
			case "1MHz":
				this.frequency = 1000;
				break;
			case "2MHz":
				this.frequency = 2000;
				break;
			case "3MHz":
				this.frequency = 3000;
				break;
			case "4MHz":
				this.frequency = 4000;
				break;
		}
	}
	
	/**
	 * Method to save a SRC File.
	 * @param fileToSave the File to save.
	 * **/
	public void saveSRCFile(File fileToSave) {
		File savingFile;
		try {
		      savingFile = new File(fileToSave.getAbsolutePath());
		      if (savingFile.createNewFile()) {
		        System.out.println("File created: " + savingFile.getName());
		        try {
		            FileWriter myWriter = new FileWriter(savingFile.getAbsolutePath());
		           
		            
		            myWriter.write(this.mnemonicWindow.getTxtArea_mnemonic().getText());
		            myWriter.close();
		            
		            System.out.println("Successfully wrote to the file.");
		            
		          } catch (IOException e) {
		            System.out.println("An error occurred.");
		            e.printStackTrace();
		          }
		      } else {
		        System.out.println("File already exists.");
		      }
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	
	/**
	 * Method to load a SRC File into the mnemonic view editor.
	 * @param file The File to load.
	 * **/
	public void loadSRCFile(File file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		String st; 

    	
    	// initialisieren der grafischen Elemente
		this.mnemonicWindow.getTxtArea_mnemonic().setText("");
		
		String output = "";
		
    	// einlesen der einzelnen Zeilen inklusive equ und labels
		try {
			while ((st = br.readLine()) != null) { 
				
				output = output + st + '\n';
				
			}
			
			this.mnemonicWindow.getTxtArea_mnemonic().setText(output);
			
		} catch (NumberFormatException | IOException e) {

			e.printStackTrace();
		}
		
	}
	
	/**
	 * Method to save the current code to a selected or new lst file.
	 * @param fileToSave The File to save.
	 * **/
	public void saveLSTFile(File fileToSave) {
		File savingFile;
		try {
		      savingFile = new File(fileToSave.getAbsolutePath());
		      if (savingFile.createNewFile()) {
		        System.out.println("File created: " + savingFile.getName());
		        try {
		            FileWriter fileWriter = new FileWriter(savingFile.getAbsolutePath());
		            
		    		String code = "";
		    		for(int i = 0; i < 		gui.getTblCodeModel().getRowCount(); i++) 
		    		{
		    			// get the data of specific line
		    			String progCounter = gui.getTblCodeModel().getValueAt(i, 1).toString();
		    			while(progCounter.length() < 4) 
		    			{
		    				progCounter = "0"+progCounter;
		    			}
		    			String progCode = gui.getTblCodeModel().getValueAt(i, 2).toString();
		    			while(progCode.length() < 4) 
		    			{
		    				progCode = "0"+progCode;
		    			}
		    			String lineCount = gui.getTblCodeModel().getValueAt(i, 3).toString();
		    			while(lineCount.length() < 5) 
		    			{
		    				lineCount = "0"+lineCount;
		    			}
		    			String label = gui.getTblCodeModel().getValueAt(i, 4).toString();
		    			while(label.length() < 4) 
		    			{
		    				label = " "+label;
		    			}
		    			String mnemonic = gui.getTblCodeModel().getValueAt(i, 5).toString();
		    			
		    			// attention to the correct space count between each variable
		    			code = code + progCounter + " " + progCode + "           " + lineCount + "  " + label + "     " + mnemonic + "\n";
		    		}
		            
		            fileWriter.write(code);
		            fileWriter.close();
		            System.out.println("Successfully wrote to the file.");
		          } catch (IOException e) {
		            System.out.println("An error occurred.");
		            e.printStackTrace();
		          }
		      } else {
		        System.out.println("File already exists.");
		      }
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	private void checkZeroFlag(int z) 
	{
		if(z == 0) 
		{
			this.memory.set_ZEROFLAG(1);
		}else 
		{
			this.memory.set_ZEROFLAG(0);
		}

	}
	private void checkDCFlag(int in_1, int in_2) 
	{
		if (((in_1 & 0x0F) + (in_2 & 0x0F)) > 0x0F) {
			this.memory.set_DCFLAG(1);
		}
		else {
			this.memory.set_DCFLAG(0);
		}
	}
	protected int T0CKI() 
	{
		return this.memory.get_MemoryDIRECT(5, 4);
	}
	protected int getPrescaler() 
	{
		return (this.memory.get_Memory(0x81) & 0x07);
	}
	protected void clearHighlights() {
		this.gui.getTableMemory().clearSelection();
	}
	protected void highlightCell(int x, int y)
	{
		this.gui.highlightCell(x, y);
	}
	protected void wakeUpSleep() 
	{
		// TODO: Wake Up Implementieren
		this.proc.setInSleep(false);
		this.gui.rdbtn_sleep.setSelected(false);
	}
	/**
	 * Getter for access to Processor Object
	 * @return reference to proc
	 */
	protected Processor getProcessor() 
	{
		return proc;
	}
	/**
	 * Getter for access to Memory Object
	 * @return reference to memory
	 */
	protected Memory getMemory() 
	{
		return memory;
	}
	/**
	 * @return the eeprom
	 */
	protected EEProm getEeprom() {
		return eeprom;
	}

	/**
	 * Getter for access to Watchdog Object
	 * @return reference to watchdog
	 */
	protected Watchdog getWatchdog() 
	{
		return wtd;
	}
	/**
	 * Getter for access to Timer Object
	 * @return reference to tmr0
	 */
	protected Timer getTimer() 
	{
		return tmr0;
	}
	/**
	 * Getter for access to Interrupt Object
	 * @return reference to isr;
	 */
	protected Interrupt getInterrupt() 
	{
		return isr;
	}
	/**
	 * @return the processorRunning
	 */
	protected boolean isProcessorRunning() {
		return processorRunning;
	}

	protected void reset() {
		System.out.println("reset funktion");
		// TODO: Reset implementieren
		
		this.memory.programmcounter = 0;
		
		// PCL
		this.memory.set_SRAMDIRECT(0x02, 0);
		this.memory.set_SRAMDIRECT(0x82, 0);
		
		// Status Register
		// TODO: PD und TO Bit
		// RP0
		this.memory.set_SRAMDIRECT(0x03, 5, 0);
		this.memory.set_SRAMDIRECT(0x83, 5, 0);
		// RP1
		this.memory.set_SRAMDIRECT(0x03, 6, 0);
		this.memory.set_SRAMDIRECT(0x83, 6, 0);
		// IRP
		this.memory.set_SRAMDIRECT(0x03, 7, 0);
		this.memory.set_SRAMDIRECT(0x83, 7, 0);
		
		//PCLATH
		this.memory.set_SRAMDIRECT(0x0A, 0);
		this.memory.set_SRAMDIRECT(0x8A, 0);
		
		// INTCON
		// INTF
		this.memory.set_SRAMDIRECT(0x0B, 1, 0);
		this.memory.set_SRAMDIRECT(0x8B, 1, 0);
		// T0IF
		this.memory.set_SRAMDIRECT(0x0B, 2, 0);
		this.memory.set_SRAMDIRECT(0x8B, 2, 0);
		// RBIE
		this.memory.set_SRAMDIRECT(0x0B, 3, 0);
		this.memory.set_SRAMDIRECT(0x8B, 3, 0);
		// INTE
		this.memory.set_SRAMDIRECT(0x0B, 4, 0);
		this.memory.set_SRAMDIRECT(0x8B, 4, 0);
		// T0IE
		this.memory.set_SRAMDIRECT(0x0B, 5, 0);
		this.memory.set_SRAMDIRECT(0x8B, 5, 0);
		// EEIE
		this.memory.set_SRAMDIRECT(0x0B, 6, 0);
		this.memory.set_SRAMDIRECT(0x8B, 6, 0);
		// GIE
		this.memory.set_SRAMDIRECT(0x0B, 7, 0);
		this.memory.set_SRAMDIRECT(0x8B, 7, 0);
		
		// Option Register
		this.memory.set_SRAMDIRECT(0x81, 255);
		
		// TRISA
		this.memory.set_SRAMDIRECT(0x85, 255);
		// TRISB
		this.memory.set_SRAMDIRECT(0x86, 255);
		// EECON1
		// RD
		this.memory.set_SRAMDIRECT(0x88, 0, 0);
		// WR
		this.memory.set_SRAMDIRECT(0x88, 1, 0);
		// WREN
		this.memory.set_SRAMDIRECT(0x88, 2, 0);
		// TODO: WRERR
		// EEIF
		this.memory.set_SRAMDIRECT(0x88, 4, 0);
	}
	/**
	 * @param processorRunning the processorRunning to set
	 */
	protected void setProcessorRunning(boolean processorRunning) {
		this.processorRunning = processorRunning;
	}

	/**
	 * @return the tableHighlight
	 */
	public int[][] getTableHighlight() {
		return tableHighlight;
	}

	/**
	 * @param tableHighlight the tableHighlight to set
	 */
	public void setTableHighlight(int[][] tableHighlight) {
		this.tableHighlight = tableHighlight;
	}
	/**
	 * @return the jumpers
	 */
	protected String[] getJumpers() {
		return jumpers;
	}

	/**
	 * @param jumpers the jumpers to set
	 */
	protected void setJumpers(String[] jumpers) {
		this.jumpers = jumpers;
	}
	/**
	 * @return the programCounterList
	 */
	protected int[] getProgramCounterList() {
		return programCounterList;
	}

	/**
	 * @param programCounterList the programCounterList to set
	 */
	protected void setProgramCounterList(int[] programCounterList) {
		this.programCounterList = programCounterList;
	}
	/**
	 * @return the breakPointList
	 */
	protected boolean[] getBreakPointList() {
		return breakPointList;
	}

	/**
	 * @param breakPointList the breakPointList to set
	 */
	protected void setBreakPointList(boolean[] breakPointList) {
		this.breakPointList = breakPointList;
	}
	/**
	 * @return the frequency
	 */
	protected int getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	protected void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the isNopCycle
	 */
	public boolean isNopCycle() {
		return isNopCycle;
	}

	/**
	 * @param isNopCycle the isNopCycle to set
	 */
	public void setNopCycle(boolean isNopCycle) {
		this.isNopCycle = isNopCycle;
	}
	
	/**
	 * @return the operationalTime
	 */
	protected double getOperationalTime() {
		return operationalTime;
	}

	/**
	 * @param operationalTime the operationalTime to set
	 */
	protected void setOperationalTime(double operationalTime) {
		this.operationalTime = operationalTime;
	}
	/**
	 * @return the sevenSegmentActive
	 */
	protected boolean isSevenSegmentActive() {
		return sevenSegmentActive;
	}

	/**
	 * @param sevenSegmentActive the sevenSegmentActive to set
	 */
	protected void setSevenSegmentActive(boolean sevenSegmentActive) {
		this.sevenSegmentActive = sevenSegmentActive;
	}
	
	/**
	 * @return the controlPortSelect
	 */
	protected int getControlPortSelect() {
		return controlPortSelect;
	}

	/**
	 * @param controlPortSelect the controlPortSelect to set
	 */
	protected void setControlPortSelect(int controlPortSelect) {
		this.controlPortSelect = controlPortSelect;
	}
	/**
	 * @return the dataPortSelect
	 */
	protected int getDataPortSelect() {
		return dataPortSelect;
	}

	/**
	 * @param dataPortSelect the dataPortSelect to set
	 */
	protected void setDataPortSelect(int dataPortSelect) {
		this.dataPortSelect = dataPortSelect;
	}

	/**
	 * @return the server
	 */
	protected IOServer getServer() {
		return server;
	}

	/**
	 * @param server the server to set
	 */
	protected void setServer(IOServer server) {
		this.server = server;
	}

	/**
	 * @return the writeActive
	 */
	public boolean getWriteActive() {
		return writeActive;
	}

	/**
	 * @param writeActive the writeActive to set
	 */
	public void setWriteActive(boolean writeActive) {
		this.writeActive = writeActive;
	}
	
	
}
