import java.util.*;
import java.net.*;
import java.io.*;

public class CallStateInSession extends CallStateBusy{
  public CallStateInSession(){

  }

  public CallState receivedBye(AudioStreamUDP audioStream,PrintWriter out, boolean faulty, Scanner scanner, String faultyOk){
    String msg = null;
    if(faulty){
      msg = faultyOk;

    }else{
      msg = "ok";
    }
    try{
      out.println(msg);
    }catch(Exception e){
      System.out.println("Failed to send OK");
      e.printStackTrace();
    return new CallStateInSession();
    }
    System.out.println("Going to state CallStateFree");
    audioStream.stopStreaming();
    return new CallStateFree();
  }

  public CallState userWantsToQuit(AudioStreamUDP audioStream, PrintWriter out, boolean faulty, Scanner scanner, String faultyBye){
    String msg = null;
    if(faulty){
      msg = faultyBye;

    }else{
      msg = "bye";
    }
    try{
      out.println(msg);
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
