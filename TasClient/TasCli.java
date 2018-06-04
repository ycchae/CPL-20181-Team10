import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class TasCli {
	static Led led;
	static Socket socket;
	static String pString = "";
	
	public static void main(String[] args)
	{
		//String message;
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
				else
					led.setColor("red");
				
				pString = led.toString();
				write();
				System.out.println("color is changed!");
				System.out.println(led.toString());
			}
		};
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, mTerm * 1000, mTerm * 1000);
		
		try
		{
			socket = new Socket();
			SocketAddress address = new InetSocketAddress("127.0.0.1", 3105);
			socket.connect(address);
			
			write();
			byte[] temp = new byte[4096];
			socket.getInputStream().read(temp);
			pString = led.toString();
			
			String readMessage;
			String readArray[];
			String command;
			String commandArray[];
			int readlen;
			int previousTerm = 0;
			
			while (true) {
				//read command from rosemary				
				byte[] buf = new byte[4096];
				readMessage = "";
				readlen = 0;
				readlen = socket.getInputStream().read(buf);
				readMessage = new String(buf, 0, readlen);
			
				// process changing TAS client				
				// decode command 
				readArray = readMessage.split("\"");
				command = readArray[3];
		
				if(command.equals(pString))
					continue;
				
				System.out.println("command is arrived!");
				System.out.println(command);
				commandArray = command.split(" ");
				
				if (command.equals("finish"))
					break;
				
				// print log
				if (!led.getPower().equals(commandArray[3]))
					System.out.println("command : power change to " + commandArray[3]);
				if (!led.getColor().equals(commandArray[5]))
					System.out.println("command : color change to " + commandArray[5]);
				if (led.getTerm() != (int)Float.parseFloat(commandArray[7]))
					System.out.println("command : term change to " + commandArray[7]);
				if (led.getIlluminance() != (int)Float.parseFloat(commandArray[9]))
					System.out.println("command : illuminance change to " + commandArray[9]);
				
				previousTerm = led.getTerm();
				led.setPower(commandArray[3]);
				led.setColor(commandArray[5]);
				led.setTerm((int)Float.parseFloat(commandArray[7]));
				led.setIlluminance((int)Float.parseFloat(commandArray[9]));
				if (previousTerm != led.getTerm()) {
					task.cancel();
					task = null;
					task = new TimerTask() {
						@Override
						public void run() {
							if (led.getColor().equals("red"))
								led.setColor("green");
							else
								led.setColor("red");
							
							pString = led.toString();
							write();
							System.out.println("color is changed!");
							System.out.println(led.toString());
						}
					};
					timer.scheduleAtFixedRate(task, led.getTerm() * 1000, led.getTerm() * 1000);
				}
				
				// write to rosemary
				pString = led.toString();
				write();
			}
			socket.close();
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
		keyboard.close();
	}
	
	public static void write() {
		String message = "";
		message = "{ \"ctname\": \"" + led.getName() + "\", \"con\": \"" + led.toString() +"\" }";
		
		try {
			socket.getOutputStream().write(message.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}	