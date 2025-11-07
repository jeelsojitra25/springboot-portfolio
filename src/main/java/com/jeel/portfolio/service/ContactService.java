package com.jeel.portfolio.service;

import com.jeel.portfolio.domain.contact.ContactMessage;
import com.jeel.portfolio.domain.contact.ContactMessageRepository;
import com.jeel.portfolio.web.dto.ContactForm;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;

@Service
public class ContactService {

    private static final Duration MIN_FORM_DURATION = Duration.ofSeconds(2);
    private static final Duration RATE_LIMIT_WINDOW = Duration.ofMinutes(1);

    private final ContactMessageRepository contactMessageRepository;
    private final SubmissionRateLimiter submissionRateLimiter;

    public ContactService(ContactMessageRepository contactMessageRepository,
                          SubmissionRateLimiter submissionRateLimiter) {
        this.contactMessageRepository = contactMessageRepository;
        this.submissionRateLimiter = submissionRateLimiter;
    }

    public void submit(ContactForm form, String ipAddress) {
        guardHoneypot(form);
        guardFormDuration(form);
        guardRateLimit(ipAddress);

        var message = new ContactMessage(form.getName(), form.getEmail(), form.getMessage());
        contactMessageRepository.save(message);
    }

    private void guardHoneypot(ContactForm form) {
        if (StringUtils.hasText(form.getNotes())) {
            throw new SpamProtectionException("Spam detected.");
        }
    }

    private void guardFormDuration(ContactForm form) {
        if (form.getStartedAt() == null) {
            throw new SpamProtectionException("Missing form timestamp.");
        }
        var started = Instant.ofEpochMilli(form.getStartedAt());
        if (Duration.between(started, Instant.now()).compareTo(MIN_FORM_DURATION) < 0) {
            throw new SpamProtectionException("Form submitted too quickly.");
        }
    }

    private void guardRateLimit(String ipAddress) {
        if (!submissionRateLimiter.tryAcquire(ipAddress, RATE_LIMIT_WINDOW)) {
            throw new RateLimitException("Please wait before sending another message.");
        }
    }
}
