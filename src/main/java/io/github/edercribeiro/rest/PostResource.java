package io.github.edercribeiro.rest;

import io.github.edercribeiro.domain.model.Post;
import io.github.edercribeiro.domain.model.User;
import io.github.edercribeiro.domain.model.repository.FollowerRepository;
import io.github.edercribeiro.domain.model.repository.PostRepository;
import io.github.edercribeiro.domain.model.repository.UserRepository;
import io.github.edercribeiro.dto.CreatePostRequest;
import io.github.edercribeiro.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private FollowerRepository followerRepository;
    private PostRepository repository;
    private UserRepository userRepository;

    @Inject
    public PostResource(UserRepository userRepository,
                        PostRepository repository,
                        FollowerRepository followerRepository) {

        this.userRepository = userRepository;
        this.repository = repository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(
            @PathParam("userId") Long userId, CreatePostRequest request){
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);

        repository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPost( @PathParam("userId") Long userId,
                              @HeaderParam("followerId") Long followerId){
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(followerId == null || followerId == 0){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("You forgot the follower identification number!")
                    .build();
        }

        var follower = userRepository.findById(followerId);

        if (follower == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Follower does not exist!")
                    .build();
        }

        var follows = followerRepository.follows(follower, user);

        if(!follows){
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You can't see these posts!")
                    .build();
        }

        // Para gerar essa "query" dentro do find basta abrir as áspas duplas.
        var query = repository.find(
                "user", Sort.by("datetime", Sort.Direction.Descending), user);

        var list = query.list();

        var postResponseList = list.stream()
                //Existem duas formas de fazer esse map. Uma está comentada.
                //.map(post -> PostResponse.fromEntity(post))
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(postResponseList).build();
    }
}
