package study.springbatch.job.file;

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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import study.springbatch.job.file.dto.Player;
import study.springbatch.job.file.dto.PlayerYears;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class FileDataReadWriteConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fileReaderWriteJob(Step fileReadWriteStep) {
        return jobBuilderFactory.get("fileReaderWriteJob")
                .incrementer(new RunIdIncrementer())
                .start(fileReadWriteStep)
                .build();
    }

    @Bean
    @JobScope
    public Step fileReadWriteStep(ItemReader playerItemReader, ItemProcessor playerItemProcessor, ItemWriter playerItemWriter) {
        return stepBuilderFactory.get("fileReadWriteStep")
                .<Player, PlayerYears>chunk(5)
                .reader(playerItemReader)
//                .writer(items -> items.forEach(System.out::println))
                .processor(playerItemProcessor)
                .writer(playerItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Player> playerItemReader() {
        return new FlatFileItemReaderBuilder<Player>()
                .name("playerItemReader")
                .resource(new FileSystemResource("Player.csv"))
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new PlayerFieldSetMapper())
                .linesToSkip(1)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Player, PlayerYears> playerItemProcessor() {
        return PlayerYears::new;
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<PlayerYears> playerItemWriter() {
        BeanWrapperFieldExtractor<PlayerYears> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"ID", "lastName", "position", "yearsExperience"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<PlayerYears> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FileSystemResource outputResource = new FileSystemResource("players_output.txt");

        return new FlatFileItemWriterBuilder<PlayerYears>()
                .name("playerItemWriter")
                .resource(outputResource)
                .lineAggregator(lineAggregator)
                .build();
    }

}
