package pl.dealsniper.core.model;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Source {

    private UUID id;

    private UUID userId;

    private String filteredUrl;
}
