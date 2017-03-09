package com.cloudpocket.utils;

import java.util.HashMap;
import java.util.Map;

import static com.cloudpocket.utils.Utils.urlEncode;

/**
 * Helper class to build URIs.
 */
public class SimpleUrlBuilder {
    private String path;
    private Map<String, String> queryParams;

    public SimpleUrlBuilder(String path) {
        this.path = path;

        this.queryParams = new HashMap<>();
    }

    /**
     * Adds query parameter.
     * Only value will be encoded.
     *
     * @param name
     *         parameter name
     * @param value
     *         parameter value
     */
    public SimpleUrlBuilder withQueryParam(String name, String value) {
        queryParams.put(name, value);
        return this;
    }

    /**
     * Builds URI.
     */
    public String build() {
        StringBuilder builder = new StringBuilder(path);
        if (!queryParams.isEmpty()) {
            builder.append('?');
            for (Map.Entry<String, String> queryParam : queryParams.entrySet()) {
                builder.append(queryParam.getKey())
                        .append('=')
                        .append(urlEncode(queryParam.getValue()))
                        .append('&');
            }
            builder.setLength(builder.length() - 1); // truncate last &
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return build();
    }

}
