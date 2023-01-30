package study.springbatch.job.dbreadnwrite;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.springbatch.core.domain.account.AccountsRepository;
import study.springbatch.core.domain.orders.Orders;
import study.springbatch.core.domain.orders.OrdersRepository;
import study.springbatch.SpringBatchTestConfig;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBatchTest
@SpringBootTest(classes = {SpringBatchTestConfig.class, TrMigrationConfig.class})
class TrMigrationConfigTest {

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    AccountsRepository accountsRepository;

    @AfterEach
    public void cleanUpEach() {
        ordersRepository.deleteAll();
        accountsRepository.deleteAll();
    }

    @Test
    void success_noData() throws Exception {
        // when
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // expect
        assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        assertEquals(accountsRepository.count(), 0);
    }

    @Test
    void success_existData() throws Exception {
        // given
        Orders orders1 = new Orders(null, "kakao gift", 15000, LocalDate.now());
        Orders orders2 = new Orders(null, "naver gift", 13000, LocalDate.now());

        ordersRepository.save(orders1);
        ordersRepository.save(orders2);

        // when
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // then
        assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        assertEquals(accountsRepository.count(), 2);
    }

}