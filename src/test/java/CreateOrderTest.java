import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.api.OrderApi;
import ru.yandex.praktikum.api.UserApi;
import ru.yandex.praktikum.data.CreateUser;
import ru.yandex.praktikum.data.OrderData;
import ru.yandex.praktikum.data.UserData;
import java.util.List;
import static org.hamcrest.Matchers.is;

public class CreateOrderTest {

    private UserData newUser;
    private UserApi userApi;
    private OrderData newOrder;
    private OrderApi orderApi;
    String accessToken;
    private List<String> ingredientsHash;

    @Before
    public void setUp() {
        userApi = new UserApi();
        newUser = CreateUser.createRandomUser();
        orderApi = new OrderApi();
        newOrder = new OrderData();

        ValidatableResponse response = userApi.createUser(newUser);
        accessToken = response.extract().body().path("accessToken");

        ValidatableResponse ingredientsResponse = orderApi.getAllIngredients();
        ingredientsHash = ingredientsResponse.extract().jsonPath().getList("data._id");
    }

    @After
    public void cleanUp() {
        userApi.deleteUser(accessToken);
    }

    @Test
    @Description("Проверка на то, что можно создать заказ с авторизацией")
    @DisplayName("Создать заказ с авторизацией")
    public void createOrderWithAuthTest() {
        List<String> ingredients = newOrder.getIngredients();
        ingredients.add(ingredientsHash.get(0));
        ingredients.add(ingredientsHash.get(2));
        ingredients.add(ingredientsHash.get(4));

        ValidatableResponse response = orderApi.createOrder(accessToken, newOrder);
        response
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @Description("Проверка на то, что можно создать заказ с ингредиентами")
    @DisplayName("Создать заказ с ингредиентами")
    public void createOrderWithIngredientsTest() {
        List<String> ingredients = newOrder.getIngredients();
        ingredients.add(ingredientsHash.get(1));
        ingredients.add(ingredientsHash.get(5));

        ValidatableResponse response = orderApi.createOrder(accessToken, newOrder);
        response
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @Description("Проверка на то, что нельзя создать заказ без авторизации")
    @DisplayName("Создать заказ без авторизации")
    public void createOrderWithoutAuthTest() {
        List<String> ingredients = newOrder.getIngredients();
        ingredients.add(ingredientsHash.get(0));
        ingredients.add(ingredientsHash.get(3));

        ValidatableResponse response = orderApi.createOrder("", newOrder);
        response
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @Description("Проверка на то, что нельзя создать заказ без ингредиентов")
    @DisplayName("Создать заказ без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        ValidatableResponse response = orderApi.createOrder(accessToken, newOrder);
        response
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @Description("Проверка на то, что нельзя создать заказ с некорректным хэшем ингредиентов")
    @DisplayName("Создать заказ с некорректным хэшем")
    public void createOrderWithWrongHashTest() {
        List<String> ingredients = newOrder.getIngredients();
        ingredientsHash.set(0, "4578dffgrfs4597845");
        ingredients.add(ingredientsHash.get(0));

        ValidatableResponse response = orderApi.createOrder(accessToken, newOrder);
        response
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
}
