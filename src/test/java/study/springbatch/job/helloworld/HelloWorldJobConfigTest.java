package study.springbatch.job.helloworld;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.springbatch.SpringBatchTestConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringBatchTest
@SpringBootTest(classes = {SpringBatchTestConfig.class, HelloWorldJobConfig.class})
class HelloWorldJobConfigTest {

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void success() throws Exception {
        // given
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // expect
        assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
    }

}