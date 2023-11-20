package axururlapi.entity;


import axururlapi.enun.SearchStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "searches")
public class SearchEntity {

    @Id
    private String id;

    @Column(length = 32, nullable = false)
    private String keyword;

    @Enumerated(EnumType.STRING) // Se for usar Enumerated
    @Column(nullable = false)
    @NotNull(message = "Search status cannot be null")
    private SearchStatus searchStatus;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "search_results", joinColumns = @JoinColumn(name = "search_id"))
    @Column(name = "url")
    private List<String> urls;
}
