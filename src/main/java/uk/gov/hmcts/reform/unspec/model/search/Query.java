package uk.gov.hmcts.reform.unspec.model.search;

import net.minidev.json.JSONObject;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;
import java.util.Objects;

import static java.util.Map.of;

public class Query {

    private final QueryBuilder queryBuilder;
    private final List<String> dataToReturn;
    private final int startIndex;

    public Query(QueryBuilder queryBuilder, List<String> dataToReturn, int startIndex) {
        Objects.requireNonNull(queryBuilder, "QueryBuilder cannot be null in search");
        if (startIndex < 0) {
            throw new IllegalArgumentException("Start index cannot be less than 0");
        }
        this.queryBuilder = queryBuilder;
        this.dataToReturn = dataToReturn;
        this.startIndex = startIndex;
    }

    @Override
    public String toString() {
        return new JSONObject(
            of("query", queryBuilder.toString(),
               "_source", dataToReturn,
               "from", startIndex
            )
        ).toString();
    }
}
