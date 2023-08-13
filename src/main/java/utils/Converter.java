package utils;

import org.json.JSONObject;

public class Converter {

    public static String prettyPrintJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        String formattedRequest = String.format("{\n" +
                        "  \"id\": %s,\n" +
                        "  \"petId\": %s,\n" +
                        "  \"quantity\": %s,\n" +
                        "  \"shipDate\": \"%s\",\n" +
                        "  \"status\": \"%s\",\n" +
                        "  \"complete\": %s\n" +
                        "}", jsonObject.get("id"), jsonObject.get("petId"), jsonObject.get("quantity"),
                jsonObject.get("shipDate"), jsonObject.get("status"), jsonObject.get("complete"));

        return formattedRequest;
    }
}
