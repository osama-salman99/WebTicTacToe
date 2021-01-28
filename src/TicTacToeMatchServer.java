import http.HttpRequest;
import http.HttpResponse;

public class TicTacToeMatchServer {
    private static int nextId = 0;
    private final int id;
    private final TicTacToeMatch ticTacToeMatch;
    private Player playerX;
    private Player playerO;
    private boolean full;
    private String lastMove;

    public TicTacToeMatchServer() {
        id = getNewId();
        ticTacToeMatch = new TicTacToeMatch();
        full = false;
    }

    private static int getNewId() {
        return nextId++;
    }

    public void assignUser(Player player) {
        if (playerX == null) {
            playerX = player;
        } else if (playerO == null) {
            playerO = player;
            full = true;
        } else {
            throw new RuntimeException("Cannot assign player: Match is full");
        }
    }

    public void processRequest(HttpRequest httpRequest) {
        if (!isRelevant(httpRequest)) {
            respondWithBadRequest(httpRequest);
            return;
        }

        if (httpRequest.getMethod().equals(HttpRequest.GET)) {
            respondWithMatchHtml(httpRequest);
        } else if (httpRequest.getMethod().equals(HttpRequest.POST)) {
            processPostRequest(httpRequest);
        } else {
            respondWithBadRequest(httpRequest);
        }
    }

    private void processPostRequest(HttpRequest httpRequest) {
        String status = httpRequest.getValue("status");
        if (status == null) {
            respondWithBadRequest(httpRequest);
            return;
        }
        switch (status) {
            case "assign":
                processAssign(httpRequest);
                break;
            case "waiting":
                processWaiting(httpRequest);
                break;
            case "play":
                processPlay(httpRequest);
                break;
            case "enquire":
                processEnquire(httpRequest);
            default:
                respondWithBadRequest(httpRequest);
        }
    }

    private void processAssign(HttpRequest httpRequest) {
        String name;
        String symbol;
        if (isPlayerX(httpRequest)) {
            name = playerX.getName();
            symbol = "X";
            playerX.setReady(true);
        } else {
            name = playerO.getName();
            symbol = "O";
            playerO.setReady(true);
        }
        HttpResponse httpResponse = new HttpResponse("200 OK");
        String responseData = "assign:" + symbol;
        responseData += ";";
        responseData += "name:" + name;
        httpResponse.setData(responseData);
        httpRequest.respond(httpResponse);
    }

    private void processWaiting(HttpRequest httpRequest) {
        Player otherPlayer;
        if (isPlayerX(httpRequest)) {
            otherPlayer = playerO;
        } else {
            otherPlayer = playerX;
        }
        HttpResponse httpResponse = new HttpResponse("200 OK");
        String responseData;
        if (otherPlayer != null && otherPlayer.isReady()) {
            responseData = "waiting:ready";
            responseData += ";";
            responseData += "name:" + otherPlayer.getName();
        } else {
            responseData = "waiting:wait";
        }
        httpResponse.setData(responseData);
        httpRequest.respond(httpResponse);
    }

    private void processPlay(HttpRequest httpRequest) {
        if (!full || !playerX.isReady() || !playerO.isReady() || ticTacToeMatch.isFinished()) {
            respondWithPlayDenied(httpRequest);
            return;
        }
        String indexString = httpRequest.getValue("index");
        if (indexString == null) {
            respondWithBadRequest(httpRequest);
            return;
        }
        int index;
        try {
            index = Integer.parseInt(indexString);
        } catch (NumberFormatException exception) {
            respondWithBadRequest(httpRequest);
            return;
        }
        if (index < 0 || index > 8) {
            respondWithBadRequest(httpRequest);
            return;
        }
        if (isPlayerX(httpRequest)) {
            if (ticTacToeMatch.isPlayerXTurn()) {
                lastMove = "X" + index;
                if (ticTacToeMatch.performMove(index)) {
                    respondWithPlayAccepted(httpRequest);
                } else {
                    respondWithPlayDenied(httpRequest);
                }
            } else {
                respondWithPlayDenied(httpRequest);
            }
        } else {
            if (!ticTacToeMatch.isPlayerXTurn()) {
                lastMove = "O" + index;
                if (ticTacToeMatch.performMove(index)) {
                    respondWithPlayAccepted(httpRequest);
                } else {
                    respondWithPlayDenied(httpRequest);
                }
            } else {
                respondWithPlayDenied(httpRequest);
            }
        }
    }

    private void processEnquire(HttpRequest httpRequest) {
        if (ticTacToeMatch.isFinished()) {
            respondWithMatchEnded(httpRequest, ticTacToeMatch.getWinner());
            return;
        }
        respondWithLastMove(httpRequest);
    }

    private void respondWithMatchEnded(HttpRequest httpRequest, char winner) {
        String winnerString;
        switch (winner) {
            case 'X':
                winnerString = playerX.getName();
                break;
            case 'O':
                winnerString = playerO.getName();
                break;
            case '-':
            default:
                winnerString = "draw";
        }
        HttpResponse httpResponse = new HttpResponse("200 OK");
        httpResponse.setData("enquire:finished;lastMove:" + lastMove + ";winner:" + winnerString);
        httpRequest.respond(httpResponse);
    }

    private void respondWithMatchHtml(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse("200 OK");
        httpResponse.setData(Resources.getMatchHtml());
        httpRequest.respond(httpResponse);
    }

    private void respondWithBadRequest(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse("400 Bad Request");
        httpResponse.setData(Resources.getBadRequestHtml());
        httpRequest.respond(httpResponse);
    }

    private void respondWithPlayDenied(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse("200 OK");
        httpResponse.setData("play:denied");
        httpRequest.respond(httpResponse);
    }

    private void respondWithPlayAccepted(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse("200 OK");
        httpResponse.setData("play:accepted");
        httpRequest.respond(httpResponse);
    }

    private void respondWithLastMove(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse("200 OK");
        httpResponse.setData("enquire:playing;lastMove:" + lastMove);
        httpRequest.respond(httpResponse);
    }

    private boolean isPlayerX(HttpRequest httpRequest) {
        return playerX != null && httpRequest.getInetAddress().equals(playerX.getInetAddress());
    }

    private boolean isPlayerO(HttpRequest httpRequest) {
        return playerO != null && httpRequest.getInetAddress().equals(playerO.getInetAddress());
    }

    public int getId() {
        return id;
    }

    public boolean isFull() {
        return full;
    }

    public boolean isRelevant(HttpRequest httpRequest) {
        return isPlayerX(httpRequest) || isPlayerO(httpRequest);
    }
}
