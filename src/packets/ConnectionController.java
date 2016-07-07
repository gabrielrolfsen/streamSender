/**
 *  
 *
 * @author Gabriel Franzoni
 * @version %I%, %G%
 * @since Jul 20, 2015
 */
package packets;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.Charset;

import registers.READMEM;
import registers.READREG;
import registers.Register_Header;
import registers.WRITEMEM;
import registers.WRITEREG;
import utils.Constants;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * @author grolfsen
 *
 */
public class ConnectionController {

	private short packet_length = 0;
	// Holds temporarily the ack_id to send in discovery_ack
	private short ack_id = 0;
	private int device_mode = 0;
	private boolean isConnected = false;

	private DatagramSocket datagramSocket = null;

	private final Context context;

	public ConnectionController(final Context context) {
		this.context = context;
		device_mode = ((byte) 0x1 << 31);
		device_mode += (Constants.DEVICE_CLASS_TRANSMITTER << 30);
		device_mode += (Constants.CLC_SINGLE << 27);
		device_mode += Constants.CHARSET_INDEX_UTF8;

		// DEBUG: DEVICE_MODE
		// System.out.println(device_mode);
	}

	private boolean sendACKPacket() {
		final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// final String currentIp = NetworkHelper.getIPAddress(true);
		final DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
		final WifiInfo wInfo = wifiManager.getConnectionInfo();

		// Gets current Ip Address
		final int currentIp = dhcpInfo.ipAddress;

		// final String macAddr = NetworkHelper.getMACAddress("eth0");
		// final short subnetMask = NetworkHelper.getSubnetMask();

		// Gets current MAC Address and converts it into a byte array
		final byte[] macAddress = wInfo.getMacAddress().getBytes(Charset.forName("UTF-8"));
		final byte[] macAddress_high = new byte[2];
		final byte[] macAddress_low = new byte[4];

		// Divide into two chunks, the high and low part
		System.arraycopy(macAddress, 0, macAddress_high, 0, macAddress_high.length);
		System.arraycopy(macAddress, macAddress_high.length, macAddress_low, 0, macAddress_low.length);

		// TODO: this is casted to short
		final int subnetMask = dhcpInfo.netmask;
		final int default_gateway = dhcpInfo.gateway;

		final Discovery_ACK discovery_packet = new Discovery_ACK((short) 0, this.packet_length, this.ack_id,
				Constants.SPEC_VERSION_MAJOR, Constants.SPEC_VERSION_MINOR, this.device_mode,
				macAddress_high, macAddress_low, 0, 0, currentIp, subnetMask, default_gateway,
				Constants.MANUFACTURER_NAME.getBytes(), Constants.MODEL_NAME.getBytes(),
				Constants.DEVICE_VER.getBytes(), Constants.MANUFACTURER_SPEC_INFO.getBytes(),
				Constants.SERIAL_NUMBER.getBytes(), Constants.USER_DEFINED_NAME.getBytes());

		// Get the byte array with the data on it
		final byte[] data = discovery_packet.getPacket();

		// TODO: Checks if it is the Server's addr
		final InetAddress destIP = datagramSocket.getInetAddress();

		// Prepare Discovery_ACK packet
		final DatagramPacket packet = new DatagramPacket(data, data.length, destIP, Constants.GIGE_PORT);

		try {
			// Sends over control channel port
			datagramSocket.send(packet);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	private boolean createDatagramSocket() {
		boolean status = false;
		try {
			datagramSocket = new DatagramSocket(Constants.GIGE_PORT);
			// Set a timeout for server's answer
			datagramSocket.setSoTimeout(Constants.REQUEST_TIMEOUT);
			status = true;
		} catch (final SocketException e) {
			e.printStackTrace();
		}
		return status;
	}

	public void startConnectionController() {
		// Listen on Port 3956 to receive the DISCOVERY_CMD packet
		System.out.println("CREATED DATAGRAM SOCKET.");
		createDatagramSocket();

		// Receives the DISCOVERY_CMD packet
		try {
			System.out.println("Wait for Discovery CMD");
			final byte[] discovery_cmd_packet = new byte[8];
			final DatagramPacket discoveryCmd = new DatagramPacket(discovery_cmd_packet,
					discovery_cmd_packet.length);

			datagramSocket.receive(discoveryCmd);

			System.out.println("DISCOVERY CMD: Got it!");

			// DEBUG: Is it needed?
			// discovery_cmd_packet = discoveryCmd.getData();

			final Discovery_CMD discoveryPacket = new Discovery_CMD(discovery_cmd_packet);
			this.ack_id = discoveryPacket.getAck_id();
			this.packet_length = discoveryPacket.getLength();

			// Sends the ACKNOWLEDGE packet
			isConnected = sendACKPacket();

			if (isConnected) {
				waitForCommand();
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	private void waitForCommand() {
		final byte[] headerData = new byte[8];
		final DatagramPacket headerPacket = new DatagramPacket(headerData, headerData.length);

		// Waits for a command packet
		try {
			datagramSocket.receive(headerPacket);

			// Parse Packet Header
			final Register_Header header = new Register_Header(headerData);
			final short length = header.getLength();

			// Command Packet
			final byte[] cmdData = new byte[length];
			final DatagramPacket cmdPacket = new DatagramPacket(cmdData, cmdData.length);

			// Receive data
			datagramSocket.receive(cmdPacket);

			switch (header.getAnswer()) {
			case Constants.READREG_CMD:
				final READREG readReg = new READREG(headerData, cmdData);
				break;
			case Constants.WRITEREG_CMD:
				final WRITEREG writeReg = new WRITEREG(headerData, cmdData);
				break;
			case Constants.READMEM_CMD:
				final READMEM readMem = new READMEM(headerData, cmdData);
				break;
			case Constants.WRITEMEM_CMD:
				final WRITEMEM writeMem = new WRITEMEM(headerData, cmdData);
				break;
			case Constants.PENDING_ACK:
				break;
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

}
