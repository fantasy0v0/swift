package com.github.fantasy0v0.swift.jdbc;

/**
 * 分页参数
 *
 * @param number 页码从0开始
 * @param size   每页大小
 */
public record Paging(long number, long size) {
}
