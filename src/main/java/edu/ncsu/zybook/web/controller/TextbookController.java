package edu.ncsu.zybook.web.controller;

import edu.ncsu.zybook.DTO.TextbookReadDTO;
import edu.ncsu.zybook.domain.model.Chapter;
import edu.ncsu.zybook.domain.model.Textbook;
import edu.ncsu.zybook.mapper.ChapterWeakMapper;
import edu.ncsu.zybook.mapper.TextbookReadDTOMapper;
import edu.ncsu.zybook.security.CustomUserDetails;
import edu.ncsu.zybook.service.IChapterService;
import edu.ncsu.zybook.service.ITextbookService;
import edu.ncsu.zybook.service.impl.ChapterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/textbooks")
public class TextbookController {

    private final ITextbookService textbookService;
    private final TextbookReadDTOMapper textbookReadDTOMapper;
    private final IChapterService iChapterService;
    private final ChapterWeakMapper chapterWeakMapper;

    public TextbookController(ITextbookService textbookService, TextbookReadDTOMapper textbookReadDTOMapper, IChapterService iChapterService, ChapterWeakMapper chapterWeakMapper) {
        this.textbookService = textbookService;
        this.textbookReadDTOMapper = textbookReadDTOMapper;
        this.iChapterService = iChapterService;
        this.chapterWeakMapper = chapterWeakMapper;
    }

    @GetMapping("/{id}")
    public String getTextbook(@PathVariable int id, @AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        Optional<Textbook> textbook = textbookService.findById(id);
        if (textbook.isPresent()) {
            TextbookReadDTO textbookReadDTO = textbookReadDTOMapper.toDTO(textbook.get());
            List<Chapter> chapters;
            if (customUserDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equalsIgnoreCase("ROLE_STUDENT"))) {
                // Only non-hidden chapters for students
                chapters = iChapterService.findAllByTextbook(textbookReadDTO.getUid())
                        .stream()
                        .filter(chapter -> !chapter.isHidden())
                        .collect(Collectors.toList());
                textbookReadDTO.setChapters(chapters.stream().map(chapterWeakMapper::toDTO).collect(Collectors.toList()));
            } else {
                // For non-students, fetch all chapters
                chapters = iChapterService.findAllByTextbook(textbookReadDTO.getUid());
                textbookReadDTO.setChapters(chapters.stream().map(chapterWeakMapper::toDTO).collect(Collectors.toList()));
            }
            textbookReadDTO.setChapters(chapters.stream().map(chapterWeakMapper::toDTO).collect(Collectors.toList()));
            model.addAttribute("textbook", textbookReadDTO);
            return "textbook/textbook";
        } else {
            return "textbook/not-found";
        }
    }

    @GetMapping
    public String getAllTextbooks(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "uid") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            Model model) {
        List<Textbook> textbooks = textbookService.getAllTextbooks(offset, limit, sortBy, sortDirection);
        model.addAttribute("textbooks", textbooks);
        System.out.println();
        return "textbook/list";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("textbook", new Textbook());
        return "textbook/create";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public  String createTextbook(@ModelAttribute Textbook textbook) {
        Textbook tbook = textbookService.create(textbook);
        return "redirect:/textbooks/"+tbook.getUid();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Optional<Textbook> textbook = textbookService.findById(id);
        if (textbook.isPresent()) {
            model.addAttribute("textbook", textbook.get());
            return "textbook/create";
        } else {
            return "textbook/not-found";
        }
    }

    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    @PutMapping("/{id}")
    public String updateTextbook(@ModelAttribute Textbook textbook) {
        textbookService.update(textbook);
        return "redirect:/textbooks";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteTextbook(@PathVariable int id){
        textbookService.delete(id);
        return "redirect:/textbooks";
    }
}