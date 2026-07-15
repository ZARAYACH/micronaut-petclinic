package io.micronaut.samples.petclinic.model;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Application user persisted for session-based authentication.
 *
 * @param id the generated user id
 * @param username the unique login name, represented as an email address
 * @param password the encoded password hash
 * @param enabled whether the user is allowed to authenticate
 * @param expired whether the user account has expired
 * @param locked whether the user account is locked
 * @param passwordExpired whether the user's password has expired
 */
@MappedEntity("USER")
public record User(
        @Id
        @GeneratedValue
        @MappedProperty("ID")
        Integer id,

        @MappedProperty("USERNAME")
        @NotBlank
        @Email
        String username,

        @MappedProperty("PASSWORD")
        @NotBlank
        String password,

        @MappedProperty("ENABLED")
        boolean enabled,

        @MappedProperty("EXPIRED")
        boolean expired,

        @MappedProperty("LOCKED")
        boolean locked,

        @MappedProperty("PASSWORD_EXPIRED")
        boolean passwordExpired
) implements BaseEntity, UserState {
    /**
     * Constructor used by Micronaut Data.
     */
    User() {
        this(null, null, null, false, false, false, false);
    }

    /**
     * Creates an enabled user with a non-expired, unlocked account.
     *
     * @param username the login name
     * @param password the encoded password hash
     */
    public User(String username, String password) {
        this(null, username, password, true, false, false, false);
    }

    /**
     * @return the login name used by the authentication provider
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * @return the encoded password hash used for password matching
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * @return {@code true} when the user can authenticate
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return {@code true} when the account has expired
     */
    @Override
    public boolean isAccountExpired() {
        return expired;
    }

    /**
     * @return {@code true} when the account is locked
     */
    @Override
    public boolean isAccountLocked() {
        return locked;
    }

    /**
     * @return {@code true} when the password has expired
     */
    @Override
    public boolean isPasswordExpired() {
        return passwordExpired;
    }
}
