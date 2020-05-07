import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
/// class MyPanel
/**
*  
*  The Panel to display the 7-segment Display.
*     a
*    ---
* f | g | b 
*    ---
* e |   | c
*    ---     . h
*     d
*               h g f e d c b a
*  input char = 0 0 0 0 0 0 0 0 b
* 
*  horizontal
*   ________
*  |________|
* 
*  vertical 
*   _
*  | |
*  | |
*  | | 
*  | |
*  |_|
* 
* 
*  
* **/
public class SevenSegmentPanel extends JPanel{

    private int First;
    private int Second;
    private int Third;
    private int Fourth;
    
    private final static int correction = 25;
    
    private final static int secondOffSet = 80;
    private final static int thirdOffSet = 160;
    private final static int fourthOffSet = 240;
    
    private final static int horizontalHeight = 5;
    private final static int horizontalWidth = 40;
    
    private final static int verticalHeight = 40;
    private final static int verticalWidth = 5;
    
    
    
	public SevenSegmentPanel() 
	{
        
    }
	public void set7Segment(int controlPort, int dataPort) 
	{
		if((controlPort & 0x01) == 0x01) 
		{
	    	this.First = dataPort;
		}
		if((controlPort & 0x02) == 0x02) 
		{
			this.Second = dataPort;
		}
		if((controlPort & 0x04) == 0x04) 
		{
			this.Third = dataPort;
		}
		if((controlPort & 0x08) == 0x08) 
		{
			this.Fourth = dataPort;
		}
		this.setChars(this.First, this.Second, this.Third, this.Fourth);
	}
    
    public void setChars(int c1,int c2, int c3, int c4) 
    {
    	this.First = c1;
    	this.Second = c2;
    	this.Third = c3;
    	this.Fourth = c4;
    	
    	repaint();
    }
    public void paintFirst(Graphics g,int Char) 
    {   
    	g.setColor(Color.red);
    	if((Char & 0x01) == 0x01) 
    	{
        	g.fillRect(correction+20, 95,horizontalWidth,horizontalHeight);
    	}
    	if((Char & 0x02) == 0x02) 
    	{
    		g.fillRect(correction+60, 55, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x04) == 0x04) 
    	{
    		g.fillRect(correction+60, 10, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x08) == 0x08)
    	{
    		g.fillRect(correction+20, 5, horizontalWidth, horizontalHeight);
    	}
    	if((Char & 0x10) == 0x10) 
    	{
    		g.fillRect(correction+15, 10, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x20) == 0x20) 
    	{
    		g.fillRect(correction+15, 55, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x40) == 0x40) 
    	{
    		g.fillRect(correction+20, 50, horizontalWidth, horizontalHeight);
    	}
    	if((Char & 0x80) == 0x80) 
    	{
    		// TODO: implement dot
    	}
    }
    
    public void paintSecond(Graphics g,int Char) 
    {
    	g.setColor(Color.red);
    	if((Char & 0x01) == 0x01) 
    	{
    		g.fillRect(correction+20+secondOffSet, 95,horizontalWidth,horizontalHeight);
    	}
    	if((Char & 0x02) == 0x02) 
    	{
    		g.fillRect(correction+60+secondOffSet, 55, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x04) == 0x04) 
    	{
    		g.fillRect(correction+60+secondOffSet, 10, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x08) == 0x08)
    	{
    		g.fillRect(correction+20+secondOffSet, 5, horizontalWidth, horizontalHeight);
    	}
    	if((Char & 0x10) == 0x10) 
    	{
    		g.fillRect(correction+15+secondOffSet, 10, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x20) == 0x20) 
    	{
    		g.fillRect(correction+15+secondOffSet, 55, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x40) == 0x40) 
    	{
    		g.fillRect(correction+20+secondOffSet, 50, horizontalWidth, horizontalHeight);
    	}
    	if((Char & 0x80) == 0x80) 
    	{
    		// TODO: implement dot
    	}  	
    }
    
