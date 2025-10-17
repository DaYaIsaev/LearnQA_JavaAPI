package tests;

import common.RandomStringGenerator;
import common.UserAuthData;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testEditJustCreatedTest(){
        //Generate user
        Map<String, String> userData = DataGenerator.getRegistrationData(20,true);

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api_dev/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");

        //Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api_dev/user/login")
                .andReturn();

        //Edit
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api_dev/user/" + userId)
                .andReturn();
        System.out.println(responseEditUser.asString());

        //Get
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api_dev/user/" + userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName",newName);
        System.out.println(responseUserData.asString());
    }

    @Description("This test check edit user w/o authorization")
    @DisplayName("Test negative edit user")
    @Test
    public void testEditUserNonAuthorized(){
        UserAuthData userAuthData = getRandomCreateUserAuthData();
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response editUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userAuthData.getUserId(),
                null,
                null,
                editData);
        Assertions.assertResponseCodeEquals(editUser,400);
        Assertions.assertJsonByName(editUser,"error","Auth token not supplied");
    }

    @Description("This test check edit user another user")
    @DisplayName("Test negative edit user")
    @Test
    public void testEditUserAnotherUser(){
        UserAuthData userAuthData = getRandomCreateUserAuthData();
        UserAuthData anotherUserAuthData = getRandomCreateUserAuthData();

        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response editUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userAuthData.getUserId(),
                anotherUserAuthData.getToken(),
                anotherUserAuthData.getCookie(),
                editData);
        Assertions.assertResponseCodeEquals(editUser,400);
        Assertions.assertJsonByName(editUser,"error","This user can only edit their own data.");
    }

    @Description("This test check edit user email w/o @")
    @DisplayName("Test negative edit user")
    @Test
    public void testEditUserEmailWithoutAt(){
        UserAuthData userAuthData = getRandomCreateUserAuthData();
        String newEmail = DataGenerator.getRandomEmail(15,false);
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response editUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userAuthData.getUserId(),
                userAuthData.getToken(),
                userAuthData.getCookie(),
                editData);

        Assertions.assertResponseCodeEquals(editUser,400);
        Assertions.assertJsonByName(editUser,"error","Invalid email format");

    }

    @Description("This test check edit user with short first name")
    @DisplayName("Test negative edit user")
    @Test
    public void testEditUserFirstNameShort(){
        UserAuthData userAuthData = getRandomCreateUserAuthData();

        String newFirstName = RandomStringGenerator.randomString(1);
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newFirstName);

        Response editUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userAuthData.getUserId(),
                userAuthData.getToken(),
                userAuthData.getCookie(),
                editData);

        Assertions.assertResponseCodeEquals(editUser,400);
        Assertions.assertJsonByName(editUser,"error","The value for field `firstName` is too short");

    }

}
