import java.io.*;

import javax.swing.table.TableColumn;

/// class Controller
/**
*  This class is the heart of this Simulator.
*  It is the connection between all classes. Each interaction by a user on the GUI is executed in the controller class.
*  Here are objects of processor, memory, timer, interrupt and watchdog.
* **/
public class Controller {
	
	/// The object of main gui class, displaying the main simulator window.
	private Simulator_Window gui;
	/// The object of the mnemonic editor gui class, used to display the code in mnemonic form, edit, load and save it.
	private MnemonicView mnemonicWindow;
	/// The object of the error dialog class, used to display error windows.
	private ErrorDialog errorView;
	/// The object of the IO server class, used to connect an IO controller.
	private IOServer server;
	/// The object of the processor class, used to work each code step.
	private Processor proc;
	/// The object of the memory class, used to store the data of the microprocessor.
	private Memory memory;
	/// The object of the mnemonic parser class, used to parse mnemonic code to binary code.
	private MnemonicParser parser;
	/// The object of the command class, holding all commands executable by the microprocessor and deciding which one is called.
	private Commands commands;
	/// The object of the timer, used to check timer pin and do timer operations.
	private Timer tmr0;
	/// The object of the interrupt class, used to check interrupt flags and set the program counter to 0004 if needed.
	private Interrupt isr;
	/// Object of the watchdog class, controlling the watchdog and executing sleep or reset if needed.
	private Watchdog wtd;
	/// Object of the EEPROM class, used to save data into and load from a txt file.
	private EEProm eeprom;
	
	/// Displaying if the processor is running.
	private boolean processorRunning = false;
	/// Displaying if the code is compiled.
	private boolean isCompiled = false;
	/// The data model to initialize the data table.
	private String[][] tableData = new String[32][9];
	/// An array holding the jumper line number and the mnemonic code. The jumpers are holding the mnemonic code line with an ':' and the program counter appended. Starting at 0.
	private String[] jumpers = new String[512];
	/// The amount of jumper marks in the code.
	private int jumpersCount = 0;
	/// An array holding the EQUs. One entry holds the "original" before the EQU, followed by an ':' appended with the value after the EQU.
	private String[] equ = new String[256];
	/// Amount of EQUs in the code.
	private int equCount = 0;
	/// The mnemonic code.
	private String[] mnemonicLines;
	/// A list of the program counter as key with the dedicated code line as value
	private int[] programCounterList = new int[1024];
	/// A list of the breakpoints.
	private boolean[] breakPointList = new boolean[1024];
	/// The line length of the compiled code.
	private int codeLength = 0;
	/// The Quartz frequency saved as kHz.
	private int frequency = 500;
	/// Displaying if the next cycle is a nop cycle, provoked by a multy cycle instruction.
	private boolean isNopCycle = false;
	/// Signals if the EEPROM write is active.
	private boolean writeActive = false;
	/// The time since the simulator program is started.
	private double operationalTime = 0.0;
	/// Displaying if the 7 segment display is active or not.
	private boolean sevenSegmentActive = false;
	/// Indicating the control port for the 7 Segment display. 1 indicates port b. 0 indicates port a
	private int controlPortSelect = 0;
	/// Indicating the data port for the 7 Segment display. 0 indicates port a. 1 indicates port b
	private int dataPortSelect = 1;
	/// master clear (reset) variable
	private boolean mclr = false;
	/// sleep variable to activate sleep mode
	private boolean sleep = false;
	/// bool to indicate wheter watchdog is enabled or not
	private boolean wdte;
	
	/**
	*  The Constructor, creating a new instances of all other classes except for the {@link Simulator_Window}.
	*  @param pGui Is an Object of {@link Simulator_Window}.
	* **/
	public Controller(Simulator_Window pGui) 
	{
		this.setGui(pGui);
		memory = new Memory(this);
		parser = new MnemonicParser(this);
		tmr0 = new Timer(this);
		isr = new Interrupt(this);
		server = new IOServer(this);
		eeprom = new EEProm(this);
		wtd = new Watchdog(this);
		setCommands(new Commands(this));
	}
	
