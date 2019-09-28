import java.util.*;
import java.net.*;
import java.io.*;


public class CallStateWaitTRO extends CallStateBusy{
    public CallStateWaitTRO(){

    }

    public CallState timedOut(){
      System.out.println("Going to state CallStateFree");
      return new CallStateFree();
    }

    
    public CallState answerCall(PrintWriter out){
      System.out.println("Sending ACK");
      //TODO: skicka ack
      try{
        out.println("ack");
      }catch(Exception e){
        e.printStackTrace();
      }
      
      System.out.println("Going to state: CallStateInSession");
      return new CallStateInSession();
    }

    public void printState(){
      System.out.println("State: Waiting TRO");
    }

}
