import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static com.sun.xml.internal.ws.commons.xmlutil.Converter.UTF_8;

public class MainClass {
    public static void main(String[] args) throws IOException {
        Server server = new Server(6969);
        Scanner scanner = new Scanner(System.in);
        while (server.isEnabled()) {
            String command = scanner.nextLine();
            switch (command) {
                case "shutdown":
                    server.shutDown();
                    System.out.println("Server shut down");
                    break;
                case "pause":
                    server.pause();
                    System.out.println("Server is paused");
                    break;
                case "resume":
                    server.resume();
                    System.out.println("Server is resumed");
                    break;
                default:
                    System.out.println("Unknown command");
            }
        }
    }

    private static void printBoard(char[] board) {
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(board[index] + " ");
                index++;
            }
            System.out.println();
        }
    }
}
