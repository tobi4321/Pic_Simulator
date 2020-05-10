/// class MnemonicParser
/**
 *  This class is used to parse mnemonic code into binary assembler code
 * 
 * 
 * */
public class MnemonicParser {
	
	
	/// obejct of main Controller
	Controller ctr;
	
	
	/// constructor 
	/**
	 * The constructor is used to set the parameter given by Controller class with creation to the local Controller object
	 *  @param pCtr temporary object of Controller
	 * 
	 * */
	public MnemonicParser(Controller pCtr) 
	{
		ctr = pCtr;
	}
	

	/**
	 *  function to parse a line of mnemonic assembler code into binary code
	 * @param c the line to parse
	 * @param line to display the line where an error occured
	 * @return the binary string of c
	 * */
	public String fromMnemToHex(String c,int line)
	{	
		String proceed = "";
		if(c.contains(";") == true )
		{
			proceed = c.substring(0,c.indexOf(";"));
		}else 
		{
			proceed = c;
		}
		c.replaceAll("\r", "");
		
		
		String wordList[] = new String[4];
		wordList[0] = "";
		wordList[1] = "";
		wordList[2] = "";
		wordList[3] = "";
		int wordListCounter = 0;
		
		for(int i = 0; i< proceed.length(); i++) 
		{
			if(proceed.charAt(i) == ' ') 
			{
				if(wordList[wordListCounter].length() == 0) 
				{
					
				}else 
				{
					wordListCounter++;
				}
			}else if(proceed.charAt(i) == ',')
			{
				if(wordList[wordListCounter].length() == 0) 
				{
					
				}else 
				{
					wordListCounter++;
				}
			}else
			{
				wordList[wordListCounter] = wordList[wordListCounter] + proceed.charAt(i); 
			}
		}
		System.out.println("Word[0]: "+wordList[0]);
		System.out.println("Word[1]: "+wordList[1]);
		System.out.println("Word[2]: "+wordList[2]);
		System.out.println("Word[3]: "+wordList[3]);
		

		String hexCode = "";
		String b;
		String bin ="";

			switch(wordList[0]) 
			{
				case "ADDWF":
					hexCode = "000111";
					hexCode = hexCode + wordList[2];
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "ANDWF":
					hexCode = "000101";
					hexCode = hexCode + wordList[2];
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "CLRF":
					hexCode = "000001";
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "CLRW":
					hexCode = "00000100000000";
					break;
				case "COMF":
					hexCode = "001001";
					hexCode = hexCode + wordList[2];
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "DECF":
					hexCode = "000011";
					hexCode = hexCode + wordList[2];
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "DECFSZ":
					hexCode = "001011";
					hexCode = hexCode + wordList[2];
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "INCF":
					hexCode = "001010";
					hexCode = hexCode + wordList[2];
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "INCFSZ":
					hexCode = "001111";
					hexCode = hexCode + wordList[2];
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "IORWF":
					hexCode = "000100";
					hexCode = hexCode + wordList[2];
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "MOVF":
					hexCode = "001000";
					hexCode = hexCode + wordList[2];
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "MOVWF":
					hexCode = "0000001";
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "NOP":
					hexCode = "00000000000000";
					break;
				case "RLF":
					hexCode = "001101";
					hexCode = hexCode + wordList[2];
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "RRF":
					hexCode = "001100";
					hexCode = hexCode + wordList[2];
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "SUBWF":
					hexCode = "000010";
					hexCode = hexCode + wordList[2];
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "SWAPF":
					hexCode = "001110";
					hexCode = hexCode + wordList[2];
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "XORWF":
					hexCode = "000110";
					hexCode = hexCode + wordList[2];
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "BCF":
					hexCode = "0100";
					b = Integer.toBinaryString(Integer.parseInt(ctr.getEQUValue(wordList[2])));
					for(int i = b.length(); i < 3;i++) 
					{
						b = "0" + b;
					}
					hexCode = hexCode + b;
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "BSF":
					hexCode = "0101";
					b = Integer.toBinaryString(Integer.parseInt(ctr.getEQUValue(wordList[2])));
					for(int i = b.length(); i < 3;i++) 
					{
						b = "0" + b;
					}
					hexCode = hexCode + b;
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "BTFSC":
					hexCode = "0110";
					b = Integer.toBinaryString(Integer.parseInt(ctr.getEQUValue(wordList[2])));
					for(int i = b.length(); i < 3;i++) 
					{
						b = "0" + b;
					}
					hexCode = hexCode + b;
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "BTFSS":
					hexCode = "0111";
					b = Integer.toBinaryString(Integer.parseInt(ctr.getEQUValue(wordList[2])));
					for(int i = b.length(); i < 3;i++) 
					{
						b = "0" + b;
					}
					hexCode = hexCode + b;
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "ADDLW":
					// Bit 7 ist X wird aber als 0 gewertet
					hexCode = "111110";
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "ANDLW":
					hexCode = "111001";
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "CALL":
					hexCode = "100";
					for(int i = 0; i< ctr.getJumpersCount(); i++) 
					{
						String[] j = ctr.getJumpers()[i].split(":");

						if(wordList[1].equals(j[0])) 
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
						String[] j = ctr.getJumpers()[i].split(":");
						if(wordList[1].equals(j[0])) 
						{
							bin = Integer.toBinaryString(Integer.parseInt(j[1]));
						}
					}
					break;
				case "IORLW":
					hexCode = "111000";
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "MOVLW":
					hexCode = "110000";
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));			
					break;
				case "RETFIE":
					hexCode = "00000000001001";
					break;
				case "RETLW":
					hexCode = "110100";
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));
					break;
				case "RETURN":
					hexCode = "00000000001000";
					break;
				case "SLEEP":
					hexCode = "00000001100011";
					break;
				case "SUBLW":
					hexCode = "111100";
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));	
					break;
				case "XORLW":
					hexCode = "111010";
					bin = this.setBinaryForToHex(ctr.getEQUValue(wordList[1]));	
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
		
		return hexCode;
	}
	
	
	/**
	 * *This function converts a hex string into a binary string with the underlaying function hexToBinary
	 * *deletion of 0x or h or H is needed for convertion
	 * @param in the string to be converted
	 * @return the binary string result of converted hex string 
	 */
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
	
	/**
	 * * Converter from hex to bin string
	 * @param hex
	 * @return
	 */
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
