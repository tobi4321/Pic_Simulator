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
	
	public Interrupt(Controller pCtr) 
	{
		this.ctr = pCtr;
	}
	
	public void checkTrigger() 
	{

	}

}
