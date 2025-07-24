import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;

public class CourierApi {
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private static final String COURIER_URL = "/api/v1/courier";

    static {
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Создать курьера")
    public static ValidatableResponse createCourier(Courier courier) {
        return given()
                .contentType("application/json")
                .body(courier)
                .when()
                .post(COURIER_URL)
                .then();
    }

    @Step("Логин курьера")
    public static ValidatableResponse loginCourier(CourierLogin credentials) {
        return given()
                .contentType("application/json")
                .body(credentials)
                .when()
                .post(COURIER_URL + "/login")
                .then();
    }

    @Step("Удалить курьера")
    public static void deleteCourier(String courierId) {
        given()
                .delete(COURIER_URL + "/" + courierId)
                .then()
                .statusCode(SC_OK);
    }
}