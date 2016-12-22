import java.util.Scanner;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class Main {
	public static SerialPort serialPort;
	public static final long timeout = 4000L;
	public static void main(String[] args) throws InterruptedException, SerialPortException, SerialPortTimeoutException, InvalidLedDeviceException {
		LedControler controler = new LedControler("COM5");
//		for(int r = 0; r < 256; r ++){
		for(int i = 0; i < 50; i++){
			controler.set(255, 0, 0, i);
			Thread.sleep(5L);
		}
//		}
		System.exit(0);
//		controler.show();
//		serialPort = new SerialPort("COM5");
//		try {
//			serialPort.openPort();
//
//			serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//
//			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
//
//			serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
//			System.out.println(serialPort.readString());
//			System.out.println(serialPort.isOpened());
//			
////			Thread.sleep(3000);
////			serialPort.writeString("0 0 255 2");
////			serialPort.writeString("0 0 255 -1");
//			Scanner scan = new Scanner(System.in);
//			while(true){
//				serialPort.writeString(scan.nextLine());
//			}
//		} catch (SerialPortException ex) {
//			System.out.println("There are an error on writing string to port : " + ex);
//		}

	}

	private static class PortReader implements SerialPortEventListener {
		@Override
		public void serialEvent(SerialPortEvent event) {
			System.out.print(event.getEventValue());
//			if (event.isRXCHAR() && event.getEventValue() > 0) {
//				try {
//					String receivedData = serialPort.readString(event.getEventValue());
//					
//					System.out.println("Received response: " + receivedData);
//					
//				} catch (SerialPortException ex) {
//					System.out.println("Error in receiving string from COM-port: " + ex);
//				}
//			}
		}

	}
}