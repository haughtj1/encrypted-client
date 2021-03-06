import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Random;

public class EncryptedClient {
	private static Random rand;
	public static void main(String sa[]) {
		rand = new Random(System.currentTimeMillis());
		int pad = 0;
		String inputBuf = "";
		String outputBuf = "";
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
				byte[] buf = new byte[5096];
				
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
				//the client first request the key from the server process
				outputBuf = "KEYREQ\n";
				output.writeBytes(outputBuf);
				outputBuf = "";
				System.out.println("[Client] Public Key Requested.");
				
				//the keypair is then read in
				inputBuf = input.readLine();
				String tmp = inputBuf.substring(0, inputBuf.indexOf(":"));
				BigInteger e = new BigInteger(tmp);
				tmp = inputBuf.substring(inputBuf.indexOf(":") + 1);
				BigInteger n = new BigInteger(tmp);
				inputBuf = "";				
				
				//now we can encrypt and send our messages
				String privateMessage = "THIS INFORMATION IS PRIVATE 946840";
				System.out.println("[Client] Secret message is: " + privateMessage);
				
				for(int i = 0; i < 3; i++) {
					pad = randPad();
					privateMessage = padString(privateMessage, pad);
				}
				System.out.println("[Client] Message with padding: " + privateMessage);
				
				System.out.println("[Client] Now sending our secret message [" + privateMessage + "]");
				BigInteger enc = new BigInteger(privateMessage.getBytes());
				enc = enc.modPow(e, n);
				outputBuf = enc.toString() + "\n";
				output.writeBytes(outputBuf);	
				System.out.println("[Client] Secret message sent, closing connection.");
				
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
	
	public static int randPad() {
		return rand.nextInt(9) + 1;
	}
	
	public static char randChar() {
		final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+=-<>,~";
		final int N = alphabet.length();
		return alphabet.charAt(rand.nextInt(N));
	}
	
	public static String padString(String text, int pad) {
		
		StringBuilder sb = new StringBuilder(text.length() + (text.length()/pad) + 1);

		int index = 0;
		char prefix = Character.MIN_VALUE;
		while(index < text.length()) {
			sb.append(prefix);
			prefix = randChar();
			sb.append(text.substring(index, Math.min(index + pad, text.length())));
			index += pad;
		}
		
		sb.insert(0, pad);
		
		return sb.toString();	
	}
}
