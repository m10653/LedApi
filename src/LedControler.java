import java.util.concurrent.atomic.AtomicBoolean;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class LedControler {
	private SerialPort serialPort;
	private AtomicBoolean isReady = new AtomicBoolean(false);
//	private boolean autoShow = false;
	private byte byte5 = 0;
	private static final long TIMEOUT = 4000L;

	public LedControler(String port) throws SerialPortException, SerialPortTimeoutException, InvalidLedDeviceException {
		serialPort = new SerialPort(port);
		serialPort.openPort();
		serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
		serialPort.addEventListener(new SerialListener(serialPort));
		long startTime = System.currentTimeMillis();
		while (startTime+ TIMEOUT > System.currentTimeMillis()) {
			byte[] temp = serialPort.readBytes(1);
			if (temp.length > 0) {
				System.out.println((int) temp[0] & 0xFF);
				if ((temp[0] & 0xFF) == 255) {
					isReady.set(true);
					break;
				} else {
					throw new SerialPortTimeoutException(port,null,(int) TIMEOUT);
				}
			}
		}
		if (!isReady.get()) {
			throw new InvalidLedDeviceException();
		}

	}
	public synchronized boolean isReady(){
		return isReady.get();
	}
	public void show() throws SerialPortException{
		byte[] temp = {0,0,0,0, (byte) (byte5 | 0b11)}; // set first two bits on byte 5 to 1
		System.out.println(Integer.toBinaryString((temp[4] & 0xFF) + 0x100).substring(1));
		serialPort.writeBytes(temp);
	
		
	}
	
	public void autoShow(boolean flag){
		if(flag){
			byte5 |= 1; // toggle fisrt bit
		}else{
			byte5 |= 0; // toggle fisrt bit
		}
		System.out.print(Integer.toBinaryString((byte5 & 0xFF) + 0x100).substring(1) + " ");
		
	}
	public void toggleAutoShow(){
		byte5 ^= 1;
	}
	public void set(int r, int g, int b, int i) throws SerialPortException{
		if(i > 255 || i < 0){
			throw new IndexOutOfBoundsException(i+ "");
		}
		normalized(r);
		normalized(g);
		normalized(b);
		byte[] temp = {(byte) r,(byte) g,(byte) b,(byte) i,byte5};
		print(temp);
		serialPort.writeBytes(temp);
		
	}
	private void print(byte[] arr){
		for(byte b:arr){
			System.out.print(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1) + " ");
		}
		System.out.println();
	}
	private int normalized(int num){
		if(num > 255){
			num = 255;
		}else if(num < 0){
			num = 0;
		}
		return num;
	}

	private static class SerialListener implements SerialPortEventListener {
		private SerialPort serialPort;
		boolean first;

		public SerialListener(SerialPort serialPort) {
			this.serialPort = serialPort;
		}

		@Override
		public void serialEvent(SerialPortEvent event) {
			if (event.isRXCHAR() && event.getEventValue() > 0) {
				try {
					String receivedData = serialPort.readString(event.getEventValue());

					System.out.print(receivedData);

				} catch (SerialPortException ex) {
					System.out.println("Error in receiving string from COM-port: " + ex);
				}
			}
		}

	}

}
