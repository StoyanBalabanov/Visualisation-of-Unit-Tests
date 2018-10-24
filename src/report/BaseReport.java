package report;

/**
 *
 * @author Stoyan Balabanov
 */
public abstract class BaseReport {
    String name;
    int complexity;
    int elements;
    int elementsCovered;
    int methods;
    int branches;
    int statements;
    int linesOfCode;
    int nonCommentLinesOfCode;
    
    public BaseReport(String name, int complexity, int elements, int elementsCovered, 
            int methods, int branches, int statements, 
            int linesOfCode, int nonCommentLinesOfCode) {
        this.name = name;
        this.complexity = complexity;
        this.elements = elements;
        this.elementsCovered = elementsCovered;
        this.methods = methods;
        this.branches = branches;
        this.statements = statements;
        this.linesOfCode = linesOfCode;
        this.nonCommentLinesOfCode = nonCommentLinesOfCode;
    }
    
    public String getName() {
        return name;
    }
    
    public int getComplexity() {
        return complexity;
    }
    
    public int getElements() {
        return elements;
    }
    
    public int getElementsCovered() {
        return elementsCovered;
    }
    
    public int getMethods() {
        return methods;
    }
    
    public int getBranches() {
        return branches;
    }
    
    public int getStatements() {
        return statements;
    }
    
    public int getLinesOfCode() {
        return linesOfCode;
    }
    
    public int getNonCommentLinesOfCode() {
        return nonCommentLinesOfCode;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }
    
    public void setElements(int elements) {
        this.elements = elements;
    }
    
    public void setElementsCovered(int elementsCovered) {
        this.elementsCovered = elementsCovered;
    }
    
    public void setMethods(int methods) {
        this.methods = methods;
    }
    
    public void setBranches(int branches) {
        this.branches = branches;
    }
    
    public void setStatements(int statements) {
        this.statements = statements;
    }
    
    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }
    
    public void setNonCommentLinesOfCode(int nonCommentLinesOfCode) {
        this.nonCommentLinesOfCode = nonCommentLinesOfCode;
    }
    
    public abstract int numPassedTests();
    
    public abstract int numFailedTests();
    
    // calculates the coverage percentage of the object as the amount of
    // elements covered compared to the total elements in the object
    public double calculateCoveragePercentage() {
        // convert values to doubles to ensure the calculation is accurate
        double elems = (double) elements;
        double elemsCovered = (double) elementsCovered;
        // get the coverage percentage by calculating what percentage
        // of the total elements are covered by the tests
        double coveragePercentage = elemsCovered / elems * 100;
        
        // round the final number by two decimal places
        return round(coveragePercentage, 2);
    }
    
    // calculates complexity density by dividing complexity by number of
    // statements
    public double calculateComplexityDensity() {
        // convert values to doubles to ensure the calculation is accurate
        double statementsDouble = (double) statements;
        double complexityDouble = (double) complexity;
        
        double complexityDensity = complexityDouble / statementsDouble;
        
        // return the results rounded by 2
        return round(complexityDensity, 2);
    }
    
    // calculates how many statements per method are there on average
    public double calculateStatementsPerMethod() {
        // convert values to doubles to ensure the calculation is accurate
        double statementsDouble = (double) statements;
        double methodsDouble = (double) methods;
        
        double statementsPerMethod = statementsDouble / methodsDouble;
        
        // return the results rounded to 2 decimal places
        return round(statementsPerMethod, 2);
    }
    
    // calculates complexity divided by number of methods
    public double calculateAverageMethodComplexity() {
        // convert values to doubles to ensure the calculation is accurate
        double complexityDouble = (double) complexity;
        double methodsDouble = (double) methods;
        
        double averageMethodComplexity = complexityDouble / methodsDouble;
        
        // return the results rounded to 2 decimal places
        return round(averageMethodComplexity, 2);
    }
    
    public abstract double calculateMethodsPerClass();
    
    // rounds double values to a specific number of decimal places
    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        
        return (double) tmp / factor;
    }
    
    @Override
    public String toString() {
        return "Name: " + name + "\n" 
                + "Complexity: " + complexity + "\n" 
                + "Coverage: " + calculateCoveragePercentage() + "\n";
    }
    
}
