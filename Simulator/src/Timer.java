/// class Timer
/**
 * This class is used for implementation of a simple Timer
 * 
 * If the Timer is active, the Timer Register is incremented each tick by specific Time
 * 
 * 
 * 
 * */
public class Timer {
	private Controller ctr;
	
	public Timer(Controller pCtr) 
	{
		this.ctr = pCtr;
	}
	
	
	protected void checkTMRIncrement() 
	{
		// if T0CS is 1 source is RA4 otherwise clkout
		if(ctr.memory.get_Memory(0x81, 5) == 1) 
		{
			// check source RA4
			if(ctr.memory.get_Memory(5, 4) == 1) 
			{
				incrementTMR();
			}
		}else 
		{
			// check clkout
			if(ctr.proc.clkout) 
			{
				incrementTMR();
			}
		}
	}
	private void incrementTMR() 
	{
		int in = ctr.memory.get_Memory(1);
		if(in == 255) 
		{
			ctr.memory.set_SRAM(1, 0);
			ctr.memory.set_SRAM(0x0b, 2, 1);
		}
	}
}
