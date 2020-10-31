
/**
 * Author: Christopher Brislin brislinchris@gmail.com
 * Date: 25 Oct 2020
 * Title of code: SerialMonitor
 * Version: 1.0
 * 
 */

import java.awt.Color;
import java.nio.charset.StandardCharsets;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class PortBuilder {

	SerialPort port;

	public SerialPort[] portsAvailable() {
		// return an array of available serial ports
		SerialPort[] portList = SerialPort.getCommPorts();
		return portList;

	}

	public void portBuild(SerialPort port, int baud) {
		// Open the selected port with the selected baud rate
		this.port = port;
		this.port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0); // default parameters used for non-blocking
																			// serial poling
		this.port.setBaudRate(baud);// default parameters used for data bits, parity and stop bits. To be added.
		this.port.openPort();
		if (portIsOpen()) {
			// display this message if the port opens successfully
			Interface.updateMessage("Port Open\n", Color.GREEN);
		}

		/*
		 * Will Hedgecock 21 March 2019 Data Available for Reading Example
		 * https://github.com/Fazecast/jSerialComm/wiki/Event-Based-Reading-Usage-
		 * Example
		 */

		// add a serial event listener to the port.
		this.port.addDataListener(new SerialPortDataListener() {
			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
			} // Returns listening event when data AVAILABLE

			@Override
			public void serialEvent(SerialPortEvent event) {
				if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
					return; // if the event is not the correct type return.
				}

				byte[] newData = new byte[port.bytesAvailable()]; // initialise a byte array to the size of the
																	// available data.
				port.readBytes(newData, newData.length); // read the bytes from the serial port into the byte array.
				Interface.updateData(new String(newData, StandardCharsets.UTF_8)); // create a string from the byte
																					// array and append it.
			}
		});

	}

	public boolean portIsOpen() {
		return port.isOpen(); // getter for port status
	}

	public void closePort() {
		port.closePort(); // close port and update message
		Interface.updateMessage("Port Closed\n", Color.RED);

	}

}
