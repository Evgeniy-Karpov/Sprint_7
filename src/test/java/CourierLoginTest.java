import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;


public class CourierLoginTest {

    private String courierId;
    private final String login = "skynet_" + UUID.randomUUID().toString().substring(0, 8);
    private final Courier testCourier = new Courier(login, "1234", "Terminator");

    @BeforeEach
    @Step("Создание тестового курьера")
    public void setUp() {
        CourierApi.createCourier(testCourier)
                .statusCode(SC_CREATED);

        courierId = CourierApi.loginCourier(new CourierLogin(login, "1234"))
                .extract().path("id").toString();
    }

    @AfterEach
    @Step("Удаление тестового курьера")
    public void tearDown() {
        if (courierId != null) {
            CourierApi.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    @Description("Проверка, что курьер может авторизоваться с валидными данными")
    public void loginCourierSuccessfully() {
        CourierApi.loginCourier(new CourierLogin(login, "1234"))
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Ошибка при отсутствии логина")
    @Description("Проверка, что система возвращает ошибку 400 с сообщением 'Недостаточно данных для входа' " +
            "при попытке авторизации без указания логина (только пароль)")
    public void loginWithoutLoginField() {
        CourierApi.loginCourier(new CourierLogin(null, "1234"))
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии пароля") // Баг
    @Description("Проверка обработки запроса без пароля. " +
            "[Баг] В текущей реализации система может возвращать неверный код ответа. " +
            "Ожидается: 400 Bad Request с сообщением 'Недостаточно данных для входа'. " +
            "Актуальный результат: [504 Gateway Timeout]")
    public void loginWithoutPasswordField() {
        CourierApi.loginCourier(new CourierLogin(login, null))
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при неверном логине")
    @Description("Проверка, что система возвращает ошибку 404 с сообщением 'Учетная запись не найдена' " +
            "при попытке авторизации с несуществующим в системе логином")
    public void loginWithInvalidLogin() {
        CourierApi.loginCourier(new CourierLogin("nonexistent", "1234"))
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при неверном пароле")
    @Description("Проверка, что система возвращает ошибку 404 с сообщением 'Учетная запись не найдена")
    public void loginWithInvalidPassword() {
        CourierApi.loginCourier(new CourierLogin(login, "wrong"))
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}