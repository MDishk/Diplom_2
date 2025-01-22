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

public class ChangeUserInfoTest {

    private UserData newUser;
    private UserApi userApi;
    private LoginUser login;
    String accessToken;

    @Before
    public void setUp() {
        userApi = new UserApi();
        newUser = CreateUser.createRandomUser();
        login = new LoginUser(newUser.getEmail(), newUser.getPassword());

        ValidatableResponse loginResponse = userApi.createUser(newUser);
        accessToken = loginResponse.extract().path("accessToken");
    }

    @After
    public void cleanUp() {
        userApi.deleteUser(accessToken);
    }

    @Test
    @Description("Проверка на то, что авторизованный пользователь может изменить поле email")
    @DisplayName("Изменить почту залогиненного пользователя")
    public void changeEmailWithLoginTest() {
        newUser.setEmail("pupupup@mail.ru");

        ValidatableResponse changesResponse = userApi.changeUserInfo(accessToken, newUser);
        changesResponse
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("user.email", is(newUser.getEmail()));

        userApi.loginUser(login);
    }

    @Test
    @Description("Проверка на то, что авторизованный пользователь может изменить поле name")
    @DisplayName("Изменить имя залогиненного пользователя")
    public void changeNameWithLoginTest() {
        newUser.setName("Satoru");

        ValidatableResponse changesResponse = userApi.changeUserInfo(accessToken, newUser);
        changesResponse
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("user.name", is(newUser.getName()));

        userApi.loginUser(login);
    }

    @Test
    @Description("Проверка на то, что авторизованный пользователь может изменить поля email и name")
    @DisplayName("Изменить почту и имя залогиненного пользователя")
    public void changeEmailAndNameWithLoginTest() {
        newUser.setEmail("pupupup@mail.ru");
        newUser.setName("Satoru");

        ValidatableResponse changesResponse = userApi.changeUserInfo(accessToken, newUser);
        changesResponse
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("user.email", is(newUser.getEmail()))
                .body("user.name", is(newUser.getName()));

        userApi.loginUser(login);
    }

    @Test
    @Description("Проверка на то, что неавторизованный пользователь не может менять свою информацию")
    @DisplayName("Изменить почту и имя незалогиненного пользователя")
    public void changeEmailAndNameWithoutLoginTest() {
        newUser.setEmail("pupupu@mail.ru");
        newUser.setName("Satoru");

        ValidatableResponse changesResponse = userApi.changeUserInfo("", newUser);
        changesResponse
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", is("You should be authorised"));
    }
}
