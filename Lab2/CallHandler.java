import java.util.*;
import java.net.*;
import java.io.*;

public class CallHandler{

  public enum CallEvent{
    INVITE,
    TRO,
    ACK,
    TIMEOUT,
    BYE,
    OK,
    USER_WANTS_TO_INVITE,
    USER_WANTS_TO_QUIT,
    BUSY,
  }

  private CallState currentState;
  private AudioStreamUDP audioStream;
  private int udpPort;
  private InetAddress ip;
  private boolean faulty;
  private String faultyMsg = null;
  private Socket clientSocket;
  private String incommingMsg;

  public CallHandler(PrintWriter out){
    currentState = new CallStateFree();
    try{
      audioStream = new AudioStreamUDP();
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public void processNextEvent(CallEvent event){
    switch(event){
      case INVITE:
      currentState = currentState.receivedInvite(audioStream, faulty, faultyMsg, clientSocket);
      faultyMsg = null;
      break;

      case TRO:
      currentState = currentState.receivedTro(ip, udpPort, audioStream,  faulty, faultyMsg, clientSocket);
      faultyMsg = null;
      break;

      case ACK:

      currentState = currentState.receivedAck(ip, udpPort, audioStream, clientSocket);
      break;

      case TIMEOUT:
      currentState = currentState.timedOut();
      break;

      case BYE:
      currentState = currentState.receivedBye(audioStream, faulty, faultyMsg, clientSocket);
      faultyMsg = null;
      break;

      case OK:
      currentState = currentState.receivedOk(audioStream);
      break;

      case USER_WANTS_TO_INVITE:
      currentState = currentState.userWantsToInvite(faulty,  faultyMsg, clientSocket);
      faultyMsg = null;
      break;

      case USER_WANTS_TO_QUIT:
      currentState = currentState.userWantsToQuit(audioStream, faulty, faultyMsg, clientSocket);
      faultyMsg = null;
      break;
      case BUSY:
        currentState = currentState.receivedBusy();
      break;

      default: break;
    }
  }

  public void setIp(InetAddress ip){
    this.ip = ip;
  }

  public void setFaultyMessage(String faultyMsg){
    this.faultyMsg = faultyMsg;
  }

  public String getFaultyMessage(){
    return faultyMsg;
  }

  public void setIncommingMessage(String incommingMsg){
    this.incommingMsg = incommingMsg;
  }

  public void setUdpPort(int port){
    this.udpPort = port;
  }

  public boolean isCurrentStateBusy(){
    return currentState.busy();
  }

  public void printState() {
    currentState.printState();
  }

  public void setFaulty(boolean faulty){
    this.faulty = faulty;
  }

  public void setClientSocket(Socket clientSocket){
    this.clientSocket = clientSocket;
  }

  


}
