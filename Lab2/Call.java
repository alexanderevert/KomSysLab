import java.util.*;
import java.net.*;
import java.io.*;
public class Call{


  private static String INCOMING_CALL_MENU = "\n INCOMING CALL(INVITE)...\n Answer / Reject?";
  private static String CALL_IP_QUESTION = "Which IP would you like to connect to?";
  private static String CALL_PORT_QUESTION = "Which port would you like to connect to?";
  private static String START_MENU = "*************************\n 1. Make call(call)\n 2. Answer call(answer)\n 3. Reject call(reject)\n 4 .Hang up(hangup)\n 5 .Quit(quit)\n*************************";

  private static String INVITE_MESSAGE = "INVITE";
  private static String TRO_MESSAGE = "TRO";
  private static String ACK_MESSAGE = "ACK";


  public static void main(String[] args){

    if (args.length != 1) {
      System.err.println(
          "Usage: java Call <port number>");
      System.exit(1);
  }

    ServerSocket serverSocket = null;
    Socket clientSocket = null ;
    BufferedReader in = null;
    PrintWriter out = null;
    String ipAddress = null;
    int port = Integer.parseInt(args[0]);

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

        IncomingCallListener callListener = new IncomingCallListener(callHandler, serverSocket, incomingCall, clientSocket);
        peerMessageListener = new PeerMessageListener(serverSocket, clientSocket, isServer, in, callHandler, out);
        messageListenerThread = new Thread(peerMessageListener);
        messageListenerThread.start();
        callListenerThread = new Thread(callListener);
        callListenerThread.start();

        while(!doQuit){
          System.out.println(START_MENU);

          while(!doQuit){
            String inSignal = scanner.nextLine(); 
            String callArr[] = inSignal.split(" ", 3);
            inSignal = callArr[0];
            inSignal.toLowerCase();
              switch(inSignal){
                case "call":
                    
                    clientSocket = new Socket(callArr[1], Integer.parseInt(callArr[2]));
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    callHandler.setOutPw(out);
                    callHandler.processNextEvent(CallHandler.CallEvent.USER_WANTS_TO_INVITE);
                    isServer = false;
                    peerMessageListener.setIsServer(isServer);

                break;
                case "answer":
                    // if incommingCall==true?
                    callHandler.processNextEvent(CallHandler.CallEvent.TRO);
                    isServer = true;
                    peerMessageListener.setIsServer(isServer);
                break;
                case "hangup":
                    callHandler.processNextEvent(CallHandler.CallEvent.USER_WANTS_TO_QUIT);
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
    } catch (Exception e){
      e.printStackTrace();
    }/*catch (IOException e){
        e.printStackTrace();
    } catch (InterruptedException e){
      e.printStackTrace();
    }*/finally{

      try{
        if(callListenerThread != null) callListenerThread.stop();
        if(messageListenerThread != null) messageListenerThread.stop();
        if(out != null) out.close();
        if(in != null) in.close();
        if (serverSocket != null) serverSocket.close();
        if(stdIn != null) stdIn.close();
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

  // ta bort send invite?
  public static boolean sendInvite(Socket socket, PrintWriter out, BufferedReader stdIn, BufferedReader in) throws IOException, SocketException{

      System.out.println(CALL_IP_QUESTION);
      try{
        String peerIpAddress = stdIn.readLine();
        System.out.println(CALL_PORT_QUESTION);
        int peerPort = Integer.parseInt(stdIn.readLine());

        System.out.println("Sending invite: " +  peerIpAddress +  ", port: " + peerPort);

        socket = new Socket(peerIpAddress, peerPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("Sending invite");
        out.println(INVITE_MESSAGE);
      }catch(IOException e){
        System.out.println("Could not send invite to IP / port");
        return false;
      }

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
    public Callhandler callHandler;
    private IncomingCallListener(CallHandler callHandler, ServerSocket serverSocket, Boolean incomingCall, Socket clientSocket){
      this.serverSocket = serverSocket;
      this.incomingCall = incomingCall;
      this.clientSocket = clientSocket;
      this.callHandler = callHandler;
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
              if(!callHandler.isCurrentStateBusy()){
                System.out.println(INCOMING_CALL_MENU);
                incomingCall = true;
              }else{
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("BUSY");
                out.close();
                clientSocket.close();
              }
               
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
                        callHandler.processNextEvent(CallHandler.CallEvent.BYE);
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
