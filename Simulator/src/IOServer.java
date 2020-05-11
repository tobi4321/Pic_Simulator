import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
	
    BufferedWriter bw;
    BufferedReader br;
    OutputStream socketoutstr;
    InputStream socketinstr;
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
					  int a = ctr.getMemory().get_MemoryDIRECT(0x05);
					  int b = ctr.getMemory().get_MemoryDIRECT(0x06);
					
					  this.sendMessage("Test Message");
					  
					  
				      socketinstr = tcpClient.getInputStream(); 
				      isr = new InputStreamReader( socketinstr ); 
				      
				      br = new BufferedReader( isr ); 
				      anfrage = br.readLine();
				      System.out.println("From Client: "+anfrage);
				}
		          sleep(400);
		        }
		        catch(InterruptedException e) {
		        } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
	
	
}
