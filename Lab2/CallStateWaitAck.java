import java.util.*;
import java.net.*;
import java.io.*;

public class CallStateWaitAck extends CallStateBusy{
  public CallStateWaitAck(){
  }

  public CallState receivedAck(InetAddress ip, int udpPort, AudioStreamUDP audioStream){
    try{
      audioStream.connectTo(ip, udpPort);
      System.out.println("To hang up: <hangup>");
      audioStream.startStreaming();
    }catch(IOException e){
      System.out.println("UDP Connection error");
      return new CallStateFree();
    }
    System.out.println("Going to state CallStateInSession");
    return new CallStateInSession();
  }

  public CallState timedOut(){
    System.out.println("Going to state CallStateFree");
    return new CallStateFree();
  }

  public void printState(){
	  System.out.println("State: Waiting ACK");
  }



}
