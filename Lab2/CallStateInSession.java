import java.util.*;
import java.net.*;
import java.io.*;

public class CallStateInSession extends CallStateBusy{
  public CallStateInSession(){

  }

  public CallState receivedBye(AudioStreamUDP audioStream,PrintWriter out, boolean faulty, Scanner scanner, String faultyMsg){
    String msg = null;
    if(faulty){
      msg = faultyMsg;
    }else{
      msg = "ok";
    }
    
    try{
      out.println(msg);
    }catch(Exception e){
      System.out.println("Failed to send OK");
    }
    
    audioStream.stopStreaming();
    System.out.println("Going to state CallStateFree");
    return new CallStateFree();
    
    
  }

  public CallState userWantsToQuit(AudioStreamUDP audioStream, PrintWriter out, boolean faulty, Scanner scanner, String faultyMsg){
    String msg = null;
    if(faulty){
      msg = faultyMsg;
    }else{
      msg = "bye";
    }

    if(msg.equals("bye")){
      try{
        out.println(msg);
      }catch(Exception e){
        audioStream.stopStreaming();
        System.out.println("Failed to send BYE");
        return new CallStateFree();
      }
      System.out.println("Going to state CallStateWaitQuitOK");
      audioStream.stopStreaming();
      return new CallStateWaitQuitOK();
    }
      System.out.println("Wrong Bye-message");
      return this;
    
    
  }


  public void printState(){
	  System.out.println("State: In session");
  }

}
