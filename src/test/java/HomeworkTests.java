import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HomeworkTests {

    @Test
    public void parsingJsonTest() {

        JsonPath response = RestAssured
                .given()
                .when()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        System.out.println(response.getMap("messages[1]"));
    }

    @Test
    public void redirectTest() {

        Response response = RestAssured
                .given()
                .redirects()
                .follow(true)
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String headerLocation = response.getHeader("Location");
        System.out.println(headerLocation);
    }

    @Test
    public void longRedirectTest() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();
        String headerLocation = response.getHeader("Location");
        System.out.println(headerLocation);
        int status = response.getStatusCode();

        while (status != 200) {
            Response nextResponse = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .get(headerLocation)
                    .andReturn();
            status = nextResponse.getStatusCode();
            headerLocation = nextResponse.getHeader("Location");
            System.out.println(headerLocation);
            System.out.println(status);
        }

    }

    @Test
    public void tokenTest() throws InterruptedException {
       String token = "";
       String expectedStatusNoToken = "Job is NOT ready";
       String expectedStatusTokenDelay = "Job is ready";
       String expectedResult = "42";
        JsonPath json = RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        token = json.get("token");
        int seconds = json.get("seconds");
        Map<String, Object> fields;
        fields = getJsonFields(token);
        Assertions.assertEquals(fields.get("status"),  expectedStatusNoToken);
        Thread.sleep(seconds*1000);
        fields = getJsonFields(token);
        Assertions.assertEquals(expectedStatusTokenDelay, fields.get("status"));
        Assertions.assertEquals(expectedResult, fields.get("result"));


    }

    public Map<String, Object> getJsonFields (String token){
        Map<String, Object> jsonFields = new HashMap<>();
        JsonPath json = RestAssured
                .given()
                .queryParams("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        String status = json.get("status");
        String result = json.get("result");
        jsonFields.put("status", status);
        jsonFields.put("result", result);
        return jsonFields;
    }

}
