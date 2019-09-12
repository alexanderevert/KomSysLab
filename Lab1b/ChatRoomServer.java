
import java.util.*;
import java.io.*;
import java.net.*;

public class ChatRoomServer{

    public static void main(String[] args) throws IOException{

        if (args.length != 1){
            System.err.println("Usage: java ChatRoomServer <port>");
            System.exit(1);
        }

        int port = Integer.valueOf(args[0]);
        ServerSocket socket = null;
        ArrayList<ClientThread> clientList = new ArrayList<ClientThread>();
        Socket clientSocket = null; 
        PrintWriter out=null;
        BufferedReader in=null;

        try{
            // Bind port 
            try{
                socket = new ServerSocket(port); 

            }catch (IOException e){
                System.err.println("Failed to listen to port: " + args[0]); 
                System.exit(1); 
            }

            // Vänta in klient
            
            System.out.println ("Waiting for connection..");

            try { 
                clientSocket = socket.accept(); 
            } catch (IOException e) { 
                System.err.println("Accept failed."); 
                System.exit(1); 
            } 

            out = new PrintWriter(clientSocket.getOutputStream(), true); 
            in = new BufferedReader(new InputStreamReader( clientSocket.getInputStream())); 
            // inte datain/dataoutputstream?

            // Ny klienttråd
            ClientThread newClient = new ClientThread(clientSocket, "User " + clientList.size(), in, out);
            clientList.add(newClient);
            Thread thread = new Thread(newClient);
            thread.start();

        } catch (IOException e){
            e.printStackTrace();
        } finally{
            if (out != null) out.close(); 
            if (in != null) in.close(); 
            if (clientSocket != null) clientSocket.close(); 
            if (socket != null) socket.close(); 
        }
        
    }


}