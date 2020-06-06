import java.util.Stack;
/// class Memory
/**
 * This class is the basic implementation of the controller memory
 * There a various variables for w_register, carry flag or the stack.
 * For often used variables and special variables there are special function to call directly. 
 * **/
public class Memory extends Thread{
	/// The object of the main Controller class.
	private Controller ctr;
	/// This memory will store the data memory.
	/// 00 to 7F is the first bank.
	/// 80 to FF is the second bank.
	private int[][] dataMemory = new int[256][8];
	/// This is the storage for the program code.
	/// 0000 is the reset and 0004 is the interrupt value.
	private int[] programMemory = new int[1024];
	/// The counter on which line the processor is.
	/// Default is 0 to indicate a reset.
	protected int programmcounter = 0;
	/// w_register storage for operations.
	protected int[] w_register = new int[8];
	/// The stack is used to store the pushed addresses by a call command.
	protected int[] intStack = new int[8];
	/// The size of the stack.
	private int stackSize = 0;
	/// The temporary store of the port A values.
	private int dataLatchA = 0;
	/// The temporary store of the port B values.
	private int dataLatchB = 0;
	
	/**
	 * The constructor, initializing the memory with the correct values  and setting the Controller object.
	 * @param pCtr The Controller object to set.
	 */
	public Memory(Controller pCtr) {
		this.ctr = pCtr;
		InitMemoryPowerOn();
	}
	
