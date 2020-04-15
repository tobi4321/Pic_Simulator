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
	
	public Processor(Controller pC) 
	{
		this.ctr = pC;
	}
	
    public void run() {
    	while (!exit) {  
    		try {
        	  //ctr.start();
        	  //ctr.compileCode();
    		for(ctr.programmCounter = 0; ctr.programmCounter < ctr.memory.programMemory.length; ctr.programmCounter++) 
    		{
    			//ctr.setSegment(ctr.programmCounter, 15-ctr.programmCounter, ctr.programmCounter, 13-ctr.programmCounter);
    			
    			//ctr.setCodeViewCounter(ctr.programCounterList[this.oldProgrammCounter]-1, ctr.programCounterList[ctr.programmCounter]-1);
    			this.oldProgrammCounter = ctr.programmCounter;
    			
    			//ctr.splitter(ctr.code[ctr.programmCounter]);
    			ctr.updateWRegTable(Integer.toHexString(ctr.programmCounter), 4, 1);
    			ctr.updateWRegTable(Integer.toBinaryString(ctr.programmCounter), 4, 2);
    			
    			// get the current code line as string
    			int codeLine = ctr.memory.programMemory[ctr.programmCounter];
    			String commandToExecute = Integer.toBinaryString(codeLine);
    			while(commandToExecute.length() < 14) 
    			{
    				commandToExecute = "0"+commandToExecute;
    			}
    			ctr.executeCommand(commandToExecute);
                ctr.showError("Test", "This is a test for the error dialog");
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
