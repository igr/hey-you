package com.oblac.hy.train;

import com.oblac.hy.WeightedStandardImages;
import com.oblac.hy.WeightedStandardPixelTrainer;

import java.io.File;
import java.util.ArrayList;

/**
 * Trainer for gender data.
 */
public class GenderTrainingData {

	public void makeTrainingFile() {
		File genderDataFolder = new File("data/gender");

		File[] genderDataSubfolders = genderDataFolder.listFiles(
			(current, name) -> new File(current, name).isDirectory());

		if (genderDataSubfolders == null) {
			System.out.println("No gender data");
			return;
		}

		ArrayList<String> filePathList = new ArrayList<>();
		ArrayList<Integer> idList = new ArrayList<>();

		int id = 0;
		for (File subfolder : genderDataSubfolders) {
			System.out.println("\t" + id + " = " + subfolder.getName());

			File[] files = subfolder.listFiles();
			if (files == null) {
				continue;
			}
			for (File file : files) {
				filePathList.add(file.getAbsolutePath());
				idList.add(id);
			}
			id++;
		}

		String[] filePaths = new String[filePathList.size()];
		filePathList.toArray(filePaths);

		Integer[] ids = new Integer[idList.size()];
		idList.toArray(ids);


		WeightedStandardPixelTrainer weightedStandardPixelTrainer = new WeightedStandardPixelTrainer();
		weightedStandardPixelTrainer.train(filePaths, ids);
		WeightedStandardImages weightedStandardImages = weightedStandardPixelTrainer.getWeightedStandardImages();

		weightedStandardImages.saveTrainedData("data/gender.txt");

		System.out.println("Gender data generated.");
	}

}

