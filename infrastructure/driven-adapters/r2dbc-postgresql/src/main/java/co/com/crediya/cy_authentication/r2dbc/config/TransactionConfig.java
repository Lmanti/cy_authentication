package co.com.crediya.cy_authentication.r2dbc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Configuration
public class TransactionConfig {
    @Bean
    @Primary
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        definition.setReadOnly(false);
        definition.setTimeout(30);
        definition.setName("WriteTransaction");
        return TransactionalOperator.create(transactionManager, definition);
    }
    
    @Bean
    public TransactionalOperator readOnlyTransactionalOperator(ReactiveTransactionManager transactionManager) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        definition.setReadOnly(true);
        definition.setTimeout(15);
        definition.setName("ReadOnlyTransaction");
        return TransactionalOperator.create(transactionManager, definition);
    }
}
