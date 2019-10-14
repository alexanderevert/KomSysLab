import java.util.*;
import java.net.*;
import java.io.*;

class IncomingCallListener implements Runnable{
    public boolean running;
    public ServerSocket serverSocket;
    public Socket clientSocket;
    public CallHandler callHandler;
    private PeerMessageListener peerMessageListener;
    private boolean faulty;
    private static String INCOMING_CALL_MENU = "\n INCOMING CALL(INVITE)...\n Answer / Reject?";

    public IncomingCallListener( CallHandler callHandler, ServerSocket serverSocket, Socket clientSocket, boolean faulty) {
      this.serverSocket = serverSocket;
      this.clientSocket = null;
      this.callHandler = callHandler;
      this.faulty = faulty;
      peerMessageListener = null;
      running = true;
    }

    @Override
    public void run() {
      try {
        while (running) {
          clientSocket = serverSocket.accept();
          if (!callHandler.isCurrentStateBusy()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String msg = in.readLine().toLowerCase().trim();
            System.out.println(msg);

            if (msg.equals("invite")) {
              peerMessageListener = new PeerMessageListener(clientSocket, in, callHandler, faulty);
              Thread messageListenerThread = new Thread(peerMessageListener);
              messageListenerThread.start();
              System.out.println(INCOMING_CALL_MENU);
            } else {
              System.out.println("Received invalid invite message.");
              System.out.println(Call.START_MENU);
              in.close();
              clientSocket.close();
            }
          } else {
            PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("Incoming call, sending busy");
            pw.println("busy");
            if(pw != null){
              pw.close();
            }
            if(clientSocket != null){
              clientSocket.close();
            }
          }
        }
      } catch (SocketException se) {
        se.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();

      } finally {
        try {
          if (clientSocket != null) {
            clientSocket.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    public Socket getClient(){
        return clientSocket;
    }
  }
