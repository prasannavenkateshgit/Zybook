package edu.ncsu.zybook.service.impl;

import edu.ncsu.zybook.domain.model.Activity;
import edu.ncsu.zybook.domain.model.Chapter;
import edu.ncsu.zybook.persistence.repository.IActivityRepository;
import edu.ncsu.zybook.service.IActivityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService implements IActivityService {

    private final IActivityRepository activityRepository;
    public ActivityService(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }
    @Override
    public Activity create(Activity activity) {
        Optional<Activity> result = activityRepository.findById(activity.getActivityId(),activity.getContentId(),activity.getSectionId(),activity.getChapId(),activity.getTbookId());
        if(result.isEmpty()) {
            return activityRepository.create(activity);
        }
        else{
            throw new RuntimeException("Activity already exists!");
        }
    }

    @Override
    public Optional<Activity> findById(int activityId, int contentId, int sectionId, int chapId, int tbookId) {
        Optional<Activity> result = activityRepository.findById(activityId, contentId, sectionId, chapId, tbookId);
        if(result.isPresent()) {
            Activity activity = result.get();
            return Optional.of(activity);
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(int activityId, int contentId, int sectionId, int chapId, int tbookId) {
        Optional<Activity> existingActivity= activityRepository.findById(activityId, contentId, sectionId, chapId, tbookId);
        if(existingActivity.isPresent()) {
            activityRepository.delete(activityId, contentId, sectionId, chapId, tbookId);
            return true;
        }
        return false;
    }

    @Override
    public List<Activity> findAllByContent(int contentId, int sectionId, int chapId, int tbookId) {
        List<Activity> allActivities = activityRepository.findAllByContent(contentId, sectionId, chapId, tbookId);
        System.out.println("In Service, activity size:"+allActivities.size());
        return allActivities;
    }

    @Override
    public List<Activity> findAllActivitiesByTextbook(int tbookId) {
        return activityRepository.findAllActivitiesByTextbook(tbookId);
    }
}
