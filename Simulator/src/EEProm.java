import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class EEProm {
	private String[] data = new String[64];
	
	public EEProm() 
	{
		try {
			loadFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
