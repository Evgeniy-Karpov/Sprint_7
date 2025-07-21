import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class CourierCreationTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private static final String COURIER_URL = "/api/v1/courier";
    private String courierId;
    private String login = "skynet_" + UUID.randomUUID().toString().substring(0, 8); // Разные варианты логинов чтобы не было пересечений
    private String password = "1234";
    private String firstName = "terminator";

    @BeforeEach
    @Step("Настройка базового URI")
    public void setUp() {
        baseURI = BASE_URI;
    }

    @AfterEach
    @Step("Удаление тестового курьера")
    public void tearDown() {
        if (courierId != null) {
            given()
                    .delete(COURIER_URL + "/" + courierId)
                    .then()
                    .statusCode(200);
        }
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Проверка, что курьер создается с валидными данными и возвращает ok: true")
    public void createCourierSuccessfully() {
        String requestBody = String.format(
                "{\"login\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\"}",
                login, password, firstName
        );

        given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(COURIER_URL)
                .then()
                .statusCode(201)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Ошибка при создании курьера с дубликатом логина")
    @Description("Проверка, что система возвращает 409 Conflict при попытке создать курьера с существующим логином")
    public void createDuplicateCourierShouldFail() {
        // Сначала создаем курьера
        createTestCourier(login, password, firstName);

        // Пытаемся создать такого же
        given()
                .contentType("application/json")
                .body(String.format(
                        "{\"login\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\"}",
                        login, password, firstName
                ))
                .when()
                .post(COURIER_URL)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Ошибка при отсутствии логина")
    @Description("Проверка, что система возвращает 400 Bad Request, если не передан логин")
    public void createCourierWithoutLoginShouldFail() {
        given()
                .contentType("application/json")
                .body("{\"password\":\"1234\",\"firstName\":\"terminator\"}")
                .when()
                .post(COURIER_URL)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии пароля")
    @Description("Проверка, что система возвращает 400 Bad Request, если не передан пароль")
    public void createCourierWithoutPasswordShouldFail() {
        given()
                .contentType("application/json")
                .body("{\"login\":\"skynet\",\"firstName\":\"terminator\"}")
                .when()
                .post(COURIER_URL)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Создание тестового курьера")
    public void createTestCourier(String login, String password, String firstName) {
        String requestBody = String.format(
                "{\"login\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\"}",
                login, password, firstName
        );

        given()
                .contentType("application/json")
                .body(requestBody)
                .post(COURIER_URL)
                .then()
                .statusCode(201);
    }
}