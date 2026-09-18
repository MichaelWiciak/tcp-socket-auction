import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
	// This class is used to connect to the server and send commands to it.
	// It also receives the response from the server and prints it to the console.
	// It also handles the exceptions that may occur during the connection process.
	
	// Constants
	private static Socket socket;
	private static PrintWriter out;
	private static BufferedReader in;
	public static final int PORT = 6789;
	
	// Constructor
	public Client() {
		socket = null;
		out = null;
		in = null;
	}
	// This method is used to send commands to the server and receive the response
	// It takes an array of strings as an argument
	// The first string in the array is the command to be sent to the server
	// The second string in the array is the item name
	// The third string in the array is the bid value
	public void command( String[] args )
	{
		// if the number of arguments is less than 1 then the user has entered no arguments
		if (args.length < 1) {
            System.out.println("No arguments provided\nThe following methods are available:\n\tshow\n\titem <string>\n\tbid <item> <value>");
            return;
        }
		// if the number of arguments is greater than 3 then the user has entered too many arguments
		// the maximum number of arguments is 3 and that is when the user is bidding on an item
		if (args.length > 3) {
			System.out.println("Too many arguments\nThe following methods are available:\n\tshow\n\titem <string>\n\tbid <item> <value>");
			return;
		}
		// the first argument is the command to be sent to the server
		String command = args[0];
		try {
			// connect to the server at PORT 6789
			socket = new Socket("localhost", PORT);
			// create a PrintWriter object to send commands to the server
			out = new PrintWriter(socket.getOutputStream(), true);
			// create a BufferedReader object to receive the response from the server
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		// if the server is not running then the connection will fail
		catch (UnknownHostException e) {
			// print an error message to the console
			System.err.println("Server at port " + PORT + " is not running. Check if the server is running.\n");
			// exit the method gracefully
			return;
		}
		catch (IOException e) {
			// print an error message to the console
			System.err.println("Couldn't get I/O for the connection to host.\n");
			// exit the method gracefully
			return; 
		}
		// Send the command to the server
		String response = "";
		try {
			// switch statement to handle the different commands
			switch (command) {
                case "show":
					// check if the number of arguments is equal to 1
					// if it is not equal to 1 then the user has entered too many arguments
					if (args.length != 1) {
						// print an error message to the console
						System.out.println("Wrong Number of Arguments\n Usage: java Client show\n Expected 1 argument, got " + args.length + " arguments");
						// close the socket
						socket.close();
						// exit the method gracefully
						return;
					}
					// send the command to the server
                    out.println("show");
					// receive the response from the server
                    response = in.readLine();
					// print the response to the console
                    while (!response.equals("end")) {
                        System.out.println(response);
						// if the response is "There are currently no items in this auction." then break out of the loop
						if (response.equals("There are currently no items in this auction.")){
							break;
						}
						// receive the next response from the server
                        response = in.readLine();
                    }
                    break;
                case "item":
					// if the number of arguments is not equal to 2 then the user has entered no item name
                    if (args.length != 2) {
						// print an error message to the console
                        System.out.println("Wrong Number of Arguments\n Usage: java Client item <item-name>\n Expected 2 arguments, got " + args.length + " arguments");
						// close the socket
						socket.close();
						// exit the method gracefully
                        return;
                    }
					// send the command to the server
                    out.println("item " + args[1]);
					// receive the response from the server
                    response = in.readLine();
					// print the response to the console
                    System.out.println(response);
                    break;
                case "bid":
					// if the number of arguments is not equal to  3 then the user has entered no bid value
                    if (args.length != 3) {
						// print an error message to the console
                        System.out.println("Wrong Number of Arguments\n Usage: java Client bid <item-name> <bid-value>\n Expected 3 arguments, got " + args.length + " arguments");
						// close the socket
						socket.close();
						// exit the method gracefully
                        return;
                    }
					// check if the bid value is a number
					try {
						// try to parse the bid value to float
						// if the bid value is not a number then the parse method will throw an exception
						Double.parseDouble(args[2]);
					}
					catch (NumberFormatException e) {
						// if the bid value is not a number then print an error message to the console
						System.out.println("Invalid bid value: " + args[2] + "\nBid value must be a number");
						// close the socket
						socket.close();
						// exit the method gracefully
						return;
					}
					// send the command to the server
                    out.println("bid " + args[1] + " " + args[2]);
					// receive the response from the server
                    response = in.readLine();
					// print the response to the console
                    System.out.println(response);
                    break;
                default:
					// if the user has entered an invalid command then print an error message to the console
                    System.out.println("Invalid command: " + command);
                    break;
            }
			// close the socket
			socket.close();
			in.close();
			out.close();
		}
		// if the server is not running then the connection will fail
		catch (UnknownHostException e) {
			System.err.println("Server at port " + PORT + " is not running. Check if the server is running.\n");
			return;
		}
		catch (IOException e) {
			System.err.println("I/O exception during execution\n Check if the server is running.\n");
			return;
		}
	}
	// main method
	// this method is used to create a Client object and call the command method
	// it takes an array of strings as an argument
	public static void main(String[] args) {
		// create a Client object
		Client bidder = new Client();
		// call the command method
		bidder.command(args);
		// exit the program gracefully after executing one command
	  }
}