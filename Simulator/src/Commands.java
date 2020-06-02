/// class Commands
/**
*  This class contains all command excecutable by the micro processor.
*  The main function "executeCommand" selects the method which is called.
* **/
public class Commands {
	/// An object of the controller.
	private Controller ctr;
	/**
	 * The constructor.
	 * @param pCtr the object of the controller class.
	 */
	public Commands(Controller pCtr) 
	{
		ctr = pCtr;
	}
	
	/**
	 * This method selects the command which must be executed
	 * @param line The command as int.
	 * **/
	public void executeCommand(int line) 
	{
		int precommand 	= (line >> 12) 	& 0x0003;
		int command 	= (line >> 8) 	& 0x000F;
		int payload 	= line 			& 0x00FF;
		if (precommand == 0) {		// Byte Oriented File Register Operations
			int d = payload >> 7;
			int f = payload & 0x7F;
			// Check for indirect adressing
			if (f == 0x00 || f == 0x80) {
				f = ctr.getMemory().get_MemoryDIRECT(0x04);
			}
			
			switch(command) {
			case 0b0111:
				this.addwf(d, f);
				break;
			case 0b0101:
				this.andwf(d, f);
				break;
			case 0b0001:
				if (d == 1) {
					this.clrf(f);
				}else {
					this.clrw();
				}
				break;
			case 0b1001:
				this.comf(d, f);
				break;
			case 0b0011:
				this.decf(d, f);
				break;
			case 0b1011:
				this.decfsz(d, f);
				break;
			case 0b1010:
				this.incf(d, f);
				break;
			case 0b1111:
				this.incfsz(d, f);
				break;
			case 0b0100:
				this.iorwf(d, f);
				break;
			case 0b1000:
				this.movf(d, f);
				break;
			case 0b0000:
				if 		((payload >> 7) == 1) {
					this.movwf(f);
				}
				else if (payload == 0b01100100) {
					this.clrwdt();
				}
				else if	(payload == 0b00001001) {
					this.retfie();
				}
				else if	(payload == 0b00001000) {
					this._return();
				}
				else if (payload == 0b01100011) {
					this.sleep();
				}
				else {
					this.nop();
				}
				break;
			case 0b1101:
				this.rlf(d, f);
				break;
			case 0b1100:
				this.rrf(d, f);
				break;
			case 0b0010:
				this.subwf(d, f);
				break;
			case 0b1110:
				this.swapf(d, f);
				break;
			case 0b0110:
				this.xorwf(d, f);
				break;
			default:
				System.out.println("There is no command for the inserted string: " + line);
				break;
			}
		}
		else if (precommand == 1) {	// Bit-Oriented File Register Operations
			int b = (line >> 7) & 0x0007;
			int f = line  		& 0x007F;
			// Check for indirect adressing
			if (f == 0x00 || f == 0x80) {
				f = ctr.getMemory().get_MemoryDIRECT(0x04);
			}
			
			switch(command >> 2) {
			case 0b00:
				this.bcf(b, f);
				break;
			case 0b01:
				this.bsf(b, f);
				break;
			case 0b10:
				this.btfsc(b, f);
				break;
			case 0b11:
				this.btfss(b, f);
				break;
			}
		}
		else if (precommand == 2) {	// Literal and control operations
			int k = line & 0x07FF;
			
			if ((command >> 3) == 0) {
				this.call(k);
			}
			else {
				this._goto(k);
			}
		}
		else if (precommand == 3) {
			int k = line & 0x00FF;
			if ((command >> 1) == 7) {
				this.addlw(k);
			}
			else if (command == 0b1001) {
				this.andlw(k);
			}
			else if (command == 0b1000) {
				this.iorlw(k);
			}
			else if ((command >> 2) == 0)  {
				this.movlw(k);
			}
			else if ((command >> 2) == 1)  {
				this.retlw(k);
			}
			else if ((command >> 1) == 6)  {
				this.sublw(k);
			}
			else if (command == 0b1010)  {
				this.xorlw(k);
			}
		}
		else {
			System.out.println("There is no command for the inserted string: " + line);
			ctr.showError("Unknown Command", "The Command with the following number is unknown: "+line+" \r Please check the compiled file.");
		}
		
	}
	
