import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;

public class RegisterTest {
    private String name, email, password;

    @Before
    public void beforeTests(){
        baseURI = "http://users.bugred.ru/tasks/rest";

        // create unique user info
        name = "Nadia" + System.currentTimeMillis();
        email = "nadia" + System.currentTimeMillis() + "@gmail.com";
        password = Long.toString(System.currentTimeMillis());
    }

    @Test
    public void doRegisterMapTest(){
         // create json body as a map
        Map<String, Object> jsonAsMap = new HashMap<String, Object>();
        jsonAsMap.put("name", name);
        jsonAsMap.put("email", email);
        jsonAsMap.put("password", password);

        given().contentType(JSON).
                body(jsonAsMap)
        .when()
                .post("/doregister")
        .then()
                .statusCode(200);
    }

    @Test
    public void verifyErrorDuplicatedUser(){

        // create user
        given().contentType(JSON)
                .body(new User(name, email, password))
                .when()
                .post("/doregister");

        // create the same user again
        Error duplicatedUser = given().contentType(JSON)
                .body(new User(name, email, password))
                .when()
                .post("/doregister")
                .getBody().as(Error.class);
        assert duplicatedUser.getType().equals("error");

    }

    @Test
    public void doRegisterClassTest(){
        // create json body a the class object
        User user = new User(name, email, password);
//        user.setName(name);
//        user.setEmail(email);
//        user.setPassword(password);
        given().contentType(JSON)
                .body(user)
                .when()
                .post("/doregister")
                .then()
                .statusCode(200);
    }

    @Test
    public void doRegisterGetBody(){
        // create user and get body as a class object
       User user = new User(name, email, password);
       User responseBody = given().contentType(JSON)
                .body(user)
                .when()
                .post("/doregister")
                .getBody().as(User.class);

        Assert.assertEquals(responseBody.getName(), name);
        Assert.assertEquals(responseBody.getEmail(), email);
    }

}
