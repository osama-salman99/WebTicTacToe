package exceptions;

import http.HttpRequest;

public class EmptyRequestException extends Throwable {
    private final HttpRequest httpRequest;

    public EmptyRequestException(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getMessage() {
        return httpRequest.getInetAddress() + " Request is empty";
    }
}
