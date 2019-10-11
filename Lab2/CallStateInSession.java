import java.util.*;
import java.net.*;
import java.io.*;

public class CallStateInSession extends CallStateBusy{
  public CallStateInSession(){

  }

  public CallState receivedBye(AudioStreamUDP audioStream, boolean faulty, String faultyMsg, Socket clientSocket){

    PrintWriter out = null;
    try{
      out = new PrintWriter(clientSocket.getOutputStream(), true);
    }catch(IOException e){
      e.printStackTrace();
    }

    String msg = null;
    if(faulty){
      msg = faultyMsg;
    }else{
      msg = "ok";
    }

    try{
      out.println(msg);
    }catch(Exception e){
      System.out.println("Failed to send OK");
    }

    audioStream.stopStreaming();
    System.out.println("Going to state CallStateFree");
    return new CallStateFree();


  }

  public CallState userWantsToQuit(AudioStreamUDP audioStream, boolean faulty,  String faultyMsg, Socket clientSocket){
    PrintWriter out = null;
    try{
      out = new PrintWriter(clientSocket.getOutputStream(), true);
    }catch(IOException e){
      e.printStackTrace();
    }

    String msg = null;
    if(faulty){
      msg = faultyMsg;
    }else{
      msg = "bye";
    }

    if(msg.equals("bye")){
      try{
        out.println(msg);
      }catch(Exception e){
        audioStream.stopStreaming();
        System.out.println("Failed to send BYE");
        return error();
      }
      
      try{
        clientSocket.setSoTimeout(5000);
      }catch(SocketException e ){
        System.out.println("Could not set timeout");
        try{
          clientSocket.close();
        }catch(Exception ie){
          ie.printStackTrace();
        }

        return error();
      }
      System.out.println("Going to state CallStateWaitQuitOK");
      audioStream.stopStreaming();
      return new CallStateWaitQuitOK();
    }
      System.out.println("Wrong Bye-message");
      audioStream.stopStreaming();

      return error();
  }

  public void printState(){
	  System.out.println("State: In session");
  }

}
