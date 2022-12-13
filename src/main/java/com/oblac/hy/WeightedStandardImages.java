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
	private final int types;
	private final Size size;
	private final int[] ids;
	private final int[] weights;
	private final Mat[] standardImages;

	public WeightedStandardImages(final int types, final Size size) {
		this.types = types;
		this.size = size;
		this.ids = new int[types];
		this.weights = new int[types];
		this.standardImages = new Mat[types];
		for (int i = 0; i < types; i++) {
			standardImages[i] = new Mat(size, Imgcodecs.IMREAD_GRAYSCALE);
		}
	}

	public WeightedStandardImages() {
		this(0, new Size(0, 0));
	}

	/**
	 * Saves {@link #generateTrainingData() training data} to a file.
	 */
	public void saveTrainedData(final String filePath) {
		try {
			final FileWriter fileWriter = new FileWriter(filePath);
			fileWriter.write(generateTrainingData());
			fileWriter.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates training data.
	 */
	public String generateTrainingData() {
		final int rows = (int) size.height;
		final int cols = (int) size.width;

		final StringBuilder content =
				new StringBuilder("types:" + types + "\n" +
						"size:" + rows + "," + cols + "\n" +
						"data\n");

		for (int i = 0; i < types; i++) {
			final StringBuilder imageType = new StringBuilder()
				.append("id:").append(ids[i]).append("\n")
				.append("weight:").append(weights[i]).append("\n")
				.append("image\n");

			for (int row = 0; row < rows; row++) {
				final StringBuilder line = new StringBuilder();
				for (int col = 0; col < cols; col++) {
					line.append((int) standardImages[i].get(row, col)[0]).append(",");
				}
				imageType.append(line).append("\n");
			}

			content.append(imageType).append("\n");
		}

		return content.toString();
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

	public int getId(final int index) {
		return ids[index];
	}

	public void setId(final int index, final int id) {
		ids[index] = id;
	}

	public int getWeight(final int index) {
		return weights[index];
	}

	public void setWeight(final int index, final int weight) {
		weights[index] = weight;
	}

	public void incrementWeight(final int index) {
		weights[index]++;
	}

	public void setStandardImage(final int index, final int row, final int col, final int pixel) {
		standardImages[index].put(row, col, pixel);
	}

	public void setStandardImage(final Mat newMat, final int index) {
		standardImages[index] = newMat;
	}

	public int getStandardImage(final int index, final int row, final int col) {
		if (index < types && row < size.width && col < size.height) {
			return (int) standardImages[index].get(row, col)[0];
		}
		return 0;
	}

	public Mat getStandardImage(final int index) {
		return standardImages[index];
	}
}
