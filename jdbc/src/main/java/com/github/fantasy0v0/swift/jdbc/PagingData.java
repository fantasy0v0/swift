package com.github.fantasy0v0.swift.jdbc;

import java.util.List;

/**
 * 分页结果
 *
 * @param total      总记录数
 * @param totalPages 总页数
 * @param data       分页结果
 * @param <T>        结果类型
 */
public record PagingData<T>(long total, long totalPages, List<T> data) {

}
