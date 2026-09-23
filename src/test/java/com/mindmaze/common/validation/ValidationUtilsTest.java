package com.mindmaze.common.validation;

import com.mindmaze.common.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidationUtilsTest {

    @Test
    @DisplayName("Should accept valid usernames and reject invalid formats")
    void shouldValidateUsernames() {
        assertThatCode(() -> ValidationUtils.validateUsername("valid_user123"))
                .doesNotThrowAnyException();

        assertThatThrownBy(() -> ValidationUtils.validateUsername("ab")) // Too short
                .isInstanceOf(ValidationException.class);

        assertThatThrownBy(() -> ValidationUtils.validateUsername("user@invalid!")) // Disallowed chars
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Should accept valid emails and reject malformed ones")
    void shouldValidateEmails() {
        assertThatCode(() -> ValidationUtils.validateEmail("alex@example.com"))
                .doesNotThrowAnyException();

        assertThatThrownBy(() -> ValidationUtils.validateEmail("not-an-email"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Should enforce minimum and maximum time spent constraints")
    void shouldValidateTimeSpent() {
        assertThatCode(() -> ValidationUtils.validateTimeSpent(5000))
                .doesNotThrowAnyException();

        assertThatThrownBy(() -> ValidationUtils.validateTimeSpent(200)) // Sub-500ms
                .isInstanceOf(ValidationException.class);

        assertThatThrownBy(() -> ValidationUtils.validateTimeSpent(700_000)) // Over 10 mins
                .isInstanceOf(ValidationException.class);
    }
}
