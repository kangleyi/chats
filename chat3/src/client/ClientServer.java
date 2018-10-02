package client;

import javax.swing.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientServer implements Runnable{
    ServerSocket server=null;

    int port;

    public ClientServer(int port,Socket socket,String username) {
        this.port=port;
        try {
            server = new ServerSocket(port);
        } catch (IOException e1) {
            try {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                JOptionPane.showMessageDialog(null, "本地端口启动异常！", "提示", JOptionPane.PLAIN_MESSAGE);
                out.writeUTF("退出" + "-1_~" + username);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            e1.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            Socket s;
            try {
                while (true) {
                    s = server.accept();
                    ClientServerThread st = new ClientServerThread(s, Integer.valueOf(port));
                    (new Thread(st)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    assert server != null;
                    server.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
