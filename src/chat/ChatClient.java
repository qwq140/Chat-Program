package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.Visibility;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.http.WebSocket.Listener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import protocol.Chat;

public class ChatClient extends JFrame {

	private ChatClient chatclient = this;
	private static final String TAG = "ChatClient : ";

	private static final int PORT = 10000;

	private JButton btnConnect, btnSend;
	private JTextField tfHost, tfChat;
	private JTextArea taChatList;
	private ScrollPane scrollPane;
	private JPanel topPanel, bottomPanel;

	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;

	public ChatClient() {
		init();
		setting();
		batch();
		listener();
		setVisible(true);
	}

	private void init() {
		btnConnect = new JButton("connect");
		btnSend = new JButton("������");
		tfHost = new JTextField("127.0.0.1", 20);
		tfChat = new JTextField(20);
		taChatList = new JTextArea(10, 30);
		scrollPane = new ScrollPane();

		topPanel = new JPanel();
		bottomPanel = new JPanel();
	}

	private void setting() {
		setTitle("ä�� �ٴ�� Ŭ���̾�Ʈ");
		setSize(350, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // ����� ���
		taChatList.setForeground(Color.BLUE);
		taChatList.setBackground(Color.ORANGE);
	}

	private void batch() {
		topPanel.add(tfHost);
		topPanel.add(btnConnect);
		bottomPanel.add(tfChat);
		bottomPanel.add(btnSend);
		scrollPane.add(taChatList);

		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	private void listener() {
		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		btnSend.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});
//		this.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(WindowEvent e) {
//				try {
//					FileWriter outFile = new FileWriter("D:\\chatLog.txt"); 
//					outFile.write(taChatList.getText());
//					outFile.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//			}
//		});
	}
	
	private void send() {
		String chat = tfChat.getText();
		//1�� taChatList�� ������
		String gubun[] = chat.split(":");
		if(gubun[0].equals(Chat.ALL)) {
			taChatList.append("[�� �޽���]"+gubun[1]+"\n");
		}
		//2�� ������ ����
		writer.println(chat);
		writer.flush();
		//3�� tfChat ����
		tfChat.setText("");
	}
	
	private void connect() {
		String host = tfHost.getText();
		try {
			socket = new Socket(host, PORT);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			taChatList.append("����Ͻ� ���̵� �Է��ϼ���."+"\n");
			ReaderThread rt = new ReaderThread();
			rt.start();
		} catch (Exception e1) {
			System.out.println(TAG+"���� ���� ����"+e1.getMessage());
		}
	}
	
	class ReaderThread extends Thread{
		@Override
		public void run() {
			String input = null;
			try {
				//�����κ��� ���� �ٸ� Ŭ���̾�Ʈ���� ä�ø޽����� �ڱ��� ä��â�� ���
				while((input = reader.readLine())!=null) {
					taChatList.append(input+"\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new ChatClient();

	}

}
