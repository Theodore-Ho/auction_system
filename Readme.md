# Distributed Systems Assignment

- [1. Introduction](#1-introduction)
- [1.1. Client Requirements](#11-client-requirements)
- [1.2. Server Requirements](#12-server-requirements)
- [2. Server Requirements](#2-application-architecture)
- [2.1. Basic Logic](#21-basic-logic)
- [2.2. Folder Instruction](#22-folder-instruction)
- [2.3. Code Structure](#23-code-structure)
- [3. How to run?](#3-how-to-run)
- [3.1. Windows](#31-windows)
- [3.2. MacOS](#32-macos)
- [4. Demo Screenshots](#4-demo-screenshots)

## 1. Introduction

For this assignment you are required to design and implement a distributed application that is a simulator of an electronic auction system.

There will be multiple clients and a server. The server will offer items for sale. The client module will allow the user to bid for items. An item is sold to the highest bidder. Your code will enable users only to bid for items – it will not conduct actual purchase transactions.

### 1.1. Client Requirements

The clients should be able to use the all services offered by the auction server.

Clients should:

- Connects to the server. The item currently being offered for sale and the current bid or a (or reserve price) are displayed.
- Enter the bid. The amount entered should be greater than the current highest bid.
- After a new bid is placed, the amount of the new bid must be displayed on the client’s window/console.

### 1.2. Server Requirements

The server should offer the following services:

- Join auction
- Leave auction
- Bid on an item
- List auction items

The server should:

- Receive connections from multiple clients, i.e. support concurrent multiple clients’ connections.
- After a client connects, notify the client which item is currently on sale and the highest bid (or reserve price).
- Specify the bid period. Max allowed is 45 seconds. When a new bid is raised, the bid period is reset back.
- When a new bid is placed, all clients are notified immediately. Clients should be notified about the time left for bidding (when appropriate).
- If the bid period elapses without a new bid, then the auction for this item closes. The successful bidder (if any) is chosen and all clients are notified.
- When an auction for one item finishes, another item auctioning should start. Minimum of 5 items should be auctioned, one after another.
- Any item not sold should be auctioned again (automatically).
- Allow including new items for sale in the auction server while the auction is running.
- Register clients: unique name will suffice.

## 2. Application Architecture

### 2.1. Basic Logic

Server running in a thread, each client running in a thread. When client create, it automatically connects to server. A HashMap in the server records all the clients' socket information it connects with. This HashMap will use for sending message to everyone.

The auction is running when server starting. The countdown is a 45 seconds infinite loop running in a unique thread. Once the client bid, the server reset the countdown to 45. Otherwise, when the countdown became 0, the program will judge the auction status: either continue auction current item, move to next item, or finish all the auction.

The message type sent between client and server is String. A message indicator header is included. For server to client, the header is split with dollar symbol "$"; for client to server, the header is split with space " ".

### 2.2. Folder Instruction

- [img](https://github.com/Theodore-Ho/auction_system/tree/main/img): Demo screenshots
- [impl](https://github.com/Theodore-Ho/auction_system/tree/main/impl): Code modules
  - [META-INF](https://github.com/Theodore-Ho/auction_system/tree/main/impl/meta-inf): Jar meta information folder
  - [src](https://github.com/Theodore-Ho/auction_system/tree/main/impl/src): Code root
- [out](https://github.com/Theodore-Ho/auction_system/tree/main/out): Build results
  - [artifacts](https://github.com/Theodore-Ho/auction_system/tree/main/out/artifacts): Jar folder
  - [production](https://github.com/Theodore-Ho/auction_system/tree/main/out/production): Class folder
- [run](https://github.com/Theodore-Ho/auction_system/tree/main/run): BAT, SHELL or EXE file to run the program

### 2.3. Code Structure

- ServerApplication: startup class for server.
- ClientApplication: startup class for client.
- entity (folder):
  - Item: class for auction item.
  - Record: class for bid record.
- enumeration (folder):
  - MsgIndicator: enum class, stores the header of message.
- socket (folder):
  - Client: class for client (client side)
  - Server: class for main server. Top level logic in this class (E. create ServerThread, countdown, record Item, record username, record auction bid, etc.)
  - ServerThread: class for client (server side), each client correspond to a serverThread. User level logic in this class (E. handle message send from user, check username exists, check bid valid, etc.)
- utils (folder):
  - IOUtils: utils for handle the message which send/receive with InputStream and OutputStream
  - PanelUtils: utils for display the message on the window panel
  - SocketUtils: utils for send group message to clients
  - TableUtils: utils for generate the String message for GUI "table" view

## 3. How to run?
Make sure you have the Java 8 or newer installed.

### 3.1. Windows
- Run with EXE
  1. Open "run" folder.
  2. Double click "AuctionServer.exe".
  3. Double click "AuctionClient.exe".
- Run with BAT
  1. Open "run" folder.
  2. Double click "server.bat".
  3. Double click "client.bat".

You can run client as much as you want.

The BAT file running the JAR in the "out" folder. Because this project contains various packages, running with class is difficult. For me, I can run with class in Mac, but in Windows not working. I test EXE with Java 8, 15, 17, and test BAT with 15, they are working.

### 3.2. MacOS
1. Open Terminal, and navigate to the "run" folder. For example, type in ```cd run``` from assignment folder.
2. Run ```./server.sh```
3. Run ```./client.sh```

You can run client as much as you want.

### 3.3. Command for the app
The input box at the bottom of GUI window. All the command is case sensitive!
- For server window:
  - ```ALLITEM``` to check all items
  - ```ADD Laptop 1000``` to add the new item "Laptop" with price 1000
  - ```QUIT``` to quit the server, and close all the client
- For client window:
  - ```AUCTION``` to enter the auction screen.
  - Input any integer to bid, this will only working when you in the auction screen.
  - ```ALLITEM``` to enter the item list screen.
  - ```ADD Laptop 1000``` to add the new item "Laptop" with price 1000, this will only working when you in the item list screen.
  - ```MENU``` to enter the menu screen, you can check your client command there.
  - ```QUIT``` to quit the client.

## 4. Demo Screenshots
![Screenshot](https://github.com/Theodore-Ho/auction_system/blob/main/img/Screenshot1.png)
![Screenshot](https://github.com/Theodore-Ho/auction_system/blob/main/img/Screenshot2.png)
![Screenshot](https://github.com/Theodore-Ho/auction_system/blob/main/img/Screenshot3.png)
![Screenshot](https://github.com/Theodore-Ho/auction_system/blob/main/img/Screenshot4.png)
![Screenshot](https://github.com/Theodore-Ho/auction_system/blob/main/img/Screenshot5.png)
![Screenshot](https://github.com/Theodore-Ho/auction_system/blob/main/img/Screenshot6.png)
![Screenshot](https://github.com/Theodore-Ho/auction_system/blob/main/img/Screenshot7.png)
![Screenshot](https://github.com/Theodore-Ho/auction_system/blob/main/img/Screenshot8.png)
![Screenshot](https://github.com/Theodore-Ho/auction_system/blob/main/img/Screenshot9.png)
