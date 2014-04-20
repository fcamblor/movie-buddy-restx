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
import java.util.HashMap;

import static java.lang.Integer.compare;

@JsonDeserialize(using = User.UserDeserializer.class)
public class User implements Comparable<User> {
    final int _id;
    final String name;
    HashMap<Integer, Integer> ratesByMovieId;

    public User(int _id, String name) {
        this._id = _id;
        this.name = name;
        this.ratesByMovieId = new HashMap<>();
    }

    @Override
    public int compareTo(User user) {
        return compare(_id, user._id);
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public HashMap<Integer, Integer> getRatesByMovieId() {
        return ratesByMovieId;
    }

    // Used only to keep "json" representation ... to be fair with other benchmarks
    // even if in real life, we will never do this...
    public static class UserDeserializer extends JsonDeserializer<User> {
        @Override
        public User deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            TreeNode node = jsonParser.getCodec().readTree(jsonParser);
            return new User(
                ((JsonNode)node.get("_id")).numberValue().intValue(),
                ((JsonNode)node.get("name")).asText()
            );
        }
    }
}