import java.util.*;
import java.io.*;
import java.net.*;

class ClientThread implements Runnable{

    private final String WELCOME_MESSAGE = "Welcome";
    private final String CLIENT_DISCONNECTED_MESSAGE = "CLIENT DISCONNECTED..";
    //private Scanner scanner = new Scanner(System.in);
    private String alias;
    private PrintWriter clientOut;
    private BufferedReader clientIn;
    private int id;
    private Socket socket;
    private List<ClientThread> clientThreads;
    private boolean disconnect;

    public ClientThread(Socket socket, int id, BufferedReader in, PrintWriter out, List<ClientThread> clientThreads) {
        this.socket = socket;
        this.clientIn = in;
        this.clientOut = out;
        this.clientThreads = clientThreads;
        this.id = id;
        this.disconnect = false;

        alias = "user " + id;
    }

    @Override
    	public void run(){

        try{
            System.out.println("Sending welcome message");
            clientOut.println(WELCOME_MESSAGE);
            //TEST START
            
            String receivedMessage;
            while(!disconnect && ((receivedMessage = getMessage()) != null)){
              if(receivedMessage.startsWith("/")){
                switch(receivedMessage.toLowerCase()){
                  case "/quit":
                    sendMessage(CLIENT_DISCONNECTED_MESSAGE);
                    clientThreads.set(id,null);
                    disconnect = true;
                    break;
                  case "/who":
                    StringBuilder sb = new StringBuilder("LIST OF CONNECTED CLIENTS:\n");
                    for(ClientThread c: clientThreads){
                      sb.append(c.alias + "\n");
                    }
                    //PrintWriter pw = new PrintWriter(this.getSocket().getOutputStream(), true);
                    clientOut.println(sb.toString());
                }
              }else{
                sendMessage(receivedMessage);
              }
              
            }
            //TEST STOP
            /*
            while(true){
                String receivedMessage;
                if((receivedMessage = getMessage()) != null){
                  System.out.println(recievedMessage);
                  int i = receivedMessage.indexOf(".");
                    if(i < 0){
                      System.out.println("Received message from client: " + receivedMessage + "\nSending message to all other clients");
                      sendMessage(receivedMessage);
                    }else{
                        String trimmedMessage = receivedMessage.substring(i);
                        clientOut.println(trimmedMessage);
                    }
                }

            }*/
        }catch(Exception e){
          e.printStackTrace();
          System.exit(1);
    }finally{
      try{
          clientOut.close();
          clientIn.close();
          socket.close();
      }catch(IOException e){
        e.printStackTrace();
      }
    }
  }

  private String getMessage() throws IOException{
      return clientIn.readLine();
  }
  private void sendMessage(String message) throws IOException{
      if(clientThreads.size()>1){
              for(ClientThread c : clientThreads){
                if(c!=this){
                  PrintWriter pw = new PrintWriter(c.getSocket().getOutputStream(), true);
                  pw.println(alias + ": " + message);
                }
              }
      }

  }
  public Socket getSocket(){
    return socket;
  }
}
