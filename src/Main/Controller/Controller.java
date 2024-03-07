package Main.Controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Controller {

    public static void main(String[] args) {
        int port = 8080; // 서버 포트 설정

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("HTTP Echo Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // 클라이언트 연결 대기
                System.out.println("Client connected: " + clientSocket);

                // 클라이언트 요청 읽기
                Scanner scanner = new Scanner(clientSocket.getInputStream());
                StringBuilder requestBuilder = new StringBuilder();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.isEmpty()) break; // 빈 줄을 만나면 요청의 끝으로 판단
                    requestBuilder.append(line).append("\r\n");
                }
                String request = requestBuilder.toString();
                System.out.println("Received request:\n" + request);

                // 클라이언트에게 요청을 그대로 응답
                String response = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "\r\n" +
                        request; // 요청을 그대로 응답
                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write(response.getBytes());

                // 클라이언트 연결 종료
                clientSocket.close();
                System.out.println("Client disconnected: " + clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
