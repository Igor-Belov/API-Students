package study.stepup.online;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static java.lang.Integer.parseInt;
import static org.hamcrest.Matchers.*;
import static study.stepup.online.Client.clientSpec;
import static study.stepup.online.Step.createStudent;
import static study.stepup.online.Step.deleteAllStudents;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class TopStudentTest extends BaseTest{
    private Student firstStudent;
    private Student secondStudent;
    private Student therdStudent;
    private Student fourthStudent;

    @BeforeEach
    void setUp() {
        deleteAllStudents();
    }

    @AfterEach
    void cleanUp() {
        deleteAllStudents();
    }

    @Test
    @DisplayName("9. get /topStudent код 200 и пустое тело, если студентов в базе нет.")
    void testTopStudent_DbEmpty_Success_Returns200() {
        //Вызовем и проверим эндпоинт
        RestAssured.given()
                .spec(clientSpec())
                .basePath("topStudent")
                .when()
                .get()
                .then().log().all()
                .statusCode(200)
                .header("Content-Length", "0");
    }

    @Test
    @DisplayName("10 get /topStudent код 200 и пустое тело, если ни у кого из студентов в базе нет оценок.")
    void testTopStudent_AllMarksEmpty_Success_Returns200() {
        firstStudent = new Student("Без оценок", List.of());
        secondStudent = new Student("Без оценок", List.of());
        createStudent(firstStudent);
        createStudent(secondStudent);
        RestAssured.given()
                .spec(clientSpec())
                .basePath("topStudent")
                .when()
                .get()
                .then().log().all()
                .statusCode(200)
                .header("Content-Length", "0");
    }

    @Test
    @DisplayName("11.1 get /topStudent код 200 и один студент, если у него максимальная средняя оценка, либо же среди всех студентов с максимальной средней у него их больше всего.(Максимальная средняя)")
    void testTopStudent_MaxAvg_Success_Returns200andStudent() {
        firstStudent = new Student("5,5,5,5,5,5,5,5,5,5,5,5,4, средний 4,9231", List.of(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 4));
        secondStudent = new Student("5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,3, средняя 4,9167", List.of(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 3));
        therdStudent = new Student("Без оценок", List.of());
        int idAvgStudent = createStudent(firstStudent);
        createStudent(secondStudent);
        createStudent(therdStudent);

        RestAssured.given()
                .spec(clientSpec())
                .basePath("topStudent")
                .when()
                .get()
                .then().log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", Matchers.equalTo(1))
                .body("[0].id", Matchers.equalTo(idAvgStudent))
                .body("[0].name", Matchers.equalTo(firstStudent.getName()))
                .body("[0].marks", Matchers.equalTo(firstStudent.getMarks()));
    }

        @Test
        @DisplayName("11.2 get /topStudent код 200 и один студент, если у него максимальная средняя оценка, либо же среди всех студентов с максимальной средней у него их больше всего.(Максимум оценок при равном AVG)")
        void testTopStudent_MaxMarks_Success_Returns200andStudent() {
            firstStudent = new Student("5,5,5,5,5,5,5,5,5,5,5,4 средний 4,9167", List.of(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 4));
            secondStudent = new Student("5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,3, средняя 4,9167", List.of(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 3));
            therdStudent = new Student("Без оценок", List.of());
            createStudent(firstStudent);
            int idAvgStudent = createStudent(secondStudent);
            createStudent(therdStudent);

            RestAssured.given()
                    .spec(clientSpec())
                    .basePath("topStudent")
                    .when()
                    .get()
                    .then().log().all()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("size()", Matchers.equalTo(1))
                    .body("[0].id", Matchers.equalTo(idAvgStudent))
                    .body("[0].name", Matchers.equalTo(secondStudent.getName()))
                    .body("[0].marks", Matchers.equalTo(secondStudent.getMarks()));
        }

    @Test
    @DisplayName("12 get /topStudent код 200 и несколько студентов, если у них всех эта оценка максимальная и при этом они равны по количеству оценок.")
    void testTopStudent_BothMaxAvg_Success_Returns200andBothStudent() {
        firstStudent = new Student("5,3", List.of(5, 3));
        secondStudent = new Student("5,3", List.of(5, 3));
        therdStudent = new Student("4", List.of(4));
        fourthStudent = new Student("Без оценок", List.of());
        int firstIdAvgStudent = createStudent(firstStudent);
        int secondIdAvgStudent = createStudent(secondStudent);
        createStudent(therdStudent);
        createStudent(fourthStudent);

        RestAssured.given()
                .spec(clientSpec())
                .basePath("topStudent")
                .when()
                .get()
                .then().log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", Matchers.equalTo(2))
                .body("findAll { it.id == " + firstIdAvgStudent + " }.size()", equalTo(1))
                .body("find { it.id == " + firstIdAvgStudent + " }.name", equalTo(firstStudent.getName()))
                .body("find { it.id == " + firstIdAvgStudent + " }.marks", equalTo(firstStudent.getMarks()))
                .body("findAll { it.id == " + secondIdAvgStudent + " }.size()", equalTo(1))
                .body("find { it.id == " + secondIdAvgStudent + " }.name", equalTo(secondStudent.getName()))
                .body("find { it.id == " + secondIdAvgStudent + " }.marks", equalTo(secondStudent.getMarks()));
    }
}
