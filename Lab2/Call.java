import java.util.*;
import java.net.*;
import java.io.*;
public class Call{


  private static String INCOMING_CALL_MENU = "\n INCOMING CALL(INVITE)...\n Answer / Reject?";

  private static String START_MENU = "*************************\n 1. Make call(call)\n 2. Quit(quit)\n*************************";

  private static String UNKOWN_COMMAND = "Unknown command.";
  private static String INVITE_MESSAGE = "INVITE";

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

    Boolean incomingCall = false;
    Scanner scanner = new Scanner(System.in);
    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    boolean doQuit = false;
    boolean doQuitToMenu = false;
    Thread callListenerThread = null;
    Thread messageListenerThread = null;

    Thread troAckThread = null;

    PeerMessageListener peerMessageListener = null;

    try{
        try{
            serverSocket = new ServerSocket(port);

        }catch (IOException e){
            System.err.println("Failed to listen to port: " + 5000);
            System.exit(1);
        }

        AudioStreamUDP audioStream = null;
        IncomingCallListener callListener = new IncomingCallListener(callHandler, serverSocket, incomingCall, clientSocket);
        peerMessageListener = new PeerMessageListener(audioStream, serverSocket, clientSocket, in, callHandler, out);
        messageListenerThread = new Thread(peerMessageListener);
        messageListenerThread.start();
        callListenerThread = new Thread(callListener);
        callListenerThread.start();
        while(!doQuit){
          System.out.println(START_MENU);
          doQuitToMenu = false;
          clientSocket = null;
          while(!doQuitToMenu){
            String inSignal = scanner.nextLine();
            String callArr[] = inSignal.split(" ", 3);
            inSignal = callArr[0];
            inSignal.toLowerCase();
              switch(inSignal){
                case "call":
                    try{
                      clientSocket = new Socket(callArr[1], Integer.parseInt(callArr[2]));
                    }catch(UnknownHostException e){
                      System.err.println("ServerHostName error: " + callArr[1]);
                    }
                    catch(ArrayIndexOutOfBoundsException e){
                      System.out.println("\nCorrect syntax: call <ip address> <port>\n");
                      System.out.println(START_MENU);
                      break;
                    }
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    callHandler.setOutPw(out);

                    peerMessageListener.setIn(in);

                    audioStream = new AudioStreamUDP();
                    callHandler.setAudioStream(audioStream);
                    peerMessageListener.setAwaitingTroAck(true);
                    System.out.println("awaitingtroack: " + peerMessageListener.getAwaitTroAck());
                    callHandler.setIp(clientSocket.getInetAddress());
                    callHandler.processNextEvent(CallHandler.CallEvent.USER_WANTS_TO_INVITE);



                break;
                case "answer":

                  if(!callListener.getIncomingCall()){
                    System.out.println("No incoming call");
                    break;
                  }

                    clientSocket = callListener.getClient();

                    audioStream = new AudioStreamUDP();
                    callHandler.setAudioStream(audioStream);

                    peerMessageListener.setIn(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    peerMessageListener.setIn(in);
                    callHandler.setOutPw(out);
                    peerMessageListener.setAwaitingTroAck(true);
                    System.out.println("awaitingtroack: " + peerMessageListener.getAwaitTroAck());

                    callHandler.setIp(clientSocket.getInetAddress());
                    callHandler.processNextEvent(CallHandler.CallEvent.INVITE);



                break;
                case "hangup":
                    callHandler.processNextEvent(CallHandler.CallEvent.USER_WANTS_TO_QUIT);
                break;
                case "quit":
                  doQuit = true;
                  doQuitToMenu = true;
                break;
                case "reject":
                break;
                default:
                System.out.println(UNKOWN_COMMAND + "\n" );
                break;
            }

            if(!callHandler.isCurrentStateBusy()){
              doQuitToMenu = true;

            }

          }

      }
    } catch (Exception e){
      e.printStackTrace();
    }finally{

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




  private static class IncomingCallListener implements Runnable{
    public boolean running;
    public ServerSocket serverSocket;
    public Socket clientSocket;
    public Boolean incomingCall;
    public CallHandler callHandler;
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

              clientSocket = serverSocket.accept();
              if(!callHandler.isCurrentStateBusy()){
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String msg = in.readLine();
                System.out.println(msg);
                if(msg.equals("invite")){
                  incomingCall = true;
                  System.out.println(INCOMING_CALL_MENU);
                }else{
                  System.out.println("hallå");

                  incomingCall = false;
                  clientSocket.close();
                }
              }else {
                PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
                pw.println("BUSY");
                clientSocket.close();
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

    public void setIncommingCall(boolean bool){
      incomingCall = bool;
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
      private BufferedReader in;
      private boolean inCall;
      private boolean awaitingTroAck;
      private CallHandler callHandler;
      private PrintWriter out;
      private boolean running;

      private PeerMessageListener(AudioStreamUDP audioStream, ServerSocket serverSocket, Socket clientSocket, BufferedReader in, CallHandler callHandler, PrintWriter out){
            this.serverSocket = serverSocket;
            this.clientSocket = clientSocket;
            this.in = in;
            inCall = false;
            this.callHandler = callHandler;
            this.out = out;
            awaitingTroAck = false;
            running = true;
      }

      @Override
      public void run(){
            while(running){
              try{
							  Thread.sleep(100);
						  } catch(InterruptedException e){
							  e.printStackTrace();
              }
              // TEST SWITCH
              if(awaitingTroAck){
                String message = null;
                try{
                  serverSocket.setSoTimeout(5000);
                  try{
                    message = in.readLine();
                  } catch(SocketTimeoutException e){
                    System.out.println("Handshake Timeout..");
                    //TODO: sätta insignal timeout?
                  }
                    System.out.println("Received: " + message);
                    message.trim().toLowerCase();

                    if(message.startsWith("tro")){
                      String[] arr = message.split(",");
                      if(arr.length != 2){
                        message = "ERROR";
                      }else{

                        String udpPort = arr[1];
                        try{
                          int port = Integer.parseInt(udpPort);
                          callHandler.setUdpPort(port);
                          callHandler.processNextEvent(CallHandler.CallEvent.TRO);
                          message = "TRO";
                        }catch(NumberFormatException e){
                          System.out.println("Could not read port number");
                        }


                      }

                    }else if(message.startsWith("ack")){
                      String[] arr = message.split(",");
                      if(arr.length != 2){
                        message = "ERROR";
                      }else{
                        String udpPort = arr[1];
                        try{
                          int port = Integer.parseInt(udpPort);
                          callHandler.setUdpPort(port);
                          callHandler.processNextEvent(CallHandler.CallEvent.ACK);
                          message = "ACK";
                        }catch(NumberFormatException e){
                          System.out.println("Could not read port number");
                        }


                      }

                    }

                    switch(message){
                      case "bye":
                        callHandler.processNextEvent(CallHandler.CallEvent.BYE);
                        break;
                      case "ok":
                        callHandler.processNextEvent(CallHandler.CallEvent.OK);
                        break;
                      case "TRO":
                        System.out.println("Received TRO" );
                        break;
                      case "ACK":
                      System.out.println("Received ACK" );
                      break;
                      default:
                        System.out.println("Handshake ERROR" + "\n" );
                        break;

                    }


                }catch(IOException e){
                  e.printStackTrace();

                }
              }

            }

      }
      public void setAwaitingTroAck(boolean awaitingTroAck){
        this.awaitingTroAck = awaitingTroAck;
      }
      public boolean getAwaitTroAck(){
        return awaitingTroAck;
      }

      public void setInCall(boolean inCall){
        this.inCall = inCall;
      }

      public void setIn(BufferedReader in){
        this.in = in;
      }
      public void setOut(PrintWriter out){
        this.out = out;
      }
      }
}
