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
 * This class is used to connect the simulator to an out sourced software with hardware communication.
 */
public class IOServer extends Thread{
	/// The object of the main Controller class.
	private Controller ctr;
	/// The server port.
	private int serverPort;
	/// The socket object for the server.
	private ServerSocket tcpServer;
	/// The socket object for the client.
	private Socket tcpClient;
	/// The value of port A to send to the client.
	private int raSend;
	/// The value of port B to send to the client.
	private int rbSend;
	/// The received value of port A from the client.
	private int raReceive;
	/// The received value of port B from the client.
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
	
    /**
     * The constructor setting the Controller object, creating a new server socket on a free port and setting the port variable.
     * @param pCtr the Controller object to set.
     */
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
	
	/**
	 * Method to start the server. 
	 * When started the server will wait for a client to connect.
	 * @see waitForClient
	 */
	protected void startServer() 
	{
		try {
			tcpClient = waitForClient();
			System.out.println("Client Connected");
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Wait until a client has connected.
	 * @return The client socket.
	 */
	protected java.net.Socket waitForClient() throws IOException 
	{
		java.net.Socket socket = tcpServer.accept(); // blockiert, bis sich ein Client angemeldet hat
	 	return socket;
	}
	
	/**
	 * The main method of the thread.
	 * As long as an client is connected, the value of port a and port b are exchanged.
	 */
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
				if(tcpClient.isConnected() && !tcpClient.isOutputShutdown() ) 
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
	
	/**
	 * Method to send a message to the client.
	 * @param pMessage The string to send.
	 */
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
	
	/**
	 * Getter to access the serverPort.
	 * @return The serverPort.
	 */
	protected int getServerPort() {
		return serverPort;
	}
	
    /**
     * Getter to access the tcpClient.
	 * @return The tcpClient.
	 */
	protected Socket getTcpClient() {
		return tcpClient;
	}
	
	/**
	 * The Setter to set the server port variable.
	 * @param serverPort The serverPort to set.
	 */
	protected void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	/**
	 * The Setter to set the value of raSend.
	 * @param ra The value to set.
	 */
	protected void setPortA(int ra) {
		this.raSend = ra;
	}

	/**
	 * The Setter to set the value of rbSend.
	 * @param rb The value to set.
	 */
	protected void setPortB(int rb) {
		this.rbSend = rb;
	}
	
	/**
	 * The Getter to get raReceive. 
	 * @return raReceive.
	 */
	protected int getPortA() 
	{
		return this.raReceive;
	}
	
	/**
	 * The Getter to get rbReceive.
	 * @return rbReceive.
	 */
	protected int getPortB() 
	{
		return this.rbReceive;
	}
}
