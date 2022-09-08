package com.github.youssefwadie.todo.util;

import lombok.Getter;

import java.time.Instant;

@Getter
public class SimpleResponseBody {
    // Required
    private final int status;
    private final String description;

    // Optional
    private final String message;
    private final String timestamp;


    private SimpleResponseBody(Builder builder) {
        this.status = builder.status;
        this.description = builder.description;

        this.message = builder.message;
        this.timestamp = builder.timestamp.toString();
    }

    public static class Builder {
        private final int status;
        private final String description;

        private String message;
        private Instant timestamp;

        public Builder(int status, String description) {
            this.description = description;
            this.status = status;

            this.message = "";
            this.timestamp = Instant.now();
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public SimpleResponseBody build() {
            return new SimpleResponseBody(this);
        }
    }


}
