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
import ru.sogaz.utils.Converter;
import ru.sogaz.utils.Props;
import ru.sogaz.utils.TestContext;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class PetStoreSteps {

    private JSONObject requestBody;
    private TestContext testContext = new TestContext();
    private static TestPropManager properties = TestPropManager.getTestPropManager();

    public PetStoreSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    public PetStoreSteps() {

    }
    @Дано("сайт магазина доступен")
    public void checkPing() {
        Response response = given().contentType(ContentType.JSON)
                .when().get(properties.getProperty(Props.BASE_STORE_URL))
                .then().extract().response();
        Assert.assertEquals("Магазин недоступен!", 200,response.getStatusCode());
    }

    @Когда("пользователь создает заказ с параметрами:$")
    public void createNewOrder(DataTable dataTable) {
        List<Map<String, String>> orderDataList = dataTable.asMaps(String.class, String.class);
        if (orderDataList.size() > 0) {
            Map<String, String> orderData = orderDataList.get(0);
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
            testContext.setOrderId(response.getBody().path("id"));
            Allure.addAttachment("Request","POST" +"\n" +
                    "Host: "+ properties.getProperty(Props.BASE_URL)  +
                    "/order" + "\n" + "Content-Type: " + response.getContentType() +
                    "\n" + Converter.prettyPrintJson(requestBody.toString()));
            Allure.addAttachment("Response", "HTTP/1.1  " +
                    response.getStatusCode() + "\n" +
                    response.getContentType() + "\n"+
                    Converter.prettyPrintJson(response.asString()));

        }
    }


    @И("пользователь получает информацию о заказе")
    public void getOrderById() {
        int id = testContext.getOrderId();
        Response response = given().contentType(ContentType.JSON)
                .when().get(properties.getProperty(Props.BASE_URL) + "/order/" + id)
                .then().log().all()
                .extract().response();
        if(response.getStatusCode()==404) {
            Assert.fail("Заказ не найден");
        }
        Allure.addAttachment("Request","GET" +"\n"
                + "Host: "+ properties.getProperty(Props.BASE_URL)+"/order/"+ id);
        Allure.addAttachment("Response", "HTTP/1.1  " + response.getStatusCode() + "\n" +
                response.getContentType() + "\n" + Converter.prettyPrintJson(response.asString()));
        Boolean complete = Boolean.parseBoolean((String) requestBody.get("complete"));
        Assert.assertEquals("Ошибка в сверяемых данных!", requestBody.get("petId"), response.getBody().path("petId"));
        Assert.assertEquals("Ошибка в сверяемых данных!", Integer.valueOf((String) requestBody.get("id")), response.getBody().path("id"));
        Assert.assertEquals("Ошибка в сверяемых данных!", complete, response.getBody().path("complete"));


    }
    @Тогда("пользователь удаляет информацию о заказе")
    public void deleteOrderById() {
        Integer id = testContext.getOrderId();
        if(id==null) {
            Assert.fail("Заказ не был создан!");
        }
        Response response = given().contentType(ContentType.MULTIPART)
                .when().delete(properties.getProperty(Props.BASE_URL) + "/order/" + id)
                .then().log().all()
                .extract().response();
        String expectedErrorMessage = String.format("Заказ с таким номером %s не найден", id);
        if(response.getStatusCode()!=200) { //TODO swagger всегда возвращает 200
            Assert.fail(expectedErrorMessage);
        }
    }

    @И("проверяет что заказ не существует")
    public void checkOrderIsNotFound() {
        Integer id = testContext.getOrderId();
        Response response = given().contentType(ContentType.JSON)
                .when().get(properties.getProperty(Props.BASE_URL) + "/order/" + id)
                .then().log().all()
                .extract().response();
        id=null;
        if(response.getStatusCode()!=404) {
            Allure.addAttachment("Response", "HTTP/1.1  "
                    + response.getStatusCode() + "\n" +
                    response.getContentType() +  "\n" +
                    Converter.prettyPrintJson(response.asString()));
            Assert.fail("Заказ существует");
        }
        Allure.addAttachment("Response","HTTP/1.1  "
                + String.valueOf(response.getStatusCode()) + "\n" + "Заказ не найден");
        Assert.assertEquals("Заказ существует",404, response.getStatusCode());
    }
}