	/**
	 * Method to initialize the correct memory values on power on
	 */
	public void InitMemoryPowerOn() {
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
		
		// TRIS A and B Register to INPUT
		this.set_SRAM(0x85, 255);
		this.set_SRAM(0x86, 255);
		
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
	
	/**
	 * A thread method to cyclic update the memory displayed on the Simulator_Window. 
	 */
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
			ctr.updateSpecialRegTable(Integer.toHexString(this.programmcounter), 4, 1);
			ctr.updateSpecialRegTable(Integer.toBinaryString(this.programmcounter), 4, 2);
			
			
			// W Register
			ctr.updateSpecialRegTable(Integer.toHexString(this.get_WREGISTER()), 0, 1);
			ctr.updateSpecialRegTable(Integer.toBinaryString(this.get_WREGISTER()), 0, 2);
			
			
			// Update the Port A and Port B
			ctr.refreshIO();
			ctr.update7Segment();
			
			for(int i = 0; i < 8; i++) 
			{
				ctr.updateStackPanel("", i);
			}
			for(int i = 0; i < this.stackSize; i++) 
			{
				ctr.updateStackPanel(Integer.toHexString(this.intStack[i]), i);
			}
			
	    	try {
				sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    }
	
	/**
	 * Method to set the INDF.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_INDF(int bit, int value) 
	{
		set_SRAM(0,bit,value);
		// eventuell Bereich aus Bank 1 hier rein
	}
	
	/**
	 * Method to set the TMR0.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_TMR0(int bit, int value) 
	{
		set_SRAM(1,bit,value);
	}
	
	/**
	 * Method to set the PCL.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_PCL(int bit, int value) 
	{
		set_SRAM(2,bit,value);
	}
	
	/**
	 * Method to set the Status registry.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_STATUS(int bit, int value) 
	{
		set_SRAM(3,bit,value);
	}
	
	/**
	 * Method to set the FSR.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_FSR(int bit, int value) 
	{
		set_SRAM(4,bit,value);
	}
	
	/**
	 * Method to set port A.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_PORTA(int bit, int value) 
	{
		set_SRAM(5,bit,value);
	}
	
	/**
	 * Method to set port B.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_PORTB(int bit, int value) 
	{
		set_SRAM(6,bit,value);
	}
	
	/**
	 * Method to set the EEDATA.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_EEDATA(int bit, int value) 
	{
		set_SRAM(8,bit,value);
	}
	
	/**
	 * Method to set the EEADR.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_EEADR(int bit, int value) 
	{
		set_SRAM(9,bit,value);
	}
	
	/**
	 * Method to set PCLATH.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_PCLATH(int bit, int value) 
	{
		set_SRAM(10,bit,value);
	}
	
	/**
	 * Method to set INTCON.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_INTCON(int bit, int value) 
	{
		set_SRAM(11,bit,value);
	}

	/**
	 * Method to set the option registry.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_OPTION_REG(int bit, int value) 
	{
		set_SRAM(129,bit,value);
	}
	
	/**
	 * Method to set tris A.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_TRISA(int bit, int value) 
	{
		set_SRAM(133,bit,value);
	}
	
	/**
	 * Method to set tris B.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_TRISB(int bit, int value) 
	{
		set_SRAM(134,bit,value);
	}
	
	/**
	 * Method to set the EECON 1.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_EECON1(int bit, int value) 
	{
		set_SRAM(136,bit,value);
	}
	
	/**
	 * Method to set the EECON 2.
	 * @param bit The bit to set.
	 * @param value The value to set.
	 */
	protected void set_EECON2(int bit, int value) 
	{
		set_SRAM(137,bit,value);
	}
	
	/**
	 * Method to set the GIE bit.
	 * @param value The value to set.
	 */
	protected void set_GIE(int value)
	{
		set_SRAMDIRECT(0x0B, 7, value);
	}
	
	/**
	 * Method to set the PD bit.
	 * @param value The value to set.
	 */
	protected void set_PD(int value)
	{
		set_SRAMDIRECT(0x03, 3, value);
	}
	/**
	 * Method to set the TO bit.
	 * @param value The value to set.
	 */
	protected void set_TO(int value)
	{
		set_SRAMDIRECT(0x03, 4, value);
	}
	
	/**
	 * Method to get the WDTE bit.
	 * @return The value of the WDTE bit.
	 */
	protected int get_WDTE()
	{
		// TODO: Implement WDTE
		return 1;
	}
	
	/**
	 * Method to set a bit in the SRAM. 
	 * The RP0 bit decides if the value is written to bank 0 or 1.
	 * @param fileaddress The address where the value should be written.
	 * @param bit The bit in the address where the value should be written.
	 * @param value The value to write.
	 */
	protected void set_SRAM(int fileaddress, int bit, int value) 
	{
		switch(fileaddress) 
		{
		// INDF
		case 0:
			set_SRAMDIRECT(0, bit, value);
			set_SRAMDIRECT(128, bit, value);
			break;
		// PCL
		case 2:
			set_SRAMDIRECT(2, bit, value);
			set_SRAMDIRECT(130, bit, value);
			// Add the PCLATH when writing on PCL
			int PCLATH 	= this.get_MemoryDIRECT(0x0A);
			int PCL 	= this.get_MemoryDIRECT(0x02);
			this.programmcounter = ((PCLATH & 0x1F) << 8) | PCL;
			break;
		// Status
		case 3:
			set_SRAMDIRECT(3, bit, value);
			set_SRAMDIRECT(131, bit, value);
			break;
		// FSR
		case 4:
			set_SRAMDIRECT(4, bit, value);
			set_SRAMDIRECT(132, bit, value);
			break;
		// PCLATH
		case 10:
			set_SRAMDIRECT(10, bit, value);
			set_SRAMDIRECT(138, bit, value);
			break;
		// INTCON
		case 11:
			set_SRAMDIRECT(11, bit, value);
			set_SRAMDIRECT(139, bit, value);
			break;
		default:
			if(dataMemory[3][5] == 0) 
			{
				// If TMR0 is accessed and Prescaler is active 
				if (fileaddress == 0x01 && this.get_Memory(0x81, 3) == 0) {
					ctr.getTimer().setPreScaler(0);
				}
				// Redirect write on PortA to Data Latch
				else if(fileaddress == 0x05) 
				{
					if(value == 1) 
					{
						this.dataLatchA = this.dataLatchA | (value << bit);
					}else 
					{
						int bitmask = 0xff;
						bitmask = bitmask ^ (0x01 << bit);
						this.dataLatchA = this.dataLatchA & bitmask;
					}

				}
				// Redirect write on PortA to Data Latch
				else if(fileaddress == 0x06) 
				{
					if(value == 1) 
					{
						this.dataLatchB = this.dataLatchB | (value << bit);
					}else 
					{
						int bitmask = 0xff;
						bitmask = bitmask ^ (0x01 << bit);
						this.dataLatchB = this.dataLatchB & bitmask;
					}
					
				}
				
				set_SRAMDIRECT(fileaddress, bit, value);
			}else if((dataMemory[3][5] == 1) && (fileaddress < 128)) 
			{
				set_SRAMDIRECT(fileaddress + 128, bit, value);
				
			}else 
			{
				set_SRAMDIRECT(fileaddress, bit, value);
			}
			break;	
		}
		
	}
	
	/**
	 * Method to set a value (0x1) in the SRAM. 
	 * The RP0 bit decides if the value is written to bank 0 or 1.
	 * @param fileaddress The address where the value should be written.
	 * @param value The value to write.
	 */
	protected void set_SRAM(int fileaddress, int value) 
	{
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
	
	/**
	 * Method to set a bit in the SRAM directly, so the RP0 bit has no impact.
	 * @param fileaddress The address where the value should be written.
	 * @param bit The bit in the address where the value should be written.
	 * @param value The value to write.
	 */
	protected void set_SRAMDIRECT(int fileaddress, int bit, int value) 
	{
		highlightCell(fileaddress, bit, value);
		dataMemory[fileaddress][bit] = value;
		if( (fileaddress == 0x88) && (bit == 0) && (value == 1)) 
		{
			// clearing rd bit
			this.set_SRAMDIRECT(0x88, 0, 0);
			
			int eepromValue = ctr.getEeprom().getData(this.get_MemoryDIRECT(0x09) & 0x03f);
			System.out.println("EEProm read: "+eepromValue);
			this.set_SRAMDIRECT(0x08,eepromValue);

		}
	}
	
	/**
	 * Method to set a value in the SRAM directly, so the RP0 bit has no impact.
	 * @param fileaddress The address where the value should be written.
	 * @param value The value to write.
	 */
	protected void set_SRAMDIRECT(int fileaddress, int value) 
	{
		String c = Integer.toBinaryString(value);
		for(int l = c.length(); l < 8; l++) 
		{
			c = "0" + c;
		}
		for(int i = 7; i >= 0; i-- ) 
		{
			this.set_SRAMDIRECT(fileaddress, 7-i, Integer.parseInt(""+c.charAt(i)));
		}
	}
	
	/**
	 * Method to highlight the written cell in the code view table in the Simulator_Window.
	 * @param fileaddress The fileaddress of the written value.
	 * @param bit The bit of the written value.
	 * @param value The value which is written, to compare if it changed.
	 */
	private void highlightCell(int fileaddress, int bit, int value) 
	{
		if(value != dataMemory[fileaddress][bit]) {
			ctr.highlightCell(fileaddress/8, fileaddress%8 + 1);
		}
	}
	
	/**
	 * Method to set the zero-flag in the SRAM.
	 * @param z The value to set.
	 */
	public void set_ZEROFLAG(int z) {

		set_SRAM(3,2,z);
	}
	
	/**
	 * Method to set the DC-flag in the SRAM.
	 * @param dc The value to set.
	 */
	public void set_DCFLAG(int dc) 
	{
		set_SRAM(3,1,dc);
	}
	
	/**
	 * Method to set the carry-flag in the SRAM.
	 * @param c The value to set.
	 */
	protected void set_CARRYFLAG(int c) 
	{
		set_SRAMDIRECT(3,0,c);
	}
	
	/**
	 * Method to get the carry-flag from the SRAM.
	 * @return The value of the carry-flag.
	 */
	protected int get_CARRYFLAG() 
	{
		return this.dataMemory[3][0];
	}
	
	/**
	 * Method to get the value of a bit from the SRAM.
	 * @param fileaddress The fileaddress to get the value from.
	 * @param bit The specific bit.
	 * @return The value of the bit.
	 */
	protected int get_Memory(int fileaddress, int bit) 
	{
		if(dataMemory[3][5] == 0) 
		{
			return dataMemory[fileaddress][bit];
		}else  if((dataMemory[3][5] == 1) && (fileaddress < 128))
		{
			return dataMemory[fileaddress+128][bit];
		}else 
		{
			return 0;
		}
	}
	
	/**
	 * Method to get the value of a register from the SRAM.
	 * The RP0 bit decides if the value is written to bank 0 or 1.
	 * @param fileaddress The fileaddress to get the value from.
	 * @return The value of the register.
	 */
	protected int get_Memory(int fileaddress) 
	{
		String c = "";
		for(int i = 0; i < 8; i++) 
		{
			if(dataMemory[3][5] == 0) 
			{
				c = c+ dataMemory[fileaddress][7-i];
			}else if((dataMemory[3][5] == 1) && (fileaddress < 128)) 
			{
				c = c+ dataMemory[fileaddress+128][7-i];
			}else 
			{
				c = c+ dataMemory[fileaddress][7-i];
			}
		}
		return Integer.parseInt(c,2);
	}
	
	/**
	 * Method to get the value of a bit from a register from the SRAM.
	 * The RP0 bit decides if the value is written to bank 0 or 1.
	 * @param fileaddress The fileaddress to get the value from.
	 * @param bit The bit to get the value from.
	 * @return The value of the bit.
	 */
	protected int get_MemoryDIRECT(int fileaddress, int bit) 
	{
		return dataMemory[fileaddress][bit];
	}
	
	/**
	 * Method to get the value of a register from the SRAM directly, the RP0 bit has no impact.
	 * @param fileaddress The fileaddress to get the value from.
	 * @return The value of the register.
	 */
	protected int get_MemoryDIRECT(int fileaddress) 
	{
		String c = "";
		for(int i = 0; i < 8; i++) 
		{
				c = c + dataMemory[fileaddress][7-i];
		}
		return Integer.parseInt(c,2);
	}

	/**
	 * Setter for the program counter.
	 * All values smaller than 0 will be replaced with 0.
	 * @param counter The value of the program counter.
	 */
	protected void set_PROGRAMMCOUNTER(int counter) 
	{
		if (counter < 0) {
			counter = 0;
		}
		programmcounter = counter;
	}
	
	/**
	 * The Getter of the program counter.
	 * @return The value of the program counter.
	 */
	protected int get_PROGRAMMCOUNTER() 
	{
		return programmcounter;
	}
	
	/**
	 * The setter of the w_register array variable to set a single bit.
	 * @param bit The position in the array to set.
	 * @param value The value to set.
	 */
	protected void set_WREGISTER(int bit, int value) 
	{
		w_register[bit] = value;
	}
	
	/**
	 * The setter of the w_register array variable to set the complete value.
	 * @param value The value to set.
	 */
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
	}
	
	/**
	 * Method to get the specific bit of the w register
	 * @param bit The index to get.
	 * @return value The value of the w register position.
	 */
	protected int get_WREGISTER(int bit) 
	{
		return w_register[bit];
	}
	
	/**
	 * The getter of the W register.
	 * @return The value of the complete w register
	 */
	protected int get_WREGISTER() 
	{
		String c = "";
		for(int i = 0; i< 8; i++) 
		{
			c = c + w_register[i] ;
		}
		return Integer.parseInt(c,2);
	}
	
	/**
	 * Method to convert the input array to a hexadecimal value.
	 * @param in The input array.
	 * @return The hexadecimal value.
	 */
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
	 * Used to push the current program counter + 1 on the stack.
	 * @param adr Is the address to push.
	 */
	protected void pushToStack(int adr) 
	{
		int saveLast = intStack[7];
		for(int i = 7; i > 0; i--) {
			intStack[i] = intStack[i - 1];
		}
		intStack[0] = adr;
		if(this.stackSize < 8) {
			this.stackSize += 1;
		}
	}
	
	/**
	 * Used to pop the needed program counter from the stack.
	 * @return The popped address.
	 */
	protected int popFromStack() 
	{
		this.stackSize -= 1;
		int retVal = intStack[0];
		for(int i = 0; i < 7; i++) {
			intStack[i] = intStack[i + 1];
		}
		return retVal;
	}

	/**
	 * Method to clear the stack.
	 */
	protected void clearStack() {
		this.stackSize = 0;
		for (int i = 0; i < 8; i++) {
			intStack[i] = 0;
		}
	}
	/**
	 * Used to clear the programMemory.
	 * The reset value is 255.
	 */
	public void clearProgMem() {
		for(int i = 0; i< this.programMemory.length; i++) 
		{
			this.programMemory[i] = 255;
		}
	}
	
	/**
	 * The getter for the programMemory variable.
	 * @return The programMemory.
	 */
	protected int[] getProgramMemory() {
		return programMemory;
	}

	/**
	 * The setter for the programMemory variable.
	 * @param programMemory The programMemory to set.
	 */
	protected void setProgramMemory(int[] programMemory) {
		this.programMemory = programMemory;
	}

	/**
	 * The getter for the dataLatchA.
	 * @return The dataLatchA.
	 */
	public int getDataLatchA() {
		return dataLatchA;
	}

	/**
	 * The setter for the dataLatchA.
	 * @param dataLatchA The dataLatchA to set.
	 */
	public void setDataLatchA(int dataLatchA) {
		this.dataLatchA = dataLatchA;
	}

	/**
	 * The getter for the dataLatchB.
	 * @return The dataLatchB.
	 */
	public int getDataLatchB() {
		return dataLatchB;
	}

	/**
	 * The setter for the dataLatchB.
	 * @param dataLatchB The dataLatchB to set.
	 */
	public void setDataLatchB(int dataLatchB) {
		this.dataLatchB = dataLatchB;
	}
}
