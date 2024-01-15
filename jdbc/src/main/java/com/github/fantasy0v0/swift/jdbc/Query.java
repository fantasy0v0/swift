package com.github.fantasy0v0.swift.jdbc;

import java.util.List;

public record Query(String sql, List<Object> params) {
}
