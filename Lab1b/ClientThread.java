import java.util.*;
import java.io.*;
import java.net.*;

class ClientThread implements Runnable {
    
    //private Scanner scanner = new Scanner(System.in); 
    private String alias; 
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket; 
      
    public ClientThread(Socket socket, String alias, BufferedReader in, PrintWriter out) { 
        this.in = in; 
        this.out = out; 
        this.alias = alias; 
        this.socket = socket; 
        
    } 

    @Override
    public void run() { 

    }


}
