package ru.ylab.habittracker.app;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import ru.ylab.habittracker.controllers.*;
import ru.ylab.habittracker.dto.*;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.repositories.HabitCompletionRepository;
import ru.ylab.habittracker.repositories.HabitsRepository;
import ru.ylab.habittracker.repositories.UsersRepository;
import ru.ylab.habittracker.repositories.impl.HabitCompletionRepositoryImpl;
import ru.ylab.habittracker.repositories.impl.HabitsRepositoryImpl;
import ru.ylab.habittracker.repositories.impl.UsersRepositoryImpl;
import ru.ylab.habittracker.services.impl.AuthenticationService;
import ru.ylab.habittracker.services.HabitCompletionService;
import ru.ylab.habittracker.services.HabitsService;
import ru.ylab.habittracker.services.UsersService;
import ru.ylab.habittracker.services.impl.HabitCompletionServiceImpl;
import ru.ylab.habittracker.services.impl.HabitsServiceImpl;
import ru.ylab.habittracker.services.impl.UsersServiceImpl;
import ru.ylab.habittracker.utils.Frequency;

import java.time.LocalDate;
import java.util.Scanner;

public class App {
    private final AuthenticationController authenticationController;
    private final UsersController usersController;
    private final HabitsController habitsController;
    private final HabitCompletionController habitCompletionController;
    private final AdministrationController administrationController;

    public App() {
        DatabaseConnection databaseConnection = new DatabaseConnection(PropertyLoader.getProperty("db.url"),
                PropertyLoader.getProperty("db.username"), PropertyLoader.getProperty("db.password"));
        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(databaseConnection.getConnection()));
            Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        UsersRepository usersRepository = new UsersRepositoryImpl(databaseConnection);
        HabitsRepository habitsRepository = new HabitsRepositoryImpl(databaseConnection);
        HabitCompletionRepository habitCompletionRepository = new HabitCompletionRepositoryImpl(databaseConnection);

        UsersService usersService = new UsersServiceImpl(usersRepository);
        AuthenticationService authenticationService = new AuthenticationService(usersService, usersRepository);
        HabitsService habitsService = new HabitsServiceImpl(usersRepository, habitsRepository);
        HabitCompletionService habitCompletionService = new HabitCompletionServiceImpl(habitsRepository, habitCompletionRepository);

