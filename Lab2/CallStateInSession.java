import java.util.*;
import java.net.*;
import java.io.*;

public class CallStateInSession extends CallState{
  public CallStateInSession(){

  }

  public CallState receivedBye(PrintWriter out){
    
    try{
      out.println("ok");
    }catch(Exception e){
      System.out.println("Failed to send OK");
      e.printStackTrace();
    return new CallStateInSession();
    }
    System.out.println("Going to state CallStateFree");
    return new CallStateFree();
  }

  public CallState userWantsToQuit(PrintWriter out){
    try{
      out.println("bye");
    }catch(Exception e){
      System.out.println("Failed to send BYE");
      e.printStackTrace();
    return new CallStateInSession();
    }
    System.out.println("Going to state CallStateWaitQuitOK");
    return new CallStateWaitQuitOK();
  }

  public void printState(){
	  System.out.println("State: In session");
  }

}
