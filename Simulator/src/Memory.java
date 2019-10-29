
public class Memory {
	protected static int[][] memory = new int[255][8];
	
	// set Methods for BANK 0 
	
	protected void set_INDF(int bit, int value) 
	{
		memory[0][bit] = value;
		// eventuell Bereich aus Bank 1 hier rein
	}
	protected void set_TMR0(int bit, int value) 
	{
		memory[1][bit] = value;
	}
	protected void set_PCL(int bit, int value) 
	{
		memory[2][bit] = value;
	}
	protected void set_STATUS(int bit, int value) 
	{
		memory[3][bit] = value;
	}
	protected void set_FSR(int bit, int value) 
	{
		memory[4][bit] = value;
	}
	protected void set_PORTA(int bit, int value) 
	{
		memory[5][bit] = value;
	}
	protected void set_PORTB(int bit, int value) 
	{
		memory[6][bit] = value;
	}
	protected void set_EEDATA(int bit, int value) 
	{
		memory[8][bit] = value;
	}
	protected void set_EEADR(int bit, int value) 
	{
		memory[9][bit] = value;
	}
	protected void set_PCLATH(int bit, int value) 
	{
		memory[10][bit] = value;
	}
	protected void set_INTCON(int bit, int value) 
	{
		memory[11][bit] = value;
	}
	
	
	//Here are the set Methods for BANK 1 Memory
	
	protected void set_OPTION_REG(int bit, int value) 
	{
		memory[129][bit] = value;
	}
	protected void set_TRISA(int bit, int value) 
	{
		memory[133][bit] = value;
	}
	protected void set_TRISB(int bit, int value) 
	{
		memory[134][bit] = value;
	}
	protected void set_EECON1(int bit, int value) 
	{
		memory[136][bit] = value;
	}
	protected void set_EECON2(int bit, int value) 
	{
		memory[137][bit] = value;
	}	
	//set General Purpose registers SRAM
	protected void set_SRAM(int fileaddress, int bit, int value) 
	{
		memory[fileaddress][bit] = value;
	}
	
	protected int get_Memory(int fileaddress, int bit) 
	{
		return memory[fileaddress][bit];
	}
	
}
