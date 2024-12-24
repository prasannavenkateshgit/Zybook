package edu.ncsu.zybook.web.controller;

import edu.ncsu.zybook.DTO.ContentReadDTO;
import edu.ncsu.zybook.domain.model.*;
import edu.ncsu.zybook.mapper.ContentReadDTOMapper;
import edu.ncsu.zybook.persistence.repository.UserRepository;
import edu.ncsu.zybook.security.CustomUserDetails;
import edu.ncsu.zybook.service.IContentService;
import edu.ncsu.zybook.service.ISectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/contents")
public class ContentController {

    private final IContentService contentService;
    private final ContentReadDTOMapper contentReadDTOMapper;
    private final UserRepository userRepository;
    private final ISectionService sectionService;

    public ContentController(IContentService contentService, ContentReadDTOMapper contentReadDTOMapper, UserRepository userRepository, ISectionService sectionService) {
        this.contentService = contentService;
        this.contentReadDTOMapper = contentReadDTOMapper;
        this.userRepository = userRepository;
        this.sectionService = sectionService;
    }

    @GetMapping("/new/text")
    public String showTextContentForm(@RequestParam int tbookId,
                                      @RequestParam int chapId,
                                      @RequestParam int sectionId,
                                      Model model) {
        TextContent content = new TextContent();
        content.setTbookId(tbookId);
        content.setChapId(chapId);
        content.setSectionId(sectionId);
        model.addAttribute("textContent", content);
        return "content/createTextContent";
    }

    @GetMapping("/new/image")
    public String showImageContentForm(@RequestParam int tbookId,
                                       @RequestParam int chapId,
                                       @RequestParam int sectionId,
                                       Model model) {
        ContentReadDTO content = new ContentReadDTO();
        content.setTbookId(tbookId);
        content.setChapId(chapId);
        content.setSectionId(sectionId);
        model.addAttribute("imageContent", content);
        return "content/createImageContent";
    }

    @PreAuthorize("hasAnyRole('FACULTY', 'TA', 'ADMIN')")
    @PostMapping("/text")
    public String createTextContent(@RequestParam("tbookId") int textbookId,
                                    @RequestParam("chapId") int chapterId,
                                    @RequestParam("sectionId") int sectionId,
                                    @ModelAttribute TextContent content) {

        content.setContentType("text");
        content.setTbookId(textbookId);
        content.setChapId(chapterId);
        content.setSectionId(sectionId);
        System.out.println("Content in controller" + content);
        Content createdContent = contentService.create(content);
        return "redirect:/contents?tbookId=" + textbookId + "&chapId=" + chapterId + "&sectionId=" + sectionId;
    }

    @PreAuthorize("hasAnyRole('FACULTY', 'TA', 'ADMIN')")
    @PostMapping("/image")
    public String createImageContent(@RequestParam("tbookId") int textbookId,
                                     @RequestParam("chapId") int chapterId,
                                     @RequestParam("sectionId") int sectionId,
                                     @ModelAttribute ContentReadDTO content) {
        content.setContentType("image");
        System.out.println(content);
        Content createdContent = contentService.create(contentReadDTOMapper.toEntity(content));
        return "redirect:/contents?tbookId=" + textbookId + "&chapId=" + chapterId + "&sectionId=" + sectionId;
    }

