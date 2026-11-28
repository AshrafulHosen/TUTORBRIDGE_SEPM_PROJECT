package com.tutorbridge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tutorbridge.application.Application;
import com.tutorbridge.application.ApplicationRepository;
import com.tutorbridge.catalog.Area;
import com.tutorbridge.catalog.AreaRepository;
import com.tutorbridge.catalog.Subject;
import com.tutorbridge.catalog.SubjectRepository;
import com.tutorbridge.review.Review;
import com.tutorbridge.review.ReviewRepository;
import com.tutorbridge.tuition.TuitionPost;
import com.tutorbridge.tuition.TuitionPostRepository;
import com.tutorbridge.user.Role;
import com.tutorbridge.user.TutorProfile;
import com.tutorbridge.user.TutorProfileRepository;
import com.tutorbridge.user.User;
import com.tutorbridge.user.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class SchemaIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private TuitionPostRepository tuitionPostRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void flywaySeedsSubjectsAndAreasAndHibernateValidatesSchema() {
        assertThat(subjectRepository.findAllByOrderByNameAsc())
                .extracting(Subject::getName)
                .containsExactly(
                        "Chemistry",
                        "English",
                        "Higher Math",
                        "ICT",
                        "Mathematics",
                        "Physics",
                        "Science");

        assertThat(areaRepository.findAllByOrderByNameAsc())
                .extracting(Area::getName)
                .containsExactly(
                        "Boyra",
                        "Daulatpur",
                        "Fulbarigate",
                        "Gollamari",
                        "Khalishpur",
                        "Nirala",
                        "Sonadanga");
    }

    @Test
    void savingTutorWithTwoSubjectsAndPreferredAreaPersistsManyToManySets() {
        User tutorUser = userRepository.save(
                new User("Tariq Hasan", "tariq@example.com", "$2a$12$hash", "01711000111", Role.TUTOR));

        Subject math = subjectRepository.findByNameIgnoreCase("Mathematics").orElseThrow();
        Subject physics = subjectRepository.findByNameIgnoreCase("Physics").orElseThrow();
        Area sonadanga = areaRepository.findByNameIgnoreCase("Sonadanga").orElseThrow();

        TutorProfile profile = new TutorProfile(tutorUser);
        profile.setInstitution("KUET");
        profile.setSubjects(Set.of(math, physics));
        profile.setPreferredAreas(Set.of(sonadanga));
        profile.setClassLevels(Set.of("Class 9", "Class 10"));
        profile.setMediums(Set.of("Bangla"));
        profile.setSalaryMin(5000);
        profile.setSalaryMax(8000);

        TutorProfile saved = tutorProfileRepository.save(profile);
        entityManager.flush();
        entityManager.clear();

        TutorProfile reloaded = tutorProfileRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getSubjects())
                .extracting(Subject::getName)
                .containsExactlyInAnyOrder("Mathematics", "Physics");
        assertThat(reloaded.getPreferredAreas())
                .extracting(Area::getName)
                .containsExactly("Sonadanga");
        assertThat(reloaded.getClassLevels()).containsExactlyInAnyOrder("Class 9", "Class 10");
        assertThat(reloaded.getMediums()).containsExactly("Bangla");
    }

    @Test
    void duplicateApplicationForSamePostAndTutorIsRejectedByUniqueConstraint() {
        User guardian = userRepository.save(
                new User("Farhana Akter", "farhana@example.com", "$2a$12$hash", "01711000222", Role.GUARDIAN));
        User tutor = userRepository.save(
                new User("Nabil Rahman", "nabil@example.com", "$2a$12$hash", "01711000333", Role.TUTOR));
        Subject math = subjectRepository.findByNameIgnoreCase("Mathematics").orElseThrow();
        Area nirala = areaRepository.findByNameIgnoreCase("Nirala").orElseThrow();

        TuitionPost post = tuitionPostRepository.save(new TuitionPost(
                guardian,
                nirala,
                Set.of(math),
                "Class 9 Math Tutor",
                "Class 9",
                "Bangla",
                5000,
                4,
                "Need experienced tutor"));

        applicationRepository.saveAndFlush(new Application(post, tutor, "I can teach math well.", 90));

        assertThatThrownBy(() ->
                        applicationRepository.saveAndFlush(new Application(post, tutor, "Second attempt", 90)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void reviewRatingSixIsRejectedByCheckConstraint() {
        User guardian = userRepository.save(
                new User("Sadia Islam", "sadia@example.com", "$2a$12$hash", "01711000444", Role.GUARDIAN));
        User tutor = userRepository.save(
                new User("Rafiq Ahmed", "rafiq@example.com", "$2a$12$hash", "01711000555", Role.TUTOR));
        Subject english = subjectRepository.findByNameIgnoreCase("English").orElseThrow();
        Area boyra = areaRepository.findByNameIgnoreCase("Boyra").orElseThrow();

        TuitionPost post = tuitionPostRepository.save(new TuitionPost(
                guardian,
                boyra,
                Set.of(english),
                "Class 8 English Tutor",
                "Class 8",
                "English",
                6000,
                3,
                "Grammar and writing focus"));
        entityManager.flush();

        assertThatThrownBy(() ->
                        reviewRepository.saveAndFlush(new Review(post, guardian, tutor, 6, "Out of range")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
