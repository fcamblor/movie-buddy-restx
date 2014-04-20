package io.restx.movies.rest;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import io.restx.movies.data.Database;
import io.restx.movies.domain.Movie;
import io.restx.movies.domain.User;
import restx.annotations.GET;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static java.lang.Math.sqrt;
import static java.util.regex.Pattern.compile;

/**
 * @author fcamblor
 */
@RestxResource
@Component
@PermitAll
public class UserResource {

    private Database database;

    public UserResource(Database database) {
        this.database = database;
    }

    @GET("/users")
    public Iterable<User> getAllUsers(){
        return database.getUsers();
    }

    @GET("/users/:id")
    public Optional<User> findUserById(int id) {
        return database.findUserById(id);
    }

    @GET("/users/search/:name/:limit")
    public Iterable<User> findUsersByName(String name, int limit) {
        final Pattern pattern = compile(name.toLowerCase());
        return database.findUsersByPredicate(new Predicate<User>() {
            @Override
            public boolean apply(User user) {
                return pattern.matcher(user.getName().toLowerCase()).find();
            }
        }, limit);
    }

    @GET("/rates/:userId")
    public Map<Integer, Integer> findRatesByUserId(int userId) {
        Map<Integer, Integer> result = new HashMap<>();
        Optional<User> user = database.findUserById(userId);
        if(user.isPresent()) {
            result = user.get().getRatesByMovieId();
        }

        return result;
    }

    @GET("/users/share/:userIdA/:userIdB")
    public Iterable<Movie> sharedMoviesBetweenUsers(int userIdA, int userIdB) {
        Optional<User> user1 = database.findUserById(userIdA);
        Optional<User> user2 = database.findUserById(userIdB);

        Set<Movie> movies = new HashSet<Movie>();
        if(user1.isPresent() && user2.isPresent()) {
            Set<Integer> movieIds = user1.get().getRatesByMovieId().keySet();
            movieIds.retainAll(user2.get().getRatesByMovieId().keySet());

            movies.addAll(Collections2.transform(movieIds, new Function<Integer, Movie>() {
                @Override
                public Movie apply(Integer movieId) {
                    return database.findMovieById(movieId).get();
                }
            }));
        }

        return movies;
    }

    @GET("/users/distance/:userIdA/:userIdB")
    public String userDistance(int userIdA, int userIdB) {
        Optional<User> user1 = database.findUserById(userIdA);
        Optional<User> user2 = database.findUserById(userIdB);

        if(user1.isPresent() && user2.isPresent()) {
            double sumOfSquares = 0.0;
            for(Map.Entry<Integer,Integer> movieRate : user1.get().getRatesByMovieId().entrySet()){
                Integer rate1 = movieRate.getValue();
                Integer rate2 = user2.get().getRatesByMovieId().get(movieRate.getKey());
                if(rate2 != null) {
                    double diff = rate1 - rate2;
                    sumOfSquares += diff*diff;
                }
            }

            return Double.toString(1.0/(1.0+sqrt(sumOfSquares)));
        } else {
            return "0";
        }
    }
}
