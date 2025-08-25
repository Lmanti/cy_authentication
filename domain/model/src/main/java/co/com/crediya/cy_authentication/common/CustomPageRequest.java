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
public class CustomPageRequest {
    private int page;
    private int size;
    private SortRequest sort;
    
    public static CustomPageRequest of(int page, int size) {
        return CustomPageRequest.builder()
            .page(page)
            .size(size)
            .build();
    }
    
    public static CustomPageRequest of(int page, int size, SortRequest sort) {
        return CustomPageRequest.builder()
            .page(page)
            .size(size)
            .sort(sort)
            .build();
    }
}