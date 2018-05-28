package thymeClient;

import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class thymeCli {
	static Led led;
	
	public static void main(String[] args)
	{
		Scanner keyboard = new Scanner(System.in);
		String mName = "";
		String mPower = "";
		String mColor = "";
		int mTerm = -1;
		int mIlluminance = -1;
		
		/* initialize led object  */
		System.out.println("Initializing!");
		System.out.print("input name, power, color, term, illuminance > ");
		mName = keyboard.next();
		mPower = keyboard.next();
		mColor = keyboard.next();
		mTerm = keyboard.nextInt();  // seconds
		mIlluminance = keyboard.nextInt();
		led = new Led(mName, mPower, mColor, mTerm, mIlluminance);
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (led.getColor().equals("red"))
					led.setColor("green");
				if (led.getColor().equals("green"))
					led.setColor("red");
			}
		};
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, mTerm * 1000);

		try
		{
			Socket socket = new Socket();
			SocketAddress address = new InetSocketAddress("127.0.0.1", 3105);
			socket.connect(address);
			
			String message;
			String readMessage;
			String readArray[];
			int read;
			while (true) {				
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
					
				System.out.println(readMessage);
				// process changing TAS client
				int cIlluminance = -1;
				int cTerm = -1;
				
				readArray = readMessage.split(" ");
				if (readArray[0].equals("finish"))
					break;
				/* turn <> */
				if (readArray[0].equals("turn")) {
					if (readArray[1].equals("on"))
						led.setPower("on");
					if (readArray[1].equals("off"))
						led.setPower("off");
				}
				/* change <> <> */
				if (readArray[0].equals("change")) {
					if (readArray[1].equals("color")) {
						if (readArray[2].equals("red"))
							led.setColor("red");
						if (readArray[2].equals("green"))
							led.setColor("green");
					}
					if (readArray[1].equals("illuminance")) {
						cIlluminance = Integer.valueOf(readArray[2]);
						led.setIlluminance(cIlluminance);
					}
					if (readArray[1].equals("term")) {
						cTerm = Integer.valueOf(readArray[2]);
						led.setTerm(cTerm);
						timer.cancel();
						timer.scheduleAtFixedRate(task, 0, cTerm * 1000);
					}
				}
					
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