import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MyPanel extends JPanel{
    private int squareX = 350;
    private int squareY = 50;
    private int squareW = 20;
    private int squareH = 20;
    private int First;
    private int Second;
    private int Third;
    private int Fourth;
   
	public MyPanel() 
	{
		setBorder(BorderFactory.createLineBorder(Color.black));
		addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                moveSquare(e.getX(),e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                moveSquare(e.getX(),e.getY());
            }
        });
        
    }
    
    private void moveSquare(int x, int y) {
        int OFFSET = 1;
        if ((squareX!=x) || (squareY!=y)) {
            repaint(squareX,squareY,squareW+OFFSET,squareH+OFFSET);
            squareX=x;
            squareY=y;
            repaint(squareX,squareY,squareW+OFFSET,squareH+OFFSET);
        } 
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
        	g.fillRect(20, 5, 40, 5);
        	g.fillRect(20, 95,40,5);
        	g.fillRect(15, 10, 5, 40);
        	g.fillRect(15, 55, 5, 40);
        	g.fillRect(60, 10, 5, 40);
        	g.fillRect(60, 55, 5, 40);
        }else if(Char == 1) 
        {
        	g.fillRect(60, 10, 5, 40);
        	g.fillRect(60, 55, 5, 40);
        }else if(Char == 2) 
        {
        	g.fillRect(20, 5, 40, 5);
        	g.fillRect(20, 50, 40, 5);
        	g.fillRect(20, 95,40,5);
        	g.fillRect(15, 55, 5, 40);
        	g.fillRect(60, 10, 5, 40);
        }else if(Char == 3) 
        {
        	g.fillRect(20, 5, 40, 5);
        	g.fillRect(20, 50, 40, 5);
        	g.fillRect(20, 95,40,5);
        	g.fillRect(60, 10, 5, 40);
        	g.fillRect(60, 55, 5, 40);
        }else if(Char == 4) 
        {
        	g.fillRect(20, 50, 40, 5);
        	g.fillRect(15, 10, 5, 40);
        	g.fillRect(60, 10, 5, 40);
        	g.fillRect(60, 55, 5, 40);
        	
        }else if(Char == 5) 
        {
        	g.fillRect(20, 5, 40, 5);
        	g.fillRect(20, 50, 40, 5);
        	g.fillRect(20, 95,40,5);
        	g.fillRect(15, 10, 5, 40);
        	g.fillRect(60, 55, 5, 40);
        }else if(Char == 6) 
        {
        	g.fillRect(20, 5, 40, 5);
        	g.fillRect(20, 50, 40, 5);
        	g.fillRect(20, 95,40,5);
        	g.fillRect(15, 10, 5, 40);
        	g.fillRect(15, 55, 5, 40);
        	g.fillRect(60, 55, 5, 40);
        }else if(Char == 7) 
        {
        	g.fillRect(20, 5, 40, 5);
        	g.fillRect(60, 10, 5, 40);
        	g.fillRect(60, 55, 5, 40);
        }else if(Char == 8) 
        {
        	g.fillRect(20, 5, 40, 5);
        	g.fillRect(20, 50, 40, 5);
        	g.fillRect(20, 95,40,5);
        	g.fillRect(15, 10, 5, 40);
        	g.fillRect(15, 55, 5, 40);
        	g.fillRect(60, 10, 5, 40);
        	g.fillRect(60, 55, 5, 40);
        }else if(Char == 9) 
        {
        	g.fillRect(20, 5, 40, 5);
        	g.fillRect(20, 50, 40, 5);
        	g.fillRect(20, 95,40,5);
        	g.fillRect(15, 10, 5, 40);
        	g.fillRect(60, 10, 5, 40);
        	g.fillRect(60, 55, 5, 40);
        }else if(Char == 10) 
        {
        	g.fillRect(20, 5, 40, 5);
        	g.fillRect(20, 50, 40, 5);
        	g.fillRect(15, 10, 5, 40);
        	g.fillRect(15, 55, 5, 40);
        	g.fillRect(60, 10, 5, 40);
        	g.fillRect(60, 55, 5, 40);
        }else if(Char == 11) 
        {
        	g.fillRect(20, 50, 40, 5);
        	g.fillRect(20, 95,40,5);
        	g.fillRect(15, 10, 5, 40);
        	g.fillRect(60, 10, 5, 40);
        	g.fillRect(60, 55, 5, 40);
        }else if(Char == 12) 
        {
        	g.fillRect(20, 5, 40, 5);
        	g.fillRect(20, 95,40,5);
        	g.fillRect(15, 10, 5, 40);
        	g.fillRect(15, 55, 5, 40);
        }else if(Char == 13) 
        {
        	g.fillRect(20, 50, 40, 5);
        	g.fillRect(20, 95,40,5);
        	g.fillRect(15, 55, 5, 40);
        	g.fillRect(60, 10, 5, 40);
        	g.fillRect(60, 55, 5, 40);
        }else if(Char == 14) 
        {
        	g.fillRect(20, 5, 40, 5);
        	g.fillRect(20, 50, 40, 5);
        	g.fillRect(20, 95,40,5);
        	g.fillRect(15, 10, 5, 40);
        	g.fillRect(15, 55, 5, 40);
        }else if(Char == 15) 
        {
        	g.fillRect(20, 5, 40, 5);
        	g.fillRect(20, 50, 40, 5);
        	g.fillRect(15, 10, 5, 40);
        	g.fillRect(15, 55, 5, 40);
        }
    }
    public void paintSecond(Graphics g,int Char) 
    {
    	g.setColor(Color.red);
        if(Char == 0) 
        {
        	g.fillRect(20+80, 5, 40, 5);
        	g.fillRect(20+80, 95,40,5);
        	g.fillRect(15+80, 10, 5, 40);
        	g.fillRect(15+80, 55, 5, 40);
        	g.fillRect(60+80, 10, 5, 40);
        	g.fillRect(60+80, 55, 5, 40);
        }else if(Char == 1) 
        {
        	g.fillRect(60+80, 10, 5, 40);
        	g.fillRect(60+80, 55, 5, 40);
        }else if(Char == 2) 
        {
        	g.fillRect(20+80, 5, 40, 5);
        	g.fillRect(20+80, 50, 40, 5);
        	g.fillRect(20+80, 95,40,5);
        	g.fillRect(15+80, 55, 5, 40);
        	g.fillRect(60+80, 10, 5, 40);
        }else if(Char == 3) 
        {
        	g.fillRect(20+80, 5, 40, 5);
        	g.fillRect(20+80, 50, 40, 5);
        	g.fillRect(20+80, 95,40,5);
        	g.fillRect(60+80, 10, 5, 40);
        	g.fillRect(60+80, 55, 5, 40);
        }else if(Char == 4) 
        {
        	g.fillRect(20+80, 50, 40, 5);
        	g.fillRect(15+80, 10, 5, 40);
        	g.fillRect(60+80, 10, 5, 40);
        	g.fillRect(60+80, 55, 5, 40);
        	
        }else if(Char == 5) 
        {
        	g.fillRect(20+80, 5, 40, 5);
        	g.fillRect(20+80, 50, 40, 5);
        	g.fillRect(20+80, 95,40,5);
        	g.fillRect(15+80, 10, 5, 40);
        	g.fillRect(60+80, 55, 5, 40);
        }else if(Char == 6) 
        {
        	g.fillRect(20+80, 5, 40, 5);
        	g.fillRect(20+80, 50, 40, 5);
        	g.fillRect(20+80, 95,40,5);
        	g.fillRect(15+80, 10, 5, 40);
        	g.fillRect(15+80, 55, 5, 40);
        	g.fillRect(60+80, 55, 5, 40);
        }else if(Char == 7) 
        {
        	g.fillRect(20+80, 5, 40, 5);
        	g.fillRect(60+80, 10, 5, 40);
        	g.fillRect(60+80, 55, 5, 40);
        }else if(Char == 8) 
        {
        	g.fillRect(20+80, 5, 40, 5);
        	g.fillRect(20+80, 50, 40, 5);
        	g.fillRect(20+80, 95,40,5);
        	g.fillRect(15+80, 10, 5, 40);
        	g.fillRect(15+80, 55, 5, 40);
        	g.fillRect(60+80, 10, 5, 40);
        	g.fillRect(60+80, 55, 5, 40);
        }else if(Char == 9) 
        {
        	g.fillRect(20+80, 5, 40, 5);
        	g.fillRect(20+80, 50, 40, 5);
        	g.fillRect(20+80, 95,40,5);
        	g.fillRect(1+805, 10, 5, 40);
        	g.fillRect(60+80, 10, 5, 40);
        	g.fillRect(60+80, 55, 5, 40);
        }else if(Char == 10) 
        {
        	g.fillRect(20+80, 5, 40, 5);
        	g.fillRect(20+80, 50, 40, 5);
        	g.fillRect(15+80, 10, 5, 40);
        	g.fillRect(15+80, 55, 5, 40);
        	g.fillRect(60+80, 10, 5, 40);
        	g.fillRect(60+80, 55, 5, 40);
        }else if(Char == 11) 
        {
        	g.fillRect(20+80, 50, 40, 5);
        	g.fillRect(20+80, 95,40,5);
        	g.fillRect(15+80, 10, 5, 40);
        	g.fillRect(60+80, 10, 5, 40);
        	g.fillRect(60+80, 55, 5, 40);
        }else if(Char == 12) 
        {
        	g.fillRect(20+80, 5, 40, 5);
        	g.fillRect(20+80, 95,40,5);
        	g.fillRect(15+80, 10, 5, 40);
        	g.fillRect(15+80, 55, 5, 40);
        }else if(Char == 13) 
        {
        	g.fillRect(20+80, 50, 40, 5);
        	g.fillRect(20+80, 95,40,5);
        	g.fillRect(15+80, 55, 5, 40);
        	g.fillRect(60+80, 10, 5, 40);
        	g.fillRect(60+80, 55, 5, 40);
        }else if(Char == 14) 
        {
        	g.fillRect(20+80, 5, 40, 5);
        	g.fillRect(20+80, 50, 40, 5);
        	g.fillRect(20+80, 95,40,5);
        	g.fillRect(15+80, 10, 5, 40);
        	g.fillRect(15+80, 55, 5, 40);
        }else if(Char == 15) 
        {
        	g.fillRect(20+80, 5, 40, 5);
        	g.fillRect(20+80, 50, 40, 5);
        	g.fillRect(15+80, 10, 5, 40);
        	g.fillRect(15+80, 55, 5, 40);
        }    	
    }
    public void paintThird(Graphics g,int Char) 
    {
    	g.setColor(Color.red);
        if(Char == 0) 
        {
        	g.fillRect(20+160, 5, 40, 5);
        	g.fillRect(20+160, 95,40,5);
        	g.fillRect(15+160, 10, 5, 40);
        	g.fillRect(15+160, 55, 5, 40);
        	g.fillRect(60+160, 10, 5, 40);
        	g.fillRect(60+160, 55, 5, 40);
        }else if(Char == 1) 
        {
        	g.fillRect(60+160, 10, 5, 40);
        	g.fillRect(60+160, 55, 5, 40);
        }else if(Char == 2) 
        {
        	g.fillRect(20+160, 5, 40, 5);
        	g.fillRect(20+160, 50, 40, 5);
        	g.fillRect(20+160, 95,40,5);
        	g.fillRect(15+160, 55, 5, 40);
        	g.fillRect(60+160, 10, 5, 40);
        }else if(Char == 3) 
        {
        	g.fillRect(20+160, 5, 40, 5);
        	g.fillRect(20+160, 50, 40, 5);
        	g.fillRect(20+160, 95,40,5);
        	g.fillRect(60+160, 10, 5, 40);
        	g.fillRect(60+160, 55, 5, 40);
        }else if(Char == 4) 
        {
        	g.fillRect(20+160, 50, 40, 5);
        	g.fillRect(15+160, 10, 5, 40);
        	g.fillRect(60+160, 10, 5, 40);
        	g.fillRect(60+160, 55, 5, 40);
        	
        }else if(Char == 5) 
        {
        	g.fillRect(20+160, 5, 40, 5);
        	g.fillRect(20+160, 50, 40, 5);
        	g.fillRect(20+160, 95,40,5);
        	g.fillRect(15+160, 10, 5, 40);
        	g.fillRect(60+160, 55, 5, 40);
        }else if(Char == 6) 
        {
        	g.fillRect(20+160, 5, 40, 5);
        	g.fillRect(20+160, 50, 40, 5);
        	g.fillRect(20+160, 95,40,5);
        	g.fillRect(15+160, 10, 5, 40);
        	g.fillRect(15+160, 55, 5, 40);
        	g.fillRect(60+160, 55, 5, 40);
        }else if(Char == 7) 
        {
        	g.fillRect(20+160, 5, 40, 5);
        	g.fillRect(60+160, 10, 5, 40);
        	g.fillRect(60+160, 55, 5, 40);
        }else if(Char == 8) 
        {
        	g.fillRect(20+160, 5, 40, 5);
        	g.fillRect(20+160, 50, 40, 5);
        	g.fillRect(20+160, 95,40,5);
        	g.fillRect(15+160, 10, 5, 40);
        	g.fillRect(15+160, 55, 5, 40);
        	g.fillRect(60+160, 10, 5, 40);
        	g.fillRect(60+160, 55, 5, 40);
        }else if(Char == 9) 
        {
        	g.fillRect(20+160, 5, 40, 5);
        	g.fillRect(20+160, 50, 40, 5);
        	g.fillRect(20+160, 95,40,5);
        	g.fillRect(15+160, 10, 5, 40);
        	g.fillRect(60+160, 10, 5, 40);
        	g.fillRect(60+160, 55, 5, 40);
        }else if(Char == 10) 
        {
        	g.fillRect(20+160, 5, 40, 5);
        	g.fillRect(20+160, 50, 40, 5);
        	g.fillRect(15+160, 10, 5, 40);
        	g.fillRect(15+160, 55, 5, 40);
        	g.fillRect(60+160, 10, 5, 40);
        	g.fillRect(60+160, 55, 5, 40);
        }else if(Char == 11) 
        {
        	g.fillRect(20+160, 50, 40, 5);
        	g.fillRect(20+160, 95,40,5);
        	g.fillRect(15+160, 10, 5, 40);
        	g.fillRect(60+160, 10, 5, 40);
        	g.fillRect(60+160, 55, 5, 40);
        }else if(Char == 12) 
        {
        	g.fillRect(20+160, 5, 40, 5);
        	g.fillRect(20+160, 95,40,5);
        	g.fillRect(15+160, 10, 5, 40);
        	g.fillRect(15+160, 55, 5, 40);
        }else if(Char == 13) 
        {
        	g.fillRect(20+160, 50, 40, 5);
        	g.fillRect(20+160, 95,40,5);
        	g.fillRect(15+160, 55, 5, 40);
        	g.fillRect(60+160, 10, 5, 40);
        	g.fillRect(60+160, 55, 5, 40);
        }else if(Char == 14) 
        {
        	g.fillRect(20+160, 5, 40, 5);
        	g.fillRect(20+160, 50, 40, 5);
        	g.fillRect(20+160, 95,40,5);
        	g.fillRect(15+160, 10, 5, 40);
        	g.fillRect(15+160, 55, 5, 40);
        }else if(Char == 15) 
        {
        	g.fillRect(20+160, 5, 40, 5);
        	g.fillRect(20+160, 50, 40, 5);
        	g.fillRect(15+160, 10, 5, 40);
        	g.fillRect(15+160, 55, 5, 40);
        }    	
    }
    public void paintFourth(Graphics g,int Char) 
    {
    	g.setColor(Color.red);
        if(Char == 0) 
        {
        	g.fillRect(20+240, 5, 40, 5);
        	g.fillRect(20+240, 95,40,5);
        	g.fillRect(15+240, 10, 5, 40);
        	g.fillRect(15+240, 55, 5, 40);
        	g.fillRect(60+240, 10, 5, 40);
        	g.fillRect(60+240, 55, 5, 40);
        }else if(Char == 1) 
        {
        	g.fillRect(60+240, 10, 5, 40);
        	g.fillRect(60+240, 55, 5, 40);
        }else if(Char == 2) 
        {
        	g.fillRect(20+240, 5, 40, 5);
        	g.fillRect(20+240, 50, 40, 5);
        	g.fillRect(20+240, 95,40,5);
        	g.fillRect(15+240, 55, 5, 40);
        	g.fillRect(60+240, 10, 5, 40);
        }else if(Char == 3) 
        {
        	g.fillRect(20+240, 5, 40, 5);
        	g.fillRect(20+240, 50, 40, 5);
        	g.fillRect(20+240, 95,40,5);
        	g.fillRect(60+240, 10, 5, 40);
        	g.fillRect(60+240, 55, 5, 40);
        }else if(Char == 4) 
        {
        	g.fillRect(20+240, 50, 40, 5);
        	g.fillRect(15+240, 10, 5, 40);
        	g.fillRect(60+240, 10, 5, 40);
        	g.fillRect(60+240, 55, 5, 40);
        	
        }else if(Char == 5) 
        {
        	g.fillRect(20+240, 5, 40, 5);
        	g.fillRect(20+240, 50, 40, 5);
        	g.fillRect(20+240, 95,40,5);
        	g.fillRect(15+240, 10, 5, 40);
        	g.fillRect(60+240, 55, 5, 40);
        }else if(Char == 6) 
        {
        	g.fillRect(20+240, 5, 40, 5);
        	g.fillRect(20+240, 50, 40, 5);
        	g.fillRect(20+240, 95,40,5);
        	g.fillRect(15+240, 10, 5, 40);
        	g.fillRect(15+240, 55, 5, 40);
        	g.fillRect(60+240, 55, 5, 40);
        }else if(Char == 7) 
        {
        	g.fillRect(20+240, 5, 40, 5);
        	g.fillRect(60+240, 10, 5, 40);
        	g.fillRect(60+240, 55, 5, 40);
        }else if(Char == 8) 
        {
        	g.fillRect(20+240, 5, 40, 5);
        	g.fillRect(20+240, 50, 40, 5);
        	g.fillRect(20+240, 95,40,5);
        	g.fillRect(15+240, 10, 5, 40);
        	g.fillRect(15+240, 55, 5, 40);
        	g.fillRect(60+240, 10, 5, 40);
        	g.fillRect(60+240, 55, 5, 40);
        }else if(Char == 9) 
        {
        	g.fillRect(20+240, 5, 40, 5);
        	g.fillRect(20+240, 50, 40, 5);
        	g.fillRect(20+240, 95,40,5);
        	g.fillRect(15+240, 10, 5, 40);
        	g.fillRect(60+240, 10, 5, 40);
        	g.fillRect(60+240, 55, 5, 40);
        }else if(Char == 10) 
        {
        	g.fillRect(20+240, 5, 40, 5);
        	g.fillRect(20+240, 50, 40, 5);
        	g.fillRect(15+240, 10, 5, 40);
        	g.fillRect(15+240, 55, 5, 40);
        	g.fillRect(60+240, 10, 5, 40);
        	g.fillRect(60+240, 55, 5, 40);
        }else if(Char == 11) 
        {
        	g.fillRect(20+240, 50, 40, 5);
        	g.fillRect(20+240, 95,40,5);
        	g.fillRect(15+240, 10, 5, 40);
        	g.fillRect(60+240, 10, 5, 40);
        	g.fillRect(60+240, 55, 5, 40);
        }else if(Char == 12) 
        {
        	g.fillRect(20+240, 5, 40, 5);
        	g.fillRect(20+240, 95,40,5);
        	g.fillRect(15+240, 10, 5, 40);
        	g.fillRect(15+240, 55, 5, 40);
        }else if(Char == 13) 
        {
        	g.fillRect(20+240, 50, 40, 5);
        	g.fillRect(20+240, 95,40,5);
        	g.fillRect(15+240, 55, 5, 40);
        	g.fillRect(60+240, 10, 5, 40);
        	g.fillRect(60+240, 55, 5, 40);
        }else if(Char == 14) 
        {
        	g.fillRect(20+240, 5, 40, 5);
        	g.fillRect(20+240, 50, 40, 5);
        	g.fillRect(20+240, 95,40,5);
        	g.fillRect(15+240, 10, 5, 40);
        	g.fillRect(15+240, 55, 5, 40);
        }else if(Char == 15) 
        {
        	g.fillRect(20+240, 5, 40, 5);
        	g.fillRect(20+240, 50, 40, 5);
        	g.fillRect(15+240, 10, 5, 40);
        	g.fillRect(15+240, 55, 5, 40);
        }    	
    }
    

    public Dimension getPreferredSize() {
        return new Dimension(240,200);
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);       
        //g.setColor(Color.RED);
        //g.fillRect(squareX,squareY,squareW,squareH);
        //g.setColor(Color.BLACK);
        //g.drawRect(squareX,squareY,squareW,squareH);
        
        g.drawRect(20, 5,40,5);
        g.drawRect(20, 50,40,5);
        g.drawRect(20, 95,40,5);
        g.drawRect(15, 10, 5, 40);
        g.drawRect(15, 55, 5, 40);
        g.drawRect(60, 10, 5, 40);
        g.drawRect(60, 55, 5, 40);
        
        g.drawRect(20+80, 5,40,5);
        g.drawRect(20+80, 50,40,5);
        g.drawRect(20+80, 95,40,5);
        g.drawRect(15+80, 10, 5, 40);
        g.drawRect(15+80, 55, 5, 40);
        g.drawRect(60+80, 10, 5, 40);
        g.drawRect(60+80, 55, 5, 40);
        
        g.drawRect(20+160, 5,40,5);
        g.drawRect(20+160, 50,40,5);
        g.drawRect(20+160, 95,40,5);
        g.drawRect(15+160, 10, 5, 40);
        g.drawRect(15+160, 55, 5, 40);
        g.drawRect(60+160, 10, 5, 40);
        g.drawRect(60+160, 55, 5, 40);
        
        g.drawRect(20+240, 5,40,5);
        g.drawRect(20+240, 50,40,5);
        g.drawRect(20+240, 95,40,5);
        g.drawRect(15+240, 10, 5, 40);
        g.drawRect(15+240, 55, 5, 40);
        g.drawRect(60+240, 10, 5, 40);
        g.drawRect(60+240, 55, 5, 40);
        
        paintFirst(g,this.First);
        paintSecond(g,this.Second);
        paintThird(g,this.Third);
        paintFourth(g,this.Fourth);
    }  
	


}
