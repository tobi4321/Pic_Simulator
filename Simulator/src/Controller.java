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
	private int jumpersCount = 0;
	int programmCounter;
	String[] code;
	int lineCount;
	Processor proc;
	

	public Controller(Simulator_Window pGui) 
	{
		this.gui = pGui;
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
		if(Command.equals("mov")) 
		{
			int adress = Integer.decode(param1);
			String[] p = param2.split(";");
			this.gui.table_Memory.setValueAt(p[0], (adress/8), (adress%8)+1);
			this.data[adress/8][adress%8] = Integer.parseInt(p[0]);
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
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;	// bin besteht nur aus 7 Bit obwohl die Register eine 8 Bit Adresse haben. Grund hierfür ist die Bank Adresierung erstes Bit wählt die Bank
				break;
			case "ANDWF":
				hexCode = "000101";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;	// bin besteht nur aus 7 Bit obwohl die Register eine 8 Bit Adresse haben. Grund hierfür ist die Bank Adresierung erstes Bit wählt die Bank
				break;
			case "CLRF":
				hexCode = "000001";
				p = params[0].split(";");
				bin = this.hexToBinary(p[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;	// bin besteht nur aus 7 Bit obwohl die Register eine 8 Bit Adresse haben. Grund hierfür ist die Bank Adresierung erstes Bit wählt die Bank
				break;
			case "CLRW":
				hexCode = "00000100000000";
				break;
			case "COMF":
				hexCode = "001001";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;	// bin besteht nur aus 7 Bit obwohl die Register eine 8 Bit Adresse haben. Grund hierfür ist die Bank Adresierung erstes Bit wählt die Bank
				break;
			case "DECF":
				hexCode = "000011";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;	// bin besteht nur aus 7 Bit obwohl die Register eine 8 Bit Adresse haben. Grund hierfür ist die Bank Adresierung erstes Bit wählt die Bank
				break;
			case "DECFSZ":
				hexCode = "001011";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
				break;
			case "INCF":
				hexCode = "001010";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
				break;
			case "INCFSZ":
				hexCode = "001111";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
				break;
			case "IORWF":
				hexCode = "000100";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
				break;
			case "MOVF":
				hexCode = "001000";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
				break;
			case "MOVWF":
				hexCode = "0000001";
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
				break;
			case "NOP":
				hexCode = "00000000000000";
				break;
			case "RLF":
				hexCode = "001101";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
				break;
			case "RRF":
				hexCode = "001100";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
				break;
			case "SUBWF":
				hexCode = "000010";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
				break;
			case "SWAPF":
				hexCode = "001110";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
				break;
			case "XORWF":
				hexCode = "000110";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.hexToBinary(params[0].replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
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
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
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
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
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
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
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
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
				break;
			case "ADDLW":
				// Bit 7 ist X wird aber als 0 gewertet
				hexCode = "111110";
				bin = this.hexToBinary(params[0].replace(";", "").replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
				break;
			case "ANDLW":
				hexCode = "111001";
				bin = this.hexToBinary(params[0].replace(";", "").replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
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
				
				System.out.println("Binary: "+bin);
				if(bin.length() < 11) 
				{
					for(int i = bin.length(); i < 11; i++) 
					{
						bin = "0"+bin;
					}
				}
				System.out.println("Binary: "+bin);
				hexCode = hexCode + bin;
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
				
				System.out.println("Binary: "+bin);
				if(bin.length() < 11) 
				{
					for(int i = bin.length(); i < 11; i++) 
					{
						bin = "0"+bin;
					}
				}
				System.out.println("Binary: "+bin);
				hexCode = hexCode + bin;
				break;
			case "IORLW":
				hexCode = "111000";
				bin = this.hexToBinary(params[0].replace(";", "").replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;
				break;
			case "MOVLW":
				hexCode = "110000";
				bin = this.hexToBinary(params[0].replace(";", "").replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;				
				break;
			case "RETFIE":
				hexCode = "00000000001001";
				break;
			case "RETLW":
				hexCode = "110100";
				bin = this.hexToBinary(params[0].replace(";", "").replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;	
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
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;	
				break;
			case "XORLW":
				hexCode = "111010";
				bin = this.hexToBinary(params[0].replace(";", "").replaceAll("0x", ""));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				if(bin.length() < 7) 
				{
					for(int i = bin.length(); i < 7; i++) 
					{
						bin = "0"+bin;
					}
				}
				hexCode = hexCode + bin;	
				break;
			default:
				System.out.println("Error in Line "+line+" while assembling to Bin-Code");
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
	    int i = Integer.parseInt(hex, 16);
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
			// execute ANDWF
			this.andwf(command.substring(6, 7),command.substring(7));
		}else if(command.substring(0, 7).equals("0000011")) 
		{
			// execute CLRF
		}else if(command.substring(0, 7).equals("0000010")) 
		{
			// execute CLRW
		}else if(command.substring(0, 6).equals("001001")) 
		{
			// execute COMF
		}else if(command.substring(0, 6).equals("000011")) 
		{
			// execute DECF
		}else if(command.substring(0, 6).equals("001011")) 
		{
			// execute DECFSZ
		}else if(command.substring(0, 6).equals("001010")) 
		{
			// execute INCF
		}else if(command.substring(0, 6).equals("001111")) 
		{
			// execute INCFSZ
		}else if(command.substring(0, 6).equals("000100")) 
		{
			// execute IORWF
		}else if(command.substring(0, 6).equals("001000")) 
		{
			// execute MOVF
		}else if(command.substring(0, 7).equals("0000001")) 
		{
			// execute MOVWF
		}else if(command.substring(0, 7).equals("00000000000000")) 
		{
			// execute NOP
		}else if(command.substring(0, 6).equals("001101")) 
		{
			// execute RLF
		}else if(command.substring(0, 6).equals("001100")) 
		{
			// execute RRF
		}else if(command.substring(0, 6).equals("000010")) 
		{
			// execute SUBWF
		}else if(command.substring(0, 6).equals("001110")) 
		{
			// execute SWAPF
		}else if(command.substring(0, 6).equals("000110")) 
		{
			// execute XORWF
		}else if(command.substring(0, 4).equals("0100")) 
		{
			// execute BCF
		}else if(command.substring(0, 4).equals("0101")) 
		{
			// execute BSF
		}else if(command.substring(0, 4).equals("0110")) 
		{
			// execute BTFSC
		}else if(command.substring(0, 4).equals("0111")) 
		{
			// execute BTFSS
		}else if(command.substring(0, 6).equals("111110")) 
		{
			// execute ADDLW
		}else if(command.substring(0, 6).equals("111001")) 
		{
			// execute ANDLW
		}else if(command.substring(0, 3).equals("100")) 
		{
			// execute CALL
		}else if(command.equals("00000001100100")) 
		{
			// execute CLRWDT
		}else if(command.substring(0, 3).equals("101")) 
		{
			// execute GOTO
		}else if(command.substring(0, 6).equals("111000")) 
		{
			// execute IORLW
		}else if(command.substring(0, 6).equals("110000")) 
		{
			// execute MOVLW
		}else if(command.equals("00000000001001")) 
		{
			// execute RETFIE
		}else if(command.substring(0, 6).equals("110100")) 
		{
			// execute RETLW
		}else if(command.equals("00000000001000")) 
		{
			// execute RETURN
		}else if(command.equals("00000001100011")) 
		{
			// execute SLEEP
		}else if(command.substring(0, 6).equals("111100")) 
		{
			// execute SUBLW
		}else if(command.substring(0, 6).equals("111010")) 
		{
			// execute XORLW
		}else {
			System.out.println("There is no command for the inserted string: "+command);
		}
		
	}
	
	//BYTE-ORIENTED FILE REGISTER OPERATIONS
	private void addwf(String d,String f) 
	{
		
	}
	private void andwf(String d, String f) 
	{
		
	}
	private void clrf() 
	{
		
	}
	private void clrw() 
	{
		
	}
	private void comf() 
	{
		
	}
	private void decf() 
	{
		
	}
	private void decfsz() 
	{
		
	}
	private void incf() 
	{
		
	}
	private void incfsz() 
	{
		
	}
	private void iorwf() 
	{
		
	}
	private void movf() 
	{
		
	}
	private void movwf() 
	{
		
	}
	private void nop() 
	{
		
	}
	private void rlf() 
	{
		
	}
	private void rrf() 
	{
		
	}
	private void subwf() 
	{
		
	}
	private void swapf() 
	{
		
	}
	private void xorwf() 
	{
		
	}
	
	//BIT-ORIENTED FILE REGISTER OPERATIONS
	private void bcf() 
	{
		
	}
	private void bsf() 
	{
		
	}
	private void btfsc() 
	{
		
	}
	private void btfss() 
	{
		
	}
	
	//LITERAL AND CONTROL OPERATIONS
	private void addlw() 
	{
		
	}
	private void andlw() 
	{
		
	}
	private void call() 
	{
		
	}
	private void clrwdt() 
	{
		
	}
	// Attention the function goto is a basic java function
	private void _goto() 
	{
		
	}
	private void iorlw() 
	{
		
	}
	private void movlw() 
	{
		
	}
	private void retfie() 
	{
		
	}
	private void retlw() 
	{
		
	}
	// Attention the function return is a basic java function
	private void _return() 
	{
		
	}
	private void sleep() 
	{
		
	}
	private void sublw() 
	{
		
	}
	private void xorlw() 
	{
		
	}
}
