
public class CallStateWaitTRO extends CallStateBusy{
    public CallStateWaitTRO(){

    }

    public CallState timedOut(){
      System.out.println("Going to state CallStateFree");
      return new CallStateFree();
    }

    public CallState answerCall(PrintWriter out){
    //TODO: skicka ack
      return new CallStateInSession();
    }

    public void printState(){
      System.out.println("State: Waiting TRO");
    }

}
