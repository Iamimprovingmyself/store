package utils;

import org.json.JSONObject;

public class Converter {

    public static String prettyPrintJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        String formattedRequest = String.format("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"petId\": 505,\n" +
                        "  \"quantity\": 1\n" +
                        "  \"shipDate\" 2022-08-10T15:01:27.971Z\n" +
                        "  \"status\" approved\n" +
                        "  \"complete\" true\n" +
                        "}", jsonObject.get("id"), jsonObject.get("petId"), jsonObject.get("quantity"),
                jsonObject.get("shipDate"), jsonObject.get("status"), jsonObject.get("complete"));

        return formattedRequest;
    }
}
