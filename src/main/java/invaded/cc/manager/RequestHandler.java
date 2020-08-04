package invaded.cc.manager;

import com.google.gson.JsonObject;
import invaded.cc.Core;
import invaded.cc.util.ConfigFile;
import invaded.cc.util.ConfigTracker;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.Getter;

import java.io.BufferedReader;
import java.util.Map;

public class RequestHandler {

    @Getter
    private static class RequestConfig {

        private ConfigFile config;

        private String host, token;
        private int port;

        public RequestConfig() {
            this.config = Core.getInstance().getDatabaseConfig();

            ConfigTracker configTracker = new ConfigTracker(config, "http");
            this.host = configTracker.getString("host");
            this.port = configTracker.getInt("port");
            this.token = configTracker.getString("token");
        }
    }

    private static final RequestConfig CONFIG = new RequestConfig();
    private static String BASE = "http://" + CONFIG.getHost() + ":" + CONFIG.getPort();

    public static HttpResponse get(String query){
        HttpRequest httpRequest = HttpRequest.get(BASE + query);

        return httpRequest.send();
    }

    public static HttpResponse post(String endpoint, Map<String, Object> body) {
        HttpRequest req = HttpRequest.post(BASE + endpoint);

        req.contentTypeJson();
        req.form(body);

        return req.send();
    }

    public static HttpResponse put(String endpoint, Map<String, Object> body){
        return HttpRequest.put(BASE + endpoint)
                .body(Core.GSON.toJson(body))
                .send();
    }

}
