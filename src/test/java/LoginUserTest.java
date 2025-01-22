import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.api.UserApi;
import ru.yandex.praktikum.data.CreateUser;
import ru.yandex.praktikum.data.LoginUser;
import ru.yandex.praktikum.data.UserData;
import static org.hamcrest.Matchers.is;

public class LoginUserTest {

    private UserData newUser;
    private UserApi userApi;
    private LoginUser login;
    String accessToken;

    @Before
    public void setUp() {
        userApi = new UserApi();
        newUser = CreateUser.createRandomUser();
        login = new LoginUser(newUser.getEmail(), newUser.getPassword());

        userApi.createUser(newUser);
        ValidatableResponse loginResponse = userApi.loginUser(login);
        accessToken = loginResponse.extract().body().path("accessToken");
    }

    @After
    public void cleanUp() {
        userApi.deleteUser(accessToken);
    }

    @Test
    @Description("Проверка на то, что существующий пользователь может авторизоваться")
    @DisplayName("Авторизация пользователя")
    public void loginUserTest() {
        ValidatableResponse loginResponse = userApi.loginUser(login);
        loginResponse
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @Description("Проверка на то, что пользователь не сможет авторизоваться без поля email")
    @DisplayName("Авторизация пользователя без почты")
    public void loginUserWithoutEmailTest() {
        login = new LoginUser(null, newUser.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(login);
        loginResponse
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @Description("Проверка на то, что пользователь не сможет авторизоваться без поля password")
    @DisplayName("Авторизация пользователя без пароля")
    public void loginUserWithoutPasswordTest() {
        login = new LoginUser(newUser.getEmail(), null);
        ValidatableResponse loginResponse = userApi.loginUser(login);
        loginResponse
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", is("email or password are incorrect"));
    }
}
