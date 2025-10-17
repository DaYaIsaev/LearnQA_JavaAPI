package lib;

import common.UserAuthData;
import io.restassured.http.Headers;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    protected String getHeader (Response Response, String name){
        Headers headers = Response.getHeaders();

        assertTrue(headers.hasHeaderWithName(name),"Response doesn`t have headers with name " + name);
        return headers.getValue(name);
    }

    protected String getCookie (Response Response, String name){
        Map<String, String> cookies = Response.getCookies();

        assertTrue(cookies.containsKey(name), "Response doesn`t have cookie with name " + name);
        return cookies.get(name);
    }

    protected int getIntFromJson(Response Response, String name){
        Response.then().assertThat().body("$", hasKey(name));
        return Response.jsonPath().getInt(name);
    }

    protected UserAuthData userLogin(Map<String, String> authData) {
        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String token = this.getHeader(responseGetAuth, "x-csrf-token");
        int userId = this.getIntFromJson(responseGetAuth, "user_id");

        UserAuthData userAuthData = new UserAuthData(String.valueOf(userId), token, cookie);
        return  userAuthData;
    }

    protected UserAuthData getRandomCreateUserAuthData(){
        Map<String, String> userData = DataGenerator.getRegistrationData(20, true);

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData);

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String userId = responseGetAuth.jsonPath().getString("user_id");
        UserAuthData userAuthData = new UserAuthData(userId, header, cookie);
        return userAuthData;
    }

}
