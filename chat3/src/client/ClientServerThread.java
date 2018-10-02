package client;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

//启动一个socket线程
public class ClientServerThread implements Runnable {
	ConcurrentHashMap<String,ClientServerThread> threadLocal=new ConcurrentHashMap<>();

	Socket socket = null;
	DataInputStream in;
	DataOutputStream out;
	String target;
	int port;
	boolean isLine;

	public ClientServerThread(Socket s,int port) throws IOException {
		this.socket = s;
		this.port=port;
	}

	public void run() {
		try {
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
			while (true) {
				String str = in.readUTF();
				System.out.println("服务端--->"+str);
				dealWithMsg(str);// 消息处理
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dealWithMsg(String str) throws Exception {
		String[] temp = str.split("-1_~");
		String flag = temp[0];// 获取消息的标志
		String data = temp[1];// 获取消息的真实内容

		switch (flag) {
			case "链接":
				login(flag, data);// 如果是登录
				break;
			case "私聊":
				privateChat(flag, data);// 如果是私聊
				break;
			case "退出":
				serverThreadClose(flag, data);// 如果是退出
				break;
			default:
				break;
		}
	}
	// 登录
	public void login(String flag, String data) throws Exception {
		this.target=data;
		this.isLine=true;
		threadLocal.put(data,this);
		out.writeUTF("链接成功！");
		out.flush();
	}

	// 私聊
	public void privateChat(String flag,String data) throws Exception {
		String[] temp = data.split("~2/-");
		String name = temp[0];
		String msg = temp[1];
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
		if (msg != null && msg.length() != 0) {
			for (int i = 0; i < ClientMainGUI.priChatFrame.size(); i++) {
				ClientPrivateChatGUI cmg = ClientMainGUI.priChatFrame.get(i);
				if (cmg.name.equals(temp[0])) {
					cmg.chatText.append(time+"\n"+"<" + name + ">" + "：" + "\n");
					cmg.chatText.append("        " + msg + "\n");
					return;
				}
			}
			ClientPrivateChatGUI cpcg=new ClientPrivateChatGUI(null,threadLocal.get(name).socket,name);
			ClientMainGUI.priChatFrame.add(cpcg);
			ClientPrivateChatGUI.hisName.setText("对方：" + name);

			cpcg.chatText.append(time+"\n"+"<" + name + ">" + "：" + "\n");
			cpcg.chatText.append("        " + msg + "\n");
		}
	}

	// 关闭服务器端线程
	public void serverThreadClose(String flag, String data)  {
		ClientServerThread st = threadLocal.get(data);
		try {
			st.out.writeUTF("下线" + "-1_~" + data);
			st.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		st.isLine=false;
	}

}
