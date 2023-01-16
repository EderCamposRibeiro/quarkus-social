package io.github.edercribeiro.domain.model.repository;

import io.github.edercribeiro.domain.model.Follower;
import io.github.edercribeiro.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user){
        // Dessa forma:
        /* Map<String, Object> params = new HashMap<>();
           params.put("follower", follower);
           params.put("user", user);
       */ // Ou da forma abaixo:
        var params = Parameters.with("follower", follower).and("user", user).map();

        PanacheQuery<Follower> query = find("follower =:follower and user =:user", params);
        var result = query.firstResultOptional();

        return result.isPresent();
    }

    public List<Follower> findByUser(Long userId) {
        var query = find("user.id", userId);
        return query.list();
    }

}
