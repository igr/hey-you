package com.oblac.hy;

/**
 * Information about recognized user.
 */
public class RecognizedUser {
	private final int label;
	private final double confidence;
	private final int genederType;

	public RecognizedUser(int label, double confidence, int genederType) {
		this.label = label;
		this.confidence = confidence;
		this.genederType = genederType;
	}

	public int getLabel() {
		return label;
	}

	public double getConfidence() {
		return confidence;
	}

	public int getGenederType() {
		return genederType;
	}
}
