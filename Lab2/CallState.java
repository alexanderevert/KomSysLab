import java.util.*;
import java.net.*;
import java.io.*;

public abstract class CallState{

  

  public boolean busy(){return false;}

  public CallState receivedInvite(PrintWriter out){
    //error(); return new Free();
    return this;
  }

  public CallState answerCall(PrintWriter out){
    return this;
  }

  public CallState receivedAck(){
    return this;
  }

  public CallState timedOut(){
    return this;
  }

  public CallState receivedBye(PrintWriter out){
    return this;
  }

  public CallState receivedOk(){
    return this;
  }
  
  public CallState userWantsToInvite(PrintWriter out){
    return this;
  }

  public CallState userWantsToQuit(PrintWriter out){
    return this;
  }

  public void printState(){
    ;
  }

  

}
