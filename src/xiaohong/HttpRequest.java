package xiaohong;

import java.util.Map;

public class HttpRequest {

	private String method;
	private Map<String, String> header;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}


}
