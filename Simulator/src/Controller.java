import java.io.*;

import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
/// class Controller
/**
*  This class is the heart of this Simulator.
*  It is the Connection to the GUI. Each interaction by a user on the GUI is executed in Controller class.
*  Here are objects of Procesor, memory, timer, interupt and watchdog
* **/
public class Controller {
	
	/// object of main gui
	private Simulator_Window gui;
	/// object of mnemonic editor gui
	private MnemonicView mnemonicWindow;
	/// object of a error dialog
	private ErrorDialog errorView;
	
	
	protected boolean isCompiled = false ;
	
	protected int[][] data = new int[32][8];
	
	protected String[][] tableData = new String[32][9];
	
	protected String[] numbers = new String[256];
	
	protected String[] jumpers = new String[512];
	
	protected String[] equ = new String[256];
	
	protected String[] lines;
	
	protected String[] mnemonicLines;
	
	protected String[] hexCode;
	protected int[] programCounterList = new int[1024];
	private int jumpersCount = 0;
	
	private int equCount = 0;
	
	protected int programmCounter;
	
	protected String[] code;
	
	protected int codeLength = 0;
	
	protected int lineCount;
	
	/// Processor object used to work each code step
	protected Processor proc;
	/// memory object used to store the data of microprocessor
	protected Memory memory;
	/// Parser Object to parse Mnemonic Code into Binary Code
	protected MnemonicParser parser;
	

	public Controller(Simulator_Window pGui) 
	{
		this.gui = pGui;
		memory = new Memory(this);
		parser = new MnemonicParser(this);
	}
	///initialice the memory table with the initiale tabledata
	public void inizializeMemory() 
	{
		for(int i = 0; i< 256; i++) {
			numbers[i] = Integer.toHexString(i);
			System.out.println(i/8+" R:"+i%8);
			data[i/8][i%8] = 0;
			tableData[i/8][i%8+1] = Integer.toString( data[i/8][i%8]);
		}
		for(int i = 0; i < 32; i++) 
		{
			tableData[i][0] = Integer.toHexString(i*8);
			this.gui.tbl_memory.addRow(new Object[] {tableData[i][0],"0","0","0","0","0","0","0","0"});
		}
	}
	protected void updateMemoryTable(String value,int x, int y) 
	{
		gui.SetData(value, x, y+1);
	}
	protected void updateWRegTable(String value, int x, int y) 
	{
		gui.setSpecialData(value, x, y);
	}
	
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
	
	public void showError(String title,String text) 
	{
		errorView = new ErrorDialog();
		errorView.setVisible(true);
		errorView.lbl_ErrorTitle.setText(title);
		errorView.lbl_ErrorText.setText(text);
	}
	
	protected void loadMnemonicFromTable() 
	{
		String mnemonic = "";
		for(int i = 0; i < 		gui.tbl_code.getRowCount(); i++) 
		{
			// check for Labels
			String label = gui.tbl_code.getValueAt(i, 4).toString();
			if(!label.isEmpty()) 
			{
				mnemonic = mnemonic + label + "\n";
			}else 
			{
				String code = gui.tbl_code.getValueAt(i, 5).toString();
				if(code.contains("EQU")) 
				{
					mnemonic = mnemonic + code + "\n";
				}else 
				{
					mnemonic = mnemonic + "  " + code + "\n";
				}
			}
		}
		mnemonicWindow.txtArea_mnemonic.setText(mnemonic);
		mnemonicWindow.txtArea_mnemonic.repaint();
	}

