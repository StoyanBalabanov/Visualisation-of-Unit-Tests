package report;

import java.util.ArrayList;

/**
 *
 * @author Stoyan Balabanov
 */
public class ClassReport extends BaseReport{
    private ArrayList<TestCaseReport> testCases;
    private String packageName;
    private String recentChanges = "";
    
    public ClassReport(String name, int complexity, int elements, int elementsCovered, 
            int methods, int branches, int statements, 
            int linesOfCode, int nonCommentLinesOfCode, String packageName) {
        super(name, complexity, elements, elementsCovered, methods, branches, 
                statements, linesOfCode, nonCommentLinesOfCode);
        this.packageName = packageName;
    }
    
    public ArrayList<TestCaseReport> getTestCases() {
        return testCases;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public String getRecentChanges() {
        return recentChanges;
    }
    
    public void setTestCases(ArrayList<TestCaseReport> testCases) {
        this.testCases = testCases;
    }
    
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public void setRecentChanges(String recentChanges) {
        this.recentChanges = recentChanges;
    }
    
    // returns all tests that have passed successfully in the package
    @Override
    public int numPassedTests() {
        int passedTests = 0;
        
        for (TestCaseReport testCase : testCases) {
            if (testCase.getHasTestPassed()) {
                passedTests += 1;
            }
        }
        
        return passedTests;
    }
    
    // returns all tests that have failed in the package
    @Override
    public int numFailedTests() {
        int failedTests = 0;
        
        for (TestCaseReport testCase : testCases) {
            if (!testCase.getHasTestPassed()) {
                failedTests += 1;
            }
        }
        
        return failedTests;
    }
    
    @Override
    public String toString() {
        String toString = super.toString() + "Package Name: " + packageName + "\n";
        for (TestCaseReport testCase : testCases) {
            toString += testCase.toString();
        }
        
        return toString;
    }

    @Override
    public double calculateMethodsPerClass() {
        return methods;
    }
}
