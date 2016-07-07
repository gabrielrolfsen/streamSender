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
public class WRITEMEM extends Register_Header {

	byte[] addresses = null;

	/**
	 * 
	 */
	public WRITEMEM(final byte header, final byte flag, final short answer, final short length,
			final short ack_id) {
		super(header, flag, answer, length, ack_id);
	}

	public WRITEMEM(final short status, final short answer, final short length, final short ack_id) {
		super(status, answer, length, ack_id);
	}

	public WRITEMEM(final byte[] header, final byte[] data) {
		super(header);
		this.addresses = data;
	}

	public byte[] getAddress(final int pos) {
		final byte[] addr = new byte[4];
		for (int i = 0; i < addr.length; i++) {
			addr[i] = addresses[i + (pos * addr.length)];
		}
		return addr;
	}

}
