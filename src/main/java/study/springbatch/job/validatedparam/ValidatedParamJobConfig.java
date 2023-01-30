package study.springbatch.job.validatedparam;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import study.springbatch.job.validatedparam.validator.FileParamValidator;

import java.util.Arrays;

import static org.springframework.batch.repeat.RepeatStatus.FINISHED;

/**
 * desc : 파일 이름 파라미터 전달 및 검증
 * run : --spring.batch.job.names = validatedPramJob -fileName=test.csv
 */
@Configuration
@RequiredArgsConstructor
public class ValidatedParamJobConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job validatedPramJob(Step validatedPramStep) {
        return jobBuilderFactory.get("validatedPramJob")
                .incrementer(new RunIdIncrementer())
//                .validator(new FileParamValidator())
                .validator(multipleValidator())
                .start(validatedPramStep)
                .build();
    }

    private CompositeJobParametersValidator multipleValidator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(new FileParamValidator()));

        return validator;
    }

    @Bean
    @JobScope
    public Step validatedPramStep(Tasklet validatedPramTasklet) {
        return stepBuilderFactory.get("validatedPramStep")
                .tasklet(validatedPramTasklet)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet validatedPramTasklet(@Value("#{jobParameters['fileName']}") String fileName) {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Validated Param Tasklet");
                return FINISHED;
            }
        };
    }

}
