package chatroomtcp_New;
import java.io.*; 
import java.net.*; 
import java.util.concurrent.CopyOnWriteArrayList; 

public class Server { 
	private static final int PORT = 12345;
	private static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>(); 

	public static void main(String[] args) { 
		try { 
			
			ServerSocket serverSocket = new ServerSocket(PORT); 
			System.out.println("Server is running and waiting for connections.."); 

			//Chấp nhận kết nối đến
			while (true) {
				//Server chấp nhập kết nối qua accept()
				Socket clientSocket = serverSocket.accept(); 
				System.out.println("New client connected: " + clientSocket); 

				//
				ClientHandler clientHandler = new ClientHandler(clientSocket); 
				clients.add(clientHandler); 
				new Thread(clientHandler).start(); // cho phép chạy đồng thời
			} 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
	} 

	// Broadcast
	public static void broadcast(String message, ClientHandler sender) 
	{ 
		for (ClientHandler client : clients) { 
			if (client != sender) { 
				client.sendMessage(message); 
			} 
		} 
	} 

	// Lớp nội bộ để xử lý các kết nối máy khách(Client)
	private static class ClientHandler implements Runnable { 
		private Socket clientSocket; 
		private PrintWriter out; 
		private BufferedReader in; 

		// Constructor 
		public ClientHandler(Socket socket) { 
			this.clientSocket = socket; 

			try { 
				// Tạo luồng đầu vào(input) và đầu ra(output) cho giao tiếp
				out = new PrintWriter(clientSocket.getOutputStream(), true); 
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
			} catch (IOException e) { 
				e.printStackTrace(); 
			} 
		} 


		@Override
		public void run() { 
			try {
				String username = null;
				username = getUsername();
                System.out.println("User " + username + " connected.");

				out.println("Welcome to the chat, " + username + "!");
				out.println("Type Your Message"); 
				String inputLine; 
				boolean lefChat = false;
				// Tiếp tục nhận tin nhắn từ Client
				while ((inputLine = in.readLine()) != null) {
					System.out.println("[" + username + "]: " + inputLine);
					if (inputLine.equalsIgnoreCase("QUIT")) {

    				sendMessage("You has left the chat.");
					lefChat = true;
					clone();
					break;
				}
					if(!lefChat) {
						broadcast("[" + username + "]: " + inputLine, this);
					}
				}

				clients.remove(this);
				// đóng các kết nối
				in.close(); 
				out.close(); 
				clientSocket.close(); 
			} catch (IOException e) { 
				e.printStackTrace(); 
			} catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

		//
		private String getUsername() throws IOException { 
			out.println("Enter your username:"); 
			return in.readLine(); 
		} 

		
		public void sendMessage(String message) { 
			out.println(message); 
			out.println("Type Your Message"); 
		} 
	} 
} 
