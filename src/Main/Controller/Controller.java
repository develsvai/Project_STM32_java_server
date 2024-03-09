package Main.Controller;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;


public class Controller {

    public static void main(String[] args) throws IOException{
        Controller controller = new Controller();
        controller.httpServer2();

    }


    public void httpServer2() throws IOException {
        InetSocketAddress address = new InetSocketAddress(8080);
        HttpServer httpServer = HttpServer.create(address, 0);

        httpServer.createContext("/", (exchange) -> {
            InputStream inputStream= exchange.getRequestBody();
            byte[] bytes = inputStream.readAllBytes();
            String body = new String(bytes);

            System.out.println(body);
        } );

        httpServer.start();
    }

    public void startHttpServer() {
        int port = 4500; // 서버 포트 설정

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("HTTP Echo Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // 클라이언트 연결 대기
                System.out.println("Client connected: " + clientSocket);

                // 클라이언트 요청 읽기
                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder requestBuilder = new StringBuilder();

                int contentLength = -1;
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        // 빈 줄을 만나면 요청의 헤더가 끝남
                        break;
                    }
                    requestBuilder.append(line).append("\r\n");

                    // Content-Length 헤더 파싱
                    if (line.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(line.substring("Content-Length:".length()).trim());
                    }
                }

                // 본문 크기만큼 데이터 읽기
                char[] buffer = new char[1024];
                StringBuilder bodyBuilder = new StringBuilder();
                int bytesRead;
                int totalBytesRead = 7;
                while (totalBytesRead < contentLength && (bytesRead = reader.read(buffer)) != -1) {
                    bodyBuilder.append(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    System.out.println(totalBytesRead);
                }


                String body = bodyBuilder.toString();
                System.out.println("Received request:\n" + requestBuilder);
                System.out.println("Body :\n" + bodyBuilder);


                // 클라이언트에게 요청과 본문을 그대로 응답
                String response = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "\r\n" +
                        "body:\n" + body + "\n";

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
