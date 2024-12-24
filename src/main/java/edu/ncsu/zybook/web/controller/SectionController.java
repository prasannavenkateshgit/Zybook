package edu.ncsu.zybook.web.controller;

import edu.ncsu.zybook.mapper.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import edu.ncsu.zybook.DTO.SectionReadDTO;
import edu.ncsu.zybook.DTO.TextbookReadDTO;
import edu.ncsu.zybook.DTO.ChapterReadDTO;
import edu.ncsu.zybook.domain.model.Chapter;
import edu.ncsu.zybook.domain.model.Section;
import edu.ncsu.zybook.domain.model.Textbook;
import edu.ncsu.zybook.domain.model.Content;
import edu.ncsu.zybook.service.IChapterService;
import edu.ncsu.zybook.service.ISectionService;
import edu.ncsu.zybook.service.ITextbookService;
import edu.ncsu.zybook.service.IContentService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sections")
public class SectionController {
    IChapterService iChapterService;
    ITextbookService iTextbookService;

    ISectionService iSectionService;
    IContentService iContentService;
    TextbookReadDTOMapper textbookReadDTOMapper;

    ChapterReadDTOMapper chapterReadDTOMapper;

    SectionReadDTOMapper sectionReadDTOMapper;

    SectionWeakMapper sectionWeakMapper;
    ContentWeakMapper contentWeakMapper;

    public SectionController(IChapterService iChapterService, ITextbookService iTextbookService, ISectionService iSectionService, TextbookReadDTOMapper textbookReadDTOMapper, ChapterReadDTOMapper chapterReadDTOMapper, SectionWeakMapper sectionWeakMapper, IContentService iContentService, SectionReadDTOMapper sectionReadDTOMapper, ContentWeakMapper contentWeakMapper) {
        this.iChapterService = iChapterService;
        this.iTextbookService = iTextbookService;
        this.iSectionService = iSectionService;
        this.textbookReadDTOMapper = textbookReadDTOMapper;
        this.chapterReadDTOMapper = chapterReadDTOMapper;
        this.sectionWeakMapper = sectionWeakMapper;
        this.iContentService = iContentService;
        this.sectionReadDTOMapper = sectionReadDTOMapper;
        this.contentWeakMapper = contentWeakMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'TA')")
    @GetMapping("/edit")
    public String showEditForm(
            @RequestParam("tbookId") int textbookId,
            @RequestParam("chapId") int chapterId,
            @RequestParam("sno") int sectionId,
            Model model) {
        Optional<Section> section = iSectionService.findById(textbookId, chapterId, sectionId);
        if (section.isPresent()) {
            Optional<Textbook> tbook = iTextbookService.findById(textbookId);
            Optional<Chapter> chap = iChapterService.findById(chapterId, textbookId);
            tbook.ifPresent(textbook -> model.addAttribute("textbook", textbook));
            chap.ifPresent(chapter -> model.addAttribute("chapter", chapter));
            model.addAttribute("section", section.get());
            return "section/create";
        } else {
            return "section/not-found";
        }
    }


    @GetMapping
    public String getAllSections(@RequestParam("tbookId") int tbookId, @RequestParam("chapId") int chapterId, Model model) {
        List<Section> sections = iSectionService.findAllByChapter(tbookId, chapterId);
        model.addAttribute("sections", sections);
        return "redirect:/chapters/chapter";
    }

    @GetMapping("/section")
    public String getSection(
            @RequestParam("tbookId") int textbookId,
            @RequestParam("chapId") int chapterId,
            @RequestParam("sno") int sectionId,
            Model model) {
        return "redirect:/contents?tbookId="+textbookId+"&chapId="+chapterId+"&sectionId="+sectionId;

//        Optional<Section> section = iSectionService.findById(textbookId, chapterId, sectionId);
//        if (section.isPresent()) {
//            List<Content> contents = iContentService.getAllContentBySection(sectionId, chapterId, textbookId);
//            SectionReadDTO sectionReadDTO = sectionReadDTOMapper.toDTO(section.get());
//            sectionReadDTO.setContents(contents.stream().map(contentWeakMapper::toDTO).collect(Collectors.toList()));
//            model.addAttribute("section", sectionReadDTO );
//            System.out.println(Arrays.toString(sectionReadDTO.getContents().toArray()));
//            return "section/section";
//        } else {
//            return "section/not-found";
//        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'TA')")
    @PostMapping
    public  String createSection(@RequestParam("tbookId") int textbookId,
                                 @RequestParam("chapId") int chapterId,
                                 @ModelAttribute Section section) {
        iSectionService.create(section);
        return "redirect:/sections/section?tbookId=" + textbookId + "&chapId=" + chapterId + "&sno=" + section.getSno() ;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'TA')")
    @GetMapping("/new")
    public String showCreateForm(@RequestParam("tbookId") Integer textbookId,
                                 @RequestParam("chapId") Integer chapterId,
                                 Model model) {
        Section section = new Section();
        section.setTbookId(textbookId);
        section.setChapId(chapterId);
        Optional<Textbook> tbook = iTextbookService.findById(textbookId);
        Optional<Chapter> chap = iChapterService.findById(chapterId, textbookId);
        tbook.ifPresent(textbook -> model.addAttribute("textbook", tbook.get()));
        chap.ifPresent(chapter -> model.addAttribute("chapter", chapter));
        model.addAttribute("section", section);
        return "section/create";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'TA')")
    @PutMapping("/update")
    public String updateSection( @RequestParam("tbookId") int textbookId,
                                 @RequestParam("chapId") int chapterId,
                                 @RequestParam("sno") int sectionId,
                                 @ModelAttribute Section section) {
        iSectionService.update(section);
        return "redirect:/chapters/chapter?tbookId=" + textbookId + "&chapId=" + chapterId;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'TA')")
    @DeleteMapping
    public String deleteSection(
            @RequestParam("tbookId") int textbookId,
            @RequestParam("chapId") int chapterId,
            @RequestParam("sno") int sectionId
    ){
        iSectionService.delete(textbookId,chapterId,sectionId);
        return "redirect:/chapters/chapter?tbookId="+textbookId+"&chapId="+chapterId;
    }


}
