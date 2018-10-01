package client;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class ClientPrivateChatGUI extends JFrame {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	JTextArea chatText;// 聊天文本框
	JTextArea sendText;// 发送文本框

	private JPanel sendPanel;// 发送面板
	private JPanel chatPanel;// 聊天面板

	private JLabel hisXiu;// 秀_对方
	private JLabel myXiu;// 秀_自己
	private JLabel toolBack;// 工具栏背景
	static JLabel hisName;//对方昵称

	private JButton sendButton;// 发送
	private JButton closeButton;// 接受

	private JSplitPane splitPane1;// 左侧分隔面板
	private JSplitPane splitPane2;// 右侧分隔面板
	private JSplitPane splitPane3;// 总分隔面板

	String name = null;
	Socket socket = null;

	public ClientPrivateChatGUI(Socket socket, String name) throws Exception {
		setResizable(false);
		this.name = name;
		this.socket = socket;
		this.setSize(598, 544);
		this.setLocationRelativeTo(null);

		// 左侧分隔板加入上下分隔板，再加入两个面板
		sendPanel = new JPanel();
		chatPanel = new JPanel();
		splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, chatPanel, sendPanel);
		splitPane1.setDividerLocation(341);
		splitPane1.setDividerSize(20);
		splitPane1.setEnabled(false);

		// 右侧分隔板加入上下分隔板，加入两个标签
		hisXiu = new JLabel();
		myXiu = new JLabel();
		splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, hisXiu, myXiu);
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
		closeButton.setBounds(218, 126, 78, 25);

		sendButton = new JButton("发送");
		sendButton.setFont(new Font("华文行楷", Font.PLAIN, 15));
		sendButton.setBounds(319, 126, 78, 25);

		sendPanel.add(closeButton);
		sendPanel.add(sendButton);

		sendText = new JTextArea();
		sendText.setFont(new Font("楷体", Font.PLAIN, 14));
		sendText.setRows(6);

		JScrollPane scrollPane_1 = new JScrollPane(sendText);
		scrollPane_1.setBounds(0, 0, 397, 126);
		sendPanel.add(scrollPane_1);

		hisName = new JLabel("");
		hisName.setForeground(SystemColor.textHighlight);
		hisName.setFont(new Font("隶书", Font.PLAIN, 20));
		hisName.setBounds(0, 126, 191, 25);
		sendPanel.add(hisName);

		chatText = new JTextArea();
		chatText.setFont(new Font("楷体", Font.PLAIN, 14));
		chatText.setRows(15);
		chatText.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(chatText);
		scrollPane.setBounds(0, 30, 397, 308);
		chatPanel.add(scrollPane);

		// 4Label加入图片
		Image imghis = new ImageIcon("F:/pictures/his.jpg").getImage().getScaledInstance(180, 250,
				Image.SCALE_AREA_AVERAGING);
		hisXiu.setIcon(new ImageIcon(imghis));

		Image imgmy = new ImageIcon("F:/pictures/my.jpg").getImage().getScaledInstance(180, 250,
				Image.SCALE_AREA_AVERAGING);
		myXiu.setIcon(new ImageIcon(imgmy));

		Image imgback = new ImageIcon("F:/pictures/back.png").getImage().getScaledInstance(400, 30,
				Image.SCALE_AREA_AVERAGING);
		toolBack.setIcon(new ImageIcon(imgback));

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
					out.writeUTF("私聊" + "-1_~" + name + "~2/-" + str);
					out.flush();
					sendText.setText("");
					Date date=new Date();
					DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time=format.format(date);
					chatText.append(time + "\n"+"<" + ClientMainGUI.userName + ">" + "：" + "\n");
					chatText.append("        " + str + "\n");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				for (int i = 0; i < ClientMainGUI.priChatFrame.size(); i++) {
					ClientPrivateChatGUI cmg = ClientMainGUI.priChatFrame.get(i);
					if (cmg.name.equals(name)) {
						ClientMainGUI.priChatFrame.remove(i);
					}
				}
				dispose();
			}
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				for (int i = 0; i < ClientMainGUI.priChatFrame.size(); i++) {
					ClientPrivateChatGUI cmg = ClientMainGUI.priChatFrame.get(i);
					if (cmg.name.equals(name)) {
						ClientMainGUI.priChatFrame.remove(i);
					}
				}
				dispose();
			}
		});
	}
}