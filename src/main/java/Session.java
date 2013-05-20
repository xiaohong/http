import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Session {

	private SocketChannel channel;

	public ByteBuffer data = ByteBuffer.allocate(5);

	private HttpRequestDecoder request;

	private LogicHandler handler;

	public Session(SocketChannel channel, LogicHandler handler) {
		this.channel = channel;
		this.handler = handler;
	}

	public SocketChannel getChannel() {
		return channel;
	}

	public HttpRequestDecoder getRequest() {
		return request;
	}

	public void setRequest(HttpRequestDecoder request) {
		this.request = request;
	}

	public LogicHandler getHandler() {
		return handler;
	}

	public void setHandler(LogicHandler handler) {
		this.handler = handler;
	}

}
