
public class CallStateFree extends CallState{
  public CallStateFree(){

  }

  public CallState userWantsToInvite(){
    System.out.println("Going to state CallStateWaitTRO");

    return new CallStateWaitTRO();
  }

  public CallState receivedInvite(PrintWriter out){
    System.out.println("Going to state CallStateWaitAck");
    //TODO: skicka tro
    return new CallStateWaitAck();
  }


}
