import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatServer extends Thread {

	static ArrayList<String> UsersList = new ArrayList<String>();
	static ArrayList<String> loginList = new ArrayList<String>();
	static HashMap<String, HashMap<String, ArrayList<String>>> messageList = new HashMap<String, HashMap<String, ArrayList<String>>>();
	Socket s;

	ChatServer(Socket s) {
		System.out
				.println("recievedpacket," + s.getPort() + s.getInetAddress());
		this.s = s;
	}

	public static void main(String[] args) {
		try {
			System.out.println(InetAddress.getLocalHost());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ServerSocket sv = null;
		try {
			sv = new ServerSocket(new Integer(args[0]));
			System.out.println("Started Server at " + args[0] + " ...");
			while (true) {

				new ChatServer(sv.accept()).start();

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		try {
			int byteSize = 65000;
			byte b[] = new byte[byteSize];
			int count;
			InputStream in = s.getInputStream();
			OutputStream Out = s.getOutputStream();
			// while((count = in.read(b))!=0)
			// in.read(b);
			// System.out.println(new String(b, "UTF-8"));

			// int count
			// System.out.println(in.);
			ByteStreamData recievedData = new ByteStreamData();
			while ((count = in.read(b)) > 0) {
				System.out.println(count);
				System.out.println(new String(b, "UTF-8"));

				recievedData.setData(b, count);
				if (count < byteSize - 1) {
					break;
				}
			}
			System.out.println("Recieved message");
			Json jsonFile = new Json(recievedData.getString());

			String queryString = jsonFile.getValue("query");
			if (queryString == null) {
				return;
			}
			System.out.println(queryString);
			if (queryString.equalsIgnoreCase("addusername")) {
				addUserName(jsonFile, Out);
			} else if (queryString.equalsIgnoreCase("login")) {
				login(jsonFile, Out);
			} else if (queryString.equalsIgnoreCase("searchfriend")) {
				searchfriend(jsonFile, Out);
			} else if (queryString.equalsIgnoreCase("sendmessage")) {
				sendMessage(jsonFile, Out);
			} else if (queryString.equalsIgnoreCase("requestmessage")) {
				requestMessage(jsonFile, Out);
			} else if (queryString.equalsIgnoreCase("getnotification")) {
				System.out.println("in notification");
				getNotification(jsonFile, Out);
			} else {
				Json responseMessage = new Json();
				responseMessage.put("query", "error");
				responseMessage.put("errormessage", "notappropriatequery"
						+ queryString);
				try {
					Out.write(responseMessage.getJsonString().getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// Out.write(new String("Hello server response here").getBytes());
			// System.out.println("server here");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void getNotification(Json jsonFile, OutputStream out) {
		String receiverName = jsonFile.getValue("username");
		Json responseMessage = new Json();

		if (messageList.containsKey(receiverName)) {

			HashMap<String, ArrayList<String>> sendersMessageList = messageList
					.get(receiverName);
			if (sendersMessageList.size() > 0) {

				responseMessage.put("query", "notificationexists");
				Iterator it = sendersMessageList.entrySet().iterator();
				int i = 0;
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					responseMessage.put("sender"+i, (String) pair.getKey());
					i++;
				}
			}else {
				responseMessage.put("query", "notificationdoesntexists");
			}
		} else {
			responseMessage.put("query", "notificationdoesntexists");
		}
		System.out.println(responseMessage);
		try {
			out.write(responseMessage.getJsonString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void searchfriend(Json jsonFile, OutputStream out) {
		String userName = jsonFile.getValue("username");
		System.out.println("username serchfriend: " + userName);
		Json responseMessage = new Json();
		if (userName != null) {
			if (UsersList.contains(userName)) {

				// code to reply that no message exists
				responseMessage.put("query", "ok");
			} else {
				responseMessage.put("query", "doesnotexists");
			}
		} else {
			responseMessage.put("query", "usernamenull");
		}
		try {
			out.write(responseMessage.getJsonString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void addUserName(Json jsonFile, OutputStream out) {
		String userName = jsonFile.getValue("username");
		if (userName != null) {
			UsersList.add(userName);
		}
	}

	private void login(Json jsonFile, OutputStream out) {
		String userName = jsonFile.getValue("username");
		Json responseMessage = new Json();
		if (userName != null) {
			if (!UsersList.contains(userName)) {
				UsersList.add(userName);
				// code to reply that no message exists
				responseMessage.put("query", "ok");
			} else {
				responseMessage.put("query", "ok");
			}
		} else {
			responseMessage.put("query", "usernamenull");
		}
		try {
			out.write(responseMessage.getJsonString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendMessage(Json jsonFile, OutputStream out) {
		synchronized (UsersList) {
			String senderName = jsonFile.getValue("username");
			String receiverName = jsonFile.getValue("receivername");
			String message = jsonFile.getValue("message");
			Json responseMessage = new Json();
			System.out.println("username" + senderName + "  receivername"
					+ receiverName + "   message" + message);
			if (senderName != null || receiverName != null) {
				if (UsersList.contains(senderName)
						&& UsersList.contains(receiverName)) {
					if (messageList.containsKey(receiverName)) {
						HashMap<String, ArrayList<String>> sendersMessageList = messageList
								.get(receiverName);
						if (sendersMessageList.containsKey(senderName)) {
							ArrayList<String> senderReceiverMessageList = sendersMessageList
									.get(senderName);
							senderReceiverMessageList.add(message);
						} else {
							ArrayList<String> senderReceiverMessageList = new ArrayList<String>();
							senderReceiverMessageList.add(message);
							sendersMessageList.put(senderName,
									senderReceiverMessageList);
						}
					} else {
						HashMap<String, ArrayList<String>> sendersMessageList = new HashMap<String, ArrayList<String>>();
						ArrayList<String> senderReceiverMessageList = new ArrayList<String>();
						senderReceiverMessageList.add(message);
						sendersMessageList.put(senderName,
								senderReceiverMessageList);
						messageList.put(receiverName, sendersMessageList);
					}
					responseMessage.put("query", "ok");
				} else {
					responseMessage.put("query", "senderorreceiverdidnotlogin");
				}
			} else {
				responseMessage.put("query", "senderorreceivernull");
			}

			try {
				// System.out.println(new
				// String(responseMessage.getJsonString().getBytes(),"UTF-8"));
				out.write(responseMessage.getJsonString().getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			UsersList.notifyAll();
		}

	}

	private void requestMessage(Json jsonFile, OutputStream out) {
		// while (true) {
		Json responseMessage = new Json();
		String receiverName = jsonFile.getValue("username");
		String senderName = jsonFile.getValue("sendername");
		System.out.println("request sendername:" + senderName + "  username:"
				+ receiverName);

		if (UsersList.contains(receiverName) && UsersList.contains(senderName)) {
			if (messageList.containsKey(receiverName)) {
				HashMap<String, ArrayList<String>> sendersMessageList = messageList
						.get(receiverName);
				if (sendersMessageList.containsKey(senderName)) {
					System.out.println("here");
					ArrayList<String> senderReceiverMessageList = sendersMessageList
							.get(senderName);
					// code to add and retrieve json string and send through
					// socket

					responseMessage.put("query", "messageresponse");
					responseMessage.put("username", receiverName);
					responseMessage.put("sendername", receiverName);
					responseMessage.put("message", senderReceiverMessageList);
					sendersMessageList.remove(senderName);

				} else {
					// code to reply that no message exists
					responseMessage.put("query", "noresponse");
				}

			} else {
				// code to reply that no message exists
				responseMessage.put("query", "noresponse");
			}

		}
		try {
			System.out.println("request" + responseMessage);
			System.out.println("request" + responseMessage);
			// String(responseMessage.getJsonString().getBytes(),"UTF-8"));
			out.write(responseMessage.getJsonString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }
	}

}
