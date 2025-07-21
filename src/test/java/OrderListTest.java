import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import io.qameta.allure.Step;


public class OrderListTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private static final String ORDERS_URL = "/api/v1/orders";

    @BeforeEach
    @Step("Настройка базового URI")
    public void setUp() {
        baseURI = BASE_URI;
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка, что возвращается корректный список заказов с пагинацией")
    public void getOrderListSuccessfully() {
        given()
                .contentType("application/json")
                .when()
                .get(ORDERS_URL)
                .then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders", hasSize(greaterThan(0)))
                .body("orders[0].id", notNullValue())
                .body("orders[0].firstName", notNullValue())
                .body("orders[0].lastName", notNullValue())
                .body("orders[0].address", notNullValue())
                .body("orders[0].track", notNullValue())
                .body("pageInfo.page", notNullValue())
                .body("pageInfo.total", notNullValue())
                .body("pageInfo.limit", notNullValue())
                .body("availableStations", notNullValue())
                .body("availableStations[0].name", notNullValue())
                .body("availableStations[0].number", notNullValue());
    }

    @Test
    @DisplayName("Проверка пагинации")
    @Description("Тест проверяет корректность работы пагинации при получении списка заказов.")
    public void checkPagination() {
        given()
                .contentType("application/json")
                .queryParam("limit", 1)
                .queryParam("page", 0)
                .when()
                .get(ORDERS_URL)
                .then()
                .statusCode(200)
                .body("orders", hasSize(lessThanOrEqualTo(1)))
                .body("pageInfo.limit", equalTo(1))
                .body("pageInfo.page", equalTo(0));
    }


}