import java.util.*;
import java.net.*;
import java.io.*;

public abstract class CallState{

  

  public boolean busy(){return false;}

  public CallState timedOut(){
    return this;
  }
  
  public CallState userWantsToInvite(PrintWriter out){
    return this;
  }

  public void printState(){
  }

public CallState receivedInvite(AudioStreamUDP audioStream, PrintWriter out) {
	return null;
}

public CallState answerCall(InetAddress ip, int udpPort, AudioStreamUDP audioStream, PrintWriter out) {
	return null;
}

public CallState userWantsToQuit(AudioStreamUDP audioStream, PrintWriter out) {
	return null;
}

public CallState receivedBye(AudioStreamUDP audioStream, PrintWriter out) {
	return null;
}

public CallState receivedOk(AudioStreamUDP audioStream) {
	return null;
}

public CallState receivedAck(InetAddress ip, int udpPort, AudioStreamUDP audioStream) {
	return null;
}

  

}
