/// class Watchdog
/**
 * This class is a basic implementation of a watchdog timer
 * 
 * The Watchdog checks wether the processor is in a deadlock. If the watchdog found a deadlock or an error, he automaticly sets the reset flag
 * 
 * 
 * 
 * */
public class Watchdog {
	
	private Controller ctr;
	
	private int preScaler;
	
	
	public Watchdog(Controller pCtr) 
	{
		this.ctr = pCtr;
	}
	
	protected void incrementWatchDog() 
	{
		this.preScaler++;
		if(this.preScaler == ( Math.pow(2.0, ctr.getPrescaler()) )) 
		{
			int in = ctr.getMemory().get_Memory(1);
			if(in == 255) 
			{
				ctr.getMemory().set_SRAM(1, 0);
				ctr.getMemory().set_SRAM(0x0b, 2, 1);
			}else 
			{
				in++;
				ctr.getMemory().set_SRAM(1, in);
			}
		}
	}
}
