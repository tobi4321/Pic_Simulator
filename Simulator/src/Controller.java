import java.io.File;

import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;

public class Controller {

	private Simulator_Window gui;
	static boolean isCompiled = false ;
	int[][] data = new int[32][8];
	String[][] tableData = new String[32][9];
	String[] numbers = new String[256];
	String[] jumpers = new String[1024];
	String[] lines;
	String[] hexCode;
	private int jumpersCount = 0;
	int programmCounter;
	String[] code;
	int lineCount;
	Processor proc;
	protected Memory memory;
	

	public Controller(Simulator_Window pGui) 
	{
		this.gui = pGui;
		memory = new Memory(this);
	}
	//initialice the memory table with the initiale tabledata
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
		gui.table_Memory.setValueAt(value, x, y+1);
	}
	protected void updateWRegTable(String value, int x, int y) 
	{
		gui.table_special_regs.setValueAt(value, x, y);
	}

	public void startSimu() {
		System.out.println("Simulation started...");
		this.outputToConsole("Simulation started...");
		proc = new Processor(this);
		proc.start();	
	}
	public void start() 
	{
		lineCount = this.gui.txtrMovxe.getLineCount();
		lines = this.gui.txtrMovxe.getText().split("\\n");
	}
	public void stopSimu() {
		System.out.println("Simulation stopped...");
		this.outputToConsole("Simulation stopped...");
		proc.stopThread();
	}
	// highlighting the text where the programmcounter points to in Mnemonic Editor 
	public void setTextActive(int row) throws BadLocationException 
	{
		this.gui.txtrMovxe.setSelectionEnd(0);
		this.gui.txtrMovxe.requestFocus();
		this.gui.txtrMovxe.select(this.gui.txtrMovxe.getLineStartOffset(row),this.gui.txtrMovxe.getLineEndOffset(row));
	}
	//splitting a commandline into the command and the parameters !! This function is only for Mnemonic Code !!
	public void splitter(String commandLine) 
	{
		String[] parts = commandLine.split (" ");

		for(int i = 0; i< parts.length;i++) {
			if(parts[i].equals("")) 
			{	// to skip the empty ones after splitting spaces 			
			}else {
				if((i+1) < parts.length) 
				{	//commands with one parameter
					parts[i+1].replaceAll("\\s+","");
					String[] params = parts[i+1].split(",");
					if(params.length >1) 
					{ //commands with two parameters
						String[] param2 = params[1].split(";", 1);
						performCommand(parts[i],params[0],param2[0]);
						i = parts.length;
					}else {
						performCommand(parts[i],params[0]);
						i = parts.length;}
					
				}else { // commands without parameters
					parts[i] = parts[i].replace(";", "");
					parts[i] = parts[i].replace("\r", "");
					performCommand(parts[i]);
					i = parts.length;
				}
			}
		}
	}
	//check if there are any jump marks in the code, adding to jumpers list and delete from code
	public String[] searchJumperMarks(String[] pCode) 
	{
		for(int i = 0; i<pCode.length;i++) 
		{
			String[] p = pCode[i].split(":");
			if(p.length > 1) 
			{
				jumpers[this.jumpersCount] = p[0]+":"+i;
				pCode[i] = p[1];
				jumpersCount++;
				System.out.println("JumperMark found: "+p[0]+" at Line "+i);
				this.outputToConsole("JumperMark found: "+p[0]+" at Line "+i);
			}
		}
		return pCode;
	}
	//performing Commands with two parameters
	private void performCommand(String Command, String param1, String param2) 
	{
		System.out.println("The Command is: "+Command+" with Params: "+param1+" AND "+ param2);
		this.outputToConsole("The Command is: "+Command+" with Params: "+param1+" AND "+ param2);
		if(Command.equals("MOVF")) 
		{
			int adress = Integer.decode(param1);
			String[] p = param2.split(";");
			this.gui.table_Memory.setValueAt(p[0], (adress/8), (adress%8)+1);
			this.data[adress/8][adress%8] = Integer.parseInt(p[0]);
			this.memory.set_SRAM(adress,Integer.parseInt(p[0]));
		}
	}
	//perform Commands with one parameters
	private void performCommand(String Command, String param1) 
	{
		System.out.println("The Command is: "+Command+" with Params: "+param1);
		this.outputToConsole("The Command is: "+Command+" with Params: "+param1);
		if(Command.equals("goto")) 
		{	String[] p = param1.split(";");
			for(int i = 0; i< jumpersCount; i++) 
			{
				String[] j = jumpers[i].split(":");
				if(p[0].equals(j[0])) 
				{
					programmCounter = Integer.parseInt(j[1])-1;
				}
			}
		}else if(Command.equals("inc") || Command.equals("dec")) {
			String[] p = param1.split(";");
			int adress = Integer.decode(p[0]);
			int cell = this.data[adress/8][adress%8];
			if(Command.equals("inc")) 
			{
				cell++;
			}else if(Command.equals("dec")) 
			{
				if(cell == 0) 
				{
					cell =256;
				}
				cell--;
			}			
			if(cell == 256) {cell = 0;}

			this.data[adress/8][adress%8] = cell;	

			this.gui.table_Memory.setValueAt(Integer.toString(cell), (adress/8), (adress%8)+1);
		}
	}
	//perfrom Commands without parameters
	private void performCommand(String command) {
		
	}
	
	
	public void outputToConsole(String in) 
	{
		this.gui.txtArea_Console.setText(in+"\n"+this.gui.txtArea_Console.getText());
	}
	public void setSegment(int c1,int c2, int c3, int c4) 
	{
		this.gui.setSegment(c1, c2, c3, c4);
	}
	public void loadFile(File pFile) 
	{

	}
	public void setCodeViewCounter(int oldC, int newC) 
	{
		this.gui.tbl_code.setValueAt(" ", oldC, 1);
		this.gui.tbl_code.setValueAt("->", newC, 1);
	}
	public void compileCode() {
		if(!Controller.isCompiled) 
		{
			lineCount = this.gui.txtrMovxe.getLineCount();
			lines = this.gui.txtrMovxe.getText().split("\\n");
			hexCode = new String[lineCount];
			this.code = new String[this.lineCount];
			for(int i = 0; i<this.lineCount ; i++) 
			{
				this.code[i] = this.lines[i];
				gui.tbl_code.addRow(new Object[] {"","","",""});
			}
			this.setColumnWidth();
			this.code = this.searchJumperMarks(this.code);
			for(int j = 0; j <this.code.length;j++) 
			{
				//gui.tbl_code.addRow(this.fromMnemToHex(this.code[j], j));
				Object[] val = this.fromMnemToHex(this.code[j], j);
				hexCode[j] = (String) val[3];
				gui.tbl_code.setValueAt(j, j, 0);
				gui.tbl_code.setValueAt(val[1], j, 1);
				gui.tbl_code.setValueAt(val[2], j, 2);
				gui.tbl_code.setValueAt(val[3], j, 3);
				gui.table_Code.setModel(gui.tbl_code);
			}
			Controller.isCompiled = true;
		}else {System.out.println("Mnemonic-Code is already compiled...");}
	}
	public void setColumnWidth() 
	{
		TableColumn column = null;
		for (int i = 0; i < 3; i++) {
		    column = gui.table_Code.getColumnModel().getColumn(i);
		    if (i < 2 ) {
		        column.setPreferredWidth(2); //third column is bigger
		        column.setResizable(false);
		    } else {
		        column.setPreferredWidth(100);
		    }
		}
	}
	public void inizializeTables() 
	{
		gui.tbl_code.setColumnIdentifiers(new Object[] {" "," ","Code", "Adress","Labels"});
		gui.tbl_memory.setColumnIdentifiers(new Object[] {"","00","01","02","03","04","05","06","07"});
		gui.tbl_special.setColumnIdentifiers(new  Object[]{"Register", "Hex-Wert", "Bin-Wert"});
	}
	public Object[] fromMnemToHex(String c,int line)
	{	
		c.replaceAll("\r", "");
		String hexCode = "";
		int akt = 0;
		String[] parts = c.split (" ");
		String[] params = null;
		String[] param2 = null;
		String[] p;
		String b;
		String bin ="";
		for(int i = 0; i< parts.length;i++) {
			if(parts[i].equals("")) 
			{
								
			}else {
				if((i+1) < parts.length) 
				{
					parts[i+1].replaceAll("\\s+","");
					params = parts[i+1].split(",");
					if(params.length >1) 
					{	akt = i;
						param2 = params[1].split(";", 1);
						i = parts.length;
					}else {
						akt =i;
						i = parts.length;
					}
				}else {
					 parts[i] = parts[i].replace(";", "");
					 parts[i] = parts[i].replace("\r", "");
					System.out.println("NOP out: "+parts[i]);
					akt = i;
					i = parts.length;
				}
			}
		}
		switch(parts[akt]) 
		{
			case "ADDWF":
				hexCode = "000111";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "ANDWF":
				hexCode = "000101";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "CLRF":
				hexCode = "000001";
				p = params[0].split(";");
				bin = this.hexToBinary(p[0].replaceAll("0x", ""));
				break;
			case "CLRW":
				hexCode = "00000100000000";
				break;
			case "COMF":
				hexCode = "001001";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "DECF":
				hexCode = "000011";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "DECFSZ":
				hexCode = "001011";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "INCF":
				hexCode = "001010";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "INCFSZ":
				hexCode = "001111";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "IORWF":
				hexCode = "000100";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "MOVF":
				hexCode = "001000";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "MOVWF":
				hexCode = "0000001";
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "NOP":
				hexCode = "00000000000000";
				break;
			case "RLF":
				hexCode = "001101";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "RRF":
				hexCode = "001100";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "SUBWF":
				hexCode = "000010";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "SWAPF":
				hexCode = "001110";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "XORWF":
				hexCode = "000110";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "BCF":
				hexCode = "0100";
				b = Integer.toBinaryString(Integer.parseInt(param2[0].replace(";", "")));
				for(int i = b.length(); i < 3;i++) 
				{
					b = "0" + b;
				}
				hexCode = hexCode + b;
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "BSF":
				hexCode = "0101";
				b = Integer.toBinaryString(Integer.parseInt(param2[0].replace(";", "")));
				for(int i = b.length(); i < 3;i++) 
				{
					b = "0" + b;
				}
				hexCode = hexCode + b;
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "BTFSC":
				hexCode = "0110";
				b = Integer.toBinaryString(Integer.parseInt(param2[0].replace(";", "")));
				for(int i = b.length(); i < 3;i++) 
				{
					b = "0" + b;
				}
				hexCode = hexCode + b;
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				break;
			case "BTFSS":
				hexCode = "0111";
				b = Integer.toBinaryString(Integer.parseInt(param2[0].replace(";", "")));
				for(int i = b.length(); i < 3;i++) 
				{
					b = "0" + b;
				}
				hexCode = hexCode + b;
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				break;
			case "ADDLW":
				// Bit 7 ist X wird aber als 0 gewertet
				hexCode = "111110";
				bin = this.hexToBinary(params[0].replace(";", "").replaceAll("0x", ""));
				break;
			case "ANDLW":
				hexCode = "111001";
				bin = this.hexToBinary(params[0].replace(";", "").replaceAll("0x", ""));
				break;
			case "CALL":
				hexCode = "100";
				for(int i = 0; i< jumpersCount; i++) 
				{
					String[] j = jumpers[i].split(":");
					p = params[0].split(";");
					if(p[0].equals(j[0])) 
					{
						bin = Integer.toBinaryString(Integer.parseInt(j[1]));
					}
				}
				break;
			case "CLRWDT":
				hexCode = "00000001100100";
				break;
			case "GOTO":
				hexCode = "101";
				for(int i = 0; i< jumpersCount; i++) 
				{
					String[] j = jumpers[i].split(":");
					p = params[0].split(";");
					if(p[0].equals(j[0])) 
					{
						bin = Integer.toBinaryString(Integer.parseInt(j[1]));
					}
				}
				break;
			case "IORLW":
				hexCode = "111000";
				bin = this.hexToBinary(params[0].replace(";", "").replaceAll("0x", ""));
				break;
			case "MOVLW":
				hexCode = "110000";
				bin = this.hexToBinary(params[0].replace(";", "").replaceAll("0x", ""));			
				break;
			case "RETFIE":
				hexCode = "00000000001001";
				break;
			case "RETLW":
				hexCode = "110100";
				bin = this.hexToBinary(params[0].replace(";", "").replaceAll("0x", ""));	
				break;
			case "RETURN":
				hexCode = "00000000001000";
				break;
			case "SLEEP":
				hexCode = "00000001100011";
				break;
			case "SUBLW":
				hexCode = "111100";
				bin = this.hexToBinary(params[0].replace(";", "").replaceAll("0x", ""));	
				break;
			case "XORLW":
				hexCode = "111010";
				bin = this.hexToBinary(params[0].replace(";", "").replaceAll("0x", ""));	
				break;
			default:
				System.out.println("Error in Line "+line+" while assembling to Bin-Code");
		}
		bin = bin.replaceAll("\\r", "");
		hexCode = hexCode.replaceAll("\\r", "");
		if(hexCode.length() < 14) 
		{
			int count = 14 - hexCode.length() - bin.length();
			for(int i = 0; i < count; i++) 
			{
				bin = "0"+bin;
			}
			hexCode = hexCode + bin;
		}
		
		String[] value = new String[4];
		value[0]=" ";
		value[1]=" ";
		value[2]= "";
		value[3]=hexCode;
		
		return value;
	}
	// Converter from hex to bin string
	public String hexToBinary(String hex) {
	    int i = Integer.parseInt(hex.replaceAll("\r", ""), 16);
	    String bin = Integer.toBinaryString(i);
	    return bin;
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
		// subtrahieren abklären, wie wird negativer wert auf dauer gespeichert
	}
	private void swapf(String d, String f) 
	{
		
	}
	private void xorwf(String d, String f) 
	{
		
	}
	
	//BIT-ORIENTED FILE REGISTER OPERATIONS
	private void bcf(String b, String f) 
	{
		
	}
	private void bsf(String b, String f) 
	{
		
	}
	private void btfsc(String b, String f) 
	{
		
	}
	private void btfss(String b, String f) 
	{
		
	}
	
	//LITERAL AND CONTROL OPERATIONS
	private void addlw(String k) 
	{
		
	}
	private void andlw(String k) 
	{
		
	}
	private void call(String k) 
	{
		
	}
	private void clrwdt() 
	{
		
	}
	// Attention the function goto is a basic java function
	private void _goto(String k) 
	{
		this.programmCounter = Integer.parseInt(k, 2)-1;
	}
	private void iorlw(String k) 
	{
		
	}
	private void movlw(String l) 
	{
		memory.set_WREGISTER(Integer.parseInt(l, 2));
	}
	private void retfie() 
	{
		
	}
	private void retlw(String k) 
	{
		
	}
	// Attention the function return is a basic java function
	private void _return() 
	{
		
	}
	private void sleep() 
	{
		
	}
	private void sublw(String k) 
	{
		
	}
	private void xorlw(String k) 
	{
		
	}
}