	/**
	 * This method executes the ADDWF command.
	 * Add the contents of the W register with register f. If d is 0 the result is stored in the W register. 
	 * @param d If the String d is 0 the result is stored in the W register.
	 * @param f The file register location as String.
	 * **/
	private void addwf(int d, int f) 
	{
		int w_in = ctr.getMemory().get_WREGISTER();
		int f_in = ctr.getMemory().get_Memory(f);
		
		int result = f_in+w_in;
		if(result > 255) 
		{
			ctr.getMemory().set_CARRYFLAG(1);
			result = result - 256;
		}else 
		{
			ctr.getMemory().set_CARRYFLAG(0);
		}
		ctr.checkZeroFlag(result);
		ctr.checkDCFlag(w_in, f_in);
		
		if(d == 0)
		{
			ctr.getMemory().set_WREGISTER(result);
		}else if(d == 1) 
		{
			ctr.getMemory().set_SRAM(f,result);
		}
	}
	
	/**
	 * This method executes the ANDWF command.
	 * AND the W register with register f. 
	 * If d is 0 the result is stored in the W register. If d is 1 the result is stored back in register f.
	 * @param d If the String d is 0 the result is stored in the W register.
	 * @param f The file register location as String.
	 * **/
	private void andwf(int d, int f) 
	{
		int w_in = ctr.getMemory().get_WREGISTER();
		int f_in = ctr.getMemory().get_Memory(f);

		
		int result = w_in & f_in;
		
		ctr.checkZeroFlag(result);

		if(d == 0 )
		{
			ctr.getMemory().set_WREGISTER(result);
		}else if(d == 1) 
		{
			ctr.getMemory().set_SRAM(f, result);
		}
	}
	
	/**
	 * This method executes the CLRF command.
	 * The contents of register f are cleared and the Z bit is set.
	 * @param f The file register location as String.
	 * **/
	private void clrf(int f) 
	{
		ctr.getMemory().set_SRAM(f, 0);
		if(f != 3 && f != 131) 
		{
			ctr.checkZeroFlag(0);
		}
	}
	
	/**
	 * This method executes the CLRW command.
	 * W register is cleared. Zero bit (Z) is set.
	 * **/
	private void clrw() 
	{
		ctr.getMemory().set_WREGISTER(0);
		ctr.checkZeroFlag(0);
	}
	
