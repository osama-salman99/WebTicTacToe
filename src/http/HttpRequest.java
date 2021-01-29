package http;

import exceptions.EmptyRequestException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class HttpRequest {
    public static final String GET = "GET";
    public static final String POST = "POST";
    private final InetAddress inetAddress;
    private final String method;
    private final String path;
    private final String data;
    private final Map<String, String> pairs;
    private final OutputStream outputStream;

    public HttpRequest(Socket socket) throws EmptyRequestException {
        inetAddress = socket.getInetAddress();
        byte[] data = new byte[65536];
        int length;
        try {
            length = socket.getInputStream().read(data);
            outputStream = socket.getOutputStream();
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Could not get input stream of socket", exception);
        }
        if (length == -1) {
            throw new EmptyRequestException(this);
        }
        Scanner scanner = new Scanner(new String(Arrays.copyOfRange(data, 0, length)));

        this.method = scanner.next();
        this.path = scanner.next().replaceFirst("/", "");

        String line;
        do {
            line = scanner.nextLine();
        } while (!line.isEmpty() && scanner.hasNext());

        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        this.data = stringBuilder.toString();
        pairs = new HashMap<>();
        processData();
    }

    private void processData() {
        String[] stringPairsArray = data.split(";");
        for (String stringPair : stringPairsArray) {
            String[] pair = stringPair.split("=");
            if (pair.length == 2) {
                pairs.put(pair[0].toLowerCase(), pair[1]);
            }
        }
    }

    public String getValue(String key) {
        key = key.toLowerCase();
        return pairs.get(key);
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public String getMethod() {
        return method;
    }

    /**
     * @return Path requested without the first slash
     */
    public String getPath() {
        return path;
    }

    public void respond(HttpResponse response) {
        try {
            outputStream.write(response.build().getBytes());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public String toString() {
        return "http.HttpRequest{" +
                "inetAddress=" + inetAddress +
                ", method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
