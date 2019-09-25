import java.util.*;
import java.io.*;
import java.net.*;

class ClientThread implements Runnable{

    private final String WELCOME_MESSAGE = "Welcome";
    private final String HELP_MESSAGE = "COMMANDS: \n/nick - Change nickname\n/who - Show chat members\n/quit - End session";
    private final String NICK_TAKEN_MESSAGE = "Nick already taken.";
    private final String CLIENT_DISCONNECTED_MESSAGE = "CLIENT DISCONNECTED..";
    private final String UNKNOWN_COMMAND = "Unknown command";
    private final String NAME_CHANGE_SUCCESFUL = "Changed name to: ";
    private final String SERVER_WELCOME_MESSAGE_INFO = "Sending welcome message...";
    private final String LIST_OF_CLIENTS_MESSAGE = "LIST OF CONNECTED CLIENTS: \n";


    private final int NAME_START_INDEX_NICK = 6;

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
            System.out.println(SERVER_WELCOME_MESSAGE_INFO);
            clientOut.println(WELCOME_MESSAGE);


            //TEST START{


            String receivedMessage;

            while(!disconnect && ((receivedMessage = getMessage()) != null)){

                if(receivedMessage.startsWith("/")){
                  handleCommands(receivedMessage);
                  }
                else{
                  sendMessage(receivedMessage);
                }


            }
          }catch(IOException e){
            System.out.println("Client disconnected. Sending messages");


        //  System.exit(1);
        }finally{

          clientThreads.remove(this);

            try{
              sendMessage("Client: " + alias + " disconnected");
              clientOut.close();
              clientIn.close();
              socket.close();

            }catch(IOException e){
              e.printStackTrace();
            }
    }
  }

  private void handleCommands(String receivedMessage)throws IOException{

    if(receivedMessage.startsWith("/nick") ){
      String nick = receivedMessage.substring(NAME_START_INDEX_NICK, receivedMessage.length());
      if(!isNickTaken(nick)) {
        alias = nick;
        clientOut.println(NAME_CHANGE_SUCCESFUL + nick);
      } else{
        clientOut.println(NICK_TAKEN_MESSAGE);

      }
    }
    else{
      switch(receivedMessage.toLowerCase()){
        case "/quit":
          sendMessage(CLIENT_DISCONNECTED_MESSAGE);
          clientThreads.remove(this);
          disconnect = true;
          break;
        case "/who":
          StringBuilder sb = new StringBuilder(LIST_OF_CLIENTS_MESSAGE);
          for(ClientThread c: clientThreads){
            sb.append(c.alias + "\n");
          }
          //PrintWriter pw = new PrintWriter(this.getSocket().getOutputStream(), true);
          clientOut.println(sb.toString());
          break;
        case "/help":
        clientOut.println(HELP_MESSAGE);
        break;
        default: clientOut.println(UNKNOWN_COMMAND);
        clientOut.println(HELP_MESSAGE);

    }
  }
}
  private boolean isNickTaken(String nick){
    for(ClientThread c: clientThreads){
      if(c.alias.equals(nick)) return true;
    }
    return false;
  }

  private String getMessage() throws IOException{
      return clientIn.readLine();
  }
  private synchronized void sendMessage(String message) throws IOException{
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
