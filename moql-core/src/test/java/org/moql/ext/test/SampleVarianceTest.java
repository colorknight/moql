package org.moql.ext.test;

public class SampleVarianceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int length = 10;
		int sample = 5;
		int count = 0;
		double sum = 0;
		int[] ary = initializeArray(length);
		double average = variance(ary);
		System.out.println("variance="+average);
		int[] sampleIndex = initializeArray(sample);
		do {
			sum += sampleVariance(sampleIndex);
//			System.out.println("sum:"+sum);
			count++;
		} while(arrange(sampleIndex, length));
		
		System.out.println("count="+count);
		System.out.println("variance="+sum/count);
	}
	
	public static int[] initializeArray(int length) {
		int[] ary = new int[length];
		for(int i = 0; i < length; i++) {
			ary[i] = i+1;
		}
		return ary;
	}
	
	public static boolean arrange(int[] sampleIndex, int length) {
		int offset = 0;
		boolean changed = false;
		for(int i = sampleIndex.length-1; i >= 0; i--) {
			if (sampleIndex[i] < length - offset) {
				sampleIndex[i] = sampleIndex[i]+1;
				if (changed) {
					adjust(sampleIndex, i);
				}
				return true;
			}
			offset++;
			changed = true;
		}
		return false;
	}
	
	protected static void adjust(int[]sampleIndex, int currentIndex) {
		for(int i = currentIndex+1; i < sampleIndex.length; i++) {
			sampleIndex[i] = sampleIndex[i-1]+1;
		}
	}
	
	public static double average(int[] ary) {
		int ret = 0;
		for(int i = 0; i < ary.length; i++) {
			ret += ary[i];
		}
		return ret/ary.length;
	}
	
	public static double variance(int[] ary) {
		double ret = 0;
		double average = average(ary);
		for(int i = 0; i < ary.length; i++) {
			double diff = ary[i] - average;
			ret += diff*diff;
		}
		return ret/ary.length;

	}
	
	public static double sampleVariance(int[] ary) {
		double ret = 0;
		double average = average(ary);
		for(int i = 0; i < ary.length; i++) {
			double diff = ary[i] - average;
			ret += diff*diff;
		}
		return ret/(ary.length-1);
	}

}
