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
public class Page<T> {
    private java.util.List<T> content;
    private long totalElements;
    private int totalPages;
    private int number;
    private int size;
}
