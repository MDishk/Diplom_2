package ru.yandex.praktikum.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.data.LoginUser;
import ru.yandex.praktikum.data.UserData;
import static io.restassured.RestAssured.given;

public class UserApi extends RestApi {

    public static final String CREATE_USER = "api/auth/register";
    public static final String LOGIN_USER = "api/auth/login";
    public static final String DELETE_USER = "api/auth/user";
    public static final String CHANGE_USER_INFO = "api/auth/user";

    @Step("POST-запрос на создание пользователя")
    public ValidatableResponse createUser(UserData newUser) {
        return given()
                .spec(requestSpecification())
                .and().body(newUser)
                .when().post(CREATE_USER)
                .then();
    }

    @Step("POST-запрос на авторизацию пользователя")
    public ValidatableResponse loginUser(LoginUser login) {
        return given()
                .spec(requestSpecification())
                .and().body(login)
                .when().post(LOGIN_USER)
                .then();
    }

    @Step("DELETE-запрос на удаление пользователя")
    public void deleteUser(String accessToken) {
        given()
                .spec(requestSpecification())
                .header("Authorization", accessToken)
                .when().delete(DELETE_USER)
                .then()
                .log().all();
    }

    @Step("PATCH-запрос на изменение информации о пользователе")
    public ValidatableResponse changeUserInfo(String accessToken, UserData userData) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", accessToken)
                .and().body(userData)
                .when().patch(CHANGE_USER_INFO)
                .then();
    }
}