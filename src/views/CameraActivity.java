/**
 *  
 *
 * @author Gabriel Franzoni
 * @version %I%, %G%
 * @since Jul 7, 2015
 */
package views;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import packets.ConnectionController;
import packets.PacketController;
import utils.Constants;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

/**
 * @author grolfsen
 *
 */
public class CameraActivity extends Activity implements CvCameraViewListener {

	private static final String TAG = "SDP Streamer::CameraActivity";

	// Refactor and singleton-it
	private final PacketController packetController = new PacketController();

	private DatagramSocket socket;
	private String ipAddr = "";
	private InetAddress destIP = null;
	private int destPort = 0;
	private String port = "";
	private int buffSize = 0;
	private byte[] frameData = null;
	private final boolean sent = false;

	private boolean isConfig = false;

	private CameraBridgeViewBase mOpenCvCameraView;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get the data (IP and port) provided by the user
		final Intent intent = getIntent();
		ipAddr = intent.getStringExtra(MainActivity.IP_ADDR);
		port = intent.getStringExtra(MainActivity.PORT);

		// Set the layout to fullscreen
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Log.d(TAG, "Creating and setting view");

		// Get camera's instance
		mOpenCvCameraView = new JavaCameraView(this, -1);

		mOpenCvCameraView.setMaxFrameSize(1280, 720);

		// Set layout as the camera preview
		setContentView(mOpenCvCameraView);

		// Set the listener to camera
		mOpenCvCameraView.setCvCameraViewListener(this);

		// Display a dialog while trying to connect to the server
		final ProgressDialog connectionDialog = ProgressDialog.show(this, "Waiting Server...", "Connecting",
				true);

		// Wait for Control Signals to start streaming
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Talk to the server and define parameters
				final ConnectionController connController = new ConnectionController(getApplicationContext());
				// DEBUG: Connection Controller
				System.out.println("Connection controller created.");
				connController.startConnectionController();

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// Dismiss Progress Dialog
						connectionDialog.dismiss();

						// Init socket with parameters received from server
						initSocket();
					}
				});

			}
		}).start();

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null) {
			mOpenCvCameraView.disableView();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null) {
			mOpenCvCameraView.disableView();
		}
	}

	private void initSocket() {
		try {
			socket = new DatagramSocket();
		} catch (final SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Set Destination IP and Port
		try {
			destIP = InetAddress.getByName(ipAddr);
		} catch (final UnknownHostException e) {
			e.printStackTrace();
		}
		destPort = Integer.parseInt(port);
	}

	@Override
	public Mat onCameraFrame(final Mat frame) {

		if (!isConfig) {
			// Set Pixel Format
			packetController.setPixelFormat(frame.depth());
			// DEBUG: Pixel format
			// System.out.println("Pixel format: " + frame.depth());

			// TODO: get it from the device
			// Set Frame Dimensions
			packetController.setFrameDimensions(320, 240);
			// Set Frame Size
			packetController.setFrameSize((int) (frame.total() * frame.channels()));
			// Set the Configuration flag ON
			isConfig = true;
		}

		Mat frame_copy = new Mat();
		// Copy the frame into a new object
		frame.copyTo(frame_copy);
		// Send over UDP
		// sendFrameOverUDP(frame_copy);
		frame_copy = null;

		return frame;
	}

	@Override
	public void onCameraViewStarted(final int width, final int height) {
	}

	@Override
	public void onCameraViewStopped() {
	}

	/**
	 * Take each raw frame from the camera stream, opens a UDP connection and
	 * sends it over the network.
	 * 
	 * @param frame
	 *            Mat frame to be sent.
	 */
	private void sendFrameOverUDP(final Mat frame) {

		// Make it Continuous
		// frame = (frame.reshape(0, 1));

		// Count frame size
		final int count = (int) (frame.total() * frame.channels());

		// Initialize a new buffer every time the size of the frame changes
		if (count != buffSize) {
			buffSize = count;
			// Transform the Mat frame into a raw data array (bytes)
			frameData = new byte[buffSize];
		}
		// Put the frame on the byte array
		frame.get(0, 0, frameData);

		// Set the current frame as the payload for transmission
		packetController.setPayload(frameData);

		// Get Leader Packet and send it
		byte[] data = packetController.getLeaderPacket();
		sendPacket(data);

		int packetId = 0;
		packetId += data[5];
		packetId = packetId << 8;
		packetId += data[6];
		packetId = packetId << 8;
		packetId += data[7];

		// System.out.println("Packet ID: " + packetId);

		// Measure the quantity of packet the payload will have
		int qty = frameData.length / Constants.PACKET_SIZE;

		// If last packet's size isn't PACKET_SIZE increase qty
		if (frameData.length % Constants.PACKET_SIZE != 0) {
			qty++;
		}

		// DEBUG: Number of packets being sent
		// System.out.println(String.valueOf("Number of packets being sent: " +
		// qty));
		// System.out.println(String.valueOf("Frame length: " +
		// frameData.length));

		// Loop through all Payload packets
		for (int i = 0; i < qty; i++) {
			data = packetController.getPayloadPacket();

			// packetId = 0;
			// packetId += data[5];
			// packetId = packetId << 8;
			// packetId += data[6];
			// packetId = packetId << 8;
			// packetId += data[7];

			// DEBUG: packetId
			// System.out.println("packetId : " + packetId);
			sendPacket(data);
		}

		// Get Trailer Packet and send it
		data = packetController.getTrailerPacket();
		sendPacket(data);
	}

	private boolean sendPacket(final byte[] data) {
		DatagramPacket packet = null;
		packet = new DatagramPacket(data, data.length, destIP, destPort);
		try {
			// Send it over UDP.
			socket.send(packet);

			return true;
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

		@Override
		public void onManagerConnected(final int status) {

			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");

				/* Now enable camera view to start receiving frames */
				mOpenCvCameraView.enableView();
			}
			break;
			default: {
				super.onManagerConnected(status);
			}
			break;
			}
		}
	};

}
