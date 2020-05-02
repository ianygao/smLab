package simModel;

import java.util.HashMap;
import java.util.Map;

class Output {
	static SMLabTesting model;

	protected Output(SMLabTesting md) {
		model = md;
		numRushSample = 0;
		numRegularSample = 0;
		numRushSamplePass = 0;
		numRegularSamplePass = 0;
		numMoves = 0;
		countOccupyingOfBufferMap = new HashMap<Integer, Integer>();
		for (int cid : Constants.EXTENDED_CID_ARRAY) {
			countOccupyingOfBufferMap.put(cid, 0);
		}
	}
	// Use OutputSequence class to define Trajectory and Sample Sequences
	// Trajectory Sequences

	// Sample Sequences
	// DSOVs available in the OutputSequence objects
	// If seperate methods required to process Trajectory or Sample
	// Sequences - add them here

	// SSOVs
	private int totalSample;
	private int overtimedSample;
	private double turnaroundUnsatisfiedLevel;

	// DSOV
	protected int numRushSample;
	protected int numRegularSample;
	protected int numRushSamplePass;
	protected int numRegularSamplePass;
	protected double numMoves;
	protected Map<Integer, Integer> countOccupyingOfBufferMap;

	// For validation log display use
	public int getTotalSample() {
		totalSample = this.numRegularSample + this.numRushSample;
		return totalSample;
	}

	public int getOvertimedSample() {
		overtimedSample = (this.numRegularSample - this.numRegularSamplePass)
				+ (this.numRushSample - this.numRushSamplePass);
		return overtimedSample;
	}

	public double getTurnaroundUnsatisfiedLevel() {
		turnaroundUnsatisfiedLevel = (Double.parseDouble(getOvertimedSample() + "")
				/ Double.parseDouble(getTotalSample() + ""));
		return turnaroundUnsatisfiedLevel;
	}

	public int getNumPassedSample() {
		return (numRushSamplePass + numRegularSamplePass);
	}

	//
	protected void sampleTested(Sample sample) {
		double timeTested = model.getClock() - sample.time;
		int step = null == sample ? 0 : sample.step;
		if (sample.rush == false) {
			this.numRegularSample++;
			if (timeTested < 60.0 && sample.sequence[step] == Constants.LU)
				this.numRegularSamplePass++;
		} else {
			this.numRushSample++;
			if (timeTested < 30.0 && sample.sequence[step] == Constants.LU)
				this.numRushSamplePass++;
		}
		// System.out.printf(
		// "numRegularSample= %d,numRushSample=
		// %d,numRegularSamplePass=%d,numRushSamplePass= %d, timeTested = %f
		// \n",
		// numRegularSample, numRushSample, numRegularSamplePass,
		// numRushSamplePass, timeTested);
	}

	// TODO add cm
	protected void countOccupyingOfBuffer() {
		for (int cid : Constants.EXTENDED_CID_ARRAY) {
			InputBuffer iBuffer = model.qInputBuffer[cid];
			if (null == iBuffer || null == iBuffer.list)
				continue;
			int fullFlag = 0;
			if (Constants.LU == cid)
				fullFlag = Constants.LU_INPUT_BUFFER_LENGTH == iBuffer.list.size() ? 1 : 0;
			else
				fullFlag = Constants.BUFFER_SIZE == iBuffer.list.size() ? 1 : 0;

			int count = countOccupyingOfBufferMap.get(cid) + fullFlag;
			countOccupyingOfBufferMap.put(cid, count);
		}
	}

	protected double[] getOccupyingRateOfBuffer() {
		double[] occupyingRateOfBufferArr = new double[Constants.EXTENDED_CID_ARRAY.length];
		for (int cid : Constants.EXTENDED_CID_ARRAY) {
			occupyingRateOfBufferArr[cid] = (Double.parseDouble(countOccupyingOfBufferMap.get(cid) + "") / numMoves);
			// System.out.printf("@@@@@@@@@@@@@@@@@@@ cid=%d,
			// countOfOccupying=%d, numMoves=%f, rate=%f \n", cid,
			// countOccupyingOfBufferMap.get(cid), numMoves,
			// occupyingRateOfBufferArr[cid]);
		}
		return occupyingRateOfBufferArr;
	}

}
