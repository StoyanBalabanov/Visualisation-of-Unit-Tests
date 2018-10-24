package report;

/**
 *
 * @author Stoyan Balabanov
 */
public class TestCaseReport {
    private String testName;
    private double time;
    // true if test has passed false if it has failed
    private boolean hasTestPassed;
    private String errorType = "";
    private String errorMessage = "";
    private String errorTrace = "";
    
    public TestCaseReport(String testName, double time, boolean hasTestPassed) {
        this.testName = testName;
        this.time = time;
        this.hasTestPassed = hasTestPassed;
    }
    
    public String getTestName() {
        return testName;
    }
    
    public double getTime() {
        return time;
    }
    
    public boolean getHasTestPassed() {
        return hasTestPassed;
    }
    
    public String getErrorType() {
        return errorType;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public String getErrorTrace() {
        return errorTrace;
    }
    
    public void setTestName(String testName) {
        this.testName = testName;
    }
    
    public void setTime(double time) {
        this.time = time;
    }
    
    public void setHasTestPassed(boolean hasTestPassed) {
        this.hasTestPassed = hasTestPassed;
    }
    
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public void setErrorTrace(String errorTrace) {
        this.errorTrace = errorTrace;
    }
    
    @Override
    public String toString() {
        String toString =  "Test Name: " + testName + "\n" + "Time: " + time + "\n";
        
        if (hasTestPassed == false) {
            toString += errorTrace + "\n";
        }
        
        return toString;
    }
    
    
}
