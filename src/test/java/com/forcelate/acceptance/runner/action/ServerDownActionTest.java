package com.forcelate.acceptance.runner.action;

import com.forcelate.acceptance.domain.processing.CaseCall;
import com.forcelate.acceptance.holder.ExecutionsHolder;
import com.forcelate.acceptance.domain.processing.CaseMapping;
import com.forcelate.acceptance.domain.processing.CaseStatus;
import org.junit.Test;
import org.junit.runner.Description;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.forcelate.acceptance.configuration.ApplicationErrorMessages.CONNECTION_LOST_MESSAGE;
import static com.forcelate.acceptance.configuration.ApplicationErrorMessages.SERVER_DOWN_MESSAGE;
import static com.forcelate.acceptance.test.RandomUtils.randomString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServerDownActionTest extends AbstractActionRunner{

    @Autowired private Throwable e;

    @Autowired private ServerDownAction actionUnderTest;

    @Test
    public void selectFalse() {
        // Arrange
        when(e.getMessage()).thenReturn(randomString());

        // Act
        boolean select = actionUnderTest.select(e);

        // Assert
        assertFalse(select);
    }

    @Test
    public void selectTrueWhenServerDown() {
        // Arrange
        when(e.getMessage()).thenReturn(randomString() + SERVER_DOWN_MESSAGE + randomString());

        // Act
        boolean select = actionUnderTest.select(e);

        // Assert
        assertTrue(select);
    }

    @Test
    public void selectTrueWhenConnectionLost() {
        // Arrange
        when(e.getMessage()).thenReturn(randomString() + CONNECTION_LOST_MESSAGE + randomString());

        // Act
        boolean select = actionUnderTest.select(e);

        // Assert
        assertTrue(select);
    }

    @Test
    public void processWhenServerDown() {
        // Arrange
        Description description =  mock(Description.class);
        when(e.getMessage()).thenReturn(randomString() + SERVER_DOWN_MESSAGE + randomString());

        // Act
        actionUnderTest.select(e);
        actionUnderTest.process(description);

        // Assert
        Optional<Map.Entry<CaseMapping, List<CaseCall>>> optional = ExecutionsHolder.getExecuted().
                entrySet().stream().findFirst();
        if (optional.isPresent()) {
            CaseCall call = optional.get().getValue().get(0);
            assertEquals(CaseStatus.SERVER_DOWN, call.getStatus());
        }
    }

    @Test
    public void processWhenConnectionLost() {
        // Arrange
        Description description =  mock(Description.class);
        when(e.getMessage()).thenReturn(randomString() + CONNECTION_LOST_MESSAGE + randomString());

        // Act
        actionUnderTest.select(e);
        actionUnderTest.process(description);

        // Assert
        Optional<Map.Entry<CaseMapping, List<CaseCall>>> optional = ExecutionsHolder.getExecuted().
                entrySet().stream().findFirst();
        if (optional.isPresent()) {
            CaseCall call = optional.get().getValue().get(0);
            assertEquals(CaseStatus.SERVER_DOWN, call.getStatus());
        }
    }
}