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
	
	protected boolean clkout = false;
	private static final int NOP = 0;
	
	public Processor(Controller pC, boolean pDebugging) 
	{
		this.ctr = pC;
		this.debugging = pDebugging;
	}
	
    public void run() {
    	ctr.memory.set_PROGRAMMCOUNTER(0);
    	while (!exit && ctr.memory.get_PROGRAMMCOUNTER() < ctr.memory.programMemory.length) {  
    		try {
    			System.out.println(ctr.memory.get_PROGRAMMCOUNTER() + " - " + ctr.memory.programMemory.length);
    			ctr.setCodeViewCounter(ctr.programCounterList[ctr.memory.get_PROGRAMMCOUNTER()]);
    			this.oldProgrammCounter = ctr.memory.get_PROGRAMMCOUNTER();
    			
    			ctr.updateSpecialRegTable(Integer.toHexString(ctr.memory.get_PROGRAMMCOUNTER()), 4, 1);
    			ctr.updateSpecialRegTable(Integer.toBinaryString(ctr.memory.get_PROGRAMMCOUNTER()), 4, 2);
    			
    			// get the current code line as string
    			int codeLine = ctr.memory.programMemory[ctr.memory.get_PROGRAMMCOUNTER()];
    			
    			ctr.clearHighlights();
    			if(ctr.isNopCycle) {
        			ctr.executeCommand(NOP);		// NOP
        			ctr.isNopCycle = false;
        			//ctr.memory.programmcounter--;
        			ctr.memory.set_PROGRAMMCOUNTER(ctr.memory.get_PROGRAMMCOUNTER() - 1);
    			}else {	
        			ctr.executeCommand(codeLine);	// Normal Execute
    			}
    			
    			// update all IO like Ports and 7 Segment
    			ctr.refreshIO();
    			
    			ctr.update7Segment();
    			
    			
    			// set the cycle clock output for timer source
    			clkout = true;
    			
    			ctr.tmr0.updateSources(ctr.memory.get_Memory(0x05, 4), clkout);
    			ctr.tmr0.checkTMRIncrement();
    			
    			ctr.isr.updateSources(ctr.memory.get_Memory(0x06));
    			ctr.isr.checkRBISR();
    			
    			// check all interrupt flags
    			ctr.isr.checkTrigger();
    			
    			clkout = false;
    			
    			if (this.debugging) {
    				while(!continueDebug) {
    					sleep(100);
    				}
    				continueDebug = false;
    			}else {
    				sleep(ctr.frequency);
    			}

    			if(ctr.memory.get_PROGRAMMCOUNTER() >= ctr.memory.programMemory.length) 
                {
                	stopThread();
                }
    			ctr.memory.set_PROGRAMMCOUNTER(ctr.memory.get_PROGRAMMCOUNTER() + 1);

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
    	//ctr.memory.programmcounter = 65536;
    	ctr.memory.set_PROGRAMMCOUNTER(65536);
    }
}
