package ru.ylab.habbittracker.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ylab.habittracker.app.Main;
import ru.ylab.habittracker.dto.*;
import ru.ylab.habittracker.models.Habit;
import ru.ylab.habittracker.models.HabitCompletion;
import ru.ylab.habittracker.models.User;
import ru.ylab.habittracker.utils.Frequency;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestApp {
    Main main;

    @BeforeEach
    public void setUp() {
        main = new Main();
    }

    @Test
    void testSignUpSuccess() {
        SignUpResponse response = main.getAuthenticationController().signUp(new SignUpRequest("John", "john@example.com", "password123"));
        assertTrue(response.isSuccess());
    }

    @Test
    void testSignInSuccess() {
        SignUpRequest signUpRequest = new SignUpRequest("John", "john@example.com", "password123");
        main.getAuthenticationController().signUp(signUpRequest);

        SignInResponse response = main.getAuthenticationController().signIn(new SignInRequest("john@example.com", "password123"));
        assertTrue(response.isSuccess());
    }

    @Test
    void testSignInFailure() {
        SignInResponse response = main.getAuthenticationController().signIn(new SignInRequest("wrong@example.com", "wrongpassword"));
        assertFalse(response.isSuccess());
    }

    @Test
    void testUpdateUserProfile() {
        main.getAuthenticationController().signUp(new SignUpRequest("John", "john@example.com", "password123"));

        User updatedUser = new User(null, "John Updated", "john@example.com", "newpassword");
        boolean success = main.getUsersController().updatingTheUserProfile(updatedUser).success();

        assertTrue(success);
    }

    @Test
    void testCreateHabit() {
        SignUpRequest signUpRequest = new SignUpRequest("John", "john@example.com", "password123");
        main.getAuthenticationController().signUp(signUpRequest);

        Habit habit = new Habit(null, "Exercise", "Daily exercise", Frequency.DAILY, "john@example.com");
        boolean result = main.getHabitsController().create(habit).success();

        assertTrue(result);
    }

    @Test
    void testDeleteHabit() {
        SignUpRequest signUpRequest = new SignUpRequest("John", "john@example.com", "password123");
        main.getAuthenticationController().signUp(signUpRequest);

        Habit habit = new Habit(null, "Exercise", "Daily exercise", Frequency.DAILY, "john@example.com");
        main.getHabitsController().create(habit);

        main.getHabitsController().delete(habit.getId());

        List<Habit> habits = main.getHabitsController().findAllUserHabitsByEmail("john@example.com").data();
        assertTrue(habits.isEmpty());
    }

    @Test
    void testMarkHabitCompletion() {
        SignUpRequest signUpRequest = new SignUpRequest("John", "john@example.com", "password123");
        main.getAuthenticationController().signUp(signUpRequest);

        Habit habit = new Habit(null, "Exercise", "Daily exercise", Frequency.DAILY, "john@example.com");
        main.getHabitsController().create(habit);

        main.getHabitCompletionController().markCompletion(habit.getId());

        List<HabitCompletion> completions = habit.getCompletions();
        assertEquals(1, completions.size());
        assertTrue(completions.get(0).isCompleted());
    }

    @Test
    void testCalculateCurrentStreak() {
        SignUpRequest signUpRequest = new SignUpRequest("John", "john@example.com", "password123");
        main.getAuthenticationController().signUp(signUpRequest);
        Habit habit = new Habit(null, "Exercise", "Daily exercise", Frequency.DAILY, "john@example.com");
        main.getHabitsController().create(habit);

        main.getHabitCompletionController().markCompletion(habit.getId());
        BaseResponse<Integer> streakResponse = main.getHabitCompletionController().calculateCurrentStreak(habit.getId(), LocalDate.now());

        assertTrue(streakResponse.success());
        assertEquals(1, streakResponse.data());
    }
}
