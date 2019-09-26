import java.util.*;
import java.net.*;
import java.io.*;
public class Call{


  private static String INCOMING_CALL_MENU = "\nINCOMING CALL (INVITE)...\n1. Answer Call\n2. Reject call";
  private static String CALL_IP_QUESTION = "Which IP would you like to connect to?";
  private static String CALL_PORT_QUESTION = "Which port would you like to connect to?";
  private static String START_MENU = "1. Make call\n2. Quit";

  private static String INVITE_MESSAGE = "INVITE";
  private static String TRO_MESSAGE = "TRO";
  private static String ACK_MESSAGE = "ACK";


  public static void main(String[] args){

    ServerSocket serverSocket = null;
    Socket clientSocket = null ;
    BufferedReader in = null;
    PrintWriter out = null;
    String ipAddress = null;
    int port = 5000;

    CallHandler callHandler = new CallHandler(out);

    boolean isServer = true;
    Boolean incomingCall = false;
    Scanner scanner = new Scanner(System.in);
    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    boolean doQuit = false;
    Thread callListenerThread = null;
    Thread messageListenerThread = null;

    PeerMessageListener peerMessageListener = null;

    try{
        // Bind port
        try{
            serverSocket = new ServerSocket(port);

        }catch (IOException e){
            System.err.println("Failed to listen to port: " + 5000);
            System.exit(1);
        }

        IncomingCallListener callListener = new IncomingCallListener(serverSocket, incomingCall, clientSocket);
        peerMessageListener = new PeerMessageListener(serverSocket, clientSocket, isServer);
        messageListenerThread = new Thread(peerMessageListener, out);
        messageListenerThread.start();
        callListenerThread = new Thread(callListener);
        callListenerThread.start();

        while(!doQuit){
          System.out.println(START_MENU);

          while(!doQuit){
            String inSignal = scanner.nextLine();
            inSignal.toLowerCase();

              switch(inSignal){
                case "call":
                    callHandler.processNextEvent(CallEvent.USER_WANTS_TO_INVITE);
                    isServer = false;
                    callListener.setIsServer(isServer);

                break;
                case "answer":
                    callHandler.processNextEvent(CallEvent.TRO);
                    isServer = true;
                    callListener.setIsServer(isServer);
                break;
                case "hangup":
                  callHandler.processNextEvent(CallEvent.USER_WANTS_TO_QUIT);
                break;
                case "quit":
                  doQuit = true;
                break;
                case "reject":
                break;
                default: break;

            }


              /*
              if(callListener.getIncomingCall()){
                handleIncomingCall(serverSocket, clientSocket, in, out, scanner, callListener);
                callListener.incomingCall = false;
                break;

              }else if(br.ready()){
                String menuChoice = br.readLine();
                if(menuChoice.equals("1")){
                  ipAddress = setConnectionDetails(ipAddress, port, br);
                  makeCall(ipAddress, port, clientSocket, out, in);
                } else if(menuChoice.equals("2")){
                  doQuit = true;
                  thread.stop();
                }
                break;
              }
              */
          }

      }
    } catch (IOException e){
        e.printStackTrace();
    } catch (InterruptedException e){
      e.printStackTrace();
    }finally{

      try{
        if(thread != null) thread.stop();
        if(out != null) out.close();
        if(in != null) in.close();
        if (serverSocket != null) serverSocket.close();
        if(br != null) br.close();
      }
      catch(IOException e){
        e.printStackTrace();
        System.exit(1);
      }
      if(scanner != null) scanner.close();

    }

  }

  public static boolean initiateCall(ServerSocket serverSocket, Socket clientSocket, BufferedReader in, PrintWriter out, IncomingCallListener callListener) throws IOException{

      clientSocket = callListener.getClient();
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      String message = in.readLine();
      if(message.equals("INVITE")){
        out.println(TRO_MESSAGE);
      }else{
        return false;
      }

      if(!in.readLine().equals(ACK_MESSAGE)){
          return false;
      }
    return true;

  }


