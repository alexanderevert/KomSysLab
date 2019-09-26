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
  public CallState receivedInvite(PrintWriter pr){
    //TODO: skicka busy
    System.out.println("Peer busy, already in call");
    return this;
  }

}
