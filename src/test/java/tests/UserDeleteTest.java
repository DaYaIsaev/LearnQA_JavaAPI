package tests;

import common.UserAuthData;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test check the deletion of user with short ID 2")
    @DisplayName("Deletion of user with short ID 2")
    @Story("Negative test")
    @Owner("Dmitriy")
    @Test
    public void testDeleteUserId2() {

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        UserAuthData userAuthData = userLogin(authData);

        Response responseUserDelete = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api_dev/user/",
                userAuthData.getToken(),
                userAuthData.getCookie(),
                userAuthData.getUserId()
        );
        System.out.println(responseUserDelete.asString());
        Assertions.assertResponseCodeEquals(responseUserDelete,400);
        Assertions.assertJsonByName(responseUserDelete,"error","Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Description("This test check the deletion of user")
    @DisplayName("Test deletion of user")
    @Story("Positive test")
    @Severity(SeverityLevel.BLOCKER)
    @Owner("Dmitriy")
    @Test
    public void testDeleteUser() {
        UserAuthData userAuthData = getRandomCreateUserAuthData();

        Response responseUserDelete = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api_dev/user/",
                userAuthData.getToken(),
                userAuthData.getCookie(),
                userAuthData.getUserId()
        );

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        UserAuthData authDataId2 = userLogin(authData);

        Response responseGetDetailInfo = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userAuthData.getUserId(),
                authDataId2.getToken(),
                authDataId2.getCookie());

        Assertions.assertResponseCodeEquals(responseGetDetailInfo,404);
        Assertions.assertResponseTextEquals(responseGetDetailInfo,"User not found");
    }

    @Description("This test check the deletion of user by another user")
    @DisplayName("Test deletion of user by another user")
    @Story("Negative test")
    @Owner("Dmitriy")
    @Test
    public void testDeleteByAnotherUser() {
        UserAuthData userAuthData = getRandomCreateUserAuthData();

        UserAuthData anotherUserAuthData = getRandomCreateUserAuthData();

        Response responseUserDelete = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api_dev/user/",
                anotherUserAuthData.getToken(),
                anotherUserAuthData.getCookie(),
                userAuthData.getUserId()
        );

        Assertions.assertResponseCodeEquals(responseUserDelete,400);
        Assertions.assertJsonByName(responseUserDelete,"error","This user can only delete their own account.");
    }
}

