package pac;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.lang.InterruptedException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Chat {
    public static void main(String[] args) {
        final int rcvPort = 8080;
        int selectNo = 0;
        Client client = null;
        Server server = null;
        Scanner scan = new Scanner(System.in);

            while (true) {
                try {
                    System.out.print("接続先を指定(1)/接続を待つ(2)/終了(3)>");
                    selectNo = scan.nextInt();
                } catch (NoSuchElementException e) {
                    selectNo = 3;
                    System.out.println("1〜3の数字を入力してください");
                }

                if (3 == selectNo) {
                    System.out.println("チャットを終了します");
                    break;
                } else if (2 == selectNo) {

                    try {
                        //受信用のスレッドを作成する
                        ServerSocket listener = new ServerSocket(rcvPort);
                        Socket socket = listener.accept();
                        server = new Server(socket);
                        server.start();

                        //接続してきたIPを使用して双方向通信用のスレッドを作成する
                        String rcvIp = socket.getInetAddress().getHostAddress();
                        client = new Client(rcvIp, rcvPort);
                        client.start();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else if (1 == selectNo) {
                    System.out.print("接続先IP>");
                    String ip = scan.next();

                    //指定したIPを使用してクライアントを作成する
                    client = new Client(ip, rcvPort);
                    client.start();

                    //接続するIPを使用して双方向通信用のスレッドを作成する
                    server = new Server(rcvPort);
                    server.start();

                }

                try {
                    //スレッドを待機させる
                    client.join();
                    //受信用のスレッドを止める
                    server.stopRunning();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

    }
}