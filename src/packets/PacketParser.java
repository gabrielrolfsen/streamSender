/**
 *  
 *
 * @author Gabriel Franzoni
 * @version %I%, %G%
 * @since Jul 6, 2015
 */
package packets;

import utils.Constants;

/**
 * @author grolfsen
 *
 */
public class PacketParser {

	public final static short GEV_STATUS_SUCCESS = (short) 0x0000;
	public final static short GEV_STATUS_PACKET_RESEND = (short) 0x0100;
	public final static short GEV_STATUS_ACCESS_DENIED = (short) 0x8006;
	public final static short GEV_STATUS_INVALID_HEADER = (short) 0x800E;
	public final static short GEV_STATUS_PACKET_UNAVAILABLE = (short) 0x800C;
	public final static short GEV_STATUS_ERROR = (short) 0x8FFF;
	public final static short GEV_FLAG_RESEND_RANGE_ERROR = (short) 0x1000;
	public final static short GEV_FLAG_PREVIOUS_BLOCK_DROPPED = (short) 0x2000;
	public final static short GEV_FLAG_PACKET_RESEND = (short) 0x3000;

	public final static int PAYLOAD_TYPE = 0x0001;
	public final static int PAYLOAD_TYPE_SPECIFIC = 0x0000;

	private final static byte DATA_LEADER_FORMAT = 0x1;
	private final static byte DATA_TRAILER_FORMAT = 0x2;
	private final static byte DATA_PAYLOAD_FORMAT = 0x3;

	private final static int LEADER_PACKET_SIZE = 64;
	// Offset for Payload packet (Header Size)
	private static int HEADER_SIZE = 20;
	private int size_x = 640;
	private int size_y = 480;

	// Pixel Format - 32bits
	private int pixel_format = -1;
	// Status flag - 16 bits
	private final short status = 0x103F;
	// GVSP Status Flag - 16 bits
	private final short gvsp_status_flag = 0;
	// Packet ID - 24 bits
	private int packet_id = -1;

	private int frame_size = 0;

	// TODO: wrap-around to 0 when reaches max value
	static short block_id = 0;

	private short ei_flag = (short) 0;

	public void setEiFlag(final short ei_flag) {
		this.ei_flag = ei_flag;
	}

	public void setFrameDimensions(final int x, final int y) {
		this.size_x = x;
		this.size_y = y;
	}

	public void setFrameSize(final int frame_size) {
		this.frame_size = frame_size;
	}

	public void setPixelFormat(final int format) {
		this.pixel_format = format;
	}

	public byte[] getPayloadPacket(final int packet_id_num, final byte[] frameData) {
		final byte[] packet = getGVSPHeader(packet_id_num, Constants.PACKET_SIZE + HEADER_SIZE,
				DATA_PAYLOAD_FORMAT);

		final int packetSize = (Constants.PACKET_SIZE + HEADER_SIZE);

		// For last chunk: if its length is bigger than chunk length, use the
		// lowest value
		/*
		 * if (PacketController.PACKET_SIZE > frameData.length) { packetSize =
		 * frameData.length; }
		 */

		// Copy the frame data to packet
		for (int i = HEADER_SIZE; i < packetSize; i++) {
			packet[i] = frameData[i - HEADER_SIZE];
		}

		return packet;
	}

	public byte[] getTrailerPacket(final int packet_id_num) {
		final byte[] packet = getGVSPHeader(packet_id_num, Constants.PACKET_SIZE + HEADER_SIZE,
				DATA_TRAILER_FORMAT);

		// Reserved Field
		packet[20] = 0;
		packet[21] = 0;

		// Payload Type
		packet[22] = (byte) (PAYLOAD_TYPE >> 8);
		packet[23] = (byte) (PAYLOAD_TYPE);

		// Size Y
		packet[24] = (byte) (size_y >> 24);
		packet[25] = (byte) (size_y >> 16);
		packet[26] = (byte) (size_y >> 8);
		packet[27] = (byte) (size_y);

		return packet;
	}

