package report;

import java.util.ArrayList;

/**
 *
 * @author Stoyan Balabanov
 */
public class ProjectReport extends BaseReport {
    ArrayList<PackageReport> packages;
    
    public ProjectReport(String name, int complexity, int elements, int elementsCovered, 
            int methods, int branches, int statements, 
            int linesOfCode, int nonCommentLinesOfCode) {
        super(name, complexity, elements, elementsCovered, methods, branches, 
                statements, linesOfCode, nonCommentLinesOfCode);
    }
    
    public void addPackages(ArrayList<PackageReport> packages) {
        this.packages = packages;
    }
    
    public ArrayList<PackageReport> getPackages() {
        return packages;
    }
    
    public ArrayList<ClassReport> getClasses() {
        ArrayList<ClassReport> classes = new ArrayList<>();
        
        for (PackageReport packageInProject : packages) {
            ArrayList<ClassReport> classesInPackage = packageInProject.getClassesInPackage();
            for (ClassReport classInPackage : classesInPackage) {
                classes.add(classInPackage);
            }            
        }
        
        return classes;
    }
    
    public PackageReport getPackageReportByName(String packageName) {
        for (PackageReport packageToFind : packages) {
            if (packageToFind.getName().equals(packageName)) {
                return packageToFind;
            }
        }
        return null;
    }
    
    public int numClasses() {
        int numClasses = 0;
        
        for (PackageReport packageInProject : packages) {
            numClasses += packageInProject.getClassesInPackage().size();
        }
        
        return numClasses;
    }
    
    @Override
    public int numPassedTests() {
        int passedTests = 0;
        
        ArrayList<PackageReport> packagesInProject = this.getPackages();
        for (PackageReport packageReport : packagesInProject) {
            passedTests += packageReport.numPassedTests();
        }
        
        return passedTests;
    }
    
    @Override
    public int numFailedTests() {
        int failedTests = 0;
        
        ArrayList<PackageReport> packagesInProject = this.getPackages();
        for (PackageReport packageReport : packagesInProject) {
            failedTests += packageReport.numFailedTests();
        }
        
        return failedTests;
    }
    
    // calculates how many statements per method are there on average
    @Override
    public double calculateMethodsPerClass() {
        // obtain number of classes in project
        double numClassesDouble = (double) getClasses().size();
        
        // convert value to double to ensure the calculation is accurate
        double methodsDouble = (double) methods;
        
        double classesPerMethod = methodsDouble / numClassesDouble;
        
        // return the results rounded by 2
        return round(classesPerMethod, 2);
    }
}
