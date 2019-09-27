import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.xml.crypto.Data;

public class guessthewordserver{

    public static void main(String args[]) throws IOException {

        if (args.length != 1){
            System.err.println("Usage: java guessthewordserver <word>");
            System.exit(1);
        }

        String word = args[0];
        byte[] data = new byte[65535];
        InetAddress clientHost;
        int clientPort;
        GuessTheWordGame game;
        DatagramSocket dSocket = null;
        DatagramPacket packet = new DatagramPacket(data, data.length);

        try{
            dSocket = new DatagramSocket(5001);

            while(true){

                System.out.println("SERVER READY!");
                // Vänta in klient
                String recMessage = receiveMessage(dSocket, packet);
                clientHost = packet.getAddress();
                clientPort = packet.getPort();
                // Inled spel
                if(recMessage.equals("hello")){
                    // Spara klientinfo

                    System.out.println("Client: " + recMessage);

                    game = new GuessTheWordGame(clientHost, clientPort, word, dSocket);

                    if(game.initiateGame()){
                      game.playGame();
                      while(game.getHasNewClient() == true){
                        game.setHasNewClient(false);
                        if(game.initiateGame()){
                          game.playGame();
                        }

                      }

                    }

                } else {
                    sendMessage("ERROR", dSocket, packet, clientHost, clientPort);
                }

            }

        } catch (SocketException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            if (dSocket != null) dSocket.close();
        }
    }

    public static void resetServer(InetAddress clientHost, int clientPort, DatagramPacket packet){
        packet.setData(new byte[65535]);
        packet.setLength(new byte[65535].length);
        clientHost = null;
        clientPort = -1;
    }

    public static void sendMessage(String message, DatagramSocket dSocket, DatagramPacket packet, InetAddress clientHost, int clientPort) throws IOException{
        packet.setData(message.getBytes());
        packet.setLength(message.getBytes().length);
        packet.setAddress(clientHost);
        packet.setPort(clientPort);
        dSocket.send(packet);
    }

    public static String receiveMessage(DatagramSocket dSocket, DatagramPacket packet) throws IOException{
        packet.setData(new byte[65535]);
        packet.setLength(new byte[65535].length);
        dSocket.receive(packet);
        return new String(packet.getData(),0,packet.getLength());
    }



}
