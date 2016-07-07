/**
 *  
 *
 * @author Gabriel Franzoni
 * @version %I%, %G%
 * @since Jul 22, 2015
 */
package utils;

/**
 * @author grolfsen
 *
 */
public final class Constants {

	/* DEVICE SPECIFICATIONS */
	public static final String MANUFACTURER_NAME = "RECON";
	public static final String MODEL_NAME = "JET";
	public static final String DEVICE_VER = "1.0";
	public static final String MANUFACTURER_SPEC_INFO = "NONE";
	public static final String SERIAL_NUMBER = "NONE";
	public static final String USER_DEFINED_NAME = "N/A";
	public static final short SPEC_VERSION_MAJOR = 2;
	public static final short SPEC_VERSION_MINOR = 0;

	/* DEVICE CLASS SELECTORS */
	public static final byte DEVICE_CLASS_TRANSMITTER = 0x0;
	public static final byte DEVICE_CLASS_RECEIVER = 0x1;
	public static final byte DEVICE_CLASS_TRANSCEIVER = 0x2;
	public static final byte DEVICE_CLASS_PERIPHERAL = 0x3;

	public static final byte CLC_SINGLE = 0x0;
	public static final byte CLC_MULTIPLE = 0x1;
	public static final byte CLC_STATIC_LAG = 0x2;
	public static final byte CLC_DYNAMIC_LAG = 0x3;

	public static final byte CHARSET_INDEX_RESERVED = 0x0;
	public static final byte CHARSET_INDEX_UTF8 = 0x1;
	public static final byte CHARSET_INDEX_ASCII = 0x2;

	/* REGISTERS COMMANDS */
	public static short DISCOVERY_ACK = 0x0003;

	public static final short READREG_CMD = 0x0080;
	public static final short READREG_ACK = 0x0081;

	public static final short WRITEREG_CMD = 0x0082;
	public static final short WRITEREG_ACK = 0x0083;

	public static final short READMEM_CMD = 0x0084;
	public static final short READMEM_ACK = 0x0085;

	public static final short WRITEMEM_CMD = 0x0086;
	public static final short WRITEMEM_ACK = 0x0087;

	public static final short PENDING_ACK = 0x0089;

	/* GENERAL CONFIGURATIONS */
	public static final int REQUEST_TIMEOUT = 5000;
	public static final int GIGE_PORT = 3956;

	public static final int PACKET_SIZE = 32748;
}
