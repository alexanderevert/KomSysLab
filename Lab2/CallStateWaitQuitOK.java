

public class CallStateWaitQuitOK extends CallStateBusy{
  public CallStateWaitQuitOK(){
  }

  public CallState timedOut(){
    System.out.println("Going to state CallStateFree");
    return new CallStateFree();
  }
  public CallState receivedOk(){
    System.out.println("Going to state CallStateFree");
    return new CallStateFree();
  }

  public void printState(){
	  System.out.println("State: Waiting quit OK");
  }
}
