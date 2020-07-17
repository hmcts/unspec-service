package uk.gov.hmcts.reform.unspec.model.documents;

import java.util.Arrays;
import java.util.List;

public enum DocumentType {
    SEALED_CLAIM;

    private final List<String> values;

    DocumentType(String... values) {
        this.values = Arrays.asList(values);
    }

    public List<String> getValues() {
        return values;
    }

    public static DocumentType fromValue(String value) {
        return Arrays.stream(values())
            .filter(v -> v.values.contains(value) || v.name().equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown Document Type: " + value));
    }
}
