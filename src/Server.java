import exceptions.EmptyRequestException;
import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Server {
    private static final String MATCH_PATH_PREFIX = "match_";
    private static final Pattern MATCH_PATH_PATTERN = Pattern.compile("^" + MATCH_PATH_PREFIX + "[0-9]+$");
    private final ArrayList<TicTacToeMatchServer> matchServers;
    private final ServerSocket serverSocket;
    private boolean enabled;
    private boolean running;

    public Server(int portNumber) throws IOException {
        this.serverSocket = new ServerSocket(portNumber);
        this.enabled = true;
        this.matchServers = new ArrayList<>();
        new Thread(this::runServer).start();
    }

    private static boolean isMatchPath(String path) {
        return MATCH_PATH_PATTERN.matcher(path).matches();
    }

    private void runServer() {
        running = true;
        while (running && enabled) {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException exception) {
                if (!serverSocket.isClosed()) {
                    exception.printStackTrace();
                }
                return;
            }
            new Thread(() -> processRequest(socket)).start();
        }
        running = false;
    }

    private void processRequest(Socket socket) {
        HttpRequest httpRequest;
        try {
            httpRequest = new HttpRequest(socket);
        } catch (EmptyRequestException exception) {
            System.out.println("Request dropped: " + exception.getMessage());
            return;
        }
        System.out.println(httpRequest);

        redirectToPath(httpRequest);
    }

    private void redirectToPath(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        if (!path.isEmpty()) {
            if (isMatchPath(path)) {
                int id = Integer.parseInt(path.replace(MATCH_PATH_PREFIX, ""));
                for (TicTacToeMatchServer matchServer : matchServers) {
                    if (matchServer.getId() == id) {
                        matchServer.processRequest(httpRequest);
                        return;
                    }
                }
            }
            respondWithNotFound(httpRequest);
            return;
        }
        if (httpRequest.getMethod().equals(HttpRequest.POST)) {
            assignToMatch(httpRequest);
            return;
        }
        respondWithIndexHtml(httpRequest);
    }

    private void respondWithIndexHtml(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse("200 OK");
        httpResponse.setData(Resources.getIndexHtml());
        httpRequest.respond(httpResponse);
    }

    private void respondWithNotFound(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse("404 Not Found");
        httpResponse.setData(Resources.getNotFoundHtml());
        httpRequest.respond(httpResponse);
    }

    private void respondWithBadRequest(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse("400 Bad Request");
        httpResponse.setData(Resources.getBadRequestHtml());
        httpRequest.respond(httpResponse);
    }

    private void assignToMatch(HttpRequest httpRequest) {
        String name = httpRequest.getValue("name");
        if (name == null || name.length() < 1) {
            respondWithBadRequest(httpRequest);
            return;
        }
        TicTacToeMatchServer matchServer = null;
        for (TicTacToeMatchServer existingMatchServer : matchServers) {
            if (!existingMatchServer.isFull()) {
                matchServer = existingMatchServer;
                break;
            }
        }
        if (matchServer == null) {
            matchServer = new TicTacToeMatchServer();
            matchServers.add(matchServer);
        }
        matchServer.assignUser(new Player(httpRequest.getInetAddress(), name));

        HttpResponse httpResponse = new HttpResponse("303 See Other");
        httpResponse.addHeader("Location", "/" + MATCH_PATH_PREFIX + matchServer.getId());
        httpRequest.respond(httpResponse);
    }


    public void shutDown() {
        this.enabled = false;
        try {
            serverSocket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void pause() {
        running = false;
    }

    public void resume() {
        runServer();
    }

}
