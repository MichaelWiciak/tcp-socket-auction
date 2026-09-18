import java.net.Socket;

public class AuctionProtocol {
    // This class is used to check if the client requests follow AuctionProtocol.
    // It also performs the appropriate action based on the client request.

    // Constants
    private Server server;
    private Socket clientSocket;
    // Constructor
    public AuctionProtocol(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    public String processInput(String inputLine) {
        // split the input into words
        String[] wordList = inputLine.split("\\s");
        // the first token is the command
        String command = wordList[0];
        // check the command and perform the appropriate action
        switch (command) {
            case "show":
                // check if the number of arguments is correct
                if (wordList.length != 1) {
                    // send an error message to the client
                    return "Wrong number of arguments\n Usage: show\n" + "Expected 1 argument, got " + wordList.length;
                }
                // send the list of items to the client
                return server.showItems();
            case "item":
                // check if the number of arguments is correct
                if (wordList.length != 2) {
                    // send an error message to the client
                    return "Wrong number of arguments\n Usage: item <item-name>\n" + "Expected 2 arguments, got " + wordList.length;
                } else {
                    String itemName = wordList[1];
                    // add the item to the list of items
                    String result = server.addItem(itemName);
                    // send the result to the client
                    return result;
                }
            case "bid":
                // check if the number of arguments is correct
                if (wordList.length != 3) {
                    // send an error message to the client handler 
                    return "Wrong number of arguments\n Usage: bid <item-name> <bid-value>" + "Expected 3 arguments, got " + wordList.length;
                } else {
                    // get the item name and the bid value
                    String itemName = wordList[1];
                    // check if the tokens[2] is a number
                    try {
                        Double.parseDouble(wordList[2]);
                    } catch (NumberFormatException e) {
                        // send an error message to the client
                        return "Invalid bid value: " + wordList[2] + "\n" + "A number was expected, but a string was received";
                    }
                    double bidValue = Double.parseDouble(wordList[2]);
                    // get the client IP address
                    String clientIP = clientSocket.getInetAddress().getHostAddress();
                    // add the bid to the list of bids
                    String result = server.makeBid(itemName, bidValue, clientIP);
                    // send the result to the client
                    return result;
                }
            default:
                // send an error message to the client if the command is invalid
                return "Invalid command: " + command;
        }
    }
}