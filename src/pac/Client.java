package pac;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class  Client extends Thread {
    private boolean running = true;
    private String ip = "";
    private int port = 0;
    private InetAddress clientIp = null;

    private Server server = null;

//    スレッドを止める関数
    public void stopRunning() {
        this.running = false;
    }

    //サーバーに接続するための情報を設定するためのコンストラクタ
    Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            //自分自身のIPを設定する
            this.clientIp = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("IPアドレスの取得に失敗しました");
            System.exit(-1);
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("相手への接続を試みます");

            //IP、PORTで接続をリクエストする
            InetSocketAddress endPoint = new InetSocketAddress(this.ip, this.port);
            Socket socket = new Socket();
            socket.connect(endPoint);

            try (InputStreamReader streamReader = new InputStreamReader(System.in);
                BufferedReader input = new BufferedReader(streamReader);
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true); )
            {
                System.out.println(this.ip + "に接続しました");
                System.out.println("メッセージ送りましょう(bye -> 切断)");

                //メッセージの入力待ち→送信のループを行う
                while (this.running) {
                    String message = input.readLine();
                    //空白および「>」以外なら、自分のIPと入力文字を送信する
                    if (!(message.equals("") && message.contains(">"))) {
                        output.println(this.clientIp + ">" + message);
                        if (message.equals("bye")) {
                            //メッセージ送信用のスレッドを停止する
                            this.stopRunning();
                        }
                    }
                }

                //後処理
                socket.close();
            } catch (NullPointerException e) {
                System.out.print("接続が切れました");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}