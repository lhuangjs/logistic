package pre.huangjs.view;

import java.util.ArrayList;

import org.nd4j.linalg.api.ndarray.INDArray;

import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class LogisticResult{
	
	public LogisticResult() {
		
	}
	
	public BorderPane getLogisticResultPane(ArrayList<INDArray> allWeights) {
		BorderPane borderPane = new BorderPane();
		borderPane.setTop(getWeightsPane(allWeights.get(allWeights.size() - 1)));
		borderPane.setCenter(getWeightsUpdateChart(allWeights));
		return borderPane;
	}
	
	public GridPane getWeightsPane(INDArray weights) {
		GridPane weightsPane = new GridPane();
		weightsPane.setHgap(10);
		weightsPane.setVgap(10);;
		weightsPane.setAlignment(Pos.CENTER);
		for(int i = 0; i < Math.ceil(weights.shape()[0] / 5.0 ); i++) {
			for(int j = 0; j < 5 && i * 5 + j < weights.shape()[0]; j++) {
				Label label = new Label("attribute" + (i * 5 + j + 1) + ": " + String.format("%.2f", weights.getDouble(i * 5 + j)));
				weightsPane.add(label, j, i);
			}
		}
		return weightsPane;
	}
	
    public LineChart<Number, Number>getWeightsUpdateChart(ArrayList<INDArray> allWeights) {
    	NumberAxis xAxis = new NumberAxis();
    	NumberAxis yAxis = new NumberAxis();
    	LineChart<Number, Number> weightsUpdateChart = new LineChart<>(xAxis, yAxis);
    	weightsUpdateChart.setCreateSymbols(false);
    	
    	// add data
    	for(int i = 0; i < allWeights.get(0).shape()[0]; i++) {
    		XYChart.Series<Number, Number> series = new XYChart.Series<>();
    		series.setName("contribute" + (i + 1));
    		for(int j = 0; j < allWeights.size(); j+= 10) {
    			// System.out.println("i = " + i + ", j = " + j);
    			series.getData().add(new XYChart.Data<Number, Number>(j, allWeights.get(j).getDouble(i)));
    		}
    		weightsUpdateChart.getData().add(series);
    	}
    	return weightsUpdateChart;
    }
}
