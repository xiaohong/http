import java.nio.channels.ServerSocketChannel;


public class Session {
	
	private ServerSocketChannel channel;
	
	public Session(ServerSocketChannel channel){
		this.channel = channel;
	}

	public ServerSocketChannel getChannel(){
		return channel;
	}
}
