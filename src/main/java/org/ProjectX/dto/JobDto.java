package org.ProjectX.dto;

import org.ProjectX.entity.SearchUrlEntity;

import java.net.URL;

public record JobDto(
        String job,
        String companyName,
        URL companyUrl,
        SearchUrlEntity search
        ) {}
