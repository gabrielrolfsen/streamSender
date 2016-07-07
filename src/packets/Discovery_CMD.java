/**
 *  
 *
 * @author Gabriel Franzoni
 * @version %I%, %G%
 * @since Jul 17, 2015
 */
package packets;

import registers.Register_Header;

/**
 * @author grolfsen
 *
 */
public class Discovery_CMD extends Register_Header {

	public static final short DISCOVERY_CMD = 0x0004;
	public static final char HEADER = 0x42;

	public Discovery_CMD(final byte[] packet) {
		super(packet);
	}

	/**
	 * Checks if the packet received is valid.
	 * 
	 * @return true if the packet meets the conditions required, false if not.
	 */
	public boolean isValid() {
		if (super.getAnswer() == DISCOVERY_CMD && super.getHeader() == HEADER) {
			return true;
		}
		return false;

	}

	/**
	 * Checks if the third bit of the flag field is set.
	 * 
	 * @return true if the device can broadcast the DISCOVERY_ACK packet, false
	 *         if not.
	 */
	public boolean canBroadcast() {
		if ((super.getFlag() >> 2) == 1) {
			return true;
		}
		return false;
	}

}
