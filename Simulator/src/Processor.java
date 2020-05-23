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
	private long slowDownTime = 1000;


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
    		this.ctr.setOperationalTime(0.0);
    		for(ctr.getMemory().programmcounter = 0; ctr.getMemory().programmcounter < ctr.getMemory().getProgramMemory().length; ctr.getMemory().programmcounter++) 
    		{
    			// Write Programmcounter in PCL
    			ctr.getMemory().set_SRAMDIRECT(0x02, ctr.getMemory().programmcounter & 0xFF);
    			// Set the marker in the gui to the active code line
    			ctr.setCodeViewCounter(ctr.getProgramCounterList()[ctr.getMemory().programmcounter]);
    			// Update the special registers
    			ctr.updateSpecialRegTable(Integer.toHexString(ctr.getMemory().programmcounter), 4, 1);
    			ctr.updateSpecialRegTable(Integer.toBinaryString(ctr.getMemory().programmcounter), 4, 2);
    			
    			// get the command of the current line as an int
    			int codeLine = ctr.getMemory().getProgramMemory()[ctr.getMemory().programmcounter];

    			
    			// If the last command was 2 cycle long then execute a NOP first.
    			ctr.clearHighlights();
    			if(ctr.isNopCycle()) {
        			ctr.getCommands().executeCommand(NOP);		// NOP
        			ctr.setNopCycle(false);
        			ctr.getMemory().set_PROGRAMMCOUNTER(ctr.getMemory().get_PROGRAMMCOUNTER() - 1);
    			}else {	
        			ctr.getCommands().executeCommand(codeLine);	// Normal Execute
    			}
    			// check eeprom state machine
    			ctr.getEeprom().checkStates(ctr.getMemory().get_MemoryDIRECT(0x89));
    			
    			// Before the command execute, the watchdog is updated
    			ctr.getWatchdog().update(ctr.getOperationalTime());
    			// checking if this pc has a breakpoint and activating the debugger

				if(ctr.getBreakPointList()[ctr.getMemory().programmcounter]) 
    			{
    				this.debugging = true;
    				this.continueDebug = false;
    			}

    			// update all analog IO 
    			ctr.refreshIO();
    			// update 7 segment display 
    			ctr.update7Segment();
    			
    			// add the cycle time to operationalTime and update the panel
    			ctr.countCycleTime(ctr.getFrequency());
    			ctr.updateOperationalTime();
    			
    			// set the cycle clock output for timer source
    			clkout = true;
    			
    			// Update timer
    			ctr.getTimer().updateSources(ctr.getMemory().get_Memory(0x05, 4), clkout);
    			ctr.getTimer().checkTMRIncrement();
    			// Update interrupt
    			ctr.getInterrupt().updateSources(ctr.getMemory().get_Memory(0x06));
    			ctr.getInterrupt().checkRBISR();
    			
    			// check all interrupt flags
    			ctr.getInterrupt().checkInterrupt();
    			
    			clkout = false;
    			
    			while(this.isInSleep) {
    				// Update run time
    				sleep((long) ((4.0/ctr.getFrequency())*slowDownTime));
    				ctr.countCycleTime(ctr.getFrequency());

    				ctr.getWatchdog().update(ctr.getOperationalTime());
    				
        			ctr.updateOperationalTime();
        			// Wake up if interrupt
        			ctr.getInterrupt().updateSources(ctr.getMemory().get_Memory(0x06));
        			ctr.getInterrupt().checkRBISR();
    				if(ctr.getInterrupt().checkInterruptFlags()) {
    					ctr.wakeUpSleep();
    				}
    				//TODO: Wake up when EEPROM write complete
    			}
    			
    			// check if eeprom write is active
    			if(ctr.getWriteActive()) 
    			{
    				if( (ctr.getOperationalTime() - ctr.getEeprom().getWriteStartTime()) >= 1.0 ) 
    				{

    					// reset eecon bits
        				ctr.getMemory().set_SRAMDIRECT(0x88, 1, 0);
        				ctr.getMemory().set_SRAMDIRECT(0x88, 4, 1);
        				// deactivate write
        				ctr.setWriteActive(false);
    				}

    			}
    			
    			if (this.debugging) {
    				while(!continueDebug) {
    					sleep(100);
    				}
    				continueDebug = false;
    			}else {
    				sleep((long) ((4.0/ctr.getFrequency())*slowDownTime));
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
