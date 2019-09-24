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
    private String serverBusy = new String("SERVER BUSY!");
    private int nrOfGuesses;
    private int nrOfGuessesStat;
    private Scanner scanner;

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

        // Server tillgänglig
        //System.out.println("Send message (OK): ");
        //String message = scanner.nextLine().toLowerCase();
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
                if(isClientValid(packet) && message.equals("start")){
                    sendMessage(new String("READY " + this.word.length), dSocket, packet);
                    return true;
                } 
                // Klient skickar fel
                else if(isClientValid(packet) && !message.equals("start")){
                    sendMessage("ERROR", dSocket, packet);
                    return false;
                } 
                // Fel klient
                else {
                    sendMessage("BUSY", dSocket, packet);
                    //return false;
                }

            } else {
                // Timeout, rätt klient
                if(isClientValid(packet)){
                    sendMessage("TIMED OUT", dSocket, packet);
                    return false;
                }
                // Timeout, annan klient -HELLO
                if(!isClientValid(packet) && message.equals("hello")){
                    clientHost = packet.getAddress();
                    clientPort = packet.getPort();
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

        
        System.out.println("Playing");

        //Game loop
        String guess;
        while(!Arrays.equals(word, currentWord) && nrOfGuesses > 0){
            
            // sätt timeout väntan på client?
            dSocket.receive(packet);

            if(isClientValid(packet)){
                nrOfGuesses--;
                guess = new String(packet.getData(),0,packet.getLength());

                isLetterInWord(guess);

                System.out.println("Client guess: " + guess + ", nr of guesses: " + nrOfGuesses);
                System.out.println("Current word: " + String.valueOf(currentWord));

                sendMessage(String.valueOf(currentWord) + ", guesses: " + nrOfGuesses + "/" + nrOfGuessesStat, dSocket, packet);

            }else{
                packet.setData(serverBusy.getBytes());
                packet.setLength(serverBusy.getBytes().length);
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