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
	private boolean isInSleep = false;

	private boolean continueDebug = false;
	


	private boolean clkout = false;
	private static final int NOP = 0;
	
	public Processor(Controller pC, boolean pDebugging) 
	{
		this.ctr = pC;
		this.debugging = pDebugging;
	}
	
    public void run() {
    	while (!exit) {  
    		try {

    		for(ctr.getMemory().programmcounter = 0; ctr.getMemory().programmcounter < ctr.getMemory().getProgramMemory().length; ctr.getMemory().programmcounter++) 
    		{
    			// Write Programmcounter in PCL
    			ctr.getMemory().set_SRAMDIRECT(0x02, ctr.getMemory().programmcounter & 0xFF);
    			
    			ctr.setCodeViewCounter(ctr.getProgramCounterList()[ctr.getMemory().programmcounter]);
    			
    			ctr.updateSpecialRegTable(Integer.toHexString(ctr.getMemory().programmcounter), 4, 1);
    			ctr.updateSpecialRegTable(Integer.toBinaryString(ctr.getMemory().programmcounter), 4, 2);
    			
    			// get the current code line as string
    			int codeLine = ctr.getMemory().getProgramMemory()[ctr.getMemory().programmcounter];
    			
    			ctr.clearHighlights();
    			if(ctr.isNopCycle()) {
        			ctr.executeCommand(NOP);		// NOP
        			ctr.setNopCycle(false);
        			ctr.getMemory().programmcounter--;
    			}else {	
        			ctr.executeCommand(codeLine);	// Normal Execute
    			}
    			
    			// checking if this pc has a breakpoint
    			if(ctr.getBreakPointList()[ctr.getMemory().programmcounter]) 
    			{
    				this.debugging = true;
    				this.continueDebug = false;
    			}
    			
    			// update all IO like Ports and 7 Segment
    			ctr.refreshIO();
    			
    			ctr.update7Segment();
    			
    			// add the cycle time to operationalTime and update the panel
    			ctr.countCycleTime();
    			System.out.println("OperationalTime: "+ctr.getOperationalTime());
    			ctr.updateOperationalTime();
    			
    			// set the cycle clock output for timer source
    			clkout = true;
    			
    			ctr.getTimer().updateSources(ctr.getMemory().get_Memory(0x05, 4), clkout);
    			ctr.getTimer().checkTMRIncrement();
    			
    			ctr.getInterrupt().updateSources(ctr.getMemory().get_Memory(0x06));
    			ctr.getInterrupt().checkRBISR();
    			
    			// check all interrupt flags
    			ctr.getInterrupt().checkInterrupt();
    			
    			clkout = false;
    			
    			while(this.isInSleep) {
    				sleep(100);
    				if(ctr.getInterrupt().checkInterruptFlags()) {
    					ctr.wakeUpSleep();
    				}
    			}
    			
    			if (this.debugging) {
    				while(!continueDebug) {
    					sleep(100);
    				}
    				continueDebug = false;
    			}else {
    				sleep(ctr.getFrequency());
    			}

    			if(ctr.getMemory().programmcounter >= ctr.getMemory().getProgramMemory().length) 
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
    	ctr.getMemory().programmcounter = 65536;
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
	
	public boolean isInSleep() {
		return isInSleep;
	}

	public void setInSleep(boolean isInSleep) {
		this.isInSleep = isInSleep;
	}
}
