package edu.ncsu.zybook.service;

import edu.ncsu.zybook.domain.model.Activity;
import edu.ncsu.zybook.domain.model.UserParticipation;

import java.util.List;
import java.util.Optional;

public interface IActivityService {
    Activity create(Activity activity);
    Optional<Activity> findById(int activityId, int contentId, int sectionId, int chapId, int tbook_id);
    boolean delete(int activityId, int contentId, int sectionId, int chapId, int tbookId);
    List<Activity> findAllByContent(int contentId, int sectionId, int chapId, int tbookId);

    List<Activity> findAllActivitiesByTextbook(int tbookId);
}
