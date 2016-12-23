import java.util.concurrent.atomic.AtomicBoolean;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class LedControler {
	private SerialPort serialPort;
	private AtomicBoolean isReady = new AtomicBoolean(false);
	private boolean autoShow = false;
	private static final long TIMEOUT = 4000L;
	private static final long SHOW_WAIT_TIME = 3500L;

	public LedControler(String port) throws SerialPortException, SerialPortTimeoutException, InvalidLedDeviceException {
		serialPort = new SerialPort(port);
		serialPort.openPort();
		serialPort.setParams(15000, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
		serialPort.addEventListener(new SerialListener(serialPort));
		long startTime = System.currentTimeMillis();
		while (startTime + TIMEOUT > System.currentTimeMillis()) {
			byte[] temp = serialPort.readBytes(1);
			if (temp.length > 0) {
				System.out.println((int) temp[0] & 0xFF);
				if ((temp[0] & 0xFF) == 255) {
					isReady.set(true);
					break;
				} else {
					throw new InvalidLedDeviceException();
				}
			}
		}
		if (!isReady.get()) {
			throw new SerialPortTimeoutException(port, null, (int) TIMEOUT);
		}

	}

	public synchronized boolean isReady() {
		return isReady.get();
	}

	public void show() throws SerialPortException {
		byte[] temp = { 0, 0, 0, (byte) 192 };
		print(temp);
		serialPort.writeBytes(temp);
		busyWait(SHOW_WAIT_TIME);

	}

	private void busyWait(long micros) {
		long waitUntil = System.nanoTime() + (micros * 1000);
		while (waitUntil > System.nanoTime())
			;
	}

	public void autoShow(boolean flag) {
		autoShow = flag;

	}

	public void toggleAutoShow() {
		autoShow = !autoShow;
	}

	public void set(int r, int g, int b, int i) throws SerialPortException {
		if (i > 63 || i < 0) {
			throw new IndexOutOfBoundsException(i + "");
		}
		normalized(r);
		normalized(g);
		normalized(b);
		byte[] temp = { (byte) r, (byte) g, (byte) b, (byte) (i + (autoShow ? 64 : 0)) };
		print(temp);
		serialPort.writeBytes(temp);
		if (autoShow) {
			busyWait(SHOW_WAIT_TIME);
		}

	}

	private void print(byte[] arr) {
		//		for(byte b:arr){
		//			System.out.print(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1) + " ");
		//		}
		//		System.out.println();
	}

	private int normalized(int num) {
		if (num > 255) {
			num = 255;
		} else if (num < 0) {
			num = 0;
		}
		return num;
	}

	private static class SerialListener implements SerialPortEventListener {
		private SerialPort serialPort;

		public SerialListener(SerialPort serialPort) {
			this.serialPort = serialPort;
		}

		@Override
		public void serialEvent(SerialPortEvent event) {
			if (event.isRXCHAR() && event.getEventValue() > 0) {
				try {
					String receivedData = serialPort.readString(event.getEventValue());

					System.out.println(receivedData);
				} catch (SerialPortException ex) {
					System.out.println("Error in receiving string from COM-port: " + ex);
				}
			}
		}

	}

}
