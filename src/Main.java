import java.util.Scanner;

/**
 * Runnable main class
 * Created by robinsat on 4/30/2017.
 */
public class Main {

    public static void main(String[] args) {
        while(true) {
            System.out.println("Enter search query, or 'quit' to exit: ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.next();

            if(input.equalsIgnoreCase("quit")) {
                System.exit(0);
            }

            // Find relevant documents
        }

    }
}
