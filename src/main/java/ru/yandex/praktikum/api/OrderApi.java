package ru.yandex.praktikum.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.data.OrderData;
import static io.restassured.RestAssured.given;

public class OrderApi extends RestApi {

    public static final String CREATE_ORDER = "api/orders";
    public static final String ALL_INGREDIENTS = "api/ingredients";
    public static final String GET_ORDER = "api/orders";

    @Step("POST-запрос на создание заказа")
    public ValidatableResponse createOrder(String accessToken, OrderData newOrder) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", accessToken)
                .and().body(newOrder)
                .when().post(CREATE_ORDER)
                .then();
    }

    @Step("GET-запрос на получение списка всех ингредиентов")
    public ValidatableResponse getAllIngredients() {
        return given()
                .spec(requestSpecification())
                .when().get(ALL_INGREDIENTS)
                .then();
    }

    @Step("GET-запрос на получение списка всех ингредиентов")
    public ValidatableResponse getUsersOrder(String accessToken) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", accessToken)
                .when().get(GET_ORDER)
                .then();
    }
}
