import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class EncryptedClient {
	public static void main(String sa[]) {
		String receivedMessage = "";
		String sendMessage = "";
		int port = 0;
		String ip = "";
		
		if(sa.length == 2) {
			try {
				port = Integer.parseInt(sa[1]);
			} catch(NumberFormatException nfe) {
				System.out.println("[Client] Specified port must be an integer.");
				return;
			}
			
			ip = sa[0];
			String temp[] = ip.split("\\.");
			if(temp.length != 4) {
				System.out.println("[Client] Invalid IP address. Must be in the form XXX.XXX.XXX.XXX");
				return;
			} else {
				for(String s : temp) {
					try {
						int i = Integer.parseInt(s);
						if(i > 255 || i < 0) {
							System.out.println("[Client] Invalid IP address");
							return;
						}
					} catch(NumberFormatException nfe) {
						System.out.println("[Client] All parts of IP address must be integers.");
						return;
					}
				}
			}
			
			System.out.println("[Client] Attempting to connect to " + ip + ":" + port);	
			try {
				Socket socket = new Socket(ip, port);
				
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
				sendMessage = "Hello World!\n";
				output.writeBytes(sendMessage);
				sendMessage = "";
				System.out.println("[Client] Message sent to server.");
				
				System.out.println("[Client] Waiting for responce from server.");
				receivedMessage = input.readLine();
				System.out.println("[Client] Response from server: " + receivedMessage);
				receivedMessage = "";
				
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} else {
			System.out.println("Arguments may only include IP address of server process, and port number.");
			return;
		}
	}
}
