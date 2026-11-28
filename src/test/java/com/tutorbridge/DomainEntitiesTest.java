package com.tutorbridge;

import static org.assertj.core.api.Assertions.assertThat;

import com.tutorbridge.admin.AuditLog;
import com.tutorbridge.application.Application;
import com.tutorbridge.application.ApplicationStatus;
import com.tutorbridge.auth.RevokedToken;
import com.tutorbridge.catalog.Area;
import com.tutorbridge.catalog.Subject;
import com.tutorbridge.demo.DemoMode;
import com.tutorbridge.demo.DemoSession;
import com.tutorbridge.notification.Notification;
import com.tutorbridge.review.Review;
import com.tutorbridge.tuition.PostStatus;
import com.tutorbridge.tuition.TuitionPost;
import com.tutorbridge.user.GuardianProfile;
import com.tutorbridge.user.Role;
import com.tutorbridge.user.TutorProfile;
import com.tutorbridge.user.User;
import com.tutorbridge.user.UserStatus;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DomainEntitiesTest {

    @Test
    void userAndProfilesHoldStateAndAssociations() {
        User guardian = new User("Guardian", "g@example.com", "hash", "01711000001", Role.GUARDIAN);
        guardian.setName("Updated Guardian");
        guardian.setEmail("g2@example.com");
        guardian.setPasswordHash("hash2");
        guardian.setPhone("01711000002");
        guardian.setRole(Role.GUARDIAN);
        assertThat(guardian.isActive()).isTrue();
        assertThat(guardian.isSuspended()).isFalse();
        guardian.setStatus(UserStatus.SUSPENDED);
        assertThat(guardian.isSuspended()).isTrue();
        assertThat(guardian.getName()).isEqualTo("Updated Guardian");
        assertThat(guardian.getEmail()).isEqualTo("g2@example.com");
        assertThat(guardian.getPasswordHash()).isEqualTo("hash2");
        assertThat(guardian.getPhone()).isEqualTo("01711000002");
        assertThat(guardian.getRole()).isEqualTo(Role.GUARDIAN);
        assertThat(guardian.getStatus()).isEqualTo(UserStatus.SUSPENDED);

        Subject math = new Subject("Mathematics");
        math.setName("Higher Math");
        Area sonadanga = new Area("Sonadanga");
        sonadanga.setName("Boyra");
        assertThat(math.equals(math)).isTrue();
        assertThat(math.equals("other")).isFalse();
        assertThat(math.hashCode()).isEqualTo(Subject.class.hashCode());
        assertThat(sonadanga.equals(sonadanga)).isTrue();
        assertThat(sonadanga.equals("other")).isFalse();
        assertThat(sonadanga.hashCode()).isEqualTo(Area.class.hashCode());

        GuardianProfile gp = new GuardianProfile(guardian);
        gp.setUser(guardian);
        gp.setHomeArea(sonadanga);
        gp.setAddressLine("Road 5, Boyra");
        assertThat(gp.getUser()).isSameAs(guardian);
        assertThat(gp.getHomeArea()).isSameAs(sonadanga);
        assertThat(gp.getAddressLine()).isEqualTo("Road 5, Boyra");

        User tutor = new User("Tutor", "t@example.com", "hash", "01711000003", Role.TUTOR);
        TutorProfile tp = new TutorProfile(tutor);
        tp.setUser(tutor);
        tp.setInstitution("KUET");
        tp.setBio("5 years experience");
        tp.setSubjects(Set.of(math));
        tp.setPreferredAreas(Set.of(sonadanga));
        tp.setClassLevels(Set.of("Class 9"));
        tp.setMediums(Set.of("Bangla"));
        tp.setSalaryMin(4000);
        tp.setSalaryMax(7000);
        tp.setVerified(true);
        tp.setRatingAvg(4.5);
        tp.setRatingCount(2);

        assertThat(tp.getUser()).isSameAs(tutor);
        assertThat(tp.getInstitution()).isEqualTo("KUET");
        assertThat(tp.getBio()).isEqualTo("5 years experience");
        assertThat(tp.getSubjects()).containsExactly(math);
        assertThat(tp.getPreferredAreas()).containsExactly(sonadanga);
        assertThat(tp.getClassLevels()).containsExactly("Class 9");
        assertThat(tp.getMediums()).containsExactly("Bangla");
        assertThat(tp.getSalaryMin()).isEqualTo(4000);
        assertThat(tp.getSalaryMax()).isEqualTo(7000);
        assertThat(tp.isVerified()).isTrue();
        assertThat(tp.getRatingAvg()).isEqualTo(4.5);
        assertThat(tp.getRatingCount()).isEqualTo(2);
    }

    @Test
    void postApplicationDemoReviewNotificationAuditAndTokenHoldFields() {
        User guardian = new User("G", "g@test.com", "h", "017", Role.GUARDIAN);
        User tutor = new User("T", "t@test.com", "h", "018", Role.TUTOR);
        User admin = new User("A", "a@test.com", "h", null, Role.ADMIN);
        Area area = new Area("Nirala");
        Subject physics = new Subject("Physics");

        TuitionPost post = new TuitionPost(
                guardian, area, Set.of(physics), "Physics Tutor", "Class 10", "English", 6000, 4, "Desc");
        post.setGuardian(guardian);
        post.setArea(area);
        post.setSubjects(Set.of(physics));
        post.setTitle("Updated Title");
        post.setClassLevel("Class 11");
        post.setMedium("Bangla");
        post.setSalary(6500);
        post.setDaysPerWeek(5);
        post.setDescription("Updated");
        post.setStatus(PostStatus.IN_REVIEW);

        assertThat(post.getGuardian()).isSameAs(guardian);
        assertThat(post.getArea()).isSameAs(area);
        assertThat(post.getSubjects()).containsExactly(physics);
        assertThat(post.getTitle()).isEqualTo("Updated Title");
        assertThat(post.getClassLevel()).isEqualTo("Class 11");
        assertThat(post.getMedium()).isEqualTo("Bangla");
        assertThat(post.getSalary()).isEqualTo(6500);
        assertThat(post.getDaysPerWeek()).isEqualTo(5);
        assertThat(post.getDescription()).isEqualTo("Updated");
        assertThat(post.getStatus()).isEqualTo(PostStatus.IN_REVIEW);

        Application application = new Application(post, tutor, "Cover", 85);
        application.setPost(post);
        application.setTutor(tutor);
        application.setCoverNote("Updated cover");
        application.setMatchScore(92);
        application.transitionTo(ApplicationStatus.SHORTLISTED);
        assertThat(application.getPost()).isSameAs(post);
        assertThat(application.getTutor()).isSameAs(tutor);
        assertThat(application.getCoverNote()).isEqualTo("Updated cover");
        assertThat(application.getMatchScore()).isEqualTo(92);
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.SHORTLISTED);

        Instant when = Instant.parse("2026-12-01T10:00:00Z");
        DemoSession demo = new DemoSession(application, when, DemoMode.ONLINE, "https://meet.example.com", "Bring notes");
        demo.setApplication(application);
        demo.setScheduledAt(when.plusSeconds(3600));
        demo.setMode(DemoMode.IN_PERSON);
        demo.setLocation("Nirala Road 2");
        demo.setNotes("Updated notes");
        assertThat(demo.getApplication()).isSameAs(application);
        assertThat(demo.getScheduledAt()).isEqualTo(when.plusSeconds(3600));
        assertThat(demo.getMode()).isEqualTo(DemoMode.IN_PERSON);
        assertThat(demo.getLocation()).isEqualTo("Nirala Road 2");
        assertThat(demo.getNotes()).isEqualTo("Updated notes");

        Review review = new Review(post, guardian, tutor, 5, "Great tutor");
        review.setPost(post);
        review.setGuardian(guardian);
        review.setTutor(tutor);
        review.setRating(4);
        review.setComment("Very good");
        assertThat(review.getPost()).isSameAs(post);
        assertThat(review.getGuardian()).isSameAs(guardian);
        assertThat(review.getTutor()).isSameAs(tutor);
        assertThat(review.getRating()).isEqualTo(4);
        assertThat(review.getComment()).isEqualTo("Very good");

        Notification notification = new Notification(tutor, "SHORTLISTED", "You were shortlisted", "#applications");
        notification.setRecipient(tutor);
        notification.setType("ACCEPTED");
        notification.setMessage("You were accepted");
        notification.setLink("#post/1");
        notification.setRead(true);
        assertThat(notification.getRecipient()).isSameAs(tutor);
        assertThat(notification.getType()).isEqualTo("ACCEPTED");
        assertThat(notification.getMessage()).isEqualTo("You were accepted");
        assertThat(notification.getLink()).isEqualTo("#post/1");
        assertThat(notification.isRead()).isTrue();

        AuditLog audit = new AuditLog(admin, "VERIFY_TUTOR", "USER", 2L, "Verified KUET ID");
        assertThat(audit.getAdmin()).isSameAs(admin);
        assertThat(audit.getAction()).isEqualTo("VERIFY_TUTOR");
        assertThat(audit.getTargetType()).isEqualTo("USER");
        assertThat(audit.getTargetId()).isEqualTo(2L);
        assertThat(audit.getDetails()).isEqualTo("Verified KUET ID");

        RevokedToken token = new RevokedToken("jti-123", when);
        assertThat(token.getJti()).isEqualTo("jti-123");
        assertThat(token.getExpiresAt()).isEqualTo(when);
        assertThat(token.equals(token)).isTrue();
        assertThat(token.equals(new RevokedToken("jti-123", when))).isTrue();
        assertThat(token.equals("other")).isFalse();
        assertThat(token.hashCode()).isEqualTo(RevokedToken.class.hashCode());
    }
}
