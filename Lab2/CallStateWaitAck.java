

public class CallStateWaitAck implements CallStateBusy{
  public CallStateWaitAck(){
  }

  public CallState receivedAck(){
    System.out.println("Going to state CallStateInSession");
    return new CallStateInSession();
  }

  public CallState timedOut(){
    System.out.println("Going to state CallStateFree");
    return new CallStateFree();
  }

}
