import java.net.*;
import java.io.*;

public class HW1Server {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java HW1Server <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error listening on port " + portNumber);
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String clientRequest = in.readLine();
            System.out.println("Request received: " + clientRequest);

            // Parsing the client request
            if (clientRequest.startsWith("GET")) {
                String[] requestParts = clientRequest.split(" ");
                if (requestParts.length == 2) {
                    String url = requestParts[1];
                    String[] urlParts = url.split("/", 2);
                    String host = urlParts[0];
                    String file = "/" + urlParts[1];

                    // Forwarding request to the actual web server
                    try (Socket webSocket = new Socket(host, 80);
                         PrintWriter webOut = new PrintWriter(webSocket.getOutputStream(), true);
                         BufferedReader webIn = new BufferedReader(new InputStreamReader(webSocket.getInputStream()))) {

                        webOut.println("GET " + file + " HTTP/1.1");
                        webOut.println("Host: " + host);
                        webOut.println("");  // End of headers

                        // Reading the response from the web server
                        String webResponse;
                        StringBuilder htmlContent = new StringBuilder();
                        boolean isHeader = true;
                        while ((webResponse = webIn.readLine()) != null) {
                            out.println(webResponse);
                            if (!isHeader) {
                                htmlContent.append(webResponse).append("\n");
                            }
                            if (webResponse.isEmpty()) {
                                isHeader = false;
                            }
                        }

                        // Saving the HTML file locally (proxy-HTML_FILENAME)
                        String fileName = "proxy-" + urlParts[1].replace("/", "_");
                        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName))) {
                            fileWriter.write(htmlContent.toString());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
