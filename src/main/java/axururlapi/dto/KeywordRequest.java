package axururlapi.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class KeywordRequest {

    @NotBlank(message = "Keyword is required")
    @Size(min = 4, max = 32, message = "Keyword must be between 4 and 32 characters")
    private String keyword;

}
