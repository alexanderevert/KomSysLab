import java.net.InetAddress;
import java.util.Scanner;

/* This example demonstrates how to connect an AudioStream to
 * a remote host.
 * Usage: java Caller <callee's ip address>
 * The port corresponding callee is provided by the user
 * after startup.
 */

public class Caller {

	public static final int SIP_PORT = 5060;

	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			System.out.println("Usage: java Caller <callee's ip address>");
			System.exit(0);
		}

		Scanner scan = new Scanner(System.in);
		AudioStreamUDP stream = null;
		String reply;
		try {
			// The AudioStream object will create a socket,
			// bound to a random port.
			stream = new AudioStreamUDP();
			int localPort = stream.getLocalPort();
			System.out.println("Bound to local port = " + localPort);
			
			// Set the address and port for the callee.
			System.out.println("What's the remote port number?");
			reply = scan.nextLine().trim();
			int remotePort = Integer.parseInt(reply);
			InetAddress address = InetAddress.getByName(args[0]);
			System.out.println(address + ", " + remotePort);
			stream.connectTo(address, remotePort);

			System.out.println("Press ENTER to start streaming");
			reply = scan.nextLine();
			stream.startStreaming();

			System.out.println("Press ENTER to stop streaming");
			reply = scan.nextLine();
			stream.stopStreaming();
		}
		finally {
			if(stream != null) stream.close();
		}
	}
}
