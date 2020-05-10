/// class Timer
/**
 * This class is used for implementation of a simple Timer
 * 
 * If the Timer is active, the Timer Register is incremented each tick by specific Time
 *    
 *    T0SE 81h Bit 4   - 1 high to low 
 *    T0CS 81h Bit 5   - 1 is ra4/t0cki - 0 is clkout
 *    T0IF 0bh Bit 2   - 1 is set by overflow of register 01h
 *    T0IE 0bh  Bit 5   - 1 allows Timer to set a interrupt
 *    
 *    PSA  81h Bit 3   - 0 is prescaler is active for timer
 *    PS0  81h Bit 0 
 *    PS1  81h Bit 1
 *    PS2  81h Bit 2
 *    
 * */
public class Timer {
	
	/// bi directional assozication to controller object
	private Controller ctr;
	
	/// ra0/clkout variables
	int ra0;
	boolean clkout;
	
	/**
	 *  0 = no edge
	 *  1 = pos edge
	 *  2 neg edge
	 */
	int raEdge;
	
	/**
	 * counting increments for comparison with prescaler value
	 */
	int preScaler;
	
	
	public Timer(Controller pCtr) 
	{
		this.ctr = pCtr;
	}
	
	
	protected void checkTMRIncrement() 
	{
		
		// if T0CS is 1 source is RA4 otherwise clkout
		if(ctr.getMemory().get_Memory(0x81, 5) == 1) 
		{
			// check source RA4
			if(ctr.getMemory().get_Memory(0x81, 4) == 0) 
			{
				if(this.raEdge == 1) 
				{
					incrementTMR();
				}
			}else 
			{
				if(this.raEdge == 2) 
				{
					incrementTMR();
				}
			}
		}else 
		{
			// check clkout
			
			if(this.clkout) 
			{
				incrementTMR();
			}
		}
	}
	/**
	 *  increments the timer register and checks if a interrupt occured
	 */
	private void incrementTMR() 
	{
		this.preScaler++;
		int preScalerActive = ctr.getMemory().get_MemoryDIRECT(0x81, 3);
		if((preScalerActive == 0) && this.preScaler == ( Math.pow(2.0, ctr.getPrescaler())*2 )
				|| preScalerActive == 1) 
		{
			int in = ctr.getMemory().get_MemoryDIRECT(1);
			if(in == 255) 
			{
				ctr.getMemory().set_SRAMDIRECT(1, 0);
				
				ctr.getMemory().set_SRAM(0x0b, 2, 1);
				ctr.getMemory().set_ZEROFLAG(1);

			}else 
			{
				in++;
				ctr.getMemory().set_SRAMDIRECT(1, in);
			}
			this.preScaler = 0;
		}
	}
	
	
	/**
	 * updates the timer sources and checks if ra has raised a edge
	 * @param pRA input for RA4
	 * @param pCLKOUT input for CLKOUT
	 */
	protected void updateSources(int pRA,boolean pCLKOUT) 
	{
		if(pRA != this.ra0) 
		{
			this.ra0 = pRA;
			if(pRA == 1) 
			{
				this.raEdge = 1;
			}else 
			{
				this.raEdge = 2;
			}
			
		}else 
		{
			this.raEdge = 0;
		}

		this.clkout = pCLKOUT;
	}
}
