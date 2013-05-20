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
	
	public Worker(){
		queues = new ConcurrentLinkedQueue<Session>();
		try{
		selector = Selector.open();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addSession(Session session){
		queues.add(session);
	}
	
	
	public void processNewSession() throws ClosedChannelException{
		while(queues.size()>0){
			Session session = queues.poll(); 
			if (session != null){
				session.getChannel().register(selector, SelectionKey.OP_READ, session);
			}
		}
	}
	
	public void run(){
		new Thread(new Runnable() {
			
			public void run() {

				try{
					for (;true;){
						
						processNewSession();
						
						int select = selector.select(100); 
						if(select >0 ){
							Set<SelectionKey> selectedKeys = selector.selectedKeys(); 
							Iterator<SelectionKey> iterator = selectedKeys.iterator(); 
							while(iterator.hasNext()){
								SelectionKey next = iterator.next(); 
								if(next.isAcceptable()){
									ServerSocketChannel chan = (ServerSocketChannel) next.channel();
									SocketChannel accept = chan.accept(); 
									accept.configureBlocking(false);
									accept.register(selector, SelectionKey.OP_READ, new Atter());
								}else if(next.isReadable()){
									System.out.println("read...");
									doRead(next);
								}else if(next.isWritable()){
									doWrite(next);
								}
								iterator.remove();
							}
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	private void doWrite(SelectionKey next) {
		// TODO Auto-generated method stub
		
	}

	private void doRead(SelectionKey next) {
		SocketChannel channel =  (SocketChannel)next.channel();
		Atter at = (Atter) next.attachment();
		try {
			at.data.rewind();
			int i = channel.read(at.data);
			if(i == -1){
				next.cancel();
				return;
			}
			at.data.flip();
			String line = readLine(at.data); 
			if(line == null){
				at.data.position(at.data.limit());
				next.interestOps(SelectionKey.OP_READ);
			}else{
				System.out.println(line);
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String readLine(ByteBuffer data){
		StringBuilder b = new StringBuilder();
		while(data.hasRemaining()){
			byte c = data.get(); 
			if(c == '\r'){
				if(data.hasRemaining()&&data.get() == '\n'){
					return b.toString();
				}
			}
			b.append((char)c);
		}
		System.out.println(b.toString());
		return null;
	}
	
}
