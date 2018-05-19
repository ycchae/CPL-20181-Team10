package thymeClient;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class thymecli {
	public static void main(String[] args)
	{
		Scanner keyboard = new Scanner(System.in);
		String mName = "";
		String mPower = "";
		int mIlluminance = -1;

		try
		{
			Socket socket = new Socket();
			SocketAddress address = new InetSocketAddress("127.0.0.1", 3105);
			socket.connect(address);
			
			String message;
			String readMessage;
			int read;
			while (true) {
				System.out.print("input name, power, illuminance > ");
				mName = keyboard.next();
				mPower = keyboard.next();
				mIlluminance = keyboard.nextInt();
				if(mIlluminance == -1)
					break;
				Led led = new Led(mName, mPower, mIlluminance);
				// write led status to lavender
				message = "{ \"ctname\": \"cnt-led\", \"con\": \"" + led.toString() +"\" }";
				//message = "{ \"ctname\": \"cnt-led\", \"con\": \"" + "hello" +"\" }";
				socket.getOutputStream().write(message.getBytes());
				
				// read command from lavender
				byte[] buf = new byte[4096];
				readMessage = "";
				read = 0;
				read = socket.getInputStream().read(buf);
				readMessage = new String(buf, 0, read);
				
				// process changing TAS client
				System.out.println(readMessage);
				if (readMessage.equals("turn off"))
					led.setPower("off");
				if (readMessage.equals("turn on"))
					led.setPower("on");
				
				// print led status
				System.out.println(led.toString());
			

			}
			socket.close();
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
		keyboard.close();
	}
}
