import java.net.*;
import java.util.*;
import java.io.*;


public class GuessTheWordGame{

    private InetAddress clientHost;
    private int clientPort;
    private char[] word;
    private byte[] data = new byte[65535];
    private DatagramPacket packet = new DatagramPacket(data, data.length);
    private DatagramSocket dSocket;
    private boolean isWordComplete=false;
    private char[] currentWord;
    private int nrOfGuesses;
    private int nrOfGuessesStat;
    private Scanner scanner;

    private final String SERVER_BUSY = "SERVER BUSY!";
    private final String SERVER_READY = "READY!";
    private final String SERVER_ERROR = "ERROR";

    private final String CLIENT_MESSAGE_START = "start";
    private final String CLIENT_MESSAGE_HELLO = "hello";
    private final String CLIENT_TIMED_OUT = "TIMED OUT";



    public GuessTheWordGame(InetAddress clientHost, int clientPort, String word, DatagramSocket dSocket){
        this.clientHost = clientHost;
        this.clientPort = clientPort;
        this.word = word.toLowerCase().toCharArray();
        this.dSocket = dSocket;
        this.packet.setAddress(clientHost);
        this.packet.setPort(clientPort);
        this.currentWord = new char[word.length()];
        this.scanner = new Scanner(System.in);
        this.nrOfGuesses = word.length();
        this.nrOfGuessesStat = word.length();
        loadWord();
    }

    private void loadWord(){
        for(int i=0;i<word.length;i++){
            currentWord[i] = '*';
        }
    }

    public boolean initiateGame() throws IOException{
        long startTime;
        long waitTime;

        String message =  "OK";
        sendMessage(message.toLowerCase(), dSocket, packet);

        // Inled spel, 10s för klient att bekräfta spel, andra klienter svaras med BUSY
        startTime = System.currentTimeMillis();
        waitTime = 10000;

        while(true){

            message = receiveMessage(this.dSocket, this.packet);
            System.out.println("Client: " + message);

            // Timeout eller inte
            if((System.currentTimeMillis() - startTime) < waitTime){

                // Klient, meddelande och timeout är ok
                if(isClientValid(packet) && message.equals(CLIENT_MESSAGE_START)){
                    sendMessage(new String(SERVER_READY + this.word.length), dSocket, packet);
                    return true;
                }
                // Klient skickar fel
                else if(isClientValid(packet) && !message.equals(CLIENT_MESSAGE_START)){
                    sendMessage(SERVER_ERROR, dSocket, packet);
                }
                // Fel klient
                else {
                    sendMessage(SERVER_BUSY, dSocket, packet);
                //    return false;
                }

            } else {
                // Timeout, rätt klient
                if(isClientValid(packet)){
                    sendMessage(CLIENT_TIMED_OUT, dSocket, packet);

                    return false;
                }
                // Timeout, annan klient -HELLO
                if(!isClientValid(packet) && message.equals(CLIENT_MESSAGE_HELLO)){
                    sendMessage(SERVER_BUSY, dSocket, packet);
                    packet.setAddress(clientHost);
                    packet.setPort(clientPort);
                    return false;
                }


            }

        }


    }

    private boolean isClientValid(DatagramPacket packet){
        if(this.clientPort == packet.getPort() && this.clientHost.equals(packet.getAddress())){
            return true;
        }else{
            return false;
        }

    }

    public void playGame() throws IOException{

        long startTime;
        long waitTime;

        System.out.println("Playing");

        //Game loop
        String guess;
        while(!Arrays.equals(word, currentWord) && nrOfGuesses > 0){

            startTime = System.currentTimeMillis();
            waitTime = 10000;
            // sätt timeout väntan på client
            dSocket.receive(packet);
            System.out.println((System.currentTimeMillis() - startTime));

            if((System.currentTimeMillis() - startTime) > waitTime){
                System.out.println("Client disconnected");
                return;
            }

            if(isClientValid(packet)){
                guess = new String(packet.getData(),0,packet.getLength());

                nrOfGuesses--;

                isLetterInWord(guess);

                System.out.println("Client guess: " + guess + ", nr of guesses: " + nrOfGuesses);
                System.out.println("Current word: " + String.valueOf(currentWord));

                sendMessage(String.valueOf(currentWord) + ", guesses: " + nrOfGuesses + "/" + nrOfGuessesStat, dSocket, packet);


            }else{
                packet.setData(SERVER_BUSY.getBytes());
                packet.setLength(SERVER_BUSY.getBytes().length);
                dSocket.send(packet);
            }

        }

        if(nrOfGuesses > 0){
            sendMessage("solved", dSocket, packet);
            System.out.println("SOLVED!");
        } else {
            sendMessage("out of guesses", dSocket, packet);
            System.out.println("OUT OF GUESSES!");

        }

    }

    private boolean isLetterInWord(String letter){
        boolean letterInWord = false;
            for(int i=0;i<word.length;i++){
                if (Character.compare(word[i], letter.charAt(0)) == 0){
                    currentWord[i]= letter.charAt(0);
                    letterInWord=true;
                }

            }

        return letterInWord;
    }

    private void sendMessage(String message, DatagramSocket dSocket, DatagramPacket packet) throws IOException{
        packet.setData(message.getBytes());
        packet.setLength(message.getBytes().length);
        dSocket.send(packet);
    }

    private String receiveMessage(DatagramSocket dSocket, DatagramPacket packet) throws IOException{
        packet.setData(new byte[65535]);
        packet.setLength(new byte[65535].length);
        dSocket.receive(packet);
        return new String(packet.getData(),0,packet.getLength());
    }

    public String getCurrentWord(){
        return String.valueOf(currentWord);
    }

}
