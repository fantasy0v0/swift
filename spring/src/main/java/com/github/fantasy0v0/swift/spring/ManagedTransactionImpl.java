package com.github.fantasy0v0.swift.spring;

import com.github.fantasy0v0.swift.connection.ManagedSavepoint;
import com.github.fantasy0v0.swift.connection.ManagedTransaction;
import com.github.fantasy0v0.swift.util.LogUtil;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

class ManagedTransactionImpl implements ManagedTransaction {

  private final PlatformTransactionManager txManager;

  private final TransactionStatus status;

  ManagedTransactionImpl(Integer level) {
    this.txManager = ContextUtil.getBean(PlatformTransactionManager.class);
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    if (null != level) {
      def.setIsolationLevel(level);
    }
    this.status = txManager.getTransaction(def);
  }

  @Override
  public void commit() {
    LogUtil.common().debug("txManager commit");
    txManager.commit(status);
  }

  @Override
  public void rollback() {
    LogUtil.common().debug("txManager rollback");
    txManager.rollback(status);
  }

  @Override
  public ManagedSavepoint createSavepoint() {
    return new ManagedSavepointImpl(status);
  }
}
