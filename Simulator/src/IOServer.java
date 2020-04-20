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
	
	Controller ctr;
	
	protected int serverPort;
	
	ServerSocket tcpServer;
	
	Socket tcpClient;
	
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
			// TODO Auto-generated catch block
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
