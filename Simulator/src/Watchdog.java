/// class Watchdog
/**
 * This class is a basic implementation of a watchdog timer
 * 
 * The Watchdog checks wether the processor is in a deadlock. If the watchdog found a deadlock or an error, he automaticly sets the reset flag
 * 
 * PSA  81h Bit 3   - 1 is prescaler is active for watchdog
 * 
 * */
public class Watchdog extends Thread{
	
	Controller ctr;
	
	int preScaler;
	int timeOutPeriod = 18;
	public boolean exit = false;
	
	public Watchdog(Controller pCtr) 
	{
		this.ctr = pCtr;
	}
	
	public void run() {
		while(!exit) {
			//TODO: If WDTE is set
			incrementWatchDog();
			
			try {
				sleep(timeOutPeriod);
			} catch (InterruptedException e) {
				System.out.println("sleep (" + timeOutPeriod + ") of watchdog failed");
			}
		}
		System.out.println("Watchdog thread stopped");
	}
	
	protected void incrementWatchDog() 
	{
		this.preScaler++;
		int preScalerActive = ctr.memory.get_MemoryDIRECT(0x81, 3);
		if((preScalerActive == 1) && this.preScaler == ( Math.pow(2.0, ctr.getPrescaler())) 
			|| preScalerActive == 0) 
		{
			int in = ctr.memory.get_Memory(1);
			if(in == 255) 
			{
				ctr.memory.set_SRAM(1, 0);
				ctr.memory.set_SRAM(0x0b, 2, 1);
			}else 
			{
				in++;
				ctr.memory.set_SRAM(1, in);
			}
		}
	}
	
	public void stopThread() 
    {
		System.out.println("Watchdog stopped.");
    	this.exit = true;
    }
}
