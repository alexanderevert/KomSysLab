

public class CallStateWaitQuitOK implements CallStateBusy{
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
}
