package io.micronaut.samples.petclinic.model;

/**
 * Minimal account state needed by the authentication provider.
 */
public sealed interface UserState permits User {
    /**
     * @return the login name
     */
    String getUsername();

    /**
     * @return the encoded password hash
     */
    String getPassword();

    /**
     * @return whether the user is allowed to authenticate
     */
    boolean isEnabled();

    /**
     * @return whether the account has expired
     */
    boolean isAccountExpired();

    /**
     * @return whether the account is locked
     */
    boolean isAccountLocked();

    /**
     * @return whether the password has expired
     */
    boolean isPasswordExpired();
}
