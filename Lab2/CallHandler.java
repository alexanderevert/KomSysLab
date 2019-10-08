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
  private PrintWriter out;
  private AudioStreamUDP audioStream;
  private int udpPort;
  private InetAddress ip;
  private boolean faulty;
  private Scanner scanner;
  public String faultyInvite;
  public String faultyTro;
  public String faultyAck = null;
  public String faultyBye;
  public String faultyOk;
  public CallHandler(PrintWriter out){
    currentState = new CallStateFree();
    //this.out = out;
    this.out = out;
  }

  public void processNextEvent(CallEvent event){
    switch(event){
      case INVITE:
      currentState = currentState.receivedInvite(audioStream, out, faulty, scanner, faultyTro);
      break;

      case TRO:
      currentState = currentState.answerCall(ip, udpPort, audioStream, out, faulty, scanner, faultyAck);
      break;

      case ACK:

      currentState = currentState.receivedAck(ip, udpPort, audioStream);
      break;

      case TIMEOUT:
      currentState = currentState.timedOut();
      break;

      case BYE:
      currentState = currentState.receivedBye(audioStream, out, faulty, scanner);
      break;

      case OK:
      currentState = currentState.receivedOk(audioStream);
      break;

      case USER_WANTS_TO_INVITE:
      currentState = currentState.userWantsToInvite(out, faulty, scanner, faultyInvite);
      break;

      case USER_WANTS_TO_QUIT:
      currentState = currentState.userWantsToQuit(audioStream, out, faulty, scanner);
      break;
      case BUSY:
        currentState = currentState.receivedBusy();
      break;

      default: break;
    }
  }

  public void setScanner(Scanner scanner){
    this.scanner = scanner;
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

  public void setFaulty(boolean faulty){
    this.faulty = faulty;
  }



}
