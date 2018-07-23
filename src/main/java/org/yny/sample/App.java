package org.yny.sample;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class App {
    public static void main(String[] args) {
        String instanceGuid = System.getenv("INSTANCE_GUID");
        if (instanceGuid == null) {
            instanceGuid = "<no INSTANCE_GUID set in env>";
        }

        String tcpPortString = System.getenv("TCP_PORT");
        int tcpPort = tcpPortString == null ? 8080 : Integer.parseInt(tcpPortString);

        listenAndFork(tcpPort, instanceGuid);
    }

    private static void listenAndFork(int tcpPort, String instanceGuid) {
        try {
            ServerSocket welcomeSocket = new ServerSocket(tcpPort);
            while(true) {
                Socket socket = welcomeSocket.accept();
                new Thread(new ReadLogReply(instanceGuid, socket)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException("no workie", e);
        }
    }

    private static class ReadLogReply implements Runnable {
        private final String instanceGuid;
        private final Socket connectionSocket;

        public ReadLogReply(String instanceGuid, Socket connectionSocket) {
            this.instanceGuid = instanceGuid;
            this.connectionSocket = connectionSocket;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[2048];
            int read = readSafe(connectionSocket, buffer);
            while (read > -1) {
                String clientSentence = new String(buffer, 0, read);
                System.out.println("Received: " + clientSentence);

                String response = "Pong [" + clientSentence + "] from INSTANCE_GUID [" + instanceGuid + "]\n";
                writeSafe(connectionSocket, response);
                read = readSafe(connectionSocket, buffer);
            }
        }

        private static int readSafe(Socket connectionSocket, byte[] buffer) {
            try {
                InputStream inputStream = connectionSocket.getInputStream();
                return inputStream.read(buffer);
            } catch (IOException e) {
                //kind'a log
                e.printStackTrace();
                return 0; //nothing happened, nothing read
            }
        }

        private static void writeSafe(Socket connectionSocket, String response) {
            try {
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                outToClient.writeBytes(response);
            } catch (IOException e) {
                //kind'a log
                e.printStackTrace();
            }
        }

    }
}
