import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static io.restassured.RestAssured.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;
import io.qameta.allure.Step;

public class OrderCreationTest {

    private Integer trackId;

    @AfterEach
    @Step("Отмена тестового заказа")
    public void tearDown() {
        if (trackId != null) {
            given()
                    .contentType("application/json")
                    .body("{\"track\": " + trackId + "}")
                    .when()
                    .put("/api/v1/orders/cancel");

        }
    }

    @ParameterizedTest(name = "Создание заказа с цветами: {0}")
    @MethodSource("colorVariations")
    @DisplayName("Проверка создания заказа с разными вариантами цветов")
    public void createOrderWithDifferentColorCombinations(String caseName, String[] colors) {
        Order order = new Order(
                "John",
                "Connor",
                "Los Angeles, 142 apt.",
                4,
                "+7 800 355 35 35",
                5,
                "2025-12-12",
                "I'll be back",
                colors
        );

        ValidatableResponse response = OrderApi.createOrder(order);

        if (colors.length == 0) {

            trackId = response
                    .statusCode(SC_CREATED)
                    .body("track", notNullValue())
                    .extract().path("track");

        } else {
            response
                    .statusCode(SC_CREATED)
                    .body("track", notNullValue());
            trackId = response.extract().path("track");
        }
    }

    private static Stream<Arguments> colorVariations() {
        return Stream.of(
                Arguments.of("Один цвет (BLACK)", new String[]{"BLACK"}),
                Arguments.of("Один цвет (GREY)", new String[]{"GREY"}),
                Arguments.of("Оба цвета", new String[]{"BLACK", "GREY"}),
                Arguments.of("Без цвета", new String[]{})
        );
    }

}