import java.util.Stack;
/// class Memory
/**
 *  This class is the basic implementation of the controller memory
 *  There a various variables for w_register, carry flag or the stack
 * 
 * **/
public class Memory {
	
	private Controller ctr;
	
	// this memory will store the data memory
	// 00 to 7F is the first bank
	// 80 to FF is the second bank
	protected int[][] dataMemory = new int[256][8];
	
	// this is the storage for the program code
	// 0000 is the reset and 0004 is the interrupt value
	protected int[] programMemory = new int[1024];
	
	// counter on which line the processor is
	// default es 0 to indicate a reset
	protected int programmcounter;
	
	// w_register storage for operations
	protected int[] w_register = new int[8];
	
	// flag to indicate a overflow
	protected int carry_flag;
	
	//
	protected int zero_flag;
	
	/// the stack is used to store the pushed adresses by a call command
	protected Stack<Integer> stack = new Stack<Integer>();
	
	// set Methods for BANK 0 
	
	public Memory(Controller pCtr) {
		this.ctr = pCtr;
		
		// initialization of memory
		// correct start values in memory
		// needed ...
	}
	
	protected void set_INDF(int bit, int value) 
	{
		dataMemory[0][bit] = value;
		// eventuell Bereich aus Bank 1 hier rein
		ctr.updateMemoryTable(this.tohexValue(dataMemory[0]), 0, 0);
	}
	
