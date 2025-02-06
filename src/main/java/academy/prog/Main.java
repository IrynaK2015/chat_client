package academy.prog;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String login = "";

		try {
			System.out.println("Enter your login: ");
			login = scanner.nextLine();
			doLogin(login);
			System.out.println("Login successful");

			Thread th = new Thread(new GetThread(login));
			th.setDaemon(true);
			th.start();

            System.out.println("Enter your message: ");
			while (true) {
				String text = scanner.nextLine();
				if (text.isEmpty()) break;

				if (text.equals("/users")) {
					getUserList();
				} else {
					String privateRecipient = "";
					if (text.contains(":")) {
						String[] parts = text.split(":");
						privateRecipient = parts[0];
						text = parts[1];
					}
					Message m = new Message(login, text);
					if (!privateRecipient.isEmpty()) 	m.setTo(privateRecipient);

					int res = m.send(Utils.getURL() + "/add");

					if (res != 200) { // 200 OK
						System.out.println("HTTP error occurred: " + res);
						return;
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			if (!login.isEmpty()) {
				doLogout(login);
				System.out.println("You left chat successfully");
			}
			scanner.close();
		}
	}

	private static void doLogin(String login) {
		try {
			if (login.isEmpty())
				throw new Exception("Login is empty");

			ServerConnector.sendLoginRequest(login);
		} catch (Exception e) {
			throw new RuntimeException("Login error: " + e.getMessage());
		}
	}

	private static void getUserList() throws IOException {
		String strBuf = ServerConnector.getRemoteUserList();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		Type listOfMyClassObject = new TypeToken<ArrayList<String>>() {}.getType();
		List<String> list = gson.fromJson(strBuf, listOfMyClassObject);
		for (Object o : list) {
			System.out.println("\t" + o);
		}
	}


	private static void doLogout(String login) {
		try {
			ServerConnector.sendLogoutRequest("logout?login=" + login);
		} catch (Exception e) {
			throw new RuntimeException("Logout failed: " + e.getMessage());
		}
	}
}
