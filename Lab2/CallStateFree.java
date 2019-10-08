import java.util.*;
import java.net.*;
import java.io.*;


public class CallStateFree extends CallState {
  public CallStateFree(){

  }

  public CallState userWantsToInvite(PrintWriter out, boolean faulty, Scanner scanner){
    String msg = null;
    if(faulty){
      scanner = new Scanner(System.in);
      System.out.println("Type invite message:");
      msg = scanner.nextLine();
      
    }else{
      msg = "invite";
    }
    try{
        out.println(msg);
    }catch(Exception e){
        System.out.println("Failed to send INVITE");
        e.printStackTrace();
      return new CallStateFree();
    }
    System.out.println("Going to state CallStateWaitTRO");
    System.out.println("Ringing...");
    return new CallStateWaitTRO();
  }

  public CallState receivedInvite(AudioStreamUDP audioStream, PrintWriter out, boolean faulty, Scanner scanner){
    String msg = null;
    if(faulty){
      scanner = new Scanner(System.in);
      System.out.println("Type tro message:");
      msg = scanner.nextLine();

    }else{
      msg = "tro";
    }
    try{
      out.println(msg + "," + audioStream.getLocalPort());
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
