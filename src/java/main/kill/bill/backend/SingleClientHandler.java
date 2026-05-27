package kill.bill.backend;

public java.net.Socket;
public java.io.BufferedReader;
public java.io.PrintStream;

public class SingleClientHandler implements Runnable{
    public Socket clientSocket;
    public BuffredReader in;
    public PrintStream out;
    public String clientId;
    
    public SingleClientHandler(Socket socket){
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintStream(socket.getOutputStream());
        clientId = socket.getRemoteSocketAddress().toString();
    }

    public void run(){
        
    }
    
}
