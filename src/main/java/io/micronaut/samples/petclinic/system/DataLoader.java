package io.micronaut.samples.petclinic.system;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.samples.petclinic.model.Owner;
import io.micronaut.samples.petclinic.model.Pet;
import io.micronaut.samples.petclinic.model.PetType;
import io.micronaut.samples.petclinic.model.Role;
import io.micronaut.samples.petclinic.model.Speciality;
import io.micronaut.samples.petclinic.model.User;
import io.micronaut.samples.petclinic.model.UserRole;
import io.micronaut.samples.petclinic.model.UserRoleId;
import io.micronaut.samples.petclinic.model.Vet;
import io.micronaut.samples.petclinic.model.VetSpeciality;
import io.micronaut.samples.petclinic.model.Visit;
import io.micronaut.samples.petclinic.repository.OwnerRepository;
import io.micronaut.samples.petclinic.repository.PetRepository;
import io.micronaut.samples.petclinic.repository.PetTypeRepository;
import io.micronaut.samples.petclinic.repository.RoleJdbcRepository;
import io.micronaut.samples.petclinic.repository.SpecialityRepository;
import io.micronaut.samples.petclinic.repository.UserJdbcRepository;
import io.micronaut.samples.petclinic.repository.UserRoleJdbcRepository;
import io.micronaut.samples.petclinic.repository.VetRepository;
import io.micronaut.samples.petclinic.repository.VetSpecialityRepository;
import io.micronaut.samples.petclinic.repository.VisitRepository;
import io.micronaut.samples.petclinic.utils.PasswordEncoder;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

/**
 * Populates the database with sample data on application startup.
 */
