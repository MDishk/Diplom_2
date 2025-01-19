package ru.yandex.praktikum.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.datafaker.Faker;

@Data
@AllArgsConstructor
public class UserData {

    private String email;
    private String password;
    private String name;

    public UserData() {
        Faker faker = new Faker();
        this.email = faker.internet().emailAddress();
        this.password = faker.internet().password();
        this.name = faker.name().firstName();
    }
}