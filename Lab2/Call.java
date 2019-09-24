import java.util.*;
import java.net.*;
import java.io.*;
public class Call{


  private static String INCOMING_CALL_MENU = "INCOMING CALL... \n1. Answer Call\n 2. Reject call";
  private static String CALL_IP_QUESTION = "Which IP would you like to connect to?";
  private static String CALL_PORT_QUESTION = "Which port would you like to connect to?";

  private static String TRO_MESSAGE = "TRO";

  public static void main(String[] args){

    CallHandler callHandler = new CallHandler();

    ServerSocket serverSocket = null;
    Socket clientSocket = null ;
    BufferedReader in = null;
    PrintWriter out = null;

    String ipAddress = null;
    int port = 5000;
    boolean incomingCall = false;
    Scanner scanner = new Scanner(System.in);
    try{
        // Bind port
        try{
            serverSocket = new ServerSocket(port);

        }catch (IOException e){
            System.err.println("Failed to listen to port: " + args[0]);
            System.exit(1);
        }

        // Vänta in klient
        System.out.println ("Waiting for connection..");

       IncomingCallListener callListener = new IncomingCallListener(serverSocket, incomingCall, clientSocket);
       Thread thread = new Thread(callListener);
        thread.start();
        while(true){
          System.out.println("menu");

          while(true){
            System.out.println("inc" + incomingCall);
              if(incomingCall == true){
                System.out.println("hallå");
                handleIncomingCall(serverSocket, clientSocket, in, out, scanner);
                callListener.incomingCall = false;
                break;
            /*  }else if(scanner.hasNext()){


                break;*/
              }
          }

      }
    } catch (IOException e){
        e.printStackTrace();
    } finally{
      try{
        if(out != null) out.close();
        if(in != null) in.close();
        if (serverSocket != null) serverSocket.close();
      }
      catch(IOException e){
        e.printStackTrace();
        System.exit(1);
      }
      if(scanner != null) scanner.close();
    }

  }

  public static boolean initiateCall(ServerSocket serverSocket, Socket clientSocket, BufferedReader in, PrintWriter out) throws IOException{
//      clientSocket = serverSocket.accept();
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      out.println(TRO_MESSAGE);

      //TODO: sätt en timeout

      if(!in.readLine().equals("ack")){
          return false;
      }

    return true;

  }
  private static void handleIncomingCall(ServerSocket serverSocket, Socket clientSocket, BufferedReader in, PrintWriter out, Scanner scanner) throws IOException{
    System.out.println(INCOMING_CALL_MENU);
    int choice = Integer.parseInt(scanner.nextLine());
    switch(choice){
      case 1:
        if(initiateCall(serverSocket, clientSocket, in, out)){
            System.out.println("succesful");
        }else{
          //TODO: fixa vad som ska hända ifall misslyckad TRO
          System.out.println("ERROR");
        }
      break;
      case 2:
      break;
    }
  }

  private static class IncomingCallListener implements Runnable{
    public boolean running;
    public ServerSocket serverSocket;
    public Socket clientSocket;
    public boolean incomingCall;
    private IncomingCallListener(ServerSocket serverSocket, boolean incomingCall, Socket clientSocket){
      this.serverSocket = serverSocket;
      this.incomingCall = incomingCall;
      this.clientSocket = clientSocket;
      running = true;
    }

    @Override
    public void run(){
        try{

          while(running){
            clientSocket = serverSocket.accept();
            //TODO: confirm att det är ett meddelande?
            System.out.println(running);
            incomingCall = true;
          }
      }catch(IOException e){
        e.printStackTrace();
      }


    }
  }
  /*public void setConnectionDetails() throws IOException{
    System.out.println(CALL_IP_QUESTION);
    ipAddress = scanner.nextLine();
    System.out.println(CALL_PORT_QUESTION);
    port = Integer.parseInt(scanner.nextLine());

  }*/

}
