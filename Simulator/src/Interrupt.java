/// class Interrupt
/**
 * This class is a basic implementation of the interrupt functionality
 * 
 * If the interrupt is enabled, this class checks the interrupt IO pins and jumps to the specific adress 0x04 
 * 
 * 
 * 
 * */
public class Interrupt{

	private Controller ctr;
	
	private int rb0;
	
	private int rb4;
	private int rb5;
	private int rb6;
	private int rb7;
	
	private int rb0Edge;
	
	private boolean rbChanged; 
	
	public Interrupt(Controller pCtr) 
	{
		this.ctr = pCtr;
	}

	/**
	 *  checking all triggers for a interrupt
	 */
	public boolean checkInterruptFlags() {
    	   // check T0IF and T0IE
    	   if(ctr.memory.get_Memory(0x0b, 2) == 1 && ctr.memory.get_Memory(0x0b, 5) == 1) 
    	   {
    		   System.out.println("Timer0 Interrupt occured");
    		   return true;
    	   }else
    	   // check INTF and INTE
    	   if(ctr.memory.get_Memory(0x0b, 1) == 1 && ctr.memory.get_Memory(0x0b, 4) == 1) 
    	   {
    		   System.out.println("INTF Interrupt occured");
    		   return true;
    	   }else
    	   // check RBIF and RBIE
    	   if(ctr.memory.get_Memory(0x0b, 0) == 1 && ctr.memory.get_Memory(0x0b, 3) == 1) 
    	   {
    		   System.out.println("RB Interrupt occured");
    		   return true;
    	   }else
    	   // check EEIF and EEIE
    	   if(ctr.memory.get_Memory(0x88, 4) == 1 && ctr.memory.get_Memory(0x0b, 6) == 1) 
    	   {
    		   System.out.println("INTF Interrupt occured");
    		   return true;
    	   }
    	   // No Interrupt
    	   return false;
	}
	
	/**
	 * execution if a interrupt occured
	 * saving the current pc
	 * jumping to pc 0x04
	 * clear GIE bit
	 */
	public void checkInterrupt() 
	{
		// If Global interrupts are enabled and an interrupt occured
		if (ctr.memory.get_Memory(0x0b, 7) == 1 && checkInterruptFlags()) {
			//ctr.memory.pushToStack(ctr.memory.programmcounter);
			ctr.memory.pushToStack(ctr.memory.get_PROGRAMMCOUNTER());
			// clearing GIE bit to disable other interrupts
			ctr.memory.set_SRAM(0x0b, 7, 0);
			// Subtract 1 because the programcounter is incremented again at the end of the run loop in processor
			//ctr.memory.programmcounter = 0x04 - 1;
			ctr.memory.set_PROGRAMMCOUNTER(0x04 - 1);
		}
	}
	
	/**
	 * check if interrupt on port rb occured
	 */
	protected void checkRBISR() 
	{
		// INTEDG is 1 = pos edge 
		if(ctr.memory.get_Memory(0x81, 6) == 1) 
		{
			if(this.rb0Edge == 1) 
			{
				ctr.memory.set_SRAM(0x0b, 1, 1);
			}
		}else 
		{
			if(this.rb0Edge == 2) 
			{
				ctr.memory.set_SRAM(0x0b, 1, 1);
			}
		}
		
		
		if(this.rbChanged) 
		{
			ctr.memory.set_SRAM(0x0b, 0, 1);
			this.rbChanged = false;
		}
	}
	
	/**
	 * check the port rb and set the edge type
	 * @param rb
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