	/**
	 * This method executes the COMF command.
	 * The contents of register f are complemented. 
	 * If d is 0 the result is stored in W. If d is 1 the result is stored back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void comf(int d, int f) 
	{
		int in = ctr.getMemory().get_Memory(f);		
		int out = 255 - in;
		
		ctr.checkZeroFlag(out);
		
		if(d == 0) 
		{
			ctr.getMemory().set_WREGISTER(out);
		}else if(d == 1) 
		{
			ctr.getMemory().set_SRAM(f, out);
		}
	}
	
	/**
	 * This method executes the DECF command.
	 * Decrement register f. 
	 * If d is 0 the result is stored in the W register. If d is 1 the result is stored back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void decf(int d, int f) 
	{
		int in = ctr.getMemory().get_Memory(f);
		if(in == 0) 
		{
			in = 255;
		}else {
			in--;
		}
		
		ctr.checkZeroFlag(in);
		
		if(d == 0) 
		{
			ctr.getMemory().set_WREGISTER(in);
		}else if(d == 1) 
		{
			ctr.getMemory().set_SRAM(f, in);
		}
	}
	
	/**
	 * This method executes the DECFSZ command.
	 * The contents of register f are decremented. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is placed back in register f.
	 * If the result is 1, the next instruction, is executed. If the result is 0, then a NOP is executed instead making it a 2TCY instruction.	 
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void decfsz(int d, int f) 
	{
		int in = ctr.getMemory().get_Memory(f);
		if(in == 0) 
		{
			in = 255;
		}else {
			in--;
			if(in == 0) 
			{
				ctr.getMemory().set_PROGRAMMCOUNTER(ctr.getMemory().get_PROGRAMMCOUNTER() + 1);
				ctr.setNopCycle(true);
			}
		}
		if(d == 0) 
		{
			ctr.getMemory().set_WREGISTER(in);
		}else if(d == 1) 
		{
			ctr.getMemory().set_SRAM(f, in);
		}
		
	}
	
	/**
	 * This method executes the INCF command.
	 * The contents of register f are incremented. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is placed back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void incf(int d, int f) 
	{
		int in = ctr.getMemory().get_Memory(f);
		if(in == 255) 
		{
			in = 0;
		}else {
			in++;
		}
		
		ctr.checkZeroFlag(in);
		
		if(d == 0) 
		{
			ctr.getMemory().set_WREGISTER(in);
		}else if(d == 1) 
		{
			ctr.getMemory().set_SRAM(f, in);
		}
	}
	
	/**
	 * This method executes the INCFSZ command.
	 * The contents of register f are incremented. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is placed back in register f.
	 * If the result is 1, the next instruction is executed. If the result is 0, a NOP is executed instead making it a 2TCY instruction.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void incfsz(int d, int f) 
	{
		int in = ctr.getMemory().get_Memory(f);
		if(in == 255) 
		{
			in = 0;
			//this.memory.programmcounter++;
			ctr.getMemory().set_PROGRAMMCOUNTER(ctr.getMemory().get_PROGRAMMCOUNTER() + 1);
			ctr.setNopCycle(true);
		}else {
			in++;
		}
		if(d == 0) 
		{
			ctr.getMemory().set_WREGISTER(in);
		}else if(d == 1) 
		{
			ctr.getMemory().set_SRAM(f, in);
		}
	}
	
	/**
	 * This method executes the IORWF command.
	 * Inclusive OR the W register with register f. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is placed back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void iorwf(int d, int f) 
	{
		int w_in = ctr.getMemory().get_WREGISTER();
		int f_in = ctr.getMemory().get_Memory(f);
		
		int result = w_in | f_in;

		ctr.checkZeroFlag(result);
		
		if(d == 0)
		{
			ctr.getMemory().set_WREGISTER(result);
		}else if(d == 1) 
		{
			ctr.getMemory().set_SRAM(f, result);
		}
	}
	
	/**
	 * This method executes the MOVF command.
	 * The contents of register f is moved to a destination dependant upon the status of d. 
	 * If d = 0, destination is W register. If d = 1, the destination is file register f itself.
	 * d = 1 is useful to test a file register since status flag Z is affected.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void movf(int d, int f) 
	{
		int f_in = ctr.getMemory().get_Memory(f);
		
		ctr.checkZeroFlag(f_in);
		if(d == 0)
		{
			ctr.getMemory().set_WREGISTER(f_in);
		}else if(d == 1) 
		{
			ctr.getMemory().set_SRAM(f, f_in);
		}
	}
	
	/**
	 * This method executes the MOVWF command.
	 * Move data from W register to register f.
	 * @param f The file register location as String
	 * **/
	private void movwf(int f) 
	{
		int in = ctr.getMemory().get_WREGISTER();
		ctr.getMemory().set_SRAM(f, in);
	}
	
	/**
	 * This method executes the NOP command.
	 * No operation.
	 * **/
	private void nop() 
	{
		// do nothing
	}
	
