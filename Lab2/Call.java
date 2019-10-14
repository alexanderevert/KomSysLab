import java.util.*;
import java.net.*;
import java.io.*;
public class Call{

  public static String START_MENU = "*************************\n 1. Make call(call)\n 2. Quit(quit)\n*************************";
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
              callHandler.setFaultyMessage(scanner.nextLine());
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
              callHandler.setFaultyMessage(scanner.nextLine());
            }
            callHandler.processNextEvent(CallHandler.CallEvent.INVITE);
            break;

          case "hangup":
          if (faulty) {
            System.out.println("Write bye, or not: ");
            callHandler.setFaultyMessage(scanner.nextLine());
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
            System.out.println(START_MENU);
            break;
          default:
            if(faulty){
              callHandler.setFaultyMessage(inSignal);
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
    while (callHandler.getFaultyMessage() == null) {
      try{
        Thread.sleep(500);
      } catch(InterruptedException e){
        e.printStackTrace();
      }
    }
  }


}
