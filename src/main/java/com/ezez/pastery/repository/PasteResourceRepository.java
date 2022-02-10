package com.ezez.pastery.repository;

import com.ezez.pastery.model.PasteResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;

@Repository
public interface PasteResourceRepository extends JpaRepository<PasteResource, Long> {

}
