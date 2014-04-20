package io.restx.movies.rest;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import io.restx.movies.data.Database;
import io.restx.movies.domain.Movie;
import restx.annotations.GET;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @author fcamblor
 */
@RestxResource
@Component
@PermitAll
public class MovieResource {

    private Database database;

    public MovieResource(Database database) {
        this.database = database;
    }

    @GET("/movies")
    public Iterable<Movie> getAllMovies(){
        return database.getMovies();
    }

    @GET("/movies/:id")
    public Optional<Movie> findMovieById(int id) {
        return database.findMovieById(id);
    }

    @GET("/movies/search/title/:title/:limit")
    public Iterable<Movie> findMoviesByTitle(String title, int limit) {
        final Pattern pattern = compile(title.toLowerCase());
        return database.findMoviesByPredicate(new Predicate<Movie>() {
            @Override
            public boolean apply(Movie movie) {
                return pattern.matcher(movie.getTitle().toLowerCase()).find();
            }
        }, limit);
    }

    @GET("/movies/search/actors/:actors/:limit")
    public Iterable<Movie> findMoviesByActor(String actors, int limit) {
        final Pattern pattern = compile(actors.toLowerCase());
        return database.findMoviesByPredicate(new Predicate<Movie>() {
            @Override
            public boolean apply(Movie movie) {
                return pattern.matcher(movie.getActors().toLowerCase()).find();
            }
        }, limit);
    }

    @GET("/movies/search/genre/:genre/:limit")
    public Iterable<Movie> findMoviesByGenreWithAPerfectI18NConsistency(String genre, int limit) {
        final Pattern pattern = compile(genre.toLowerCase());
        return database.findMoviesByPredicate(new Predicate<Movie>() {
            @Override
            public boolean apply(Movie movie) {
                return pattern.matcher(movie.getGenre().toLowerCase()).find();
            }
        }, limit);
    }
}

