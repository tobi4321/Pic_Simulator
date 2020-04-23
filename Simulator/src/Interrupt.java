/// class Interrupt
/**
 * This class is a basic implementation of the interrupt functionality
 * 
 * If the interrupt is enabled, this class checks the interrupt IO pins and jumps to the specific adress 0x04 
 * 
 * 
 * 
 * */
public class Interrupt extends Thread{

	private Controller ctr;
	
	public Interrupt(Controller pCtr) 
	{
		this.ctr = pCtr;
	}
	
	public void run() 
	{
		try {
			//ctr.getIOAnalog_OUT();
			//ctr.getIOAnalog_IN();
			
			Thread.sleep(100);
		}catch(Exception e) {
			
		}
	}
}
