package com.todo.activities.domain;

import java.util.Optional;

public interface ActivityTypeRepository {
    ActivityType save(ActivityType activityType);
    Optional<ActivityType> findById( long activityTypeId );
}