	/**
	 * This method executes the RLF command.
	 * The contents of register f are rotated one bit to the left through the Carry Flag. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is stored back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void rlf(int d, int f) 
	{
		int in = ctr.getMemory().get_Memory(f);
		int carry = ctr.getMemory().get_CARRYFLAG();
		
		if ((in & 128) == 128) {
			ctr.getMemory().set_CARRYFLAG(1);
		}
		else {
			ctr.getMemory().set_CARRYFLAG(0);
		}
		int result = ((in << 1) & 0xFF) | (carry&1);
		if(d == 0) 
		{
			ctr.getMemory().set_WREGISTER(result);
		}else
		{
			ctr.getMemory().set_SRAM(f, result);
		}
	}
	
	/**
	 * This method executes the RRF command.
	 * The contents of register f are rotated one bit to the right through the Carry Flag. 
	 * If d is 0 the result is placed in the W register. If d is 1 the result is stored back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void rrf(int d, int f) 
	{
		int in = ctr.getMemory().get_Memory(f);
		int carry = ctr.getMemory().get_CARRYFLAG();
		
		if ((in & 1) == 1) {
			ctr.getMemory().set_CARRYFLAG(1);
		}
		else {
			ctr.getMemory().set_CARRYFLAG(0);
		}
		int result = (in >> 1) | (carry << 7);
		
		if(d == 0) 
		{
			ctr.getMemory().set_WREGISTER(result);
		}else if(d == 1) 
		{
			ctr.getMemory().set_SRAM(f, result);
		}
	}
	
	/**
	 * This method executes the SUBWF command.
	 * Subtract (2s complement method) W register from register f. 
	 * If d is 0 the result is stored in the W register. If d is 1 the result is stored back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void subwf(int d, int f) 
	{
		// TODO: WTF alles fucking falsch mit dc und carry flaggg
		int w_in = ctr.getMemory().get_WREGISTER();
		int f_in = ctr.getMemory().get_Memory(f);
		int result;
		if(w_in > f_in) 
		{
			result = 256 - (w_in-f_in);
		}else {
			result = f_in - w_in;
		}
		
		if (f_in - w_in < 0) {
			this.ctr.getMemory().set_CARRYFLAG(0);
		}else {
			this.ctr.getMemory().set_CARRYFLAG(1);
		}
		
		if (f_in - w_in > 15) {
			this.ctr.getMemory().set_DCFLAG(1);
		}else {
			this.ctr.getMemory().set_DCFLAG(0);
		}
		ctr.checkZeroFlag(result);
		
		if(d == 0)
		{
			ctr.getMemory().set_WREGISTER(result);
		}else if(d == 1) 
		{
			ctr.getMemory().set_SRAM(f, result);
		}
	}
	
	/**
	 * This method executes the SWAPF command.
	 * The upper and lower nibbles of register f are exchanged. 
	 * If d is 0 the result is placed in W register. If d is 1 the result is placed in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void swapf(int d, int f) 
	{
		int f_in = ctr.getMemory().get_Memory(f);
		int result = ((f_in & 0x0F) << 4) | ((f_in & 0xF0) >> 4);

		if(d == 0)
		{
			ctr.getMemory().set_WREGISTER(result);
		}else if(d == 1) 
		{
			ctr.getMemory().set_SRAM(f, result);
		}
	}
	
	/**
	 * This method executes the XORWF command.
	 * Exclusive OR the contents of the W register with register f. 
	 * If d is 0 the result is stored in the W register. If d is 1 the result is stored back in register f.
	 * @param d If the String d is 0 the result is stored in the W register
	 * @param f The file register location as String
	 * **/
	private void xorwf(int d, int f) 
	{
		int w_in = ctr.getMemory().get_WREGISTER();
		int f_in = ctr.getMemory().get_Memory(f);
		
		int result = f_in ^ w_in;
		
		ctr.checkZeroFlag(result);
		
		if(d == 0)
		{
			ctr.getMemory().set_WREGISTER(result);
		}else if(d == 1) 
		{
			ctr.getMemory().set_SRAM(f, result);
		}
	}
	
