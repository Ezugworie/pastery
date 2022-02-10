package com.ezez.pastery.service.Impl;

import com.ezez.pastery.exception.ResourceNotFoundException;
import com.ezez.pastery.model.PasteResource;
import com.ezez.pastery.repository.PasteResourceRepository;
import com.ezez.pastery.service.PasteResourceService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@Service
public class PasteResourceServiceImpl implements PasteResourceService {
    @Autowired
    PasteResourceRepository pasteResourceRepository;

    public PasteResource createPasteResource(@NotBlank PasteResource pasteResource) {
        pasteResource.setExpiryDate(LocalDateTime.now().plusHours(24));
        String randomString = RandomStringUtils.randomAlphanumeric(6);
        pasteResource.setUrl(randomString);
        pasteResourceRepository.save(pasteResource);
        return pasteResource;
    }

    @Override
    public PasteResource updatePasteResource(@NotBlank PasteResource newPasteResource, @NotBlank Long id) {
        PasteResource pasteResource = pasteResourceRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Paste Resource", "id", id));
        pasteResource.setBody(newPasteResource.getBody());
        pasteResource.setTitle(newPasteResource.getTitle());
        pasteResource.setExpiryDate(LocalDateTime.now().plusHours(24));
        return pasteResource;
    }
}
