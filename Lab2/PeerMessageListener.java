import java.util.*;
import java.net.*;
import java.io.*;

class PeerMessageListener implements Runnable{
    private Socket clientSocket;
    private BufferedReader in;
    private CallHandler callHandler;
    private boolean running;
    private boolean faulty;

    public PeerMessageListener(Socket clientSocket, BufferedReader in, CallHandler callHandler, boolean faulty){
          this.clientSocket = clientSocket;
          this.in = in;
          this.callHandler = callHandler;
          this.faulty = faulty;
          running = true;
          callHandler.setClientSocket(clientSocket);
    }

    @Override
    public void run(){
      try{
        clientSocket.setSoTimeout(20000);
      }catch(SocketException e){
        e.printStackTrace();
      }
          while(running){
            try{
                Thread.sleep(100);
            } catch(InterruptedException e){
            }
              String message = null;
                try{
                  message = in.readLine();
                } catch(Exception e){
                  message = "timeout";
                  running = false;
                }
                if(message == null){
                  message = "timeout";
                }
                  System.out.println("Received: " + message);
                  if(message.startsWith("tro")){

                    String[] arr = message.split(",");   // all denna logik i receivedTro
                    if(arr.length != 2){
                      message = "ERROR";
                    }else{
                      String udpPort = arr[1];
                      try{
                        int port = Integer.parseInt(udpPort);
                        if(faulty){
                          callHandler.setFaultyMessage(null);
                          System.out.println("Write ack, or not: ");
                          Call.setFaultyMsg(callHandler);
                        }
                        callHandler.setUdpPort(port);
                        callHandler.processNextEvent(CallHandler.CallEvent.TRO);
                        message = "TRO";
                      }catch(NumberFormatException e){
                        System.out.println("Could not read port number");
                      }
                    }
                  }else if(message.startsWith("ack")){
                    String[] arr = message.split(",");
                    if(arr.length != 2){
                      message = "ERROR";
                    }else{
                      String udpPort = arr[1];
                      try{
                        int port = Integer.parseInt(udpPort);
                        callHandler.setUdpPort(port);
                        callHandler.processNextEvent(CallHandler.CallEvent.ACK);
                        message = "ACK";
                      }catch(NumberFormatException e){
                        System.out.println("Could not read port number");
                      }
                    }
                  }

                  switch(message.trim().toLowerCase()){
                    case "bye":
                    if(faulty){
                      callHandler.setFaultyMessage(null);
                      System.out.println("Write ok, or not: ");
                      Call.setFaultyMsg(callHandler);
                      }
                      callHandler.processNextEvent(CallHandler.CallEvent.BYE);
                      running = false;
                      break;
                    case "ok":
                      callHandler.processNextEvent(CallHandler.CallEvent.OK);

                      running = false;
                      break;
                    case "tro":
                      break;
                    case "ack":
                      break;
                    case "busy":
                      System.out.println("Busy");
                      callHandler.processNextEvent(CallHandler.CallEvent.BUSY);
                      running = false;
                    break;
                    case "timeout":
                      callHandler.processNextEvent(CallHandler.CallEvent.TIMEOUT);
                      running = false;
                      break;
                    case "reject":
                        callHandler.processNextEvent(CallHandler.CallEvent.TIMEOUT);
                        running = false;
                        break;
                    default:
                        System.out.println("Wrong message");
                        callHandler.processNextEvent(CallHandler.CallEvent.TIMEOUT);
                        running = false;
                      break;
                  }
            }
      }
  }
