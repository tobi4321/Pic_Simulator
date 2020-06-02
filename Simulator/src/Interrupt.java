/// class Interrupt
/**
 * This class is a basic implementation of the interrupt functionality.
 * If the interrupt is enabled, this class checks the interrupt IO pins and jumps to the specific adress 0x04.
 * */
public class Interrupt{
	/// The object of the main Controller class.
	private Controller ctr;
	/// The value of the rb0 pin.
	private int rb0;
	/// The value of the rb4 pin.
	private int rb4;
	/// The value of the rb5 pin.
	private int rb5;
	/// The value of the rb6 pin.
	private int rb6;
	/// The value of the rb7 pin.
	private int rb7;
	///	Displaying if and edge was detected and what an edge. 0 means no edge. 1 means positive edge. 2 means negative edge.
	private int rb0Edge;
	/// Displaying if a change on the rb ports occurred.
	private boolean rbChanged; 
	
	/**
	 * The constructor, setting the Controller object.
	 * @param pCtr The controller object to set.
	 */
	public Interrupt(Controller pCtr) 
	{
		this.ctr = pCtr;
	}

	/**
	 *  Method to check all triggers for a interrupt.
	 */
	public boolean checkInterruptFlags() {
    	   // check T0IF and T0IE
    	   if(ctr.getMemory().get_Memory(0x0b, 2) == 1 && ctr.getMemory().get_Memory(0x0b, 5) == 1) 
    	   {
    		   System.out.println("Timer0 Interrupt occured");
    		   return true;
    	   }else
    	   // check INTF and INTE
    	   if(ctr.getMemory().get_Memory(0x0b, 1) == 1 && ctr.getMemory().get_Memory(0x0b, 4) == 1) 
    	   {
    		   System.out.println("INTF Interrupt occured");
    		   return true;
    	   }else
    	   // check RBIF and RBIE
    	   if(ctr.getMemory().get_Memory(0x0b, 0) == 1 && ctr.getMemory().get_Memory(0x0b, 3) == 1) 
    	   {
    		   System.out.println("RB Interrupt occured");
    		   return true;
    	   }else
    	   // check EEIF and EEIE
    	   if(ctr.getMemory().get_Memory(0x88, 4) == 1 && ctr.getMemory().get_Memory(0x0b, 6) == 1) 
    	   {
    		   System.out.println("INTF Interrupt occured");
    		   return true;
    	   }
    	   // No Interrupt
    	   return false;
	}
	
	/**
	 * If interrupts are enabled and an interrupt occurred the current program counter is saved, set to 0x04 and the GIE bit is cleared.
	 */
	public void checkInterrupt() 
	{
		// If Global interrupts are enabled and an interrupt occured
		if (ctr.getMemory().get_Memory(0x0b, 7) == 1 && checkInterruptFlags()) {
			//ctr.memory.pushToStack(ctr.memory.programmcounter);
			ctr.getMemory().pushToStack(ctr.getMemory().get_PROGRAMMCOUNTER());
			// clearing GIE bit to disable other interrupts
			ctr.getMemory().set_SRAM(0x0b, 7, 0);
			// Subtract 1 because the programcounter is incremented again at the end of the run loop in processor
			//ctr.memory.programmcounter = 0x04 - 1;
			ctr.getMemory().set_PROGRAMMCOUNTER(0x04 - 1);
		}
	}
	
	/**
	 * Checking if an interrupt on port rb occurred.
	 */
	protected void checkRBISR() 
	{
		// INTEDG is 1 = pos edge 
		if(ctr.getMemory().get_Memory(0x81, 6) == 1) 
		{
			if(this.rb0Edge == 1) 
			{
				ctr.getMemory().set_SRAM(0x0b, 1, 1);
			}
		}else 
		{
			if(this.rb0Edge == 2) 
			{
				ctr.getMemory().set_SRAM(0x0b, 1, 1);
			}
		}
		
		if(this.rbChanged) 
		{
			ctr.getMemory().set_SRAM(0x0b, 0, 1);
			this.rbChanged = false;
		}
	}
	
	/**
	 * Check the port rb and set the edge type.
	 * @param rb The value ob the port B.
	 */
	protected void updateSources(int rb) 
	{
		if(this.rb0 != (rb & 0x01)) 
		{
			this.rb0 = (rb & 0x01);
			if((rb & 0x01) == 1) 
			{
				this.rb0Edge = 1;
			}else 
			{
				this.rb0Edge = 2;
			}
		}else 
		{
			this.rb0Edge = 0;
		}
		
		if(this.rb4 != (rb & 0x10)) {
			this.rbChanged = true;
			this.rb4 = (rb & 0x10);
		}
		if(this.rb5 != (rb & 0x20)) {
			this.rbChanged = true;
			this.rb5 = (rb & 0x20);
		}
		if(this.rb6 != (rb & 0x40)) {
			this.rbChanged = true;
			this.rb6 = (rb & 0x40);
		}
		if(this.rb7 != (rb & 0x80)) {
			this.rbChanged = true;
			this.rb7 = (rb & 0x80);
		}
	}
}
