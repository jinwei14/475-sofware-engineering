import net.secodo.jcircuitbreaker.breaker.CircuitBreaker;
import net.secodo.jcircuitbreaker.breaker.impl.DefaultCircuitBreaker;
import net.secodo.jcircuitbreaker.breakhandler.BreakHandler;
import net.secodo.jcircuitbreaker.breakhandler.impl.ReturnStaticValueHandler;
import net.secodo.jcircuitbreaker.breakstrategy.BreakStrategy;
import net.secodo.jcircuitbreaker.breakstrategy.impl.LimitedConcurrentExecutionsStrategy;
import net.secodo.jcircuitbreaker.exception.TaskExecutionException;

public class MyCaller {

    private final SomeServiceClass myService;
    private final CircuitBreaker<Long> circuitBreaker;
    private final BreakHandler<Long> breakHandler;
    private final BreakStrategy<Long> breakStrategy;

    public MyCaller() {
        this.myService = new SomeServiceClass();

        // prepare the circuit breaker
        this.circuitBreaker = new DefaultCircuitBreaker<>();
        this.breakStrategy = new LimitedConcurrentExecutionsStrategy<>(3l); // we define a break strategy that allows only 3 executions in parallel
        this.breakHandler = new ReturnStaticValueHandler<>(-1l); // we define a break handler which returns -1 in case circuit breaker prevents execution of myService.somePossiblyLongRunningMethod
    }

    public Long runService(int serviceParam)  {
        try {
            System.out.println("213");
            return circuitBreaker.execute(() -> myService.somePossiblyLongRunningMethod(serviceParam), breakStrategy, breakHandler);

        } catch (TaskExecutionException e) {
            // TaskExecutionException can be thrown by circuit breaker "only and only" if myService.somePossiblyLongRunningMethod thrown exception
            System.out.println("Calling somePossiblyLongRunningMethod resulted in exception: " + e.getTaskException());
            throw new RuntimeException(e.getTaskException().getMessage(), e.getTaskException()); // getTaskExcepition() returns possible exception thrown by myService.somePossiblyLongRunningMethod(serviceParam)
        }
    }


        public static void main(String[] args) {
            MyCaller run  = new MyCaller();
            run.runService(3);
            run.runService(3);
            run.runService(3);

        }


}