@Singleton
@Requires(property = "petclinic.sample-data.enabled", value = "true", defaultValue = "true")
public class DataLoader implements ApplicationEventListener<StartupEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DataLoader.class);

    private final VetRepository vetRepository;
    private final SpecialityRepository specialityRepository;
    private final PetTypeRepository petTypeRepository;
    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;
    private final VisitRepository visitRepository;
    private final VetSpecialityRepository vetSpecialityRepository;
    private final RoleJdbcRepository roleJdbcRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleJdbcRepository userRoleJdbcRepository;
    private final UserJdbcRepository userJdbcRepository;


    /**
     * Creates the data loader with the repositories used to seed sample data.
     *
     * @param vetRepository repository for vets
     * @param specialityRepository repository for specialities
     * @param petTypeRepository repository for pet types
     * @param ownerRepository repository for owners
     * @param petRepository repository for pets
     * @param visitRepository repository for visits
     * @param vetSpecialityRepository repository for vet-speciality join rows
     * @param roleJdbcRepository repository for sample security roles
     * @param passwordEncoder encoder for sample user passwords
     * @param userRoleJdbcRepository repository for sample user-role assignments
     * @param userJdbcRepository repository for sample users
     */
    public DataLoader(VetRepository vetRepository,
                      SpecialityRepository specialityRepository,
                      PetTypeRepository petTypeRepository,
                      OwnerRepository ownerRepository,
                      PetRepository petRepository,
                      VisitRepository visitRepository,
                      VetSpecialityRepository vetSpecialityRepository,
                      RoleJdbcRepository roleJdbcRepository,
                      PasswordEncoder passwordEncoder,
                      UserRoleJdbcRepository userRoleJdbcRepository,
                      UserJdbcRepository userJdbcRepository) {
        this.vetRepository = vetRepository;
        this.specialityRepository = specialityRepository;
        this.petTypeRepository = petTypeRepository;
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
        this.visitRepository = visitRepository;
        this.vetSpecialityRepository = vetSpecialityRepository;
        this.roleJdbcRepository = roleJdbcRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRoleJdbcRepository = userRoleJdbcRepository;
        this.userJdbcRepository = userJdbcRepository;
    }

    /**
     * Seeds the database when the application starts.
     *
     * @param event the startup event
     */
    @Override
    @Transactional
    public void onApplicationEvent(StartupEvent event) {
        LOG.info("Loading sample data...");
        loadData();
        LOG.info("Sample data loaded successfully.");
    }

    private void loadData() {
        // create roles
        Role roleStaff = createRole(Role.Authority.ROLE_STAFF);
        Role roleAdmin = createRole(Role.Authority.ROLE_ADMIN);

        createUser("admin@example.com", "password123", roleAdmin);
        createUser("staff@example.com", "password123", roleStaff);


        // Create specialities
        Speciality radiology = createSpeciality("radiology");
        Speciality surgery = createSpeciality("surgery");
        Speciality dentistry = createSpeciality("dentistry");

        // Create vets
        Vet james = createVet("James", "Carter");
        Vet helen = createVet("Helen", "Leary");
        Vet linda = createVet("Linda", "Douglas", surgery, dentistry);
        Vet rafael = createVet("Rafael", "Ortega", surgery);
        Vet henry = createVet("Henry", "Stevens", radiology);
        Vet sharon = createVet("Sharon", "Jenkins");

        // Create pet types
        PetType cat = createPetType("cat");
        PetType dog = createPetType("dog");
        PetType lizard = createPetType("lizard");
        PetType snake = createPetType("snake");
        PetType bird = createPetType("bird");
        PetType hamster = createPetType("hamster");

        // Create owners and their pets
        Owner george = createOwner("George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
        Pet leo = createPet("Leo", LocalDate.of(2010, 9, 7), cat, george);

        Owner betty = createOwner("Betty", "Davis", "638 Cardinal Ave.", "Sun Prairie", "6085551749");
        Pet basil = createPet("Basil", LocalDate.of(2012, 8, 6), hamster, betty);

        Owner eduardo = createOwner("Eduardo", "Rodriquez", "2693 Commerce St.", "McFarland", "6085558763");
        Pet jewel = createPet("Jewel", LocalDate.of(2010, 3, 7), dog, eduardo);
        Pet rosy = createPet("Rosy", LocalDate.of(2011, 4, 17), dog, eduardo);

        Owner harold = createOwner("Harold", "Davis", "563 Friendly St.", "Windsor", "6085553198");
        Pet iggy = createPet("Iggy", LocalDate.of(2010, 11, 30), lizard, harold);

        Owner peter = createOwner("Peter", "McTavish", "2387 S. Fair Way", "Madison", "6085552765");
        Pet george2 = createPet("George", LocalDate.of(2010, 1, 20), snake, peter);

        Owner jean = createOwner("Jean", "Coleman", "105 N. Lake St.", "Monona", "6085552654");
        Pet samantha = createPet("Samantha", LocalDate.of(2012, 9, 4), cat, jean);
        Pet max = createPet("Max", LocalDate.of(2012, 9, 4), cat, jean);

        Owner jeff = createOwner("Jeff", "Black", "1450 Oak Blvd.", "Monona", "6085555387");
        Pet lucky = createPet("Lucky", LocalDate.of(2011, 8, 6), bird, jeff);

        Owner maria = createOwner("Maria", "Escobito", "345 Maple St.", "Madison", "6085557683");
        Pet mulligan = createPet("Mulligan", LocalDate.of(2007, 2, 24), dog, maria);

        Owner david = createOwner("David", "Schroeder", "2749 Blackhawk Trail", "Madison", "6085559435");
        Pet freddy = createPet("Freddy", LocalDate.of(2010, 3, 9), bird, david);

        Owner carlos = createOwner("Carlos", "Estaban", "2335 Independence La.", "Waunakee", "6085555487");
        Pet lucky2 = createPet("Lucky", LocalDate.of(2010, 6, 24), dog, carlos);
        Pet sly = createPet("Sly", LocalDate.of(2012, 6, 8), cat, carlos);

        // Create some visits
        createVisit(samantha, LocalDate.of(2013, 1, 1), "rabies shot");
        createVisit(samantha, LocalDate.of(2013, 1, 4), "neutered");
        createVisit(max, LocalDate.of(2013, 1, 2), "rabies shot");
        createVisit(max, LocalDate.of(2013, 1, 3), "neutered");
    }

    private Role createRole(Role.Authority authority) {
        return roleJdbcRepository.save(new Role(null, authority));
    }

    private User createUser(@NotBlank String username, @NotBlank String rawPassword, Role role) {
        User user = new User(username, passwordEncoder.encode(rawPassword));
        user = userJdbcRepository.save(user);
        UserRoleId userRoleId = new UserRoleId(user, role);
        userRoleJdbcRepository.save(new UserRole(userRoleId));
        return user;
    }

    private void createUserRole(User user, Role role) {
        UserRoleId userRoleId = new UserRoleId(user, role);
        userRoleJdbcRepository.save(new UserRole(userRoleId));
    }

    private Speciality createSpeciality(String name) {
        Speciality speciality = new Speciality(name);
        return specialityRepository.save(speciality);
    }

    private Vet createVet(String firstName, String lastName, Speciality... specialities) {
        Vet vet = new Vet(firstName, lastName)
                .withSpecialities(Set.copyOf(Arrays.asList(specialities)));
        vet = vetRepository.save(vet);

        for (Speciality speciality : specialities) {
            vetSpecialityRepository.save(new VetSpeciality(vet.id(), speciality.id()));
        }
        return vet;
    }

    private PetType createPetType(String name) {
        PetType petType = new PetType(name);
        return petTypeRepository.save(petType);
    }

    private Owner createOwner(String firstName, String lastName, String address, String city, String telephone) {
        Owner owner = new Owner(firstName, lastName, address, city, telephone);
        return ownerRepository.save(owner);
    }

    private Pet createPet(String name, LocalDate birthDate, PetType type, Owner owner) {
        Pet pet = new Pet(name, birthDate, type, owner);
        return petRepository.save(pet);
    }

    private Visit createVisit(Pet pet, LocalDate date, String description) {
        Visit visit = new Visit(date, description, pet);
        return visitRepository.save(visit);
    }
}
