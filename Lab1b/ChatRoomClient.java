import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class ChatRoomClient{


    public static void main(String[] args) throws IOException{

 		 if (args.length != 2){
        	    System.err.println("Usage: java ChatRoomServer <port>, host <ip.addr>");
            	System.exit(1);
        	}

		 String hostName = args[1];
		 int port = Integer.parseInt(args[0]);
		 Socket socket = null;
		 PrintWriter out = null;
		 BufferedReader in = null;
		 try{
			 socket = new Socket(hostName, port);
			 out = new PrintWriter(socket.getOutputStream(), true);
			 in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 BufferedReader stdIn = new BufferedReader(
                                   new InputStreamReader(System.in));
      		String userInput = null;
			String lineIn = null;
			boolean disconnect = false;
      MessageListener listener = new MessageListener(in);
      Thread thread = new Thread(listener);
      thread.start();

		 	while(!disconnect){
				if(stdIn.ready()){
					userInput = stdIn.readLine();
					if(userInput.equals("/quit")) {
						disconnect = true;
            listener.running = false;
						out.println(userInput);
						try{
							TimeUnit.SECONDS.sleep(1);
						} catch(InterruptedException e){
							e.printStackTrace();
						}
					}else{
						out.println(userInput);
					}
		     }
    }
		 }catch(UnknownHostException e){
			System.err.println("ServerHostName error: " + hostName);
			System.exit(1);
		 }catch(SocketException e){
      e.printStackTrace();
			System.exit(1);
		}catch(IOException io){
			 System.err.println("IOException");
			 System.exit(1);
		 }finally{
			 if(socket != null) socket.close();
			 if(out != null) out.close();
			 if(in != null) in.close();
		 }



	}

  private static class MessageListener implements Runnable{
    private BufferedReader in;
    public boolean running;
    private MessageListener(BufferedReader in){
      this.in = in;
      running = true;
    }

    @Override
    public void run(){
        try{


        while(running){

          if(in.ready()){
            System.out.println();
            System.out.println(in.readLine());
          }
        }
      }catch(IOException e){
        e.printStackTrace();
      }


    }
  }
}
