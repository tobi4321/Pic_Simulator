/// class Watchdog
/**
 * This class is a basic implementation of a watchdog timer
 * 
 * The Watchdog checks wether the processor is in a deadlock. If the watchdog found a deadlock or an error, he automaticly sets the reset flag
 * 
 * PSA  81h Bit 3   - 1 is prescaler is active for watchdog
 * 
 * */
public class Watchdog {
	
	private Controller ctr;
	
	private int preScaler;
	private double timeStamp;
	int timeOutPeriod = 10;
	public boolean exit = false;
	
	public Watchdog(Controller pCtr) 
	{
		this.ctr = pCtr;
		this.timeStamp = this.ctr.getOperationalTime();
	}
	
	public void update(double timeNow) {
		if(ctr.getMemory().get_WDTE() == 1 && timeNow - this.timeStamp >= 18.0) {
			incrementWatchDog();
			this.timeStamp = this.ctr.getOperationalTime();
		}
	}
	
	protected void incrementWatchDog() 
	{
		System.out.println(this.preScaler);
		this.preScaler++;
		int preScalerActive = ctr.getMemory().get_MemoryDIRECT(0x81, 3);
		if((preScalerActive == 1) && this.preScaler >= ( Math.pow(2.0, ctr.getPrescaler())) 
			|| preScalerActive == 0) 
		{
			System.out.println("Watchdog time-out occured at time: " + this.ctr.getOperationalTime());
			this.ctr.getMemory().set_TO(0);
			this.ctr.getMemory().set_SRAMDIRECT(0x03, 0);
			
			if(this.ctr.getProcessor().isInSleep()) {
				this.ctr.wakeUpSleep();
			}else {
				this.ctr.reset();
			}
			this.preScaler = 0;
		}
	}
	
	protected void setPreScaler(int preScaler) {
		this.preScaler = preScaler;
	}
	
	protected void reset() {
		this.timeStamp = this.ctr.getOperationalTime();
		this.preScaler = 0;
	}
}
