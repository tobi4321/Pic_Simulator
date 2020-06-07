/// class Processor
/**
 * This class is the main computing unit.
 * If the thread is started, the program walks through the program memory.
 * If there is a command to execute it will be executed and the counter arrow is set to the next one.
 * */

public class Processor extends Thread{
	/// The object of the main Controller class.
	private Controller ctr;
	/// Displaying if the processor thread should exit.
	private Boolean exit = false;
	/// Displaying if the processor should run in debugging mode.
	private boolean debugging = false;
	/// Displaying if the processor is in sleep mode.
	private boolean isInSleep = false;
	/// Displaying if the next step should be executed when the processor is in debug mode.
	private boolean continueDebug = false;
	/// The multiplicator to slow down the simulation speed.
	private long slowDownTime = 1000;
	/// Signals the clock output signal.
	private boolean clkout = false;
	/// Signals if the processor is running.
	private boolean isRunning = false;
	/// command number for the NOP command
	private static final int NOP = 0;
	
	/**
	 * The constructor setting the Controller object and debugging variable.
	 * @param pC The Controller object.
	 * @param pDebugging The debugging boolean.
	 */
	public Processor(Controller pC, boolean pDebugging) 
	{
		this.ctr = pC;
		this.debugging = pDebugging;
	}
	
	/**
	 * The main Processor thread method.
	 * First the operational time is set to 0.
	 * Then the program counter is set to 0 and incremented until it hits the program length.
	 * The program counter is set in the PCL and the tables in the Simulator_Window is updated.
	 * The command is loaded from the active position and executed, except the active cycle is a NOP.
	 * Then EEPROM state and the watchdog are updated. The Simulator_Window is updated again and all interrupts are checked.
	 * If the processor is in sleep only cycle time, watchdog and interrupts are updated.
	 * If the eeprom write is active the processor is paused for 1ms operational time.
	 */
    public void run() {
    	this.ctr.setOperationalTime(0.0);
		this.isRunning = true;
		this.ctr.getMemory().set_PROGRAMMCOUNTER(0);
    	
		while (!exit) {  
    		try {

    			// Write Programmcounter in PCL
    			ctr.getMemory().set_SRAMDIRECT(0x02, ctr.getMemory().programmcounter & 0xFF);
    			// Set the marker in the gui to the active code line
    			ctr.setCodeViewCounter(ctr.getProgramCounterList()[ctr.getMemory().programmcounter]);
    			// Update the special registers
    			ctr.updateSpecialRegTable(Integer.toHexString(ctr.getMemory().programmcounter), 4, 1);
    			ctr.updateSpecialRegTable(Integer.toBinaryString(ctr.getMemory().programmcounter), 4, 2);
    			
    			// get the command of the current line as an int
    			int codeLine = ctr.getMemory().getProgramMemory()[ctr.getMemory().programmcounter];

    			// checking if this pc has a breakpoint and activating the debugger
    			if(ctr.getBreakPointList()[ctr.getMemory().programmcounter]) 
    			{
    				this.debugging = true;
    				this.continueDebug = false;
    			}
    			
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
    			ctr.getTimer().updateSources(ctr.getMemory().get_MemoryDIRECT(0x05, 4), clkout);
    			ctr.getTimer().checkTMRIncrement();
    			// Update interrupt
    			
    			ctr.getInterrupt().updateSources(ctr.getMemory().get_MemoryDIRECT(0x06));
    			ctr.getInterrupt().checkRBISR();
    			
    			// check all interrupt flags
    			ctr.getInterrupt().checkInterrupt();
    			
    			clkout = false;
    			
    			while(this.isInSleep) {
    				
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
    				if(exit) {
        				break;
        			}
    				// Update run time
    				sleep((long) ctr.getSimulationSpeed());
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
    			if(exit) {
    				break;
    			}
    			if (this.debugging) {
    				while(!continueDebug) {
    					sleep(100);
    				}
    				continueDebug = false;
    			}else {
    				sleep((long) ctr.getSimulationSpeed());
    			}
	
    		}
    		
    		catch(InterruptedException e) {
    		}
    	}
    	this.isRunning = false;
    	System.out.println("Thread stopped");
    }
    
    /**
     * Method to set the boolean to continue the debugging process to the next step.
     */
    public void continueDebugStep() {
    	this.continueDebug = true;
    }
    
    /**
     * Method to stop the processor thread and set the program counter to the maximum.
     */
    public void stopThread() 
    {
    	this.exit = true;
    	//ctr.getMemory().programmcounter = 65536;
    }
    
	/**
	 * The Getter for the debugging variable.
	 * @return The debugging.
	 */
	protected boolean isDebugging() {
		return debugging;
	}

	/**
	 * The Setter for the debugging variable.
	 * @param debugging The debugging to set.
	 */
	protected void setDebugging(boolean debugging) {
		this.debugging = debugging;
	}
	
	/**
	 * The Getter for the continueDebug variable.
	 * @return The continueDebug.
	 */
	protected boolean isContinueDebug() {
		return continueDebug;
	}

	/**
	 * The Setter for the continueDebug variable.
	 * @param continueDebug The continueDebug to set.
	 */
	protected void setContinueDebug(boolean continueDebug) {
		this.continueDebug = continueDebug;
	}
	
	/**
	 * The Getter for the isInSleep variable.
	 * @return The isInSleep.
	 */
	public boolean isInSleep() {
		return isInSleep;
	}

	/**
	 * The Setter for the isInSleep variable.
	 * @param isInSleep The isInSleep to set.
	 */
	public void setInSleep(boolean isInSleep) {
		this.isInSleep = isInSleep;
	}
	
	/**
	 * Method to get the state of the processor.
	 * @return The isRunning.
	 */
	public boolean getRunning() {
		return this.isRunning;
	}
}
