package com.forcelate.acceptance.helpers;

import com.forcelate.acceptance.domain.processing.Execution;
import com.forcelate.acceptance.test.AcceptanceUtils;
import com.forcelate.acceptance.domain.processing.CaseStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.forcelate.acceptance.configuration.ApplicationConstants.UNCERTAIN_COUNT;
import static com.forcelate.acceptance.domain.processing.CaseStatus.*;
import static com.forcelate.acceptance.test.RandomUtils.randomSmallInteger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class CountUtilsTest {

    @Configuration
    static class ContextConfiguration {

        @Bean
        CountUtils countUtils() {
            return new CountUtils();
        }
    }

    @Autowired
    private CountUtils utilsUnderTest;

    @Test
    public void executionsStatusesNull() {
        // Act
        Map<CaseStatus, Long> result = utilsUnderTest.executionsStatuses(null);

        // Assert
        assertTrue(result.size() == 0);
    }

    @Test
    public void executionsStatuses() {
        // Arrange
        int serverDowns = randomSmallInteger();
        int failures = randomSmallInteger();
        int unexpected = randomSmallInteger();
        int missing = randomSmallInteger();
        int success = randomSmallInteger();
        List<Execution> executions = new ArrayList<>();
        // each status
        executions.add(AcceptanceUtils.randomExecutionByStatusCount(SERVER_DOWN, serverDowns));
        executions.add(AcceptanceUtils.randomExecutionByStatusCount(FAILURE, failures));
        executions.add(AcceptanceUtils.randomExecutionByStatusCount(UNEXPECTED, unexpected));
        executions.add(AcceptanceUtils.randomExecutionByStatusCount(MISSING, missing));
        executions.add(AcceptanceUtils.randomExecutionByStatusCount(SUCCESS, success));
        // UNEXPECTED, SUCCESS status separate
        executions.add(AcceptanceUtils.randomExecutionByStatuses(UNEXPECTED, SUCCESS));

        // Act
        Map<CaseStatus, Long> result = utilsUnderTest.executionsStatuses(executions);

        // Assert
        assertEquals(serverDowns, result.get(SERVER_DOWN).intValue());
        assertEquals(failures, result.get(FAILURE).intValue());
        assertEquals(unexpected + 1, result.get(UNEXPECTED).intValue());
        assertEquals(UNCERTAIN_COUNT, result.get(EMPTY).intValue());
        assertEquals(missing, result.get(MISSING).intValue());
        assertEquals(success + 1, result.get(SUCCESS).intValue());
        assertTrue(result instanceof TreeMap);
    }
}
