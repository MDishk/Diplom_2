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
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.is;

public class CreateUserTest {

    private UserData newUser;
    private UserApi userApi;
    private LoginUser login;

    @Before
    public void setUp() {
        userApi = new UserApi();
        newUser = CreateUser.createRandomUser();
        login = new LoginUser(newUser.getEmail(), newUser.getPassword());
    }

    @After
    public void cleanUp() {
        ValidatableResponse loginResponse = userApi.loginUser(login);

        if (loginResponse.extract().statusCode() == SC_OK) {
            String accessToken = loginResponse.extract().path("accessToken");
            if (accessToken != null) {
                userApi.deleteUser(accessToken);
            }
        }
    }

    @Test
    @Description("Проверка на то, что можно создать уникального пользователя")
    @DisplayName("Создать уникального пользователя")
    public void createUniqueCourierTest() {
        ValidatableResponse response = userApi.createUser(newUser);
        response
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @Description("Проверка на то, что нельзя создать уже зарегистрированного пользователя")
    @DisplayName("Создать уже зарегистрированного пользователя")
    public void createSameUserTest() {
        userApi.createUser(newUser);

        ValidatableResponse response = userApi.createUser(newUser);
        response
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", is("User already exists"));
    }
    @Test
    @Description("Проверка на то, что нельзя создать пользователя без поля email")
    @DisplayName("Создать пользователя без почты")
    public void createUserWithoutEmailTest() {
        newUser.setEmail(null);

        ValidatableResponse response = userApi.createUser(newUser);
        response
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @Description("Проверка на то, что нельзя создать пользователя без поля password")
    @DisplayName("Создать пользователя без пароля")
    public void createUserWithoutPasswordTest() {
        newUser.setPassword(null);

        ValidatableResponse response = userApi.createUser(newUser);
        response
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @Description("Проверка на то, что нельзя создать пользователя без поля name")
    @DisplayName("Создать пользователя без имени")
    public void createUserWithoutNameTest() {
        newUser.setName(null);

        ValidatableResponse response = userApi.createUser(newUser);
        response
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", is("Email, password and name are required fields"));
    }
}