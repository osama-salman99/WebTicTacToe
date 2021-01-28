package http;

import java.util.ArrayList;
import java.util.List;

public class HttpResponse {
    private static final String CLRF = "\r\n";
    private final String status;
    private final List<Header> headers;
    private String data;

    public HttpResponse(String status) {
        this.status = status;
        this.headers = new ArrayList<>();
    }

    public void addHeader(String header, String value) {
        headers.add(new Header(header, value));
    }

    public void setData(String data) {
        this.data = data;
    }

    String build() {
        StringBuilder httpResponseBuilder = new StringBuilder("HTTP/1.1 ");
        httpResponseBuilder.append(status).append(CLRF);
        for (Header header : headers) {
            httpResponseBuilder.append(header.header).append(": ").append(header.value).append(CLRF);
        }
        if (data != null) {
            httpResponseBuilder.append("Content-Length: ").append(data.getBytes().length).append(CLRF).append(CLRF);
            httpResponseBuilder.append(data);
        }
        httpResponseBuilder.append(CLRF);
        return httpResponseBuilder.toString();
    }

    private static class Header {
        private final String header;
        private final String value;

        public Header(String header, String value) {
            this.header = header.replace(":", "");
            this.value = value;
        }
    }
}
