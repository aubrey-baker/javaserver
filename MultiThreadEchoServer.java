import java.net.*;
import java.io.*;

public class MultiThreadEchoServer {
    public static void main(String[] args) throws IOException {
        
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
        
        int portNumber = Integer.parseInt(args[0]);
        
        try{
            ServerSocket serverSocket =
                new ServerSocket(portNumber);

	    while(true){
	        Socket clientSocket = serverSocket.accept();
	        ClientWorker w=new ClientWorker(clientSocket);
	        Thread t=new Thread(w);
	        t.start();
	    }
        
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}

class ClientWorker implements Runnable {
  private Socket client;

//Constructor
  ClientWorker(Socket client) {
    this.client = client;
  }

  public void run(){
    String line;
    BufferedReader in = null;
    PrintWriter out = null;
    try{
      in = new BufferedReader(new 
        InputStreamReader(client.getInputStream()));
      out = new 
        PrintWriter(client.getOutputStream(), true);
    } catch (IOException e) {
      System.out.println("in or out failed");
      System.exit(-1);
    }

    while(true){
      try{
        line = in.readLine();
        
//Send data back to client
        out.println(line);
       }catch (IOException e) {
        System.out.println("Read failed");
        System.exit(-1);
       }
    }
  }
}
