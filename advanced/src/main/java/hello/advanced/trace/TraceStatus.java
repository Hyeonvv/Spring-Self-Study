package hello.advanced.trace;

import lombok.Getter;

@Getter
public class TraceStatus {

    private TraceId traceId;
    private Long startTimeMs; // 종료 시간 측정을 위해
    private String message; // 시작 시 사용한 메시지

    public TraceStatus(TraceId traceId, Long startTimeMs, String message) {
        this.traceId = traceId;
        this.startTimeMs = startTimeMs;
        this.message = message;
    }
}
