package com.tutorbridge.common;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void notFoundReturns404ProblemDetail() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Post not found."))
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    void conflictReturns409ProblemDetail() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail").value("You have already applied to this post."));
    }

    @Test
    void invalidTransitionReturns409ProblemDetail() throws Exception {
        mockMvc.perform(get("/test/transition"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("This application is ACCEPTED, so it can't be rejected."));
    }

    @Test
    void badRequestReturns400ProblemDetail() throws Exception {
        mockMvc.perform(get("/test/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Unknown subject: Quantum"));
    }

    @Test
    void tooManyRequestsReturns429WithRetryAfterHeader() throws Exception {
        mockMvc.perform(get("/test/rate-limit"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "45"))
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.title").value("Too Many Requests"))
                .andExpect(jsonPath("$.detail").value("Too many login attempts. Please wait and try again."));
    }

    @Test
    void validationErrorsReturn400WithFieldMap() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"salary\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors.title").value("Title is required"))
                .andExpect(jsonPath("$.errors.salary").value("Salary must be at least 1"));
    }

    @Test
    void malformedJsonReturns400WithoutLeakingInternalDetails() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Malformed Request"))
                .andExpect(jsonPath("$.detail").value("The request body could not be read. Please check your input format."));
    }

    @Test
    void authenticationExceptionReturns401() throws Exception {
        mockMvc.perform(get("/test/auth"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("Please sign in to continue."));
    }

    @Test
    void accessDeniedReturns403() throws Exception {
        mockMvc.perform(get("/test/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.detail").value("You do not have permission to perform this action."));
    }

    @Test
    void dataIntegrityViolationReturns409WithoutSqlDetails() throws Exception {
        mockMvc.perform(get("/test/integrity"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("This operation conflicts with existing data."))
                .andExpect(jsonPath("$.detail", not(containsString("uk_users_email"))));
    }

    @Test
    void constraintViolationReturns400() throws Exception {
        mockMvc.perform(get("/test/constraint"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation Failed"));
    }

    @Test
    void unexpectedExceptionReturns500WithoutLeakingInternalMessage() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred. Please try again later."))
                .andExpect(jsonPath("$.detail", not(containsString("jdbc:postgresql"))));
    }

    record SamplePayload(
            @NotBlank(message = "Title is required") String title,
            @Min(value = 1, message = "Salary must be at least 1") int salary) {
    }

    @RestController
    @RequestMapping("/test")
    static class ThrowingController {

        @GetMapping("/not-found")
        void notFound() {
            throw new NotFoundException("Post not found.");
        }

        @GetMapping("/conflict")
        void conflict() {
            throw new ConflictException("You have already applied to this post.");
        }

        @GetMapping("/transition")
        void transition() {
            throw new InvalidTransitionException("This application is ACCEPTED, so it can't be rejected.");
        }

        @GetMapping("/bad-request")
        void badRequest() {
            throw new BadRequestException("Unknown subject: Quantum");
        }

        @GetMapping("/rate-limit")
        void rateLimit() {
            throw new TooManyRequestsException("Too many login attempts. Please wait and try again.", 45);
        }

        @PostMapping("/validate")
        void validate(@Valid @RequestBody SamplePayload payload) {
        }

        @GetMapping("/auth")
        void auth() {
            throw new BadCredentialsException("Bad credentials");
        }

        @GetMapping("/forbidden")
        void forbidden() {
            throw new AccessDeniedException("Denied");
        }

        @GetMapping("/integrity")
        void integrity() {
            throw new DataIntegrityViolationException("duplicate key violates uk_users_email");
        }

        @GetMapping("/constraint")
        void constraint() {
            throw new ConstraintViolationException("Invalid parameter", Set.of());
        }

        @GetMapping("/unexpected")
        void unexpected() {
            throw new IllegalStateException("Connection failed to jdbc:postgresql://secret-host/db");
        }
    }
}
