package io.restx.movies.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.restx.movies.domain.Movie;
import io.restx.movies.domain.User;
import restx.factory.AutoStartable;
import restx.factory.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Optional.fromNullable;
import static java.util.Collections.binarySearch;

/**
 * @author fcamblor
 */
@Component
// Declared as "Autostartable" in order to call start() once for all,
// and avoiding to hot reload class changes for this class (we want to keep its state)
public class Database implements AutoStartable {

    private final ObjectMapper mapper;
    private ImmutableList<User> users;
    private ImmutableList<Movie> movies;

    private void initPredefinedVotes() {
        for(int[] votes : new int[][]{
          /*userId      movieId       rate*/
          {3022, 772, 2},
          {3022, 24, 10},
          {3022, 482, 4},
          {3022, 302, 7},
          {3022, 680, 6},
          {9649, 772, 2},
          {9649, 24, 8},
          {9649, 482, 9},
          {9649, 302, 3},
          {9649, 556, 8},
          {2349, 453, 7},
          {2349, 461, 9},
          {2349, 258, 10},
          {2349, 494, 9},
          {2349, 158, 4},
          {496, 682, 4},
          {496, 559, 7},
          {496, 537, 4},
          {496, 352, 3},
          {496, 005, 9},
        }){
            int userId = votes[0];
            int movieId = votes[1];
            int rate = votes[2];

            User user = findUserById(userId).get();
            user.getRatesByMovieId().put(movieId, rate);
        }
    }

    public Database(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void start() {
        try(
                InputStream usersStream = Database.class.getClassLoader().getResourceAsStream("db/users.json");
                InputStream moviesStream = Database.class.getClassLoader().getResourceAsStream("db/movies.json");
        ) {
            List users = mapper.readValue(usersStream,
                    mapper.getTypeFactory().constructCollectionType(List.class, User.class));
            List movies = mapper.readValue(moviesStream,
                    mapper.getTypeFactory().constructCollectionType(List.class, Movie.class));

            // sort for binary search
            Collections.sort(movies);
            Collections.sort(users);

            this.users = ImmutableList.copyOf(users);
            this.movies = ImmutableList.copyOf(movies);

            // pre-fill with some votes
            initPredefinedVotes();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public Optional<Movie> findMovieById(int id) {
        int index = binarySearch(movies, new Movie(id, "", "", ""));
        return fromNullable((index < 0) ? null : movies.get(index));
    }

    public Optional<User> findUserById(int id) {
        int index = binarySearch(users, new User(id, ""));
        return fromNullable((index < 0) ? null : users.get(index));
    }

    public Iterable<User> findUsersByPredicate(Predicate<User> p, int limit) {
        return Iterables.limit(Collections2.filter(users, p), limit);
    }

    public Iterable<Movie> findMoviesByPredicate(Predicate<Movie> p, int limit) {
        return Iterables.limit(Collections2.filter(movies, p), limit);
    }

    public ImmutableList<User> getUsers() {
        return users;
    }

    public ImmutableList<Movie> getMovies() {
        return movies;
    }
}
