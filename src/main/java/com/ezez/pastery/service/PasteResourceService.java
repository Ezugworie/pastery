package com.ezez.pastery.service;

import com.ezez.pastery.model.PasteResource;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;

@Service
public interface PasteResourceService {

    PasteResource createPasteResource(@NotBlank PasteResource pasteResource);

    PasteResource updatePasteResource(@NotBlank PasteResource pasteResource, @NotBlank Long id);

}
