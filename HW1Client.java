import java.io.*;
import java.net.*;

public class HW1Client {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java HW1Client <host> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
            Socket socket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.print("Enter the URL to fetch (e.g., GET example.com/index.html): ");
            String userInput = stdIn.readLine();
            out.println(userInput);

            // Reading and saving the response from the server
            String fileName = userInput.split(" ")[1].replace("/", "_");
            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName))) {
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    System.out.println(serverResponse); // Show the response (optional)
                    fileWriter.write(serverResponse + "\n");

                    // Progress indicator
                    System.out.print(".");
                }
                System.out.println("\nDownload complete. Saved as: " + fileName);
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            e.printStackTrace();
        }
    }
}
