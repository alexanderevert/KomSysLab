import java.io.*;
import java.util.*;
import java.net.*;

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
		 String lineIn;
		 try{
			 socket = new Socket(hostName, port);
			 out = new PrintWriter(socket.getOutputStream(), true);
			 in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			 lineIn = in.readLine();
		 		System.out.println(lineIn);



		 }catch(UnknownHostException e){
					System.err.println("ServerHostName error: " + hostName);
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
}
