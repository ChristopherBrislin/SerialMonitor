
/**
 * Author: Christopher Brislin brislinchris@gmail.com
 * Date: 25 Oct 2020
 * Title of code: SerialMonitor
 * Version: 1.0
 * 
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import com.fazecast.jSerialComm.SerialPort;

public class Interface implements ActionListener {

	PortBuilder portBuilder = new PortBuilder();
	JComboBox<Integer> baud;
	JComboBox<SerialPort> ports = new JComboBox<SerialPort>();

	// static variables for access across classes
	static JLabel message = new JLabel("Select Port");
	static JTextArea textArea;

	boolean scrollFlag = false;

	public void interfaceStart() {
		JFrame frame = new JFrame("Serial Monitor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(Constants.INIT_WINDOW_SIZE);

		frame.add(entryPoint());
		frame.setVisible(true);
	}

	public Component entryPoint() {
		// The entry panel the user first sees.
		JPanel container = new JPanel(new BorderLayout(30, 30));
		container.add(dataArea(), BorderLayout.CENTER);
		container.add(controlPanel(), BorderLayout.SOUTH);
		return container;

	}

	public JScrollPane dataArea() {
		// method to initialise the textArea in a scroll pane.
		textArea = new JTextArea();
		textArea.setEditable(false);
		JScrollPane data = new JScrollPane(textArea);

		return data;

	}

	static void updateData(String s) {
		// method to append data to the textarea.
		textArea.append(s);
	}

	public void clearData() {
		// method to clear the displayed data
		textArea.setText("");

	}

	static void updateMessage(String s, Color c) {
		// method for setting message label and colour.
		message.setForeground(c);
		message.setText(s);

	}

	public void setAutoscroll(boolean b) {
		// autoscroll checkbox sets autoscroll flag. This is used to set the caret
		// update policy.
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		if (b) {

			textArea.setCaretPosition(textArea.getDocument().getLength());
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		} else {
			caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		}

	}

	public Component controlPanel() {
		// Panel containing the user interface
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
		container.add(settings());
		container.add(buttons());

		return container;
	}

	public void updatePorts() {
		// update list of serial ports. DefaultComboBox used to refresh ComboBox list.
		ports.setModel(new DefaultComboBoxModel<SerialPort>(portBuilder.portsAvailable()));

	}

	public JPanel settings() {
		// Panel containing interface settings
		JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel("Serial Port: ");
		updatePorts(); // refresh the list of serial ports

		JLabel baudLabel = new JLabel("Baudrate: ");

		baud = new JComboBox<Integer>(Constants.BAUD_RATES);

		JCheckBox autoScrollCheckBox = new JCheckBox("Autoscroll");
		autoScrollCheckBox.addActionListener(this);

		container.add(baudLabel);
		container.add(baud);
		container.add(label);
		container.add(ports);
		container.add(autoScrollCheckBox);

		return container;

	}

	public JPanel buttons() {
		// Panel containing the interface buttons as well as message label
		JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JButton refreshPorts = new JButton("Refresh Ports");
		refreshPorts.addActionListener(this);
		JButton openPort = new JButton("Open Port");
		openPort.addActionListener(this);
		JButton closePort = new JButton("Close Port");
		closePort.addActionListener(this);
		JButton clearData = new JButton("Clear");
		clearData.addActionListener(this);

		container.add(refreshPorts);
		container.add(openPort);
		container.add(closePort);
		container.add(clearData);
		container.add(message);

		return container;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Action listener - switch case that responds based on button name
		switch (e.getActionCommand()) {

		case ("Refresh Ports"):
			updatePorts();
			break;

		case ("Open Port"):
			portBuilder.portBuild((SerialPort) ports.getSelectedItem(), (int) baud.getSelectedItem());
			if (!portBuilder.portIsOpen()) {
				// if the port does not open - display this message and refresh list of ports
				updateMessage("Port not available", Color.RED);
				updatePorts();
			}
			break;

		case ("Close Port"):
			portBuilder.closePort();
			break;

		case ("Clear"):
			clearData();
			break;

		case ("Autoscroll"):
			// set the scroll flag. This is not the most efficient use of checkbox but was
			// the simplest implementation with a single action listener.
			if (!scrollFlag) {
				scrollFlag = true;
			} else {
				scrollFlag = false;
			}
			setAutoscroll(scrollFlag);
			break;

		default:
			break;
		}

	}

}
