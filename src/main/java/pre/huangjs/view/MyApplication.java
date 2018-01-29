package pre.huangjs.view;

import java.io.IOException;
import java.util.ArrayList;

import org.nd4j.linalg.api.ndarray.INDArray;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MyApplication extends Application {
	private Logistic logistic;
	private TabPane tabPane;
	private double cellSize;
	private int cellCount;

	public MyApplication() {
		cellSize = 100.0;
		cellCount = 6;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// menu bar
		MenuBar menuBar = getMenuBar();

		// tab pane
		tabPane = new TabPane();

		// root pane
		VBox rootPane = new VBox(menuBar, tabPane);

		/* writing board */
		WritingBoard writingBoard = new WritingBoard(cellSize, cellCount);
		Tab writingBoardTab = new Tab();
		writingBoardTab.setText("Data generator");
		writingBoardTab.setContent(writingBoard.getWritingBoard());
		tabPane.getTabs().add(writingBoardTab);

		Scene scene = new Scene(rootPane);
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				Platform.exit();
			}
		});
		primaryStage.show();

	}

	private MenuBar getMenuBar() {

		/* do logistic menu */
		MenuBar menuBar = new MenuBar();

		// create Tools menu
		Menu toolsMenu = new Menu("Tools");
		menuBar.getMenus().add(toolsMenu);

		// add "Run logistic" item to tools menu
		MenuItem runLogistic = new MenuItem("Run logistic");
		toolsMenu.getItems().add(runLogistic);
		runLogistic.setAccelerator(KeyCombination.keyCombination("Ctrl + R"));

		runLogistic.setOnAction(event -> {
			doLogistic();
		});

		return menuBar;
	}

	public void doLogistic() {
		logistic = new Logistic();
		Stage secondStage = new Stage();

		Label inputDataFileLabel = new Label("Data Set File: ");
		TextField idfTextField = new TextField();
		idfTextField.setPrefSize(300, 40);
		idfTextField.setMaxSize(300, 40);
		idfTextField.setMinSize(300, 40);

		Label alphaLabel = new Label("Alpha: ");
		TextField alphaTextField = new TextField();
		alphaTextField.setPrefSize(300, 40);
		alphaTextField.setMaxSize(300, 40);
		alphaTextField.setMinSize(300, 40);

		Label mcLabel = new Label("Max Cycle: ");
		TextField mcTextField = new TextField();
		mcTextField.setPrefSize(300, 40);
		mcTextField.setMaxSize(300, 40);
		mcTextField.setMinSize(300, 40);

		Button submitBtn = new Button("Submit");
		submitBtn.setPrefSize(400, 40);
		submitBtn.setMaxSize(400, 40);
		submitBtn.setMinSize(400, 40);
		submitBtn.setOnAction(event -> {
			String inputDataFile = "resources/data/" + idfTextField.getText();
			double alpha = Double.parseDouble(alphaTextField.getText());
			int maxCycle = Integer.parseInt(mcTextField.getText());

			// do logistic

			try {
				logistic.loadData(inputDataFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logistic.gradAscent(alpha, maxCycle);

			// show logistic result
			ArrayList<INDArray> allWeights = logistic.getAllWeights(); // data
			System.out.println(allWeights);
			LogisticResult logisticRs = new LogisticResult();
			Tab logisticRsTab = new Tab();
			logisticRsTab.setText("Result");
			logisticRsTab.setContent(logisticRs.getLogisticResultPane(allWeights));
			tabPane.getTabs().add(logisticRsTab);

			/* predict */
			Predict predict = new Predict(cellSize, cellCount, logistic.getWeights());
			Tab predictTab = new Tab("Prediction");
			predictTab.setContent(predict.getPredictPane());
			tabPane.getTabs().add(predictTab);
			secondStage.close();
			tabPane.getSelectionModel().select(1);
		});

		// layout
		GridPane doLogisticPane = new GridPane();
		doLogisticPane.add(inputDataFileLabel, 0, 0);
		doLogisticPane.add(idfTextField, 1, 0);
		doLogisticPane.add(alphaLabel, 0, 1);
		doLogisticPane.add(alphaTextField, 1, 1);
		doLogisticPane.add(mcLabel, 0, 2);
		doLogisticPane.add(mcTextField, 1, 2);
		doLogisticPane.add(submitBtn, 0, 3, 2, 1);
		doLogisticPane.setVgap(10);

		Scene secondScene = new Scene(doLogisticPane);
		secondStage.setScene(secondScene);
		secondStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
