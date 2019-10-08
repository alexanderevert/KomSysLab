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

public CallState userWantsToInvite(PrintWriter out, boolean faulty, Scanner scanner, String faultyInvite){
  error();
  return new CallStateFree();
}

public void printState(){
}

public CallState receivedInvite(AudioStreamUDP audioStream, PrintWriter out, boolean faulty, Scanner scanner, String faultyTro){
  error();
  return new CallStateFree();
}

public CallState answerCall(InetAddress ip, int udpPort, AudioStreamUDP audioStream, PrintWriter out, boolean faulty, Scanner scanner, String faultyAck) {
  error();
  return new CallStateFree();
}


public CallState userWantsToQuit(AudioStreamUDP audioStream, PrintWriter out, boolean faulty, Scanner scanner) {
  error();
  return new CallStateFree();
}
public CallState receivedBye(AudioStreamUDP audioStream, PrintWriter out, boolean faulty, Scanner scanner) {
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

public CallState receivedBusy(){
  error();
  return new CallStateFree();
}

private void error(){
  System.out.println("Going to state CallStateFree");
}


}
