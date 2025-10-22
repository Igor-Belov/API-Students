package study.stepup.online;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.lang.Integer.parseInt;
import static org.hamcrest.Matchers.*;
import static study.stepup.online.Client.clientSpec;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static study.stepup.online.Client.clientSpec;

public class Step {
    static public int createStudent(Student student) {
        return parseInt(RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(Map.of(
                        "name", student.getName(),
                        "marks", student.getMarks()))
                .when()
                .post()
                .then().log().all()
                .statusCode(201)
                .extract()
//                .jsonPath()
//                .getInt("id"); Пока не поправят баг
                .asString());
    }

    static public void deleteStudent(int id) {
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .delete("/{id}", id);
    }

    static public void deleteAllStudents() {
        //Получим максимальный id
        int maxId = createStudent(new Student("maxId", List.of()));
        //удалим всех
        for (int i = -2; i < maxId; i++) {
            deleteStudent(i);
        }
    }
}
