package study.springbatch.job.dbreadnwrite;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import study.springbatch.core.domain.account.Accounts;
import study.springbatch.core.domain.account.AccountsRepository;
import study.springbatch.core.domain.orders.Orders;
import study.springbatch.core.domain.orders.OrdersRepository;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class TrMigrationConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final OrdersRepository ordersRepository;

    private final AccountsRepository accountsRepository;

    @Bean
    public Job trMigrationJob(Step trMigrationStep) {
        return jobBuilderFactory.get("trMigrationJob")
                .incrementer(new RunIdIncrementer())
                .start(trMigrationStep)
                .build();
    }

    @Bean
    @JobScope
    public Step trMigrationStep(ItemReader trOrdersReader, ItemProcessor trOrdersProcessor, ItemWriter trOrdersWriter) {
        return stepBuilderFactory.get("trMigrationStep")
                .<Orders, Accounts>chunk(5)
                .reader(trOrdersReader)
//                .writer(new ItemWriter() {
//                    @Override
//                    public void write(List items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .processor(trOrdersProcessor)
                .writer(trOrdersWriter)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Orders> trOrders() {
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrdersReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Orders, Accounts> trOrdersProcessor() {
        return Accounts::new;
    }

    @Bean
    @StepScope
    public ItemWriter<Accounts> trOrdersWriter() {
        return items -> items.forEach(accountsRepository::save);
    }

//    @Bean
//    @StepScope
//    public RepositoryItemWriter<Accounts> trOrdersWriter() {
//        return new RepositoryItemWriterBuilder<Accounts>()
//                .repository(accountsRepository)
//                .methodName("save")
//                .build();
//    }

}
