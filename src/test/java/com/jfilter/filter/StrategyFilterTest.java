package com.jfilter.filter;

import com.jfilter.mock.MockClasses;
import com.jfilter.mock.MockHttpRequest;
import com.jfilter.mock.MockMethods;
import com.jfilter.request.RequestSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.MethodParameter;

public class StrategyFilterTest {
    private StrategyFilter strategyFilter;

    @Before
    public void init() {
        MethodParameter methodParameter = MockMethods.mockIgnoreStrategyMethod();
        strategyFilter = new StrategyFilter(methodParameter);
    }

    @Test
    public void testUserSession() {
        RequestSession requestSession = new RequestSession(MockHttpRequest.getMockUserRequest());
        FilterFields filterFields = strategyFilter.getFields(MockClasses.getUserMock(), requestSession);
        Assert.assertEquals(1, filterFields.getFieldsMap().size());
    }

    @Test
    public void testClearSession() {
        RequestSession requestSession = new RequestSession(MockHttpRequest.getMockClearRequest());
        FilterFields filterFields = strategyFilter.getFields(MockClasses.getUserMock(), requestSession);
        Assert.assertEquals(0, filterFields.getFieldsMap().size());
    }
}
