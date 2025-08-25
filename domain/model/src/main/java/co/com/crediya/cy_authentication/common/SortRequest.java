package co.com.crediya.cy_authentication.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SortRequest {
    private String property;
    private Direction direction;
    
    public enum Direction {
        ASC, DESC
    }
    
    public static SortRequest by(String property) {
        return SortRequest.builder()
            .property(property)
            .direction(Direction.ASC)
            .build();
    }
    
    public static SortRequest by(String property, Direction direction) {
        return SortRequest.builder()
            .property(property)
            .direction(direction)
            .build();
    }
}