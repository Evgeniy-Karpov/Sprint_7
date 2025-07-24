import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;


public class OrderApi {
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private static final String ORDERS_URL = "/api/v1/orders";

    static {
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Создать заказ")
    public static ValidatableResponse createOrder(Order order) {
        return given()
                .contentType("application/json")
                .body(order)
                .when()
                .post(ORDERS_URL)
                .then();
    }

    @Step("Получить список заказов")
    public static ValidatableResponse getOrderList(int limit, int page) {
        return given()
                .queryParam("limit", limit)
                .queryParam("page", page)
                .when()
                .get(ORDERS_URL)
                .then();
    }

}