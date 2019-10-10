import java.util.*;
import java.net.*;
import java.io.*;


public class CallStateFree extends CallState {
  public CallStateFree(){

  }

  public CallState userWantsToInvite(boolean faulty, String faultyMsg, Socket clientSocket){
    String msg = null;
    PrintWriter out = null;
    try{
      out = new PrintWriter(clientSocket.getOutputStream(), true);
    }catch(IOException e){
      System.out.println("Failed to create PrintWriter");
      error();
    }
    if(faulty){
      msg = faultyMsg;
    }else{
      msg = "invite";
    }
    try{
        out.println(msg);
    }catch(Exception e){
        System.out.println("Failed to send INVITE");
        error();
    }

    System.out.println("Going to state CallStateWaitTRO");
    System.out.println("Ringing...");
    return new CallStateWaitTRO();
  }

  public CallState receivedInvite(AudioStreamUDP audioStream, boolean faulty, String faultyMsg, Socket clientSocket){
    String msg = null;
    PrintWriter out = null;
    try{
      out = new PrintWriter(clientSocket.getOutputStream(), true);
    }catch(IOException e){
      System.out.println("Failed to create PrintWriter");
      error();
    }
    if(faulty){
      msg = faultyMsg;
    }else{
      msg = "tro";
    }
    try{
      out.println(msg + "," + audioStream.getLocalPort());
    }catch(Exception e){
      System.out.println("Failed to send TRO");
      error();
    return new CallStateFree();
    }
    System.out.println("Going to state CallStateWaitAck");
    return new CallStateWaitAck();
  }

  public void printState(){
	  System.out.println("State: Free");
  }


}
