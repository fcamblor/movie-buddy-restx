package io.restx.movies.rest;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import io.restx.movies.data.Database;
import io.restx.movies.domain.Movie;
import io.restx.movies.domain.Rate;
import io.restx.movies.domain.User;
import restx.WebException;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.http.HttpStatus;
import restx.security.PermitAll;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fcamblor
 */
@RestxResource
@Component
@PermitAll
public class RateResource {

    private Database database;

    public RateResource(Database database) {

        this.database = database;
    }

    @POST("/rates")
    public void createRate(Rate rate) {
        User user = database.findUserById(rate.getUserId()).get();
        user.getRatesByMovieId().put(rate.getMovieId(), rate.getRate());

        throw new WebException(HttpStatus.MOVED_PERMANENTLY, "/rates/"+user.getId());
    }

    @GET("/rates/:userid")
    public Map<Integer, Integer> findRatesByUserId(int userid) {
        Map<Integer, Integer> result = new HashMap<>();
        Optional<User> user = database.findUserById(userid);
        if(user.isPresent()) {
            result = user.get().getRatesByMovieId();
        }

        return result;
    }

}
