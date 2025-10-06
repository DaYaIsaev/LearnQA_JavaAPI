package homeWorkTests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
        assertEquals(fields.get("status"), expectedStatusNoToken);
        Thread.sleep(seconds * 1000);
        fields = getJsonFields(token);
        assertEquals(expectedStatusTokenDelay, fields.get("status"));
        assertEquals(expectedResult, fields.get("result"));


    }

    public Map<String, Object> getJsonFields(String token) {
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

    @Test
    public void passwordSelectionTest() {

        Map<String, String> data = new HashMap<>();
        //String login = "super_admin";
        data.put("login", "super_admin");

        List<String> passwords = getPasswords2019("https://en.wikipedia.org/wiki/List_of_the_most_common_passwords");
        for (int i = 0; i < passwords.size(); i++) {

            data.put("password", passwords.get(i));
            System.out.println(data);
            Response response = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();
            response.prettyPrint();

            String responseCoockie = response.getCookie("auth_cookie");
            System.out.println(responseCoockie);


            Map<String, String> coockeis = new HashMap<>();
            coockeis.put("auth_cookie", responseCoockie);
            Response responseCookie = RestAssured
                    .given()
                    .cookies(coockeis)
                    .when()
                    .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();
            if (Objects.equals(responseCookie.getBody().asString(), "You are authorized")) {
                System.out.println("Ваш пароль: " + passwords.get(i));
                break;
            } else
                System.out.println(responseCookie.getBody().asString());

        }

    }

    public List<String> getPasswords2019(String url) {
        List<String> passwords = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            Element table = doc.select("table.wikitable > caption:contains(Top 25 most common passwords by year)").first().parent();
            int year2019ColumnIndex = -1;
            Elements headerRow = table.select("thead > tr, tr:first-child");
            if (headerRow.isEmpty()) {
                headerRow = table.select("tr:first-child");
            }

            Elements headerCells = headerRow.select("th");
            for (int i = 0; i < headerCells.size(); i++) {
                if (headerCells.get(i).text().contains("2019")) {
                    year2019ColumnIndex = i;
                    break;
                }
            }

            Elements dataRows = table.select("tbody > tr:not(:first-child)");
            for (Element row : dataRows) {
                Elements cells = row.select("td");
                if (cells.size() > year2019ColumnIndex) {
                    passwords.add(cells.get(year2019ColumnIndex).text());
                }
            }
            System.out.println(passwords);

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return passwords;
    }

    @Test
    public void checkShortStringTest(){
        String hello = "Hello, world";
        assertTrue( hello.length() > 15,"String should be more than 15 symbols");
    }

    @Test
    public void cookieMethodTest() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        String cookieValue = response.getCookie("HomeWork");
        assertEquals(200, response.getStatusCode(), "Unexpected response status");
        assertTrue(response.getCookies().containsKey("HomeWork"), "Cookie 'HomeWork' not found in response");
        assertEquals("hw_value", cookieValue, "Unexpected value cookie for cookie 'HomeWork' " + cookieValue);
    }

    @Test
    public void headerMethodTest(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        String cookieValue = response.getHeaders().getValue("x-secret-homework-header");
        assertEquals(200, response.getStatusCode(), "Unexpected response status");
        assertTrue(response.getHeaders().hasHeaderWithName("x-secret-homework-header"),"Header 'x-secret-homework-header' not found in response");
        assertEquals("Some secret value", cookieValue, "Unexpected value header for header 'x-secret-homework-header' " + cookieValue);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
                            "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
                            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
                            "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
    })
    public void userAgentTest(String userAgent){
        Response response = RestAssured
                .given()
                .header("user-agent", userAgent)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .andReturn();
        if (response.jsonPath().get("device").equals("Unknown")){
            System.out.println("user-agent " + userAgent + " returned an incorrect value for device");
        } if (response.jsonPath().get("browser").equals("Unknown")) {
            System.out.println("user-agent " + userAgent + " returned an incorrect value for browser");
        } if (response.jsonPath().get("platform").equals("Unknown")) {
            System.out.println("user-agent " + userAgent + " returned an incorrect value for platform");
        }
    }
}
