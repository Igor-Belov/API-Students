package study.stepup.online;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class Client {
    private static final String BASE_URL = "http://localhost";

    static public RequestSpecification clientSpec() {
        return given().log().all()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .port(8080);
    }
}