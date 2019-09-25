import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class guessthewordclient{
    public static void main(String[] args) throws IOException, UnknownHostException{

        if (args.length != 2) {
            System.err.println(
                "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }
        
        boolean exit = false;
        String host = new String(args[0]);
        InetAddress ip = InetAddress.getByName(host);
        int port = Integer.parseInt(args[1]);
        byte[] data = new byte[65535];
        Scanner scanner = new Scanner(System.in);
        DatagramPacket packet = new DatagramPacket(data, data.length);
        DatagramSocket dSocket = new DatagramSocket();
        String message = "";
        String returnedMessage = "";


        try{
            dSocket.connect(ip, port);
            while(!exit){
                boolean isStartInTime = false;
                // Skicka HELLO manuellt
                System.out.println("Send message: ");
                message = scanner.nextLine().toLowerCase();
                sendMessage(message, dSocket, packet);

                // Vänta OK, Timeout om inget svar från server på 10s
                dSocket.setSoTimeout(10000);
                try{
                    returnedMessage = receiveMessage(dSocket, packet);
                    System.out.println("Server: " + returnedMessage);
                } catch (SocketTimeoutException e){
                    returnedMessage = "SERVER TIMED OUT!";
                } 
                
                if(returnedMessage.equals("ok")){

                        while(isStartInTime){

                        System.out.println("Send message: ");
                        message = scanner.nextLine().toLowerCase();
                        sendMessage(message, dSocket, packet);

                        dSocket.setSoTimeout(10000);
                        returnedMessage = receiveMessage(dSocket, packet);
                        System.out.println("Server: " + returnedMessage);

                        if(returnedMessage.startsWith("READY", 0)){
                            System.out.println("Server: Guess a " + returnedMessage.charAt(6) + " letter word!");
                            gameMode(scanner, dSocket, ip, port, packet);
                            exit = true;
                        } else if(returnedMessage.equals("TIMED OUT")){
                            isStartInTime = false;
                        }

                    }
                }else {
                    System.out.println(returnedMessage.toUpperCase());
                }

            }

        } catch (SocketException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (dSocket != null) dSocket.close();   
            if (scanner != null) scanner.close();  
        }
    }

    public static void gameMode(Scanner scanner, DatagramSocket dSocket, InetAddress serverHost, int serverPort, DatagramPacket packet) throws IOException{
        
        // Spel-loop
        String guess;
        String currentWord = "";

        while(!currentWord.equals("game over")){
            System.out.println("Enter a letter: ");
            guess = new String(scanner.nextLine());

            if(guess.length() == 1 && Character.isLetter(guess.charAt(0))){
                sendMessage(guess.toLowerCase(), dSocket, packet);
                currentWord = receiveMessage(dSocket, packet);
                System.out.println("Current word: " + currentWord);
            }

            dSocket.setSoTimeout(100);
            
            try{
                currentWord = receiveMessage(dSocket, packet);
                if(currentWord.equals("solved")){
                    currentWord = new String("game over");
                    System.out.println("SOLVED!!!" + "\nShutting down..");
                } else if(currentWord.equals("out of guesses")){
                    currentWord = new String("game over");
                    System.out.println("OUT OF GUESSES!" + "\nShutting down..");
                }
            } catch (SocketTimeoutException e){
            }
            
        }

    }

    public static boolean retry(boolean exit, Scanner scanner){
        System.out.print("Try to connect again? (y/n): ");
        String ans = new String(scanner.next());
        System.out.println(ans);
        return !ans.equals("y");
    }

    public static void sendMessage(String message, DatagramSocket dSocket, DatagramPacket packet) throws IOException{
        packet.setData(message.getBytes());
        packet.setLength(message.getBytes().length);
        dSocket.send(packet);
    }

    public static String receiveMessage(DatagramSocket dSocket, DatagramPacket packet) throws IOException{
        packet.setData(new byte[65535]);
        packet.setLength(new byte[65535].length);
        dSocket.receive(packet);
        return new String(packet.getData(),0,packet.getLength());
    }
}