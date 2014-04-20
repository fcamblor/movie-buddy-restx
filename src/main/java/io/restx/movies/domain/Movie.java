package io.restx.movies.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.IntNode;

import java.io.IOException;

import static java.lang.Integer.compare;

@JsonDeserialize(using = Movie.MovieDeserializer.class)
public class Movie implements Comparable<Movie> {
    final int _id;
    final String title;
    final String actors;
    final String genre;
    @JsonIgnore
    final String json;

    public Movie(int _id, String title, String actors, String genre, String json) {
        this._id = _id;
        this.title = title;
        this.actors = actors;
        this.genre = genre;
        this.json = json;
    }

    @Override
    public int compareTo(Movie movie) {
        return compare(_id, movie._id);
    }

    @Override
    public String toString() {
        return json;
    }

    public int getId() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getActors() {
        return actors;
    }

    public String getGenre() {
        return genre;
    }

    // Used only to keep "json" representation ... to be fair with other benchmarks
    // even if in real life, we will never do this...
    public static class MovieDeserializer extends JsonDeserializer<Movie> {
        @Override
        public Movie deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            TreeNode node = jsonParser.getCodec().readTree(jsonParser);
            return new Movie(
                    ((JsonNode)node.get("_id")).numberValue().intValue(),
                    ((JsonNode)node.get("Title")).asText(),
                    ((JsonNode)node.get("Actors")).asText(),
                    ((JsonNode)node.get("Genre")).asText(),
                    node.toString()
            );
        }
    }
}