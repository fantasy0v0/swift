package com.github.fantasy0v0.swift;

import java.sql.ResultSet;
import java.sql.SQLException;

public

interface FunctionWithException<R> {

  R apply(ResultSet resultSet) throws SQLException;

}
