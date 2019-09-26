

public class CallStateWaitAck extends CallStateBusy{
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

  public void printState(){
	  System.out.println("State: Waiting ACK");
  }

}
