package com.gongyi.platform.common;

import java.util.List;

public record PageResult<T>(List<T> content, long totalElements, int totalPages, int number, int size) {
}
