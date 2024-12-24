package edu.ncsu.zybook.service;

import edu.ncsu.zybook.DTO.ChapterReadDTO;
import edu.ncsu.zybook.domain.model.Chapter;

import java.util.List;
import java.util.Optional;

public interface IChapterService {
    Chapter create(Chapter chapter);
    Optional<Chapter> update(Chapter chapter);
    boolean delete(int tbookId, int chapterId);
    Optional<Chapter> findById(int cno, int tbookId);
    List<Chapter> findAllByTextbook(int tbookId);
    Optional<Chapter> findByTitle(String title, int tbookId);
}
