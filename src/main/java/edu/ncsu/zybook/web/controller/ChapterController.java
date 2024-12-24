package edu.ncsu.zybook.web.controller;

import edu.ncsu.zybook.DTO.ChapterReadDTO;
import edu.ncsu.zybook.DTO.TextbookReadDTO;
import edu.ncsu.zybook.domain.model.Chapter;
import edu.ncsu.zybook.domain.model.Section;
import edu.ncsu.zybook.domain.model.Textbook;
import edu.ncsu.zybook.mapper.ChapterReadDTOMapper;
import edu.ncsu.zybook.mapper.SectionWeakMapper;
import edu.ncsu.zybook.mapper.TextbookReadDTOMapper;
import edu.ncsu.zybook.security.CustomUserDetails;
import edu.ncsu.zybook.service.IChapterService;
import edu.ncsu.zybook.service.ISectionService;
import edu.ncsu.zybook.service.ITextbookService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chapters")
public class ChapterController {
    IChapterService iChapterService;
    ITextbookService iTextbookService;

    ISectionService iSectionService;
    TextbookReadDTOMapper textbookReadDTOMapper;

    ChapterReadDTOMapper chapterReadDTOMapper;

    SectionWeakMapper sectionWeakMapper;

    public ChapterController(IChapterService iChapterService, ITextbookService iTextbookService, ISectionService iSectionService, TextbookReadDTOMapper textbookReadDTOMapper, ChapterReadDTOMapper chapterReadDTOMapper, SectionWeakMapper sectionWeakMapper) {
        this.iChapterService = iChapterService;
        this.iTextbookService = iTextbookService;
        this.iSectionService = iSectionService;
        this.textbookReadDTOMapper = textbookReadDTOMapper;
        this.chapterReadDTOMapper = chapterReadDTOMapper;
        this.sectionWeakMapper = sectionWeakMapper;
    }

    @PreAuthorize("hasAnyRole('FACULTY', 'TA', 'ADMIN')")
    @GetMapping("/edit")
    public String showEditForm(
            @RequestParam("tbookId") int textbookId,
            @RequestParam("chapId") int chapterId,
            Model model) {
        Optional<Chapter> chapter = iChapterService.findById(chapterId,  textbookId);
        if (chapter.isPresent()) {
            Optional<Textbook> tbook = iTextbookService.findById(textbookId);
            tbook.ifPresent(textbook -> model.addAttribute("textbook", textbook));
            model.addAttribute("chapter", chapter.get());
            return "chapter/create";
        } else {
            return "chapter/not-found";
        }
    }

    @GetMapping()
    public String getAllChapters(@RequestParam("tbookId") int tbookId, Model model) {
        List<Chapter> chapters = iChapterService.findAllByTextbook(tbookId)
                .stream()
                .filter(chapter -> !chapter.isHidden())
                .collect(Collectors.toList());;
                System.out.println(chapters);
        model.addAttribute("chapters", chapters);
        return "chapter/list";
    }

    @GetMapping("/chapter")
    public String getChapter(@RequestParam("tbookId") int textbookId,
                             @RequestParam("chapId") int chapterId,
                             @AuthenticationPrincipal CustomUserDetails customUserDetails,
                             Model model) {
        Optional<Chapter> chapter = iChapterService.findById(chapterId, textbookId);
        if (chapter.isPresent()) {
            List<Section> sections;
            if (customUserDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equalsIgnoreCase("ROLE_STUDENT"))) {
                sections = iSectionService.findAllByChapter(textbookId, chapterId)
                        .stream()
                        .filter(section -> !section.isHidden())
                        .collect(Collectors.toList());
            } else {
                sections = iSectionService.findAllByChapter(textbookId, chapterId);
            }

            ChapterReadDTO chapterReadDTO = chapterReadDTOMapper.toDTO(chapter.get());
            chapterReadDTO.setSections(sections.stream().map(sectionWeakMapper::toDTO).collect(Collectors.toList()));
            model.addAttribute("chapter", chapterReadDTO);
            return "chapter/chapter";
        } else {
            return "chapter/not-found";
        }
    }

    @PreAuthorize("hasAnyRole('FACULTY', 'TA', 'ADMIN')")
    @PostMapping
    public  String createChapter(@ModelAttribute Chapter chapter) {
        iChapterService.create(chapter);
        return "redirect:/chapters/chapter?tbookId="+ chapter.getTbookId()+"&chapId="+chapter.getCno();
    }

    @PreAuthorize("hasAnyRole('FACULTY', 'TA', 'ADMIN')")
    @GetMapping("/new")
    public String showCreateForm(@RequestParam("tbookId") Integer textbookId, Model model) {
        Chapter chapter = new Chapter();
        chapter.setTbookId(textbookId);
        Optional<Textbook> tbook = iTextbookService.findById(textbookId);
        tbook.ifPresent(textbook -> model.addAttribute("textbook", tbook.get()));
        model.addAttribute("chapter", chapter);
        return "chapter/create";
    }

    @PreAuthorize("hasAnyRole('FACULTY', 'TA', 'ADMIN')")
    @PutMapping("/update")
    public String updateChapter( @RequestParam("tbookId") int textbookId,
                                 @RequestParam("chapId") int chapterId,
                                 @ModelAttribute Chapter chapter) {
        iChapterService.update(chapter);
        return "redirect:/textbooks/" + textbookId;
    }

    @PreAuthorize("hasAnyRole('FACULTY', 'TA', 'ADMIN')")
    @DeleteMapping
    public String deleteChapter(
            @RequestParam("tbookId") int textbookId,
            @RequestParam("chapId") int chapterId
    ){
        iChapterService.delete(textbookId,chapterId);
        return "redirect:/textbooks/"+textbookId;
    }
}
