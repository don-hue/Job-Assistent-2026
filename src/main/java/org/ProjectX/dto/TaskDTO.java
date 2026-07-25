package org.ProjectX.dto;

import org.ProjectX.entity.SearchUrlEntity;

public record TaskDTO(
        String url,
        SearchUrlEntity search
) {
}
