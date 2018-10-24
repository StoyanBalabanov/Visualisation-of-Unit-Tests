package visualisation;

import report.PackageReport;
import report.ClassReport;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import util.RunCMD;
import util.XMLEditor;
import report.ProjectReport;
import java.nio.file.Paths;
import util.*;

/**
 * FXML Controller class
 *
 * @author Stoyan Balabanov
 */
public class ImportProjectController implements Initializable {
    
    @FXML
    private Label label;
    final DirectoryChooser sourceFolderDirectoryChooser = new DirectoryChooser();
    private Stage myStage;
    private Parent root;
    
    @FXML Button sourceFolderButton;
    @FXML TextField sourceFolderPathTextField;
    
    @FXML Label chooseFoldersLabel;
    
    @FXML HBox sourceClassesHBox;
    @FXML TextField sourceClassesPathTextField;
    @FXML Button sourceClassButton;
    final DirectoryChooser sourceClassDirectoryChooser = new DirectoryChooser();
    
    @FXML HBox testClassesHBox;
    @FXML TextField testClassesPathTextField;
    @FXML Button testClassesButton;
    final DirectoryChooser testClassDirectoryChooser = new DirectoryChooser();
    
    @FXML Label continueLabel;
    @FXML Button continueButton;
    @FXML Label pleaseWaitLabel;
    

    final Label labelSelectedDirectory = new Label();
    
    public void setStage(Stage stage) {
         myStage = stage;
    }
    
    @FXML
    private void handleSourceFolderButtonAction(ActionEvent event) {
        
        File selectedDirectory = sourceFolderDirectoryChooser.showDialog(myStage);

        if(selectedDirectory == null){
            labelSelectedDirectory.setText("No Directory selected");
        } else{
            String sourceFolderPath = selectedDirectory.getAbsolutePath();
            sourceFolderPathTextField.setText(sourceFolderPath);
            
            String sourceClassesPath = sourceFolderPath + "\\src\\main\\java";
            String testCodePath = sourceFolderPath + "\\src\\test\\java";
            
            sourceClassesPathTextField.setText(sourceClassesPath);
            testClassesPathTextField.setText(testCodePath);
            
            sourceFolderPathTextField.setVisible(true);
            
            chooseFoldersLabel.setVisible(true);
            
            sourceClassesHBox.setVisible(true);
            testClassesHBox.setVisible(true);
            
            continueLabel.setVisible(true);
            continueButton.setVisible(true);
            pleaseWaitLabel.setVisible(true);
        }
    }
    
    @FXML
    /**
     * Lets the user edit the given source classes folder
     */
    private void changeSourceClassesFolder(ActionEvent event) {
        File selectedDirectory = sourceClassDirectoryChooser.showDialog(myStage);
        
        if(selectedDirectory == null){
            labelSelectedDirectory.setText("No Directory selected");
        } else{
            String sourceClassFolderPath = selectedDirectory.getAbsolutePath();
            sourceClassesPathTextField.setText(sourceClassFolderPath);
        }
        
    }
    
    @FXML
    /**
     * Lets the user edit the given test classes folder
     */
    private void changeTestClassesFolder(ActionEvent event) {
        File selectedDirectory = testClassDirectoryChooser.showDialog(myStage);
        
        if(selectedDirectory == null){
            labelSelectedDirectory.setText("No Directory selected");
        } else{
            String testClassFolderPath = selectedDirectory.getAbsolutePath();
            testClassesPathTextField.setText(testClassFolderPath);
        }
        
    }
    
    @FXML
    /**
     * Lets the user edit the given test classes folder
     */
    private void continueToVisualisation(ActionEvent event) throws SAXException, 
            IOException, ParserConfigurationException, XPathExpressionException, 
            InterruptedException, InterruptedException, TransformerException {
        // read the given directory
        String sourceFolderPath = sourceFolderPathTextField.getText();
        String projectName = new File(sourceFolderPath).getName();
        // set label to the name of the directory
        labelSelectedDirectory.setText(sourceFolderPath);

        // folder created to store all imported and produced files from the
        // test analysis
        String testAnalysisPath = sourceFolderPath + "/test_analysis";
        Files.createDirectories(Paths.get(testAnalysisPath));
        
        // copy build file to the project directory
        Files.copy(new File(
                System.getProperty("user.dir") + "/build files report/build.xml").toPath(),
                new File(sourceFolderPath + "/test_analysis/build.xml").toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                java.nio.file.StandardCopyOption.COPY_ATTRIBUTES,
                java.nio.file.LinkOption.NOFOLLOW_LINKS);
        
        // copy clove library file to the project directory
        Files.copy(new File(
                System.getProperty("user.dir") + "/build files report/clover.jar").toPath(),
                new File(sourceFolderPath + "/test_analysis/clover.jar").toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                java.nio.file.StandardCopyOption.COPY_ATTRIBUTES,
                java.nio.file.LinkOption.NOFOLLOW_LINKS);
        
        
        
        try {
            
            XMLEditor editBuildFile = new XMLEditor(testAnalysisPath);
            // go back one directory as the build file is in the test_analysis folder
            // and we need to go back to have access to the the src folder
            // the first part of the path is removed as it unnecessarily refers to
            // the folder the project is in
            String sourceClassesDirectory = "../" + sourceClassesPathTextField.getText()
                    .replace(sourceFolderPath + "\\", "").replace("\\", "/");
            String testClassesDirectory = "../" + testClassesPathTextField.getText()
                    .replace(sourceFolderPath + "\\", "").replace("\\", "/");

            editBuildFile.updateSourceAndTestDirectories(sourceClassesDirectory, testClassesDirectory);


            RunCMD cmdOperator = new RunCMD(new File(testAnalysisPath));
            cmdOperator.executeCommand("ant clean");
            cmdOperator.executeCommand("ant with.clover compile");
            cmdOperator.executeCommand("ant test");
            cmdOperator.executeCommand("ant clover.report");

            XMLEditor extractTestResults = new XMLEditor(testAnalysisPath);
            ProjectReport projectReport = extractTestResults.getFullReport(projectName);

            sceneHandler(event, projectReport);
            
        } catch (Exception e) {
            changeSceneToIncorrectProjectImport(event);
        }
        
    }
    
    
    @FXML
    /**
     * Passes the ProjectReport from the analysis onto the next view
     */
    private void sceneHandler(ActionEvent event, 
            ProjectReport projectReport) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ProjectView.fxml"));
        
        Parent projectViewParent = loader.load();
        Scene projectViewScene = new Scene(projectViewParent);
        
        // access the controller of the view that we will change to
        ProjectViewController controller = loader.getController();
        controller.initData(projectReport);
        
        System.out.println("Scene changing...");
        //This line gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(projectViewScene);
        window.show();
    }
    
     /**
     * When this method is called, it will means that the project evaluation
     * has been unsuccessful and the user is redirected to a view that tells 
     * him that
     */
    public void changeSceneToIncorrectProjectImport(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("IncorrectProjectImport.fxml"));
        Parent tableViewParent = loader.load();
        
        Scene tableViewScene = new Scene(tableViewParent);
        
        //This line gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(tableViewScene);
        window.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
