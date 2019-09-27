import java.util.*;
import java.net.*;
import java.io.*;


public class CallStateFree extends CallState {
  public CallStateFree(){

  }

  public CallState userWantsToInvite(){

  try{
      out.println("invite");
  }catch(Exception e){
      System.out.println("Failed to send invite");
      e.printStackTrace();
  }
    

    System.out.println("Going to state CallStateWaitTRO");
    return new CallStateWaitTRO();
  }

  public CallState receivedInvite(){
    System.out.println("Going to state CallStateWaitAck");
    //TODO: skicka tro
    return new CallStateWaitAck();
  }

  public void printState(){
	  System.out.println("State: Free");
  }


}
