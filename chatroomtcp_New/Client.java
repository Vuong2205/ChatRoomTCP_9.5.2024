package chatroomtcp_New;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
	private static final String SERVER_ADDRESS = "localhost";
	private static final int SERVER_PORT = 12345;

	public static void main(String[] args) {
		try {
			Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
			System.out.println("Connected to the chat server!");

			// Khởi tạo input và output bằng Stream
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// khoi tao luong de xu ly du lieu vao (input messsge)
			// readLine : doc tung dong du lieu

			new Thread(() -> {
				try {
					String serverResponse;
					while ((serverResponse = in.readLine()) != null) {
						System.out.println(serverResponse);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();//thuc thi doan ma trong lambda expression

			// Đọc tin nhắn từ màn hình console và gửi đến server
			Scanner scanner = new Scanner(System.in);
			String userInput;
			while (true) {
				userInput = scanner.nextLine();
				out.println(userInput);
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
