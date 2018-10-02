package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.AbstractList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ServerStart {

	static Map<String,ServerThread> serverThread = new ConcurrentHashMap<>();

	// 启动服务器端，开始在端口号上监听
	public static void main(String[] args) throws Exception {
		ServerSocket server = new ServerSocket(6000);
		new ServerGUI();
		try {
			while (true) {
				Socket s = server.accept();
				ServerThread st = new ServerThread(s);
				(new Thread(st)).start();
			}
		} finally {
			server.close();
		}
	}
}
