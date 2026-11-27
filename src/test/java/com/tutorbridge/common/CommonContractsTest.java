package com.tutorbridge.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.tutorbridge.common.event.DomainEvents;
import com.tutorbridge.config.ClockConfig;
import com.tutorbridge.config.WebConfig;
import io.swagger.v3.oas.models.OpenAPI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class CommonContractsTest {

    @Test
    void clockConfigUsesConfiguredTimeZone() {
        Clock clock = new ClockConfig().clock("Asia/Dhaka");
        assertThat(clock.getZone()).isEqualTo(ZoneId.of("Asia/Dhaka"));
    }

    @Test
    void webConfigDefinesBearerOpenApiSpec() {
        OpenAPI api = new WebConfig().tutorBridgeOpenApi();
        assertThat(api.getInfo().getTitle()).isEqualTo("TutorBridge API");
        assertThat(api.getComponents().getSecuritySchemes()).containsKey(WebConfig.BEARER_SCHEME_NAME);
    }

    @Test
    void domainEventsNormalizeNullCollectionsAndStoreFields() {
        var published = new DomainEvents.PostPublished(1L, "Math Tutor", 2L, null);
        assertThat(published.matchedTutorIds()).isEmpty();

        var filled = new DomainEvents.PostFilled(1L, "Math Tutor", 2L, 3L, null);
        assertThat(filled.rejectedTutorIds()).isEmpty();

        Instant now = Instant.parse("2026-11-28T00:00:00Z");
        var action = new DomainEvents.AdminAction(9L, "VERIFY", "USER", 4L, "Verified tutor", now);
        assertThat(action.occurredAt()).isEqualTo(now);
    }

    @Test
    void baseEntityEqualityHandlesTransientInstances() {
        BaseEntity first = new DummyEntity();
        BaseEntity second = new DummyEntity();
        assertThat(first).isEqualTo(first);
        assertThat(first).isNotEqualTo(second);
        assertThat(first).isNotEqualTo(null);
        assertThat(first.hashCode()).isEqualTo(DummyEntity.class.hashCode());
    }

    static class DummyEntity extends BaseEntity {
    }
}
