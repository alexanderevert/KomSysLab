import java.io.*;
import java.util.*;
import java.net.*;

public class ChatRoomServer{

    public static void main(String[] args) throws IOException{

        if (args.length != 1){
            System.err.println("Usage: java ChatRoomServer <port>");
            System.exit(1);
        }

        String port = args[0];
        SocketServer socket = null;
        ArrayList<ClientThread> clientList = new ArrayList<ClientThread>();

        try{
            // Bind port 
            try{
                socket = new ServerSocket(port); 

            }catch (IOException e){
                System.err.println("Failed to listen to port: " + args[0]); 
                System.exit(1); 
            }

            // Vänta in klient
            Socket clientSocket = null; 
            System.out.println ("Waiting for connection..");

            try { 
                clientSocket = serverSocket.accept(); 
            } catch (IOException e) { 
                System.err.println("Accept failed."); 
                System.exit(1); 
            } 

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); 
            BufferedReader in = new BufferedReader(new InputStreamReader( clientSocket.getInputStream())); 
            // inte datain/dataoutputstream?

            // Ny klienttråd
            ClientThread newClient = new ClientThread(clientSocket, "User " + clientList.length(), in, out);
            clientList.add(newClient);
            Thread thread = new Thread(newClient);
            tread.start();

        } catch (IOException e){
            e.printStackTrace();
        } finally{
            if (out != null) out.close(); 
            if (in != null) in.close(); 
            if (clientSocket != null) clientSocket.close(); 
            if (serverSocket != null) serverSocket.close(); 
        }
        
    }


}