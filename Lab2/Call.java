import java.util.*;
import java.net.*;
import java.io.*;
public class Call{

  private static String INCOMING_CALL_MENU = "\n INCOMING CALL(INVITE)...\n Answer / Reject?";
  private static String START_MENU = "*************************\n 1. Make call(call)\n 2. Quit(quit)\n*************************";
  private static String UNKOWN_COMMAND = "Unknown command.";
  
  public static void main(String[] args) {
    boolean faulty = false;
    if (args.length < 1 || args.length > 2) {
      System.err.println("Usage: java Call <port number>\n + faulty if error mode");
      System.exit(1);
    }
    if (args.length == 2) {
      if (args[1].equals("faulty")) {
        faulty = true;
      }
    }
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    BufferedReader in = null;
    PrintWriter out = null;
    int port = Integer.parseInt(args[0]);
    CallHandler callHandler = new CallHandler(out);
    callHandler.setFaulty(faulty);
    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    boolean doQuit = false;
    boolean doQuitToMenu = false;
    Thread callListenerThread = null;
    Scanner scanner = new Scanner(System.in);

    

    try {
      try {
        serverSocket = new ServerSocket(port);
      } catch (IOException e) {
        System.err.println("Failed to listen to port: " + port);
        System.exit(1);
      }
      
      IncomingCallListener callListener = new IncomingCallListener(callHandler, serverSocket,
        clientSocket, faulty);
      callListenerThread = new Thread(callListener);
      callListenerThread.start();
      while (!doQuit) {
        System.out.println(START_MENU);
        doQuitToMenu = false;
        clientSocket = null;
        while (!doQuitToMenu) {

          String inSignal = scanner.nextLine();
          String callArr[] = inSignal.split(" ", 3);
          inSignal = callArr[0];
          inSignal.toLowerCase();
          switch (inSignal) {
          case "call":
            try {
              clientSocket = new Socket(callArr[1], Integer.parseInt(callArr[2]));
            }catch(ConnectException e){
              System.out.println("Do not recognize host name and/or port.");
              break;
            }
            catch (UnknownHostException e) {
              System.out.println("Do not recognize host name and/or port.");
              break;
            } catch (ArrayIndexOutOfBoundsException e) {
              System.out.println("\nCorrect syntax: call <ip address> <port>\n");
              break;
            }catch(Exception e){
              System.out.println("Do not recognize host name and/or port.");
              break;
            }

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PeerMessageListener peerMessageListener = new PeerMessageListener(clientSocket, in, callHandler, faulty);
            Thread messageListenerThread = new Thread(peerMessageListener);
            messageListenerThread.start();
            if (faulty) {
              System.out.println("Write invite, or not: ");
              callHandler.faultyMsg = scanner.nextLine();
            }
            callHandler.processNextEvent(CallHandler.CallEvent.USER_WANTS_TO_INVITE);
            break;
          case "answer":

            if (callListener.getClient() == null) {
              System.out.println("No incoming call");
              break;
            }
            if (faulty) {
              System.out.println("Write tro, or not: ");
              callHandler.faultyMsg = scanner.nextLine();
            }
            callHandler.processNextEvent(CallHandler.CallEvent.INVITE);
            break;
          case "hangup":
          if (faulty) {
            System.out.println("Write bye, or not: ");
            callHandler.faultyMsg = scanner.nextLine();
          }
            callHandler.processNextEvent(CallHandler.CallEvent.USER_WANTS_TO_QUIT);
            break;
          case "quit":
            doQuit = true;
            doQuitToMenu = true;
            break;
          case "busy":
            callHandler.processNextEvent(CallHandler.CallEvent.BUSY);
            break;
          case "reject":
            //TODO: ???
            /*
            if(callListener.getClient() != null){
              clientSocket = callListener.getClient();
              out = new PrintWriter(clientSocket.getOutputStream(), true);
              System.out.println("Sending reject");
              out.println("reject");
              clientSocket.close();
              callListener.setClientSocket(null);
            }else{

            }*/

            break;
          default:
            if(faulty){
              callHandler.faultyMsg = inSignal;
            }else{
              System.out.println(UNKOWN_COMMAND + "\n");
            }
            break;
          }
          if (!callHandler.isCurrentStateBusy() && callListener.getClient() != null) {
            doQuitToMenu = true;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (callListenerThread != null)
          callListenerThread.stop();
        if (out != null)
          out.close();
        if (in != null)
          in.close();
        if (serverSocket != null)
          serverSocket.close();
        if (stdIn != null)
          stdIn.close();
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(1);
      }
      if (scanner != null)
        scanner.close();
    }
  }

  public static void setFaultyMsg(CallHandler callHandler) {
    while (callHandler.faultyMsg == null) {
      try{
        Thread.sleep(500);
      } catch(InterruptedException e){
        e.printStackTrace();
      }
    }
  }

  public static class PeerMessageListener implements Runnable{
      private Socket clientSocket;
      private BufferedReader in;
      private CallHandler callHandler;
      private boolean running;
      private boolean faulty;

      public PeerMessageListener(Socket clientSocket, BufferedReader in, CallHandler callHandler, boolean faulty){
            this.clientSocket = clientSocket;
            this.in = in;
            this.callHandler = callHandler;
            this.faulty = faulty;
            running = true;
            callHandler.setClientSocket(clientSocket);
      }

      @Override
      public void run(){
        try{
          clientSocket.setSoTimeout(20000);
        }catch(SocketException e){
          e.printStackTrace();
        }
            while(running){
              try{
							  Thread.sleep(100);
						  } catch(InterruptedException e){
              }
                String message = null;
                  try{
                    message = in.readLine();
                  } catch(Exception e){
                    System.out.println("Connection timeout");
                    message = "timeout";
                    running = false;
                  }
                  if(message == null){
                    message = "timeout";
                  }
                    System.out.println("Received: " + message);
                    if(message.startsWith("tro")){
                      String[] arr = message.split(",");
                      if(arr.length != 2){
                        message = "ERROR";
                      }else{
                        String udpPort = arr[1];
                        try{
                          int port = Integer.parseInt(udpPort);
                          if(faulty){
                            callHandler.faultyMsg = null;
                            System.out.println("Write ack, or not: ");
                            setFaultyMsg(callHandler);
                          }

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

                    switch(message.trim().toLowerCase()){
                      case "bye":
                      if(faulty){
                        callHandler.faultyMsg = null;
                        System.out.println("Write ok, or not: ");
                        setFaultyMsg(callHandler);
                        System.out.println("faultiMsg: " + callHandler.faultyMsg);
                        }
                        callHandler.processNextEvent(CallHandler.CallEvent.BYE);
                        running = false;
                        break;
                      case "ok":
                        callHandler.processNextEvent(CallHandler.CallEvent.OK);

                        running = false;
                        break;
                      case "tro":
                        break;
                      case "ack":
                        break;
                      case "busy":
                        System.out.println("Busy");
                        callHandler.processNextEvent(CallHandler.CallEvent.BUSY);
                        running = false;
                      break;
                      case "timeout":
                        callHandler.processNextEvent(CallHandler.CallEvent.TIMEOUT);
                        running = false;
                        break;
                      case "reject":
                          callHandler.processNextEvent(CallHandler.CallEvent.TIMEOUT);
                          running = false;
                          break;
                      default:
                          System.out.println("Wrong message");
                          callHandler.processNextEvent(CallHandler.CallEvent.TIMEOUT);
                          running = false;
                        break;
                    }
              }
        }
    }
}
