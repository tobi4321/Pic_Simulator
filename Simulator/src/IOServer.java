import java.net.Socket;
import java.io.IOException;
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
	
	public IOServer(Controller pCtr) 
	{
		this.ctr = pCtr;
	}
	
	protected void startServer() 
	{
		try {
			tcpServer = new ServerSocket(serverPort);
			
			tcpClient = waitForClient();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	protected java.net.Socket waitForClient() throws IOException 
	{
		java.net.Socket socket = tcpServer.accept(); // blockiert, bis sich ein Client angemeldet hat
	 	return socket;
	}
	
	protected void readMessage() 
	{
		
	}
	protected void sendMessage() 
	{
		
	}
	
	
}
