package com.github.youssefwadie.todo.security.util;

import java.time.Instant;

import lombok.Getter;

@Getter
public class SimpleResponseBody {
    // Required
    private final int status;
    private final String error;

    // Optional
    private final String message;
    private final String timestamp;



    private SimpleResponseBody(Builder builder) {
        this.status = builder.status;
        this.error = builder.error;

        this.message = builder.errorMessage;
        this.timestamp = builder.timestamp.toString();
    }

    public static class Builder {
        private final int status;
        private final String error;

        private String errorMessage;
        private Instant timestamp;

        public Builder(int status, String error) {
            this.error = error;
            this.status = status;

            this.errorMessage = "";
            this.timestamp = Instant.now();
        }

        public Builder setMessage(String message) {
            this.errorMessage = message;
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
