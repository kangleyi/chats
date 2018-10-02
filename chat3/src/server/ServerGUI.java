package server;

import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import java.awt.Color;

public class ServerGUI extends JFrame {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JPanel jpanel;

	static JList<String> list1;// 在线用户列表
	static JList<String> list2;// IP和端口号列表
	static Vector<String> vc1 = new Vector<String>();// 存放用户名
	static Vector<String> vc2 = new Vector<String>();// 存放IP和端口号

	static JLabel label1;
	static JLabel label2;

	// 服务端界面
	public ServerGUI() throws Exception {
		setFont(new Font("隶书", Font.PLAIN, 20));
		setTitle("\u670D\u52A1\u5668\u5217\u8868");
		setResizable(false);
		this.setSize(400, 407);
		this.setLocationRelativeTo(null);

		jpanel = new JPanel();
		jpanel.setBackground(SystemColor.controlHighlight);
		jpanel.setLayout(null);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setContinuousLayout(true);
		splitPane.setBounds(0, 73, 394, 305);
		splitPane.setDividerLocation(194);
		jpanel.add(splitPane);

		list1 = new JList<String>();
		list1.setForeground(new Color(153, 50, 204));
		list1.setFont(new Font("隶书", Font.PLAIN, 17));
		list1.setBackground(UIManager.getColor("Button.background"));
		list1.setVisibleRowCount(20);

		JScrollPane scrollPane = new JScrollPane(list1);
		splitPane.setLeftComponent(scrollPane);

		list2 = new JList<String>();
		list2.setForeground(new Color(153, 50, 204));
		list2.setFont(new Font("隶书", Font.PLAIN, 17));
		list2.setBackground(UIManager.getColor("Button.background"));
		list2.setVisibleRowCount(20);

		JScrollPane scrollPane_1 = new JScrollPane(list2);
		splitPane.setRightComponent(scrollPane_1);

		label1 = new JLabel("在线用户： " + ServerStart.serverThread.size() + "人");
		label1.setForeground(SystemColor.textHighlight);
		label1.setFont(new Font("隶书", Font.PLAIN, 20));
		label1.setBounds(0, 44, 194, 29);
		jpanel.add(label1);

		label2 = new JLabel("IP\u5730\u5740\u3001\u7AEF\u53E3\u53F7");
		label2.setForeground(SystemColor.textHighlight);
		label2.setFont(new Font("隶书", Font.PLAIN, 20));
		label2.setBounds(199, 44, 195, 29);
		jpanel.add(label2);

		this.getContentPane().add(jpanel);
		this.setVisible(true);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					for(String str:ServerStart.serverThread.keySet()){
						ServerThread st = ServerStart.serverThread.get(str);
						st.out.writeUTF("关闭" + "-1_~" + 1);
						st.out.flush();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
	}
}
