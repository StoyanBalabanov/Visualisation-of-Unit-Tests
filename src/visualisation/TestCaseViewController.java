package visualisation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import report.ClassReport;
import report.PackageReport;
import report.ProjectReport;
import report.TestCaseReport;

/**
 * FXML Controller class
 *
 * @author Stoyan Balabanov
 */
public class TestCaseViewController implements Initializable {
    private ProjectReport projectReport;
    private PackageReport parentPackage;
    private ClassReport parentClass;
    private TestCaseReport selectedTestCase;
    
    @FXML private Label testCaseNameLabel;
    @FXML private Label testCaseTimeLabel;
    @FXML private Label testCaseStatusLabel;
    
    @FXML private GridPane errorPane;
    @FXML private Label testCaseErrorTypeLabel;
    @FXML private Label testCaseErrorMessageLabel;
    @FXML private Label testCaseErrorTraceLabel;
    
    
    public void initData(ProjectReport projectReport, PackageReport parentPackage,
            ClassReport parentClass, TestCaseReport selectedTestCase) {
        this.projectReport = projectReport;
        this.parentPackage = parentPackage;
        this.parentClass = parentClass;
        this.selectedTestCase = selectedTestCase;
        
        testCaseNameLabel.setText(selectedTestCase.getTestName());
        testCaseTimeLabel.setText(Double.toString(selectedTestCase.getTime()));
        
        if (selectedTestCase.getHasTestPassed()) {
            testCaseStatusLabel.setText("Pass");
            testCaseStatusLabel.setStyle("-fx-text-fill: green");
        } else {
            testCaseStatusLabel.setText("Fail");
            testCaseStatusLabel.setStyle("-fx-text-fill: red");
            
            testCaseErrorTypeLabel.setText(selectedTestCase.getErrorType());
            testCaseErrorTypeLabel.setWrapText(true);
            testCaseErrorMessageLabel.setText(selectedTestCase.getErrorMessage());
            testCaseErrorMessageLabel.setWrapText(true);
            testCaseErrorTraceLabel.setText(selectedTestCase.getErrorTrace());
            testCaseErrorTraceLabel.setWrapText(true);
            
            errorPane.setVisible(true);
        }
    }
    
    /**
     * When this method is called, it will change the Scene to 
     * a TableView example
     * @param event
    */
    public void goBackToClassViewButtonPushed(ActionEvent event) throws IOException
    {   
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ClassView.fxml"));
        Parent packageViewParent = loader.load();
        
        Scene projectViewScene = new Scene(packageViewParent);
        
        //access the controller and call a method
        ClassViewController controller = loader.getController();
        controller.initData(projectReport, parentPackage, parentClass);
        
        //This line gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(projectViewScene);
        window.show();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
