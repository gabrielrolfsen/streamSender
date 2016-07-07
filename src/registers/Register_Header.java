/**
 *  
 *
 * @author Gabriel Franzoni
 * @version %I%, %G%
 * @since Jul 22, 2015
 */
package registers;

/**
 * @author grolfsen
 *
 */
public class Register_Header {

	/* Header */
	private byte header = 0x0;
	/*
	 * bit 0-2: reserved. bit 3 : allows broadcast of ACK. bit 4-7: standard
	 * definition from GVCP header.
	 */
	private byte flag = 0x0;

	private short status = 0;
	private short command = 0;
	/*
	 * Number of valid data bytes in this message, not including this header.
	 * This represents the number of bytes of payload appended after this
	 * header.
	 */
	private short length = 0;
	private short ack_id = 0;

	public static final char HEADER = 0x42;

	public Register_Header(final byte[] packet) {
		super();
		this.header = packet[0];
		this.flag = packet[1];
		this.command = packet[2];
		this.command = (short) ((short) (this.command << 8) + packet[3]);
		this.length = packet[4];
		this.length = (short) ((short) (this.length << 8) + packet[5]);
		this.ack_id = packet[6];
		this.ack_id = (short) ((short) (this.length << 8) + packet[7]);
	}

	public Register_Header(final byte header, final byte flag, final short command, final short length,
			final short ack_id) {
		super();
		this.header = header;
		this.flag = flag;
		this.command = command;
		this.length = length;
		this.ack_id = ack_id;
	}

	public Register_Header(final short status, final short answer, final short length, final short ack_id) {
		super();
		this.status = status;
		this.command = answer;
		this.length = length;
		this.ack_id = ack_id;
	}

	public byte getHeader() {
		return this.header;
	}

	public byte getFlag() {
		return this.flag;
	}

	public short getStatus() {
		return this.status;
	}

	public short getAnswer() {
		return this.command;
	}

	public short getLength() {
		return this.length;
	}

	public short getAck_id() {
		return this.ack_id;
	}

}
