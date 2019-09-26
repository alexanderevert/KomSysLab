
public class CallStateWaitTRO extends CallStateBusy{
    public CallStateWaitTRO(){

    }

    public CallState timedOut(){
      System.out.println("Going to state CallStateFree");
      return new CallStateFree();
    }

}
