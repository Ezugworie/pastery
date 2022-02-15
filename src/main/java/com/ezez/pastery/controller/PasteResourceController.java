package com.ezez.pastery.controller;

import com.ezez.pastery.exception.ResourceNotFoundException;
import com.ezez.pastery.model.PasteResource;
import com.ezez.pastery.repository.PasteResourceRepository;
import com.ezez.pastery.service.PasteResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
@Validated
public class PasteResourceController {

    @Autowired
    PasteResourceRepository pasteResourceRepository;

    @Autowired
    PasteResourceService pasteResourceService;

    @PostMapping("/resource")
    public ResponseEntity<PasteResource> createResource(@Valid @RequestBody PasteResource pasteResource) {
        PasteResource result = pasteResourceService.createPasteResource(pasteResource);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/resources")
    public List<PasteResource> getAllResources() {
        return pasteResourceRepository.findAll();
    }

    @GetMapping("/resource/{id}")
    public PasteResource getResourceById(@Valid @PathVariable Long id) {
        PasteResource result = pasteResourceRepository.findById(id).get();
        return result;
    }

    @PutMapping("/resource/{id}")
    public ResponseEntity<PasteResource> updateResource(@Valid @RequestBody PasteResource pasteResource, @PathVariable Long id) {
        PasteResource result = pasteResourceService.updatePasteResource(pasteResource, id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/resource/{id}")
    public Map<String, Boolean> deleteResourceById(@Valid @PathVariable Long id) throws ResourceNotFoundException {
        PasteResource result = pasteResourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paste resource",  "id", id));
        pasteResourceRepository.delete(result);
        Map<String, Boolean> response = new HashMap();
        response.put("deleted successfully", Boolean.TRUE);
        return response;
    }
}
