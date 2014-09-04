package labs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO
 * 
 * @author Per Classon pclasson@kth.se
 * @author Mattis Kancans Envall mattiske@kth.se
 */
public class IntervalCover {
	private Interval goal;
	private List<Interval> list;
	private double nextLimit;
	private List<Interval> solution;
	private int nextIndex;

	public static void main(String[] args) {
		Kattio io = new Kattio(System.in);
		do {
			IntervalCover instance = getNextInstance(io);
			solveAndPrintResult(instance, io);
		} while (io.hasMoreTokens());
		io.flush();
	}
	
	private IntervalCover(Interval goal, List<Interval> list) {
		this.goal = goal;
		this.list = list;
		solution = new ArrayList<>(list.size());
	}

	public int[] solve() {
		Collections.sort(list);
		nextLimit = goal.start;
		nextIndex = 0;
		while (!isNotCovered()) {
			if (solveNext())
				break;
		}
		return getSolution();
	}

	private boolean solveNext() {
		Interval best = list.get(nextIndex);
		if (nextIndex+1 == list.size() && best.stop >= goal.stop) {
			print("lastbest");
			solution.add(best);
			return true;
		}
		
		for (int i= nextIndex+1; i<list.size(); i++) {
			Interval current = list.get(i);
			
			if (current.start > nextLimit) {
				print("found", best);
				solution.add(best);
				nextLimit = best.stop;
				nextIndex = i;
				return false;
			}
			
			if (current.stop > best.stop)
				best = current;
		}
		print("no solution");
		solution.clear();
		return true;
	}

	private boolean isNotCovered() {
		print("isCovered?", nextLimit, ">=", goal.stop, nextLimit >= goal.stop);
		return nextLimit >= goal.stop;
	}

	private int[] getSolution() {
		int[] solutionIndexes = new int[solution.size()];
		for (int i = 0; i < solutionIndexes.length; i++) {
			solutionIndexes[i] = solution.get(i).index;
		}
		return solutionIndexes;
	}

	private static IntervalCover getNextInstance(Kattio io) {
		Interval goal = new Interval(-1, io.getDouble(), io.getDouble());
		int n = io.getInt();

		List<Interval> list = new ArrayList<Interval>(n);
		for (int i = 0; i < n; i++) {
			list.add(new Interval(i, io.getDouble(), io.getDouble()));
		}
		return new IntervalCover(goal, list);
	}

	private static void solveAndPrintResult(IntervalCover instance, Kattio io) {
		int[] solution = instance.solve();

		if (solution.length == 0) {
			io.println("impossible");
			return;
		}

		io.println(solution.length);
		io.print(solution[0]);
		for (int i = 1; i < solution.length; i++) {
			io.print(' ');
			io.print(solution[i]);
		}
		io.println();
	}

	private static class Interval implements Comparable<Interval> {
		public final int index;
		public final double start, stop;

		public Interval(int index, double start, double stop) {
			this.start = start;
			this.stop = stop;
			this.index = index;
		}

		@Override
		public int compareTo(Interval o) {
			return (int) Math.signum(this.start - o.start);
		}
		@Override
		public String toString() {
			return String.format("[%f, %f]", start, stop);
		}
	}

	private static final boolean DEBUG = false;

	private static void print(Object... args) {
		if (!DEBUG)
			return;
		for (Object s : args){
			System.out.print(s);
			System.out.print(' ');
		}
		System.out.println();
	}
}
