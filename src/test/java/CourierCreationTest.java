import io.qameta.allure.Description;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;


public class CourierCreationTest {

    private String courierId;
    private final String login = "skynet_" + UUID.randomUUID().toString().substring(0, 8);
    private final Courier validCourier = new Courier(login, "1234", "Terminator");

    @AfterEach
    @DisplayName("Удаление тестового курьера")
    public void tearDown() {
        if (courierId != null) {
            CourierApi.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Проверка, что курьер создается с валидными данными и возвращает ok: true")
    public void createCourierSuccessfully() {
        CourierApi.createCourier(validCourier)
                .statusCode(SC_CREATED)
                .body("ok", is(true));

        courierId = CourierApi.loginCourier(new CourierLogin(login, "1234"))
                .extract().path("id").toString();
    }

    @Test
    @DisplayName("Ошибка при создании курьера с дубликатом логина")
    @Description("Проверка, что система возвращает 409 Conflict при попытке создать курьера с существующим логином")
    public void createDuplicateCourierShouldFail() {

        CourierApi.createCourier(validCourier); // Создаём первого курьера
        courierId = CourierApi.loginCourier(new CourierLogin(login, "1234"))
                .extract().path("id").toString();

        CourierApi.createCourier(validCourier) // Пытаемся создать такого же
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Ошибка при отсутствии логина")
    @Description("Проверка, что система возвращает 400 Bad Request, если не передан логин")
    public void createCourierWithoutLoginShouldFail() {
        Courier invalidCourier = new Courier(null, "1234", "Terminator");
        CourierApi.createCourier(invalidCourier)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии пароля")
    @Description("Проверка, что система возвращает 400 Bad Request, если не передан пароль")
    public void createCourierWithoutPasswordShouldFail() {
        Courier invalidCourier = new Courier(login, null, "Terminator");
        CourierApi.createCourier(invalidCourier)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

}