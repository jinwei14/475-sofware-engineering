package ic.doc.be;

import net.secodo.jcircuitbreaker.breaker.CircuitBreaker;
import net.secodo.jcircuitbreaker.breaker.impl.DefaultCircuitBreaker;
import net.secodo.jcircuitbreaker.breakhandler.BreakHandler;
import net.secodo.jcircuitbreaker.breakhandler.impl.NoActionHandler;
import net.secodo.jcircuitbreaker.breakstrategy.BreakStrategy;
import net.secodo.jcircuitbreaker.breakstrategy.impl.SingleExecutionAllowedStrategy;
import net.secodo.jcircuitbreaker.exception.TaskExecutionException;
import org.apache.http.client.fluent.Request;

import java.util.concurrent.*;

public class WeatherDataServer extends BackEndWebServer {

    private static final int DEFAULT_PORT = 5002;

    public WeatherDataServer(int port, DataSource dataSource) throws Exception {
        super(port, dataSource);
    }

    private static class WeatherDataSource implements DataSource {

        private final ExecutorService executorService = Executors.newFixedThreadPool(1);
        private long lastRequestTime;

        private static CircuitBreaker<String> circuitBreaker;
        private static BreakHandler<String> breakHandler;
        private static BreakStrategy<String> breakStrategy;
        // prepare the circuit breaker


        public void WeatherDataServer(){
            this.circuitBreaker = new DefaultCircuitBreaker<>();
            this.breakStrategy = new SingleExecutionAllowedStrategy<>(); // one execution a time allowed
            this.breakHandler = new NoActionHandler<>(); // we don't care about "pings" which were skipped
        }

        @Override
        public String data() {
//            return "Latest temp forecast: " + calculateTemperatureForecast() + " celcius";

            try {
                return  circuitBreaker.execute( ()  -> "Latest temp forecast: " + calculateTemperatureForecast() + " celcius", breakStrategy, breakHandler);
            } catch (TaskExecutionException e) {
                // TaskExecutionException cBuild, Execution, Deployment Build, Execution, Deployment an be thrown by circuit breaker "only and only" iProject Structuref myService.somePossiblyLongRunningMethod thrown exception
                System.out.println("Calling somePossiblyLongRunningMethod resulted in exception: " + e.getTaskException());
                throw new RuntimeException(e.getTaskException().getMessage(), e.getTaskException());
                // getTaskExcepition() returns possible exception thrown by myService.somePossiblyLongRunningMethod(serviceParam)
            }

        }

        // code here simulates the server getting overloaded when requests come frequently,
        // by adding an artificial delay to the processing of requests

        private int calculateTemperatureForecast() {
            Future<Integer> future = executorService.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    busyWork();
                    return (int) (30 - (Math.random() * 30));
                }
            });

            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        private void busyWork() {
            long timeSinceLastRequest = currentTime() - lastRequestTime;
            lastRequestTime = currentTime();
            long delay = Math.max(0, 15000 - timeSinceLastRequest);
            if (delay > 8000) {
                System.out.println("WARNING: Overloaded... ");
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                new RuntimeException(e);
            }
        }

        private long currentTime() {
            return System.currentTimeMillis();
        }
    }

    private static int portFrom(String[] args) {
        if (args.length < 1) {
            return DEFAULT_PORT;
        }
        return Integer.valueOf(args[0]);
    }

    public static void main(String[] args) throws Exception {
        new WeatherDataServer(portFrom(args), new WeatherDataSource());
    }
}

//    The weather data service contains some code to simulate it getting overloaded by bursts of requests.
//    If you refresh the webpage a few times in quick succession you should see this effect.
//    Build a circuit breaker between the front end server and the weather data server to control requests
//    to the weather service, and try to make sure that the user gets a page back quickly, even if it does not contain both news and weather data.
//    You can change whatever you like in the code.
