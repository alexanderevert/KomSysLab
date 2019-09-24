

public class CallHandler{
  public enum CallEvent{
    CALL_INITIATE,
    CALL_END,
    CALL_ANSWER,
  }
  private CallState currentState;

  public CallHandler(){
    currentState = new CallStateFree();

  }

  public void processNextEvent(CallEvent event){
    switch(event){
      case CALL_INITIATE:

      //TODO: skicka invite innan man sätts i state 
      currentState = currentState.userWantsToInvite();
      break;
      case CALL_END:
      currentState = currentState.userWantsToQuit();
      break;
      case CALL_ANSWER:
      currentState = currentState.answerCall();
      break;

      default: break;
    }
  }

}
