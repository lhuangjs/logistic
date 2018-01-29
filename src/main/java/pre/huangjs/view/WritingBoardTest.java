package pre.huangjs.view;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WritingBoardTest extends Application {
	private double cellSize;
	private int cellCount;
	private double gap;
	private double strokeLineWidth;
	private int[][] pictureData; // a sample data
	private ArrayList<Integer[]> samplesData; // all samples data
	private ToggleGroup toggleGroup;

	public WritingBoardTest() {

		// parameter initial
		cellSize = 100;
		cellCount = 6;
		gap = 1;
		strokeLineWidth = 10;
		pictureData = new int[cellCount][cellCount];
		samplesData = new ArrayList<>();
		toggleGroup = new ToggleGroup();
	}

	public void start(Stage primaryStage) throws Exception {
		GridPane gridPane = getGridPane(cellSize, cellCount);
		gridPane.setGridLinesVisible(true);

		// show picture data
		Label pictureDataLabel = getPictureDataLabel();

		// canvas
		Canvas canvas = getCanvas(cellSize, cellCount, pictureDataLabel);

		// stack pane
		StackPane stackPane = new StackPane();
		stackPane.getChildren().addAll(gridPane, canvas);

		// bottom pane
		VBox labelRadioBox = getLabelRadioBox();
		Button saveSampleBtn = getSaveSampleBtn(toggleGroup);
		TextField savePathField = getSavePathField();
		Label tipLabel = getTipLabel();
		Button saveBtn = getSaveBtn(savePathField, tipLabel);
		GridPane bottomGrid = new GridPane();
		bottomGrid.add(labelRadioBox, 0, 0);
		bottomGrid.add(saveSampleBtn, 1, 0, 2, 1);
		bottomGrid.add(savePathField, 0, 1);
		bottomGrid.add(saveBtn, 1, 1);
		bottomGrid.add(tipLabel, 2, 1);

		// border pane
		BorderPane borderPane = new BorderPane();
		borderPane.setLeft(new ScrollPane(pictureDataLabel));
		borderPane.setCenter(stackPane);
		borderPane.setBottom(bottomGrid);

		// scene
		Scene scene = new Scene(borderPane);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private String getPictureDataStr() {
		String result = "";
		for (int i = 0; i < pictureData.length; i++) {
			for (int j = 0; j < pictureData[i].length; j++) {
				result += pictureData[i][j] + "\t";
			}
			result += "\n";
		}
		return result;
	}

	private String saveSamplesData(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileWriter fw = null;
			BufferedWriter bw = null;
			try {
				fw = new FileWriter(file);
				bw = new BufferedWriter(fw);
				for (Integer[] sample : samplesData) {
					String sampleStr = "";
					for (int attr : sample) {
						sampleStr += attr + "\t";
					}
					sampleStr += "\n";
					bw.write(sampleStr);
					bw.flush();
				}
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "save data successfully!";
		} else {
			return "file has exist!";
		}
	}

	private void initPictureData() {
		for (int i = 0; i < pictureData.length; i++) {
			for (int j = 0; j < pictureData[i].length; j++) {
				pictureData[i][j] = 0;
			}
		}
	}

	private GridPane getGridPane(double cellSize, int cellCount) {
		GridPane gridPane = new GridPane();
		gridPane.setGridLinesVisible(true);
		gridPane.setHgap(gap);
		gridPane.setVgap(gap);
		gridPane.setPrefSize(cellSize * cellCount, cellSize * cellCount);
		gridPane.setMaxSize(cellSize * cellCount, cellSize * cellCount);
		gridPane.setMinSize(cellSize * cellCount, cellSize * cellCount);

		gridPane.setOnMousePressed(e -> {
			System.out.println("grid press");
		});

		for (int i = 0; i < cellCount; i++) {
			ColumnConstraints col = new ColumnConstraints();
			col.setPrefWidth(cellSize);
			col.setMaxWidth(cellSize);
			col.setMinWidth(cellSize);
			gridPane.getColumnConstraints().add(col);
			RowConstraints row = new RowConstraints();
			row.setPrefHeight(cellSize);
			row.setMaxHeight(cellSize);
			row.setMinHeight(cellSize);
			gridPane.getRowConstraints().add(row);
		}
		return gridPane;
	}

	private Canvas getCanvas(double cellSize, int cellCount, Label pictureDataLabel) {
		Canvas canvas = new Canvas(cellSize * cellCount, cellSize * cellCount);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		canvas.setOnMousePressed(e -> {
			System.out.println("press C ");
		});

		// writing board
		gc.setStroke(Color.GREEN);
		gc.setLineWidth(strokeLineWidth);
		canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				gc.strokeOval(event.getX(), event.getY(), strokeLineWidth, strokeLineWidth);
				System.out.println(event.getX() + ", " + event.getY());
				int[] coordinate = getCoordinate(event.getX(), event.getY());
				pictureData[coordinate[0]][coordinate[1]] = 1;
				pictureDataLabel.setText(getPictureDataStr());
			}
		});

		// double-click to reset the canvas
		canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
					gc.clearRect(0, 0, cellSize * cellCount, cellSize * cellCount);
					initPictureData();
				}
			}
		});
		return canvas;
	}

	private int[] getCoordinate(double x, double y) {
		int[] coordinate = new int[2]; // rowIndex, columnIndex
		coordinate[0] = (int) (y / cellSize);
		coordinate[1] = (int) (x / cellSize);
		System.out.println("R" + (coordinate[0] + 1) + "C" + (coordinate[1] + 1));
		return coordinate;
	}

	private Label getPictureDataLabel() {
		Label pictureDataLabel = new Label(getPictureDataStr());
		pictureDataLabel.setPrefSize(200, 200);
		return pictureDataLabel;
	}

	private VBox getLabelRadioBox() {
		RadioButton yesBtn = new RadioButton("Yes");
		yesBtn.setToggleGroup(toggleGroup);
		yesBtn.setUserData(1);;
		yesBtn.setSelected(true);
		RadioButton noBtn = new RadioButton("No");
		noBtn.setToggleGroup(toggleGroup);
		noBtn.setUserData(0);
		return new VBox(yesBtn, noBtn);

	}

	private TextField getSavePathField() {
		TextField savePathField = new TextField();
		savePathField.setPrefSize(50, 50);
		savePathField.setMaxSize(50, 50);
		savePathField.setMinSize(50, 50);
		return savePathField;
	}

	private Label getTipLabel() {
		Label tipLabel = new Label("");
		tipLabel.setPrefSize(100, 100);
		tipLabel.setMaxSize(100, 100);
		tipLabel.setMinSize(100, 100);
		return tipLabel;
	}

	private Button getSaveBtn(TextField savePathField, Label tipLabel) {
		Button saveBtn = new Button("Save Samples into File");
		saveBtn.setOnMouseClicked(e -> {
			if (savePathField.getText() != null && !savePathField.getText().equals("")) {
				String returnVal = saveSamplesData(savePathField.getText());
				tipLabel.setText(returnVal);
			} else {
				tipLabel.setText("file path is empty!");
			}
		});
		return saveBtn;
	}

	private Button getSaveSampleBtn(ToggleGroup toggleGroup) {
		Button saveSampleBtn = new Button("Save as Samples");
		saveSampleBtn.setOnMouseClicked(e -> {
			// System.out.println(getPictureDataStr());

			Integer[] sample = new Integer[cellCount * cellCount + 1]; // attribution + label
			// System.out.println("sample" + sample.length);
			for (int i = 0; i < pictureData.length; i++) {
				// System.out.println("pictureData.length: " + pictureData.length);
				// System.out.println("pictureData[i].length: " + pictureData[i].length);
				for (int j = 0; j < pictureData[i].length; j++) {
					// System.out.println("(" + i + ", " + j + ")");
					sample[i * cellCount + j] = pictureData[i][j];
					// System.out.println("pictureData[i][j]" + pictureData[i][j]);
				}
			}
			int label = -1;
			if(toggleGroup.getSelectedToggle().getUserData().toString().equals("1")) {
				label = 1;
			}else if(toggleGroup.getSelectedToggle().getUserData().toString().equals("0")){
				label = 0;
			}else {
				System.exit(1);
			}
			sample[sample.length - 1] = label;
			samplesData.add(sample); 
		});
		return saveSampleBtn;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
