package org.jmhsrobotics.leddriver;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class Main {
	public static SerialPort serialPort;
	public static final long timeout = 4000L;

	public static void main(String[] args) throws InterruptedException, SerialPortException, SerialPortTimeoutException, InvalidLedDeviceException {
		LedControler controler = new LedControler("COM5");
		//		controler.toggleAutoShow();

		for (int j = 0; j < 500 / 2; j++) {
			color(controler, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
			controler.show();
			random(controler);
			controler.show();
			color(controler, 255, 255, 255);
			controler.show();
			color(controler, 0, 0, 0);
			controler.show();
		}
		Thread.sleep(5000);

		System.exit(0);

	}

	private static void random(LedControler c) throws SerialPortException {
		for (int i = 0; i < 50; i++) {
			c.set((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255), i);
		}
	}

	private static void color(LedControler c, int r, int g, int b) throws SerialPortException {
		for (int i = 0; i < 50; i++) {
			c.set(r, g, b, i);
		}
	}
}