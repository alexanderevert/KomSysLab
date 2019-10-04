import java.util.*;
import java.net.*;
import java.io.*;


public class CallStateWaitTRO extends CallStateBusy{
    public CallStateWaitTRO(){

    }

    public CallState timedOut(){
      System.out.println("Going to state CallStateFree");
      return new CallStateFree();
    }

    public CallState answerCall(InetAddress ip, int udpPort, AudioStreamUDP audioStream, PrintWriter out){
      System.out.println("Sending ACK");
      try{
        out.println("ack,"+ audioStream.getLocalPort());
      }catch(Exception e){
        e.printStackTrace();
      }
      try{
        audioStream.connectTo(ip, udpPort);
        audioStream.startStreaming();
      }catch(IOException ioe){
        System.out.println("UDP connection error");
        return new CallStateFree();
      }
      System.out.println("Going to state: CallStateInSession");
      return new CallStateInSession();
    }

    public void printState(){
      System.out.println("State: Waiting TRO");
    }

}
