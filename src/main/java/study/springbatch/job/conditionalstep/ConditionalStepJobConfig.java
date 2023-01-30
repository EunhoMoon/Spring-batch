package study.springbatch.job.conditionalstep;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ConditionalStepJobConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job conditionalStepJob(
            Step conditionalStartStep,
            Step conditionalAllStep,
            Step conditionalFailStep,
            Step conditionalCompletedStep) {
        return jobBuilderFactory.get("conditionalStepJob")
                .incrementer(new RunIdIncrementer())
                .start(conditionalStartStep)
                .on("FAILED").to(conditionalFailStep)
                .from(conditionalStartStep)
                .on("COMPLETED").to(conditionalCompletedStep)
                .from(conditionalStartStep)
                .on("*").to(conditionalAllStep)
                .end()
                .build();
    }

    @Bean
    @JobScope
    public Step conditionalStartStep() {
        return stepBuilderFactory.get("conditionalStartStep")
                .tasklet(new Tasklet() {
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("Conditional Start Step");

//                        return RepeatStatus.FINISHED;
                        throw new Exception("Exception!");
                    }
                })
                .build();
    }

    @Bean
    @JobScope
    public Step conditionalAllStep() {
        return stepBuilderFactory.get("conditionalAllStep")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("Conditional All Step");

                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    @JobScope
    public Step conditionalFailStep() {
        return stepBuilderFactory.get("conditionalFailStep")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("Conditional Fail Step");

                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    @JobScope
    public Step conditionalCompletedStep() {
        return stepBuilderFactory.get("conditionalCompletedStep")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("Conditional Completed Step");

                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

}
