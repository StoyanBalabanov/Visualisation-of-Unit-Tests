package visualisation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import javafx.scene.control.TableCell;
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
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import report.ClassReport;
import report.PackageReport;
import report.ProjectReport;
import report.TestCaseReport;

/**
 * FXML Controller class
 *
 * @author UserPC
 */
public class ClassViewController implements Initializable {
    private ProjectReport projectReport;
    private PackageReport parentPackage;
    private ClassReport selectedClass;
    
    @FXML private Label classNameLabel;
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
      
    // configure test cases table
    @FXML private TableView<TestCaseReport> testCasesTableView;
    @FXML private TableColumn<TestCaseReport, String> testCaseNameColumn;
    @FXML private TableColumn<TestCaseReport, Integer> testCaseTimeColumn;
    @FXML private TableColumn<TestCaseReport, Boolean> testCasePassColumn;
    @FXML private TableColumn<TestCaseReport, String> testCaseErrorTraceColumn;
    
    @FXML private Button testCaseViewButton;
    
    
    /**
     * This method accepts a package to initialize the view
     * @param pReport
     * @param packageReport 
     * @param classReport 
    */
    public void initData(ProjectReport pReport, PackageReport packageReport,
            ClassReport classReport) {
        projectReport = pReport;
        parentPackage = packageReport;
        selectedClass = classReport;
        
        classNameLabel.setText("Class Name: " + selectedClass.getName());
        //complexityLabel.setText(Integer.toString(projectReport.getComplexity()));
        
        // set up coverage percentage info
        double coveragePercentage = selectedClass.calculateCoveragePercentage();
        coveragePercentageLabel.setText(coveragePercentage + "% percent covered");
        
        int elements = selectedClass.getElements();
        int elementsCovered = selectedClass.getElementsCovered();
        
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
        int testsPassed = selectedClass.numPassedTests();
        int testsFailed = selectedClass.numFailedTests();
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
        
        numMethodsLabel.setText("Methods: " + selectedClass.getMethods());
        Label numMethodsPopOverLabel = new Label("Total number of methods in the class.");
        numMethodsPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverMethods = new PopOver(numMethodsPopOverLabel);
        
        numMethodsLabel.setOnMouseEntered(mouseEvent -> {
            popOverMethods.show(numMethodsLabel);
        });
        
        numMethodsLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverMethods.hide();
        });
        
        numBranchesLabel.setText("Branches: " + selectedClass.getBranches());
        Label numBranchesPopOverLabel = new Label("Total number of branches in the class.");
        numMethodsPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverBranches = new PopOver(numBranchesPopOverLabel);
        
        numBranchesLabel.setOnMouseEntered(mouseEvent -> {
            popOverBranches.show(numBranchesLabel);
        });
        
        numBranchesLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverBranches.hide();
        });
        
        numStatementsLabel.setText("Statements: " + selectedClass.getStatements());
        Label numStatementsPopOverLabel = new Label("Total number of statements in the class.");
        numStatementsPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverStatements = new PopOver(numStatementsPopOverLabel);
        
        numStatementsLabel.setOnMouseEntered(mouseEvent -> {
            popOverStatements.show(numStatementsLabel);
        });
        
        numStatementsLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverStatements.hide();
        });
        
        numLinesOfCodeLabel.setText("LOC: " + selectedClass.getLinesOfCode());
        Label numLinesOfCodePopOverLabel = new Label("Total number of lines of code in the class.");
        numLinesOfCodePopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverLinesOfCode = new PopOver(numLinesOfCodePopOverLabel);
        
        numLinesOfCodeLabel.setOnMouseEntered(mouseEvent -> {
            popOverLinesOfCode.show(numLinesOfCodeLabel);
        });
        
        numLinesOfCodeLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverLinesOfCode.hide();
        });
        
        numNonCommentLinesOfCodeLabel.setText("NCLOC: " + selectedClass.getNonCommentLinesOfCode());
        Label numNonCommentLinesOfCodePopOverLabel = new Label("Total number of lines of code excluding comments in the class.");
        numNonCommentLinesOfCodePopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverNonCommentLinesOfCode = new PopOver(numNonCommentLinesOfCodePopOverLabel);
        
        numNonCommentLinesOfCodeLabel.setOnMouseEntered(mouseEvent -> {
            popOverNonCommentLinesOfCode.show(numNonCommentLinesOfCodeLabel);
        });
        
        numNonCommentLinesOfCodeLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverNonCommentLinesOfCode.hide();
        });
        
        complexityLabel.setText("Complexity: " + selectedClass.getComplexity());
        Label complexityPopOverLabel = new Label(
                "Cyclomatic complexity represents the number of paths in the class.");
        complexityPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverComplexity = new PopOver(complexityPopOverLabel);
        
        complexityLabel.setOnMouseEntered(mouseEvent -> {
            popOverComplexity.show(complexityLabel);
        });
        
        complexityLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverComplexity.hide();
        });
        
        complexityDensityLabel.setText("Complexity density: " + selectedClass.calculateComplexityDensity());
        Label complexityDensityPopOverLabel = new Label(
                "Complexity density is the complexity divided by the number of statements in the class.");
        complexityDensityPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverComplexityDensity = new PopOver(complexityDensityPopOverLabel);
        
        complexityDensityLabel.setOnMouseEntered(mouseEvent -> {
            popOverComplexityDensity.show(complexityDensityLabel);
        });
        
        complexityDensityLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverComplexityDensity.hide();
        });
        
        statementsPerMethodLabel.setText("Statements/Method: " + selectedClass.calculateStatementsPerMethod());
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
        
        averageMethodComplexityLabel.setText("Average method complexity: " + selectedClass.calculateAverageMethodComplexity());
        Label averageMethodComplexityPopOverLabel = new Label("Complexity divided by number of methods in the class.");
        averageMethodComplexityPopOverLabel.setPadding(new Insets(10, 10, 10, 10));
        PopOver popOverAverageMethodComplexity = new PopOver(averageMethodComplexityPopOverLabel);
        
        averageMethodComplexityLabel.setOnMouseEntered(mouseEvent -> {
            popOverAverageMethodComplexity.show(averageMethodComplexityLabel);
        });
        
        averageMethodComplexityLabel.setOnMouseExited(mouseEvent -> {
            //Hide PopOver when mouse exits label
            popOverAverageMethodComplexity.hide();
        });
        
        
        // set up the colums in the test cases table
        testCaseNameColumn.setCellValueFactory(new PropertyValueFactory<>("testName"));
        testCaseTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        testCasePassColumn.setCellValueFactory(new PropertyValueFactory<>("hasTestPassed"));
        testCaseErrorTraceColumn.setCellValueFactory((TableColumn.CellDataFeatures<TestCaseReport, String> p) 
                -> new ReadOnlyObjectWrapper(p.getValue().getErrorTrace()));
        
        
        testCasePassColumn.setCellFactory(column -> {
            return new TableCell<TestCaseReport, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item != null) {
                        if (item) {
                            setText("Pass");
                            setStyle("-fx-alignment:center; -fx-text-fill: green");
                        } else {
                            setText("Fail");
                            setStyle("-fx-alignment:center; -fx-text-fill: red");
                        }
                    }
                }
            };
        });
        
        // get test cases
        ObservableList<TestCaseReport> testCases = FXCollections.observableArrayList(selectedClass.getTestCases());
        testCasesTableView.setItems(testCases);
        
        // disable the detailed person view button until a row is selected
        this.testCaseViewButton.setDisable(true);
    }
    
    /**
     * When this method is called, it will pass the selected TestCase object to
     * a the detailed TestCaseView
    */
    public void changeSceneToDetailedTestCaseView(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("TestCaseView.fxml"));
        Parent tableViewParent = loader.load();
        
        Scene tableViewScene = new Scene(tableViewParent);
        
        //access the controller and call a method
        TestCaseViewController controller = loader.getController();
        controller.initData(projectReport, parentPackage, selectedClass,
                testCasesTableView.getSelectionModel().getSelectedItem());
        
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
    public void goBackToPackageViewButtonPushed(ActionEvent event) throws IOException
    {   
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("PackageView.fxml"));
        Parent packageViewParent = loader.load();
        
        Scene projectViewScene = new Scene(packageViewParent);
        
        //access the controller and call a method
        PackageViewController controller = loader.getController();
        controller.initData(projectReport, parentPackage);
        
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
        this.testCaseViewButton.setDisable(false);
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
