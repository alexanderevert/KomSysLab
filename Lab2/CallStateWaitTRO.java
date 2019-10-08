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

    public CallState answerCall(InetAddress ip, int udpPort, AudioStreamUDP audioStream, PrintWriter out, boolean faulty, Scanner scanner, String faultyAck){


      String msg = null;
      if(faulty){

        //System.out.println("Type ack message:");
        //msg = scanner.nextLine();
        msg = faultyAck;
        System.out.println("You typed: " + msg);
      }else{
        msg = "ack";
      }

      System.out.println("Sending ACK");
      try{
        out.println(msg + ","+ audioStream.getLocalPort());
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

    public CallState receivedBusy(){
      System.out.println("The person you are trying to call is busy");
      System.out.println("Going to CallStateFree");
      return new CallStateFree();
    }

}
