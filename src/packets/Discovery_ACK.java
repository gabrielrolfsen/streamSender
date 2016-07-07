/**
 *  
 *
 * @author Gabriel Franzoni
 * @version %I%, %G%
 * @since Jul 17, 2015
 */
package packets;

import registers.Register_Header;
import utils.Constants;

/**
 * @author grolfsen
 *
 */
public class Discovery_ACK extends Register_Header {

	private short spec_version_major = 0;
	private short spec_version_minor = 0;

	private int device_mode = 0;
	private byte[] device_MAC_address_high = new byte[2];
	private byte[] device_MAC_address_low = new byte[4];
	private int ip_config_options = 0;
	private int ip_config_current = 0;
	private int current_IP = 0;
	private int current_subnet_mask = 0;
	private int default_gateway = 0;
	private byte[] manufacturer_name = new byte[32];
	private byte[] model_name = new byte[32];
	private byte[] device_version = new byte[32];
	private byte[] manufacturer_specific_information = new byte[48];
	private byte[] serial_number = new byte[16];
	private byte[] user_defined_name = new byte[16];

	public Discovery_ACK(final short status, final short length, final short ack_id,
			final short spec_version_major, final short spec_version_minor, final int device_mode,
			final byte[] device_MAC_address_high, final byte[] device_MAC_address_low,
			final int ip_config_options, final int ip_config_current, final int current_IP,
			final int current_subnet_mask, final int default_gateway, final byte[] manufacturer_name,
			final byte[] model_name, final byte[] device_version,
			final byte[] manufacturer_specific_information, final byte[] serial_number,
			final byte[] user_defined_name) {
		super(status, Constants.DISCOVERY_ACK, length, ack_id);
		this.spec_version_major = spec_version_major;
		this.spec_version_minor = spec_version_minor;
		this.device_mode = device_mode;
		this.device_MAC_address_high = device_MAC_address_high;
		this.device_MAC_address_low = device_MAC_address_low;
		this.ip_config_options = ip_config_options;
		this.ip_config_current = ip_config_current;
		this.current_IP = current_IP;
		this.current_subnet_mask = current_subnet_mask;
		this.default_gateway = default_gateway;
		this.manufacturer_name = manufacturer_name;
		this.model_name = model_name;
		this.device_version = device_version;
		this.manufacturer_specific_information = manufacturer_specific_information;
		this.serial_number = serial_number;
		this.user_defined_name = user_defined_name;
	}

	public byte[] getPacket() {
		final byte[] packet = new byte[256];
		packet[0] = (byte) (super.getStatus() >> 8);
		packet[1] = (byte) super.getStatus();

		packet[2] = (byte) (super.getAnswer() >> 8);
		packet[3] = (byte) super.getAnswer();

		packet[4] = (byte) (super.getLength() >> 8);
		packet[5] = (byte) super.getLength();

		packet[6] = (byte) (super.getAck_id() >> 8);
		packet[7] = (byte) super.getAck_id();

		packet[8] = (byte) (this.spec_version_major >> 8);
		packet[9] = (byte) this.spec_version_major;

		packet[10] = (byte) (this.spec_version_minor >> 8);
		packet[11] = (byte) this.spec_version_minor;

		packet[12] = (byte) (this.device_mode >> 24);
		packet[13] = (byte) (this.device_mode >> 16);
		packet[14] = (byte) (this.device_mode >> 8);
		packet[15] = (byte) (this.device_mode);

		packet[16] = (byte) 0x0;
		packet[17] = (byte) 0x0;

		packet[18] = this.device_MAC_address_high[0];
		packet[19] = this.device_MAC_address_high[1];

		packet[20] = this.device_MAC_address_high[0];
		packet[21] = this.device_MAC_address_high[1];
		packet[22] = this.device_MAC_address_high[2];
		packet[23] = this.device_MAC_address_high[3];

		packet[24] = (byte) (this.ip_config_options >> 24);
		packet[25] = (byte) (this.ip_config_options >> 16);
		packet[26] = (byte) (this.ip_config_options >> 8);
		packet[27] = (byte) (this.ip_config_options);

		packet[28] = (byte) (this.ip_config_current >> 24);
		packet[29] = (byte) (this.ip_config_current >> 16);
		packet[30] = (byte) (this.ip_config_current >> 8);
		packet[31] = (byte) (this.ip_config_current);

		// Reserved fields
		for (int i = 32; i < 44; i++) {
			packet[i] = (byte) 0x0;
		}

		packet[44] = (byte) (this.current_IP >> 24);
		packet[45] = (byte) (this.current_IP >> 16);
		packet[46] = (byte) (this.current_IP >> 8);
		packet[47] = (byte) (this.current_IP);

		// Reserved fields
		for (int i = 48; i < 60; i++) {
			packet[i] = (byte) 0x0;
		}

		packet[60] = (byte) (this.current_subnet_mask >> 24);
		packet[61] = (byte) (this.current_subnet_mask >> 16);
		packet[62] = (byte) (this.current_subnet_mask >> 8);
		packet[63] = (byte) (this.current_subnet_mask);

		// Reserved fields
		for (int i = 64; i < 76; i++) {
			packet[i] = (byte) 0x0;
		}

		packet[76] = (byte) (this.default_gateway >> 24);
		packet[77] = (byte) (this.default_gateway >> 16);
		packet[78] = (byte) (this.default_gateway >> 8);
		packet[79] = (byte) (this.default_gateway);

		// Manufacturer Name - 32 bytes
		for (int i = 80; i < 112; i++) {
			packet[i] = manufacturer_name[i - 80];
		}

		// Model Name - 32 bytes
		for (int i = 112; i < 144; i++) {
			packet[i] = model_name[i - 112];
		}

		// Device Version - 32 bytes
		for (int i = 144; i < 176; i++) {
			packet[i] = device_version[i - 144];
		}

		// Manufacturer Specific Info - 48 bytes
		for (int i = 176; i < 224; i++) {
			packet[i] = manufacturer_specific_information[i - 176];
		}

		// Serial Number - 16 bytes
		for (int i = 224; i < 240; i++) {
			packet[i] = serial_number[i - 224];
		}

		// User Defined Name - 16 bytes
		for (int i = 240; i < 256; i++) {
			packet[i] = user_defined_name[i - 240];
		}

		return packet;
	}

}
