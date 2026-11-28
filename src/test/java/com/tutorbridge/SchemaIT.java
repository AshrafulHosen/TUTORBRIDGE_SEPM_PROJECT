package com.tutorbridge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tutorbridge.admin.AuditLog;
import com.tutorbridge.admin.AuditLogRepository;
import com.tutorbridge.application.Application;
import com.tutorbridge.application.ApplicationRepository;
import com.tutorbridge.application.ApplicationStatus;
import com.tutorbridge.auth.RevokedToken;
import com.tutorbridge.auth.RevokedTokenRepository;
import com.tutorbridge.catalog.Area;
import com.tutorbridge.catalog.AreaRepository;
import com.tutorbridge.catalog.Subject;
import com.tutorbridge.catalog.SubjectRepository;
import com.tutorbridge.demo.DemoMode;
import com.tutorbridge.demo.DemoSession;
import com.tutorbridge.demo.DemoSessionRepository;
import com.tutorbridge.notification.Notification;
import com.tutorbridge.notification.NotificationRepository;
import com.tutorbridge.review.Review;
import com.tutorbridge.review.ReviewRepository;
import com.tutorbridge.tuition.PostStatus;
import com.tutorbridge.tuition.TuitionPost;
import com.tutorbridge.tuition.TuitionPostRepository;
import com.tutorbridge.user.GuardianProfile;
import com.tutorbridge.user.GuardianProfileRepository;
import com.tutorbridge.user.Role;
import com.tutorbridge.user.TutorProfile;
import com.tutorbridge.user.TutorProfileRepository;
import com.tutorbridge.user.User;
import com.tutorbridge.user.UserRepository;
import com.tutorbridge.user.UserStatus;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Runs the Flyway migrations on a real PostgreSQL 16 and lets Hibernate validate the schema
 * against the entities ({@code ddl-auto: validate}): if the context starts, they agree.
 */
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Testcontainers
class SchemaIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired TestEntityManager em;
    @Autowired UserRepository users;
    @Autowired TutorProfileRepository tutorProfiles;
    @Autowired GuardianProfileRepository guardianProfiles;
    @Autowired SubjectRepository subjects;
    @Autowired AreaRepository areas;
    @Autowired TuitionPostRepository posts;
    @Autowired ApplicationRepository applications;
    @Autowired DemoSessionRepository demos;
    @Autowired ReviewRepository reviews;
    @Autowired NotificationRepository notifications;
    @Autowired AuditLogRepository auditLogs;
    @Autowired RevokedTokenRepository revokedTokens;

    @Test
    void flywaySeedsTheLookupTables() {
        assertThat(subjects.findAllByOrderByNameAsc()).extracting(Subject::getName).containsExactly(
                "Chemistry", "English", "Higher Math", "ICT", "Mathematics", "Physics", "Science");
        assertThat(areas.findAllByOrderByNameAsc()).extracting(Area::getName).containsExactly(
                "Boyra", "Daulatpur", "Fulbarigate", "Gollamari", "Khalishpur", "Nirala", "Sonadanga");
        assertThat(subjects.findByNameIn(List.of("Physics", "ICT", "Unknown"))).hasSize(2);
        assertThat(areas.findByName("Nirala")).isPresent();
    }

    @Test
    void usersAreFoundByEmailIgnoringCase() {
        User guardian = users.save(new User("  Rahim@Example.com ", "{hash}", "Rahim", "01711000111", Role.GUARDIAN));

        assertThat(guardian.getEmail()).isEqualTo("rahim@example.com");
        assertThat(guardian.getCreatedAt()).isNotNull();
        assertThat(users.findByEmailIgnoreCase("RAHIM@example.COM")).contains(guardian);
        assertThat(users.existsByEmailIgnoreCase("rahim@EXAMPLE.com")).isTrue();
        assertThat(users.countByRole(Role.GUARDIAN)).isEqualTo(1);
    }

    @Test
    void tutorProfileKeepsItsManyToManySetsAfterReload() {
        User tutor = users.save(new User("tutor@example.com", "{hash}", "Karim", "01811000222", Role.TUTOR));
        TutorProfile profile = new TutorProfile(tutor);
        profile.updateDetails("KUET", "Patient teacher", 5000, 8000);
        profile.replaceSubjects(List.of(subject("Mathematics"), subject("Physics")));
        profile.replacePreferredAreas(List.of(area("Sonadanga")));
        profile.replaceClassLevels(List.of("Class 8", "Class 9"));
        profile.replaceMediums(List.of("Bangla"));
        tutorProfiles.save(profile);
        em.flush();
        em.clear();

        TutorProfile reloaded = tutorProfiles.findByUserId(tutor.getId()).orElseThrow();
        assertThat(reloaded.getSubjects()).extracting(Subject::getName)
                .containsExactlyInAnyOrder("Mathematics", "Physics");
        assertThat(reloaded.getPreferredAreas()).extracting(Area::getName).containsExactly("Sonadanga");
        assertThat(reloaded.getClassLevels()).containsExactlyInAnyOrder("Class 8", "Class 9");
        assertThat(reloaded.getMediums()).containsExactly("Bangla");
        assertThat(reloaded.getSalaryMin()).isEqualTo(5000);
        assertThat(reloaded.isVerified()).isFalse();
        assertThat(tutorProfiles.findByUserStatus(UserStatus.ACTIVE)).hasSize(1);
        assertThat(tutorProfiles.countByVerifiedFalse()).isEqualTo(1);
    }

    @Test
    void guardianProfileAndPostKeepTheirRelationships() {
        User guardian = users.save(new User("g@example.com", "{hash}", "Guardian", null, Role.GUARDIAN));
        GuardianProfile profile = new GuardianProfile(guardian);
        profile.updateDetails(area("Boyra"), "House 4, Road 2");
        guardianProfiles.save(profile);
        TuitionPost post = posts.save(new TuitionPost(guardian, area("Boyra"),
                Set.of(subject("Mathematics"), subject("ICT")), "Class 8 Math and ICT",
                "Class 8", "Bangla", 5000, 3, null));
        em.flush();
        em.clear();

        assertThat(guardianProfiles.findByUserId(guardian.getId()).orElseThrow().getHomeArea().getName())
                .isEqualTo("Boyra");
        TuitionPost reloaded = posts.findByIdAndGuardianId(post.getId(), guardian.getId()).orElseThrow();
        assertThat(reloaded.getSubjects()).extracting(Subject::getName)
                .containsExactlyInAnyOrder("Mathematics", "ICT");
        assertThat(reloaded.getStatus()).isEqualTo(PostStatus.OPEN);
        assertThat(reloaded.isOwnedBy(guardian.getId())).isTrue();
        assertThat(posts.findByGuardianIdOrderByCreatedAtDesc(guardian.getId())).hasSize(1);
        assertThat(posts.countByStatus(PostStatus.OPEN)).isEqualTo(1);
    }

    @Test
    void uniqueConstraintRejectsASecondApplicationToTheSamePost() {
        TuitionPost post = post();
        User tutor = users.save(new User("t@example.com", "{hash}", "Tutor", null, Role.TUTOR));
        applications.saveAndFlush(new Application(post, tutor, "Hi", 80));

        assertThat(applications.existsByPostIdAndTutorId(post.getId(), tutor.getId())).isTrue();
        assertThatThrownBy(() -> applications.saveAndFlush(new Application(post, tutor, "Again", 80)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void checkConstraintRejectsAReviewRatingOfSix() {
        TuitionPost post = post();
        User tutor = users.save(new User("t@example.com", "{hash}", "Tutor", null, Role.TUTOR));

        assertThatThrownBy(() -> reviews.saveAndFlush(new Review(post, post.getGuardian(), tutor, 6, "Too good")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void applicationDemoAndReviewRoundTrip() {
        TuitionPost post = post();
        User tutor = users.save(new User("t@example.com", "{hash}", "Tutor", null, Role.TUTOR));
        Application application = applications.save(new Application(post, tutor, null, 90));
        assertThat(application.transitionTo(ApplicationStatus.SHORTLISTED)).isEqualTo(ApplicationStatus.APPLIED);
        Instant at = Instant.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        demos.save(new DemoSession(application, at, DemoMode.IN_PERSON, "Boyra", null));
        reviews.save(new Review(post, post.getGuardian(), tutor, 5, "Great"));
        em.flush();
        em.clear();

        assertThat(applications.findByPostIdAndStatusOrderByMatchScoreDesc(post.getId(), ApplicationStatus.SHORTLISTED))
                .hasSize(1);
        assertThat(applications.findByIdAndTutorId(application.getId(), tutor.getId())).isPresent();
        assertThat(demos.findByApplicationId(application.getId()).orElseThrow().getScheduledAt()).isEqualTo(at);
        assertThat(demos.findByApplicationPostGuardianIdOrderByScheduledAtAsc(post.getGuardian().getId())).hasSize(1);
        assertThat(demos.findByApplicationTutorIdOrderByScheduledAtAsc(tutor.getId())).hasSize(1);
        assertThat(reviews.existsByPostId(post.getId())).isTrue();
        assertThat(reviews.findByTutorIdOrderByCreatedAtDesc(tutor.getId()))
                .singleElement().extracting(Review::getRating).isEqualTo(5);
    }

    @Test
    void notificationsAuditLogsAndRevokedTokens() {
        User guardian = users.save(new User("g@example.com", "{hash}", "Guardian", null, Role.GUARDIAN));
        User admin = users.save(new User("admin@example.com", "{hash}", "Admin", null, Role.ADMIN));
        notifications.save(new Notification(guardian, "APPLICATION_SUBMITTED", "Karim applied", "#post/1"));
        notifications.save(new Notification(guardian, "APPLICATION_SUBMITTED", "Rina applied", "#post/1"));
        auditLogs.save(new AuditLog(admin, "SUSPEND_USER", "USER", guardian.getId(), "Spam"));
        Instant now = Instant.now();
        revokedTokens.save(new RevokedToken("expired-jti", now.minusSeconds(60)));
        revokedTokens.save(new RevokedToken("live-jti", now.plusSeconds(3600)));
        em.flush();

        assertThat(notifications.countByRecipientIdAndReadFalse(guardian.getId())).isEqualTo(2);
        assertThat(notifications.markAllRead(guardian.getId())).isEqualTo(2);
        assertThat(notifications.countByRecipientIdAndReadFalse(guardian.getId())).isZero();
        assertThat(notifications.findTop30ByRecipientIdOrderByCreatedAtDesc(guardian.getId()))
                .hasSize(2).allMatch(Notification::isRead);
        assertThat(auditLogs.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 10)).getContent())
                .singleElement().extracting(AuditLog::getAction).isEqualTo("SUSPEND_USER");
        assertThat(revokedTokens.deleteExpired(now)).isEqualTo(1);
        assertThat(revokedTokens.existsById("live-jti")).isTrue();
        assertThat(revokedTokens.existsById("expired-jti")).isFalse();
    }

    private TuitionPost post() {
        User guardian = users.save(new User("guardian@example.com", "{hash}", "Guardian", null, Role.GUARDIAN));
        return posts.save(new TuitionPost(guardian, area("Sonadanga"), Set.of(subject("Physics")),
                "Class 9 Physics", "Class 9", "English", 6000, 4, "Evenings"));
    }

    private Subject subject(String name) {
        return subjects.findByName(name).orElseThrow();
    }

    private Area area(String name) {
        return areas.findByName(name).orElseThrow();
    }
}
