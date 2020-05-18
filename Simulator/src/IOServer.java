import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;


/// class IOServer
/**
 *  used to connect simulator to a outsourced software with hardware communication 
 * 
 * 
 */
public class IOServer extends Thread{
	
	private Controller ctr;
	
	private int serverPort;
	
	private ServerSocket tcpServer;
	
	private Socket tcpClient;
	
	private int raSend;
	private int rbSend;
	
	private int raReceive;
	private int rbReceive;

	BufferedWriter bw;
    BufferedReader br;
    OutputStream socketoutstr;
    InputStream socketinstr;
    
    DataInputStream inFromClient;
    DataOutputStream outToClient;
    
    InputStreamReader isr;
    OutputStreamWriter osr;
    
    String anfrage; 
    String antwort;
	
	public IOServer(Controller pCtr) 
	{
		this.ctr = pCtr;
		try {
			tcpServer = new ServerSocket(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		setServerPort(tcpServer.getLocalPort());

		this.start();
	}
	
	protected void startServer() 
	{
		try {

			
			tcpClient = waitForClient();
			System.out.println("Client Connected");
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	protected java.net.Socket waitForClient() throws IOException 
	{
		java.net.Socket socket = tcpServer.accept(); // blockiert, bis sich ein Client angemeldet hat
	 	return socket;
	}
	

	/**
	 * @return the serverPort
	 */
	protected int getServerPort() {
		return serverPort;
	}
	   /**
		 * @return the tcpClient
		 */
		protected Socket getTcpClient() {
			return tcpClient;
		}
	/**
	 * @param serverPort the serverPort to set
	 */
	protected void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	@Override
	public void run() 
	{
		
		while(this.isAlive()) 
		{
			if(tcpClient != null) 
			{
				if(tcpClient.isClosed()) 
				{
					tcpClient = null;
					this.startServer();
				}
			}else 
			{
				this.startServer();
			}

			try {
				if(!tcpClient.isOutputShutdown()) 
				{

					
					  this.sendMessage(raSend+":"+rbSend);
					  
				      socketinstr = tcpClient.getInputStream();
				      isr = new InputStreamReader( socketinstr ); 
				      br = new BufferedReader( isr ); 
				      anfrage = br.readLine();
				      String[] receive = anfrage.split(":");
				      this.raReceive = Integer.parseInt(receive[0]);
				      this.rbReceive = Integer.parseInt(receive[1]);
				      System.out.println("From Client: "+anfrage);

				}

				  			
		        sleep(20);
		     }
		     catch(InterruptedException | IOException e) {
		    	 System.out.println("Error: "+e.getMessage());
		     }
		}    
	}
	
	public void sendMessage(String pMessage) 
	{

		try {
			socketoutstr = tcpClient.getOutputStream();
		      osr = new OutputStreamWriter( socketoutstr ); 
		      bw = new BufferedWriter( osr ); 
		      antwort = pMessage; 
		      bw.write(antwort); 
		      bw.newLine(); 
		      bw.flush(); 
		      
		} catch (IOException e) {
			e.printStackTrace();
		}   
	}

	protected void setPortA(int ra) {
		this.raSend = ra;
	}

	protected void setPortB(int rb) {
		this.rbSend = rb;
	}
	protected int getPortA() 
	{
		return this.raReceive;
	}
	protected int getPortB() 
	{
		return this.rbReceive;
	}
	
	
}
