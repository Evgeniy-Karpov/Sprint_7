import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import io.qameta.allure.Step;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class CourierLoginTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private static final String LOGIN_URL = "/api/v1/courier/login";

    // Фиксированные тестовые данные (курьер уже создан)
    private final String validLogin = "skynet";
    private final String validPassword = "1234";
    private final String invalidLogin = "invalidskynet";
    private final String invalidPassword = "wrong";

    @BeforeEach
    @Step("Настройка базового URI")
    public void setUp() {
        baseURI = BASE_URI;
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    @Description("Проверка, что курьер может авторизоваться с валидными данными")
    public void loginCourierSuccessfully() {
        given()
                .contentType("application/json")
                .body(String.format("{\"login\":\"%s\",\"password\":\"%s\"}",
                        validLogin, validPassword))
                .when()
                .post(LOGIN_URL)
                .then()
                .statusCode(200)
                .body("id", notNullValue()); // Проверяем, что вернулся ID
    }

    @Test
    @DisplayName("Ошибка при отсутствии логина")
    @Description("Проверка, что система возвращает ошибку 400 с сообщением 'Недостаточно данных для входа' " +
            "при попытке авторизации без указания логина (только пароль)")
    public void loginWithoutLoginField() {
        given()
                .contentType("application/json")
                .body("{\"password\":\"" + validPassword + "\"}")
                .when()
                .post(LOGIN_URL)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии пароля") // Баг
    @Description("Проверка обработки запроса без пароля. " +
            "[Баг] В текущей реализации система может возвращать неверный код ответа. " +
            "Ожидается: 400 Bad Request с сообщением 'Недостаточно данных для входа'. " +
            "Актуальный результат: [504 Gateway Timeout]")
    public void loginWithoutPasswordField() {
        given()
                .contentType("application/json")
                .body("{\"login\":\"" + validLogin + "\"}")
                .when()
                .post(LOGIN_URL)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при неверном логине")
    @Description("Проверка, что система возвращает ошибку 404 с сообщением 'Учетная запись не найдена' " +
            "при попытке авторизации с несуществующим в системе логином")
    public void loginWithInvalidLogin() {
        given()
                .contentType("application/json")
                .body(String.format("{\"login\":\"%s\",\"password\":\"%s\"}",
                        invalidLogin, validPassword))
                .when()
                .post(LOGIN_URL)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при неверном пароле")
    @Description("Проверка, что система возвращает ошибку 404 с сообщением 'Учетная запись не найдена")
    public void loginWithInvalidPassword() {
        given()
                .contentType("application/json")
                .body(String.format("{\"login\":\"%s\",\"password\":\"%s\"}",
                        validLogin, invalidPassword))
                .when()
                .post(LOGIN_URL)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}