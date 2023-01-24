package io.github.edercribeiro.rest;

import io.github.edercribeiro.domain.model.User;
import io.github.edercribeiro.domain.model.repository.UserRepository;
import io.github.edercribeiro.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setUp() {
        // Usuário padrão dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        // Usuário seguidor
        var follower = new User();
        follower.setAge(33);
        follower.setName("Cicrano");
        userRepository.persist(follower);
        followerId = follower.getId();
    }

    @Test
    @DisplayName("Should return 409 when followerId is equal to UserId!")
    public void sameUserAsFollowerTest(){

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
             .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
               .body(Matchers.is("You_can't_follow_yourself"));
    }

    @Test
    @DisplayName("Should return 404 when User id doesn't exist!")
    public void userNotFoundTest(){

        var body = new FollowerRequest();
        body.setFollowerId(userId);
        var nonexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", nonexistentUserId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should follow a user!")
    public void followUserTest(){

        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }



}
















