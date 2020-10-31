import java.awt.Color;
import java.nio.charset.StandardCharsets;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;


public class PortBuilder {

	SerialPort port;
	String data;

	public SerialPort[] portsAvailable() {
		SerialPort[] portList = SerialPort.getCommPorts();
		return portList;

	}


	public void portBuild(SerialPort port) {
		this.port = port;
		this.port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
		this.port.openPort();

	}

	public void portBuild(SerialPort port, int baud) {
		this.port = port;
		this.port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
		this.port.setBaudRate(baud);
		this.port.openPort();
		if (portIsOpen()) {
			Interface.updateMessage("Port Open\n", Color.GREEN);
		}
		
		this.port.addDataListener(new SerialPortDataListener() {
			   @Override
			   public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
			   @Override
			   public void serialEvent(SerialPortEvent event)
			   {
			      if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
			         return;
			      byte[] newData = new byte[port.bytesAvailable()];
			      port.readBytes(newData, newData.length);
			      Interface.updateData(new String(newData, StandardCharsets.UTF_8));
			   }
			});
		
		
	}


	public boolean portIsOpen() {
		return port.isOpen();
	}

	public void closePort() {
		port.closePort();
		Interface.updateMessage("Port Closed\n", Color.RED);

	}

}