    public void paintThird(Graphics g,int Char) 
    {
    	g.setColor(Color.red);
    	if((Char & 0x01) == 0x01) 
    	{
    		g.fillRect(correction+20+thirdOffSet, 95,horizontalWidth,horizontalHeight);
    	}
    	if((Char & 0x02) == 0x02) 
    	{
    		g.fillRect(correction+60+thirdOffSet, 55, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x04) == 0x04) 
    	{
    		g.fillRect(correction+60+thirdOffSet, 10, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x08) == 0x08)
    	{
    		g.fillRect(correction+20+thirdOffSet, 5, horizontalWidth, horizontalHeight);
    	}
    	if((Char & 0x10) == 0x10) 
    	{
    		g.fillRect(correction+15+thirdOffSet, 10, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x20) == 0x20) 
    	{
    		g.fillRect(correction+15+thirdOffSet, 55, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x40) == 0x40) 
    	{
    		g.fillRect(correction+20+thirdOffSet, 50, horizontalWidth, horizontalHeight);
    	}
    	if((Char & 0x80) == 0x80) 
    	{
    		// TODO: implement dot
    	}   	
    }
    
    public void paintFourth(Graphics g,int Char) 
    {
    	g.setColor(Color.red);
    	if((Char & 0x01) == 0x01) 
    	{
    		g.fillRect(correction+20+fourthOffSet, 95,horizontalWidth,horizontalHeight);
    	}
    	if((Char & 0x02) == 0x02) 
    	{
    		g.fillRect(correction+60+fourthOffSet, 55, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x04) == 0x04) 
    	{
    		g.fillRect(correction+60+fourthOffSet, 10, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x08) == 0x08)
    	{
    		g.fillRect(correction+20+fourthOffSet, 5, horizontalWidth, horizontalHeight);
    	}
    	if((Char & 0x10) == 0x10) 
    	{
    		g.fillRect(correction+15+fourthOffSet, 10, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x20) == 0x20) 
    	{
    		g.fillRect(correction+15+fourthOffSet, 55, verticalWidth, verticalHeight);
    	}
    	if((Char & 0x40) == 0x40) 
    	{
    		g.fillRect(correction+20+fourthOffSet, 50, horizontalWidth, horizontalHeight);
    	}
    	if((Char & 0x80) == 0x80) 
    	{
    		// TODO: implement dot
    	}   	
    }
    

    public Dimension getPreferredSize() {
        return new Dimension(310,105);
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);       
        
        g.drawRect(correction+20, 5,40,5);
        g.drawRect(correction+20, 50,40,5);
        g.drawRect(correction+20, 95,40,5);
        g.drawRect(correction+15, 10, 5, 40);
        g.drawRect(correction+15, 55, 5, 40);
        g.drawRect(correction+60, 10, 5, 40);
        g.drawRect(correction+60, 55, 5, 40);
        
        g.drawRect(correction+20+80, 5,40,5);
        g.drawRect(correction+20+80, 50,40,5);
        g.drawRect(correction+20+80, 95,40,5);
        g.drawRect(correction+15+80, 10, 5, 40);
        g.drawRect(correction+15+80, 55, 5, 40);
        g.drawRect(correction+60+80, 10, 5, 40);
        g.drawRect(correction+60+80, 55, 5, 40);
        
        g.drawRect(correction+20+160, 5,40,5);
        g.drawRect(correction+20+160, 50,40,5);
        g.drawRect(correction+20+160, 95,40,5);
        g.drawRect(correction+15+160, 10, 5, 40);
        g.drawRect(correction+15+160, 55, 5, 40);
        g.drawRect(correction+60+160, 10, 5, 40);
        g.drawRect(correction+60+160, 55, 5, 40);
        
        g.drawRect(correction+20+240, 5,40,5);
        g.drawRect(correction+20+240, 50,40,5);
        g.drawRect(correction+20+240, 95,40,5);
        g.drawRect(correction+15+240, 10, 5, 40);
        g.drawRect(correction+15+240, 55, 5, 40);
        g.drawRect(correction+60+240, 10, 5, 40);
        g.drawRect(correction+60+240, 55, 5, 40);
        
        paintFirst(g,this.First);
        paintSecond(g,this.Second);
        paintThird(g,this.Third);
        paintFourth(g,this.Fourth);
    }  
	


}