        usersController = new UsersController(usersService);
        authenticationController = new AuthenticationController(authenticationService);
        habitsController = new HabitsController(habitsService);
        habitCompletionController = new HabitCompletionController(habitCompletionService);
        administrationController = new AdministrationController(usersService);
    }

    public void start(Scanner scanner) {
        showMenu(scanner);
    }

    private void showMenu(Scanner scanner) {
        while (true) {
            switch (getOption(scanner, "1. Register\n2. Login\n3. Exit\nChoose an option:")) {
                case 1 -> register(scanner);
                case 2 -> login(scanner);
                case 3 -> {
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid option, please try again.");
            }
        }
    }

    private void register(Scanner scanner) {
        BaseResponse<UserResponse> signUpResponse = authenticationController.signUp(new SignUpRequest(
                prompt(scanner, "Enter name:"),
                prompt(scanner, "Enter email:"),
                prompt(scanner, "Enter password:")
        ));
        System.out.println(signUpResponse);
    }

    private void login(Scanner scanner) {
        BaseResponse<UserResponse> signInResponse = authenticationController.signIn(new SignInRequest(
                prompt(scanner, "Enter email:"),
                prompt(scanner, "Enter password:")
        ));
        System.out.println(signInResponse);
        if (signInResponse.status().equals("User successfully logged in")) {
            userMenu(scanner, signInResponse.data().id());
        }
    }

    private void userMenu(Scanner scanner, Long id) {
        while (true) {
            switch (getOption(scanner, "1. Manage Users\n2. Manage Habits\n3. Block User (Admin)\n4. Return back\nAny other key - Logout\nChoose an option:")) {
                case 1 -> {
                    if (!manageUsers(scanner, id)) {
                        return;
                    }
                }
                case 2 -> manageHabits(scanner, id);
                case 3 -> blockUser(scanner, id);
                case 4 -> {
                    return;
                }
                default -> System.out.println("Logged out.");
            }
        }
    }

    private boolean manageUsers(Scanner scanner, Long id) {
        while (true) {
            switch (getOption(scanner, "1. Update Profile\n2. Change Password\n3. Delete Account\n4. Return back\nAny other key - Back\nChoose an option:")) {
                case 1 -> updateUser(scanner, id);
                case 2 -> {
                    deleteUser(id);
                    return false;
                }
                case 3 -> {
                    return true;
                }
            }
        }
    }

    private void updateUser(Scanner scanner, Long id) {
        String name = prompt(scanner, "Enter name: ");
        String email = prompt(scanner, "Enter email: ");
        String password = prompt(scanner, "Enter password: ");
        System.out.println(usersController.updatingTheUserProfile(new UpdateUserProfileRequest(id, name, email, password)));
    }

    private void deleteUser(Long id) {
        usersController.deleteUserById(id);
        System.out.println("User (id = " + id + ") deleted.");
    }

    private String prompt(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private int getOption(Scanner scanner, String menu) {
        System.out.print(menu);
        int result = scanner.nextInt();
        scanner.nextLine();
        return result;
    }

    private void manageHabits(Scanner scanner, Long id) {
        while (true) {
            switch (getOption(scanner, "1. Create Habit\n2. Edit Habit\n3. Delete Habit\n4. View All Habits\n5. View Habits by Creation Date\n6. Track Habit Completion\n7. Statistics and Analytics\n8. Return back\nAny other key - Back\nChoose an option:")) {
                case 1 -> createHabit(scanner, id);
                case 2 -> updateHabit(scanner);
                case 3 -> deleteHabit(scanner);
                case 4 -> System.out.println(habitsController.findAllUserHabitsById(id));
                case 5 -> viewHabitsByDate(scanner, id);
                case 6 -> trackHabitCompletion(scanner);
                case 7 -> statisticsAndAnalytics(scanner);
                case 8 -> {
                    return;
                }
            }
        }
    }

    private void blockUser(Scanner scanner, Long id) {
        String userId = prompt(scanner, "Enter the user id to block: ");
        System.out.println(administrationController.blockUser(id, Long.parseLong(userId)));
    }

    private void createHabit(Scanner scanner, Long id) {
        String name = prompt(scanner, "Enter habit name: ");
        String description = prompt(scanner, "Enter habit description: ");
        Frequency frequency = getFrequency(scanner);
        if (frequency != null) {
            System.out.println(habitsController.create(new Habit(null, name, description, frequency, id, LocalDate.now())));
        }
    }

    private void updateHabit(Scanner scanner) {
        Long id = getId(scanner);
        String name = prompt(scanner, "Enter new habit name: ");
        String description = prompt(scanner, "Enter new habit description: ");
        Frequency frequency = getFrequency(scanner);
        System.out.println(habitsController.update(new Habit(null, name, description, frequency, id, LocalDate.now())));
    }

    private void deleteHabit(Scanner scanner) {
        Long id = getId(scanner);
        habitsController.delete(id);
        System.out.println("Habit deleted!");
    }

    private void viewHabitsByDate(Scanner scanner, Long id) {
        LocalDate date = getDate(scanner);
        if (date != null) {
            System.out.println(habitsController.findAllUserHabitsByUserIdAndDate(id, date));
        }
    }

    private void trackHabitCompletion(Scanner scanner) {
        Long id = getId(scanner);
        habitCompletionController.markCompletion(id);
        System.out.println("Habit completion marked.");
    }

    private void statisticsAndAnalytics(Scanner scanner) {
        while (true) {
            switch (getOption(scanner, "1. Streak Count\n2. Completion Percentage\n3. Progress Report\nAny other key - Back\nChoose an option:")) {
                case 1 -> calculateStreak(scanner);
                case 2 -> calculateCompletionPercentage(scanner);
                case 3 -> generateProgressReport(scanner);
                case 4 -> {
                    return;
                }
            }
        }
    }

    private void calculateStreak(Scanner scanner) {
        Long id = getId(scanner);
        System.out.println(habitCompletionController.calculateCurrentStreak(id, LocalDate.now()));
    }

    private void calculateCompletionPercentage(Scanner scanner) {
        Long id = getId(scanner);
        LocalDate startDate = getDate(scanner);
        LocalDate endDate = getDate(scanner);
        if (startDate != null && endDate != null) {
            System.out.println(habitCompletionController.calculateCompletionPercentage(id, startDate, endDate));
        }
    }

    private void generateProgressReport(Scanner scanner) {
        Long id = getId(scanner);
        LocalDate startDate = getDate(scanner);
        LocalDate endDate = getDate(scanner);
        if (startDate != null && endDate != null) {
            System.out.println(habitCompletionController.generateHabitReport(id, startDate, endDate));
        }
    }

    private Long getId(Scanner scanner) {
        System.out.print("Enter habit id: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        return id;
    }

    private LocalDate getDate(Scanner scanner) {
        try {
            System.out.print("Enter date (yyyy-MM-dd): ");
            return LocalDate.parse(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid date format.");
            return null;
        }
    }

    private Frequency getFrequency(Scanner scanner) {
        System.out.print("Enter habit frequency: \n1. daily\n2. weekly\n");
        try {
            return Frequency.fromValue(Integer.parseInt(scanner.nextLine()));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid frequency.");
            return null;
        }
    }

    public AuthenticationController getAuthenticationController() {
        return authenticationController;
    }

    public UsersController getUsersController() {
        return usersController;
    }

    public HabitsController getHabitsController() {
        return habitsController;
    }

    public HabitCompletionController getHabitCompletionController() {
        return habitCompletionController;
    }

    public AdministrationController getAdministrationController() {
        return administrationController;
    }
}
