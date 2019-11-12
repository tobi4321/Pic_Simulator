import javax.swing.text.BadLocationException;

public class Processor extends Thread{
	
	Controller ctr;
	Boolean exit = false;
	int oldProgrammCounter = 0;
	public Processor(Controller pC) 
	{
		this.ctr = pC;
	}
    public void run() {
    	while (!exit) {  
    		try {
        	  ctr.start();
        	  //ctr.compileCode();
    		for(ctr.programmCounter = 0; ctr.programmCounter < ctr.codeLength; ctr.programmCounter++) 
    		{
    			//ctr.setSegment(ctr.programmCounter, 15-ctr.programmCounter, ctr.programmCounter, 13-ctr.programmCounter);
    			ctr.setCodeViewCounter(this.oldProgrammCounter, ctr.programmCounter);
    			this.oldProgrammCounter = ctr.programmCounter;
    			//ctr.splitter(ctr.code[ctr.programmCounter]);
    			ctr.executeCommand(ctr.hexCode[ctr.programmCounter]);
                sleep(1000);
                if(ctr.programmCounter == ctr.codeLength-1) 
                {
                	stopThread();
                }
    		}

    		}
    		catch(InterruptedException e) {
    		}
    	}
    	System.out.println("Thread stopped");
      }
    public void stopThread() 
    {
    	this.exit = true;
    	ctr.programmCounter = 65536;
    }
}
