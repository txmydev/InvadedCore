package invaded.cc.util.json;


import com.google.gson.JsonObject;

public class JsonChain {

    private JsonObject jsonObject = new JsonObject();

    public JsonChain addProperty(String propertyId, Number object){
        this.jsonObject.addProperty(propertyId, object);
        return this;
    }

    public JsonChain addProperty(String propertyId, String object){
        this.jsonObject.addProperty(propertyId, object);
        return this;
    }

    public JsonChain addProperty(String propertyId, Character object){
        this.jsonObject.addProperty(propertyId, object);
        return this;
    }

    public JsonChain addProperty(String propertyId, Boolean object){
        this.jsonObject.addProperty(propertyId, object);
        return this;
    }

    public JsonChain addAll(JsonObject other){
        other.entrySet().forEach(entry -> {
            this.jsonObject.add(entry.getKey(), entry.getValue());
        });

        return this;
    }

    public JsonObject get(){
        return jsonObject;
    }

}
