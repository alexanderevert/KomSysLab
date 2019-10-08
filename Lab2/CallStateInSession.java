import java.util.*;
import java.net.*;
import java.io.*;

public class CallStateInSession extends CallStateBusy{
  public CallStateInSession(){

  }

  public CallState receivedBye(AudioStreamUDP audioStream,PrintWriter out, boolean faulty, Scanner scanner){
    String msg = null;
    if(faulty){
      scanner = new Scanner(System.in);
      System.out.println("Type ok message:");
      msg = scanner.nextLine();

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

  public CallState userWantsToQuit(AudioStreamUDP audioStream, PrintWriter out, boolean faulty, Scanner scanner){
    String msg = null;
    if(faulty){
      scanner = new Scanner(System.in);
      System.out.println("Type bye message:");
      msg = scanner.nextLine();

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
