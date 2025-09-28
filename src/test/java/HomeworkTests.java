import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

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
    public void longRedirectTest(){
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .get("https://playground.learnqa.ru/api/long_redirect")
                    .andReturn();
        String headerLocation = response.getHeader("Location");
        System.out.println(headerLocation);
        int status = response.getStatusCode();

            while(status!=200){
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
}
