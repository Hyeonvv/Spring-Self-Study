package hello.advanced.trace.logtrace;

import hello.advanced.trace.TraceStatus;
import org.junit.jupiter.api.Test;

class FieldLogTraceTest {

    FieldLogTrace trace = new FieldLogTrace();

    @Test
    void begin_end_level2() {
        TraceStatus trace1 = trace.begin("hello1");
        TraceStatus trace2 = trace.begin("hello2");

        trace.end(trace2);
        trace.end(trace1);
    }

    @Test
    void begin_exception_level2() {
        TraceStatus trace1 = trace.begin("hello1");
        TraceStatus trace2 = trace.begin("hello2");

        trace.exception(trace2, new IllegalStateException());
        trace.exception(trace1, new IllegalStateException());
    }
}