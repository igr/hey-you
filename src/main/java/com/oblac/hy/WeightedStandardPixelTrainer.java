package com.oblac.hy;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.FileReader;

import static com.oblac.hy.MatUtil.compareMatDif;
import static com.oblac.hy.MatUtil.compareMatDiv;
import static com.oblac.hy.MatUtil.toMedialMat;

public class WeightedStandardPixelTrainer {

	private Size imageSize;
	private WeightedStandardImages weightedStandardImages;

	public WeightedStandardPixelTrainer() {
		this(new Size(90, 90));
	}

	public WeightedStandardPixelTrainer(Size imageSize) {
		this.imageSize = imageSize;
		this.weightedStandardImages = new WeightedStandardImages();
	}

	public WeightedStandardImages getWeightedStandardImages() {
		return weightedStandardImages;
	}

	public void train(String[] imageFilePaths, Integer[] ids) {
		if (imageFilePaths.length != ids.length) {
			System.out.println("Inconsistent data");
			return;
		}

		int[] variety = appendVarietyOf(ids);
		int types = variety[variety.length - 1];
		int standardImageRow = (int) imageSize.width, standardImageCol = (int) imageSize.height;
		WeightedStandardImages weightedStandardImages = new WeightedStandardImages(types, imageSize);

		for (int i = 0; i < types; i++) {
			weightedStandardImages.setId(i, variety[i]);
		}

		int typeNo = 0, index = 0;
		for (String imageFilePath : imageFilePaths) {
			Mat mat = Imgcodecs.imread(imageFilePath, Imgcodecs.IMREAD_GRAYSCALE);
			Imgproc.resize(mat, mat, imageSize);
			mat = toMedialMat(mat);

			for (int i = 0; i < types; i++) {
				if (weightedStandardImages.getId(i) == ids[index]) {
					typeNo = i;
					break;
				}
			}

			for (int row = 0; row < standardImageRow; row++) {
				for (int col = 0; col < standardImageCol; col++) {
					double sumValue = (weightedStandardImages.getStandardImage(typeNo, row, col) *
						weightedStandardImages.getWeight(typeNo)) +
						mat.get(row, col)[0];

					int value = (int) sumValue / (weightedStandardImages.getWeight(typeNo) + 1);

					weightedStandardImages.setStandardImage(typeNo, row, col, value);
				}
			}

			weightedStandardImages.incrementWeight(typeNo);

			index++;
		}

		this.weightedStandardImages = weightedStandardImages;
	}

	private int[] appendVarietyOf(Integer[] integers) {
		int length = integers.length;
		int[] result = new int[length + 1];

		int variety = 0;

		for (int i = 0; i < length; i++) {
			result[i] = integers[i];
			boolean flagMatched = false;

			for (int j = 0; j < variety; j++) {
				if (result[j] == result[i]) {
					flagMatched = true;
					break;
				}
			}

			if (!flagMatched) {
				result[variety] = result[i];
				variety++;
			}
		}

		result[length] = variety;

		return result;
	}

	/**
	 * Loads previously prepared training data.
	 */
	public void loadTrainedData(String filePath) {
		String mainString = readFile(filePath);

		int startIndex, stopIndex;
		String key;

		// total image types
		key = "types:";
		startIndex = mainString.indexOf(key, 0);
		stopIndex = mainString.indexOf("\n", startIndex);
		int types = Integer.parseInt(mainString.substring(startIndex + key.length(), stopIndex));

		// image size
		key = "size:";
		startIndex = mainString.indexOf(key, stopIndex);
		stopIndex = mainString.indexOf(",", startIndex);
		int width = Integer.parseInt(mainString.substring(startIndex + key.length(), stopIndex));

		startIndex = stopIndex + 1;
		stopIndex = mainString.indexOf("\n", startIndex);
		int height = Integer.parseInt(mainString.substring(startIndex, stopIndex));

		imageSize = new Size(width, height);
		WeightedStandardImages weightedStandardImages = new WeightedStandardImages(types, imageSize);

		stopIndex = mainString.indexOf("data\n", stopIndex);
		for (int i = 0; i < types; i++) {

			// image id
			key = "id:";
			startIndex = mainString.indexOf(key, stopIndex);
			stopIndex = mainString.indexOf("\n", startIndex);
			int id = Integer.parseInt(mainString.substring(startIndex + key.length(), stopIndex));
			weightedStandardImages.setId(i, id);

			// image weight
			key = "weight:";
			startIndex = mainString.indexOf(key, stopIndex);
			stopIndex = mainString.indexOf("\n", startIndex);
			int weight = Integer.parseInt(mainString.substring(startIndex + key.length(), stopIndex));
			weightedStandardImages.setWeight(i, weight);

			key = "image\n";
			startIndex = mainString.indexOf(key, stopIndex);
			stopIndex = startIndex + key.length();

			for (int row = 0, col; row < width; row++) {
				for (col = 0; col < height; col++) {
					// standard image data

					startIndex = stopIndex + 1;
					stopIndex = mainString.indexOf(',', startIndex);

					int pixel = Integer.parseInt(mainString.substring(startIndex, stopIndex));
					weightedStandardImages.setStandardImage(i, row, col, pixel);
				}
				stopIndex++;
			}
		}

		this.weightedStandardImages = weightedStandardImages;
	}

	private String readFile(String filePath) {
		StringBuilder fileData = new StringBuilder();

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
			String tempString = bufferedReader.readLine();

			while (tempString != null) {
				fileData.append(tempString).append("\n");
				tempString = bufferedReader.readLine();
			}

			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileData.toString();
	}

	/**
	 * Returns predicted type on trained set, or -1 if not recognized.
	 */
	public int predict(Mat inputMat) {
		int id = -1;
		float similarity = 0;

		Imgproc.resize(inputMat, inputMat, imageSize);
		//inputMat = toMedialMat(inputMat);

		int types = weightedStandardImages.getTypes();

		for (int i = 0; i < types; i++) {
			float currentSimilarity =
				compareMatDiv(weightedStandardImages.getStandardImage(i), inputMat) +
				compareMatDif(weightedStandardImages.getStandardImage(i), inputMat);

			if (currentSimilarity > similarity) {
				similarity = currentSimilarity;
				id = weightedStandardImages.getId(i);
			}
		}

		if (similarity < 20) {
			return -1;
		}

		return id;
	}

}
