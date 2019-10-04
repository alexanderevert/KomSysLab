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
        System.out.println("Failed to send INVITE");
        e.printStackTrace();
      return new CallStateFree();
    }
    System.out.println("Going to state CallStateWaitTRO");
    return new CallStateWaitTRO();
  }

  public CallState receivedInvite(AudioStreamUDP audioStream, PrintWriter out){
    
    try{
      out.println("tro," + audioStream.getLocalPort());
    }catch(Exception e){
      System.out.println("Failed to send TRO");
      e.printStackTrace();
    return new CallStateFree();
    }
    System.out.println("Going to state CallStateWaitAck");
    return new CallStateWaitAck();
  }

  public void printState(){
	  System.out.println("State: Free");
  }


}
