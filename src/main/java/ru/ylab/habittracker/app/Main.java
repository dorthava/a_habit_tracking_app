package ru.ylab.habittracker.app;

import ru.ylab.habittracker.controllers.*;
import ru.ylab.habittracker.dto.*;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.repositories.*;
import ru.ylab.habittracker.services.*;
import ru.ylab.habittracker.utils.Frequency;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private final AuthenticationController authenticationController;
    private final UsersController usersController;
    private final HabitsController habitsController;
    private final HabitCompletionController habitCompletionController;
    private final AdministrationController administrationController;

    public Main() {
        UsersRepository usersRepository = new UsersRepositoryImpl();
        HabitsRepository habitsRepository = new HabitsRepositoryImpl();
        HabitCompletionRepository habitCompletionRepository = new HabitCompletionRepositoryImpl();

        UsersService usersService = new UsersServiceImpl(usersRepository, habitsRepository);
        AuthenticationService authenticationService = new AuthenticationService(usersService, usersRepository);
        HabitsService habitsService = new HabitsServiceImpl(usersRepository, habitsRepository);
        HabitCompletionService habitCompletionService = new HabitCompletionServiceImpl(habitsRepository, habitCompletionRepository);

        usersController = new UsersController(usersService);
        authenticationController = new AuthenticationController(authenticationService);
        habitsController = new HabitsController(habitsService);
        habitCompletionController = new HabitCompletionController(habitCompletionService);
        administrationController = new AdministrationController(usersService);
    }

    public static void main(String[] args) {
        Main main = new Main();
        Scanner scanner = new Scanner(System.in);
        main.createAdmin(main);

            main.showMenu(scanner);
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
        SignUpResponse signUpResponse = authenticationController.signUp(new SignUpRequest(
                prompt(scanner, "Enter name:"),
                prompt(scanner, "Enter email:"),
                prompt(scanner, "Enter password:")
        ));
        System.out.println(signUpResponse);
    }

    private void login(Scanner scanner) {
        SignInResponse signInResponse = authenticationController.signIn(new SignInRequest(
                prompt(scanner, "Enter email:"),
                prompt(scanner, "Enter password:")
        ));
        System.out.println(signInResponse);
        if (signInResponse.isSuccess()) {
            userMenu(scanner, signInResponse.getEmail());
        }
    }

    private void userMenu(Scanner scanner, String email) {
        while (true) {
            switch (getOption(scanner, "1. Manage Users\n2. Manage Habits\n3. Block User (Admin)\n4. Return back\nAny other key - Logout\nChoose an option:")) {
                case 1 -> {
                    if(!manageUsers(scanner, email)) { return; }
                }
                case 2 -> manageHabits(scanner, email);
                case 3 -> blockUser(scanner, email);
                case 4 -> {
                    return;
                }
                default -> System.out.println("Logged out.");
            }
        }
    }

    private boolean manageUsers(Scanner scanner, String email) {
        while (true) {
            switch (getOption(scanner, "1. Update Profile\n2. Change Password\n3. Delete Account\n4. Return back\nAny other key - Back\nChoose an option:")) {
                case 1 -> updateUser(scanner, email);
                case 2 -> changePassword(scanner, email);
                case 3 -> {
                    deleteUser(email);
                    return false;
                }
                case 4 -> {
                    return true;
                }
            }
        }
    }

    private void updateUser(Scanner scanner, String email) {
        String name = prompt(scanner, "Enter name: ");
        String password = prompt(scanner, "Enter password: ");
        System.out.println(usersController.updatingTheUserProfile(new User(null, name, email, password)));
    }

    private void changePassword(Scanner scanner, String email) {
        String newPassword = prompt(scanner, "Enter new password: ");
        System.out.println(usersController.forgotPassword(email, newPassword));
    }

    private void deleteUser(String email) {
        usersController.deleteUserByEmail(email);
        System.out.println("User " + email + " has been deleted.");
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

    private void manageHabits(Scanner scanner, String email) {
        while (true) {
            switch (getOption(scanner, "1. Create Habit\n2. Edit Habit\n3. Delete Habit\n4. View All Habits\n5. View Habits by Creation Date\n6. Track Habit Completion\n7. Statistics and Analytics\n8. Return back\nAny other key - Back\nChoose an option:")) {
                case 1 -> createHabit(scanner, email);
                case 2 -> updateHabit(scanner);
                case 3 -> deleteHabit(scanner);
                case 4 -> System.out.println(habitsController.findAllUserHabitsByEmail(email));
                case 5 -> viewHabitsByDate(scanner, email);
                case 6 -> trackHabitCompletion(scanner);
                case 7 -> statisticsAndAnalytics(scanner);
                case 8 -> {
                    return;
                }
            }
        }
    }

    private void blockUser(Scanner scanner, String email) {
        String emailBlock = prompt(scanner, "Enter the email to block: ");
        System.out.println(administrationController.blockUser(email, emailBlock));
    }

    private void createHabit(Scanner scanner, String email) {
        String name = prompt(scanner, "Enter habit name: ");
        String description = prompt(scanner, "Enter habit description: ");
        Frequency frequency = getFrequency(scanner);
        if (frequency != null) {
            System.out.println(habitsController.create(new Habit(null, name, description, frequency, email)));
        }
    }

    private void updateHabit(Scanner scanner) {
        Long id = getId(scanner);
        String name = prompt(scanner, "Enter new habit name: ");
        String description = prompt(scanner, "Enter new habit description: ");
        System.out.println(habitsController.update(new Habit(id, name, description, null, null)));
    }

    private void deleteHabit(Scanner scanner) {
        Long id = getId(scanner);
        habitsController.delete(id);
        System.out.println("Habit deleted!");
    }

    private void viewHabitsByDate(Scanner scanner, String email) {
        LocalDate date = getDate(scanner, "Enter date (yyyy-MM-dd): ");
        if (date != null) {
            System.out.println(habitsController.findAllUserHabitsByEmailAndDate(email, date));
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
        LocalDate startDate = getDate(scanner, "Enter start date (yyyy-MM-dd): ");
        LocalDate endDate = getDate(scanner, "Enter end date (yyyy-MM-dd): ");
        if (startDate != null && endDate != null) {
            System.out.println(habitCompletionController.calculateCompletionPercentage(id, startDate, endDate));
        }
    }

    private void generateProgressReport(Scanner scanner) {
        Long id = getId(scanner);
        LocalDate startDate = getDate(scanner, "Enter start date (yyyy-MM-dd): ");
        LocalDate endDate = getDate(scanner, "Enter end date (yyyy-MM-dd): ");
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

    private LocalDate getDate(Scanner scanner, String message) {
        try {
            System.out.print(message);
            return LocalDate.parse(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid date format.");
            return null;
        }
    }

    private Frequency getFrequency(Scanner scanner) {
        System.out.print("Enter habit frequency (daily/weekly): ");
        try {
            return Frequency.fromString(scanner.nextLine());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid frequency.");
            return null;
        }
    }

    private void createAdmin(Main main) {
        main.authenticationController.signUp(new SignUpRequest("admin", "admin@gmail.com", "admin"));
        main.administrationController.setAdminRole("admin@gmail.com");
    }
}
