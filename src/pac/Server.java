package pac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

//スレッドのすべてをコントロールするさいにはThreadを継承するほうがいいのかな
public class Server extends Thread {
    private boolean running = true;
    private Socket socket = null;
    private int port = 0;

    private ServerSocket listener = null;

    private Client client = null;

    //クライアントの接続を受けてインスタンス生成する際のコンストラクタ
    Server(Socket socket){
        this.socket = socket;
    }

    //接続先を指定してインスタンス生成する際のコンストラクタ
    Server(int port) { this.port = port;}

    //    スレッドを止める関数
    public void stopRunning() {
        this.running = false;
    }

    @Override
    public void run() {

            System.out.println("相手からの接続を待ちます");

            //クライアントが未接続の場合
            if(this.socket == null) {
                //通信用のソケットを作成してクライアントの接続を待機する
                try {
                    this.listener = new ServerSocket(this.port);
                    this.socket = this.listener.accept();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            //接続してきたIPアドレスを取得する
            String rcvIp = this.socket.getInetAddress().getHostAddress();
            System.out.println(rcvIp + "から接続されました");


            //メッセージを取得する
            try (InputStreamReader streamReader = new InputStreamReader(this.socket.getInputStream());
                 BufferedReader reader = new BufferedReader(streamReader);) {

                //メッセージ受信→出力のループを行う
                while (this.running) {
                    String message = reader.readLine();
                    if (message.contains(">bye") || message == null) {
                        //メッセージ受信用のスレッドを停止する
                        this.stopRunning();
                    }
                    System.out.println(message);
                    Thread.sleep(100);
                }

                //後処理
                this.socket.close();
                this.listener.close();
            } catch (NullPointerException e) {
                System.out.println("切断されました");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
}