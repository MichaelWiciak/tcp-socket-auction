import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ClientHandler implements Runnable {
    // This class is used to handle the communication between the server and the client.
    // It also checks if client requests follow AuctionProtocol.
    // It receives the request from the client and sends the response back to the client.
    // It also handles the exceptions that may occur during the connection process.

    // Constants
    private Socket clientSocket;
    private Server server;
    private AuctionProtocol auctionProtocol;


    // Constructor
    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.auctionProtocol = new AuctionProtocol(server, clientSocket);
    }
    // This method is used to handle the client requests
    // It takes no arguments
    public void run() {
        try {
            // create a PrintWriter object to send commands to the server
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            // create a BufferedReader object to receive the response from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            // read the input from the client
            while ((inputLine = in.readLine()) != null) {
                String response = auctionProtocol.processInput(inputLine);
                out.println(response);
                // log the request
                server.logRequest(clientSocket.getInetAddress().getHostAddress(), inputLine);
            }
            // close the connection
            clientSocket.close();
        } 
        // handle the exceptions
        catch (IOException e) {
            System.err.println("Error handling client request: " + "IOException occurred\n" + "Check if the client is connected to the server");
        }
    }
}