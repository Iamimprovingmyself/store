package ru.sogaz.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import io.qameta.allure.Allure;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import manager.TestPropManager;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Assert;
import utils.Converter;
import utils.Props;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class PetStoreSteps {

    private JSONObject requestBody;
    private static TestPropManager properties = TestPropManager.getTestPropManager();

    @Дано("сайт магазина доступен")
    public void checkPing() {
        Response response = given().contentType(ContentType.JSON)
                .when().get(properties.getProperty(Props.BASE_STORE_URL))
                .then().extract().response();
        Assert.assertEquals("Магазин недоступен!", 200, response.getStatusCode());
    }

    @Когда("пользователь создает заказ с параметрами:$")
    public void createNewOrder(DataTable dataTable) {
        List<Map<String, String>> orderDataList = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> orderData : orderDataList) {
            JSONTokener tokener;
            JSONObject template = null;
            try {
                tokener = new JSONTokener(new FileReader("src/test/resources/data/order.json"));
                template = new JSONObject(tokener);
            } catch (FileNotFoundException exception) {
                System.out.println("По указанному пути файл не найден");
            }

            for (String key : template.keySet()) {
                if (orderData.containsKey(key)) {
                    template.put(key, orderData.get(key));
                }
            }
            requestBody = template;
            Response response = given().contentType(ContentType.JSON)
                    .body(requestBody.toString()).when()
                    .post(properties.getProperty(Props.BASE_URL) + "/order")
                    .then().log().all()
                    .extract().response();
            Assert.assertEquals("Не совпал код ответа", 200, response.getStatusCode());
            Allure.addAttachment("Request", "POST" + "\n" +
                    "Host: " + properties.getProperty(Props.BASE_URL) +
                    "/order" + "\n" + "Content-Type: " + response.getContentType() +
                    "\n" + Converter.prettyPrintJson(requestBody.toString()));
            Allure.addAttachment("Response", "HTTP/1.1  " +
                    response.getStatusCode() + "\n" +
                    response.getContentType() + "\n" +
                    Converter.prettyPrintJson(response.asString()));
        }
    }


    @И("пользователь получает информацию о заказе")
    public void getOrderById(DataTable dataTable) {
        List<Map<String, String>> orderDataList = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : orderDataList) {
            if (!row.containsKey("id")) {
                Assert.fail("Информация о заказе доступна только по ID, введите ID");
            }
            Integer expectedId = Integer.valueOf(row.get("id"));
            Integer expectedQuantity = Integer.valueOf(row.get("quantity"));
            Boolean expectedComplete = Boolean.valueOf(row.get("complete"));
            Response response = given().contentType(ContentType.JSON)
                    .when().get(properties.getProperty(Props.BASE_URL) + "/order/" + row.get("id"))
                    .then().log().all()
                    .extract().response();
            if (response.getStatusCode() == 404) {
                Assert.fail("Заказ с id=" + row.get("id") + " не найден");
            }

            Integer actualId = response.getBody().path("id");
            Integer actualQuantity = response.getBody().path("quantity");
            Boolean actualComplete = response.getBody().path("complete");
            Assert.assertEquals("Неверное значение id", expectedId, actualId);
            Assert.assertEquals("Неверное значение quantity", expectedQuantity, actualQuantity);
            Assert.assertEquals("Неверное значение complete", expectedComplete, actualComplete);
        }
    }


    @Тогда("пользователь удаляет информацию о заказах")
    public void deleteOrdersById(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> data : dataList) {
            String id = data.get("id");
            if (id == null || id.isEmpty()) {
                Assert.fail("Введите ID для удаления заказа");
            }
            Response response = given().contentType(ContentType.JSON)
                    .when().get(properties.getProperty(Props.BASE_URL) + "/order/" + id)
                    .then().log().all()
                    .extract().response();

            if (response.getStatusCode() == 404) {
                Assert.fail("Заказ с id=" + id + " не найден");
            }
            Response delete = given().contentType(ContentType.MULTIPART)
                    .when().delete(properties.getProperty(Props.BASE_URL) + "/order/" + id)
                    .then().log().all()
                    .extract().response();

            String expectedErrorMessage = String.format("Заказ с таким номером %s не найден", id);
            if (delete.getStatusCode() != 200) { //TODO swagger всегда возвращает 200
                Assert.fail(expectedErrorMessage);
            }
        }
    }


    @И("пользователь проверяет, что заказы не существуют:$")
    public void checkOrdersDoNotExist(DataTable dataTable) {
        List<Map<String, String>> orderDataList = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> orderData : orderDataList) {
            String id = orderData.get("id");
            if (id == null || id.isEmpty()) {
                Assert.fail("Введите ID для проверки заказа");
            }
            boolean orderNotFound = checkOrderIsNotFound(id);
            Assert.assertTrue("Заказ с id=" + id + " существует", orderNotFound);
        }
    }

    private boolean checkOrderIsNotFound(String id) {
        Response response = given().contentType(ContentType.JSON)
                .when().get(properties.getProperty(Props.BASE_URL) + "/order/" + id)
                .then().log().all()
                .extract().response();
        return response.getStatusCode() == 404;
    }

}
