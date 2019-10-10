import java.util.*;
import java.net.*;
import java.io.*;

public abstract class CallState{


public boolean busy(){
  return false;
}

public CallState timedOut(){
  return error();

}

public CallState userWantsToInvite(boolean faulty,  String faultyMsg, Socket clientSocket){
  return error();

}

public void printState(){
}

public CallState receivedInvite(AudioStreamUDP audioStream, boolean faulty, String faultyMsg, Socket clientSocket){
  return error();

}

public CallState receivedTro(InetAddress ip, int udpPort, AudioStreamUDP audioStream, boolean faulty, String faultyMsg, Socket clientSocket) {
  return error();
}


public CallState userWantsToQuit(AudioStreamUDP audioStream, boolean faulty, String faultyMsg, Socket clientSocket) {
  return error();

}

public CallState receivedBye(AudioStreamUDP audioStream,  boolean faulty, String faultyMsg, Socket clientSocket) {
  return error();

}

public CallState receivedOk(AudioStreamUDP audioStream) {
  return error();

}

public CallState receivedAck(InetAddress ip, int udpPort, AudioStreamUDP audioStream, Socket clientSocket) {
  return error();

}

public CallState receivedBusy(){
  return error();
}

private CallState error(){
  return error();
}


}