	//
	//START OF BIT-ORIENTED FILE REGISTER OPERATIONS
	//
	
	/**
	 * This method executes the BCF command.
	 * Bit b in register f is cleared
	 * @param b The bit as String
	 * @param f The file register location as String
	 * **/
	private void bcf(int b, int f) 
	{
		ctr.getMemory().set_SRAM(f, b, 0);
	}
	
	/**
	 * This method executes the BSF command.
	 * Bit b in register f is set.
	 * @param b The bit as String
	 * @param f The file register location as String
	 * **/
	private void bsf(int b, int f) 
	{
		ctr.getMemory().set_SRAM(f, b, 1);
	}
	
	/**
	 * This method executes the STFSC command.
	 * If bit b in register f is 1 then the next instruction is executed. 
	 * If bit b, in register f, is 0 then the next instruction is discarded, and a NOP is executed instead, making this a 2TCY instruction.
	 * @param b The bit as String
	 * @param f The file register location as String
	 * **/
	private void btfsc(int b, int f) 
	{
		int in = ctr.getMemory().get_Memory(f, b);
		if(in == 0) 
		{
			this.ctr.getMemory().set_PROGRAMMCOUNTER(this.ctr.getMemory().get_PROGRAMMCOUNTER() + 1);
			ctr.setNopCycle(true);
		}
	}
	
	/**
	 * This method executes the BTFSS command.
	 * If bit b in register f is 0 then the next instruction is executed. 
	 * If bit b is 1, then the next instruction is discarded and a NOP is executed instead, making this a 2TCY instruction.
	 * @param b The bit as String
	 * @param f The file register location as String
	 * **/
	private void btfss(int b, int f) 
	{
		int in = ctr.getMemory().get_Memory(f, b);
		if(in == 1) 
		{
			this.ctr.getMemory().set_PROGRAMMCOUNTER(this.ctr.getMemory().get_PROGRAMMCOUNTER() + 1);
			ctr.setNopCycle(true);
		}
	}
	
	//
	// BEGIN OF LITERAL AND CONTROL OPERATIONS
	//
	
	/**
	 * This method executes the ADDLW command.
	 * The contents of the W register are added to the eight bit literal k and the result is placed in the W register
	 * @param k The Literal as String.
	 * **/
	private void addlw(int k) 
	{
		int in = ctr.getMemory().get_WREGISTER();
		int result = in + k;

		if(result > 255) 
		{
			this.ctr.getMemory().set_CARRYFLAG(1);
			result = result - 256;
		}else 
		{
			this.ctr.getMemory().set_CARRYFLAG(0);
		}
		ctr.checkZeroFlag(result);
		ctr.checkDCFlag(in, k);
		
		ctr.getMemory().set_WREGISTER(in + k);
	}
	
	/**
	 * This method executes the ANDLW command.
	 * The contents of W register are ANDed with the eight bit literal k. 
	 * The result is placed in the W register.
	 * @param k The Literal as String.
	 * **/
	private void andlw(int k) 
	{    
		int w_in = ctr.getMemory().get_WREGISTER();
		int result = w_in & k;
		
		ctr.checkZeroFlag(result);
		
		ctr.getMemory().set_WREGISTER(result);
	}
	
	/**
	 * This method executes the CALL command.
	 * Call Subroutine.
	 * @param k The Literal as String.
	 * **/
	private void call(int k) 
	{
		ctr.getMemory().pushToStack(this.ctr.getMemory().programmcounter);
		this.ctr.getMemory().programmcounter = k-1;
		ctr.setNopCycle(true);
	}
	
	/**
	 * This method executes the CLRWDT command.
	 * Clears the Watchdog timer.
	 * **/
	private void clrwdt() 
	{
		// TODO: CLRWDT implementieren
		if(this.ctr.getWDTE()) 
		{
			this.ctr.getWatchdog().reset();
			// setting TO and PD Bits
			this.ctr.getMemory().set_SRAMDIRECT(0x03, 3, 1);
			this.ctr.getMemory().set_SRAMDIRECT(0x03, 4, 1);
		}else 
		{
			// watchdog is not enabled
			//What to do?
		}
	}
	
