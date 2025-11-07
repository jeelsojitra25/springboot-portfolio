package com.jeel.portfolio.web.controller;

import com.jeel.portfolio.service.ContactService;
import com.jeel.portfolio.service.RateLimitException;
import com.jeel.portfolio.service.SpamProtectionException;
import com.jeel.portfolio.web.dto.ContactForm;
import com.jeel.portfolio.web.view.SeoMetadataFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;

@Controller
public class ContactController {

    private final ContactService contactService;
    private final SeoMetadataFactory seoMetadataFactory;

    public ContactController(ContactService contactService, SeoMetadataFactory seoMetadataFactory) {
        this.contactService = contactService;
        this.seoMetadataFactory = seoMetadataFactory;
    }

    @GetMapping("/contact")
    public String contactPage(Model model) {
        if (!model.containsAttribute("contactForm")) {
            ContactForm form = new ContactForm();
            form.setStartedAt(Instant.now().toEpochMilli());
            model.addAttribute("contactForm", form);
        }
        model.addAttribute("meta", seoMetadataFactory.contact());
        return "contact";
    }

    @PostMapping("/contact")
    public String submitContact(@Valid @ModelAttribute("contactForm") ContactForm form,
                                BindingResult bindingResult,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        model.addAttribute("meta", seoMetadataFactory.contact());

        if (bindingResult.hasErrors()) {
            form.setStartedAt(Instant.now().toEpochMilli());
            return "contact";
        }

        try {
            contactService.submit(form, request.getRemoteAddr());
            redirectAttributes.addFlashAttribute("contactStatus", "success");
            return "redirect:/contact";
        } catch (RateLimitException ex) {
            bindingResult.reject("rateLimit", ex.getMessage());
        } catch (SpamProtectionException ex) {
            bindingResult.reject("spam", "Submission failed spam checks.");
        }

        form.setStartedAt(Instant.now().toEpochMilli());
        return "contact";
    }
}
