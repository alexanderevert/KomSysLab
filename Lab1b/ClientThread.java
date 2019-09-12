class ClientThread implements Runnable {
    
    private Scanner scanner = new Scanner(System.in); 
    private String alias; 
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket; 
      
    public ClientThread(Socket socket, String alias, DataInputStream in, DataOutputStream out) { 
        this.in = in; 
        this.dos = out; 
        this.alias = alias; 
        this.socket = socket; 
        
    } 

    @Override
    public void run() { 

    }


}
