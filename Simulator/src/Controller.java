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
	/// Processor object used to work each code step
	protected Processor proc;
	/// memory object used to store the data of microprocessor
	protected Memory memory;
	/// Parser Object to parse Mnemonic Code into Binary Code
	protected MnemonicParser parser;
	
	/// Displaying if the code is compiled.
	protected boolean isCompiled = false;
	/// The data model to initialize the data table.
	protected String[][] tableData = new String[32][9];
	/// An array holding the jumper line number and the mnemonic code.
	/**
	 * The jumpers are holding the mnemonic code line with an ':' and the program counter appended.
	 * They are listed starting at 0.
	 */
	protected String[] jumpers = new String[512];
	/// Amount of jumper marks in the code.
	private int jumpersCount = 0;
	/// An array holding the EQUs.
	/**
	 * One entry holds the "original" before the EQU, followed by an ':' appended with the value after the EQU.
	 */
	protected String[] equ = new String[256];
	/// Amount of EQUs in the code.
	private int equCount = 0;
	/// The mnemonic code.
	protected String[] mnemonicLines;
	/// A list of the program counter as key with the dedicated code line as value
	protected int[] programCounterList = new int[1024];
	/// The current position of the program in the code.
	protected int programmCounter;
	/// The program code as string array. Every code line is one string.
	protected String[] code;
	/// The length of the compiled code
	protected int codeLength = 0;
	/// The Quartz frequency 
	protected int frequency = 1000;
	
	/**
	*  The Constructor, creating a new Memory and MnemonicParser.
	*  @param pGui Is an Object of {@link Simulator_Window}
	* **/
	public Controller(Simulator_Window pGui) 
	{
		this.gui = pGui;
		memory = new Memory(this);
		parser = new MnemonicParser(this);

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
			System.out.println(i/8+" R:"+i%8);
			data[i/8][i%8] = 0;
			tableData[i/8][i%8+1] = Integer.toString( data[i/8][i%8]);
		}
		for(int i = 0; i < 32; i++) {
			tableData[i][0] = Integer.toHexString(i*8);
			this.gui.tbl_memory.addRow(new Object[] {tableData[i][0],"0","0","0","0","0","0","0","0"});
		}
		// initialization of special register table
		this.gui.tbl_special.addRow(new Object[] { "W-Reg", "00","00000000" });
		this.gui.tbl_special.addRow(new Object[] { "FSR", "00","00000000" });
		this.gui.tbl_special.addRow(new Object[] { "PCL", "30","00110000" });
		this.gui.tbl_special.addRow(new Object[] { "PCLATH", "00","00000000" });
		this.gui.tbl_special.addRow(new Object[] {"PC", "0030","00000000"});
		this.gui.tbl_special.addRow(new Object[] { "Status", "C0","10100000" });
		
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
	protected void updateMemoryTable(String value,int x, int y) 
	{
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
		gui.setSpecialData(value, x, y);
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
	*  Method to load the program code from the code view table.
	*  If labels are found in the 5th column they are added.
	*  Otherwise the blank mnemonic code from the 6th column is checked for EQUs. If it contains EQUs the line is added, otherwise the line is added with a space as prefix.
	*  Then the mnemonic code string is loaded into the mnemonic editor.
	* **/
	protected void loadMnemonicFromTable() 
	{
		String mnemonic = "";
		// iterate over the code view table
		for (int i = 0; i < gui.tbl_code.getRowCount(); i++) {
			// check for Labels and add if found
			String label = gui.tbl_code.getValueAt(i, 4).toString();
			if(!label.isEmpty()) {
				mnemonic = mnemonic + label + "\n";
			}
			else {
				// else check if line is an EQU and add it, otherwise add it with an space as prefix
				String code = gui.tbl_code.getValueAt(i, 5).toString();
				if (code.contains("EQU")) {
					mnemonic = mnemonic + code + "\n";
				}
				else {
					mnemonic = mnemonic + "  " + code + "\n";
				}
			}
		}
		// add and repaint.
		mnemonicWindow.txtArea_mnemonic.setText(mnemonic);
		mnemonicWindow.txtArea_mnemonic.repaint();
	}

	/**
	*  Method to start the simulation.
	*  If the code is compiled ({@link isCompiles}) a new {@link Processor} will be created and started.
	*  Otherwise and an error window will be displayed.
	*  @see showError
	* **/
	public void startSimu() {
		System.out.println("Simulation started...");
		if (this.isCompiled) {
			proc = new Processor(this);
			proc.start();	
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

		proc.stopThread();
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
				
				System.out.println("JumperMark found: "+pCode[i]+" at Line "+i+" to pc: "+this.programmCounter);
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
	 * Method to get the values of the analog IO output pins. For every active pin a 1 will be appended to a string, for every inactive a 0. 
	 * @return the 8 pin values as a String
	 * **/
	protected String getIOAnalog_OUT() 
	{
		String value = "";
		if(gui.rb_io_out_1.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		if(gui.rb_io_out_2.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		if(gui.rb_io_out_3.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		if(gui.rb_io_out_4.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		if(gui.rb_io_out_5.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		if(gui.rb_io_out_6.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		if(gui.rb_io_out_7.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		if(gui.rb_io_out_8.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		return value;
	}
	
	/**
	 * Method to get the values of the analog IO input pins. For every active pin a 1 will be appended to a string, for every inactive a 0. 
	 * @return the 8 pin values as a String
	 * **/
	protected String getIOAnalog_IN() 
	{
		String value = "";
		if(gui.rb_io_in_1.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		if(gui.rb_io_in_2.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		if(gui.rb_io_in_3.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		if(gui.rb_io_in_4.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		if(gui.rb_io_in_5.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		if(gui.rb_io_in_6.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		if(gui.rb_io_in_7.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		if(gui.rb_io_in_8.isSelected()) {value = "1" + value;}else {value = "0" + value;}
		return value;
	}
	
	/**
	 * Method to set the values of the analog IO output pins.
	 * @param number is the integer (0-255) to set
	 * **/
	protected void setIOAnalog_OUT(int number) 
	{
		if (number >= 128) {gui.rb_io_out_1.setSelected(true); number = number - 128;}else {gui.rb_io_out_1.setSelected(false);}
		if (number >= 64)  {gui.rb_io_out_2.setSelected(true); number = number - 64;} else {gui.rb_io_out_2.setSelected(false);}
		if (number >= 32)  {gui.rb_io_out_3.setSelected(true); number = number - 32;} else {gui.rb_io_out_3.setSelected(false);}
		if (number >= 16)  {gui.rb_io_out_4.setSelected(true); number = number - 16;} else {gui.rb_io_out_4.setSelected(false);}
		if (number >= 8)   {gui.rb_io_out_5.setSelected(true); number = number - 8;}  else {gui.rb_io_out_5.setSelected(false);}
		if (number >= 4)   {gui.rb_io_out_6.setSelected(true); number = number - 4;}  else {gui.rb_io_out_6.setSelected(false);}
		if (number >= 2)   {gui.rb_io_out_7.setSelected(true); number = number - 2;}  else {gui.rb_io_out_7.setSelected(false);}
		if (number >= 1)   {gui.rb_io_out_8.setSelected(true); number = number - 1;}  else {gui.rb_io_out_8.setSelected(false);}
	}
	
	/**
	 * Method to set the values of the analog IO input pins.
	 * @param number is the integer (0-255) to set
	 * **/
	protected void setIOAnalog_IN(int number) 
	{
		if (number >= 128) {gui.rb_io_in_1.setSelected(true); number = number - 128;}else {gui.rb_io_in_1.setSelected(false);}
		if (number >= 64)  {gui.rb_io_in_2.setSelected(true); number = number - 64;} else {gui.rb_io_in_2.setSelected(false);}
		if (number >= 32)  {gui.rb_io_in_3.setSelected(true); number = number - 32;} else {gui.rb_io_in_3.setSelected(false);}
		if (number >= 16)  {gui.rb_io_in_4.setSelected(true); number = number - 16;} else {gui.rb_io_in_4.setSelected(false);}
		if (number >= 8)   {gui.rb_io_in_5.setSelected(true); number = number - 8;}  else {gui.rb_io_in_5.setSelected(false);}
		if (number >= 4)   {gui.rb_io_in_6.setSelected(true); number = number - 4;}  else {gui.rb_io_in_6.setSelected(false);}
		if (number >= 2)   {gui.rb_io_in_7.setSelected(true); number = number - 2;}  else {gui.rb_io_in_7.setSelected(false);}
		if (number >= 1)   {gui.rb_io_in_8.setSelected(true); number = number - 1;}  else {gui.rb_io_in_8.setSelected(false);}
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
			// TODO Auto-generated catch block
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
				gui.tbl_code.addRow(new Object[] {" ",st.substring(0, 4),st.substring(5, 9),st.substring(20, 25),label,""});
			}else 
			{
				gui.tbl_code.addRow(new Object[] {" ",st.substring(0, 4),st.substring(5, 9),st.substring(20, 25),label, st.substring(36)});
			}

			if(!st.substring(0, 4).equals("    ")) 
			{
				this.codeLength = Integer.parseInt(st.substring(0, 4),16);
				memory.programMemory[codeLength] = Integer.parseInt(st.substring(5, 9), 16);
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
	 * @param oldC the old Counter row
	 * @param newC the new Counter row
	 * **/
	public void setCodeViewCounter(int oldC, int newC) 
	{
		this.gui.tbl_code.setValueAt(" ", oldC-1, 0);
		this.gui.tbl_code.setValueAt("->", newC-1, 0);
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
					gui.tbl_code.addRow(new Object[]{"","    ", "    ", j+1 , "",""});
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
					gui.tbl_code.addRow(new Object[]{"","    ", "    ", j+1 , "",mnemonicLines[j]});
				}else if(proceed.contains("EQU")) 
				{
					// EQU should not affect any variable
					gui.tbl_code.addRow(new Object[]{"","    ", "    ", j+1 , "",mnemonicLines[j]});
				}else if(proceed.charAt(0) != ' ') 
				{
					// if the first char in a line is unequal to space it is a label
					// pay attention that the EQU is checked before, because EQU has unequal first character too
					
					
					gui.tbl_code.addRow(new Object[]{"","    ", "    ", j+1 , proceed,""});
				}else if(this.mnemonicLines[j].charAt(0) == ' ') 
				{
					
					// normal code line, executed by parser 
					System.out.println("Controller: "+proceed);
					String binaryCode = parser.fromMnemToHex(proceed, j).toString();
					
					gui.tbl_code.addRow(new Object[]{"",Integer.toHexString(pc), Integer.toHexString(Integer.parseInt(binaryCode, 2)), j+1 , "",mnemonicLines[j]});
					this.memory.programMemory[pc] = Integer.parseInt(binaryCode, 2);
					
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
		    column = gui.table_Code.getColumnModel().getColumn(i);
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
		gui.tbl_code.setColumnIdentifiers	(new Object[] {" ", "ProgramCounter", "ProgramCode", "LineCount","Label","MnemonicCode"});
		gui.tbl_memory.setColumnIdentifiers	(new Object[] {"","00","01","02","03","04","05","06","07"});
		gui.tbl_special.setColumnIdentifiers(new Object[]{"Register", "Hex-Wert", "Bin-Wert"});
	}

	/**
	 * This method selects the command which must be executed
	 * @param command the command to execute as a String
	 * **/
	public void executeCommand(String command) 
	{
		if(command.substring(0, 6).equals("000111")) 					// ADDWF
		{
			this.addwf(command.substring(6, 7),command.substring(7));
		}else if(command.substring(0, 6).equals("000101")) 				// ANDWF
		{
			this.andwf(command.substring(6, 7),command.substring(7));
		}else if(command.substring(0, 7).equals("0000011"))  			// CLRF
		{
			this.clrf(command.substring(7));
		}else if(command.substring(0, 7).equals("0000010")) 			// CLRW
		{
			this.clrw(); 
		}else if(command.substring(0, 6).equals("001001")) 				// COMF
		{
			this.comf(command.substring(6, 7),command.substring(7)); 
		}else if(command.substring(0, 6).equals("000011")) 				// DECF
		{
			this.decf(command.substring(6, 7),command.substring(7)); 
		}else if(command.substring(0, 6).equals("001011")) 				// DECFSZ
		{
			this.decfsz(command.substring(6, 7),command.substring(7)); 
		}else if(command.substring(0, 6).equals("001010")) 				// INCF
		{
			this.incf(command.substring(6, 7),command.substring(7)); 
		}else if(command.substring(0, 6).equals("001111")) 				// INCFSZ
		{
			this.incfsz(command.substring(6, 7),command.substring(7)); 
		}else if(command.substring(0, 6).equals("000100")) 				// IORWF
		{
			this.iorwf(command.substring(6, 7),command.substring(7)); 
		}else if(command.substring(0, 6).equals("001000")) 				// MOVF
		{
			this.movf(command.substring(6, 7),command.substring(7)); 
		}else if(command.substring(0, 7).equals("0000001")) 			// MOVWF
		{
			this.movwf(command.substring(7)); 
		}else if(command.equals("00000000000000")) 						 // NOP
		{
			this.nop();
		}else if(command.substring(0, 6).equals("001101")) 				// RLF
		{
			this.rlf(command.substring(6, 7),command.substring(7)); 
		}else if(command.substring(0, 6).equals("001100")) 				// RRF
		{
			this.rrf(command.substring(6, 7),command.substring(7)); 
		}else if(command.substring(0, 6).equals("000010")) 				// SUBWF
		{
			this.subwf(command.substring(6, 7),command.substring(7)); 
		}else if(command.substring(0, 6).equals("001110")) 				// SWAPF
		{
			this.swapf(command.substring(6, 7),command.substring(7)); 
		}else if(command.substring(0, 6).equals("000110")) 				// XORWF
		{
			this.xorwf(command.substring(6, 7),command.substring(7)); 
		}else if(command.substring(0, 4).equals("0100")) 				// BCF
		{
			this.bcf(command.substring(4, 7),command.substring(7)); 
		}else if(command.substring(0, 4).equals("0101")) 				// BSF
		{
			this.bsf(command.substring(4, 7),command.substring(7)); 
		}else if(command.substring(0, 4).equals("0110")) 				// BTFSC
		{
			this.btfsc(command.substring(4, 7),command.substring(7)); 
		}else if(command.substring(0, 4).equals("0111")) 				// BTFSS
		{
			this.btfss(command.substring(4, 7),command.substring(7)); 
		}else if(command.substring(0, 6).equals("111110")) 				// ADDLW
		{
			this.addlw(command.substring(6)); 
		}else if(command.substring(0, 6).equals("111001")) 				// ANDLW
		{
			this.andlw(command.substring(6)); 
		}else if(command.substring(0, 3).equals("100")) 				// CALL
		{
			this.call(command.substring(3)); 
		}else if(command.equals("00000001100100")) 					// CLRWDT
		{
			this.clrwdt(); 
		}else if(command.substring(0, 3).equals("101")) 				// GOTO
		{
			this._goto(command.substring(3));
		}else if(command.substring(0, 6).equals("111000")) 				// IORLW
		{
			this.iorlw(command.substring(6)); 
		}else if(command.substring(0, 6).equals("110000")) 				// MOVLW
		{
			this.movlw(command.substring(6)); 
		}else if(command.equals("00000000001001")) 					// RETFIE
		{
			this.retfie(); 
		}else if(command.substring(0, 6).equals("110100")) 				// RETLW
		{
			this.retlw(command.substring(6)); 
		}else if(command.equals("00000000001000")) 					// RETURN
		{
			this._return(); 
		}else if(command.equals("00000001100011")) 					// SLEEP
		{
			this.sleep(); 
		}else if(command.substring(0, 6).equals("111100")) 				// SUBLW
		{
			this.sublw(command.substring(6)); 
		}else if(command.substring(0, 6).equals("111010")) 				// XORLW
		{
			this.xorlw(command.substring(6)); 
		}else {												// error
			System.out.println("There is no command for the inserted string: "+command);
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
	private void addwf(String d,String f) 
	{
		int w_in = memory.get_WREGISTER();
		int f_in = memory.get_Memory(Integer.parseInt(f,2));
		if(d.equals("0"))
		{
			memory.set_WREGISTER(f_in+w_in);
		}else if(d.equals("1")) 
		{
			memory.set_SRAM(Integer.parseInt(f,2), w_in+f_in);
		}
	}
	
	/**
	 * This method executes the ANDWF command.
	 * AND the W register with register 'f'. 
	 * If 'd' is 0 the result is stored in the W register. If 'd' is 1 the result is stored back in register 'f'.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void andwf(String d, String f) 
	{
		int w_in = memory.get_WREGISTER();
		int f_in = memory.get_Memory(Integer.parseInt(f,2));
		String w_bin = Integer.toBinaryString(w_in);
		String f_bin = Integer.toBinaryString(f_in);
		String out = "";
		for(int i = 0; i< 8 ;i++) 
		{
			if(w_bin.charAt(7-i) == f_bin.charAt(7-i)) 
			{
				if(w_bin.charAt(7-i) == '1') 
				{
					out = "1" + out;
				}else { out = "0" + out;}
			}else {
				out = "0" + out;
			}
		}
		if(d.equals("0"))
		{
			memory.set_WREGISTER(Integer.parseInt(out, 2));
		}else if(d.equals("1")) 
		{
			memory.set_SRAM(Integer.parseInt(f,2), Integer.parseInt(out, 2));
		}
	}
	
	/**
	 * This method executes the CLRF command.
	 * The contents of register f are cleared and the Z bit is set.
	 * @param f The file register location as String
	 * **/
	private void clrf(String f) 
	{
		memory.set_SRAM(Integer.parseInt(f, 2), 0);
	}
	
	/**
	 * This method executes the CLRW command.
	 * W register is cleared. Zero bit (Z) is set.
	 * **/
	private void clrw() 
	{
		memory.set_WREGISTER(0);
	}
	
	/**
	 * This method executes the COMF command.
	 * The contents of register f are complemented. 
	 * If d is 0 the result is stored in W. If d is 1 the result is stored back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void comf(String d, String f) 
	{
		int in = memory.get_Memory(Integer.parseInt(f, 2));
		int out = 255 - in;
		if(d.equals("0")) 
		{
			memory.set_WREGISTER(out);
		}else if(d.equals("1")) 
		{
			memory.set_SRAM(Integer.parseInt(f, 2), out);
		}
	}
	
	/**
	 * This method executes the DECF command.
	 * Decrement register f. 
	 * If d is 0 the result is stored in the W register. If d is 1 the result is stored back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void decf(String d, String f) 
	{
		int in = memory.get_Memory(Integer.parseInt(f, 2));
		if(in == 0) 
		{
			in = 255;
		}else {
			in--;
		}
		if(d.equals("0")) 
		{
			memory.set_WREGISTER(in);
		}else if(d.equals("1")) 
		{
			memory.set_SRAM(Integer.parseInt(f, 2), in);
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
	private void decfsz(String d, String f) 
	{
		int in = memory.get_Memory(Integer.parseInt(f, 2));
		if(in == 0) 
		{
			in = 255;
		}else {
			in--;
			if(in == 0) 
			{
				this.programmCounter++;
			}
		}
		if(d.equals("0")) 
		{
			memory.set_WREGISTER(in);
		}else if(d.equals("1")) 
		{
			memory.set_SRAM(Integer.parseInt(f, 2), in);
		}
	}
	
	/**
	 * This method executes the INCF command.
	 * The contents of register f are incremented. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is placed back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void incf(String d, String f) 
	{
		int in = memory.get_Memory(Integer.parseInt(f, 2));
		if(in == 255) 
		{
			in = 0;
		}else {
			in++;
		}
		if(d.equals("0")) 
		{
			memory.set_WREGISTER(in);
		}else if(d.equals("1")) 
		{
			memory.set_SRAM(Integer.parseInt(f, 2), in);
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
	private void incfsz(String d, String f) 
	{
		int in = memory.get_Memory(Integer.parseInt(f, 2));
		if(in == 255) 
		{
			in = 0;
			this.programmCounter++;
		}else {
			in++;
		}
		if(d.equals("0")) 
		{
			memory.set_WREGISTER(in);
		}else if(d.equals("1")) 
		{
			memory.set_SRAM(Integer.parseInt(f, 2), in);
		}
	}
	
	/**
	 * This method executes the IORWF command.
	 * Inclusive OR the W register with register f. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is placed back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void iorwf(String d, String f) 
	{
		int w_in = memory.get_WREGISTER();
		int f_in = memory.get_Memory(Integer.parseInt(f,2));
		String w_bin = Integer.toBinaryString(w_in);
		String f_bin = Integer.toBinaryString(f_in);
		String out = "";
		for(int i = 0; i< 8 ;i++) 
		{
			if(w_bin.charAt(7-i) == '1' ||  f_bin.charAt(7-i) == '1') 
			{
					out = "1" + out;
			}else {
				out = "0" + out;
			}
		}
		if(d.equals("0"))
		{
			memory.set_WREGISTER(Integer.parseInt(out, 2));
		}else if(d.equals("1")) 
		{
			memory.set_SRAM(Integer.parseInt(f,2), Integer.parseInt(out, 2));
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
	private void movf(String d, String f) 
	{
		if(d.equals("0"))
		{
			int f_in =this.memory.get_Memory(Integer.parseInt(f,2));
			memory.set_WREGISTER(f_in);
		}else if(d.equals("1")) 
		{
			int w_in = memory.get_WREGISTER();
			memory.set_SRAM(Integer.parseInt(f,2), w_in);
		}
	}
	
	/**
	 * This method executes the MOVWF command.
	 * Move data from W register to register 'f'.
	 * @param f The file register location as String
	 * **/
	private void movwf(String f) 
	{
		int in = memory.get_WREGISTER();
		memory.set_SRAM(Integer.parseInt(f, 2), in);
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
	private void rlf(String d, String f) 
	{
		int in = memory.get_Memory(Integer.parseInt(f, 2));
		String f_in = Integer.toBinaryString(in);
		int carry = memory.get_CARRYFLAG();
		String carry_in = Integer.toBinaryString(carry);
		String out = carry_in;
		// refill the f_in string
		if(f_in.length() < 8) 
		{
			for(int i = 0; i< f_in.length(); i++) 
			{
				f_in = "0" + f_in;
			}
		}
		for(int i = 0; i<7; i++) 
		{
			if(f_in.charAt(7-i) == '1') 
			{
				out = "1" + out;
			}else {
				out = "0" + out;
			}
		}
		if(f_in.charAt(0) == '0') 
		{
			memory.set_CARRYFLAG(0);
		}else {
			memory.set_CARRYFLAG(1);
		}
		if(d.equals("0")) 
		{
			memory.set_WREGISTER(Integer.parseInt(out, 2));
		}else if(d.equals("1")) 
		{
			memory.set_SRAM(Integer.parseInt(f, 2), Integer.parseInt(out, 2));
		}
	}
	
	/**
	 * This method executes the RRF command.
	 * The contents of register f are rotated one bit to the right through the Carry Flag. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is stored back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void rrf(String d, String f) 
	{
		int in = memory.get_Memory(Integer.parseInt(f, 2));
		String f_in = Integer.toBinaryString(in);
		int carry = memory.get_CARRYFLAG();
		String carry_in = Integer.toBinaryString(carry);
		String out = carry_in;
		if(f_in.length() < 8) 
		{
			for(int i = 0; i< f_in.length(); i++) 
			{
				f_in = "0" + f_in;
			}
		}
		for(int i = 0; i<7; i++) 
		{
			if(f_in.charAt(i) == '1') 
			{
				out =  out + "1" ;
			}else {
				out =  out + "0";
			}
		}
		if(f_in.charAt(7) == '0') 
		{
			memory.set_CARRYFLAG(0);
		}else {
			memory.set_CARRYFLAG(1);
		}
		if(d.equals("0")) 
		{
			memory.set_WREGISTER(Integer.parseInt(out, 2));
		}else if(d.equals("1")) 
		{
			memory.set_SRAM(Integer.parseInt(f, 2), Integer.parseInt(out, 2));
		}
	}
	
	/**
	 * This method executes the SUBWF command.
	 * Subtract (2s complement method) W register from register 'f'. 
	 * If 'd' is 0 the result is stored in the W register. If 'd' is 1 the result is stored back in register 'f'.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void subwf(String d, String f) 
	{
		int w_in = memory.get_WREGISTER();
		int f_in = memory.get_Memory(Integer.parseInt(f, 2));
		
		int erg;
		if(w_in > f_in) 
		{
			erg = 255 - (w_in-f_in);
		}else {
			erg = f_in - w_in;
		}
		
		if(d.equals("0"))
		{
			memory.set_WREGISTER(erg);
		}else if(d.equals("1")) 
		{
			memory.set_SRAM(Integer.parseInt(f,2), erg);
		}
	}
	
	/**
	 * This method executes the SWAPF command.
	 * The upper and lower nibbles of register 'f' are exchanged. 
	 * If 'd' is 0 the result is placed in W register. If 'd' is 1 the result is placed in register 'f'.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void swapf(String d, String f) 
	{
		int f_in = memory.get_Memory(Integer.parseInt(f,2));
		String in = Integer.toBinaryString(f_in);
		for(int i = in.length(); i< 8; i++) 
		{
			in = "0"+in;
		}
		String erg = in.substring(4, 8) + in.substring(0, 4);
		if(d.equals("0"))
		{
			memory.set_WREGISTER(Integer.parseInt(erg, 2));
		}else if(d.equals("1")) 
		{
			memory.set_SRAM(Integer.parseInt(f,2),Integer.parseInt(erg, 2) );
		}
	}
	
	/**
	 * This method executes the XORWF command.
	 * Exclusive OR the contents of the W register with register 'f'. 
	 * If 'd' is 0 the result is stored in the W register. If 'd' is 1 the result is stored back in register 'f'.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void xorwf(String d, String f) 
	{
		int w_in = memory.get_WREGISTER();
		int f_in = memory.get_Memory(Integer.parseInt(f,2));
		String w_bin = Integer.toBinaryString(w_in);
		while(w_bin.length() < 9) 
		{
			w_bin = "0"+w_bin;
		}
		String f_bin = Integer.toBinaryString(f_in);
		while(f_bin.length() < 9) 
		{
			f_bin = "0"+f_bin;
		}
		String out = "";
		for(int i = 0; i< 8 ;i++) 
		{
			if(w_bin.charAt(7-i) == '1' || '1' ==  f_bin.charAt(7-i)) 
			{
				if(w_bin.charAt(7-i)  ==  f_bin.charAt(7-i)) 
				{
					out = "0" + out;
				}else { out = "1" + out;}
			}else {
				out = "0" + out;
			}
		}
		if(d.equals("0"))
		{
			memory.set_WREGISTER(Integer.parseInt(out, 2));
		}else if(d.equals("1")) 
		{
			memory.set_SRAM(Integer.parseInt(f,2), Integer.parseInt(out, 2));
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
	private void bcf(String b, String f) 
	{
		memory.set_SRAM(Integer.parseInt(f, 2), Integer.parseInt(b, 2), 0);
	}
	
	/**
	 * This method executes the BSF command.
	 * Bit b in register f is set.
	 * @param b The bit as String
	 * @param f The file register location as String
	 * **/
	private void bsf(String b, String f) 
	{
		memory.set_SRAM(Integer.parseInt(f, 2), Integer.parseInt(b, 2), 0);
	}
	private void btfsc(String b, String f) 
	{
		int in = memory.get_Memory(Integer.parseInt(f, 2), Integer.parseInt(b, 2));
		if(in == 0) 
		{
			
		}else {
			this.programmCounter++;
		}
	}
	
	/**
	 * This method executes the BTFSS command.
	 * If bit b in register f is 0 then the next instruction is executed. 
	 * If bit b is 1, then the next instruction is discarded and a NOP is executed instead, making this a 2TCY instruction.
	 * @param b The bit as String
	 * @param f The file register location as String
	 * **/
	private void btfss(String b, String f) 
	{
		int in = memory.get_Memory(Integer.parseInt(f, 2), Integer.parseInt(b, 2));
		if(in == 1) 
		{
			
		}else {
			this.programmCounter++;
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
	private void addlw(String k) 
	{
		int in = memory.get_WREGISTER();
		memory.set_WREGISTER(in+Integer.parseInt(k, 2));
	}
	
	/**
	 * This method executes the ANDLW command.
	 * The contents of W register are ANDed with the eight bit literal 'k'. 
	 * The result is placed in the W register.
	 * @param k The Literal as String.
	 * **/
	private void andlw(String k) 
	{    // eventuell k string mit nullen auffüllen
		while(k.length() < 9) 
		{
			k = "0"+k;
		}
		
		int w_in = memory.get_WREGISTER();
		String w_bin = Integer.toBinaryString(w_in);
		
		while(w_bin.length() < 9) 
		{
			w_bin = "0"+w_bin;
		}
		
		String out = "";
		for(int i = 0; i< 8 ;i++) 
		{
			if(w_bin.charAt(7-i) == k.charAt(7-i)) 
			{
				if(w_bin.charAt(7-i) == '1') 
				{
					out = "1" + out;
				}else { out = "0" + out;}
			}else {
				out = "0" + out;
			}
		}
		memory.set_WREGISTER(Integer.parseInt(out, 2));
	}
	
	/**
	 * This method executes the CALL command.
	 * Call Subroutine.
	 * @param k The Literal as String.
	 * **/
	private void call(String k) 
	{
		memory.pushToStack(this.programmCounter);
		this.programmCounter = Integer.parseInt(k, 2)-1;
	}
	
	/**
	 * This method executes the CLRWDT command.
	 * Clears the Watchdog timer.
	 * **/
	private void clrwdt() 
	{
		// keine ahnung was hier zu tun ist
	}
	
	// Attention the function goto is a basic java function
	/**
	 * This method executes the GOTO command.
	 * Sets the {@link programmCounter} to the new position 'k'.
	 * @param k the position as String
	 * **/
	private void _goto(String k) 
	{
		System.out.println("k: "+k);
		this.programmCounter = Integer.parseInt(k, 2)-1;
	}
	
	/**
	 * This method executes the IORLW command.
	 * The contents of the W register is ORed with the eight bit literal 'k'. 
	 * The result is placed in the W register.
	 * @param k the position as String
	 * **/
	private void iorlw(String k) 
	{   // eventuell hier mit nullen auffüllen 
		int w_in = memory.get_WREGISTER();
		int k_in = Integer.parseInt(k,2);
		String w_bin = Integer.toBinaryString(w_in);
		String k_bin = Integer.toBinaryString(k_in);
		String out = "";
		for(int i = 0; i< 8 ;i++) 
		{
			if(w_bin.charAt(7-i) == '1' ||  k_bin.charAt(7-i) == '1') 
			{
					out = "1" + out;
			}else {
				out = "0" + out;
			}
		}
		memory.set_WREGISTER(Integer.parseInt(out, 2));
	}
	
	/**
	 * This method executes the IORLW command.
	 * The literal 'l' is loaded to the W register.
	 * @param l the literal as String
	 * **/
	private void movlw(String l) 
	{
		memory.set_WREGISTER(Integer.parseInt(l, 2));
	}
	
	/**
	 * This method executes the RETFIE command.
	 * Return from Interrupt
	 * **/
	private void retfie() 
	{
		// keine ahnung was hier gemacht werden muss
	}
	
	/**
	 * This method executes the RETLW command.
	 * Returns with the Literal 'k' in the W Register
	 * @param k the literal as String
	 * **/
	private void retlw(String k) 
	{
		memory.set_WREGISTER(Integer.parseInt(k, 2));
		this.programmCounter = memory.popFromStack();
	}
	
	// Attention the function return is a basic java function
	/**
	 * This method executes the RETURN command.
	 * Returns from subroutine.
	 * **/
	private void _return() 
	{
		this.programmCounter = memory.popFromStack();
	}
	
	/**
	 * This method executes the SLEEP command.
	 * **/
	private void sleep() 
	{
		// keine ahnung was hier gemacht werden muss
	}
	
	/**
	 * This method executes the SUBLW command.
	 * The W register is subtracted from the literal 'k'.
	 * The result is placed in the W register.
	 * @param k The literal as String
	 * **/
	private void sublw(String k) 
	{  // flags müssen bei Überlauf noch gesetzt werden
		int w_in = memory.get_WREGISTER();
		int k_in = Integer.parseInt(k, 2);		
		int erg;
		if(w_in > k_in) 
		{
			erg = 255 - (w_in-k_in);
		}else {
			erg = k_in - w_in;
		}
		memory.set_WREGISTER(erg);
	}
	
	/**
	 * This method executes the XORLW command.
	 * The contents of the W register are XORed with the eight bit literal 'k'. 
	 * The result is placed in the W register
	 * @param k The literal as String
	 * **/
	private void xorlw(String k) 
	{   // eventuell hier noch nullen auffüllen
		int w_in = memory.get_WREGISTER();
		int k_in = Integer.parseInt(k,2);
		String w_bin = fillUpBinString(Integer.toBinaryString(w_in));
		String k_bin  = fillUpBinString(Integer.toBinaryString(k_in));
		
		String out = "";
		for(int i = 0; i< 8 ;i++) 
		{
			if(w_bin.charAt(7-i) == '1' || '1' ==  k_bin.charAt(7-i)) 
			{
				if(w_bin.charAt(7-i)  ==  k_bin.charAt(7-i)) 
				{
					out = "0" + out;
				}else { out = "1" + out;}
			}else {
				out = "0" + out;
			}
		}
		memory.set_WREGISTER(Integer.parseInt(out, 2));
	}
	
	//
	// end of commands
	//


	/**
	 * Method to close the Window of the Mnemonic editor.
	 * **/

	public void closeMnemonicWindow() {
		// TODO Auto-generated method stub
		this.mnemonicWindow.dispose();
	}
	
	/**
	 * Method to clear the code view Table.
	 * **/
	public void clearCodeTable() 
	{
		int rows = gui.tbl_code.getRowCount();
    	for(int i = 0; i<rows; i++) 
    	{
        	gui.tbl_code.removeRow(0);  		
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
		
		int portIN = gui.comboBox_AnalogIn_PortSelector.getSelectedIndex();
		int portOUT = gui.comboBox_AnalogOUT_PortSelector.getSelectedIndex();
		
		if (portIN > 0) {
			String portInValues = this.getIOAnalog_IN();
			int writeNumber = 0;
			switch(portIN) {
			case 1:
				for(int i = 0; i < 8; i++) {
					memory.set_PORTA(7 - i, Integer.parseInt((String) portInValues.subSequence(i, i + 1)));
				}
				break;
			case 2:
				for(int i = 0; i < 8; i++) {
					memory.set_PORTB(7 - i, Integer.parseInt((String) portInValues.subSequence(i, i + 1)));
				}
				break;
			case 3:
				for(int i = 0; i < 8; i++) {
					memory.set_TRISA(7 - i, Integer.parseInt((String) portInValues.subSequence(i, i + 1)));
				}
				break;
			case 4:
				for(int i = 0; i < 8; i++) {
					memory.set_TRISB(7 - i, Integer.parseInt((String) portInValues.subSequence(i, i + 1)));
				}
				break;
			}
			
		}
		if (portOUT > 0) {
			int writeNumber = 0;
			switch(portOUT) {
			case 1:
				writeNumber = memory.get_Memory((int)5);
				break;
			case 2:
				writeNumber = memory.get_Memory((int)6);
				break;
			case 3:
				writeNumber = memory.get_Memory((int)0x85);
				break;
			case 4:
				writeNumber = memory.get_Memory((int)0x86);
				break;
			}
			this.setIOAnalog_OUT(writeNumber);
		}
	}
	
	/**
	 * Method to update the selected quarz frequency.
	 * @param selectedItem The selected Item from the drop down menu.
	 * **/
	public void updateFrequency(String selectedItem) {
		switch(selectedItem) 
		{
			case "500kHz":
				this.frequency = 1500;
				break;
			case "1MHz":
				this.frequency = 1200;
				break;
			case "2MHz":
				this.frequency = 900;
				break;
			case "3MHz":
				this.frequency = 600;
				break;
			case "4MHz":
				this.frequency = 300;
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
		           
		            
		            myWriter.write(this.mnemonicWindow.txtArea_mnemonic.getText());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String st; 

    	
    	// initialisieren der grafischen Elemente
		this.mnemonicWindow.txtArea_mnemonic.setText("");
		
		String output = "";
		
    	// einlesen der einzelnen Zeilen inklusive equ und labels
		try {
			while ((st = br.readLine()) != null) { 
				
				output = output + st + '\n';
				
			}
			
			this.mnemonicWindow.txtArea_mnemonic.setText(output);
			
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
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
		    		for(int i = 0; i < 		gui.tbl_code.getRowCount(); i++) 
		    		{
		    			// get the data of specific line
		    			String progCounter = gui.tbl_code.getValueAt(i, 1).toString();
		    			while(progCounter.length() < 4) 
		    			{
		    				progCounter = "0"+progCounter;
		    			}
		    			String progCode = gui.tbl_code.getValueAt(i, 2).toString();
		    			while(progCode.length() < 4) 
		    			{
		    				progCode = "0"+progCode;
		    			}
		    			String lineCount = gui.tbl_code.getValueAt(i, 3).toString();
		    			while(lineCount.length() < 5) 
		    			{
		    				lineCount = "0"+lineCount;
		    			}
		    			String label = gui.tbl_code.getValueAt(i, 4).toString();
		    			while(label.length() < 4) 
		    			{
		    				label = " "+label;
		    			}
		    			String mnemonic = gui.tbl_code.getValueAt(i, 5).toString();
		    			
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
	private String fillUpBinString(String in) 
	{
		String out = in;
		while(out.length() < 8) 
		{
			out = "0"+ out;
		}
		return out;
	}


}
