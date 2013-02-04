package xiaohong;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Http {

	static Selector mainSelector;

	public static void main(String[] args) {
		try {
			mainSelector = Selector.open();

			ServerSocketChannel channel = ServerSocketChannel.open();
			channel.configureBlocking(false);
			channel.socket().bind(new InetSocketAddress(9999));
			channel.register(mainSelector, SelectionKey.OP_ACCEPT);
			
			while (true) {
				mainSelector.select();
				Set<SelectionKey> selectedKeys = mainSelector.selectedKeys();
				Iterator<SelectionKey> iterator = selectedKeys.iterator();
				while (iterator.hasNext()) {
					SelectionKey selectionKey = iterator.next();
					if (selectionKey.isAcceptable()) {
						doConnect(selectionKey);
					} else if (selectionKey.isReadable()) {
						doRead(selectionKey);
					} else if (selectionKey.isWritable()) {
						doWrite(selectionKey);
					}
					iterator.remove();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void doWrite(SelectionKey selectionKey) {
		// TODO Auto-generated method stub

	}

	private static void doRead(SelectionKey selectionKey) {
		try {
			SocketChannel channel = (SocketChannel) selectionKey.channel();
			ByteBuffer dst = ByteBuffer.allocate(1024 * 1024);
			int read = channel.read(dst);
			if (read > 0) {
			dst.flip();

			InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(dst.array(), 0, dst.remaining()), "UTF-8");

				System.out.println(new String(dst.array(), 0, dst.remaining()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readLine(InputStreamReader reader) {
		try {
		StringBuilder builder = new StringBuilder();
		char c = (char) reader.read();
			while (c != '\n') {
				builder.append(c);
				c = (char) reader.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void doConnect(SelectionKey selectionKey) {
		ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
		try {
			SocketChannel accept = channel.accept();
			accept.configureBlocking(false);
			accept.register(mainSelector, SelectionKey.OP_READ);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
