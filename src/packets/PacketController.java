/**
 *  
 *
 * @author Gabriel Franzoni
 * @version %I%, %G%
 * @since Jul 7, 2015
 */
package packets;

import utils.Constants;

/**
 * @author grolfsen
 *
 */
public class PacketController {

	private final PacketParser packetParser = new PacketParser();

	// Packet ID number to be sent
	private int packet_id_num = 0;

	// Byte Array to hold frame data
	private byte[] frameData = null;

	// Size of the Frame Data
	private int dataSize = 0;

	// Position Counter for the segmentation process
	private int actualPos = 0;

	/**
	 * 
	 * @param format
	 */
	public void setPixelFormat(final int format) {
		packetParser.setPixelFormat(format);
	}

	/**
	 * 
	 * @param ei_flag
	 */
	public void setEiFlag(final short ei_flag) {
		packetParser.setEiFlag(ei_flag);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void setFrameDimensions(final int x, final int y) {
		packetParser.setFrameDimensions(x, y);
	}

	public void setFrameSize(final int frame_size) {
		packetParser.setFrameSize(frame_size);
	}

	public byte[] getLeaderPacket() {
		packet_id_num = 0;
		return packetParser.getLeaderPacket(packet_id_num);
	}

	public byte[] getPayloadPacket() {
		// Byte Array to hold a chunk from the frameData array
		final byte[] chunk = new byte[Constants.PACKET_SIZE];

		// Length of the chunk
		int length = Constants.PACKET_SIZE;

		// Adjusts last chunk size;
		if ((actualPos + Constants.PACKET_SIZE) >= dataSize) {
			length = dataSize - actualPos;
			// chunk = new byte[length];
		}

		// Copy the section from the source Frame Data to a new array
		// System.arraycopy(frameData, actualPos, chunk, 0, chunk.length);
		System.arraycopy(frameData, actualPos, chunk, 0, length);

		// Increments position to be copied
		actualPos = actualPos + Constants.PACKET_SIZE;

		// Increments packet Id number
		packet_id_num++;

		// DEBUG: Packet_id_num
		// System.out.println(packet_id_num);

		return packetParser.getPayloadPacket(packet_id_num, chunk);
	}

	public byte[] getTrailerPacket() {
		// Reset position counter
		actualPos = 0;
		packet_id_num++;
		return packetParser.getTrailerPacket(packet_id_num);
	}

	public void setPayload(final byte[] frameData) {
		this.frameData = frameData;
		this.dataSize = frameData.length;
	}

}
