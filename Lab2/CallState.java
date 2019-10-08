import java.util.*;
import java.net.*;
import java.io.*;

public abstract class CallState{



public boolean busy(){
  return false;
}

public CallState timedOut(){
  error();
  return new CallStateFree();
}

public CallState userWantsToInvite(PrintWriter out){
  error();
  return new CallStateFree();
}

public void printState(){
}

public CallState receivedInvite(AudioStreamUDP audioStream, PrintWriter out){
  error();
  return new CallStateFree();
}

public CallState answerCall(InetAddress ip, int udpPort, AudioStreamUDP audioStream, PrintWriter out) {
  error();
  return new CallStateFree();
}


public CallState userWantsToQuit(AudioStreamUDP audioStream, PrintWriter out) {
  error();
  return new CallStateFree();
}
public CallState receivedBye(AudioStreamUDP audioStream, PrintWriter out) {
  error();
  return new CallStateFree();
}

public CallState receivedOk(AudioStreamUDP audioStream) {
  error();
  return new CallStateFree();
}

public CallState receivedAck(InetAddress ip, int udpPort, AudioStreamUDP audioStream) {
  error();
  return new CallStateFree();
}

private void error(){
  System.out.println("Error");
}


}
