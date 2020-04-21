/// class MnemonicParser
/**
*  
*  The Parser to pars mnemonic Code to Hex
* 
* **/
public class MnemonicParser {
	
	Controller ctr;
	
	public MnemonicParser(Controller pCtr) 
	{
		ctr = pCtr;
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
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "ANDWF":
				hexCode = "000101";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "CLRF":
				hexCode = "000001";
				p = params[0].split(";");
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "CLRW":
				hexCode = "00000100000000";
				break;
			case "COMF":
				hexCode = "001001";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "DECF":
				hexCode = "000011";
				//hexCode = hexCode + param2[0].replace(";", "");
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "DECFSZ":
				hexCode = "001011";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "INCF":
				hexCode = "001010";
				//hexCode = hexCode + param2[0].replace(";", "");
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "INCFSZ":
				hexCode = "001111";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "IORWF":
				hexCode = "000100";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "MOVF":
				hexCode = "001000";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "MOVWF":
				hexCode = "0000001";
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "NOP":
				hexCode = "00000000000000";
				break;
			case "RLF":
				hexCode = "001101";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "RRF":
				hexCode = "001100";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "SUBWF":
				hexCode = "000010";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "SWAPF":
				hexCode = "001110";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "XORWF":
				hexCode = "000110";
				hexCode = hexCode + param2[0].replace(";", "");
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "BCF":
				hexCode = "0100";
				b = Integer.toBinaryString(Integer.parseInt(ctr.getEQUValue(param2[0].replace(";", ""))));
				for(int i = b.length(); i < 3;i++) 
				{
					b = "0" + b;
				}
				hexCode = hexCode + b;
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "BSF":
				hexCode = "0101";
				b = Integer.toBinaryString(Integer.parseInt(ctr.getEQUValue(param2[0].replace(";", ""))));
				for(int i = b.length(); i < 3;i++) 
				{
					b = "0" + b;
				}
				hexCode = hexCode + b;
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0]));
				break;
			case "BTFSC":
				hexCode = "0110";
				b = Integer.toBinaryString(Integer.parseInt(ctr.getEQUValue(param2[0].replace(";", "").replaceAll(" ", ""))));
				for(int i = b.length(); i < 3;i++) 
				{
					b = "0" + b;
				}
				hexCode = hexCode + b;
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0].replaceAll(" ", "")));
				//System.out.println("hexCode: "+hexCode+" bin: "+bin+" d:"+param2[0]);
				break;
			case "BTFSS":
				hexCode = "0111";
				b = Integer.toBinaryString(Integer.parseInt(ctr.getEQUValue(param2[0].replace(";", "").replaceAll(" ", ""))));
				for(int i = b.length(); i < 3;i++) 
				{
					b = "0" + b;
				}
				hexCode = hexCode + b;
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0].replaceAll(" ", "")));
				break;
			case "ADDLW":
				// Bit 7 ist X wird aber als 0 gewertet
				hexCode = "111110";
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0].replace(";", "")));
				break;
			case "ANDLW":
				hexCode = "111001";
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0].replace(";", "")));
				break;
			case "CALL":
				hexCode = "100";
				for(int i = 0; i< ctr.getJumpersCount(); i++) 
				{
					String[] j = ctr.jumpers[i].split(":");
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
				for(int i = 0; i< ctr.getJumpersCount(); i++) 
				{
					String[] j = ctr.jumpers[i].split(":");
					p = params[0].split(";");
					if(p[0].equals(j[0])) 
					{
						bin = Integer.toBinaryString(Integer.parseInt(j[1]));
					}
				}
				break;
			case "IORLW":
				hexCode = "111000";
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0].replace(";", "")));
				break;
			case "MOVLW":
				hexCode = "110000";
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0].replace(";", "")));			
				break;
			case "RETFIE":
				hexCode = "00000000001001";
				break;
			case "RETLW":
				hexCode = "110100";
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0].replace(";", "")));
				break;
			case "RETURN":
				hexCode = "00000000001000";
				break;
			case "SLEEP":
				hexCode = "00000001100011";
				break;
			case "SUBLW":
				hexCode = "111100";
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0].replace(";", "")));	
				break;
			case "XORLW":
				hexCode = "111010";
				bin = this.setBinaryForToHex(ctr.getEQUValue(params[0].replace(";", "")));	
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
	
	//This method contains a the first parameter from Mnemonic Command and exchange the hex or equ value
	private String setBinaryForToHex(String in) 
	{
		
		if(in.contains("0x")) 
		{
			return this.hexToBinary(in.replaceAll("0x", ""));					
		}else if(in.contains("h")){
			return this.hexToBinary(in.replaceAll("h", ""));
		}else if(in.contains("H")) {
			return this.hexToBinary(in.replaceAll("H", ""));
		}else {
			return this.hexToBinary(in);
		}
	}
	
	// Converter from hex to bin string
	public String hexToBinary(String hex) {
	    if(hex.equals("    ")) 
	    {
	    	return "0";
	    }else 
	    {
			int i = Integer.parseInt(hex.replaceAll("\r", "").replaceAll(";", ""), 16);
		    String bin = Integer.toBinaryString(i);
		    return bin;
	    }

	}
}
