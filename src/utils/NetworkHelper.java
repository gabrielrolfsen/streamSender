/**
 *  
 *
 * @author Gabriel Franzoni
 * @version %I%, %G%
 * @since Jul 20, 2015
 */
package utils;

/**
 * @author grolfsen
 *
 */
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

public class NetworkHelper {

	/**
	 * Convert byte array to hex string
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(final byte[] bytes) {
		final StringBuilder sbuf = new StringBuilder();
		for (int idx = 0; idx < bytes.length; idx++) {
			final int intVal = bytes[idx] & 0xff;
			if (intVal < 0x10) {
				sbuf.append("0");
			}
			sbuf.append(Integer.toHexString(intVal).toUpperCase());
		}
		return sbuf.toString();
	}

	public static short getSubnetMask() {
		InetAddress localHost = null;
		short subnetMask = 0;
		try {
			localHost = Inet4Address.getLocalHost();
			NetworkInterface networkInterface = null;
			try {
				networkInterface = NetworkInterface.getByInetAddress(localHost);
			} catch (final SocketException e) {
				e.printStackTrace();
			}
			subnetMask = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
		} catch (final UnknownHostException e) {
			e.printStackTrace();
		}

		return subnetMask;
	}

	/**
	 * Returns MAC address of the given interface name.
	 * 
	 * @param interfaceName
	 *            eth0, wlan0 or NULL=use first interface
	 * @return mac address or empty string
	 */
	public static String getMACAddress(final String interfaceName) {
		try {
			final List<NetworkInterface> interfaces = Collections.list(NetworkInterface
					.getNetworkInterfaces());
			for (final NetworkInterface intf : interfaces) {
				if (interfaceName != null) {
					if (!intf.getName().equalsIgnoreCase(interfaceName)) {
						continue;
					}
				}
				final byte[] mac = intf.getHardwareAddress();
				if (mac == null) {
					return "";
				}
				final StringBuilder buf = new StringBuilder();
				for (int idx = 0; idx < mac.length; idx++) {
					buf.append(String.format("%02X:", mac[idx]));
				}
				if (buf.length() > 0) {
					buf.deleteCharAt(buf.length() - 1);
				}
				return buf.toString();
			}
		} catch (final Exception ex) {
		} // for now eat exceptions
		return "";
	}

	public static byte[] getMACAddress() {
		byte[] mac = null;

		try {
			final List<NetworkInterface> interfaces = Collections.list(NetworkInterface
					.getNetworkInterfaces());
			for (final NetworkInterface intf : interfaces) {

				mac = intf.getHardwareAddress();
				break;
			}
		} catch (final Exception ex) {
		} // for now eat exceptions
		return mac;
	}

	/**
	 * Get IP address from first non-localhost interface
	 * 
	 * @param ipv4
	 *            true=return ipv4, false=return ipv6
	 * @return address or empty string
	 */
	public static String getIPAddress(final boolean useIPv4) {
		try {
			final List<NetworkInterface> interfaces = Collections.list(NetworkInterface
					.getNetworkInterfaces());
			for (final NetworkInterface intf : interfaces) {
				final List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (final InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						final String sAddr = addr.getHostAddress().toUpperCase();
						final boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (useIPv4) {
							if (isIPv4) {
								return sAddr;
							}
						} else {
							if (!isIPv4) {
								final int delim = sAddr.indexOf('%'); // drop
								// ip6 port suffix
								return delim < 0 ? sAddr : sAddr.substring(0, delim);
							}
						}
					}
				}
			}
		} catch (final Exception ex) {
		} // for now eat exceptions
		return "";
	}

}
