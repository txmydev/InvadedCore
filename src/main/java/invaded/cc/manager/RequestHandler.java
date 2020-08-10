package invaded.cc.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import invaded.cc.Core;
import invaded.cc.util.ConfigFile;
import invaded.cc.util.ConfigTracker;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.Getter;

import java.util.List;
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

    public static HttpResponse post(String endpoint, String data) {
        HttpRequest req = HttpRequest.post(BASE + endpoint);

        req.contentTypeJson();
        req.body(data);

        return req.send();
    }

    public static HttpResponse post(String endpoint, Map<String, Object> body) {
        HttpRequest req = HttpRequest.post(BASE + endpoint);
        req.contentTypeJson();

        JsonObject jsonObject = new JsonObject();

        for (Map.Entry<String, Object> e : body.entrySet()) {
            if(e.getValue() instanceof List) {
                JsonArray array = new JsonArray();
                ((List<String>) e.getValue()).forEach(array::add);
                jsonObject.add(e.getKey(), array);
                continue;
            }

            Object v = e.getValue();
            if(v instanceof Boolean) jsonObject.addProperty(e.getKey(), (boolean) e.getValue());
            else if(v instanceof Character) jsonObject.addProperty(e.getKey(), (Character) e.getValue());
            else if(v instanceof Number) jsonObject.addProperty(e.getKey(), (Number) e.getValue());
            else if(v instanceof String) jsonObject.addProperty(e.getKey(), (String) e.getValue());
            else throw new IllegalArgumentException("Cannot parse type " + e.getValue().getClass().getName());
        }

        req.body(jsonObject.toString());
        return req.send();
    }

    public static HttpResponse get(String endpoint, Map<String, Object> query) {
        HttpRequest request = HttpRequest.get(BASE + endpoint);
        query.forEach(request::query);

        return request.send();
    }

    public static HttpResponse put(String endpoint, Map<String, Object> body){
        return HttpRequest.put(BASE + endpoint)
                .body(Core.GSON.toJson(body))
                .send();
    }

    public static HttpResponse delete(String endpoint, Map<String, Object> query) {
        HttpRequest request = HttpRequest.delete(BASE + endpoint);
        if(query != null) query.forEach(request::query);

        return request.send();
    }

}