    @PreAuthorize("hasAnyRole('FACULTY', 'TA', 'ADMIN')")
    @GetMapping("/edit/text")
    public String editTextContentForm(@RequestParam("tbookId") int textbookId,
                                      @RequestParam("chapId") int chapterId,
                                      @RequestParam("sectionId") int sectionId,
                                      @RequestParam("contentId") int contentId,
                                      Model model) {
        Optional<Content> result = contentService.findById(contentId, sectionId, chapterId, textbookId);

        if (result.isPresent()) {
            TextContent textContent = (TextContent) result.get();
            model.addAttribute("textContent", textContent);
            return "content/createTextContent";
        } else {
            return "section/not-found";
        }
    }

    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN', 'TA')")
    @GetMapping("/edit/image")
    public String editImageContentForm(@RequestParam("tbookId") int textbookId,
                                       @RequestParam("chapId") int chapterId,
                                       @RequestParam("sectionId") int sectionId,
                                       @RequestParam("contentId") int contentId,
                                       Model model) {
        Optional<Content> result = contentService.findById(contentId, sectionId, chapterId, textbookId);

        if (result.isPresent()) {

            ContentReadDTO contentReadDTO = contentReadDTOMapper.toDTO((ImageContent) result.get());
            // Add the found content to the model with a cast to TextContent if needed
            model.addAttribute("imageContent", contentReadDTO);

            return "content/createImageContent";
        } else {
            // Redirect to an error or "not found" page if the content is missing
            return "section/not-found";
        }
    }

    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN', 'TA')")
    @PutMapping("/text")
    public String updateTextContent(@RequestParam("tbookId") int textbookId,
                                    @RequestParam("chapId") int chapterId,
                                    @RequestParam("sectionId") int sectionId,
                                    @RequestParam("contentId") int contentId,
                                    @ModelAttribute TextContent content
    ) {
        try {
            content.setContentType("text");
            Optional<Content> updatedContent = contentService.update(content);
            if (updatedContent.isPresent()) {
                // Redirect to a content view page after update
                return "redirect:/contents?tbookId=" + textbookId + "&chapId=" + chapterId + "&sectionId=" + sectionId;
            } else return "section/not-found";

        } catch (RuntimeException e) {
            return "section/not-found";
        }
    }

    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN', 'TA')")
    @PutMapping("/image")
    public String updateImageContent(@RequestParam("tbookId") int textbookId,
                                     @RequestParam("chapId") int chapterId,
                                     @RequestParam("sectionId") int sectionId,
                                     @RequestParam("contentId") int contentId,
                                     @ModelAttribute ContentReadDTO content) {
        content.setContentType("image");
        Optional<Content> createdContent = contentService.update(contentReadDTOMapper.toEntity(content));
        return "redirect:/contents?tbookId=" + textbookId + "&chapId=" + chapterId + "&sectionId=" + sectionId;

    }

    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN', 'TA')")
    @DeleteMapping
    public String deleteContent(@RequestParam int sectionId,
                                @RequestParam int chapId,
                                @RequestParam int tbookId,
                                @RequestParam int contentId
    ) {
        try {
            boolean deleted = contentService.delete(contentId, sectionId, chapId, tbookId);
            return deleted ? "redirect:/contents?tbookId=" + tbookId + "&chapId=" + chapId + "&sectionId=" + sectionId
                    : "section/not-found.html";
        } catch (RuntimeException e) {
            return "section/not-found.html";
        }
    }


    @GetMapping
    public String getAllContentBySection(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @RequestParam int sectionId,
                                         @RequestParam int chapId,
                                         @RequestParam int tbookId,
                                         Model model) {
        List<Content> contentList;
        if (customUserDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equalsIgnoreCase("ROLE_STUDENT"))) {
            contentList = contentService.getAllContentBySection(sectionId, chapId, tbookId)
                    .stream()
                    .filter(content -> !content.isHidden())
                    .collect(Collectors.toList());
        } else {
            contentList = contentService.getAllContentBySection(sectionId, chapId, tbookId);
        }
        List<ContentReadDTO> contentReadDTOS = contentList.stream()
                .map(contentReadDTOMapper::toDTO)
                .collect(Collectors.toList());
        String loggedInUserRole = userRepository.getUserRole(customUserDetails.getId());
        model.addAttribute("userId", customUserDetails.getId());
        model.addAttribute("allContents", contentReadDTOS);
        model.addAttribute("sectionId", sectionId);
        model.addAttribute("chapId", chapId);
        model.addAttribute("tbookId", tbookId);

        model.addAttribute("currentRole",loggedInUserRole);
        return "content/list";
    }

    @GetMapping("/perChapter")
    public String getAllContentByChapter(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @RequestParam int chapId,
                                         @RequestParam int tbookId,
                                         Model model) {

        List<Section> sections = sectionService.findAllByChapter(chapId, tbookId);
        List<ContentReadDTO> contentReadDTOS =  new ArrayList<>();
        sections.forEach(section -> {
            List<Content> contentList = contentService.getAllContentBySection(section.getSno(), chapId, tbookId);
            contentReadDTOS.addAll(contentList.stream()
                    .map(contentReadDTOMapper::toDTO)
                    .toList());
        });

        String loggedInUserRole = userRepository.getUserRole(customUserDetails.getId());
        model.addAttribute("userId", customUserDetails.getId());
        model.addAttribute("allContents", contentReadDTOS);
        model.addAttribute("chapId", chapId);
        model.addAttribute("tbookId", tbookId);
        model.addAttribute("currentRole",loggedInUserRole);
        return "content/list";
    }
}

