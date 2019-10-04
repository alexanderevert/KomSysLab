import java.util.*;
import java.net.*;
import java.io.*;

public class CallStateInSession extends CallState{
  public CallStateInSession(){

  }

  public CallState receivedBye(AudioStreamUDP audioStream,PrintWriter out){
    
    try{
      out.println("ok");
    }catch(Exception e){
      System.out.println("Failed to send OK");
      e.printStackTrace();
    return new CallStateInSession();
    }
    System.out.println("Going to state CallStateFree");
    audioStream.stopStreaming();
    return new CallStateFree();
  }

  public CallState userWantsToQuit(AudioStreamUDP audioStream, PrintWriter out){
    try{
      out.println("bye");
    }catch(Exception e){
      System.out.println("Failed to send BYE");
      e.printStackTrace();
    return new CallStateInSession();
    }
    System.out.println("Going to state CallStateWaitQuitOK");
    //audioStream.stopStreaming(); 
    return new CallStateWaitQuitOK();
  }

  public void printState(){
	  System.out.println("State: In session");
  }

}
