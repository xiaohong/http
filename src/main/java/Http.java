import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Http {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			Socket socket = new Socket("127.0.0.1", 8080);
			socket.getOutputStream().write("GET / HTTP".getBytes());
			Thread.currentThread().sleep(100);
			socket.getOutputStream().write(
					"/1.1\r\nHost:www.google.com.hk\r\n\r\n".getBytes());
			socket.getOutputStream().write("GET / HTTP".getBytes());
			InputStream inputStream = socket.getInputStream();
			int read = inputStream.read();
			System.out.println("0" + read);
			while (read != -1) {
				System.out.print((char) read);
				read = inputStream.read();
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
