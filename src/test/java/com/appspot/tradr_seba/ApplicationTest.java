package com.appspot.tradr_seba;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ApplicationTest {
    @Test
    public void testMessage() {
        assertTrue(Application.index().contains("<p>Welcome to Tradr.</p>"));
    }
}