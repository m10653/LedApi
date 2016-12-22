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
		for(int i = 0; i < 50; i++){
			controler.set(255, 0, 0, i);
			Thread.sleep(5L);
		}
		System.exit(0);

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