import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	// This class is the server side of the auction system. It is responsible for handling client requests and updating the auction items.
	// It also logs all requests to a file called log.txt.
	// The log file is in the following format:
	// <date>|<time>|<client-ip>|<request>

	// Constants
	public static final int PORT = 6789;
	// The thread pool size is the maximum number of threads that can be created to handle client requests
    public static final int THREAD_POOL_SIZE = 30;
	private Map<String, Double> items;
    private Map<String, String> bids;
    
    // Constructor
    public Server() {
		// create a HashMap to store the items and their current bid values
        items = new HashMap<>();
        bids = new HashMap<>();
		try {
			// create a FileWriter object to write to the log file
			FileWriter fw = new FileWriter("log.txt", false);
			fw.close();
		} catch (IOException e) {
			// print an error message to the console
			System.err.println("Failed to delete log file.");
		}
    }
	// add an item to the auction
	// this method is synchronized so that only one thread can access it at a time
	public synchronized String addItem(String itemName) {
		// if the item already exists then return a failure message
        if (items.containsKey(itemName)) {
            return "Failure.";
        }
		// add the item to the HashMap and set its bid value to 0
        items.put(itemName, 0.0);
        return "Success.";
    }
	// make a bid on an item
	// this method is synchronized so that only one thread can access it at a time
	public synchronized String makeBid(String itemName, double bidValue, String clientIP) {
		// if the item does not exist or the bid value is less than or equal to 0 then return a failure message
        if (!items.containsKey(itemName) || bidValue <= 0) {
            return "Failure.";
        }
		// if the bid value is greater than the current bid value then update the bid value and return a success message
		// otherwise return a failure message
        double currentBid = items.get(itemName);
        if (bidValue > currentBid) {
            items.put(itemName, bidValue);
            bids.put(itemName, clientIP);
            return "Accepted";
        } else {
            return "Rejected.";
        }
    }
	// show all items in the auction
	// this method is synchronized so that only one thread can access it at a time
	public synchronized String showItems() {
		// if there are no items then return a message saying so
        if (items.isEmpty()) {
            return "There are currently no items in this auction.";
        }
		// otherwise return a string containing all the items and their current bid values
        StringBuilder sb = new StringBuilder("");
        for (String itemName : items.keySet()) {
            sb.append(itemName).append(" :  ").append(items.get(itemName)).append(" :  ");
            if (bids.containsKey(itemName)) {
                sb.append(bids.get(itemName));
            } else {
                sb.append("<no bids>");
            }
            sb.append("\n");
        }
		sb.append("end");
        return sb.toString();
    }
	// log a request to the log file
	// this method is synchronized so that only one thread can access it at a time
	public synchronized void logRequest(String clientIP, String request) {
		// write the request to the log file
        try {
            FileWriter fw = new FileWriter("log.txt", true);
            fw.write(String.format("%s|%s|%s|%s\n", LocalDate.now(), LocalTime.now(), clientIP, request));
            fw.close();
        } catch (IOException e) {
			// print an error message to the console
            System.err.println("Failed to log request as there was an error writing to the log file. The error could have been caused by the file being deleted or moved.");
        }
    }
	// main method
	// this method is responsible for creating a thread pool and listening for client connections
	// when a client connects, a new thread is created to handle the client's requests
	// the thread pool size is the maximum number of threads that can be created to handle client requests
	// if the thread pool size is exceeded then the client will have to wait until a thread becomes available
	// the server will continue to listen for client connections until it is terminated
	// the server will terminate if an error occurs while listening for client connections	
	public static void main( String[] args )
	{
		// create a thread pool
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		// listen for client connections
		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			// create a new Server object
            Server server = new Server();
			// print a message to the console
            System.out.println("Server started. Listening on port "+ PORT + ".");
            while (true) {
				// accept a client connection
                Socket clientSocket = serverSocket.accept();
				// create a new thread to handle the client's requests
                executor.execute(new ClientHandler(clientSocket, server));
            }
        } catch (IOException e) {
			// print an error message to the console
            System.err.println("Server error. Server shutting down. The error could have been caused by the port being in use or a problem with the thread.");
        }
	}
}