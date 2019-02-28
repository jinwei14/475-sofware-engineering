//package ic.doc.be;
//
//public class MyCaller {
//    private final PingService pingService;
//    private final CircuitBreaker<Void> circuitBreaker;
//    private final BreakHandler<Void> breakHandler;
//    private final BreakStrategy<Void> breakStrategy;
//
//    public MyCaller() {
//        this.pingService = new PingService();
//
//        // prepare the circuit breaker
//        this.circuitBreaker = new DefaultCircuitBreaker<>();
//        this.breakStrategy = new SingleExecutionAllowedStrategy<>(); // one execution a time allowed
//        this.breakHandler = new NoActionHandler<>(); // we don't care about "pings" which were skipped
//    }
//
//    public void returnString(int pingId) throws IOException {
//        try {
//            // NOTE: notice cast to VoidTask below
//            circuitBreaker.execute((VoidTask) () -> pingService.sendPing(pingId), breakStrategy, breakHandler);
//        } catch (TaskExecutionException e) {
//            System.out.println("Calling ping resulted in exception: " + e.getTaskException());
//            throw new IOException(e.getTaskException().getMessage(), e.getTaskException());
//        }
//    }
//}