	public byte[] getLeaderPacket(final int packet_id_num) {
		// Increments block ID
		block_id++;

		// Packet to be sent
		final byte[] packet = getGVSPHeader(packet_id_num, Constants.PACKET_SIZE + HEADER_SIZE,
				DATA_LEADER_FORMAT);

		// Payload Type (Specific)
		packet[20] = 0x0;
		packet[21] = 0x0;

		// Payload Type
		packet[22] = (byte) (PAYLOAD_TYPE >> 8);
		packet[23] = (byte) (PAYLOAD_TYPE);

		// Pixel Format
		packet[32] = (byte) (pixel_format >> 24);
		packet[33] = (byte) (pixel_format >> 16);
		packet[34] = (byte) (pixel_format >> 8);
		packet[35] = (byte) (pixel_format);

		// Size X
		packet[36] = (byte) (size_x >> 24);
		packet[37] = (byte) (size_x >> 16);
		packet[38] = (byte) (size_x >> 8);
		packet[39] = (byte) (size_y);

		// Size Y
		packet[40] = (byte) (size_y >> 24);
		packet[41] = (byte) (size_y >> 16);
		packet[42] = (byte) (size_y >> 8);
		packet[43] = (byte) (size_y);

		// Frame Size
		packet[44] = (byte) (frame_size >> 24);
		packet[45] = (byte) (frame_size >> 16);
		packet[46] = (byte) (frame_size >> 8);
		packet[47] = (byte) (frame_size);

		// Offset x/y and padding x/y
		for (int i = 48; i < LEADER_PACKET_SIZE; i++) {
			packet[i] = 0;
		}

		return packet;
	}

	public byte[] getGVSPHeader(final long packet_id_num, final int buffsize, byte packet_format) {

		// Whole packet to be sent - is it necessary?
		final byte[] packet = new byte[buffsize];

		// Block ID / Flag - 16 bits
		short block_id_flag = 0;

		if (ei_flag == 0) {
			block_id_flag = block_id;
			packet_id = (short) packet_id_num;
		} else if (ei_flag == 1) {
			block_id_flag = gvsp_status_flag;
			// Packet_id field turns into reserved (0x0);
			packet_id = 0x0;

			// Set block_id64_high
			packet[8] = (byte) (packet_id_num >> 56);
			packet[9] = (byte) (packet_id_num >> 48);
			packet[10] = (byte) (packet_id_num >> 40);
			packet[11] = (byte) (packet_id_num >> 32);

			// Set block_id64_low
			packet[12] = (byte) (packet_id_num >> 24);
			packet[13] = (byte) (packet_id_num >> 16);
			packet[14] = (byte) (packet_id_num >> 8);
			packet[15] = (byte) (packet_id_num);

			// Set packet_id32
			packet[16] = (byte) ((int) packet_id_num >> 24);
			packet[17] = (byte) ((int) packet_id_num >> 16);
			packet[18] = (byte) ((int) packet_id_num >> 8);
			packet[19] = (byte) ((int) packet_id_num);

		}
		// TODO: implement status verification
		// Set status
		packet[0] = (byte) (status >> 8);
		packet[1] = (byte) (status);

		// Set block_id_flag
		packet[2] = (byte) (block_id_flag >> 8);
		packet[3] = (byte) (block_id_flag);

		// Clean the 4 upper bits from packet_format
		packet_format = (byte) (packet_format << 4);
		packet_format = (byte) (packet_format >> 4);

		// Set ei_flag and packet_format
		packet[4] = (byte) ((ei_flag << 7) | packet_format);

		// Set packet_id
		packet[5] = (byte) (packet_id >> 16);
		packet[6] = (byte) (packet_id >> 8);
		packet[7] = (byte) (packet_id);

		return packet;
	}
}
