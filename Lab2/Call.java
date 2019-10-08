import java.util.*;
import java.net.*;
import java.io.*;
public class Call{


  private static String INCOMING_CALL_MENU = "\n INCOMING CALL(INVITE)...\n Answer / Reject?";

  private static String START_MENU = "*************************\n 1. Make call(call)\n 2. Quit(quit)\n*************************";

  private static String UNKOWN_COMMAND = "Unknown command.";
  private static String INVITE_MESSAGE = "INVITE";


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
    System.out.println("Faulty: " + faulty);
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    BufferedReader in = null;
    PrintWriter out = null;
    String ipAddress = null;
    int port = Integer.parseInt(args[0]);
    CallHandler callHandler = new CallHandler(out);
    callHandler.setFaulty(faulty);
    Boolean incomingCall = false;
    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    boolean doQuit = false;
    boolean doQuitToMenu = false;
    Thread callListenerThread = null;
    Scanner scanner = new Scanner(System.in);
    callHandler.setScanner(scanner);
    Thread troAckThread = null;

    try {
      try {
        serverSocket = new ServerSocket(port);

      } catch (IOException e) {
        System.err.println("Failed to listen to port: " + port);
        System.exit(1);
      }

      AudioStreamUDP audioStream = null;
      IncomingCallListener callListener = new IncomingCallListener(faulty, scanner, audioStream, callHandler, serverSocket, incomingCall,
          clientSocket);
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
            } catch (UnknownHostException e) {
              System.err.println("ServerHostName error: " + callArr[1]);
            } catch (ArrayIndexOutOfBoundsException e) {
              System.out.println("\nCorrect syntax: call <ip address> <port>\n");
              System.out.println(START_MENU);
              break;
            }
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            PeerMessageListener peerMessageListener = new PeerMessageListener(faulty, scanner, audioStream, serverSocket, clientSocket,
                in, callHandler, out);
            Thread messageListenerThread = new Thread(peerMessageListener);

            callHandler.setOutPw(out);
            messageListenerThread.start();
            audioStream = new AudioStreamUDP();
            callHandler.setAudioStream(audioStream);
            peerMessageListener.setAwaitingTroAck(true);
            System.out.println("awaitingtroack: " + peerMessageListener.getAwaitTroAck());
            callHandler.setIp(clientSocket.getInetAddress());
            if (faulty) {
              System.out.println("Write invite, or not: ");
              callHandler.faultyInvite = scanner.nextLine();
            }
            callHandler.processNextEvent(CallHandler.CallEvent.USER_WANTS_TO_INVITE);

            break;
          case "answer":

            if (!callListener.getIncomingCall()) {
              System.out.println("No incoming call");
              break;
            }

            clientSocket = callListener.getClient();

            audioStream = new AudioStreamUDP();
            callHandler.setAudioStream(audioStream);

            /*
             * peerMessageListener.setIn(new BufferedReader(new
             * InputStreamReader(clientSocket.getInputStream()))); out = new
             * PrintWriter(clientSocket.getOutputStream(), true); in = new
             * BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             * peerMessageListener.setIn(in); callHandler.setOutPw(out);
             * peerMessageListener.setAwaitingTroAck(true);
             * System.out.println("awaitingtroack: " +
             * peerMessageListener.getAwaitTroAck());
             */

            callHandler.setIp(clientSocket.getInetAddress());
            if (faulty) {
              System.out.println("Write tro, or not: ");
              callHandler.faultyTro = scanner.nextLine();
            }
            callHandler.processNextEvent(CallHandler.CallEvent.INVITE);

            break;
          case "hangup":
            if (faulty) {
              System.out.println("Write bye, or not: ");
              callHandler.faultyBye = scanner.nextLine();
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
            if (!callListener.getIncomingCall()) {
              System.out.println("No incoming call");
              break;
            }
            clientSocket = callListener.getClient();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("Sending reject");
            out.println("reject");

            break;
          default:
            if(faulty){
              callHandler.faultyAck = inSignal;
            }else{
              System.out.println(UNKOWN_COMMAND + "\n");
            }

            break;
          }