  public static boolean sendInvite(Socket socket, PrintWriter out, BufferedReader stdIn, BufferedReader in) {

      System.out.println(CALL_IP_QUESTION);
      try{
        String peerIpAddress = stdIn.readLine();
        System.out.println(CALL_PORT_QUESTION);
        int peerPort = Integer.parseInt(stdIn.readLine());

        System.out.println("Sending invite: " +  peerIpAddress +  ", port: " + peerPort);

        socket = new Socket(ipAddress, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("Sending invite");
        out.println(INVITE_MESSAGE);
      }catch(IOException e){
        System.out.println("Could not send invite to: " + peerIpAddress + ": " + peerPort);
        return false;
      }

      return true;



      String message = null;
      socket.setSoTimeout(10000);
      try{
        message = in.readLine();

      }catch(SocketTimeoutException e){
        System.out.println("No answer");
        return false;
      }

      if(message.equals(TRO_MESSAGE)){
        System.out.println("TRO received, sending ACK");
        out.println(ACK_MESSAGE);
        return true;
      }

      System.out.println("No TRO received");
      return false;


  }



  private static void handleIncomingCall(ServerSocket serverSocket, Socket clientSocket, BufferedReader in, PrintWriter out, Scanner scanner, IncomingCallListener callListener) throws IOException, InterruptedException{
    System.out.println(INCOMING_CALL_MENU);
    int choice = Integer.parseInt(scanner.nextLine());
    switch(choice){
      case 1:
        if(initiateCall(serverSocket, clientSocket, in, out, callListener)){
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
    public Boolean incomingCall;
    private IncomingCallListener(ServerSocket serverSocket, Boolean incomingCall, Socket clientSocket){
      this.serverSocket = serverSocket;
      this.incomingCall = incomingCall;
      this.clientSocket = clientSocket;
      running = true;
    }

    @Override
    public void run(){
        try{

          while(running){
            //TODO: confirm att det är ett meddelande?
        //    System.out.println("running: " + running);
            if(incomingCall == false){
              clientSocket = serverSocket.accept();
              incomingCall = true;
            }



          }
      }
      catch(SocketException se){
        se.printStackTrace();
      }catch(IOException e){
        e.printStackTrace();


      }finally{
        try{
          if(clientSocket != null){
            clientSocket.close();
          }
        }catch(IOException e){
          e.printStackTrace();
        }

      }

    }
    public Socket getClient(){
        return clientSocket;
    }

    public boolean getIncomingCall() throws InterruptedException{
      Thread.sleep(1);
      return incomingCall;
    }
  }


  private static class PeerMessageListener implements Runnable{
      private ServerSocket serverSocket;
      private Socket clientSocket;
      private boolean isServer;
      private BufferedReader in;
      private boolean inCall;
      private boolean awaitingTroAck;
      private CallHandler callHandler;
      private PrintWriter out;
      private PeerMessageListener(ServerSocket serverSocket, Socket clientSocket, boolean isServer, BufferedReader in, CallHandler callHandler, PrintWriter out){
            this.serverSocket = serverSocket;
            this.clientSocket = clientSocket;
            this.isServer = isServer;
            this.in = in;
            inCall = false;
            this.callHandler = callHandler;
            this.out = out;
      }

      @Override
      public void run(){
            while(true){

                  if(inCall){
                    try{
                      serverSocket.setSoTimeout(300);
                    try{
                      String message = in.readLine();
                      if(message.equals("bye")){
                        callHandler.processNextEvent(CallEvent.BYE, out);
                      }
                      }catch(SocketTimeoutException e){

                      }
                    }catch(IOException e){

                    }
                  }else if(awaitingTroAck){


                  }



            }

        }
      public void setIsServer(boolean isServer){
        this.isServer = isServer;
      }
      public void setInCall(boolean inCall){
        this.inCall = inCall;
      }
      }
}
