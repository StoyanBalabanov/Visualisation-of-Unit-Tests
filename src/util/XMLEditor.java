package util;

import report.PackageReport;
import report.ClassReport;
import report.TestCaseReport;
import report.ProjectReport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Stoyan Balabanov
 */
public class XMLEditor {
    private String fileDirectory;
    
    public XMLEditor(String fileDirectory){
        this.fileDirectory = fileDirectory;
    }
    
    // Get access to the given xml file
    private Document initializeDoc(String filePath) throws SAXException, IOException, 
            ParserConfigurationException  {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(filePath);
        return doc; 
    }
    
    private void updateElementAttribute(Document doc, String elementID, 
            String attribute, String newValue) throws XPathExpressionException {
        // get element with given id
        Element elementToEdit = getElementById(doc, elementID);
        // update the element's attribute
        NamedNodeMap attributes = elementToEdit.getAttributes();
        Node nodeAttr = attributes.getNamedItem(attribute);
        nodeAttr.setTextContent(newValue);
    }
    
    
    private void finalizeChanges(Document doc) throws TransformerConfigurationException, TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        System.out.println("-----------Modified File-----------");
        StreamResult consoleResult = new StreamResult(new File(fileDirectory + "/build.xml"));
        transformer.transform(source, consoleResult);
    }
    
    private static Element getElementById(Document doc, String id) {
        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile("//*[@id = '" + id + "']");
            Element result = (Element) expr.evaluate(doc, XPathConstants.NODE);
            return result;
        } catch (XPathExpressionException ex) {
            return null;
        }
    }
    
    public void updateSourceAndTestDirectories(String srcDirectory, String testDirectory) throws 
            SAXException, IOException, ParserConfigurationException, 
            XPathExpressionException, TransformerException {
        // get build file
        String filePath = fileDirectory + "/build.xml";
        Document doc = initializeDoc(filePath);
        
        updateElementAttribute(doc, "1", "srcdir", srcDirectory);
        updateElementAttribute(doc, "2", "srcdir", testDirectory);
        
        finalizeChanges(doc);
    }
    
    private ArrayList<ClassReport> getClassesReport() 
            throws SAXException, IOException, 
            ParserConfigurationException, XPathExpressionException {
        ArrayList<ClassReport> projectReport = new ArrayList<>();
        // initialize variables
        XPath xPath =  XPathFactory.newInstance().newXPath();
        String filePath = fileDirectory + "/current_report.xml";
        Document doc = initializeDoc(filePath);
        
        // starting point for the search - we can expect all reports to have
        // this structure
        String expression = "/coverage/project/package";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(
            doc, XPathConstants.NODESET);
        
        //ProjectReport projectReport = 
        //ArrayList<PackageReport> packagesReport = getPackageReport(xPath, doc);
        
        // iterate over all classes listed in the report
        for(int i=0; i<=nodeList.getLength(); i++ ) {
            Node currentNode = nodeList.item(i);
            
            if (currentNode != null && currentNode.getNodeType() == Node.ELEMENT_NODE) {
                String packageName = currentNode.getAttributes().getNamedItem("name").getTextContent();
                Element eElement = (Element) currentNode;

                // find all source classes
                NodeList fileClasses = eElement.getElementsByTagName("file");

                for (int j=0; j<fileClasses.getLength(); j++) {
                    Node currentClassNode = fileClasses.item(j);
                        

                    NamedNodeMap classAttrs = currentClassNode.getAttributes();
                    
                    NamedNodeMap nodeAttrs = currentClassNode.getFirstChild()
                            .getNextSibling().getAttributes();

                    int complexity = Integer.parseInt(currentClassNode.getFirstChild()
                            .getNextSibling().getAttributes()
                            .getNamedItem("complexity").getNodeValue());
                    
                    String className = classAttrs.getNamedItem("name")
                            .getNodeValue().replace(".java", "");
                    
                    // check if the class is an interface
                    // if it is not obtain test metrics for the found classes
                    if (complexity > 0) {
                        // obtain total elements and the amount of them that
                        // is covered to calculate the coverage percentage
                        int elems = Integer.parseInt(nodeAttrs
                            .getNamedItem("elements").getNodeValue());

                        int elemsCovered = Integer.parseInt(nodeAttrs
                                .getNamedItem("coveredelements").getNodeValue());
                        
                        int methods = Integer.parseInt(nodeAttrs
                                .getNamedItem("methods").getNodeValue());
                        
                        int statements = Integer.parseInt(nodeAttrs
                                .getNamedItem("statements").getNodeValue());
                
                        int branches = Integer.parseInt(nodeAttrs
                                .getNamedItem("conditionals").getNodeValue());
                
                        int linesOfCode = Integer.parseInt(nodeAttrs
                                .getNamedItem("loc").getNodeValue());

                        int nonCommentLinesOfCode = Integer.parseInt(nodeAttrs
                                .getNamedItem("ncloc").getNodeValue());
                        
                        ClassReport classReport = 
                                new ClassReport(className, complexity, elems, 
                                    elemsCovered, methods, branches, statements,
                                    linesOfCode, nonCommentLinesOfCode, packageName);
                        
                        ArrayList<TestCaseReport> testCases = getTestCases(classReport);
                        
                        classReport.setTestCases(testCases);
                        
                        projectReport.add(classReport);
                    }
                }               
            }
        }
        return projectReport;
    }
    
    private ArrayList<PackageReport> getPackagesReport() throws 
            XPathExpressionException, SAXException, IOException, ParserConfigurationException {
        ArrayList<PackageReport> packages = new ArrayList<>();
        
        XPath xPath =  XPathFactory.newInstance().newXPath();
        String filePath = fileDirectory + "/current_report.xml";
        Document doc = initializeDoc(filePath);
        
        // starting point for the search - we can expect all reports to have
        // this structure
        String expression = "/coverage/project";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(
            doc, XPathConstants.NODESET);
        
        for(int i=0; i<=nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            
            if (currentNode != null && currentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) currentNode;
                
                // find all source classes
                NodeList packageNodes = eElement.getElementsByTagName("package");
                
                // obtain metrics for each package and add the package
                // to the packages array list
                for (int j=0; j<packageNodes.getLength(); j++) {
                    Node currentPackageNode = packageNodes.item(j);
                    NamedNodeMap packageAttrs = currentPackageNode.getAttributes();
                    NamedNodeMap metricsAtrs = currentPackageNode.getFirstChild()
                            .getNextSibling().getAttributes();
                    
                    String packageName = packageAttrs.getNamedItem("name").getNodeValue();
                    int complexity = Integer.parseInt(metricsAtrs
                            .getNamedItem("complexity").getNodeValue()); 
                    
                    int elems = Integer.parseInt(metricsAtrs
                            .getNamedItem("elements").getNodeValue());

                    int elemsCovered = Integer.parseInt(metricsAtrs
                            .getNamedItem("coveredelements").getNodeValue());
                    
                    int methods = Integer.parseInt(metricsAtrs
                        .getNamedItem("methods").getNodeValue());
                    
                    int statements = Integer.parseInt(metricsAtrs
                        .getNamedItem("statements").getNodeValue());
                
                    int branches = Integer.parseInt(metricsAtrs
                        .getNamedItem("conditionals").getNodeValue());
                
                    int linesOfCode = Integer.parseInt(metricsAtrs
                        .getNamedItem("loc").getNodeValue());
                
                    int nonCommentLinesOfCode = Integer.parseInt(metricsAtrs
                        .getNamedItem("ncloc").getNodeValue());

                    PackageReport currentPackage = new PackageReport(
                            packageName, complexity, elems, elemsCovered, methods,
                            branches , statements, linesOfCode, nonCommentLinesOfCode);
                    packages.add(currentPackage);
                }
            }
        }
        return packages;
    }
    
    private ProjectReport getProjectReport(String name) throws 
            XPathExpressionException, SAXException, IOException, ParserConfigurationException {
        ProjectReport project = null;
        
        XPath xPath =  XPathFactory.newInstance().newXPath();
        String filePath = fileDirectory + "/current_report.xml";
        Document doc = initializeDoc(filePath);
        
        // starting point for the search - we can expect all reports to have
        // this structure
        String expression = "/coverage";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(
            doc, XPathConstants.NODESET);
        
        for(int i=0; i<=nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            
            if (currentNode != null && currentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) currentNode;
                
                // find all source classes
                NodeList projectNodes = eElement.getElementsByTagName("project");
                
                Node projectNode = projectNodes.item(0);
                NamedNodeMap projectNodeAttrs = projectNode.getFirstChild()
                        .getNextSibling().getAttributes();

                int complexity = Integer.parseInt(projectNodeAttrs
                        .getNamedItem("complexity").getNodeValue()); 

                int elems = Integer.parseInt(projectNodeAttrs
                        .getNamedItem("elements").getNodeValue());

                int elemsCovered = Integer.parseInt(projectNodeAttrs
                        .getNamedItem("coveredelements").getNodeValue());
                
                int methods = Integer.parseInt(projectNodeAttrs
                        .getNamedItem("methods").getNodeValue());
                
                int statements = Integer.parseInt(projectNodeAttrs
                        .getNamedItem("statements").getNodeValue());
                
                int branches = Integer.parseInt(projectNodeAttrs
                        .getNamedItem("conditionals").getNodeValue());
                
                int linesOfCode = Integer.parseInt(projectNodeAttrs
                        .getNamedItem("loc").getNodeValue());
                
                int nonCommentLinesOfCode = Integer.parseInt(projectNodeAttrs
                        .getNamedItem("ncloc").getNodeValue());

                project = new ProjectReport(name, complexity, elems, elemsCovered,
                        methods, branches, statements, linesOfCode, nonCommentLinesOfCode);
            }
        }
        return project;
    }
    
    // this method returns all test cases for a given test class
    private ArrayList<TestCaseReport> getTestCases(ClassReport classReport) throws 
            SAXException, IOException, ParserConfigurationException, 
            XPathExpressionException {
        // ArrayList to store all the test cases
        ArrayList<TestCaseReport> testCases = new ArrayList<>();
        
        // this is the directory JUnit reports are generated in
        // from them test failure traces can be extracted
        File junitReportDirectory = new File(fileDirectory + "/build/testresults");
        
        String className = classReport.getName() + "Test";
        
        // a report is generated for each test class and we have to go 
        // through all of them to find the report for the given class
        File[] listOfFiles = junitReportDirectory.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && 
                listOfFiles[i].getName().toLowerCase().contains(className.toLowerCase())) {
                String testFilePath = listOfFiles[i].getAbsolutePath();
                // change backward slashes given by getAbsolutePath to
                // forward slashes becuase XML reads them in the latter format
                testFilePath = testFilePath.replace("\\", "/");
                
                System.out.println(testFilePath);
                // initialize variables
                XPath xPath =  XPathFactory.newInstance().newXPath();
                Document doc = initializeDoc(testFilePath);
                
                // get all testcase nodes in the xml file
                XPathExpression expr = xPath.compile("//testcase");
                Object exprResult = expr.evaluate(doc, XPathConstants.NODESET);
                NodeList nodeList = (NodeList) exprResult;
                
                for (int j=0; j < nodeList.getLength(); j++) {
                    Node currentNode = nodeList.item(j);
                    NamedNodeMap attrs = currentNode.getAttributes();
                    
                    String testCaseName = attrs.getNamedItem("name").getTextContent();
                    double time = Double.parseDouble(
                            attrs.getNamedItem("time").getTextContent());
                    // if the test case has child nodes it has failed and the
                    // child node is the failure trace                    
                    boolean hasPassed = !currentNode.hasChildNodes();
                    
                    TestCaseReport currentTestCase = new TestCaseReport(testCaseName,
                            time, hasPassed);
                    if (!hasPassed) {
                        Node failureNode = currentNode.getFirstChild()
                                .getNextSibling();
                        NamedNodeMap failureNodeAttrs = failureNode.getAttributes();
                        String errorType = failureNodeAttrs.
                                getNamedItem("type").getTextContent();
                        currentTestCase.setErrorType(errorType);
                        
                        String errorMessage = failureNodeAttrs.
                                getNamedItem("message").getTextContent();
                        currentTestCase.setErrorMessage(errorMessage);
                        
                        String errorTrace = "";
                        String errorContents = failureNode.getTextContent();
                        String[] lines = errorContents.split("\\r?\\n");
                        for (String line : lines) {
                            line = line.trim();
                            if (line.startsWith("at")) {
                                errorTrace += line + "\n";
                            }
                        }
                        // JUnit produces an incorrect version of the name of 
                        // the test case in the error trace, 
                        // so this is fixed below
                        String[] linesTrace = errorTrace.split("\\r?\\n");
                        String firstLine = linesTrace[0];
                        int indexLastDot = firstLine.lastIndexOf('.');
                        int nextToLastDot = firstLine.lastIndexOf('.', indexLastDot - 1) + 1;
                        int indexLastBracket = firstLine.lastIndexOf('(');
                        String incorrectSubstring = firstLine.substring(nextToLastDot, indexLastBracket);
                        String fixed = firstLine.replace(incorrectSubstring, testCaseName);
                        
                        currentTestCase.setErrorTrace(fixed);   
                    }                   

                    testCases.add(currentTestCase);
                }
            }
        }
        return testCases;
    }
    
    public ProjectReport getFullReport(String name) throws SAXException, 
            IOException, ParserConfigurationException, XPathExpressionException {
        
        // get metrics for each class
        ArrayList<ClassReport> classes = this.getClassesReport();
        
        // get metrics for each package and add the classes to them
        ArrayList<PackageReport> packages = this.getPackagesReport();
        for (PackageReport packageReport : packages) {
            packageReport.addClassesToPackage(classes);
        }
        
        // get metrics for full project and add the packages to the project        
        ProjectReport project = this.getProjectReport(name);
        project.addPackages(packages);
        
        return project;
    }
    
    public static void main(String[] args) throws SAXException, IOException, 
            ParserConfigurationException, TransformerException, XPathExpressionException {
        /*XMLEditor ddd = new XMLEditor("C:/Users/UserPC/Desktop/build.xml");
        
        Document doc = ddd.initializeDoc();
        
        ddd.updateElementAttribute(doc, "1", "srcdir", "5");
        
        ddd.finalizeChanges(doc);
        (*/
        
        XMLEditor ddd = new XMLEditor("C:/Users/UserPC/Desktop/Test Project");
        System.out.println("BBB");
        ddd.getFullReport("Test Project");
        /*ArrayList<ClassReport> classes = ddd.getClassesReport();
        ArrayList<PackageReport> packages = ddd.getPackagesReport();
        ProjectReport project = ddd.getProjectReport("tutorial");
        
        for (ClassReport classReport: classes) {
            System.out.println(classReport.toString());
        }
        
        for (PackageReport packageReport: packages) {
            System.out.println(packageReport.toString());
        }
        
        System.out.println(project.toString());
        */
        
        /*File folder = new File("C:/Users/UserPC/Desktop/tutorial" + "/build/testresults");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
              System.out.println("File " + listOfFiles[i].getName());
              System.out.println(listOfFiles[i].getName().toLowerCase().contains("MoneyTest".toLowerCase()));
            }else if (listOfFiles[i].isDirectory()) {
              System.out.println("Directory " + listOfFiles[i].getName());
            }
        }*/
        
        //ddd.addTestFailures(hmap, "MoneyBagTest");
     }
    
    
    
    
}
