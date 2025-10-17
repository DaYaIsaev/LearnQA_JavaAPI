package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import lib.ApiCoreRequests;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;

@Epic("Authorisation cases")
@Feature("Authorization")
public class UserAuthTest extends BaseTestCase {

    String coockie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test successfully authorize by email and password")
    @DisplayName("Test positive auth user")
    @Severity(SeverityLevel.BLOCKER)
    public void testAuthUser() {

        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api_dev/user/auth",
                        this.header,
                        this.coockie);
        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @Description("This test check authorization status w/o sending auth cookie or token")
    @DisplayName("Test negative auth user")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition) {
        if (condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(
                    "https://playground.learnqa.ru/api_dev/user/auth",
                    this.coockie);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else if (condition.equals("headers")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(
                    "https://playground.learnqa.ru/api_dev/user/auth",
                    this.header);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else {
            throw new IllegalArgumentException("Condition value is not known: " + condition);
        }
    }

    @Description("This test check creation of user with domain w/o @")
    @DisplayName("Test negative creation user")
    @Test
    public void testCreateUserWithIncorrectDomain() {
        Map<String, String> userData = DataGenerator.getRegistrationData(20, false);
        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/",
                userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser, "Invalid email format");
    }

    @Description("This test check creation user w/o one of the necessary field")
    @DisplayName("Test negative creation user")
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testNegativeCreateUserWithoutField(String condition) {
        Map<String, String> userData = DataGenerator.getRegistrationData(20, true);
        if (condition.equals("email")) {
            userData.remove("email");
            Response responseForCheck = apiCoreRequests.makePostRequest(
                    "https://playground.learnqa.ru/api_dev/user/",
                    userData);
            System.out.println(responseForCheck.asString());

            Assertions.assertResponseCodeEquals(responseForCheck, 400);
            Assertions.assertResponseTextEquals(responseForCheck, "The following required params are missed: " + condition);

        } else if (condition.equals("password")) {
            userData.remove("password");
            Response responseForCheck = apiCoreRequests.makePostRequest(
                    "https://playground.learnqa.ru/api_dev/user/",
                    userData);

            Assertions.assertResponseCodeEquals(responseForCheck, 400);
            Assertions.assertResponseTextEquals(responseForCheck, "The following required params are missed: " + condition);

        } else if (condition.equals("username")) {
            userData.remove("username");
            Response responseForCheck = apiCoreRequests.makePostRequest(
                    "https://playground.learnqa.ru/api_dev/user/",
                    userData);

            Assertions.assertResponseCodeEquals(responseForCheck, 400);
            Assertions.assertResponseTextEquals(responseForCheck, "The following required params are missed: " + condition);

        } else if (condition.equals("firstName")) {
            userData.remove("firstName");
            Response responseForCheck = apiCoreRequests.makePostRequest(
                    "https://playground.learnqa.ru/api_dev/user/",
                    userData);

            Assertions.assertResponseCodeEquals(responseForCheck, 400);
            Assertions.assertResponseTextEquals(responseForCheck, "The following required params are missed: " + condition);

        } else if (condition.equals("lastName")) {
            userData.remove("lastName");
            Response responseForCheck = apiCoreRequests.makePostRequest(
                    "https://playground.learnqa.ru/api_dev/user/",
                    userData);

            Assertions.assertResponseCodeEquals(responseForCheck, 400);
            Assertions.assertResponseTextEquals(responseForCheck, "The following required params are missed: " + condition);

        } else {
            throw new IllegalArgumentException("Condition value is not known: " + condition);
        }
    }

    @Description("This test check creation of user with short email name")
    @DisplayName("Test positive creation user")
    @Test
    public void testCreateUserShortName() {
        //String email = DataGenerator.getRandomEmail(false);
        Map<String, String> userData = DataGenerator.getRegistrationData(1, true);
        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/",
                userData);
        System.out.println(responseCreateUser.asString());
        Assertions.assertResponseCodeEquals(responseCreateUser, 200);
        Assertions.assertJsonHasField(responseCreateUser, "id");
    }

    @Description("This test check creation of user with long email name")
    @DisplayName("Test negative creation user")
    @Test
    public void testCreateUserLongName() {
        Map<String, String> userData = DataGenerator.getRegistrationData(251, true);
        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/",
                userData);
        System.out.println(responseCreateUser.asString());
        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser, "The value of 'email' field is too long");
    }
}
