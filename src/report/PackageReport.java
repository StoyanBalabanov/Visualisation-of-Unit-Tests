package report;

import java.util.ArrayList;

/**
 *
 * @author Stoyan Balabanov
 */
public class PackageReport extends BaseReport{
    ArrayList<ClassReport> classReports = new ArrayList<>();
    
    public PackageReport(String name, int complexity, int elements, int elementsCovered, 
            int methods, int branches, int statements, 
            int linesOfCode, int nonCommentLinesOfCode) {
        super(name, complexity, elements, elementsCovered, methods, branches, 
                statements, linesOfCode, nonCommentLinesOfCode);
    }
    
    public void addClassesToPackage(ArrayList<ClassReport> classReports) {
        for (ClassReport classReport : classReports) {
            if (classReport.getPackageName().equals(this.name)) {
                this.classReports.add(classReport);
            }
        }
    }
    
    public ArrayList<ClassReport> getClassesInPackage() {
        return classReports;
    }
    
    public ClassReport getClassByName(String name) {
        ClassReport searchedClass = null;
        for (ClassReport classReport : classReports) {
            if (classReport.getName().equals(name)) {
                searchedClass =  classReport;
            }
        }
        return searchedClass;
    }
    
    public int numClasses() {
        int numClasses = 0;
        
        numClasses = classReports.size();
        
        return numClasses;
    }
    
    @Override
    public int numPassedTests() {
        int passedTests = 0;
        
        ArrayList<ClassReport> classesInPackage = this.getClassesInPackage();
        for (ClassReport classReport : classesInPackage) {
            passedTests += classReport.numPassedTests();
        }
        
        return passedTests;
    }
    
    @Override
    public int numFailedTests() {
        int failedTests = 0;
        
        ArrayList<ClassReport> classesInPackage = this.getClassesInPackage();
        for (ClassReport classReport : classesInPackage) {
            failedTests += classReport.numFailedTests();
        }
        
        return failedTests;
    }
    
    // calculates how many statements per method are there on average
    @Override
    public double calculateMethodsPerClass() {
        // obtain number of classes in project
        double numClassesDouble = (double) getClassesInPackage().size();
        
        // convert value to double to ensure the calculation is accurate
        double methodsDouble = (double) methods;
        
        double classesPerMethod = methodsDouble / numClassesDouble;
        
        // return the results rounded by 2
        return round(classesPerMethod, 2);
    }
}
