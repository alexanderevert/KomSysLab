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
  }

  private CallState currentState;
  private PrintWriter out;
  private AudioStreamUDP audioStream;
  private int udpPort;
  private InetAddress ip;
  
  public CallHandler(PrintWriter out){
    currentState = new CallStateFree();
    //this.out = out;
    this.out = out;
  }

  public void processNextEvent(CallEvent event){
    switch(event){
      case INVITE:
      currentState = currentState.receivedInvite(audioStream, out);
      break;

      case TRO:
      currentState = currentState.answerCall(ip, udpPort, audioStream, out);
      break;

      case ACK:
      
      currentState = currentState.receivedAck(ip, udpPort, audioStream);
      break;

      case TIMEOUT:
      currentState = currentState.timedOut();
      break;

      case BYE:
      currentState = currentState.receivedBye(audioStream, out);
      break;

      case OK:
      currentState = currentState.receivedOk(audioStream);
      break;

      case USER_WANTS_TO_INVITE:
      //TODO: skicka invite innan man sätts i state
      currentState = currentState.userWantsToInvite(out);
      break;

      case USER_WANTS_TO_QUIT:
      currentState = currentState.userWantsToQuit(audioStream, out);
      break;


      default: break;
    }
  }

  public void setIp(InetAddress ip){
    this.ip = ip;
  }

  public void setUdpPort(int port){
    this.udpPort = port;
  }

  public void setAudioStream(AudioStreamUDP audioStream){
    this.audioStream = audioStream;
  }
  
  public void setOutPw(PrintWriter out){
    this.out = out;
  }

  public boolean isCurrentStateBusy(){
    return currentState.busy();
  }

  public void printState() {
    currentState.printState();
  }


}
