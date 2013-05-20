import java.nio.ByteBuffer;


public class HttpRequestDecoder {
	
	static public enum State{
		Init, Header,Body,Complete;
	}

	private byte[] lines;
	State state = State.Init;
	
//	public State encode(ByteBuffer bf){
//		switch (state){
//		case Init:
//			
//			
//		}
//		
//	}
}
