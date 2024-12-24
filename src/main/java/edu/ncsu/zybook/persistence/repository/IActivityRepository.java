package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.Activity;

import java.util.List;
import java.util.Optional;

public interface IActivityRepository {

    Activity create(Activity activity);

    Optional<Activity> findById(int activityId, int contentId, int sectionId, int chapId, int tbookId);

    boolean delete(int activityId, int contentId, int sectionId, int chapId, int tbookId);

    List<Activity> findAllByContent(int contentId, int sectionId, int chapId, int tbookId);

    List<Activity> findAllActivitiesByTextbook(int tbookId);
}