	protected void set_TMR0(int bit, int value) 
	{
		dataMemory[1][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[1]), 0, 1);
	}
	
	protected void set_PCL(int bit, int value) 
	{
		dataMemory[2][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[2]), 0, 2);
	}
	
	protected void set_STATUS(int bit, int value) 
	{
		dataMemory[3][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[3]), 0, 3);
		ctr.updateWRegTable(this.tohexValue(dataMemory[3]), 1, 5);
		ctr.updateWRegTable(Integer.toBinaryString(value), 2, 5);
	}
	
	protected void set_FSR(int bit, int value) 
	{
		dataMemory[4][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[4]), 0, 4);
		ctr.updateWRegTable(this.tohexValue(dataMemory[4]), 1, 1);
		ctr.updateWRegTable(Integer.toBinaryString(value), 2, 1);
	}
	
	protected void set_PORTA(int bit, int value) 
	{
		dataMemory[5][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[5]), 0, 5);
	}
	
	protected void set_PORTB(int bit, int value) 
	{
		dataMemory[6][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[6]), 0, 6);
	}
	
	protected void set_EEDATA(int bit, int value) 
	{
		dataMemory[8][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[8]), 1, 0);
	}
	
	protected void set_EEADR(int bit, int value) 
	{
		dataMemory[9][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[9]), 1, 1);
	}
	
	protected void set_PCLATH(int bit, int value) 
	{
		dataMemory[10][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[10]), 1, 2);
	}
	
	protected void set_INTCON(int bit, int value) 
	{
		dataMemory[11][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[11]), 1, 3);
	}
	
	
	//Here are the set Methods for BANK 1 Memory
	
	protected void set_OPTION_REG(int bit, int value) 
	{
		dataMemory[129][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[129]), 8, 1);
	}
	
	protected void set_TRISA(int bit, int value) 
	{
		dataMemory[133][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[133]), 8, 5);
	}
	
	protected void set_TRISB(int bit, int value) 
	{
		dataMemory[134][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[134]), 8, 6);
	}
	
	protected void set_EECON1(int bit, int value) 
	{
		dataMemory[136][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[136]), 9, 0);
	}
	
	protected void set_EECON2(int bit, int value) 
	{
		dataMemory[137][bit] = value;
		ctr.updateMemoryTable(this.tohexValue(dataMemory[137]), 9, 1);
	}
	
	//set General Purpose registers SRAM
	protected void set_SRAM(int fileaddress, int bit, int value) 
	{
		if(dataMemory[3][5] == 0) 
		{
			dataMemory[fileaddress][bit] = value;
			ctr.updateMemoryTable(this.tohexValue(dataMemory[fileaddress]), fileaddress/8, fileaddress%8);
		}else if(dataMemory[3][5] == 1) 
		{
			dataMemory[fileaddress+128][bit] = value;
			ctr.updateMemoryTable(this.tohexValue(dataMemory[fileaddress]), fileaddress/8, fileaddress%8);
		}

	}
	
	protected void set_SRAM(int fileaddress, int value) 
	{
		String c = Integer.toBinaryString(value);
		for(int l = c.length(); l < 8; l++) 
		{
			c = "0" + c;
		}
		for(int i = 7; i >= 0; i-- ) 
		{
			if(dataMemory[3][5] == 0) 
			{
				dataMemory[fileaddress][i] = Integer.parseInt(""+c.charAt(i));
				ctr.updateMemoryTable(this.tohexValue(dataMemory[fileaddress]), fileaddress/8, fileaddress%8);
			}else if(dataMemory[3][5] == 1) 
			{
				dataMemory[fileaddress+128][i] = Integer.parseInt(""+c.charAt(i));
				ctr.updateMemoryTable(this.tohexValue(dataMemory[fileaddress]), fileaddress/8, fileaddress%8);
			}
			
		}

	}
	
	protected void set_CARRYFLAG(int c) 
	{
		this.carry_flag = c;
	}
	
	protected int get_CARRYFLAG() 
	{
		return this.carry_flag;
	}
	
	protected int get_Memory(int fileaddress, int bit) 
	{
		return dataMemory[fileaddress][bit];
	}
	
	protected int get_Memory(int fileaddress) 
	{
		String c = "";
		for(int i = 0; i < 8; i++) 
		{
			if(dataMemory[3][5] == 0) 
			{
				c = c+ dataMemory[fileaddress][i];
			}else if(dataMemory[3][5] == 1) 
			{
				c = c+ dataMemory[fileaddress+128][i];
			}
			
		}
		return Integer.parseInt(c,2);
	}
	
	// Getter and Setter for programmcounter
	protected void set_PROGRAMMCOUNTER(int counter) 
	{
		programmcounter = counter;
	}
	
	protected int get_PROGRAMMCOUNTER() 
	{
		return programmcounter;
	}
	// Getter and Setter for w-register
	protected void set_WREGISTER(int bit, int value) 
	{
		w_register[bit] = value;
		ctr.updateWRegTable(this.tohexValue(w_register), 0, 1);
		ctr.updateWRegTable(this.tohexValue(w_register), 0, 2);
	}
	
	protected void set_WREGISTER(int value) 
	{
		String c = Integer.toBinaryString(value);
		for(int l = c.length(); l < 8; l++) 
		{
			c = "0" + c;
		}
		for(int i = 7; i>=0; i--) 
		{
			w_register[i] = Integer.parseInt(""+c.charAt(i));
		}
		ctr.updateWRegTable(Integer.toHexString(this.get_WREGISTER()), 0, 1);
		ctr.updateWRegTable(Integer.toBinaryString(this.get_WREGISTER()), 0, 2);
	}
	
	protected int get_WREGISTER(int bit) 
	{
		return w_register[bit];
	}
	
	protected int get_WREGISTER() 
	{
		String c = "";
		for(int i = 0; i< 8; i++) 
		{
			c = c + w_register[i] ;
		}
		return Integer.parseInt(c,2);
	}

	
	private String tohexValue(int[] in) 
	{
		String out = "";
		for(int i = 0; i < in.length; i++) 
		{
			out = out + in[i];
		}
		int decimal = Integer.parseInt(out, 2);
		String hexout = Integer.toString(decimal, 16);
		return hexout;
	}
	
	protected void pushToStack(int adr) 
	{
		this.stack.push(adr);
	}
	
	protected int popFromStack() 
	{
		return this.stack.pop();
	}

	public void clearProgMem() {
		// TODO Auto-generated method stub
		for(int i = 0; i< this.programMemory.length; i++) 
		{
			this.programMemory[i] = 0;
		}
	}
}
