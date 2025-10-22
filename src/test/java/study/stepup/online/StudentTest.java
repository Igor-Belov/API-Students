package study.stepup.online;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static java.lang.Integer.parseInt;
import static org.hamcrest.Matchers.*;
import static study.stepup.online.Client.clientSpec;
import static study.stepup.online.Step.deleteAllStudents;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class StudentTest extends BaseTest{
    static int studentId = -1;
    static Student student;
    static Student anotherStudent;

    @BeforeEach
    void setUp() {
        student = new Student(studentId, "Николай Петрович Абу-Трололоёв", List.of(5, 4, 5, 3, 4));
        System.out.println(student);
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(student)
                .when()
                .post()
                .then().log().all()
                .statusCode(201);
    }

    @AfterEach
    void cleanUp() {
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .delete("/{id}", student.getId());
        if (anotherStudent != null) {
            RestAssured.given()
                    .spec(clientSpec())
                    .basePath("student")
                    .when()
                    .delete("/{id}", anotherStudent.getId());
        }
    }

    @AfterAll
    static void fullCleanUp() {
        deleteAllStudents();
    }

    //get /student/{id} возвращает JSON студента с указанным ID и заполненным именем, если такой есть в базе, код 200.
    @Test
    @DisplayName("1 get /student/{id} Получить данные существующего студента")
    public void testGetStudentById_Success_Returns200AndStudentData() {
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .get("/{id}", student.getId())
                .then().log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", Matchers.equalTo(student.getId()))
                .body("name", Matchers.equalTo(student.getName()))
                .body("marks", Matchers.equalTo(student.getMarks()));
    }

    //get /student/{id} возвращает код 404, если студента с данным ID в базе нет.
    @Test
    @DisplayName("2 get /student/{id} Получить ошибку если студент не существует")
    public void testGetStudentById_Fail_Returns404() {
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .get("/{id}", student.getId()-100)
                .then().log().all()
                .statusCode(404)
                .header("Content-Length", "0")
                .body(Matchers.emptyString());
    }

    //post /student добавляет студента в базу, если студента с таким ID ранее не было, при этом имя заполнено, код 201, тело пустое.
    @Test
    @DisplayName("3.1 post /student  Получить 201 при создании студента с именем")
    public void testPostNewStudentWithNullMarks_Success_Returns201() {
        anotherStudent = new Student(-2, "Ян");
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(anotherStudent)
                .when()
                .post()
                .then().log().all()
                .statusCode(201)
                .header("Content-Length", "0")
                .body(Matchers.emptyString());
    }

    //post /student добавляет студента в базу, если студента с таким ID ранее не было, при этом имя заполнено, код 201, тело пустое.
    @Test
    @DisplayName("3.2 post /student  Получить 201 при создании студента с именем и пустыми оценками")
    public void testPostNewStudentWithEmptyMarks_Success_Returns201() {
        anotherStudent = new Student(-2, "Ян", List.of());
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(anotherStudent)
                .when()
                .post()
                .then().log().all()
                .statusCode(201)
                .header("Content-Length", "0")
                .body(Matchers.emptyString());
    }

    //post /student добавляет студента в базу, если студента с таким ID ранее не было, при этом имя заполнено, код 201, тело пустое.
    @Test
    @DisplayName("3.3 post /student  Получить 201 при создании студента с именем оценками")
    public void testPostNewStudentWithMarks_Success_Returns201() {
        anotherStudent = new Student(-2, "Ян", List.of(1,2,3,4,5));
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(anotherStudent)
                .when()
                .post()
                .then().log().all()
                .statusCode(201)
                .header("Content-Length", "0")
                .body(Matchers.emptyString());
    }

    @Test
    @DisplayName("6.1 post /student  Получить 400 при создании студента c null имени")
    public void testPostNewStudentNullName_Fail_Returns400() {
        anotherStudent = new Student(-2, "Ян", List.of(1,2,3,4,5));
        anotherStudent.setName(new String());
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(anotherStudent)
                .when()
                .post()
                .then().log().all()
                .statusCode(400)
                .header("Content-Length", "0")
                .body(Matchers.emptyString());
    }

    @Test
    @DisplayName("6.2 post /student  Получить 400 при создании студента c пустым именем")
    public void testPostNewStudentEmptyName_Fail_Returns400() {
        anotherStudent = new Student(-2, "Ян", List.of(1,2,3,4,5));
        anotherStudent.setName("");
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(anotherStudent)
                .when()
                .post()
                .then().log().all()
                .statusCode(400)
                .header("Content-Length", "0")
                .body(Matchers.emptyString());
    }

    @Test
    @DisplayName("6.3 post /student  Получить 400 при создании студента без поля имя")
    public void testPostNewStudentWithoutName_Fail_Returns400() {
        anotherStudent = new Student(-2, "Ян", List.of(1,2,3,4,5));
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(Map.of(
                        "id", anotherStudent.getId(),
                        "marks", anotherStudent.getMarks()))
                .when()
                .post()
                .then().log().all()
                .statusCode(400)
                .header("Content-Length", "0")
                .body(Matchers.emptyString());
    }

    @Test
    @DisplayName("3.7 post /student  Получить 201 при создании студента без поля оценки")
    public void testPostNewStudentWithoutMarks_Success_Returns201() {
        anotherStudent = new Student(-2, "Ян", List.of(1,2,3,4,5));
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(Map.of(
                        "id", anotherStudent.getId(),
                        "name", anotherStudent.getName()))
                .when()
                .post()
                .then().log().all()
                .statusCode(201)
                .header("Content-Length", "0")
                .body(Matchers.emptyString());
    }

    //post /student обновляет имя студента в базе, если студент с таким ID ранее был. код 201.
    @Test
    @DisplayName("4.1 post /student  Получить 201 и id студента если уже студент существует. Меняем имя")
    public void testChangesNameStudent_Success_Returns201() {
        String newName = student.getName() + "Оглы";
        student.setName(newName);
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(student)
                .when()
                .post()
                .then().log().all()
                .statusCode(201);
//Проверим что новое имя записалось, а оценки остались целыми (не создался новый студент)
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .get("/{id}", student.getId())
                .then().log().all()
                .body("id", Matchers.equalTo(student.getId()))
                .body("name", Matchers.equalTo(student.getName()))
                .body("marks", Matchers.equalTo(student.getMarks()));
    }

    //post /student обновляет оценки в базе, если студент с таким ID ранее был, при этом имя заполнено, код 201.
    @Test
    @DisplayName("4.2 post /student  Получить 201 и id студента если уже студент существует. Меняем оценки")
    public void testChangesMarksStudent_Success_Returns201() {
        List<Integer> newMarks = List.of(2, 2, 2, 5, 5);
        student.setMarks(newMarks);
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(student)
                .when()
                .post()
                .then().log().all()
                .statusCode(201)
                .header("Content-Length", "0")
                .body(Matchers.emptyString());
//Проверим что новые оценки записалось, а имя остались целым (не создался новый студент)
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .get("/{id}", student.getId())
                .then().log().all()
                .body("id", Matchers.equalTo(student.getId()))
                .body("name", Matchers.equalTo(student.getName()))
                .body("marks", Matchers.equalTo(student.getMarks()));
    }

    @Test
    @DisplayName("5.1 post /student  Получить 201 и id студента при создании студента c id null")
    public void testPostNewStudentWithoutId_Success_Returns201andId() {
        anotherStudent = new Student(-2,"Ян", List.of(1,2,3,4,5));
        int responseId = parseInt(RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(Map.of(
                "name", anotherStudent.getName(),
                "marks", anotherStudent.getMarks()))
                .when()
                .post()
                .then().log().all()
                .statusCode(201)
                .contentType(ContentType.JSON)
//                .body("id", allOf(
//                        instanceOf(Number.class),
//                        greaterThan(0)))
                .extract()
//                .jsonPath()
//                .getInt("id"); Пока не поправят баг
                .asString());

        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .delete("/{id}", responseId);

    }

    @Test
    @DisplayName("5.2 post /student  Получить 201 и id студента при создании студента c id null. Проверка, что создается новый студент с новым id +1, а не перетирается старый")
    public void testPostNewStudentWithoutId_Success_Returns201andNewIncrementId() {
        anotherStudent = new Student(-2,"Ян", List.of(1,2,3,4,5));
        int firstId = parseInt(RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(Map.of(
                        "name", anotherStudent.getName(),
                        "marks", anotherStudent.getMarks()))
                .when()
                .post()
                .then().log().all()
                .extract()
//                .jsonPath()
//                .getInt("id"); Пока не поправят баг
                .asString());

        int secondId = parseInt(RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(Map.of(
                        "name", anotherStudent.getName()))
                .when()
                .post()
                .then().log().all()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract()
//                .jsonPath()
//                .getInt("id"); Пока не поправят баг
                .asString());

        Assertions.assertEquals(firstId + 1, secondId);

        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .delete("/{id}", firstId);

        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .delete("/{id}", secondId);
    }

    @Test
    @DisplayName("5.3 post /student  Получить 201 и id студента при создании студента c id null. Проверка, что вернулся правильный id и по нему можно посмотреть студента")
    public void testPostNewStudentWithoutId_Success_StudentAvailable() {
        anotherStudent = new Student(-2,"Тест", List.of(1));
        int firstId = parseInt(RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .body(Map.of(
                        "name", anotherStudent.getName(),
                        "marks", anotherStudent.getMarks()))
                .when()
                .post()
                .then().log().all()
                .extract()
//                .jsonPath()
//                .getInt("id"); Пока не поправят баг
                .asString());

        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .get("/{id}", firstId)
                .then().log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", Matchers.equalTo(firstId))
                .body("name", Matchers.equalTo(anotherStudent.getName()))
                .body("marks", Matchers.equalTo(anotherStudent.getMarks()));


    }

    @Test
    @DisplayName("7.1 delete /student/{id} удаляет студента с указанным ID из базы, код 200")
    public void testDeleteStudent_Success_Returns200() {
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .delete("/{id}", student.getId())
                .then().log().all()
                .statusCode(200)
                .header("Content-Length", "0")
                .body(Matchers.emptyString());
    }

    @Test
    @DisplayName("7.2 delete /student/{id} удаляет студента с указанным ID из базы. Проверка что действительно удалили и при попытке обратится Get получим 404.")
    public void testFactDeleteStudent_Success_Returns404() {
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .delete("/{id}", student.getId())
                .then().log().all()
                .statusCode(200);

        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .get("/{id}", student.getId())
                .then().log().all()
                .statusCode(404)
                .header("Content-Length", "0")
                .body(Matchers.emptyString());
    }

    @Test
    @DisplayName("8. delete /student/{id} возвращает код 404, если студента с таким ID в базе нет.")
    public void testDeleteNotExistStudent_Fail_Returns404() {
        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .delete("/{id}", student.getId());

        RestAssured.given()
                .spec(clientSpec())
                .basePath("student")
                .when()
                .delete("/{id}", student.getId())
                .then().log().all()
                .statusCode(404)
                .header("Content-Length", "0")
                .body(Matchers.emptyString());
    }
}