	public void startSimu() {
		System.out.println("Simulation started...");
		this.outputToConsole("Simulation started...");
		proc = new Processor(this);
		proc.start();	
	}
	public void start() 
	{
		lineCount = this.mnemonicWindow.txtArea_mnemonic.getLineCount();
		lines = this.mnemonicWindow.txtArea_mnemonic.getText().split("\\n");
	}
	public void stopSimu() {
		System.out.println("Simulation stopped...");
		this.outputToConsole("Simulation stopped...");
		proc.stopThread();
	}
	/**
	 * 
	 * 
	 * have to be changed to codeTable
	 * 
	 * 
	 * 
	 * 
	 * 
	 * ***/
	// highlighting the text where the programmcounter points to in Mnemonic Editor 
	public void setTextActive(int row) throws BadLocationException 
	{
		this.mnemonicWindow.txtArea_mnemonic.setSelectionEnd(0);
		this.mnemonicWindow.txtArea_mnemonic.requestFocus();
		this.mnemonicWindow.txtArea_mnemonic.select(this.mnemonicWindow.txtArea_mnemonic.getLineStartOffset(row),this.mnemonicWindow.txtArea_mnemonic.getLineEndOffset(row));
	}

	//check if there are any jump marks in the code, adding to jumpers list and delete from code
	public String[] searchJumperMarks(String[] pCode) 
	{
		for(int i = 0; i<pCode.length;i++) 
		{
			if((pCode[i].charAt(0) != ' ') && (pCode[i].contains("EQU") == false))
			{
				jumpers[this.jumpersCount] = pCode[i]+":"+(i+1);
				// kann eventuell weg gelassen werden
				
				jumpersCount++;
				
				System.out.println("JumperMark found: "+pCode[i]+" at Line "+i);
				this.outputToConsole("JumperMark found: "+pCode[i]+" at Line "+i);
			}
		}
		return pCode;
	}
	public String[] searchEQUMarks(String[] pCode) {
		for(int i = 0; i<pCode.length; i++) 
		{
			if(pCode[i].contains("EQU")) 
			{
				String beforeToken ="";
				String afterToken ="";
				String[] token;
				token = pCode[i].split(" ");

				if(token.length > 2) 
				{
					for(int j = 0; j< token.length; j++) 
					{
						if(token[j].equals("EQU")) 
						{  // eventuell ist es hier nötig den abstand zwischen wörtern zum token zu zählen
							beforeToken = token[j-1];
							afterToken = token[j+1];
							equ[this.equCount] = beforeToken+":"+afterToken;
							// zeile soll nicht mehr gelöscht werden
							// pCode[i] = "";
							this.equCount++;
							System.out.println("EQU found in Line "+i+" ("+pCode[i]+")");
						}
					}
				}else {
					System.out.println("No valid EQU: "+pCode[i]);
				}
			}
		}
		return pCode;
	}
	public String getEQUValue(String equName) 
	{
		String out = "";
		for(int i = 0; i<this.equCount; i++) {
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
	protected String getIOPort_A() 
	{
		String port_a = "";
		if(gui.rb_io_1.isSelected()) {port_a = "1"+port_a;}else {port_a = "0"+port_a;}
		if(gui.rb_io_2.isSelected()){port_a = "1"+port_a;}else {port_a = "0"+port_a;}
		if(gui.rb_io_3.isSelected()){port_a = "1"+port_a;}else {port_a = "0"+port_a;}
		if(gui.rb_io_4.isSelected()){port_a = "1"+port_a;}else {port_a = "0"+port_a;}
		if(gui.rb_io_5.isSelected()){port_a = "1"+port_a;}else {port_a = "0"+port_a;}
		if(gui.rb_io_6.isSelected()){port_a = "1"+port_a;}else {port_a = "0"+port_a;}
		if(gui.rb_io_7.isSelected()){port_a = "1"+port_a;}else {port_a = "0"+port_a;}
		if(gui.rb_io_8.isSelected()){port_a = "1"+port_a;}else {port_a = "0"+port_a;}
		return "";
	}
	protected String getIOPort_B() 
	{
		return "";
	}
	protected void setIOPort_A() 
	{
		
	}
	protected void setIOPort_B() 
	{
		
	}
	
	public void outputToConsole(String in) 
	{
		this.gui.txtArea_Console.setText(in+"\n"+this.gui.txtArea_Console.getText());
	}
	public void setSegment(int c1,int c2, int c3, int c4) 
	{
		this.gui.setSegment(c1, c2, c3, c4);
	}
	
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
    	for(int i = 0; i<gui.tbl_code.getRowCount(); i++) 
    	{
        	gui.tbl_code.removeRow(i);  		
    	}

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
	}
	
	public void saveMnemonicCode(String text) {
		String[] splittedMnemonic = text.replaceAll("\\r", "").split("\\n");
		mnemonicLines = new String[splittedMnemonic.length];
		mnemonicLines = splittedMnemonic;
		this.isCompiled = false;
	}
	
	public void setCodeViewCounter(int oldC, int newC) 
	{
		this.gui.tbl_code.setValueAt(" ", oldC, 0);
		this.gui.tbl_code.setValueAt("->", newC, 0);
	}
	
	public void setCodeViewLabel(int line,String label) 
	{
		//this.gui.tbl_code.setValueAt(label, line, 4);
	}
	
	public void setCodeViewAdress(int line,int adress) 
	{
		//this.gui.tbl_code.setValueAt(Integer.toHexString(adress), line, 2);
	}
	
	protected int getJumpersCount() 
	{
		return this.jumpersCount;
	}
	
	// needs to be reworked
	public void compileCode() {
		// local program counter variable 
		int pc = 0;
		
		if(!this.isCompiled) 
		{
			// delete all memory from last code
			this.memory.clearProgMem();
			
	    	// initialisieren der grafischen Elemente
	    	for(int i = 0; i<gui.tbl_code.getRowCount(); i++) 
	    	{
	        	gui.tbl_code.removeRow(i);  		
	    	}
			
			// suche nach variablen marken , speichern der wertpaare
			this.mnemonicLines = this.searchEQUMarks(this.mnemonicLines);
			
			for(int j = 0; j <this.mnemonicLines.length;j++) 
			{
				//gui.tbl_code.addRow(this.fromMnemToHex(this.code[j], j));
				
				if(this.mnemonicLines[j].isEmpty()) 
				{
					// empty line 
					gui.tbl_code.addRow(new Object[]{"","", "", j , "",""});
				}else if(this.mnemonicLines[j].contains("org")) 
				{
					// org statement should change the pc
					String[] tmp = this.mnemonicLines[j].split(" ");
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
					gui.tbl_code.addRow(new Object[]{"","", "", j , "",mnemonicLines[j]});
				}else if(this.mnemonicLines[j].contains("EQU")) 
				{
					// EQU should not affect any variable
					gui.tbl_code.addRow(new Object[]{"","", "", j , "",mnemonicLines[j]});
				}else if(this.mnemonicLines[j].charAt(0) != ' ') 
				{
					// if the first char in a line is unequal to space it is a label
					// pay attention that the EQU is checked before, because EQU has unequal first character too
					
					// save that jumper mark with the correct program counter
					jumpers[this.jumpersCount] = this.mnemonicLines[j]+":"+(pc);
					jumpersCount++;
					
					gui.tbl_code.addRow(new Object[]{"","", "", j , mnemonicLines[j],""});
				}else if(this.mnemonicLines[j].charAt(0) == ' ') 
				{
					// normal code line, executed by parser 
					String binaryCode = parser.fromMnemToHex(this.mnemonicLines[j], j)[3].toString();
					
					gui.tbl_code.addRow(new Object[]{"",Integer.toHexString(pc), Integer.toHexString(Integer.parseInt(binaryCode, 2)), j , "",mnemonicLines[j]});
					this.memory.programMemory[pc] = Integer.parseInt(binaryCode, 2);
					// pc needs to be incremented
					pc++;
				}
			}
			this.isCompiled = true;
		}else {
			System.out.println("Mnemonic-Code is already compiled...");
		}
	}
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
	public void inizializeTables() 
	{
		gui.tbl_code.setColumnIdentifiers(new Object[] {" ", "ProgramCounter", "ProgramCode", "LineCount","Label","MnemonicCode"});
		gui.tbl_memory.setColumnIdentifiers(new Object[] {"","00","01","02","03","04","05","06","07"});
		gui.tbl_special.setColumnIdentifiers(new  Object[]{"Register", "Hex-Wert", "Bin-Wert"});
	}


	// This function selects the command which must executed
	public void executeCommand(String command) 
	{
		if(command.substring(0, 6).equals("000111")) 
		{
			this.addwf(command.substring(6, 7),command.substring(7));
		}else if(command.substring(0, 6).equals("000101")) 
		{
			this.andwf(command.substring(6, 7),command.substring(7)); // execute ANDWF
		}else if(command.substring(0, 7).equals("0000011")) 
		{
			this.clrf(command.substring(7)); // execute CLRF
		}else if(command.substring(0, 7).equals("0000010")) 
		{
			this.clrw(); // execute CLRW
		}else if(command.substring(0, 6).equals("001001")) 
		{
			this.comf(command.substring(6, 7),command.substring(7)); // execute COMF
		}else if(command.substring(0, 6).equals("000011")) 
		{
			this.decf(command.substring(6, 7),command.substring(7)); // execute DECF
		}else if(command.substring(0, 6).equals("001011")) 
		{
			this.decfsz(command.substring(6, 7),command.substring(7)); // execute DECFSZ
		}else if(command.substring(0, 6).equals("001010")) 
		{
			this.incf(command.substring(6, 7),command.substring(7)); // execute INCF
		}else if(command.substring(0, 6).equals("001111")) 
		{
			this.incfsz(command.substring(6, 7),command.substring(7)); // execute INCFSZ
		}else if(command.substring(0, 6).equals("000100")) 
		{
			this.iorwf(command.substring(6, 7),command.substring(7)); // execute IORWF
		}else if(command.substring(0, 6).equals("001000")) 
		{
			this.movf(command.substring(6, 7),command.substring(7)); // execute MOVF
		}else if(command.substring(0, 7).equals("0000001")) 
		{
			this.movwf(command.substring(7)); // execute MOVWF
		}else if(command.equals("00000000000000")) 
		{
			this.nop(); // execute NOP
		}else if(command.substring(0, 6).equals("001101")) 
		{
			this.rlf(command.substring(6, 7),command.substring(7)); // execute RLF
		}else if(command.substring(0, 6).equals("001100")) 
		{
			this.rrf(command.substring(6, 7),command.substring(7)); // execute RRF
		}else if(command.substring(0, 6).equals("000010")) 
		{
			this.subwf(command.substring(6, 7),command.substring(7)); // execute SUBWF
		}else if(command.substring(0, 6).equals("001110")) 
		{
			this.swapf(command.substring(6, 7),command.substring(7)); // execute SWAPF
		}else if(command.substring(0, 6).equals("000110")) 
		{
			this.xorwf(command.substring(6, 7),command.substring(7)); // execute XORWF
		}else if(command.substring(0, 4).equals("0100")) 
		{
			this.bcf(command.substring(4, 7),command.substring(7)); // execute BCF
		}else if(command.substring(0, 4).equals("0101")) 
		{
			this.bsf(command.substring(4, 7),command.substring(7)); // execute BSF
		}else if(command.substring(0, 4).equals("0110")) 
		{
			this.btfsc(command.substring(4, 7),command.substring(7)); // execute BTFSC
		}else if(command.substring(0, 4).equals("0111")) 
		{
			this.btfss(command.substring(4, 7),command.substring(7)); // execute BTFSS
		}else if(command.substring(0, 6).equals("111110")) 
		{
			this.addlw(command.substring(6)); // execute ADDLW
		}else if(command.substring(0, 6).equals("111001")) 
		{
			this.andlw(command.substring(6)); // execute ANDLW
		}else if(command.substring(0, 3).equals("100")) 
		{
			this.call(command.substring(3)); // execute CALL
		}else if(command.equals("00000001100100")) 
		{
			this.clrwdt(); // execute CLRWDT
		}else if(command.substring(0, 3).equals("101")) 
		{
			this._goto(command.substring(3));// execute GOTO
		}else if(command.substring(0, 6).equals("111000")) 
		{
			this.iorlw(command.substring(6)); // execute IORLW
		}else if(command.substring(0, 6).equals("110000")) 
		{
			this.movlw(command.substring(6)); // execute MOVLW
		}else if(command.equals("00000000001001")) 
		{
			this.retfie(); // execute RETFIE
		}else if(command.substring(0, 6).equals("110100")) 
		{
			this.retlw(command.substring(6)); // execute RETLW
		}else if(command.equals("00000000001000")) 
		{
			this._return(); // execute RETURN
		}else if(command.equals("00000001100011")) 
		{
			this.sleep(); // execute SLEEP
		}else if(command.substring(0, 6).equals("111100")) 
		{
			this.sublw(command.substring(6)); // execute SUBLW
		}else if(command.substring(0, 6).equals("111010")) 
		{
			this.xorlw(command.substring(6)); // execute XORLW
		}else {
			System.out.println("There is no command for the inserted string: "+command);
		}
		
	}
	
	//BYTE-ORIENTED FILE REGISTER OPERATIONS
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
	private void clrf(String f) 
	{
		memory.set_SRAM(Integer.parseInt(f, 2), 0);
	}
	private void clrw() 
	{
		memory.set_WREGISTER(0);
	}
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
	private void movwf(String f) 
	{
		int in = memory.get_WREGISTER();
		memory.set_SRAM(Integer.parseInt(f, 2), in);
	}
	private void nop() 
	{
		// do nothing
	}
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
	
	//BIT-ORIENTED FILE REGISTER OPERATIONS
	private void bcf(String b, String f) 
	{
		memory.set_SRAM(Integer.parseInt(f, 2), Integer.parseInt(b, 2), 0);
	}
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
	private void btfss(String b, String f) 
	{
		int in = memory.get_Memory(Integer.parseInt(f, 2), Integer.parseInt(b, 2));
		if(in == 1) 
		{
			
		}else {
			this.programmCounter++;
		}
	}
	
	//LITERAL AND CONTROL OPERATIONS
	private void addlw(String k) 
	{
		int in = memory.get_WREGISTER();
		memory.set_WREGISTER(in+Integer.parseInt(k, 2));
	}
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
	private void call(String k) 
	{
		memory.pushToStack(this.programmCounter+1);
		this.programmCounter = Integer.parseInt(k, 2);
	}
	private void clrwdt() 
	{
		// keine ahnung was hier zu tun ist
	}
	// Attention the function goto is a basic java function
	private void _goto(String k) 
	{
		this.programmCounter = Integer.parseInt(k, 2)-1;
	}
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
	private void movlw(String l) 
	{
		memory.set_WREGISTER(Integer.parseInt(l, 2));
	}
	private void retfie() 
	{
		// keine ahnung was hier gemacht werden muss
	}
	private void retlw(String k) 
	{
		memory.set_WREGISTER(Integer.parseInt(k, 2));
		this.programmCounter = memory.popFromStack();
	}
	// Attention the function return is a basic java function
	private void _return() 
	{
		this.programmCounter = memory.popFromStack();
	}
	private void sleep() 
	{
		// keine ahnung was hier gemacht werden muss
	}
	private void sublw(String k) 
	{  // flags müssen bei überlauf noch gesetzt werden
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
	private void xorlw(String k) 
	{   // eventuell hier noch nullen auffüllen
		int w_in = memory.get_WREGISTER();
		int k_in = Integer.parseInt(k,2);
		String w_bin = Integer.toBinaryString(w_in);
		String k_bin = Integer.toBinaryString(k_in);
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
	public void closeMnemonicWindow() {
		// TODO Auto-generated method stub
		this.mnemonicWindow.dispose();
	}


}
