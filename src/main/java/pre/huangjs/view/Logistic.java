package pre.huangjs.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

public class Logistic {
	private INDArray attributionData;
	private INDArray labelData;
	private INDArray weights;
	private ArrayList<INDArray> allWeights; // record weights' update process

	public INDArray getAttributionData() {
		return attributionData;
	}

	public INDArray getLabelData() {
		return labelData;
	}

	public INDArray getWeights() {
		return weights;
	}

	public ArrayList<INDArray> getAllWeights() {
		return allWeights;
	}

//	public static void main(String[] args) {
//		Logistic logistic = new Logistic();
//		try {
//			logistic.loadData("resources/data/4vs3_6X6.txt");
//			// loadData("/4v7Test.txt");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		// System.out.println("attributionData:\n" + attributionData);
//		logistic.gradAscent(0.001, 500);
//		System.out.println("weights: " + logistic.getWeights());
//	}

	public Logistic() {
		allWeights = new ArrayList<>();
	}

	public void loadData(String filePath) throws IOException {
		File inputFile = new File(filePath);
		FileInputStream is = new FileInputStream(inputFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		ArrayList<String> dataList = new ArrayList<>();

		while ((line = br.readLine()) != null) {
			dataList.add(line);
		}
		br.close();

		// transform as matrix
		int row = dataList.size();
		int column = dataList.get(0).split("\t").length;
		// System.out.println("R" + row + "C" + column);
		attributionData = Nd4j.ones(row, column);
		labelData = Nd4j.zeros(row, 1);

		for (int i = 0; i < row; i++) {
			String[] sample = dataList.get(i).split("\t");
			for (int j = 0; j < column - 1; j++) {
				attributionData.put(i, j + 1, Double.parseDouble(sample[j]));
			}
			labelData.put(i, 0, Double.parseDouble(sample[column - 1]));
		}
	}

	// private static INDArray sigmoid(INDArray data) {
	// INDArray result = Nd4j.create(data.rows(), 1);
	// for(int i = 0; i < data.rows(); i ++) {
	// result.put(i, 1, 1 / (1 + Math.exp(data.getDouble(i))));
	// }
	// return result;
	// }
	public void gradAscent(double alpha, int maxCycle) {
		weights = Nd4j.ones(attributionData.columns(), 1);
		for (int i = 0; i < maxCycle; i++) {
			INDArray predictedVal = Transforms.sigmoid(attributionData.mmul(weights));
			INDArray grad = attributionData.transpose().mmul(labelData.sub(predictedVal));
			// System.out.println(weights.shape()[0] + " " + weights.shape()[1]);
			weights = weights.add(grad.mul(alpha));
			allWeights.add(weights);
		}
	}

	public static int predict(INDArray weights, int[] unknownClassData) {
		double[] data = new double[unknownClassData.length + 1];
		data[0] = 1;
		for (int i = 0; i < unknownClassData.length; i++) {
			data[i + 1] = unknownClassData[i];
		}
		INDArray unknownClassMat = Nd4j.create(data);
		INDArray prediction = Transforms.sigmoid(unknownClassMat.mmul(weights));
		if (prediction.getDouble(0) < 0.5) {
			return 0;
		} else if (prediction.getDouble(0) > 0.5) {
			return 1;
		} else {
			return -1;
		}
	}
}
