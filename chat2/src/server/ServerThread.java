package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Iterator;

//启动一个socket线程
public class ServerThread implements Runnable {
	Socket socket = null;
	DataInputStream in;
	DataOutputStream out;

	String userName;
	private String userPassword;

	// 数据库操作的引用
	String sql1 = "Select*from client";
	String sql2 = "insert into client values (?,?)";
	Connection con = null;
	Statement stmt = null;
	PreparedStatement ps = null;
	ResultSet rs = null;

	public ServerThread(Socket s) throws IOException {
		this.socket = s;
	}

	public void run() {
		try {
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
			while (true) {
				String str = in.readUTF();
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
			case "登录":
				login(flag, data);// 如果是登录
				break;
			case "注册":
				register(flag, data);// 如果是注册
				break;
			case "群聊":
				publicChat(flag, data);// 如果是群聊
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
		connectDatabase();

		String[] temp = data.split("~2/-");// 提取用户名和密码
		this.userName = temp[0];
		this.userPassword = temp[1];

		while (rs.next()) { // 检查用户是否存在
			if (rs.getString(1).equals(this.userName)) {
				if (rs.getString(2).equals(this.userPassword)) { // 密码输入正确
					Iterator<ServerThread> it = ServerStart.serverThread.iterator();
					while (it.hasNext()) {// 检查是否重复登陆
						ServerThread st = it.next();
						if (st.userName.equals(this.userName) && st.userPassword.equals(this.userPassword)) {
							ServerStart.serverThread.remove(st);
							ServerStart.serverThread.add(this);
						}
					}
					if (it.hasNext() == false) {// 可以登录
						ServerStart.serverThread.add(this);
						// 更新服务器列表
						ServerGUI.vc1.add(this.userName);
						String str = this.socket.getInetAddress() + ": " + this.socket.getPort();
						ServerGUI.vc2.add(str);

						ServerGUI.list1.setListData(ServerGUI.vc1);
						ServerGUI.list2.setListData(ServerGUI.vc2);
						ServerGUI.label1.setText("在线用户： " + ServerStart.serverThread.size() + "人");

						out.writeUTF("登录成功！");
						out.flush();

						// 向所有在线成员发消息
						for (int i = 0; i < ServerStart.serverThread.size(); i++) {
							ServerThread st = ServerStart.serverThread.get(i);
							if (this.userName != st.userName) {
								st.out.writeUTF("上线" + "-1_~" + this.userName);
								st.out.flush();
								this.out.writeUTF("上线" + "-1_~" + st.userName);
								this.out.flush();
							}
						}
						return;
					}
				} else {
					out.writeUTF("密码错误！");
					out.flush();
					return;
				}
			}
		}
		out.writeUTF("用户不存在！");
		out.flush();

		disconnectDatabase();

	}

	// 注册
	public void register(String falg, String data) throws Exception {
		connectDatabase();// 连接数据库

		String[] temp = data.split("~2/-");// 提取用户名和密码

		while (rs.next()) {
			if (rs.getString(1).equals(temp[0])) {
				this.out.writeUTF("用户已存在！");
				this.out.flush();
				return;
			}
		}
		// 写入数据库
		ps = con.prepareStatement(sql2);
		ps.setString(1, temp[0]);
		ps.setString(2, temp[1]);
		ps.executeUpdate();
		this.out.writeUTF("注册成功！");
		this.out.flush();

		disconnectDatabase();
	}

	// 群聊
	public void publicChat(String flag, String data) throws Exception {
		if (data != null && data.length() != 0) {
			for (int i = 0; i < ServerStart.serverThread.size(); i++) {
				ServerThread st = ServerStart.serverThread.get(i);
				if (this.userName != st.userName) {
					st.out.writeUTF(flag + "-1_~" + this.userName + "~2/-" + data);
					st.out.flush();
				}
			}
		}
	}

	// 私聊
	public void privateChat(String flag, String data) throws IOException {
		String[] temp = data.split("~2/-");

		if (temp[1] != null && temp[1].length() != 0) {
			for (int i = 0; i < ServerStart.serverThread.size(); i++) {
				ServerThread st = ServerStart.serverThread.get(i);
				if (temp[0].equals(st.userName)) {
					if(!st.socket.isOutputShutdown()){
						st.out.writeUTF(flag + "-1_~" + this.userName + "~2/-" + temp[1]);
						st.out.flush();
					}else{
						new Chart(st.userName,temp[1]).add();
					}
				}
			}
		}
	}

	// 关闭服务器端线程
	public void serverThreadClose(String flag, String data) throws IOException {
		for (int i = 0; i < ServerStart.serverThread.size(); i++) {
			ServerThread st = ServerStart.serverThread.get(i);
			if (data.equals(st.userName)) {
				//ServerStart.serverThread.remove(i);
				// 更新服务器列表
				ServerGUI.vc1.remove(st.userName);
				String str = st.socket.getInetAddress() + ": " + st.socket.getPort();
				ServerGUI.vc2.remove(str);
			}
			st.out.writeUTF("下线" + "-1_~" + data);
			st.out.flush();
		}
		ServerGUI.list1.setListData(ServerGUI.vc1);
		ServerGUI.list2.setListData(ServerGUI.vc2);
		ServerGUI.label1.setText("在线用户： " + ServerStart.serverThread.size() + "人");
	}

	// 连接数据库
	public void connectDatabase() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chart", "root", "");
		stmt = con.createStatement();
		rs = stmt.executeQuery(sql1);
	}

	// 断开数据库
	public void disconnectDatabase() throws SQLException {
		con.close();
		stmt.close();
		rs.close();
		ps.close();
	}
}
