package chat;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Vector;

import protocol.Chat;

public class ChatServer {

	private static final String TAG = "ChatServer : ";
	private ServerSocket serverSocket;
	private Vector<ClientInfo> vc; //Ŭ���̾�Ʈ���� ������ ���� �÷���

	public ChatServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG + "Ŭ���̾�Ʈ �����...");
			while (true) {
				Socket socket = serverSocket.accept();
				ClientInfo clientInfo = new ClientInfo(socket);
				clientInfo.start();
				vc.add(clientInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ClientInfo extends Thread {

		Socket socket;
		String id;
		BufferedReader reader;
		PrintWriter writer;
		FileWriter fout = null;

		public ClientInfo(Socket socket) {
			this.socket = socket;
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
			} catch (Exception e) {
				System.out.println("���� ���� ����" + e.getMessage());
			}

		}

		@Override
		public void run() {
			String input = null;
			try {
				while ((input = reader.readLine()) != null) {
					routing(input);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void routing(String input) {
			String gubun[] = input.split(":");
			//���̵� �Է��� �������� �Է��϶�� �ȳ��� ���
			//���̵� �Է��� ������ ������ �����ϰ��ִ� ��� Ŭ���̾�Ʈ���� ���������� �˸�
			if (id == null) {
				if (gubun[0].equals(Chat.ID)) {
					id = gubun[1];
					for (int i = 0; i < vc.size(); i++) {
						vc.get(i).writer.println(id + "�Բ��� �����ϼ̽��ϴ�.");
						vc.get(i).writer.flush();
					}
				} else {
					writer.println("id�� �Է��Ͻʽÿ�");
					writer.flush();
					return;
				}
			}
			if (gubun[0].equals(Chat.ALL)) {
				try {
					//ä�� �α� ���� ����
					fout = new FileWriter("D:\\log.txt", true);
					fout.append(LocalDateTime.now().withNano(0) + " ["+id+"] " + gubun[1] + "\n");
					fout.close();
					//�ڱ� �ڽ��� ������ Ŭ���̾�Ʈ���� �Է��� �޽��� ���� �˸�
					for (int i = 0; i < vc.size(); i++) {
						if (vc.get(i) != this) {
							vc.get(i).writer.println("[" + id + "]" + gubun[1]);
							vc.get(i).writer.flush();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		new ChatServer();
	}

}
