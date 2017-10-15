package com.oblac.hy;

import org.opencv.core.Mat;

public class MatUtil {

	public static float compareMatDiv(Mat mat1, Mat mat2) {
		if (!mat1.size().equals(mat2.size())) {
			System.out.println("Inconsistent data");
			return -1;
		}

		int rows = mat1.rows();
		int cols = mat1.cols();

		float sumOfSimilarityByColInRow = 0;

		for (int row = 0; row < rows; row++) {
			float sumOfSimilarityByRow = 0;

			for (int col = 0; col < cols; col++) {
				float pixel1 = (float) mat1.get(row, col)[0];
				float pixel2 = (float) mat2.get(row, col)[0];

				float pixelSimilarity;

				if (pixel1 == pixel2) {
					pixelSimilarity = 100;
				} else if (pixel1 > pixel2) {
					pixelSimilarity = ((255 + pixel2) / (255 + pixel1)) * 100;
				} else {
					pixelSimilarity = (pixel1 / pixel2) * 100;
				}

				sumOfSimilarityByRow = sumOfSimilarityByRow + pixelSimilarity;
			}

			sumOfSimilarityByRow = sumOfSimilarityByRow / cols;
			sumOfSimilarityByColInRow = sumOfSimilarityByColInRow + sumOfSimilarityByRow;
		}

		float similarity = sumOfSimilarityByColInRow / rows;

		if (similarity > 50) {
			similarity = similarity - 50;
		} else {
			similarity = 50 - similarity;
		}

		return similarity;
	}

	public static float compareMatDif(Mat mat1, Mat mat2) {
		if (!mat1.size().equals(mat2.size())) {
			System.out.println("Inconsistent data");
			return -1;
		}

		int rows = mat1.rows();
		int cols = mat1.cols();

		float sumOfSimilarityByColInRow = 0;

		for (int row = 0; row < rows; row++) {
			float sumOfSimilarityByRow = 0;

			for (int col = 0; col < cols; col++) {
				float pixel1 = (float) mat1.get(row, col)[0];
				float pixel2 = (float) mat2.get(row, col)[0];

				float pixelSimilarity;
				if (pixel1 == pixel2) {
					pixelSimilarity = 100;
				} else if (pixel1 < pixel2) {
					pixelSimilarity = (255 - pixel2 + pixel1) / 255 * 100;
				} else {
					pixelSimilarity = (255 - pixel1 + pixel2) / 255 * 100;
				}

				sumOfSimilarityByRow = sumOfSimilarityByRow + pixelSimilarity;
			}

			sumOfSimilarityByRow = sumOfSimilarityByRow / cols;
			sumOfSimilarityByColInRow = sumOfSimilarityByColInRow + sumOfSimilarityByRow;
		}

		float similarity = sumOfSimilarityByColInRow / rows;

		if (similarity > 50) {
			similarity = similarity - 50;
		} else {
			similarity = 50 - similarity;
		}

		return similarity;
	}

	public static Mat toMedialMat(Mat mat) {
		Mat mat2 = new Mat(mat.size(), mat.type());

		int rows = mat.rows();
		int cols = mat.cols();

		double sumOfPixelByColInRow = 0;

		for (int x = 0; x < rows; x++) {
			double sumOfPixelByRow = 0;

			for (int y = 0; y < cols; y++) {
				sumOfPixelByRow = sumOfPixelByRow + mat.get(x, y)[0];
			}

			sumOfPixelByRow = sumOfPixelByRow / cols;
			sumOfPixelByColInRow = sumOfPixelByColInRow + sumOfPixelByRow;
		}

		double mediumPixel = sumOfPixelByColInRow / rows;

		int perfectMediumPixel = 255 / 2;

		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				double pixelValue = (int) mat.get(x, y)[0];
				double mediumValue = (int) (pixelValue * perfectMediumPixel / mediumPixel);

				if (mediumValue > 255) {
					mediumValue = 255;
				}

				mat2.put(x, y, mediumValue);
			}
		}

		return mat2;
	}

}
