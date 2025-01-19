import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.api.OrderApi;
import ru.yandex.praktikum.api.UserApi;
import ru.yandex.praktikum.data.OrderData;
import ru.yandex.praktikum.data.UserData;
import java.util.List;
import static org.hamcrest.Matchers.is;

public class GetOrdersTest {

    private UserData newUser;
    private UserApi userApi;
    private OrderData newOrder;
    private OrderApi orderApi;
    String accessToken;
    private List<String> ingredientsHash;

    @Before
    public void setUp() {
        userApi = new UserApi();
        newUser = new UserData();
        orderApi = new OrderApi();
        newOrder = new OrderData();

        ValidatableResponse response = userApi.createUser(newUser);
        accessToken = response.extract().body().path("accessToken");

        ValidatableResponse ingredientsResponse = orderApi.getAllIngredients();
        ingredientsHash = ingredientsResponse.extract().jsonPath().getList("data._id");
        List<String> ingredients = newOrder.getIngredients();
        ingredients.add(ingredientsHash.get(0));
        ingredients.add(ingredientsHash.get(2));
        ingredients.add(ingredientsHash.get(4));

        orderApi.createOrder(accessToken, newOrder);
    }

    @After
    public void cleanUp() {
        userApi.deleteUser(accessToken);
    }

    @Test
    @Description("Проверка на то, что можно получить заказ пользователя с авторизацией")
    @DisplayName("Получить заказ с авторизацией")
    public void getOrderWithAuthTest() {
        ValidatableResponse response = orderApi.getUsersOrder(accessToken);
        response
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @Description("Проверка на то, что нельзя получить заказ пользователя без авторизации")
    @DisplayName("Получить заказ без авторизации")
    public void getOrderWithoutAuthTest() {
        ValidatableResponse response = orderApi.getUsersOrder("");
        response
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", is("You should be authorised"));
    }
}
