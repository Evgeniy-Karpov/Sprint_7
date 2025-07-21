import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import io.qameta.allure.Step;

public class OrderCreationTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private static final String ORDERS_URL = "/api/v1/orders";
    private static final String CANCEL_ORDER_URL = "/api/v1/orders/cancel";
    private Integer trackId;

    @BeforeEach
    @Step("Настройка базового URI")
    public void setUp() {
        baseURI = BASE_URI;
    }

    @AfterEach
    @Step("Отмена тестового заказа")
    public void tearDown() {
        if (trackId != null) {
            given()
                    .contentType("application/json")
                    .body("{\"track\": " + trackId + "}")
                    .put(CANCEL_ORDER_URL);
        }
    }

    @ParameterizedTest(name = "Создание заказа с цветами: {0}")
    @MethodSource("colorVariations")
    @DisplayName("Проверка создания заказа с разными вариантами цветов")
    public void createOrderWithDifferentColorCombinations(String caseName, String[] colors) {
        String requestBody = buildOrderRequest(colors);

        ValidatableResponse response = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(ORDERS_URL)
                .then();

        if (colors.length == 0) {
            response.statusCode(400);
        } else {
            response
                    .statusCode(201)
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

    @Step("Формирование тела запроса")
    public String buildOrderRequest(String[] colors) {
        String colorsPart = colors.length == 0 ? "" :
                String.format("\"color\": [\"%s\"]", String.join("\", \"", colors));

        return String.format(
                "{" +
                        "\"firstName\": \"John\"," +
                        "\"lastName\": \"Connor\"," +
                        "\"address\": \"Los Angeles, 142 apt.\"," +
                        "\"metroStation\": 4," +
                        "\"phone\": \"+7 800 355 35 35\"," +
                        "\"rentTime\": 5," +
                        "\"deliveryDate\": \"1995-07-08\"," +
                        "\"comment\": \"I’ll be back\"," +
                        "%s" +
                        "}",
                colorsPart
        ).replace(",\n}", "}");
    }
}