import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class HttpServer {
	
	Selector selector;
	
	private Worker worker;
	
	public  void aa(String[] args) {
		try{
		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.configureBlocking(false);
		channel.bind(new InetSocketAddress("127.0.0.1", 88));
		
		selector = Selector.open();
		channel.register(selector, SelectionKey.OP_ACCEPT);
		
		worker = new Worker();
		
		while(true){
			int select = selector.select(); 
			if(select >0){
				Set<SelectionKey> selectedKeys = selector.selectedKeys(); 
				Iterator<SelectionKey> iterator = selectedKeys.iterator();
				while(iterator.hasNext()){
					SelectionKey next = iterator.next(); 
					if(next.isAcceptable()){
						ServerSocketChannel chan = (ServerSocketChannel) next.channel();
						SocketChannel accept = chan.accept(); 
						accept.configureBlocking(false);
					//	accept.register(selector, SelectionKey.OP_READ, new Atter());
						System.out.println("connect...");
						worker.addSession(new Session(chan));
					}else{
						System.err.println("error...");
					}
					iterator.remove();
				}
			}
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	public static void main(String[] args) {
		HttpServer v = new HttpServer();
		v.aa(args);
	}
}
