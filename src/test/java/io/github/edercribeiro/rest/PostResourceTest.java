package io.github.edercribeiro.rest;

import io.github.edercribeiro.domain.model.Follower;
import io.github.edercribeiro.domain.model.Post;
import io.github.edercribeiro.domain.model.User;
import io.github.edercribeiro.domain.model.repository.FollowerRepository;
import io.github.edercribeiro.domain.model.repository.PostRepository;
import io.github.edercribeiro.domain.model.repository.UserRepository;
import io.github.edercribeiro.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class) // Uma das formas de mapear a URL! A outra está na classe UserResourceTest.java
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRepository postRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;


    @BeforeEach
    @Transactional
    public void setUP(){
        // Usuário padrão dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        // Cria a postagem para o usuário.
        Post post = new Post();
        post.setText("Hello");
        post.setUser(user);
        postRepository.persist(post);

        // Usuário que não é um seguidor
        var userNotFollower = new User();
        userNotFollower.setAge(33);
        userNotFollower.setName("Cicrano");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        // Usuário que é um seguidor
        var userFollower = new User();
        userFollower.setAge(35);
        userFollower.setName("Beltrano");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("Should create a post for an user")
    public void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .pathParam("userId", userId)
        .when()
            .post()
        .then()
            .statusCode(201);
    }

    @Test
    @DisplayName("Should return 404 when trying to make a post for a nonexistent user")
    public void postForNonexistentUserTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var nonexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", nonexistentUserId)
        .when()
                .post()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 404 when user does not exist")
    public void listPostUserNotFoundTest(){
        var nonexistentUserId = 999;

        given()
                .pathParam("userId", nonexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 when followerId header not found")
    public void listPostFollowerHeaderNotSentTest(){

        given()
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the follower identification number!"));
    }
    @Test
    @DisplayName("Should return 400 when follower does not exist")
    public void listPostFollowerNonexistentTest(){
        var nonexistentFollowerId = 999;
        given()
                .pathParam("userId", userId)
                .header("followerId", nonexistentFollowerId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("Follower does not exist!"));
    }

    @Test
    @DisplayName("Should return 403 when follower does not follow")
    public void listPostNotAFollowerTest(){
        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
                .when()
                .get()
                .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts!"));
    }

    @Test
    @DisplayName("Should return posts")
    public void listPostTest(){
        given()
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }

}

