import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper for talking to a central REST service.  The service is assumed to
 * expose a simple JSON API for submitting results and fetching a leaderboard.
 * Clients may enable "central mode" in preferences (see UserDatabase) and
 * the app will use this class instead of the local store.
 */
public class RemoteService {
    private static String baseUrl = "http://localhost:8000"; // configurable
    private static HttpClient client = HttpClient.newHttpClient();

    /**
     * Set the base URL of the remote server (e.g. after reading a config).
     */
    public static void setBaseUrl(String url) {
        baseUrl = url;
    }

    /**
     * Submit a game result to the central server.  This method returns
     * true if the request succeeded (status 200).
     */
    public static boolean postResult(String username, String gameCode, boolean win, int timeSeconds) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("username", username);
            payload.put("game", gameCode);
            payload.put("win", win);
            payload.put("time", timeSeconds);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/submit"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            return resp.statusCode() == 200;
        } catch (Exception e) {
            // ignore network errors, we'll fallback to local store
            return false;
        }
    }

    /**
     * Fetch the global leaderboard from the remote service as plain text.
     */
    public static String fetchGlobalLeaderboard() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/leaderboard"))
                    .header("Accept", "text/plain")
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                return resp.body();
            }
        } catch (Exception e) {
            // ignore
        }
        return "(unable to fetch remote leaderboard)";
    }
}
