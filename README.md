# tcp-socket-auction

A Java auction over TCP sockets. A one-shot command-line client sends a single command to a persistent multi-client server, which keeps auction state in memory and logs every request.

## Commands

| Command              | Effect                                                     |
| -------------------- | ---------------------------------------------------------- |
| `show`               | List items, current highest bid, and bidder IP             |
| `item <name>`        | Add an item (starts at 0); `Failure.` if it already exists |
| `bid <name> <value>` | Place a bid; `Accepted`, `Rejected.`, or `Failure.`        |

## Run

```bash
javac -d out src/client/Client.java src/server/*.java
java -cp out Server        # terminal 1, listens on 127.0.0.1:6789
java -cp out Client show
java -cp out Client item "Table"
java -cp out Client bid "Table" 10
```

The client accepts exactly one command, prints the server's reply, and exits.

## Protocol

Line-based plain text over TCP on `127.0.0.1:6789`. Each connection carries one space-delimited request. `show` responses are rendered as ` <name> : <bid> : <ip>` lines followed by `end`.

## Structure

- `src/client/Client.java` - parses CLI args, sends one request, prints the reply
- `src/server/Server.java` - auction state (synchronized maps) and the 30-thread pool entry point
- `src/server/ClientHandler.java` - per-connection socket I/O
- `src/server/AuctionProtocol.java` - request parsing, validation, dispatch

## Logs

`log.txt` records every request as `date|time|client-ip|request` and is truncated when the server starts.