          if (!callHandler.isCurrentStateBusy()) {
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

  public static void setFaultyAck(CallHandler callHandler) {
    System.out.println("Write ack, or not: ");
    while (callHandler.faultyAck == null) {
      try{
        Thread.sleep(100);
      } catch(InterruptedException e){
      }
    }
    System.out.println("faultyAck: "+ callHandler.faultyAck);
  }


  private static class IncomingCallListener implements Runnable{
    public boolean running;
    public ServerSocket serverSocket;
    public Socket clientSocket;
    public Boolean incomingCall;
    public CallHandler callHandler;
    private PeerMessageListener peerMessageListener;
    private AudioStreamUDP audioStream;
    private boolean faulty;
    private Scanner scanner;

    private IncomingCallListener(boolean faulty, Scanner scanner, AudioStreamUDP audioStream, CallHandler callHandler, ServerSocket serverSocket, Boolean incomingCall, Socket clientSocket){
      this.serverSocket = serverSocket;
      this.incomingCall = incomingCall;
      this.clientSocket = null;
      this.callHandler = callHandler;
      this.audioStream = audioStream;
      peerMessageListener = null;
      running = true;
      this.faulty = faulty;
      this.scanner = scanner;
    }



    @Override
    public void run(){
      try{

        while(running){
            clientSocket = serverSocket.accept();
            if(!callHandler.isCurrentStateBusy()){
              BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
              String msg = in.readLine().toLowerCase().trim();
              System.out.println(msg);
              if(msg.equals("invite")){
                incomingCall = true;
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                callHandler.setOutPw(out);
                peerMessageListener = new PeerMessageListener(faulty, scanner, audioStream, serverSocket, clientSocket, in, callHandler, out);
                peerMessageListener.setAwaitingTroAck(true);
                Thread messageListenerThread = new Thread(peerMessageListener);
                messageListenerThread.start();
                System.out.println(INCOMING_CALL_MENU);
              }else{
                System.out.println("Received invalid invite message.");
                incomingCall = false;
                in.close();
                clientSocket.close();
              }

            }else{
              PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
              System.out.println("Incoming call, sending busy");
              pw.println("busy");
          /*    try{
							  Thread.sleep(100);
						  } catch(InterruptedException e){
							  e.printStackTrace();
              }*/
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
      private boolean faulty;
      private Scanner scanner;

      private PeerMessageListener(Boolean faulty, Scanner scanner, AudioStreamUDP audioStream, ServerSocket serverSocket, Socket clientSocket, BufferedReader in, CallHandler callHandler, PrintWriter out){
            this.serverSocket = serverSocket;
            this.clientSocket = clientSocket;
            this.in = in;
            inCall = false;
            this.callHandler = callHandler;
            this.out = out;
            awaitingTroAck = false;
            running = true;
            this.faulty = faulty;
            this.scanner = scanner;
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
							  e.printStackTrace();
              }
        //      if(awaitingTroAck){
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
                          setFaultyAck(callHandler);
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
                    CallState nextCallState = null;
                    try{


                    switch(message.trim().toLowerCase()){
                      case "bye":
                        if (faulty) {
                          System.out.println("Write ok, or not: ");
                          callHandler.faultyOk = scanner.nextLine();
                        }
                        callHandler.processNextEvent(CallHandler.CallEvent.BYE);
                        clientSocket.setSoTimeout(5000);
                        break;
                      case "ok":

                        callHandler.processNextEvent(CallHandler.CallEvent.OK);
                        running = false;
                        break;
                      case "tro":
                        System.out.println("Received TRO" );
                        clientSocket.setSoTimeout(0);

                        break;
                      case "ack":
                        System.out.println("Received ACK");
                        clientSocket.setSoTimeout(0);
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
                          System.out.println("Rejecting call");
                          callHandler.processNextEvent(CallHandler.CallEvent.TIMEOUT);
                          running = false;
                          break;
                      default:
                        System.out.println("No message" + "\n" );
                        running = false;

                        break;

                    }
                  }catch(SocketException e){
                    e.printStackTrace();
                  }



              }

        //    }
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
