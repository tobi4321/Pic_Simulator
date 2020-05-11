/// class Processor
/**
 * This class is the main computing unit.
 * if the thread is started, the programm walks through the program memory.
 * if there is a command to execute it will be executed and the Counter arrow is set to the next one
 * 
 * 
 * */

public class Processor extends Thread{
	
	private Controller ctr;
	private Boolean exit = false;
	private boolean debugging = false;

	private boolean continueDebug = false;
	protected boolean isInSleep = false;
	
	protected boolean clkout = false;
	private static final int NOP = 0;
	
	public Processor(Controller pC, boolean pDebugging) 
	{
		this.ctr = pC;
		this.debugging = pDebugging;
	}
	
    public void run() {
    	while (!exit) {  
    		try {

    		for(ctr.memory.programmcounter = 0; ctr.memory.programmcounter < ctr.memory.programMemory.length; ctr.memory.programmcounter++) 
    		{
    			// Write Programmcounter in PCL
    			ctr.memory.set_SRAMDIRECT(0x02, ctr.memory.programmcounter & 0xFF);
    			
    			ctr.setCodeViewCounter(ctr.programCounterList[ctr.memory.programmcounter]);
    			this.oldProgrammCounter = ctr.memory.programmcounter;
    			
    			ctr.updateSpecialRegTable(Integer.toHexString(ctr.memory.programmcounter), 4, 1);
    			ctr.updateSpecialRegTable(Integer.toBinaryString(ctr.memory.programmcounter), 4, 2);
    			
    			// get the current code line as string
    			int codeLine = ctr.memory.programMemory[ctr.memory.programmcounter];
    			
    			ctr.clearHighlights();
    			if(ctr.isNopCycle) {
        			ctr.executeCommand(NOP);		// NOP
        			ctr.isNopCycle = false;
        			ctr.memory.programmcounter--;
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

    			if(ctr.memory.programmcounter >= ctr.memory.programMemory.length) 
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
    	ctr.memory.programmcounter = 65536;
    }
    
	/**
	 * @return the debugging
	 */
	protected boolean isDebugging() {
		return debugging;
	}

	/**
	 * @param debugging the debugging to set
	 */
	protected void setDebugging(boolean debugging) {
		this.debugging = debugging;
	}
	/**
	 * @return the continueDebug
	 */
	protected boolean isContinueDebug() {
		return continueDebug;
	}

	/**
	 * @param continueDebug the continueDebug to set
	 */
	protected void setContinueDebug(boolean continueDebug) {
		this.continueDebug = continueDebug;
	}
}
