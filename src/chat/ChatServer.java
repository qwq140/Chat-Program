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
	private Vector<ClientInfo> vc; //클라이언트들의 정보를 담을 컬렉션

	public ChatServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG + "클라이언트 대기중...");
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
				System.out.println("서버 연결 실패" + e.getMessage());
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
			//아이디 입력을 안했으면 입력하라는 안내문 출력
			//아이디 입력을 했으면 서버에 접속하고있는 모든 클라이언트에게 접속했음을 알림
			if (id == null) {
				if (gubun[0].equals(Chat.ID)) {
					id = gubun[1];
					for (int i = 0; i < vc.size(); i++) {
						vc.get(i).writer.println(id + "님께서 접속하셨습니다.");
						vc.get(i).writer.flush();
					}
				} else {
					writer.println("id를 입력하십시오");
					writer.flush();
					return;
				}
			}
			if (gubun[0].equals(Chat.ALL)) {
				try {
					//채팅 로그 파일 저장
					fout = new FileWriter("D:\\log.txt", true);
					fout.append(LocalDateTime.now().withNano(0) + " ["+id+"] " + gubun[1] + "\n");
					fout.close();
					//자기 자신을 제외한 클라이언트에게 입력한 메시지 내용 알림
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
