import java.util.*;
import java.net.*;
import java.io.*;

public class CallStateInSession extends CallState{
  public CallStateInSession(){

  }

  public CallState receivedBye(PrintWriter out){
    System.out.println("Going to state CallStateFree");

    //TODO: skicka ok
    return new CallStateFree();
  }

  public CallState userWantsToQuit(PrintWriter out){
    //TODO: skicka bye
    System.out.println("Going to state CallStateWaitQuitOK");
    return new CallStateWaitQuitOK();
  }

  public void printState(){
	  System.out.println("State: In session");
  }

}
