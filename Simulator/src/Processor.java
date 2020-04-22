/// class Processor
/**
 * This class is the main computing unit.
 * if the thread is started, the programm walks through the program memory.
 * if there is a command to execute it will be executed and the Counter arrow is set to the next one
 * 
 * 
 * */

public class Processor extends Thread{
	
	Controller ctr;
	Boolean exit = false;
	int oldProgrammCounter = 0;
	protected boolean debugging = false;
	protected boolean continueDebug = false;

	
	public Processor(Controller pC, boolean pDebugging) 
	{
		this.ctr = pC;
		this.debugging = pDebugging;
	}
	
    public void run() {
    	while (!exit) {  
    		try {

    		for(ctr.programmCounter = 0; ctr.programmCounter < ctr.memory.programMemory.length; ctr.programmCounter++) 
    		{
    			//ctr.setSegment(ctr.programmCounter, 15-ctr.programmCounter, ctr.programmCounter, 13-ctr.programmCounter);
    			
    			//ctr.setCodeViewCounter(ctr.programCounterList[this.oldProgrammCounter], ctr.programCounterList[ctr.programmCounter]);
    			ctr.setCodeViewCounter(ctr.programCounterList[ctr.programmCounter]);
    			this.oldProgrammCounter = ctr.programmCounter;
    			
    			//ctr.splitter(ctr.code[ctr.programmCounter]);
    			ctr.updateSpecialRegTable(Integer.toHexString(ctr.programmCounter), 4, 1);
    			ctr.updateSpecialRegTable(Integer.toBinaryString(ctr.programmCounter), 4, 2);
    			
    			// get the current code line as string
    			int codeLine = ctr.memory.programMemory[ctr.programmCounter];
    			String commandToExecute = Integer.toBinaryString(codeLine);
    			while(commandToExecute.length() < 14) 
    			{
    				commandToExecute = "0"+commandToExecute;
    			}
    			ctr.executeCommand(commandToExecute);
    			
    			ctr.refreshIO();
    			if (this.debugging) {
    				while(!continueDebug) {
    					sleep(100);
    				}
    				continueDebug = false;
    			}else {
    				sleep(ctr.frequency);
    			}

    			if(ctr.programmCounter >= ctr.memory.programMemory.length) 
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
    public void continueDebugStep() {
    	this.continueDebug = true;
    }
    public void stopThread() 
    {
    	this.exit = true;
    	ctr.programmCounter = 65536;
    }
}
