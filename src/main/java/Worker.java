import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Worker {

	private ConcurrentLinkedQueue<Session> queues;

	private Selector selector;

	public Worker() {
		queues = new ConcurrentLinkedQueue<Session>();
		try {
			selector = Selector.open();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addSession(Session session) {
		queues.add(session);
		selector.wakeup();
	}

	public void wakeup() {
		selector.wakeup();
	}

	public void processNewSession() throws IOException {
		while (queues.size() > 0) {
			Session session = queues.poll();
			if (session != null) {
				session.getChannel().configureBlocking(false);
				session.getChannel().register(selector, SelectionKey.OP_READ,
						session);
			}
		}
	}

	public void run() {
		new Thread(new Runnable() {

			public void run() {

				for (; true;) {

					try {
						processNewSession();

						int select = selector.select();
						if (select > 0) {
							Set<SelectionKey> selectedKeys = selector
									.selectedKeys();
							Iterator<SelectionKey> iterator = selectedKeys
									.iterator();
							while (iterator.hasNext()) {
								SelectionKey next = iterator.next();
								if (next.isReadable()) {
									System.out.println("read...");
									doRead(next);
								} else if (next.isWritable()) {
									doWrite(next);
								}
								iterator.remove();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}).start();
	}

	private void doWrite(SelectionKey next) throws IOException {

	}

	private void doRead(SelectionKey next) {
		SocketChannel channel = (SocketChannel) next.channel();
		// 如何从channel读取数据，如果数据不是一次性全部来的话，那已经读取的数据如何处理
		Session at = (Session) next.attachment();
		try {
			for (; true;) {
				ByteBuffer buff = ByteBuffer.allocate(1024);
				int i = channel.read(buff);
				if (i == 0) {

					break;
				}
				if (i == -1) {
					channel.shutdownOutput();
					next.cancel();
					return;
				}

				if (at.getRequest() == null) {
					at.setRequest(new HttpRequestDecoder(at));
				}
				buff.flip();
				State state = at.getRequest().encode(buff);
				if (state == State.Complete) {
					next.interestOps(next.interestOps() & ~SelectionKey.OP_READ);
					next.interestOps(next.interestOps() | SelectionKey.OP_WRITE);
					// channel.register(selector, SelectionKey.OP_WRITE);

					ByteBuffer wrap = at.getHandler()
							.handle(at.getRequest().getReqest()).toBytes();
					channel.write(wrap);

					// next.interestOps(next.interestOps()&
					// ~SelectionKey.OP_READ);
					next.interestOps(SelectionKey.OP_READ);

					return;
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
