package utb.fai;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TelnetClient {

    private String serverIp;
    private int port;
    private Socket socket;
    private InputStream input;
    private OutputStream output;


    public TelnetClient(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

    public void run() {
        try {
            socket = new Socket(serverIp, port);
            input = socket.getInputStream();
            output = socket.getOutputStream();

            // Thread for receiving
            Thread receiveThread = new Thread(() -> {
                try {
                    byte[] response = new byte[1024];
                    int len;
                    while (!socket.isClosed()) {
                        if (input.available() > 0) {
                            len = input.read(response);
                            System.out.write(response, 0, len);
                        }
                        Thread.sleep(20);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });

            // Thread for sending
            Thread sendThread = new Thread(() -> {
                try (Scanner scanner = new Scanner(System.in)) { // Scanner for listening to console input
                    String userInput;
                    while (!socket.isClosed()) {
                        userInput = scanner.nextLine(); // waits for user to type a line and press enter
                        if (userInput.equals("/QUIT")) {
                            socket.close();
                            System.out.println("Connection closed");
                            break;
                        }
                        output.write((userInput + "\n").getBytes());
                        output.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // start both threads
            receiveThread.start();
            sendThread.start();

            // wait for running threads to finish their job
            receiveThread.join();
            sendThread.join();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally { // whatever happens close the socket if not closed already
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    System.out.println("closed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
