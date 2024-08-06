package com.atoss.idea.management.system.security.token;

public interface TokenConfig {

    /**
     * Provides the expiry time of the token in milliseconds.
     * @return The token's expiry time in milliseconds.
     */
    long getExpiryMs();

    /**
     * Provides the expiry time of the token in seconds.
     * @return The token's expiry time in seconds.
     */
    long getExpirySeconds();

    /**
     * The path to be used in the cookie responsible for the token.
     * @return The relative path for the token's cookie.
     */
    String getPath();

    /**
     * Provides the type of the token (e.g. accessToken, refreshToken)
     * @return The type of the token.
     */
    String getType();
}
