package invaded.cc.core.manager;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.ConfigFile;
import invaded.cc.core.util.ConfigTracker;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.Getter;
import invaded.cc.common.library.gson.JsonArray;
import invaded.cc.common.library.gson.JsonObject;
import invaded.cc.common.library.gson.JsonPrimitive;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RequestHandler {

    private static final RequestConfig CONFIG = new RequestConfig();
    private static String BASE = "http://" + (CONFIG.isDomain() ? CONFIG.getHost() : (CONFIG.getHost() + ":" + CONFIG.getPort()));

    public static HttpResponse get(String query) {
        HttpRequest httpRequest = HttpRequest.get(BASE + query);
        httpRequest.tokenAuthentication(CONFIG.getToken());

        return httpRequest.send();
    }

    public static HttpResponse post(String endpoint, String data) {
        HttpRequest req = HttpRequest.post(BASE + endpoint);

        req.contentTypeJson();
        req.bodyText(data);
        req.tokenAuthentication(CONFIG.getToken());

        return req.send();
    }

    public static HttpResponse post(String endpoint, Map<String, Object> body){
        return post(endpoint, body, Collections.emptyMap());
    }

    public static HttpResponse post(String endpoint, Map<String, Object> body, Map<String, Object> query) {
        HttpRequest req = HttpRequest.post(BASE + endpoint);
        req.tokenAuthentication(CONFIG.getToken());
        req.contentTypeJson();
        query.forEach(req::query);

        JsonObject jsonObject = new JsonObject();

        for (Map.Entry<String, Object> e : body.entrySet()) {
            if (e.getValue() instanceof List) {
                JsonArray array = new JsonArray();
                ((List<String>) e.getValue()).forEach(val -> array.add(new JsonPrimitive(val)));
                jsonObject.add(e.getKey(), array);
                continue;
            }

            Object v = e.getValue();
            if (v instanceof Boolean) jsonObject.addProperty(e.getKey(), (boolean) e.getValue());
            else if (v instanceof Character) jsonObject.addProperty(e.getKey(), (Character) e.getValue());
            else if (v instanceof Number) jsonObject.addProperty(e.getKey(), (Number) e.getValue());
            else if (v instanceof String) jsonObject.addProperty(e.getKey(), (String) e.getValue());
            else if(v == null) continue;
            else throw new IllegalArgumentException("Cannot parse type " + e.getValue().getClass().getName());
        }

        req.body(jsonObject.toString());
        return req.send();
    }

    public static HttpResponse get(String endpoint, Map<String, Object> query) {
        HttpRequest request = HttpRequest.get(BASE + endpoint);
        query.forEach(request::query);
        request.tokenAuthentication(CONFIG.getToken());

        return request.send();
    }

    public static HttpResponse put(String endpoint, Map<String, Object> body, Map<String, Object> query) {
        HttpRequest request = HttpRequest.put(BASE + endpoint)
                .body(Spotify.GSON.toJson(body))
                .tokenAuthentication(CONFIG.getToken());

        query.forEach((str, obj) -> request.query(str, obj.toString()));

        return request.send();
    }

    public static HttpResponse delete(String endpoint, Map<String, Object> query) {
        HttpRequest request = HttpRequest.delete(BASE + endpoint);
        request.tokenAuthentication(CONFIG.getToken());

        if (query != null) query.forEach(request::query);

        return request.send();
    }

    @Getter
    private static class RequestConfig {

        private ConfigFile config;

        private String host, token;
        private int port;
        private boolean domain;

        public RequestConfig() {
            this.config = Spotify.getInstance().getDatabaseConfig();

            ConfigTracker configTracker = new ConfigTracker(config, "http");
            this.host = configTracker.getString("host");
            this.port = configTracker.getInt("port");
            this.token = configTracker.getString("token");
            this.domain = configTracker.getBoolean("domain");
        }
    }

}
