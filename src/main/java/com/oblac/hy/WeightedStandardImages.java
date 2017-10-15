package com.oblac.hy;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Set of weighted images.
 */
public class WeightedStandardImages {
	private int types;
	private Size size;
	private int[] ids;
	private int[] weights;
	private Mat[] standardImages;

	public WeightedStandardImages(int types, Size size) {
		this.types = types;
		this.size = size;
		this.ids = new int[types];
		this.weights = new int[types];
		this.standardImages = new Mat[types];
		for (int i = 0; i < types; i++) {
			standardImages[i] = new Mat(size, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		}
	}

	public WeightedStandardImages() {
		this(0, new Size(0, 0));
	}

	/**
	 * Saves {@link #generateTrainingData() training data} to a file.
	 */
	public void saveTrainedData(String filePath) {
		try {
			FileWriter fileWriter = new FileWriter(filePath);
			fileWriter.write(generateTrainingData());
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates training data.
	 */
	public String generateTrainingData() {
		int rows = (int) size.height, cols = (int) size.width;

		String content =
			"types:" + types + "\n" +
				"size:" + rows + "," + cols + "\n" +
				"data\n";

		for (int i = 0; i < types; i++) {
			StringBuilder imageType = new StringBuilder()
				.append("id:").append(ids[i]).append("\n")
				.append("weight:").append(weights[i]).append("\n")
				.append("image\n");

			for (int row = 0; row < rows; row++) {
				StringBuilder line = new StringBuilder();
				for (int col = 0; col < cols; col++) {
					line.append((int) standardImages[i].get(row, col)[0]).append(",");
				}
				imageType.append(line).append("\n");
			}

			content = content + imageType + "\n";
		}

		return content;
	}

	/**
	 * Returns the number of types.
	 */
	public int getTypes() {
		return types;
	}

	public Size getSize() {
		return size;
	}

	public int getId(int index) {
		return ids[index];
	}

	public void setId(int index, int id) {
		ids[index] = id;
	}

	public int getWeight(int index) {
		return weights[index];
	}

	public void setWeight(int index, int weight) {
		weights[index] = weight;
	}

	public void incrementWeight(int index) {
		weights[index]++;
	}

	public void setStandardImage(int index, int row, int col, int pixel) {
		standardImages[index].put(row, col, pixel);
	}

	public void setStandardImage(Mat newMat, int index) {
		standardImages[index] = newMat;
	}

	public int getStandardImage(int index, int row, int col) {
		if (index < types && row < size.width && col < size.height) {
			return (int) standardImages[index].get(row, col)[0];
		}
		return 0;
	}

	public Mat getStandardImage(int index) {
		return standardImages[index];
	}
}
