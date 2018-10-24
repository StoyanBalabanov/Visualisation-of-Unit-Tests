package visualisation;

/**
 *
 * @author Stoyan Balabanov
 */

import report.PackageReport;
import report.ClassReport;
import report.ProjectReport;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import util.*;

public class PackageViewController implements Initializable{
    private ProjectReport projectReport;
    private PackageReport selectedPackage;
    
    @FXML private Label packageNameLabel;
    @FXML private Label coveragePercentageLabel;
    @FXML private Label coverageElementsLabel;
    @FXML private Region coverageRegion;
    @FXML private Label testResultsLabel;
    @FXML private Region testResultsRegion;
    
    // configure metrics presented in code metrics vbox
    @FXML private Label numMethodsLabel;
    @FXML private Label numBranchesLabel;
    @FXML private Label numStatementsLabel;
    @FXML private Label numLinesOfCodeLabel;
    @FXML private Label numNonCommentLinesOfCodeLabel;
    @FXML private Label numClassesLabel;
    @FXML private Label complexityLabel;
    @FXML private Label complexityDensityLabel;
    @FXML private Label statementsPerMethodLabel;
    @FXML private Label methodsPerClassLabel;
    @FXML private Label averageMethodComplexityLabel;
      
    // configure classes table
    @FXML private TableView<ClassReport> classesTableView;
    @FXML private TableColumn<ClassReport, String> classNameColumn;
    @FXML private TableColumn<ClassReport, Integer> classComplexityColumn;
    @FXML private TableColumn<ClassReport, Double> classCoverageColumn;
    @FXML private TableColumn<ClassReport, Integer> classTestsPassedColumn;
    @FXML private TableColumn<ClassReport, Integer> classTestsFailedColumn;
    
    @FXML private Button classViewButton;
    
