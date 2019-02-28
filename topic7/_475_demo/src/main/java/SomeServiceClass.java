public class SomeServiceClass {
    public Long somePossiblyLongRunningMethod(int callId) {
        // some heavy and long running operation here
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {}
        return (long) 1;
    }
}