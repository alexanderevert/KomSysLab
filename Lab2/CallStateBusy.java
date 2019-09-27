import java.util.*;
import java.net.*;
import java.io.*;

public class CallStateBusy extends CallState{

  public CallStateBusy(){

  }
  /*
  public CallState busy(){
    return this;
  }
  public CallState free(){
    System.out.println("Busy");
    return this;
  }
  */
  public CallState receivedInvite(){
    //TODO: skicka busy
    // ex out.println(); PrintWriter out inlagd i superclassen CallState, sätts i Handlern
    System.out.println("Peer busy, already in call");
    return this;
  }

}
