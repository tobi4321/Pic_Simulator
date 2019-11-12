public class Interrupt extends Thread{

	private Controller ctr;
	
	public Interrupt(Controller pCtr) 
	{
		this.ctr = pCtr;
	}
	
	public void run() 
	{
		try {
			ctr.getIOPort_A();
			ctr.getIOPort_B();
			
			Thread.sleep(100);
		}catch(Exception e) {
			
		}
	}
}
