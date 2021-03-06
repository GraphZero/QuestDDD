package com.quest.activities.domain;
import com.quest.activities.domain.activity.ActivitiesList;
import com.quest.activities.domain.activity.Activity;
import com.quest.activities.domain.activity.ActivityType;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ActivityTest {

    @Test
    void shouldntCreateActivity() {
        // given
        ActivitiesList activitiesList = null;
        ActivityType activityType = new ActivityType(
                "test",
                Duration.ZERO,
                1000
        );
        // when
        // then
        assertThrows(Activity.WrongLatitudeOrLongitude.class,
                () -> new Activity(activityType, activitiesList, -5555, 5555));
    }

    @Test
    void shouldntStartActivity() {
        // given
        ActivitiesList activitiesList = null;
        ActivityType activityType = new ActivityType(
                "test",
                Duration.ZERO,
                1000
        );
        Activity activity = new Activity(activityType, activitiesList, 0, 0);
        // when
        activity.startActivity();
        // then
        assertThrows( Activity.ActivityAlreadyStarted.class, activity::startActivity,
                "Since activity has already started, exception is being thrown.");
    }

    @Test
    void shouldntFinishActivity() {
        // given
        ActivitiesList activitiesList = null;
        ActivityType activityType = new ActivityType(
                "test",
                Duration.ZERO,
                1000
        );
        Activity activity = new Activity(activityType, activitiesList, 0, 0);
        // when
        // then
        assertThrows( Activity.ActivityNotStarted.class, activity::finishActivity,
                "Since activity hasn't  started, exception is being thrown.");
    }
}