package academy.prog;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ServerConnector {
    public static void sendLoginRequest(String login) throws IOException {
        sendGetWithoutResponse("login?login=" + login);
    }

    public static void sendLogoutRequest(String login) throws IOException {
        sendGetWithoutResponse("logout?login=" + login);
    }

    public static String getRemoteUserList() throws IOException {
        URL url = new URL(Utils.getURL() + "/users");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try (InputStream is = conn.getInputStream()) {
            byte[] buf = Utils.responseBodyToArray(is);

            return new String(buf, StandardCharsets.UTF_8);
        }
    }

    private static void sendGetWithoutResponse(String queryString) throws IOException {
        URL obj = new URL(Utils.getURL() + "/" + queryString);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if (responseCode > 200) {
            throw new RuntimeException("HTTP code " + responseCode);
        }
    }
}
