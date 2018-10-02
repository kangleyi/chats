package client;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class ClientMainGUI extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	static ArrayList<ClientPrivateChatGUI> priChatFrame = new ArrayList<ClientPrivateChatGUI>();
	static ClientPublicChatGUI pubChatFrame = null;

	static JList<String> friendList;// 好友列表
	static Vector<String> array = new Vector<String>();
	static String userName;
	private JLabel icon;// 头像
	private JLabel backGround;// 背景图片
	JLabel UserName;// 用户名

	private JScrollPane friendScrollPane;// 好友滚动面板
	private JPanel friendPanel;// 好友面板
	private JPanel inforPanel;// 信息面板

	static JButton pubChat;// 群聊按钮
	static JButton fresh;
	private JSplitPane splitPane;// 分隔面板
	private JTextField textField;

	public ClientMainGUI(Socket socket, String name) throws Exception {

		userName = name;
		this.setSize(278, 595);
		this.setLocationRelativeTo(null);
		setResizable(false);

		inforPanel = new JPanel();
		inforPanel.setLayout(null);
		friendPanel = new JPanel();
		friendPanel.setLayout(null);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, inforPanel, friendPanel);
		splitPane.setDividerLocation(118);
		splitPane.setDividerSize(20);
		splitPane.setEnabled(false);

		// 添加背景
		backGround = new JLabel();
		backGround.setBackground(Color.MAGENTA);
		backGround.setBounds(0, 0, 271, 117);
		Image imgback = new ImageIcon("F:/pictures/main.jpg").getImage().getScaledInstance(282, 139,
				Image.SCALE_AREA_AVERAGING);
		backGround.setIcon(new ImageIcon(imgback));
		inforPanel.add(backGround);
		Image imgicon = new ImageIcon("F:/pictures/icon2.png").getImage().getScaledInstance(96, 96,
				Image.SCALE_AREA_AVERAGING);

		// 添加图像
		icon = new JLabel();
		backGround.add(icon);
		icon.setBounds(10, 10, 96, 96);
		icon.setIcon(new ImageIcon(imgicon));

		// 添加用户名
		UserName = new JLabel(userName);
		UserName.setForeground(new Color(153, 0, 255));
		backGround.add(UserName);
		UserName.setBackground(UIManager.getColor("Button.background"));
		UserName.setFont(new Font("华文隶书", Font.PLAIN, 20));
		UserName.setBounds(116, 56, 155, 24);

		// 添加按钮
		pubChat = new JButton("群聊");
		pubChat.setBackground(UIManager.getColor("Button.background"));
		pubChat.setFont(new Font("华文行楷", Font.PLAIN, 20));
		pubChat.setBounds(194, 0, 77, 23);
		//friendPanel.add(pubChat);

		// 添加列表，滚动面板
		friendList = new JList<String>();
		friendList.setFont(new Font("华文行楷", Font.PLAIN, 25));
		friendList.setBackground(UIManager.getColor("Button.background"));
		friendList.setBorder(new LineBorder(UIManager.getColor("Button.shadow"), 5));
		friendList.setForeground(new Color(153, 102, 255));

		friendScrollPane = new JScrollPane(friendList);
		friendScrollPane.setToolTipText("");
		friendScrollPane.setViewportBorder(UIManager.getBorder("Button.border"));
		friendScrollPane.setBounds(0, 43, 271, 379);
		friendPanel.add(friendScrollPane);

		textField = new JTextField();
		textField.setFont(new Font("华文隶书", Font.PLAIN, 20));
		textField.setText("\u5728\u7EBF\u5217\u8868");
		textField.setBackground(UIManager.getColor("Button.background"));
		textField.setForeground(UIManager.getColor("Button.foreground"));
		textField.setBounds(0, 20, 98, 23);
		textField.setEditable(false);
		textField.setColumns(10);
		friendPanel.add(textField);

		this.getContentPane().add(splitPane);
		this.setVisible(true);

		pubChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if ("群聊".equals(cmd)) {
					try {
						pubChatFrame = new ClientPublicChatGUI(socket);
						ClientPublicChatGUI.friendList.setListData(ClientMainGUI.array);
						ClientPublicChatGUI.textArea
								.setText("当前在线人数：" + (ClientMainGUI.array.size() + 1) + "人" + "\n" + "我：  " + name);
						pubChat.setEnabled(false);
					} catch (Exception e1) {
						try {
							socket.close();
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						e1.printStackTrace();
					}
				}
			}
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					out.writeUTF("退出" + "-1_~" + userName);
					out.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				ClientMainGUI.friendList.setListData(ClientMainGUI.array);
			}
		});

		friendList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getSource() == friendList)
					if (e.getClickCount() == 2) {
						try {
							int index = friendList.getSelectedIndex();
							String str = array.get(index);
							for (int i = 0; i < ClientMainGUI.priChatFrame.size(); i++) {
								ClientPrivateChatGUI cmg = ClientMainGUI.priChatFrame.get(i);
								if (cmg.name.equals(str)) {
									return;
								}
							}
							Socket socketClient = new Socket();
							String[] attr=str.split("-/")[1].split(":");
							SocketAddress sAddr = new InetSocketAddress(attr[0],Integer.valueOf(attr[1]));
							try{
								socketClient.connect(sAddr, 8000);
								DataOutputStream out=new DataOutputStream(socketClient.getOutputStream());
								out.writeUTF("链接-1_~" + str);
								out.flush();
								String rst = new DataInputStream(socketClient.getInputStream()).readUTF();
								if(rst.contains("链接成功")){
									new Thread(new ClientSender(socketClient, str)).start();
								}else{
									str+="（链接失败）";
								}
								priChatFrame.add(new ClientPrivateChatGUI(socket,socketClient, str));
								ClientPrivateChatGUI.hisName.setText("对方：" + str);
							}catch (ConnectException cs){
								priChatFrame.add(new ClientPrivateChatGUI(socket,socketClient, str+"(不在线)"));
								ClientPrivateChatGUI.hisName.setText("对方：" + str+"(不在线)");
							}
						} catch (Exception e1) {
							try {
								socket.close();
							} catch (IOException e2) {
								e2.printStackTrace();
							}
							e1.printStackTrace();
						}
					}
			}
		});
	}
}
