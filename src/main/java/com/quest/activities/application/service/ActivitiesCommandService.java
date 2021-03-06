package com.quest.activities.application.service;

import com.ddd.common.annotations.ApplicationService;
import com.ddd.common.events.ActivityIsFinished;
import com.quest.activities.application.commands.*;
import com.quest.activities.domain.activeness.MostActiveRegion;
import com.quest.activities.domain.activity.ExperienceCalcService;
import com.quest.activities.application.service.exceptions.ActivitiesListNotFound;
import com.quest.activities.application.service.exceptions.ActivityNotFound;
import com.quest.activities.application.service.exceptions.ActivityTypeNotFound;
import com.quest.activities.application.service.exceptions.UserNotFound;
import com.quest.activities.domain.activity.*;
import com.quest.activities.domain.admin.AdminControlPanel;
import com.quest.activities.domain.location.NearbyQuestersFinder;
import com.quest.activities.domain.location.dto.NearbyQuestersDto;
import com.quest.activities.domain.user.User;
import com.quest.activities.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@ApplicationService
@Transactional
@RequiredArgsConstructor
public class ActivitiesCommandService implements IActivitiesCommandService {
    private final UserRepository userRepository;
    private final ActivitiesListRepository activitiesListRepository;
    private final ActivityTypeRepository activityTypeRepository;
    private final ActivityRepository activityRepository;

    private final ActivitiesListFactory activitiesListFactory;

    private final ExperienceCalcService experienceCalcService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final AdminControlPanel adminControlPanel;

    @Value("{bonus.experience}")
    private String adminExperienceBonus;

    public ActivitiesList addNewActivitiesListToUser(CreateNewActivitiesListCommand createNewActivitiesListCommand) {
        User owningUser = userRepository
                .find(createNewActivitiesListCommand.getUserId())
                .orElseThrow(UserNotFound::new);
        ActivitiesList activitiesList = activitiesListFactory.createActivities(owningUser);
        owningUser.addActivities(activitiesList);
        activitiesListRepository.save(activitiesList);
        userRepository.save(owningUser);
        return activitiesList;
    }

    public Activity addActivityToActivityList(AddActivityCommand addActivityCommand){
        ActivitiesList owningActivitiesList = activitiesListRepository
                .findById(addActivityCommand.getActivitiesListId())
                .orElseThrow(ActivitiesListNotFound::new);
        ActivityType typeOfActivity = activityTypeRepository
                .findById(addActivityCommand.getActivityTypeId())
                .orElseThrow(ActivityTypeNotFound::new);
        Activity newActivity = new Activity(typeOfActivity, owningActivitiesList,
                addActivityCommand.getLongitude(),
                addActivityCommand.getLatitude());
        owningActivitiesList.addActivity(newActivity);
        activityRepository.save(newActivity);
        activitiesListRepository.save(owningActivitiesList);
        return newActivity;
    }

    public Activity startActivity(StartActivityCommand startActivityCommand){
        Activity activity = activityRepository
                .findById(startActivityCommand.getActivityId())
                .orElseThrow(ActivityNotFound::new);
        activity.startActivity();
        return activityRepository.save(activity);
    }

    public Activity finishActivity(FinishActivityCommand finishActivityCommand){
        Activity activity = activityRepository
                .findById(finishActivityCommand.getActivityId())
                .orElseThrow(ActivityNotFound::new);
        User owningUser = userRepository.find(activity.getActivitiesList().getUser().getId()).get();
        activity.finishActivity();
        activity.setAward((long) adminControlPanel.grantBonus(Double.parseDouble(adminExperienceBonus)) + experienceCalcService.calculateExperienceGain(activity, activity.getActivitiesList(), owningUser ));
        owningUser.addExperience(activity.getAward());
        notifyUserAboutFinishingActivity(activity, owningUser);
        return activityRepository.save(activity);
    }

    void notifyUserAboutFinishingActivity(Activity activity, User owningUser){
        applicationEventPublisher.publishEvent(
                new ActivityIsFinished(this,
                        activity.getId(),
                        owningUser.getEmailAddress(),
                        owningUser.getUsername(),
                        LocalDateTime.now(),
                        activity.getAward()));
    }



}
