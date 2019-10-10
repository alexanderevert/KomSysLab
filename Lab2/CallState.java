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

public CallState userWantsToInvite(boolean faulty,  String faultyMsg, Socket clientSocket){
  error();
  return new CallStateFree();
}

public void printState(){
}

public CallState receivedInvite(AudioStreamUDP audioStream, boolean faulty, String faultyMsg, Socket clientSocket){
  error();
  return new CallStateFree();
}

public CallState receivedTro(InetAddress ip, int udpPort, AudioStreamUDP audioStream, boolean faulty, String faultyMsg, Socket clientSocket) {
  error();
  return new CallStateFree();
}


public CallState userWantsToQuit(AudioStreamUDP audioStream, boolean faulty, String faultyMsg, Socket clientSocket) {
  error();
  return new CallStateFree();
}

public CallState receivedBye(AudioStreamUDP audioStream,  boolean faulty, String faultyMsg, Socket clientSocket) {
  error();
  return new CallStateFree();
}

public CallState receivedOk(AudioStreamUDP audioStream) {
  error();
  return new CallStateFree();
}

public CallState receivedAck(InetAddress ip, int udpPort, AudioStreamUDP audioStream, Socket clientSocket) {
  error();
  return new CallStateFree();
}

public CallState receivedBusy(){
  error();
  return new CallStateFree();
}

private CallState error(){
  System.out.println("Going to state CallStateFree");
  return new CallStateFree();

}


}
