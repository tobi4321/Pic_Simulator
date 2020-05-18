import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class EEProm {
	
	private Controller ctr;
	
	private String[] data = new String[64];
	
	private static int state = 0;
	
	public EEProm(Controller pCtr) 
	{
		ctr = pCtr; 
		try {
			loadFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	protected void checkStates(int line) 
	{
		if(state == 0 && line == 0x3055) 
		{
			state = 1;
		}else if(state == 1 && line == 0x0089) 
		{
			state = 2;
		}else if(state == 2 && line == 0x30AA) 
		{
			state = 3;
		}else if(state == 3 && line == 0x0089) 
		{
			state = 4;
		}else if(state == 4 && line == 0x1488) 
		{

			if(ctr.getMemory().get_MemoryDIRECT(0x88, 2) == 1) 
			{
				// jetzt wird geschrieben
				ctr.setWriteActive(true);
				this.setData(ctr.getMemory().get_MemoryDIRECT(0x08), ctr.getMemory().get_MemoryDIRECT(0x09));
			}
		}else 
		{
			state = 0;
		}
	}
	
	
	protected void setData(int value,int adress) 
	{
		data[adress] = Integer.toHexString(value);
		this.writeFile();
	}
	protected int getData(int adress) 
	{
		System.out.println("getEEPROMData: adress:"+adress+" data:"+data[adress]);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	}
	private void writeFile() 
	{
		PrintWriter pw;
		String out = "";
		try {
			pw = new PrintWriter("src/eeprom.txt");
			for(int i=0; i< 0x3f; i++) 
			{
				out = out + data[i] + '\n';
			}
			pw.write(out);
			pw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
