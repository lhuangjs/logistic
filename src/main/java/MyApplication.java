


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MyApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

     
    	TextArea tf = new TextArea();
    	tf.setText("GGGGGGGGGGGGG\nTTTTTTTTT\nAAAAAAAAAAAA\nCCCC\n");
        Scene scene = new Scene(new VBox(tf), 50, 50);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
