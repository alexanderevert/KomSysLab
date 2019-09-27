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
  //private PrintWriter out;
  
  public CallHandler(PrintWriter out){
    currentState = new CallStateFree();
    //this.out = out;
    currentState.out = out;
  }

  public void processNextEvent(CallEvent event){
    switch(event){
      case INVITE:
      currentState = currentState.receivedInvite();
      break;

      case TRO:
      currentState = currentState.answerCall();
      break;

      case ACK:
      currentState = currentState.receivedAck();
      break;

      case TIMEOUT:
      currentState = currentState.timedOut();
      break;

      case BYE:
      currentState = currentState.receivedBye();
      break;

      case OK:
      currentState = currentState.receivedOk();
      break;

      case USER_WANTS_TO_INVITE:
      //TODO: skicka invite innan man sätts i state
      currentState = currentState.userWantsToInvite();
      break;

      case USER_WANTS_TO_QUIT:
      currentState = currentState.userWantsToQuit();
      break;


      default: break;
    }
  }

  public void setOutPw(PrintWriter out){
    currentState.out = out;
  }

  public void printState() {
    currentState.printState();
  }


}
