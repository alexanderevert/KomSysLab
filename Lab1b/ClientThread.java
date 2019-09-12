import java.util.*;
import java.io.*;
import java.net.*;

class ClientThread implements Runnable{

    private final String WELCOME_MESSAGE = "Welcome";
    //private Scanner scanner = new Scanner(System.in);
    private String alias;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public ClientThread(Socket socket, String alias, BufferedReader in, PrintWriter out) {
        this.alias = alias;
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    @Override
    	public void run(){

        try{
            out.println(WELCOME_MESSAGE);

        }catch(Exception e){
          System.err.println("IO Exception");
          System.exit(1);
    }finally{
      try{
          out.close();
          in.close();
          socket.close();
      }catch(IOException e){
        e.printStackTrace();
      }
    }


  }
}
