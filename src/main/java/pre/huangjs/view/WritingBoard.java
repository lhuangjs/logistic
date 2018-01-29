package pre.huangjs.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class WritingBoard {
	private double cellSize;
	private int cellCount;
	private double gap;
	private double strokeLineWidth;
	private int[][] pictureData; // a sample data
	private ArrayList<Integer[]> samplesData; // all samples data
	private ToggleGroup toggleGroup;

	public WritingBoard(double cellSize, int cellCount) {

		// parameter initial
		this.cellSize = cellSize;
		this.cellCount = cellCount;
		gap = 1;
		strokeLineWidth = 10;
		pictureData = new int[cellCount][cellCount];
		samplesData = new ArrayList<>();
		toggleGroup = new ToggleGroup();
	}
	
	/**
	 * @return a whole writing board with writing board pane and buttons
	 */
	public BorderPane getWritingBoard() {
		

		// show picture data
		TextArea pictureDataTextArea = getPictureDataTextArea();
		
		// wtiting board pane
		StackPane writingBoardPane = getWriteBoardPane(cellSize, cellCount, pictureDataTextArea);
		
		// set the layout on the left
		VBox labelRadioBox = getLabelRadioBox(); // Yes or No radio
		Label samplesCountLabel = getSamplesCountLabel(); // show the number of samples 
		Button saveSampleBtn = getSaveSampleBtn(toggleGroup, samplesCountLabel);
		GridPane leftGrid = new GridPane();
		leftGrid.add(pictureDataTextArea, 0, 0);
		leftGrid.add(labelRadioBox, 0, 1);
		leftGrid.add(saveSampleBtn, 0, 2);
		leftGrid.add(samplesCountLabel, 0, 3);
		leftGrid.setVgap(10);

		// set the layout on the bottom
		TextField savePathField = getSavePathField();
		Label tipLabel = getTipLabel();
		Button saveBtn = getSaveBtn(savePathField, tipLabel);
		GridPane centerGrid = new GridPane();
		centerGrid.add(writingBoardPane, 0, 0, 2, 1);
		centerGrid.add(savePathField, 0, 1, 1, 2);
		centerGrid.add(saveBtn, 1, 1);
		centerGrid.add(tipLabel, 1, 2);
		centerGrid.setVgap(10);
		centerGrid.setHgap(10);
//		centerGrid.setGridLinesVisible(true);
		centerGrid.setPadding(new Insets(10));

		// border pane
		BorderPane borderPane = new BorderPane();
		borderPane.setLeft(leftGrid);
		borderPane.setCenter(centerGrid);
		return borderPane;		
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

	private Label getSamplesCountLabel() {
		Label samplesCountLabel = new Label();
		samplesCountLabel.setText("Samples Count: " + samplesData.size());
		return samplesCountLabel;
	}

	private String saveSamplesData(String filePath) {
		File file = new File("resources/data/" + filePath);
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
	
	public StackPane getWriteBoardPane(double cellSize, int cellCount, TextArea pictureDataTextArea){
		GridPane gridPane = getGridPane(cellSize, cellCount);
		gridPane.setGridLinesVisible(true);
		
		// canvas
		Canvas canvas = getCanvas(cellSize, cellCount, pictureDataTextArea);
		
		// put grid pane and canvas into stack pane
		StackPane stackPane = new StackPane();
		stackPane.getChildren().addAll(gridPane, canvas);
		return stackPane;
	}

	private GridPane getGridPane(double cellSize, int cellCount) {
		GridPane gridPane = new GridPane();
		gridPane.setGridLinesVisible(true);
		gridPane.setHgap(gap);
		gridPane.setVgap(gap);
		gridPane.setPrefSize(cellSize * cellCount, cellSize * cellCount);
		gridPane.setMaxSize(cellSize * cellCount, cellSize * cellCount);
		gridPane.setMinSize(cellSize * cellCount, cellSize * cellCount);
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

	public Canvas getCanvas(double cellSize, int cellCount, TextArea pictureDataTextArea) {
		Canvas canvas = new Canvas(cellSize * cellCount, cellSize * cellCount);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		// writing board
		gc.setStroke(Color.GREEN);
		gc.setLineWidth(strokeLineWidth);
		canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				gc.strokeOval(event.getX(), event.getY(), strokeLineWidth, strokeLineWidth);
				// System.out.println(event.getX() + ", " + event.getY());
				int[] coordinate = getCoordinate(event.getX(), event.getY());
				pictureData[coordinate[0]][coordinate[1]] = 1;
				pictureDataTextArea.setText(getPictureDataStr());
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
		//System.out.println("R" + (coordinate[0] + 1) + "C" + (coordinate[1] + 1));
		return coordinate;
	}

	public TextArea getPictureDataTextArea() {
		TextArea pictureDataTextArea = new TextArea(getPictureDataStr());
		pictureDataTextArea.setPrefSize(200, 200);
		pictureDataTextArea.setMaxSize(200, 200);
		pictureDataTextArea.setMinSize(200, 200);
		return pictureDataTextArea;
	}

	private VBox getLabelRadioBox() {
		RadioButton yesBtn = new RadioButton("Yes");
		yesBtn.setToggleGroup(toggleGroup);
		yesBtn.setUserData(1);
		yesBtn.setSelected(true);
		RadioButton noBtn = new RadioButton("No");
		noBtn.setToggleGroup(toggleGroup);
		noBtn.setUserData(0);
		return new VBox(yesBtn, noBtn);

	}

	private TextField getSavePathField() {
		TextField savePathField = new TextField();
		savePathField.setPrefSize(400, 60);
		savePathField.setMaxSize(400, 60);
		savePathField.setMinSize(400, 60);
		return savePathField;
	}

	private Label getTipLabel() {
		Label tipLabel = new Label("");
		tipLabel.setPrefHeight(30);
		tipLabel.setMaxHeight(30);
		tipLabel.setMinHeight(30);
		return tipLabel;
	}

	private Button getSaveBtn(TextField savePathField, Label tipLabel) {
		Button saveBtn = new Button("Save Samples into File");
		saveBtn.setPrefHeight(30);
		saveBtn.setMaxHeight(30);
		saveBtn.setMinHeight(30);
		
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

	private Button getSaveSampleBtn(ToggleGroup toggleGroup, Label samplesCountLabel) {
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
			if (toggleGroup.getSelectedToggle().getUserData().toString().equals("1")) {
				label = 1;
			} else if (toggleGroup.getSelectedToggle().getUserData().toString().equals("0")) {
				label = 0;
			} else {
				System.exit(1);
			}
			sample[sample.length - 1] = label;
			samplesData.add(sample);
			samplesCountLabel.setText("Samples Count: " + samplesData.size());
		});
		return saveSampleBtn;
	}
	
	/**
	 * @return
	 */
	public int[] getSampleAttrData() {
		int[] sampleAttrData = new int[cellCount * cellCount];
		for (int i = 0; i < pictureData.length; i++) {
			for (int j = 0; j < pictureData[i].length; j++) {
				sampleAttrData[i * cellCount + j] = pictureData[i][j];
			}
		}
		return sampleAttrData;
	}
}
