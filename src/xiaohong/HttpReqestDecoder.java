package xiaohong;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class HttpReqestDecoder {
	
	public static enum State{
		start,
 header
		
	}
	
	private ByteBuffer old;
	
	
	public State decode(State s, ByteBuffer bf){
		switch (s) {
		case start:
			String first = readLine(bf); 
			String[] h = splitFirst(first);
			if (h.length != 3) {
				throw new RuntimeException(first);
			}
			s = State.header;

		case header:
			Map<String, String> header = readHeader(bf);
		default:
			break;
		}
		return State.start;
	}


	private Map<String, String> readHeader(ByteBuffer bf) {
		Map<String, String> data = new HashMap<String, String>();
		while(true){
			String readLine = readLine(bf); 
			if("".equals(readLine)){
				return data;
			}
			String[] header = splitHeader(readLine);
			data.put(header[0], header[1]);
		}
	}

	private String[] splitHeader(String readLine) {
		String[] data = new String[2];
		String t = "";
		for (int i = 0; i < readLine.length(); i++) {
			char ch = readLine.charAt(i);
			if (Character.isWhitespace(ch)) {
				continue;
			}
			if (":".equals(ch)) {
				return new String[] { t, readLine.substring(i + 1) };
			}
			t = t + ch;
		}
		return null;
    }


	private String[] splitFirst(String first) {
		int status = 0;
		String t = "";
		String[] h = new String[3];
		int m = 0;
		for (int i = 0; i < first.length(); i++) {
			char c = first.charAt(i);
			if (Character.isWhitespace(c)) {
				if (t.equals("")) {
					continue;
				} else {
					h[m++] = t;
				}
			} else {
				t = t + "";
			}
		}
		return h;
    }


	private String readLine(ByteBuffer bf) {
		StringBuilder builder = new StringBuilder();
		for(;true;){
			byte b = bf.get();
			if(b == '\r'){
				if(bf.get() == '\n'){
					return builder.toString();
				}else{
					throw new RuntimeException("非法请求");
				}
			}else if(b == '\n'){
				return builder.toString();
			}else{
				builder.append((char)b);
			}
			
		}
    }

}