	/**
	 * This method executes the GOTO command.
	 * Sets the {@link programmCounter} to the new position k.
	 * The function goto is a basic java function, therefore _goto is used.
	 * @param k the position as String
	 * **/
	private void _goto(int k) 
	{
		this.ctr.getMemory().programmcounter = k-1;
		ctr.setNopCycle(true);
	}
	
	/**
	 * This method executes the IORLW command.
	 * The contents of the W register is ORed with the eight bit literal k. 
	 * The result is placed in the W register.
	 * @param k the position as String
	 * **/
	private void iorlw(int k) 
	{
		int w_in = ctr.getMemory().get_WREGISTER();
		int result = w_in | k;
		
		ctr.checkZeroFlag(result);

		ctr.getMemory().set_WREGISTER(result);
	}
	
	/**
	 * This method executes the IORLW command.
	 * The literal l is loaded to the W register.
	 * @param l the literal as String
	 * **/
	private void movlw(int k) 
	{
		ctr.getMemory().set_WREGISTER(k);
	}
	
	/**
	 * This method executes the RETFIE command.
	 * Return from Interrupt
	 * **/
	private void retfie() 
	{
		ctr.getMemory().set_GIE(1);
		this.ctr.getMemory().programmcounter = ctr.getMemory().popFromStack();
		ctr.setNopCycle(true);
	}
	
	/**
	 * This method executes the RETLW command.
	 * Returns with the Literal 'k' in the W Register
	 * @param k the literal as String
	 * **/
	private void retlw(int k) 
	{
		ctr.getMemory().set_WREGISTER(k);
		this.ctr.getMemory().programmcounter = ctr.getMemory().popFromStack();
		ctr.setNopCycle(true);
	}
	
	// Attention the function return is a basic java function
	/**
	 * This method executes the RETURN command.
	 * Returns from subroutine.
	 * **/
	private void _return() 
	{
		this.ctr.getMemory().programmcounter = ctr.getMemory().popFromStack();
		ctr.setNopCycle(true);
	}
	
	/**
	 * This method executes the SLEEP command.
	 * **/
	private void sleep() 
	{
		// TODO: Sleep implementieren.
		
		
		this.ctr.getMemory().set_PD(0);
		this.ctr.getMemory().set_TO(1);
		ctr.getWatchdog().reset();
		
		ctr.getProcessor().setInSleep(true);
		ctr.setSleeping(true);
		ctr.getGui().getTglbtnSleeping().setSelected(true);
	}
	
	/**
	 * This method executes the SUBLW command.
	 * The W register is subtracted from the literal k.
	 * The result is placed in the W register.
	 * @param k The literal as String
	 * **/
	private void sublw(int k) 
	{ 
		int w_in = ctr.getMemory().get_WREGISTER();
		int result;
		if(w_in > k) 
		{
			result = 256 - (w_in - k);
			this.ctr.getMemory().set_CARRYFLAG(0);
			
		}else {
			result = k - w_in;
			this.ctr.getMemory().set_CARRYFLAG(1);
		}
		ctr.checkZeroFlag(result);
		ctr.checkDCFlag(w_in, k);
		ctr.getMemory().set_WREGISTER(result);
	}
	
	/**
	 * This method executes the XORLW command.
	 * The contents of the W register are XORed with the eight bit literal k. 
	 * The result is placed in the W register
	 * @param k The literal as String
	 * **/
	private void xorlw(int k) 
	{   // eventuell hier noch nullen auffüllen
		int w_in = ctr.getMemory().get_WREGISTER();
		int result = w_in ^ k;
		
		ctr.checkZeroFlag(result);

		ctr.getMemory().set_WREGISTER(result);
	}
	
	//
	// end of commands
	//
}
