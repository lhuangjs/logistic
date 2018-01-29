package pre.huangjs.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.nd4j.linalg.api.ndarray.INDArray;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class Predict {
	
	private double cellSize;
	private int cellCount;
	private INDArray weights;
	
	public Predict(double cellSize, int cellCount, INDArray weights) {
		this.cellSize = cellSize;
		this.cellCount = cellCount;
		this.weights = weights;
	}
	
	public BorderPane getPredictPane() {
		BorderPane predictPane = new BorderPane();
		
		// writing board
		WritingBoard writingBoard = new WritingBoard(cellSize, cellCount);
		TextArea pictureDataTextArea = writingBoard.getPictureDataTextArea();
		StackPane writingBoardPane = writingBoard.getWriteBoardPane(cellSize, cellCount, pictureDataTextArea);
		
		// prediction result box
		HBox predictResultBox = getPredictResultBox(writingBoard, weights);
		predictPane.setLeft(pictureDataTextArea);
		predictPane.setCenter(writingBoardPane);
		predictPane.setBottom(predictResultBox);
		return predictPane;
	}
	
	private HBox getPredictResultBox(WritingBoard writingBoard, INDArray weights) {
		HBox predictRsBox = new HBox();
		Button doPredict = new Button("Do Predict");
		Label rsLabel = new Label();
		predictRsBox.getChildren().addAll(doPredict, rsLabel);
		
		doPredict.setOnMouseClicked(event -> {
			int[] unknownClassData = writingBoard.getSampleAttrData();
			int rs = Logistic.predict(weights, unknownClassData);
			String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
			if(rs == 1) {
				rsLabel.setText("The Prediction is Yes " + "[" + timeStamp + "]");
			}else if(rs == 0){
				rsLabel.setText("The Prediction is No " + "[" + timeStamp + "]");
			}else {
				rsLabel.setText("The Prediction is can not recognize " + "[" + timeStamp + "]");
			}
		});
		return predictRsBox;
	}
}
