package com.ezez.pastery.model;

import com.ezez.pastery.model.audit.DateAudit;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "paste_resource")
public class PasteResource extends DateAudit {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    @NotNull
    private String title;

    @Column(name = "body")
    @NotNull
    private String body;

    @Column(name = "url")
    private String url;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;



}
