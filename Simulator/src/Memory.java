import java.util.Stack;
/// class Memory
/**
 *  This class is the basic implementation of the controller memory
 *  There a various variables for w_register, carry flag or the stack
 * 
 * **/
public class Memory extends Thread{
	
	private Controller ctr;
	
	/// this memory will store the data memory
	/// 00 to 7F is the first bank
	/// 80 to FF is the second bank
	protected int[][] dataMemory = new int[256][8];
	
	/// this is the storage for the program code
	/// 0000 is the reset and 0004 is the interrupt value
	protected int[] programMemory = new int[1024];
	
	
	/// counter on which line the processor is
	/// default es 0 to indicate a reset
	protected int programmcounter;
	
	/// w_register storage for operations
	protected int[] w_register = new int[8];
	
	/// the stack is used to store the pushed adresses by a call command
	protected Stack<Integer> stack = new Stack<Integer>();
	
	// set Methods for BANK 0 
	
	public Memory(Controller pCtr) {
		this.ctr = pCtr;
		
		// initialization of memory
		// correct start values in memory
		// needed ...
		
		// Status Register
		// PD
		this.dataMemory[3][3] = 1;
		this.dataMemory[128+3][3] = 1;
		// TO
		this.dataMemory[3][4] = 1;
		this.dataMemory[128+3][4] = 1;
		
		
		// Option_Reg
		// PS0
		this.dataMemory[129][0] = 1;
		// PS1
		this.dataMemory[129][1] = 1;
		//PS2
		this.dataMemory[129][2] = 1;
		//PSA
		this.dataMemory[129][3] = 1;
		//T0SE
		this.dataMemory[129][4] = 1;
		//T0CS
		this.dataMemory[129][5] = 1;
		//INTEDG
		this.dataMemory[129][6] = 1;
		//RBPU
		this.dataMemory[129][7] = 1;
		
		
		
	}
	
	public void cyclicMemoryUpdate() 
	{
		
	}
	public void run() {
		while(true) 
		{
			for(int i = 0; i < 256; i++) 
	    	{
	    		ctr.updateMemoryTable(this.tohexValue(dataMemory[i]), i/8, i%8);
	    	}
	    	try {
				sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
	
	protected void set_INDF(int bit, int value) 
	{
		dataMemory[0][bit] = value;
		// eventuell Bereich aus Bank 1 hier rein

	}
	
	protected void set_TMR0(int bit, int value) 
	{
		dataMemory[1][bit] = value;
	}
	
	protected void set_PCL(int bit, int value) 
	{
		dataMemory[2][bit] = value;
	}
	
	protected void set_STATUS(int bit, int value) 
	{
		dataMemory[3][bit] = value;
		ctr.updateSpecialRegTable(this.tohexValue(dataMemory[3]), 1, 5);
		ctr.updateSpecialRegTable(Integer.toBinaryString(value), 2, 5);
	}
	
	protected void set_FSR(int bit, int value) 
	{
		dataMemory[4][bit] = value;
		ctr.updateSpecialRegTable(this.tohexValue(dataMemory[4]), 1, 1);
		ctr.updateSpecialRegTable(Integer.toBinaryString(value), 2, 1);
	}
	
	protected void set_PORTA(int bit, int value) 
	{
		dataMemory[5][bit] = value;
	}
	
	protected void set_PORTB(int bit, int value) 
	{
		dataMemory[6][bit] = value;
	}
	
	protected void set_EEDATA(int bit, int value) 
	{
		dataMemory[8][bit] = value;
	}
	
	protected void set_EEADR(int bit, int value) 
	{
		dataMemory[9][bit] = value;
	}
	
	protected void set_PCLATH(int bit, int value) 
	{
		dataMemory[10][bit] = value;
	}
	
	protected void set_INTCON(int bit, int value) 
	{
		dataMemory[11][bit] = value;
	}
	
	
	//Here are the set Methods for BANK 1 Memory
	
	protected void set_OPTION_REG(int bit, int value) 
	{
		dataMemory[129][bit] = value;
	}
	
	protected void set_TRISA(int bit, int value) 
	{
		dataMemory[133][bit] = value;
	}
	
	protected void set_TRISB(int bit, int value) 
	{
		dataMemory[134][bit] = value;
	}
	
	protected void set_EECON1(int bit, int value) 
	{
		dataMemory[136][bit] = value;
	}
	
	protected void set_EECON2(int bit, int value) 
	{
		dataMemory[137][bit] = value;
	}
	
	//set General Purpose registers SRAM
	protected void set_SRAM(int fileaddress, int bit, int value) 
	{
		if(dataMemory[3][5] == 0) 
		{
			dataMemory[fileaddress][bit] = value;
		}else if(dataMemory[3][5] == 1) 
		{
			dataMemory[fileaddress+128][bit] = value;
		}

	}
	
	protected void set_SRAM(int fileaddress, int value) 
	{
		System.out.println("Memory Incoming: "+value);
		String c = Integer.toBinaryString(value);
		for(int l = c.length(); l < 8; l++) 
		{
			c = "0" + c;
		}
		for(int i = 7; i >= 0; i-- ) 
		{
			if(dataMemory[3][5] == 0) 
			{
				dataMemory[fileaddress][7-i] = Integer.parseInt(""+c.charAt(i));
				System.out.println("Added Value "+value+" to Register "+fileaddress);
			}else if(dataMemory[3][5] == 1) 
			{
				dataMemory[fileaddress+128][7-i] = Integer.parseInt(""+c.charAt(i));
				System.out.println("Added Value "+value+" to Register "+(fileaddress+128));
			}
		}
	}
	
	protected void set_CARRYFLAG(int c) 
	{
		this.dataMemory[3][0] = c;
	}
	
	protected int get_CARRYFLAG() 
	{
		return this.dataMemory[3][0];
	}
	
	protected int get_Memory(int fileaddress, int bit) 
	{
		if(dataMemory[3][5] == 0) 
		{
			return dataMemory[fileaddress][bit];
		}else  if(dataMemory[3][5] == 1)
		{
			return dataMemory[fileaddress+128][bit];
		}else 
		{
			return 0;
		}
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
		ctr.updateSpecialRegTable(this.tohexValue(w_register), 0, 1);
		ctr.updateSpecialRegTable(this.tohexValue(w_register), 0, 2);
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
			w_register[7-i] = Integer.parseInt(""+c.charAt(i));
		}
		ctr.updateSpecialRegTable(Integer.toHexString(this.get_WREGISTER()), 0, 1);
		ctr.updateSpecialRegTable(Integer.toBinaryString(this.get_WREGISTER()), 0, 2);
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
			out = in[i] +out;
		}
		int decimal = Integer.parseInt(out, 2);
		String hexout = Integer.toString(decimal, 16);
		return hexout;
	}
	
	/**
	 * * used to push the current programmcounter+1 on the stack
	 * @param adr is the adress to push
	 */
	protected void pushToStack(int adr) 
	{
		this.stack.push(adr);
	}
	
	/**
	 * * used to pop the needed programm counter from stack
	 * @return is the popped adress
	 */
	protected int popFromStack() 
	{
		return this.stack.pop();
	}

	/**
	 * * used to clear the programMemory
	 *   reset value is ff
	 */
	public void clearProgMem() {
		for(int i = 0; i< this.programMemory.length; i++) 
		{
			this.programMemory[i] = 255;
		}
	}
}
