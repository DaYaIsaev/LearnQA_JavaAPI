import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {
    @Test
    public void testRestAssured() {
        Map<String, String> data = new HashMap<>();
        data.put("login","secret_login");
        data.put("password","secret_pass");

        Response responseForGet = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();
        System.out.println(data);

        String responseCoockie = responseForGet.getCookie("auth_cookie");

       Map<String, String> coockeis = new HashMap<>();
        coockeis.put("auth_cookie",responseCoockie);

        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookies(coockeis)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();

        responseForCheck.print();




    }
}
