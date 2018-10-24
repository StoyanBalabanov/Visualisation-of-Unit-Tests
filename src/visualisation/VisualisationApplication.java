package visualisation;

/**
 *
 * @author Stoyan Balabanov
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VisualisationApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ImportProject.fxml"));
        Parent root = (Parent)loader.load();
        ImportProjectController controller = (ImportProjectController)loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setTitle("Visualisation of Unit Tests");
        primaryStage.setScene(new Scene(root, 1100, 800));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

