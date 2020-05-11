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
	
	private Controller ctr;
	
	private int preScaler;
	int timeOutPeriod = 18;
	public boolean exit = false;
	
	public Watchdog(Controller pCtr) 
	{
		this.ctr = pCtr;
	}
	
	public void run() {
		while(!exit) {
			//TODO: If WDTE is set, wo ist WDTE?? (0x2007, 2)?
			if(true) {//this.ctr.memory.get_MemoryDIRECT(0x20, 9) == 1) {
				incrementWatchDog();
			}
			
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
			if(this.ctr.proc.isInSleep) {
				this.ctr.wakeUpSleep();
			}else {
				this.ctr.reset();
			}
		}
	}
	
	public void stopThread() 
    {
		System.out.println("Watchdog stopped.");
    	this.exit = true;
    }
}
