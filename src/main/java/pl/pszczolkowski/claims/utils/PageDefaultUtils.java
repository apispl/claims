package pl.pszczolkowski.claims.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageDefaultUtils {

    PageDefaultUtils() {
    }

    public static Pageable getPageWithDefaults(Integer page, Integer size, String direction, String sort) {
        return PageRequest.of(defaultPageSize(page),
                size == null ? 10 : size,
                StringUtils.isEmpty(direction) ? Sort.Direction.DESC : determineSortType(direction),
                StringUtils.isEmpty(sort) ? "id" : sort);
    }

    private static int defaultPageSize(Integer page) {
        return page == null ? 0 : page;
    }

    private static Sort.Direction determineSortType(String direction) {
        if (direction == null)
            return Sort.Direction.ASC;
        return "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }
}
