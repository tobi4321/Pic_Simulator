import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class EEProm {
	
	private Controller ctr;
	
	private String[] data = new String[64];
	
	private static int state = 0;
	
	private double writeStartTime;
	
	public EEProm(Controller pCtr) 
	{
		ctr = pCtr; 
		try {
			loadFromFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	protected void checkStates(int eeprom2) 
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
	
	
	protected void setData(int value,int adress) 
	{
		data[adress] = Integer.toHexString(value);
		this.writeFile();
	}
	protected int getData(int adress) 
	{
		return Integer.parseInt(data[adress], 16);
	}
	
	private void loadFromFile() throws IOException 
	{
		FileReader fr;
		BufferedReader br;
		try {
			fr = new FileReader("src/eeprom.txt");
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
	private void writeFile() 
	{
		PrintWriter pw;
		String out = "";
		try {
			pw = new PrintWriter("src/eeprom.txt");
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
	 * @return the writeStartTime
	 */
	public double getWriteStartTime() {
		return this.writeStartTime;
	}
}
