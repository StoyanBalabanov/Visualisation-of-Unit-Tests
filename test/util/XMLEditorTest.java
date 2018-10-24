package util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import report.ClassReport;
import report.PackageReport;
import report.ProjectReport;
import util.XMLEditor;

/**
 *
 * @author Stoyan Balabanov
 */
public class XMLEditorTest {
    
    private XMLEditor editor;
    String  filePath;
    private Document doc;

    @Before
    public void setUp() throws Exception {
        filePath = (System.getProperty("user.dir") + "/test/util").replace("\\", "/");
        editor = new XMLEditor(filePath);
    }
    
    @Test
    public void testUpdateSourceAndTestDirectories() throws SAXException, IOException, 
            ParserConfigurationException, XPathExpressionException, TransformerException {
        String sourceClassesDirectory = "/src/main/java";
        String testClassesDirectory = "/src/test/java";
        
        editor.updateSourceAndTestDirectories(sourceClassesDirectory, testClassesDirectory);
        
        File editedBuild = new File(filePath + "util/build.xml");
        File correctBuild = new File(filePath + "util/build_correct.xml");
        
        assertTrue(FileUtils.contentEquals(correctBuild, editedBuild));
        
        editor.updateSourceAndTestDirectories("wrong", "wrong");
    }
    
    @Test
    public void testGetFullReport() throws SAXException, IOException, 
            ParserConfigurationException, XPathExpressionException {
        ProjectReport projectReport = editor.getFullReport("Test Project");
        assertEquals("Test Project", projectReport.getName());
        assertEquals(2, projectReport.getPackages().size());
        assertEquals(2, projectReport.getClasses().size());
        assertEquals(13, projectReport.getComplexity());
        assertEquals(42, projectReport.getElements());
        assertEquals(33, projectReport.getElementsCovered());
        assertEquals(12, projectReport.getMethods());
        assertEquals(2, projectReport.getBranches());
        assertEquals(28, projectReport.getStatements());
        assertEquals(94, projectReport.getLinesOfCode());
        assertEquals(69, projectReport.getNonCommentLinesOfCode());
        
        
        PackageReport packageReport = projectReport.getPackageReportByName("math");
        assertEquals("math", packageReport.getName());
        assertEquals(1, packageReport.getClassesInPackage().size());
        assertEquals(7, packageReport.getComplexity());
        assertEquals(21, packageReport.getElements());
        assertEquals(18, packageReport.getElementsCovered());
        assertEquals(7, packageReport.getMethods());
        assertEquals(0, packageReport.getBranches());
        assertEquals(14, packageReport.getStatements());
        assertEquals(42, packageReport.getLinesOfCode());
        assertEquals(31, packageReport.getNonCommentLinesOfCode());
        
        ClassReport classReport = packageReport.getClassByName("MathOperations");
        assertEquals("MathOperations", classReport.getName());
        assertEquals(7, classReport.getComplexity());
        assertEquals(21, classReport.getElements());
        assertEquals(18, classReport.getElementsCovered());
        assertEquals(7, classReport.getMethods());
        assertEquals(0, classReport.getBranches());
        assertEquals(14, classReport.getStatements());
        assertEquals(42, classReport.getLinesOfCode());
        assertEquals(31, classReport.getNonCommentLinesOfCode());
        
        
        System.out.println(projectReport.getName());
    }
    
}
