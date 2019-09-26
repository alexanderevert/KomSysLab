

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
  public CallHandler(PrintWriter out){
    currentState = new CallStateFree();
    this.out = out;
  }

  public void processNextEvent(CallEvent event){
    switch(event){
      case CallEvent.INVITE:
      currentState = currentState.receivedInvite(out);
      break;

      case CallEvent.TRO:
      currentState = currentState.answerCall();
      break;

      case CallEvent.ACK:
      currentState = currentState.receivedAck();
      break;

      case CallEvent.TIMEOUT:
      currentState = currentState.timedOut();
      break;

      case CallEvent.BYE:
      currentState = currentState.receivedBye();
      break;

      case CallEvent.OK:
      currentState = currentState.receivedOk();
      break;

      case CallEvent.USER_WANTS_TO_INVITE:
      //TODO: skicka invite innan man sätts i state
      currentState = currentState.userWantsToInvite();
      break;

      case CallEvent.USER_WANTS_TO_QUIT:
      currentState = currentState.userWantsToQuit();
      break;


      default: break;
    }
  }


}
