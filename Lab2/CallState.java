import java.util.*;
import java.net.*;
import java.io.*;

public abstract class CallState{

  PrintWriter out;

  public CallState receivedInvite(){
    return this;
  }

  public CallState answerCall(){
    return this;
  }

  public CallState receivedAck(){
    return this;
  }

  public CallState timedOut(){
    return this;
  }

  public CallState receivedBye(){
    return this;
  }

  public CallState receivedOk(){
    return this;
  }
  
  public CallState userWantsToInvite(){
    return this;
  }

  public CallState userWantsToQuit(){
    return this;
  }

  public void printState(){
    ;
  }

  

}
