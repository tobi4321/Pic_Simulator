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
	/// The object of the main Controller class.
	private Controller ctr;
	/// Internal counter to counter if a prescaler is assigned.
	private int preScaler;
	/// Saving a time stamp to compare against, to get the 18ms timeout.
	private double timeStamp;

	/**
	 * The constructor. Assigning the Controller object and setting an initial time stamp.
	 * @param pCtr
	 */
	public Watchdog(Controller pCtr) 
	{
		this.ctr = pCtr;
		this.timeStamp = this.ctr.getOperationalTime();
	}
	
	/**
	 * Method to check if a 18ms operational time timeout occurred. If so, the timestamp variable is reset and the watchdog incremented.
	 * @param timeNow
	 */
	public void update(double timeNow) {
		if(ctr.getMemory().get_WDTE() == 1 && (timeNow - this.timeStamp) >= 18.0) {
			incrementWatchDog();
			this.timeStamp = this.ctr.getOperationalTime();
		}
	}
	
	/**
	 * Method to check if an watchdog timeout (considering the prescaler) happened.
	 * If the prescaler is not assigned to the watchdog or the prescaler variable is full, the TO bit and status register is cleared and the processor is waked up or reset.
	 */
	protected void incrementWatchDog() 
	{
		System.out.println(this.preScaler);
		this.preScaler++;
		int preScalerActive = ctr.getMemory().get_MemoryDIRECT(0x81, 3);
		if((preScalerActive == 1) && this.preScaler >= ( Math.pow(2.0, ctr.getPrescaler())) || preScalerActive == 0) 
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
	
	/**
	 * Method to reset the watchdog. 
	 * The timestamp is set to the actual operational time and the prescaler is set to 0.
	 */
	protected void reset() {
		this.timeStamp = this.ctr.getOperationalTime();
		this.preScaler = 0;
	}
	
	/**
	 * The Setter for the preScaler variable.
	 * @param preScaler The preScaler to set.
	 */
	protected void setPreScaler(int preScaler) {
		this.preScaler = preScaler;
	}
}
