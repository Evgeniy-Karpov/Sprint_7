import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class OrderListTest {


    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка, что возвращается корректный список заказов с пагинацией")
    public void getOrderListSuccessfully() {
        OrderApi.getOrderList(30, 0)
                .statusCode(SC_OK)
                .body("orders", notNullValue())
                .body("orders.size()", greaterThan(0))
                .body("orders[0].id", notNullValue());
    }

    @Test
    @DisplayName("Проверка пагинации")
    @Description("Тест проверяет корректность работы пагинации при получении списка заказов.")
    public void checkPagination() {
        OrderApi.getOrderList(1, 0)
                .statusCode(SC_OK)
                .body("orders.size()", lessThanOrEqualTo(1))
                .body("pageInfo.limit", equalTo(1))
                .body("pageInfo.page", equalTo(0));
    }


}