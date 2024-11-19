package web.thaiticketmajor.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class ChangeZoneRequest {
    private List<Integer> selectedSeatIds;
    private Integer selectedZoneId;

    // Getters and Setters
}

