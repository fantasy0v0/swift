package com.github.fantasy0v0.swift.spring;

import com.github.fantasy0v0.swift.connection.ManagedSavepoint;
import org.springframework.transaction.TransactionStatus;

/**
 * @author fan 2026/1/16
 */
class ManagedSavepointImpl implements ManagedSavepoint {

  private final TransactionStatus status;

  private final Object savepoint;

  ManagedSavepointImpl(TransactionStatus status) {
    this.status = status;
    savepoint = status.createSavepoint();
  }

  @Override
  public void release() {
    status.releaseSavepoint(savepoint);
  }

  @Override
  public void rollback() {
    status.rollbackToSavepoint(savepoint);
  }
}
