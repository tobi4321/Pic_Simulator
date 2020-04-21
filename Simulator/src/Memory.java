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
	/// default is 0 to indicate a reset
	protected int programmcounter = 0;
	
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

	
	public void run() {
		while(true) 
		{
			// for memory view
			for(int i = 0; i < 256; i++) 
	    	{
	    		ctr.updateMemoryTable(this.tohexValue(dataMemory[i]), i/8, i%8);
	    	}
			// for special register table	
			
			// PCL 
			// ctr.programmCounter needs to be saved in a cyclic way
			ctr.updateSpecialRegTable(this.tohexValue(dataMemory[2]), 2, 1);
			ctr.updateSpecialRegTable(Integer.toBinaryString(Integer.parseInt(this.tohexValue(dataMemory[2]), 16)), 2, 2);
			
			// Status Register
			ctr.updateSpecialRegTable(this.tohexValue(dataMemory[3]), 5, 1);
			ctr.updateSpecialRegTable(Integer.toBinaryString(Integer.parseInt(this.tohexValue(dataMemory[3]), 16)), 5, 2);
			
			// FSR Register
			ctr.updateSpecialRegTable(this.tohexValue(dataMemory[4]), 1, 1);
			ctr.updateSpecialRegTable(Integer.toBinaryString(Integer.parseInt(this.tohexValue(dataMemory[4]), 16)), 1, 2);
			
			// PC LATH
			ctr.updateSpecialRegTable(this.tohexValue(dataMemory[10]), 3, 1);
			ctr.updateSpecialRegTable(Integer.toBinaryString(Integer.parseInt(this.tohexValue(dataMemory[10]), 16)), 3, 2);
			
			// PC
			// pc lath must be concatenated in front
			ctr.updateSpecialRegTable(Integer.toHexString(ctr.programmCounter), 4, 1);
			ctr.updateSpecialRegTable(Integer.toBinaryString(ctr.programmCounter), 4, 2);
			
			
			// W Register
			ctr.updateSpecialRegTable(Integer.toHexString(this.get_WREGISTER()), 0, 1);
			ctr.updateSpecialRegTable(Integer.toBinaryString(this.get_WREGISTER()), 0, 2);
			
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
		set_SRAM(0,bit,value);
		// eventuell Bereich aus Bank 1 hier rein

	}
	
	protected void set_TMR0(int bit, int value) 
	{
		set_SRAM(1,bit,value);
	}
	
	protected void set_PCL(int bit, int value) 
	{
		set_SRAM(2,bit,value);
	}
	
	protected void set_STATUS(int bit, int value) 
	{
		set_SRAM(3,bit,value);
	}
	
	protected void set_FSR(int bit, int value) 
	{
		set_SRAM(4,bit,value);
	}
	
	protected void set_PORTA(int bit, int value) 
	{
		set_SRAM(5,bit,value);
	}
	
	protected void set_PORTB(int bit, int value) 
	{
		set_SRAM(6,bit,value);
	}
	
	protected void set_EEDATA(int bit, int value) 
	{
		set_SRAM(8,bit,value);
	}
	
	protected void set_EEADR(int bit, int value) 
	{
		set_SRAM(9,bit,value);
	}
	
	protected void set_PCLATH(int bit, int value) 
	{
		set_SRAM(10,bit,value);
	}
	
	protected void set_INTCON(int bit, int value) 
	{
		set_SRAM(11,bit,value);
	}
	
	
	//Here are the set Methods for BANK 1 Memory
	
	protected void set_OPTION_REG(int bit, int value) 
	{
		set_SRAM(129,bit,value);
	}
	
	protected void set_TRISA(int bit, int value) 
	{
		set_SRAM(133,bit,value);
	}
	
	protected void set_TRISB(int bit, int value) 
	{
		set_SRAM(134,bit,value);
	}
	
	protected void set_EECON1(int bit, int value) 
	{
		set_SRAM(136,bit,value);
	}
	
	protected void set_EECON2(int bit, int value) 
	{
		set_SRAM(137,bit,value);
	}
	
	//set General Purpose registers SRAM
	protected void set_SRAM(int fileaddress, int bit, int value) 
	{
		switch(fileaddress) 
		{
		// INDF
		case 0:
			dataMemory[0][bit] = value;
			dataMemory[128][bit] = value;
			break;
		// PCL
		case 2:
			dataMemory[2][bit] = value;
			dataMemory[130][bit] = value;
			break;
		// Status
		case 3:
			dataMemory[3][bit] = value;
			dataMemory[131][bit] = value;
			break;
		// FSR
		case 4:
			dataMemory[4][bit] = value;
			dataMemory[132][bit] = value;
			break;
		// PCLATH
		case 10:
			dataMemory[10][bit] = value;
			dataMemory[138][bit] = value;
			break;
		// INTCON
		case 11:
			dataMemory[11][bit] = value;
			dataMemory[139][bit] = value;
			break;
		default:
			if(dataMemory[3][5] == 0) 
			{
				dataMemory[fileaddress][bit] = value;
			}else if(dataMemory[3][5] == 1) 
			{
				dataMemory[fileaddress+128][bit] = value;
			}
			break;	
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
			this.set_SRAM(fileaddress, 7-i, Integer.parseInt(""+c.charAt(i)));
		}
	}
	
	protected void set_CARRYFLAG(int c) 
	{
		set_SRAM(3,0,c);
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
		String bin = Integer.toBinaryString(counter);
		while(bin.length() < 13) 
		{
			bin = "0" + bin;
		}
		for(int i = 0; i< bin.length(); i++) 
		{
			if(i < 5) 
			{
				// setting pc lath
				this.set_SRAM(10, 7-i,Integer.parseInt(""+bin.charAt(i)));
			}else 
			{
				// setting pc low
				this.set_SRAM(2, 7-i,Integer.parseInt(""+bin.charAt(i)));
			}

		}

	}
	
	protected int get_PROGRAMMCOUNTER() 
	{
		return programmcounter;
	}
	
	// Getter and Setter for w-register
	protected void set_WREGISTER(int bit, int value) 
	{
		w_register[bit] = value;
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
	}
	
	
	/**
	 * * function to get ther specific bit of w register
	 * @param bit bit index
	 * @return value of the w register specific bit
	 */
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
