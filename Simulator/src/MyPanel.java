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
* 
* **/
public class MyPanel extends JPanel{

    private int First;
    private int Second;
    private int Third;
    private int Fourth;
    
    private int correction = 40;
    
	public MyPanel() 
	{
        
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
        if(Char == 0) 
        {
        	g.fillRect(correction+20, 5, 40, 5);
        	g.fillRect(correction+20, 95,40,5);
        	g.fillRect(correction+15, 10, 5, 40);
        	g.fillRect(correction+15, 55, 5, 40);
        	g.fillRect(correction+60, 10, 5, 40);
        	g.fillRect(correction+60, 55, 5, 40);
        }else if(Char == 1) 
        {
        	g.fillRect(correction+60, 10, 5, 40);
        	g.fillRect(correction+60, 55, 5, 40);
        }else if(Char == 2) 
        {
        	g.fillRect(correction+20, 5, 40, 5);
        	g.fillRect(correction+20, 50, 40, 5);
        	g.fillRect(correction+20, 95,40,5);
        	g.fillRect(correction+15, 55, 5, 40);
        	g.fillRect(correction+60, 10, 5, 40);
        }else if(Char == 3) 
        {
        	g.fillRect(correction+20, 5, 40, 5);
        	g.fillRect(correction+20, 50, 40, 5);
        	g.fillRect(correction+20, 95,40,5);
        	g.fillRect(correction+60, 10, 5, 40);
        	g.fillRect(correction+60, 55, 5, 40);
        }else if(Char == 4) 
        {
        	g.fillRect(correction+20, 50, 40, 5);
        	g.fillRect(correction+15, 10, 5, 40);
        	g.fillRect(correction+60, 10, 5, 40);
        	g.fillRect(correction+60, 55, 5, 40);
        	
        }else if(Char == 5) 
        {
        	g.fillRect(correction+20, 5, 40, 5);
        	g.fillRect(correction+20, 50, 40, 5);
        	g.fillRect(correction+20, 95,40,5);
        	g.fillRect(correction+15, 10, 5, 40);
        	g.fillRect(correction+60, 55, 5, 40);
        }else if(Char == 6) 
        {
        	g.fillRect(correction+20, 5, 40, 5);
        	g.fillRect(correction+20, 50, 40, 5);
        	g.fillRect(correction+20, 95,40,5);
        	g.fillRect(correction+15, 10, 5, 40);
        	g.fillRect(correction+15, 55, 5, 40);
        	g.fillRect(correction+60, 55, 5, 40);
        }else if(Char == 7) 
        {
        	g.fillRect(correction+20, 5, 40, 5);
        	g.fillRect(correction+60, 10, 5, 40);
        	g.fillRect(correction+60, 55, 5, 40);
        }else if(Char == 8) 
        {
        	g.fillRect(correction+20, 5, 40, 5);
        	g.fillRect(correction+20, 50, 40, 5);
        	g.fillRect(correction+20, 95,40,5);
        	g.fillRect(correction+15, 10, 5, 40);
        	g.fillRect(correction+15, 55, 5, 40);
        	g.fillRect(correction+60, 10, 5, 40);
        	g.fillRect(correction+60, 55, 5, 40);
        }else if(Char == 9) 
        {
        	g.fillRect(correction+20, 5, 40, 5);
        	g.fillRect(correction+20, 50, 40, 5);
        	g.fillRect(correction+20, 95,40,5);
        	g.fillRect(correction+15, 10, 5, 40);
        	g.fillRect(correction+60, 10, 5, 40);
        	g.fillRect(correction+60, 55, 5, 40);
        }else if(Char == 10) 
        {
        	g.fillRect(correction+20, 5, 40, 5);
        	g.fillRect(correction+20, 50, 40, 5);
        	g.fillRect(correction+15, 10, 5, 40);
        	g.fillRect(correction+15, 55, 5, 40);
        	g.fillRect(correction+60, 10, 5, 40);
        	g.fillRect(correction+60, 55, 5, 40);
        }else if(Char == 11) 
        {
        	g.fillRect(correction+20, 50, 40, 5);
        	g.fillRect(correction+20, 95,40,5);
        	g.fillRect(correction+15, 10, 5, 40);
        	g.fillRect(correction+60, 10, 5, 40);
        	g.fillRect(correction+60, 55, 5, 40);
        }else if(Char == 12) 
        {
        	g.fillRect(correction+20, 5, 40, 5);
        	g.fillRect(correction+20, 95,40,5);
        	g.fillRect(correction+15, 10, 5, 40);
        	g.fillRect(correction+15, 55, 5, 40);
        }else if(Char == 13) 
        {
        	g.fillRect(correction+20, 50, 40, 5);
        	g.fillRect(correction+20, 95,40,5);
        	g.fillRect(correction+15, 55, 5, 40);
        	g.fillRect(correction+60, 10, 5, 40);
        	g.fillRect(correction+60, 55, 5, 40);
        }else if(Char == 14) 
        {
        	g.fillRect(correction+20, 5, 40, 5);
        	g.fillRect(correction+20, 50, 40, 5);
        	g.fillRect(correction+20, 95,40,5);
        	g.fillRect(correction+15, 10, 5, 40);
        	g.fillRect(correction+15, 55, 5, 40);
        }else if(Char == 15) 
        {
        	g.fillRect(correction+20, 5, 40, 5);
        	g.fillRect(correction+20, 50, 40, 5);
        	g.fillRect(correction+15, 10, 5, 40);
        	g.fillRect(correction+15, 55, 5, 40);
        }
    }
    public void paintSecond(Graphics g,int Char) 
    {
    	g.setColor(Color.red);
        if(Char == 0) 
        {
        	g.fillRect(correction+20+80, 5, 40, 5);
        	g.fillRect(correction+20+80, 95,40,5);
        	g.fillRect(correction+15+80, 10, 5, 40);
        	g.fillRect(correction+15+80, 55, 5, 40);
        	g.fillRect(correction+60+80, 10, 5, 40);
        	g.fillRect(correction+60+80, 55, 5, 40);
        }else if(Char == 1) 
        {
        	g.fillRect(correction+60+80, 10, 5, 40);
        	g.fillRect(correction+60+80, 55, 5, 40);
        }else if(Char == 2) 
        {
        	g.fillRect(correction+20+80, 5, 40, 5);
        	g.fillRect(correction+20+80, 50, 40, 5);
        	g.fillRect(correction+20+80, 95,40,5);
        	g.fillRect(correction+15+80, 55, 5, 40);
        	g.fillRect(correction+60+80, 10, 5, 40);
        }else if(Char == 3) 
        {
        	g.fillRect(correction+20+80, 5, 40, 5);
        	g.fillRect(correction+20+80, 50, 40, 5);
        	g.fillRect(correction+20+80, 95,40,5);
        	g.fillRect(correction+60+80, 10, 5, 40);
        	g.fillRect(correction+60+80, 55, 5, 40);
        }else if(Char == 4) 
        {
        	g.fillRect(correction+20+80, 50, 40, 5);
        	g.fillRect(correction+15+80, 10, 5, 40);
        	g.fillRect(correction+60+80, 10, 5, 40);
        	g.fillRect(correction+60+80, 55, 5, 40);
        	
        }else if(Char == 5) 
        {
        	g.fillRect(correction+20+80, 5, 40, 5);
        	g.fillRect(correction+20+80, 50, 40, 5);
        	g.fillRect(correction+20+80, 95,40,5);
        	g.fillRect(correction+15+80, 10, 5, 40);
        	g.fillRect(correction+60+80, 55, 5, 40);
        }else if(Char == 6) 
        {
        	g.fillRect(correction+20+80, 5, 40, 5);
        	g.fillRect(correction+20+80, 50, 40, 5);
        	g.fillRect(correction+20+80, 95,40,5);
        	g.fillRect(correction+15+80, 10, 5, 40);
        	g.fillRect(correction+15+80, 55, 5, 40);
        	g.fillRect(correction+60+80, 55, 5, 40);
        }else if(Char == 7) 
        {
        	g.fillRect(correction+20+80, 5, 40, 5);
        	g.fillRect(correction+60+80, 10, 5, 40);
        	g.fillRect(correction+60+80, 55, 5, 40);
        }else if(Char == 8) 
        {
        	g.fillRect(correction+20+80, 5, 40, 5);
        	g.fillRect(correction+20+80, 50, 40, 5);
        	g.fillRect(correction+20+80, 95,40,5);
        	g.fillRect(correction+15+80, 10, 5, 40);
        	g.fillRect(correction+15+80, 55, 5, 40);
        	g.fillRect(correction+60+80, 10, 5, 40);
        	g.fillRect(correction+60+80, 55, 5, 40);
        }else if(Char == 9) 
        {
        	g.fillRect(correction+20+80, 5, 40, 5);
        	g.fillRect(correction+20+80, 50, 40, 5);
        	g.fillRect(correction+20+80, 95,40,5);
        	g.fillRect(correction+1+805, 10, 5, 40);
        	g.fillRect(correction+60+80, 10, 5, 40);
        	g.fillRect(correction+60+80, 55, 5, 40);
        }else if(Char == 10) 
        {
        	g.fillRect(correction+20+80, 5, 40, 5);
        	g.fillRect(correction+20+80, 50, 40, 5);
        	g.fillRect(correction+15+80, 10, 5, 40);
        	g.fillRect(correction+15+80, 55, 5, 40);
        	g.fillRect(correction+60+80, 10, 5, 40);
        	g.fillRect(correction+60+80, 55, 5, 40);
        }else if(Char == 11) 
        {
        	g.fillRect(correction+20+80, 50, 40, 5);
        	g.fillRect(correction+20+80, 95,40,5);
        	g.fillRect(correction+15+80, 10, 5, 40);
        	g.fillRect(correction+60+80, 10, 5, 40);
        	g.fillRect(correction+60+80, 55, 5, 40);
        }else if(Char == 12) 
        {
        	g.fillRect(correction+20+80, 5, 40, 5);
        	g.fillRect(correction+20+80, 95,40,5);
        	g.fillRect(correction+15+80, 10, 5, 40);
        	g.fillRect(correction+15+80, 55, 5, 40);
        }else if(Char == 13) 
        {
        	g.fillRect(correction+20+80, 50, 40, 5);
        	g.fillRect(correction+20+80, 95,40,5);
        	g.fillRect(correction+15+80, 55, 5, 40);
        	g.fillRect(correction+60+80, 10, 5, 40);
        	g.fillRect(correction+60+80, 55, 5, 40);
        }else if(Char == 14) 
        {
        	g.fillRect(correction+20+80, 5, 40, 5);
        	g.fillRect(correction+20+80, 50, 40, 5);
        	g.fillRect(correction+20+80, 95,40,5);
        	g.fillRect(correction+15+80, 10, 5, 40);
        	g.fillRect(correction+15+80, 55, 5, 40);
        }else if(Char == 15) 
        {
        	g.fillRect(correction+20+80, 5, 40, 5);
        	g.fillRect(correction+20+80, 50, 40, 5);
        	g.fillRect(correction+15+80, 10, 5, 40);
        	g.fillRect(correction+15+80, 55, 5, 40);
        }    	
    }
    public void paintThird(Graphics g,int Char) 
    {
    	g.setColor(Color.red);
        if(Char == 0) 
        {
        	g.fillRect(correction+20+160, 5, 40, 5);
        	g.fillRect(correction+20+160, 95,40,5);
        	g.fillRect(correction+15+160, 10, 5, 40);
        	g.fillRect(correction+15+160, 55, 5, 40);
        	g.fillRect(correction+60+160, 10, 5, 40);
        	g.fillRect(correction+60+160, 55, 5, 40);
        }else if(Char == 1) 
        {
        	g.fillRect(correction+60+160, 10, 5, 40);
        	g.fillRect(correction+60+160, 55, 5, 40);
        }else if(Char == 2) 
        {
        	g.fillRect(correction+20+160, 5, 40, 5);
        	g.fillRect(correction+20+160, 50, 40, 5);
        	g.fillRect(correction+20+160, 95,40,5);
        	g.fillRect(correction+15+160, 55, 5, 40);
        	g.fillRect(correction+60+160, 10, 5, 40);
        }else if(Char == 3) 
        {
        	g.fillRect(correction+20+160, 5, 40, 5);
        	g.fillRect(correction+20+160, 50, 40, 5);
        	g.fillRect(correction+20+160, 95,40,5);
        	g.fillRect(correction+60+160, 10, 5, 40);
        	g.fillRect(correction+60+160, 55, 5, 40);
        }else if(Char == 4) 
        {
        	g.fillRect(correction+20+160, 50, 40, 5);
        	g.fillRect(correction+15+160, 10, 5, 40);
        	g.fillRect(correction+60+160, 10, 5, 40);
        	g.fillRect(correction+60+160, 55, 5, 40);
        	
        }else if(Char == 5) 
        {
        	g.fillRect(correction+20+160, 5, 40, 5);
        	g.fillRect(correction+20+160, 50, 40, 5);
        	g.fillRect(correction+20+160, 95,40,5);
        	g.fillRect(correction+15+160, 10, 5, 40);
        	g.fillRect(correction+60+160, 55, 5, 40);
        }else if(Char == 6) 
        {
        	g.fillRect(correction+20+160, 5, 40, 5);
        	g.fillRect(correction+20+160, 50, 40, 5);
        	g.fillRect(correction+20+160, 95,40,5);
        	g.fillRect(correction+15+160, 10, 5, 40);
        	g.fillRect(correction+15+160, 55, 5, 40);
        	g.fillRect(correction+60+160, 55, 5, 40);
        }else if(Char == 7) 
        {
        	g.fillRect(correction+20+160, 5, 40, 5);
        	g.fillRect(correction+60+160, 10, 5, 40);
        	g.fillRect(correction+60+160, 55, 5, 40);
        }else if(Char == 8) 
        {
        	g.fillRect(correction+20+160, 5, 40, 5);
        	g.fillRect(correction+20+160, 50, 40, 5);
        	g.fillRect(correction+20+160, 95,40,5);
        	g.fillRect(correction+15+160, 10, 5, 40);
        	g.fillRect(correction+15+160, 55, 5, 40);
        	g.fillRect(correction+60+160, 10, 5, 40);
        	g.fillRect(correction+60+160, 55, 5, 40);
        }else if(Char == 9) 
        {
        	g.fillRect(correction+20+160, 5, 40, 5);
        	g.fillRect(correction+20+160, 50, 40, 5);
        	g.fillRect(correction+20+160, 95,40,5);
        	g.fillRect(correction+15+160, 10, 5, 40);
        	g.fillRect(correction+60+160, 10, 5, 40);
        	g.fillRect(correction+60+160, 55, 5, 40);
        }else if(Char == 10) 
        {
        	g.fillRect(correction+20+160, 5, 40, 5);
        	g.fillRect(correction+20+160, 50, 40, 5);
        	g.fillRect(correction+15+160, 10, 5, 40);
        	g.fillRect(correction+15+160, 55, 5, 40);
        	g.fillRect(correction+60+160, 10, 5, 40);
        	g.fillRect(correction+60+160, 55, 5, 40);
        }else if(Char == 11) 
        {
        	g.fillRect(correction+20+160, 50, 40, 5);
        	g.fillRect(correction+20+160, 95,40,5);
        	g.fillRect(correction+15+160, 10, 5, 40);
        	g.fillRect(correction+60+160, 10, 5, 40);
        	g.fillRect(correction+60+160, 55, 5, 40);
        }else if(Char == 12) 
        {
        	g.fillRect(correction+20+160, 5, 40, 5);
        	g.fillRect(correction+20+160, 95,40,5);
        	g.fillRect(correction+15+160, 10, 5, 40);
        	g.fillRect(correction+15+160, 55, 5, 40);
        }else if(Char == 13) 
        {
        	g.fillRect(correction+20+160, 50, 40, 5);
        	g.fillRect(correction+20+160, 95,40,5);
        	g.fillRect(correction+15+160, 55, 5, 40);
        	g.fillRect(correction+60+160, 10, 5, 40);
        	g.fillRect(correction+60+160, 55, 5, 40);
        }else if(Char == 14) 
        {
        	g.fillRect(correction+20+160, 5, 40, 5);
        	g.fillRect(correction+20+160, 50, 40, 5);
        	g.fillRect(correction+20+160, 95,40,5);
        	g.fillRect(correction+15+160, 10, 5, 40);
        	g.fillRect(correction+15+160, 55, 5, 40);
        }else if(Char == 15) 
        {
        	g.fillRect(correction+20+160, 5, 40, 5);
        	g.fillRect(correction+20+160, 50, 40, 5);
        	g.fillRect(correction+15+160, 10, 5, 40);
        	g.fillRect(correction+15+160, 55, 5, 40);
        }    	
    }
    public void paintFourth(Graphics g,int Char) 
    {
    	g.setColor(Color.red);
        if(Char == 0) 
        {
        	g.fillRect(correction+20+240, 5, 40, 5);
        	g.fillRect(correction+20+240, 95,40,5);
        	g.fillRect(correction+15+240, 10, 5, 40);
        	g.fillRect(correction+15+240, 55, 5, 40);
        	g.fillRect(correction+60+240, 10, 5, 40);
        	g.fillRect(correction+60+240, 55, 5, 40);
        }else if(Char == 1) 
        {
        	g.fillRect(correction+60+240, 10, 5, 40);
        	g.fillRect(correction+60+240, 55, 5, 40);
        }else if(Char == 2) 
        {
        	g.fillRect(correction+20+240, 5, 40, 5);
        	g.fillRect(correction+20+240, 50, 40, 5);
        	g.fillRect(correction+20+240, 95,40,5);
        	g.fillRect(correction+15+240, 55, 5, 40);
        	g.fillRect(correction+60+240, 10, 5, 40);
        }else if(Char == 3) 
        {
        	g.fillRect(correction+20+240, 5, 40, 5);
        	g.fillRect(correction+20+240, 50, 40, 5);
        	g.fillRect(correction+20+240, 95,40,5);
        	g.fillRect(correction+60+240, 10, 5, 40);
        	g.fillRect(correction+60+240, 55, 5, 40);
        }else if(Char == 4) 
        {
        	g.fillRect(correction+20+240, 50, 40, 5);
        	g.fillRect(correction+15+240, 10, 5, 40);
        	g.fillRect(correction+60+240, 10, 5, 40);
        	g.fillRect(correction+60+240, 55, 5, 40);
        	
        }else if(Char == 5) 
        {
        	g.fillRect(correction+20+240, 5, 40, 5);
        	g.fillRect(correction+20+240, 50, 40, 5);
        	g.fillRect(correction+20+240, 95,40,5);
        	g.fillRect(correction+15+240, 10, 5, 40);
        	g.fillRect(correction+60+240, 55, 5, 40);
        }else if(Char == 6) 
        {
        	g.fillRect(correction+20+240, 5, 40, 5);
        	g.fillRect(correction+20+240, 50, 40, 5);
        	g.fillRect(correction+20+240, 95,40,5);
        	g.fillRect(correction+15+240, 10, 5, 40);
        	g.fillRect(correction+15+240, 55, 5, 40);
        	g.fillRect(correction+60+240, 55, 5, 40);
        }else if(Char == 7) 
        {
        	g.fillRect(correction+20+240, 5, 40, 5);
        	g.fillRect(correction+60+240, 10, 5, 40);
        	g.fillRect(correction+60+240, 55, 5, 40);
        }else if(Char == 8) 
        {
        	g.fillRect(correction+20+240, 5, 40, 5);
        	g.fillRect(correction+20+240, 50, 40, 5);
        	g.fillRect(correction+20+240, 95,40,5);
        	g.fillRect(correction+15+240, 10, 5, 40);
        	g.fillRect(correction+15+240, 55, 5, 40);
        	g.fillRect(correction+60+240, 10, 5, 40);
        	g.fillRect(correction+60+240, 55, 5, 40);
        }else if(Char == 9) 
        {
        	g.fillRect(correction+20+240, 5, 40, 5);
        	g.fillRect(correction+20+240, 50, 40, 5);
        	g.fillRect(correction+20+240, 95,40,5);
        	g.fillRect(correction+15+240, 10, 5, 40);
        	g.fillRect(correction+60+240, 10, 5, 40);
        	g.fillRect(correction+60+240, 55, 5, 40);
        }else if(Char == 10) 
        {
        	g.fillRect(correction+20+240, 5, 40, 5);
        	g.fillRect(correction+20+240, 50, 40, 5);
        	g.fillRect(correction+15+240, 10, 5, 40);
        	g.fillRect(correction+15+240, 55, 5, 40);
        	g.fillRect(correction+60+240, 10, 5, 40);
        	g.fillRect(correction+60+240, 55, 5, 40);
        }else if(Char == 11) 
        {
        	g.fillRect(correction+20+240, 50, 40, 5);
        	g.fillRect(correction+20+240, 95,40,5);
        	g.fillRect(correction+15+240, 10, 5, 40);
        	g.fillRect(correction+60+240, 10, 5, 40);
        	g.fillRect(correction+60+240, 55, 5, 40);
        }else if(Char == 12) 
        {
        	g.fillRect(correction+20+240, 5, 40, 5);
        	g.fillRect(correction+20+240, 95,40,5);
        	g.fillRect(correction+15+240, 10, 5, 40);
        	g.fillRect(correction+15+240, 55, 5, 40);
        }else if(Char == 13) 
        {
        	g.fillRect(correction+20+240, 50, 40, 5);
        	g.fillRect(correction+20+240, 95,40,5);
        	g.fillRect(correction+15+240, 55, 5, 40);
        	g.fillRect(correction+60+240, 10, 5, 40);
        	g.fillRect(correction+60+240, 55, 5, 40);
        }else if(Char == 14) 
        {
        	g.fillRect(correction+20+240, 5, 40, 5);
        	g.fillRect(correction+20+240, 50, 40, 5);
        	g.fillRect(correction+20+240, 95,40,5);
        	g.fillRect(correction+15+240, 10, 5, 40);
        	g.fillRect(correction+15+240, 55, 5, 40);
        }else if(Char == 15) 
        {
        	g.fillRect(correction+20+240, 5, 40, 5);
        	g.fillRect(correction+20+240, 50, 40, 5);
        	g.fillRect(correction+15+240, 10, 5, 40);
        	g.fillRect(correction+15+240, 55, 5, 40);
        }    	
    }
    

    public Dimension getPreferredSize() {
        return new Dimension(310,105);
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);       
        //g.setColor(Color.RED);
        //g.fillRect(squareX,squareY,squareW,squareH);
        //g.setColor(Color.BLACK);
        //g.drawRect(squareX,squareY,squareW,squareH);
        
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
