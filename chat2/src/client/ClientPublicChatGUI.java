package client;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.awt.Font;
import java.awt.Color;
import java.util.Date;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.UIManager;

public class ClientPublicChatGUI extends JFrame {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	JTextArea chatText;// 聊天文本框
	JTextArea sendText;// 发送文本框
	static JTextArea textArea;

	private JPanel sendPanel;// 发送面板
	private JPanel chatPanel;// 聊天面板
	private JPanel memberPanel;// 成员面板
	private JPanel informPanel;// 通知栏

	private JLabel toolBack;// 工具栏背景
	public final JLabel member = new JLabel("群成员");
	public final JLabel inform = new JLabel("群公告");

	private JButton sendButton;// 发送
	private JButton closeButton;// 接受

	private JSplitPane splitPane1;// 左侧分隔面板
	private JSplitPane splitPane2;// 右侧分隔面板
	private JSplitPane splitPane3;// 总分隔面板
	private JScrollPane scrollPane;

	static JList<String> friendList;// 好友列表
	static Vector<String> array = new Vector<String>();
	private JScrollPane scrollPane_2;
	private JScrollPane scrollPane_3;

	public ClientPublicChatGUI(Socket socket) throws Exception {

		setResizable(false);
		this.setSize(598, 540);
		this.setLocationRelativeTo(null);

		// 左侧分隔板加入上下分隔板，再加入两个面板
		sendPanel = new JPanel();
		chatPanel = new JPanel();
		splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, chatPanel, sendPanel);
		splitPane1.setDividerLocation(335);
		splitPane1.setDividerSize(20);
		splitPane1.setEnabled(false);

		// 右侧分隔板加入上下分隔板，加入两个标签
		memberPanel = new JPanel();
		informPanel = new JPanel();
		splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, memberPanel, informPanel);
		splitPane2.setDividerLocation(250);
		splitPane2.setDividerSize(10);
		splitPane2.setEnabled(false);

		// 左右两个分隔板加入
		splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, splitPane1, splitPane2);
		splitPane3.setDividerLocation(400);
		splitPane3.setDividerSize(10);
		splitPane3.setEnabled(false);

		// 各个面板的配置
		// 1加入文本框
		sendPanel.setLayout(null);
		chatPanel.setLayout(null);

		// 2加入工具栏背景Label
		toolBack = new JLabel();
		toolBack.setBounds(0, 0, 397, 30);
		chatPanel.add(toolBack);

		// 3加入按钮
		closeButton = new JButton("关闭");
		closeButton.setFont(new Font("华文行楷", Font.PLAIN, 15));
		closeButton.setBounds(224, 128, 78, 25);

		sendButton = new JButton("发送");
		sendButton.setFont(new Font("华文行楷", Font.PLAIN, 15));
		sendButton.setBounds(319, 128, 78, 25);

		sendPanel.add(closeButton);
		sendPanel.add(sendButton);

		sendText = new JTextArea();
		sendText.setFont(new Font("楷体", Font.PLAIN, 14));
		sendText.setRows(5);

		scrollPane_3 = new JScrollPane(sendText);
		scrollPane_3.setBounds(0, 0, 397, 126);
		sendPanel.add(scrollPane_3);

		// 4Label加入图片
		Image imgback = new ImageIcon("F:/pictures/back.png").getImage().getScaledInstance(400, 30,
				Image.SCALE_AREA_AVERAGING);
		toolBack.setIcon(new ImageIcon(imgback));

		chatText = new JTextArea();
		chatText.setFont(new Font("楷体", Font.PLAIN, 14));
		chatText.setRows(12);
		chatText.setBounds(0, 30, 397, 314);
		chatText.setEditable(false);

		scrollPane_2 = new JScrollPane(chatText);
		scrollPane_2.setBounds(0, 30, 397, 304);
		chatPanel.add(scrollPane_2);

		memberPanel.setLayout(null);
		informPanel.setLayout(null);
		member.setFont(new Font("华文隶书", Font.PLAIN, 18));
		member.setBounds(0, 0, 90, 25);
		inform.setFont(new Font("华文隶书", Font.PLAIN, 18));
		inform.setBounds(0, 0, 90, 25);
		memberPanel.add(member);
		informPanel.add(inform);

		friendList = new JList<String>();
		friendList.setForeground(new Color(153, 0, 255));
		friendList.setFont(new Font("楷体", Font.PLAIN, 20));
		friendList.setBackground(UIManager.getColor("Button.background"));
		friendList.setVisibleRowCount(14);

		scrollPane = new JScrollPane(friendList);
		scrollPane.setBounds(0, 24, 179, 225);
		memberPanel.add(scrollPane);

		textArea = new JTextArea();
		textArea.setFont(new Font("楷体", Font.PLAIN, 18));
		textArea.setForeground(new Color(51, 102, 255));
		textArea.setRows(10);
		textArea.setBackground(UIManager.getColor("Button.background"));

		JScrollPane scrollPane_1 = new JScrollPane(textArea);
		scrollPane_1.setBounds(0, 22, 179, 226);
		informPanel.add(scrollPane_1);

		this.getContentPane().add(splitPane3);
		this.setVisible(true);

		// 添加事件
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					String str = sendText.getText();
					if (str.trim().length() == 0)
						return;
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					out.writeUTF("群聊" + "-1_~" + str);
					out.flush();
					sendText.setText("");
					Date date = new Date();
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = format.format(date);
					chatText.append(time + "\n" + "<" + ClientMainGUI.userName + ">" + "：" + "\n");
					chatText.append("        " + str + "\n");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ClientMainGUI.pubChat.setEnabled(true);
				ClientMainGUI.pubChatFrame = null;
				dispose();
			}
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ClientMainGUI.pubChat.setEnabled(true);
				ClientMainGUI.pubChatFrame = null;
				dispose();
			}
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				friendList.setListData(ClientMainGUI.array);
			}
		});
	}
}