	/**
	*  Method to initialize the memory and the labels of all tables.
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
			this.getGui().getTblMemoryModel().addRow(new Object[] {tableData[i][0],"0","0","0","0","0","0","0","0"});
		}
		// initialization of special register table
		this.getGui().getTblSpecialModel().addRow(new Object[] { "W-Reg", "00","00000000" });
		this.getGui().getTblSpecialModel().addRow(new Object[] { "FSR", "00","00000000" });
		this.getGui().getTblSpecialModel().addRow(new Object[] { "PCL", "30","00110000" });
		this.getGui().getTblSpecialModel().addRow(new Object[] { "PCLATH", "00","00000000" });
		this.getGui().getTblSpecialModel().addRow(new Object[] {"PC", "0030","00000000"});
		this.getGui().getTblSpecialModel().addRow(new Object[] { "Status", "C0","10100000" });
		
		for(int i = 0; i < 8; i++) 
		{
			this.getGui().getTblStackModel().addRow(new Object[] {i,""});
		}
	}
	
	/**
	 * Starts a thread from Memory to cyclic update the memory table.
	 */
	public void startMemoryUpdateThread() {
		memory.start();
	}
	
	/**
	*  Method to input a value into a specific cell of the memory table.
	*  @param value Is a String which is put in the cell.
	*  @param x Is an integer referencing to the column.
	*  @param y Is an integer referencing to the row + 1, because the first row are the labels.
	* **/
	protected void updateMemoryTable(String value, int x, int y) 
	{
		while (value.length() < 2) {
			value = "0" + value;
		}
		getGui().SetData(value, x, y + 1);
	}
	
	/**
	 * Method to input a value into a specific cell of the special register table.
	 * @param value Is a String which is put in the cell.
	 * @param x Is an integer referencing to the column.
	 * @param y Is an integer referencing to the row.
	 */
	protected void updateSpecialRegTable(String value, int x, int y) 
	{
		// When editing the binary values display 8 digits.
		if(y == 2) {
			while (value.length() < 8) {
				value = 0 + value;
			}
		}
		getGui().setSpecialData(value, x, y);
	}
	
	/**
	 * Method to update the stack panel with the data in the Memory variable stack.
	 * @param value The string value to fill in.
	 * @param y The stack position as an int.
	 */
	protected void updateStackPanel(String value, int y) 
	{
		getGui().setStackData(value, y, 1);
	}
	
