import java.util.*;
import java.net.*;
import java.io.*;


public class CallStateFree extends CallState {
  public CallStateFree(){

  }

  public CallState userWantsToInvite(PrintWriter out){
    try{
        out.println("invite");
    }catch(Exception e){
        System.out.println("Failed to send invite");
        e.printStackTrace();
      return new CallStateFree();
    }
    System.out.println("Going to state CallStateWaitTRO");
    return new CallStateWaitTRO();
  }

  public CallState receivedInvite(PrintWriter out){
    //TODO: bekräfta 
    
    System.out.println("Sending tro");
    out.println("tro");

    System.out.println("Going to state CallStateWaitAck");
    return new CallStateWaitAck();
  }

  public void printState(){
	  System.out.println("State: Free");
  }


}