    /**
     * This method accepts a package to initialize the view
     * @param pReport
     * @param packageReport 
    */
    public void initData(ProjectReport pReport, PackageReport packageReport) {
        projectReport = pReport;
        selectedPackage = packageReport;
        packageNameLabel.setText("Package Name: " + selectedPackage.getName());
        //complexityLabel.setText(Integer.toString(projectReport.getComplexity()));
        
        // set up coverage percentage info
        double coveragePercentage = selectedPackage.calculateCoveragePercentage();
        coveragePercentageLabel.setText(coveragePercentage + "% percent covered");
        
        int elements = selectedPackage.getElements();
        int elementsCovered = selectedPackage.getElementsCovered();
        
        coverageElementsLabel.setText(
                elementsCovered + "/" + elements + " elements");
        // set up the bar to display test coverage percentage
        //divide by 100 to get a value from 0 to 1
        double coveragePercentageDisplay = coveragePercentage / 100.0;
        coverageRegion.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
        new Stop(coveragePercentageDisplay, Color.rgb(0, 177, 238)),
        new Stop(coveragePercentageDisplay, Color.rgb(158, 162, 162))
        ), CornerRadii.EMPTY, Insets.EMPTY)));
        
        // set up test results info
        int testsPassed = selectedPackage.numPassedTests();
        int testsFailed = selectedPackage.numFailedTests();
        int totalTests = testsPassed + testsFailed;
        testResultsLabel.setText(testsPassed + "/" + totalTests + " tests passed");
        // set up the bar to display test passed percentage
        //divide by 100 to get a value from 0 to 1
        //double testsPassed = projectReport.numPassedTests();
        //double testsFailed = projectReport.numFailedTests();
        double testsPassedPercentage = (double) testsPassed / (double) (testsPassed+testsFailed);
        System.out.println(testsPassedPercentage);
        testResultsRegion.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
        new Stop(testsPassedPercentage, Color.rgb(0, 125, 0)),
        new Stop(testsPassedPercentage, Color.rgb(189, 0, 0))
        ), CornerRadii.EMPTY, Insets.EMPTY)));
        
        numMethodsLabel.setText("Methods: " + selectedPackage.getMethods());
        Label numMethodsPopOverLabel = new Label("Total number of methods in the package.");
        numMethodsPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverMethods = new PopOver(numMethodsPopOverLabel);
        
        numMethodsLabel.setOnMouseEntered(mouseEvent -> {
            popOverMethods.show(numMethodsLabel);
        });
        
        numMethodsLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverMethods.hide();
        });
        
        numBranchesLabel.setText("Branches: " + selectedPackage.getBranches());
        Label numBranchesPopOverLabel = new Label("Total number of branches in the package.");
        numMethodsPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverBranches = new PopOver(numBranchesPopOverLabel);
        
        numBranchesLabel.setOnMouseEntered(mouseEvent -> {
            popOverBranches.show(numBranchesLabel);
        });
        
        numBranchesLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverBranches.hide();
        });
        
        numStatementsLabel.setText("Statements: " + selectedPackage.getStatements());
        Label numStatementsPopOverLabel = new Label("Total number of statements in the package.");
        numStatementsPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverStatements = new PopOver(numStatementsPopOverLabel);
        
        numStatementsLabel.setOnMouseEntered(mouseEvent -> {
            popOverStatements.show(numStatementsLabel);
        });
        
        numStatementsLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverStatements.hide();
        });
        
        numLinesOfCodeLabel.setText("LOC: " + selectedPackage.getLinesOfCode());
        Label numLinesOfCodePopOverLabel = new Label("Total number of lines of code in the package.");
        numLinesOfCodePopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverLinesOfCode = new PopOver(numLinesOfCodePopOverLabel);
        
        numLinesOfCodeLabel.setOnMouseEntered(mouseEvent -> {
            popOverLinesOfCode.show(numLinesOfCodeLabel);
        });
        
        numLinesOfCodeLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverLinesOfCode.hide();
        });
        
        numNonCommentLinesOfCodeLabel.setText("NCLOC: " + selectedPackage.getNonCommentLinesOfCode());
        Label numNonCommentLinesOfCodePopOverLabel = new Label("Total number of lines of code excluding comments in the package.");
        numNonCommentLinesOfCodePopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverNonCommentLinesOfCode = new PopOver(numNonCommentLinesOfCodePopOverLabel);
        
        numNonCommentLinesOfCodeLabel.setOnMouseEntered(mouseEvent -> {
            popOverNonCommentLinesOfCode.show(numNonCommentLinesOfCodeLabel);
        });
        
        numNonCommentLinesOfCodeLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverNonCommentLinesOfCode.hide();
        });
        
        numClassesLabel.setText("Classes: " + selectedPackage.getClassesInPackage().size());
        Label numClassesPopOverLabel = new Label("Total number of classes in the package.");
        numClassesPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverClasses = new PopOver(numClassesPopOverLabel);
        
        numClassesLabel.setOnMouseEntered(mouseEvent -> {
            popOverClasses.show(numClassesLabel);
        });
        
        numClassesLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverClasses.hide();
        });
        
        complexityLabel.setText("Complexity: " + selectedPackage.getComplexity());
        Label complexityPopOverLabel = new Label(
                "Cyclomatic complexity represents the number of paths in the package.");
        complexityPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverComplexity = new PopOver(complexityPopOverLabel);
        
        complexityLabel.setOnMouseEntered(mouseEvent -> {
            popOverComplexity.show(complexityLabel);
        });
        
        complexityLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverComplexity.hide();
        });
        
        complexityDensityLabel.setText("Complexity density: " + selectedPackage.calculateComplexityDensity());
        Label complexityDensityPopOverLabel = new Label(
                "Complexity density is the complexity divided by the number of statements in the package.");
        complexityDensityPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverComplexityDensity = new PopOver(complexityDensityPopOverLabel);
        
        complexityDensityLabel.setOnMouseEntered(mouseEvent -> {
            popOverComplexityDensity.show(complexityDensityLabel);
        });
        
        complexityDensityLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverComplexityDensity.hide();
        });
        
        statementsPerMethodLabel.setText("Statements/Method: " + selectedPackage.calculateStatementsPerMethod());
        Label statementsPerMethodPopOverLabel = new Label("Number of statements per method.");
        statementsPerMethodPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverStatementsPerMethod = new PopOver(statementsPerMethodPopOverLabel);
        
        statementsPerMethodLabel.setOnMouseEntered(mouseEvent -> {
            popOverStatementsPerMethod.show(statementsPerMethodLabel);
        });
        
        statementsPerMethodLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverStatementsPerMethod.hide();
        });
        
        methodsPerClassLabel.setText("Methods/Class: " + selectedPackage.calculateMethodsPerClass());
        Label methodsPerClassPopOverLabel = new Label("Number of methods per class.");
        methodsPerClassPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverMethodsPerClass = new PopOver(methodsPerClassPopOverLabel);
        
        methodsPerClassLabel.setOnMouseEntered(mouseEvent -> {
            popOverMethodsPerClass.show(methodsPerClassLabel);
        });
        
        methodsPerClassLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverMethodsPerClass.hide();
        });
        
        averageMethodComplexityLabel.setText("Average method complexity: " + selectedPackage.calculateAverageMethodComplexity());
        Label averageMethodComplexityPopOverLabel = new Label("Complexity divided by number of methods in the package.");
        averageMethodComplexityPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverAverageMethodComplexity = new PopOver(averageMethodComplexityPopOverLabel);
        
        averageMethodComplexityLabel.setOnMouseEntered(mouseEvent -> {
            popOverAverageMethodComplexity.show(averageMethodComplexityLabel);
        });
        
        averageMethodComplexityLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverAverageMethodComplexity.hide();
        });
        
        // set up the colums in the classes table
        classNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        classComplexityColumn.setCellValueFactory(new PropertyValueFactory<>("complexity"));
        classCoverageColumn.setCellValueFactory((TableColumn.CellDataFeatures<ClassReport, Double> p) 
                -> new ReadOnlyObjectWrapper(p.getValue().calculateCoveragePercentage()));
        classTestsPassedColumn.setCellValueFactory((TableColumn.CellDataFeatures<ClassReport, Integer> p) 
                -> new ReadOnlyObjectWrapper(p.getValue().numPassedTests()));
        classTestsFailedColumn.setCellValueFactory((TableColumn.CellDataFeatures<ClassReport, Integer> p) 
                -> new ReadOnlyObjectWrapper(p.getValue().numFailedTests()));
        
        
        // get classes
        ObservableList<ClassReport> classes = FXCollections.observableArrayList(selectedPackage.getClassesInPackage());
        classesTableView.setItems(classes);
        
        // disable the detailed class view button until a row is selected
        this.classViewButton.setDisable(true);
    }
    
    /**
     * When this method is called, it will pass the selected Class object to
     * a the detailed ClassView
    */
    public void changeSceneToDetailedClassView(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ClassView.fxml"));
        Parent tableViewParent = loader.load();
        
        Scene tableViewScene = new Scene(tableViewParent);
        
        //access the controller and call a method
        ClassViewController controller = loader.getController();
        controller.initData(projectReport, selectedPackage,
                classesTableView.getSelectionModel().getSelectedItem());
        
        //This line gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(tableViewScene);
        window.show();
    }
    
    /**
     * When this method is called, it will change the Scene to 
     * a TableView example
     * @param event
    */
    public void goBackToProjectViewButtonPushed(ActionEvent event) throws IOException
    {   
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ProjectView.fxml"));
        Parent packageViewParent = loader.load();
        
        Scene projectViewScene = new Scene(packageViewParent);
        
        //access the controller and call a method
        ProjectViewController controller = loader.getController();
        controller.initData(projectReport);
        
        //This line gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(projectViewScene);
        window.show();
    }
    
    /**
     * This method will enable the detailed view button once a row in the table is
     * selected
    */
    public void userClickedOnTable()
    {
        this.classViewButton.setDisable(false);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
}