	/**
	 * Method which is called when a break point is set in the table.
	 * @param row The row to set the breakpoint in.
	 */
	protected void setBreakPoint(int row) 
	{
		boolean found = false;
		for(int i = 0; i < programCounterList.length; i++) 
		{
			if(programCounterList[i]-1 == row) 
			{
				found = true;
				if(breakPointList[i] == false) 
				{
					breakPointList[i] = true;
					this.getGui().getTblCodeModel().setValueAt("O", row, 0);
				}else 
				{
					breakPointList[i] = false;
					this.getGui().getTblCodeModel().setValueAt(" ", row, 0);
				}
			}
		}
		if(!found) 
		{
			this.showError("Breakpoint Error", "The selected line is not a executable line");
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
	*  @param title Is a String and the title of the dialog.
	*  @param text Is a String and the text of the dialog.
	* **/
	public void showError(String title,String text) 
	{
		errorView = new ErrorDialog();
		errorView.setVisible(true);
		errorView.lbl_ErrorTitle.setText(title);
		errorView.lbl_ErrorText.setText(text);
	}
	
	/**
	 * This method will update the seven segment panel.
	 * There is a control input and a data input needed.
	 * These inputs can be chosen (Port a and Port b).
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
			this.getGui().getSevenSegmentPanel().set7Segment(controlPort, dataPort);
		}
	}
	// used to update the operationTime panel in gui
	protected void updateOperationalTime() 
	{
		getGui().updateOperationalTime(this.operationalTime);
	}
	
	/**
	 * Method to add the cycle time to the operationalTime and round to 6 decimal digits.
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
		for (int i = 0; i < getGui().getTblCodeModel().getRowCount(); i++) {
			// check for Labels and add if found
			String label = getGui().getTblCodeModel().getValueAt(i, 4).toString();
			if(!label.isEmpty()) {
				mnemonic = mnemonic + label + "\n";
			}
			else {
				// else check if line is an EQU and add it, otherwise add it with an space as prefix
				String code = getGui().getTblCodeModel().getValueAt(i, 5).toString();
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
	*  Method to continue to the next line of code while debugging. If the processor is not running, an error window is displayed.
	*  @see showError
	* **/
	public void continueDebugStep() {
		if (processorRunning) {
			proc.continueDebugStep();
		}else {
			this.showError("Next Step Degubber", "Debugger is not running. Please start debugging before continuing to the next line.");
		}
	}

	/**
	 * Method to check if there are any jump marks in the not compiled code. 
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
	 * @param pCode Is a String array which holds the program code.
	 * @return The given program code.
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
	 * @return The EQU equivalent as a String.
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
		this.getGui().setSegment(c1, c2, c3, c4);
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
				getGui().getTblCodeModel().addRow(new Object[] {" ",st.substring(0, 4),st.substring(5, 9),st.substring(20, 25),label,""});
			}else 
			{
				getGui().getTblCodeModel().addRow(new Object[] {" ",st.substring(0, 4),st.substring(5, 9),st.substring(20, 25),label, st.substring(36)});
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
	 * @param text Is the File to be loaded.
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
		this.getGui().highlightRow(newC - 1);
	}
	
	/**
	 * Method to get get the amount of jumper marks.
	 * @return The number of jump marks as an integer.
	 * **/
	protected int getJumpersCount() 
	{
		return this.jumpersCount;
	}
	
	/**
	 * Method to compile the code.
	 * @deprecated Needs to be reworked.
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
					getGui().getTblCodeModel().addRow(new Object[]{"","    ", "    ", j+1 , "",""});
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
					getGui().getTblCodeModel().addRow(new Object[]{"","    ", "    ", j+1 , "",mnemonicLines[j]});
				}else if(proceed.contains("EQU")) 
				{
					// EQU should not affect any variable
					getGui().getTblCodeModel().addRow(new Object[]{"","    ", "    ", j+1 , "",mnemonicLines[j]});
				}else if(proceed.charAt(0) != ' ') 
				{
					// if the first char in a line is unequal to space it is a label
					// pay attention that the EQU is checked before, because EQU has unequal first character too
					
					
					getGui().getTblCodeModel().addRow(new Object[]{"","    ", "    ", j+1 , proceed,""});
				}else if(this.mnemonicLines[j].charAt(0) == ' ') 
				{
					
					// normal code line, executed by parser 
					System.out.println("Controller: "+proceed);
					String binaryCode = parser.fromMnemToHex(proceed, j).toString();
					
					getGui().getTblCodeModel().addRow(new Object[]{"",Integer.toHexString(pc), Integer.toHexString(Integer.parseInt(binaryCode, 2)), j+1 , "",mnemonicLines[j]});
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
	 * Method to set the column width of the code view table.
	 * **/
	public void setColumnWidth() 
	{
		TableColumn column = null;
		for (int i = 0; i < 3; i++) {
		    column = getGui().getTableCode().getColumnModel().getColumn(i);
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
		getGui().getTblCodeModel().setColumnIdentifiers	(new Object[] {" ", "ProgramCounter", "ProgramCode", "LineCount","Label","MnemonicCode"});
		getGui().getTblMemoryModel().setColumnIdentifiers	(new Object[] {"","00","01","02","03","04","05","06","07"});
		getGui().getTblSpecialModel().setColumnIdentifiers(new Object[]{"Register", "Hex-Wert", "Bin-Wert"});
	}

	/**
	 * Method to close the Window of the Mnemonic editor. Disposes the MnemonicView object.
	 * **/
	public void closeMnemonicWindow() {
		this.mnemonicWindow.dispose();
	}
	
	/**
	 * Method to clear the code view Table.
	 * **/
	public void clearCodeTable() 
	{
		int rows = getGui().getTblCodeModel().getRowCount();
    	for(int i = 0; i<rows; i++) 
    	{
        	getGui().getTblCodeModel().removeRow(0);  		
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
	 * All pins selected as output are overwritten from the data latch memory.
	 * All pins selected as inputs are writing to the Memory.
	 * **/
	public void refreshIO() {
		int trisA = this.memory.get_MemoryDIRECT(0x85);
		int trisB = this.memory.get_MemoryDIRECT(0x86);
		// set the Tris register from Memory
		getGui().setTrisA(trisA);
		getGui().setTrisB(trisB);
		
		int dataLatchA = this.memory.getDataLatchA();
		int dataLatchB = this.memory.getDataLatchB();

		// read value from ports and save to memory
		int ra;
		int rb;
		
		if(this.server.getTcpClient() != null && this.server.getTcpClient().isConnected()) 
		{
			ra = this.server.getPortA();
			rb = this.server.getPortB();
		}else 
		{
			ra = getGui().getPortA();
			rb = getGui().getPortB();
		}

		for(int i = 0; i < 8; i++) {
			// tris = 1 -> eingang 
			if((trisA & 1) == 1) {
				this.memory.set_SRAMDIRECT(0x05, i, ra & 1);
			}else 
			{
				this.memory.set_SRAMDIRECT(0x05, i, dataLatchA & 1);
			}
			
			if((trisB & 1) == 1) {
				this.memory.set_SRAMDIRECT(0x06, i, rb & 1);
			}else 
			{
				this.memory.set_SRAMDIRECT(0x06, i, dataLatchB & 1);
			}
			
			dataLatchA = dataLatchA >> 1;
			dataLatchB = dataLatchB >> 1;
			
			trisA = trisA >> 1;
    		trisB = trisB >> 1;
			ra 	  = ra >> 1;
			rb 	  = rb >> 1;
		}
		// update Port register from Memory
		getGui().setPortA(this.memory.get_MemoryDIRECT(0x05));
		getGui().setPortB(this.memory.get_MemoryDIRECT(0x06));
		
		this.server.setPortA(this.memory.get_MemoryDIRECT(0x05));
		this.server.setPortB(this.memory.get_MemoryDIRECT(0x06));
	}
	
	/**
	 * Method to update the selected quartz frequency.
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
	 * Method to save the current code to a selected or new LST file.
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
		    		for(int i = 0; i < 		getGui().getTblCodeModel().getRowCount(); i++) 
		    		{
		    			// get the data of specific line
		    			String progCounter = getGui().getTblCodeModel().getValueAt(i, 1).toString();
		    			while(progCounter.length() < 4) 
		    			{
		    				progCounter = "0"+progCounter;
		    			}
		    			String progCode = getGui().getTblCodeModel().getValueAt(i, 2).toString();
		    			while(progCode.length() < 4) 
		    			{
		    				progCode = "0"+progCode;
		    			}
		    			String lineCount = getGui().getTblCodeModel().getValueAt(i, 3).toString();
		    			while(lineCount.length() < 5) 
		    			{
		    				lineCount = "0"+lineCount;
		    			}
		    			String label = getGui().getTblCodeModel().getValueAt(i, 4).toString();
		    			while(label.length() < 4) 
		    			{
		    				label = " "+label;
		    			}
		    			String mnemonic = getGui().getTblCodeModel().getValueAt(i, 5).toString();
		    			
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
	
	/**
	 * Method which sets the zero flag, depending on the input. If the input is 0 the zero flag is set to 1, otherwise to 0.
	 * @param z The input to check.
	 */
	protected void checkZeroFlag(int z) 
	{
		if(z == 0) 
		{
			this.memory.set_ZEROFLAG(1);
		}else 
		{
			this.memory.set_ZEROFLAG(0);
		}

	}
	
	/**
	 * Method which sets the DC flag depending on the input. If the lower 4 bits of both input numbers are larger than 15 the DC flag is set to 1, otherwise to 0.
	 * @param in_1 The first input number as int.
	 * @param in_2 The second input number as int.
	 */
	protected void checkDCFlag(int in_1, int in_2) 
	{
		if (((in_1 & 0x0F) + (in_2 & 0x0F)) > 0x0F) {
			this.memory.set_DCFLAG(1);
		}
		else {
			this.memory.set_DCFLAG(0);
		}
	}
	
	/**
	 * @deprecated
	 */
	protected int T0CKI() 
	{
		return this.memory.get_MemoryDIRECT(5, 4);
	}
	
	/**
	 * Method to get the prescaler value from the Memory.
	 * @return The prescaler value
	 */
	protected int getPrescaler() 
	{
		return (this.memory.get_Memory(0x81) & 0x07);
	}
	
	/**
	 * Method to remove all highlights in the memory view table.
	 */
	protected void clearHighlights() {
		this.getGui().getTableMemory().clearSelection();
	}
	
	/**
	 * Method to highlight a specific cell in the memory view table.
	 * @param x The row of the cell.
	 * @param y The column of the cell.
	 */
	protected void highlightCell(int x, int y)
	{
		this.getGui().highlightCell(x, y);
	}
	
	/**
	 * Wakes the processor up from sleeping state.
	 */
	protected void wakeUpSleep() 
	{
		this.proc.setInSleep(false);
		this.sleep = false;
		this.getGui().getTglbtnSleeping().setSelected(false);
	}
	
	/**
	 * Method to reset the processor Memory.
	 */
	protected void reset() {
		System.out.println("reset funktion");
		// TODO: Reset implementieren
		
		this.memory.programmcounter = 0;
		
		// PCL
		this.memory.set_SRAMDIRECT(0x02, 0);
		this.memory.set_SRAMDIRECT(0x82, 0);
		
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
		// EEIF
		this.memory.set_SRAMDIRECT(0x88, 4, 0);
	}
	
	/**
	 * Getter to access the Processor Object.
	 * @return Reference to proc.
	 */
	protected Processor getProcessor() 
	{
		return proc;
	}
	/**
	 * Getter to access the Memory Object.
	 * @return Reference to memory.
	 */
	protected Memory getMemory() 
	{
		return memory;
	}
	
	/**
	 * Getter to access the EEPROM Object
	 * @return Reference to eeprom.
	 */
	protected EEProm getEeprom() {
		return eeprom;
	}

	/**
	 * Getter for access to Watchdog Object
	 * @return Reference to watchdog.
	 */
	protected Watchdog getWatchdog() 
	{
		return wtd;
	}
	
	/**
	 * Getter for access to the Timer Object.
	 * @return Reference to tmr0
	 */
	protected Timer getTimer() 
	{
		return tmr0;
	}
	
	/**
	 * Getter for access to the Simulator_Window Object.
	 * @return Reference to gui.
	 */
	public Simulator_Window getGui() {
		return gui;
	}

	/**
	 * Setter to set the Simulator_Window.
	 * @param gui The gui to set.
	 */
	public void setGui(Simulator_Window gui) {
		this.gui = gui;
	}

	/**
	 * Getter to access the Interrupt Object.
	 * @return Reference to isr.
	 */
	protected Interrupt getInterrupt() 
	{
		return isr;
	}
	
	/**
	 * Getter to access the processorRunning Variable.
	 * @return The processorRunning variable.
	 */
	protected boolean isProcessorRunning() {
		return processorRunning;
	}
	
	/**
	 * Setter to set the processorRunning Object.
	 * @param processorRunning the processorRunning to set
	 */
	protected void setProcessorRunning(boolean processorRunning) {
		this.processorRunning = processorRunning;
	}
	
	/**
	 * Getter to access the jumpers variable.
	 * @return The jumpers.
	 */
	protected String[] getJumpers() {
		return jumpers;
	}

	/**
	 * Setter to set the jumpers variable.
	 * @param jumpers The jumpers to set.
	 */
	protected void setJumpers(String[] jumpers) {
		this.jumpers = jumpers;
	}
	
	/**
	 * Getter to access the programCounterList Variable.
	 * @return The programCounterList.
	 */
	protected int[] getProgramCounterList() {
		return programCounterList;
	}

	/**
	 * Setter to set the programCounterList variable.
	 * @param programCounterList The programCounterList to set.
	 */
	protected void setProgramCounterList(int[] programCounterList) {
		this.programCounterList = programCounterList;
	}
	/**
	 * Getter to get the breakPointList variable.
	 * @return The breakPointList.
	 */
	protected boolean[] getBreakPointList() {
		return breakPointList;
	}

	/**
	 * Setter to set the breakPointList variable.
	 * @param breakPointList The breakPointList to set.
	 */
	protected void setBreakPointList(boolean[] breakPointList) {
		this.breakPointList = breakPointList;
	}
	/**
	 * Getter to get the frequency.
	 * @return The frequency.
	 */
	protected int getFrequency() {
		return frequency;
	}

	/**
	 * Setter to set the frequency.
	 * @param frequency The frequency to set.
	 */
	protected void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * Getter to access the isNopCycle variable.
	 * @return The isNopCycle.
	 */
	public boolean isNopCycle() {
		return isNopCycle;
	}

	/**
	 * Setter to set the isNopCycle variable.
	 * @param isNopCycle The isNopCycle to set.
	 */
	public void setNopCycle(boolean isNopCycle) {
		this.isNopCycle = isNopCycle;
	}
	
	/**
	 * Getter to get the operationalTime variable.
	 * @return The operationalTime.
	 */
	protected double getOperationalTime() {
		return operationalTime;
	}

	/**
	 * Setter to set the operationalTime variable.
	 * @param operationalTime The operationalTime to set.
	 */
	protected void setOperationalTime(double operationalTime) {
		this.operationalTime = operationalTime;
	}
	/**
	 * Getter to access the sevenSegmentActive variable.
	 * @return the sevenSegmentActive.
	 */
	protected boolean isSevenSegmentActive() {
		return sevenSegmentActive;
	}

	/**
	 * Setter to set the sevenSegmentActive variable.
	 * @param sevenSegmentActive The sevenSegmentActive to set.
	 */
	protected void setSevenSegmentActive(boolean sevenSegmentActive) {
		this.sevenSegmentActive = sevenSegmentActive;
	}
	
	/**
	 * Getter to get the controlPortSelect variable.
	 * @return The controlPortSelect.
	 */
	protected int getControlPortSelect() {
		return controlPortSelect;
	}

	/**
	 * Setter so set the controlPortSelect variable.
	 * @param controlPortSelect The controlPortSelect to set.
	 */
	protected void setControlPortSelect(int controlPortSelect) {
		this.controlPortSelect = controlPortSelect;
	}
	/**
	 * Getter to get the dataPortSelect variable.
	 * @return The dataPortSelect.
	 */
	protected int getDataPortSelect() {
		return dataPortSelect;
	}

	/**
	 * Setter to set the dataPortSelect variable.
	 * @param dataPortSelect The dataPortSelect to set.
	 */
	protected void setDataPortSelect(int dataPortSelect) {
		this.dataPortSelect = dataPortSelect;
	}

	/**
	 * Getter to get the IOServer object.
	 * @return The server.
	 */
	protected IOServer getServer() {
		return server;
	}

	/**
	 * Setter to set the server object.
	 * @param server The server to set.
	 */
	protected void setServer(IOServer server) {
		this.server = server;
	}

	/**
	 * The Getter to get the writeActive variable.
	 * @return The writeActive.
	 */
	public boolean getWriteActive() {
		return writeActive;
	}

	/**
	 * Setter to set the writeActive variable.
	 * @param writeActive The writeActive to set.
	 */
	public void setWriteActive(boolean writeActive) {
		this.writeActive = writeActive;
	}

	/**
	 * Getter to get the Commands object.
	 * @return The commands.
	 */
	public Commands getCommands() {
		return commands;
	}

	/**
	 * Setter to set the commands object.
	 * @param commands the commands to set
	 */
	public void setCommands(Commands commands) {
		this.commands = commands;
	}
	/**
	 * Setter to set the MCLR variable
	 * @param b
	 */
	public void setMCLR(boolean b) {
		this.mclr = b;
	}
	/**
	 * Setter to set the sleep variable
	 * @param b
	 */
	public void setSleeping(boolean b) {
		this.sleep = b;
	}
	/**
	 * Setter to set the wdte variable
	 * @param b
	 */
	public void setWDTE(boolean b) {
		this.wdte = b;
	}

	public boolean getWDTE() {
		return this.wdte;
	}
	
	protected void incPC() 
	{
		this.getMemory().set_PROGRAMMCOUNTER(this.getMemory().get_PROGRAMMCOUNTER()+1);
	}
}
