package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Getting info cases")
@Feature("Get info")
public class UserGetTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void getUserDataNotAuth() {
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api_dev/user/2")
                .andReturn();
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Test
    public void testGetUserDetailsAuthUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api_dev/user/login")
                .andReturn();

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api_dev/user/2")
                .andReturn();

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Description("This test check getting of info about himself")
    @DisplayName("Test positive getting user details info")
    @Test
    public void testUserGetDetailsInfo() {

        Map<String, String> userData = DataGenerator.getRegistrationData(20, true);

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/",
                userData);

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/login",
                authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String userId = responseGetAuth.jsonPath().getString("user_id");

        Response responseGetDetailInfo = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userId,
                header,
                cookie);

        Assertions.assertJsonHasField(responseGetDetailInfo, "username");
        Assertions.assertJsonHasField(responseGetDetailInfo, "firstName");
        Assertions.assertJsonHasField(responseGetDetailInfo, "lastName");
        Assertions.assertJsonHasField(responseGetDetailInfo, "email");
    }

    @Description("This test check getting of info about another user")
    @DisplayName("Test negative getting user details info")
    @Test
    public void testUserGetInfo() {

        Map<String, String> userData = DataGenerator.getRegistrationData(20, true);
        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/",
                userData);

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/login",
                authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Map<String, String> newUserData = DataGenerator.getRegistrationData(20, true);
        Response responseCreateNewUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/",
                newUserData);

        String userId = responseCreateNewUser.jsonPath().getString("id");

        Response responseGetDetailInfo = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userId,
                header,
                cookie);

        Assertions.assertJsonHasField(responseGetDetailInfo, "username");
        Assertions.assertJsonHasNotField(responseGetDetailInfo, "firstName");
        Assertions.assertJsonHasNotField(responseGetDetailInfo, "lastName");
        Assertions.assertJsonHasNotField(responseGetDetailInfo, "email");
    }
}
