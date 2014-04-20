package io.restx.movies.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.IntNode;

import java.io.IOException;

/**
 * @author fcamblor
 */
@JsonDeserialize(using = Rate.RateDeserializer.class)
public class Rate {
    int userId;
    int movieId;
    int rate;

    public Rate(int userId, int movieId, int rate) {
        this.userId = userId;
        this.movieId = movieId;
        this.rate = rate;
    }

    public int getUserId() {
        return userId;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getRate() {
        return rate;
    }

    public static class RateDeserializer extends JsonDeserializer<Rate> {
        @Override
        public Rate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            TreeNode node = jsonParser.getCodec().readTree(jsonParser);
            return new Rate(
                    ((IntNode) node.get("userId")).numberValue().intValue(),
                    ((IntNode) node.get("movieId")).numberValue().intValue(),
                    ((IntNode) node.get("rate")).numberValue().intValue());
        }
    }
}
