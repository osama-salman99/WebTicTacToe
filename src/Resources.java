import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Resources {
    private static String indexHtml;
    private static String matchHtml;
    private static String notFoundHtml;
    private static String badRequestHtml;

    private Resources() {
    }

    public static String getIndexHtml() {
        if (indexHtml == null) {
            try {
                indexHtml = new String(Files.readAllBytes(Paths.get("res\\index.html")));
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
        return indexHtml;
    }

    public static String getMatchHtml() {
        if (matchHtml == null) {
            try {
                matchHtml = new String(Files.readAllBytes(Paths.get("res\\match.html")));
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
        return matchHtml;
    }

    public static String getNotFoundHtml() {
        if (notFoundHtml == null) {
            try {
                notFoundHtml = new String(Files.readAllBytes(Paths.get("res\\not_found.html")));
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
        return notFoundHtml;
    }

    public static String getBadRequestHtml() {
        if (badRequestHtml == null) {
            try {
                badRequestHtml = new String(Files.readAllBytes(Paths.get("res\\bad_request.html")));
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
        return badRequestHtml;
    }
}

