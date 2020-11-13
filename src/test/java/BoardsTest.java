import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class BoardsTest {
    private String baseUrl = "https://api.trello.com/1/members/me/boards";
    private String key = "803dc42d60f6293de5655101db7edd60";
    private String token = "3b6f1e71d2c0a1c57d7c0dde2a5549b71d9d43faaaa05207a48253ba80bc5287";
    private String name = "Test_Board";
    private String baseUrl2 = "https://api.trello.com/1/";

    @Before
    public void beforeTests(){
        baseURI= baseUrl2;
    }

    @Test
    public void getAllBoards() {
        given().param("fields", "name,url").param("key", key).param("token", token)
        .when().get(baseUrl).then()
                .statusCode(200)
                .body("id", hasItems("5e625256861d131d231d8978"))
                .body("name", hasItems("My board1"));
    }

    @Test
    public void getNumberOfBoards(){
        given().param("fields", "name,url").param("key", key).param("token", token)
        .when().get(baseUrl).then().assertThat()
                .body("size()", is(12));
    }
    @Test
    public void queryTest(){
        String body = given().param("fields", "name,url").param("key", key).param("token", token).
        when().get(baseUrl).getBody().asString();
        System.out.println(body);
    }
    @Test
    public void createBoard(){
        System.out.println(given().header("Content-Type","application/json").log().ifValidationFails().queryParam("key", key).queryParam("token", token).queryParam("name", name)
                .when().post("boards/").getBody().jsonPath().getString("id"));//then().statusCode(200);

        given().param("fields", "name,url").param("key", key).param("token", token)
                .when().get("boards/").then()
                .body("name", hasItems("Test_Board"));
    }

    @Test
    public void deleteBoards(){
        // create board and gets its id
        String id = given()
                .header("Content-Type", "application/json")
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("name", name)
                .when()
                .post("boards/")
                .getBody().jsonPath().getString("id");

        // delete board by id
        given()
                .header("Content-Type","application/json")
                .queryParam("key", key)
                .queryParam("token", token)
                .when()
                .delete("boards/{id}", id)
                .then().statusCode(200);
    }

    @Test
    public void updateBoards(){
        // create new board and get its id
        String id = given()
                .header("Content-Type", "application/json")
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("name", name)
                .when().post("boards/")
                .getBody().jsonPath().getString("id");

        // update board description
        given()
                .header("Content-Type","application/json")
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("desc","New description")
        .when()
                .put("boards/{id}",id)
        .then()
                .statusCode(200);

        // check that the board has new description update
        given()
                .header("Content-Type", "application/json")
                .queryParam("key", key)
                .queryParam("token", token)
                .when()
                .get("boards/{id}",id)
                .then()
                .body("desc", equalTo("New description"));
    }


}
