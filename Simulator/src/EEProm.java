import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
/// class EEProm
/**
*  This class is used to check if a valid EEPROM Write access sequence is executed.
*  Furthermore this class reads and writes to the eeprom.txt file which represents the EEPROM memory.
* **/
public class EEProm {
	/// The object of the main controller.
	private Controller ctr;
	/// The eeprom data loaded from the eeprom.txt file.
	private String[] data = new String[64];
	/// The state of the write sequence state machine.
	private static int state = 0;
	/// A timestamp of the write.
	private double writeStartTime;
	
	private String filePath;
	
	/**
	 * The constructor. Setting the Controller object and loading the eeprom memory from the txt file.
	 * @param pCtr The Controller object to set.
	 */
	public EEProm(Controller pCtr) 
	{
		filePath = System.getProperty("user.dir") + "/eeprom.txt";
		try {
			filePath = URLDecoder.decode(filePath, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		File f = new File(filePath);
		if(!f.exists()) {
			try {
				f.createNewFile();
				for (int i = 0; i < 64; i++) {
					this.data[i] = "00";
				}
				this.writeFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ctr = pCtr; 
		try {
			loadFromFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to check the state of the state machine.
	 * @param eeprom2 The value of the eeprom data register.
	 * @throws UnsupportedEncodingException 
	 */
	protected void checkStates(int eeprom2) throws UnsupportedEncodingException 
	{
		if(state == 0 && eeprom2  == 0x55) 
		{
			state = 1;
		}else 
		{
			if(state == 1 && eeprom2 == 0x55) 
			{
				// everything ok
			}else 
			{
				if(state == 1 && eeprom2 == 0xAA) 
				{
					state = 2;
				}else 
				{
					if(state == 2 && eeprom2 == 0xAA) 
					{
						//everything ok
						if(state == 2 && ctr.getMemory().get_MemoryDIRECT(0x88, 1) == 1) 
						{
							// check if eecon1, write enable bit is set
							if(ctr.getMemory().get_MemoryDIRECT(0x88, 2) == 1) 
							{
								// jetzt wird geschrieben
								ctr.setWriteActive(true);
								this.setData(ctr.getMemory().get_MemoryDIRECT(0x08), ctr.getMemory().get_MemoryDIRECT(0x09));
								this.writeStartTime = ctr.getOperationalTime();
								state = 0;
								System.out.println("EEPROM Write started");
							}else 
							{
								// write error
								state = 0;
							}
						}
					}else 
					{
						state = 0;
					}
				}
			}
		}
	}
	
	/**
	 * Method to set one value in the data array and therefore in the eeprom memory.
	 * @param value The value to set.
	 * @param adress The address to set.
	 * @throws UnsupportedEncodingException 
	 */
	protected void setData(int value,int address) throws UnsupportedEncodingException 
	{
		data[address] = Integer.toHexString(value);
		this.writeFile();
	}
	
	/**
	 * Method to get one value from the data array (the eeprom memory).
	 * @param address The Address to read the value from.
	 * @return The value read.
	 */
	protected int getData(int address) 
	{
		return Integer.parseInt(data[address], 16);
	}
	
	/**
	 * Method to load the eeprom.txt file into the data array.
	 */
	private void loadFromFile() throws IOException 
	{
		FileReader fr;
		BufferedReader br;
		try {
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);
			
			for(int i=0; i< 0x40; i++) 
			{
				data[i] = br.readLine();
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to write the data array into the eeprom.txt file.
	 * @throws UnsupportedEncodingException 
	 */
	private void writeFile() throws UnsupportedEncodingException 
	{
		PrintWriter pw;
		String out = "";
		try {
			pw = new PrintWriter(filePath);
			for(int i=0; i< 0x40; i++) 
			{
				out = out + data[i] + '\n';
			}
			pw.write(out);
			pw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter to get the writeStartTime variable.
	 * @return The writeStartTime.
	 */
	public double getWriteStartTime() {
		return this.writeStartTime;
	}
}
