package com.github.fantasy0v0.swift.spring;

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
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);
    if (null != level) {
      def.setIsolationLevel(level);
    }
    this.status = txManager.getTransaction(def);
    status.createSavepoint();
  }

  @Override
  public void commit() {
    LogUtil.common().debug("txManager commit");
    this.txManager.commit(this.status);
  }

  @Override
  public void rollback() {
    LogUtil.common().debug("txManager rollback");
    this.txManager.rollback(this.status);
  }
}
