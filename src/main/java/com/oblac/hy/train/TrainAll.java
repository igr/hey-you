package com.oblac.hy.train;

import org.opencv.core.Core;

public class TrainAll {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		System.out.println("---> EXTRACT FACES");

		new PrepareFaces().detectAllFacesFromRawPhotos();

		System.out.println("---> GENDER DATA");

		new GenderTrainingData().makeTrainingFile();
	}
}
