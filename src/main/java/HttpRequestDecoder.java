import java.nio.ByteBuffer;

public class HttpRequestDecoder {

	private byte[] lines;
	State state = State.Init;
	private ByteBuffer remind;

	private Session session;

	private HttpReqest reqest;

	public HttpRequestDecoder(Session at) {
		this.session = at;
		this.remind = ByteBuffer.allocate(1024);
		remind.flip();
		this.reqest = new HttpReqest();
	}

	public byte[] getLines() {
		return lines;
	}

	public void setLines(byte[] lines) {
		this.lines = lines;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public ByteBuffer getRemind() {
		return remind;
	}

	public void setRemind(ByteBuffer remind) {
		this.remind = remind;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public HttpReqest getReqest() {
		return reqest;
	}

	public void setReqest(HttpReqest reqest) {
		this.reqest = reqest;
	}

	public State encode(ByteBuffer bf) {
		if (remind.hasRemaining()) {
			ByteBuffer nbf = ByteBuffer.allocate(remind.remaining()
					+ bf.remaining());
			while (remind.hasRemaining()) {
				nbf.put(remind.get());
			}
			while (bf.hasRemaining()) {
				nbf.put(bf.get());
			}
			nbf.rewind();
			bf = nbf;
		}
		try {
			switch (state) {
			case Init:
				String header = readLine(bf);
				String[] h = splitHeader(header);
				reqest.setMethod(h[0]);
				reqest.setUrl(h[1]);
				reqest.setProtocol(h[2]);
				state = State.Header;
			case Header:
				while (true) {
					String hs = readLine(bf);
					if (hs == null) {
						state = State.Complete;
						return state;
					}
					reqest.getHeader().add(hs);
				}
			}
		} catch (Exception e) {
			remind.limit(remind.capacity() - 1);
			remind.rewind();
			while (bf.hasRemaining()) {
				remind.put(bf.get());
			}
			remind.rewind();
			return null;
		}

		return null;
	}

	private String[] splitHeader(String header) {
		String[] d = new String[3];
		String s = null;
		int k = 0;
		for (int i = 0; i < header.length(); i++) {
			if (header.charAt(i) == ' ' && s != null) {
				d[k] = s;
				k++;
				s = null;
			} else if (header.charAt(i) == ' ' && s == null) {
				continue;
			} else {
				if (s == null) {
					s = "" + header.charAt(i);
				} else {
					s = s + header.charAt(i);
				}
			}
		}
		d[k] = s;
		return d;
	}

	public String readLine(ByteBuffer data) {
		StringBuilder b = new StringBuilder();
		data.mark();
		while (data.hasRemaining()) {
			byte c = data.get();
			if (c == '\r') {
				if (data.hasRemaining() && data.get() == '\n') {
					if (b.length() == 0) {
						return null;
					} else {
						return b.toString();
					}
				}
			}
			b.append((char) c);
		}
		data.reset();
		throw new RuntimeException("");
	}
}
