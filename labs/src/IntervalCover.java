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

	
	private IntervalCover(Interval goal, List<Interval> list) {
		this.goal = goal;
		this.list = list;
		solution = new ArrayList<>(list.size());
	}

	public int[] solve() {
		print();
		print("goal", goal);
		
		if (goal.start == goal.stop)
			return onePointSolution();
		
		Collections.sort(list);
		
		nextLimit = goal.start;
		nextIndex = 0;
		
		for(Interval i : list)
			print(i);
		
		while (!isNotCovered()) {
			Interval next = findNext();
			if(next == null)
				return new int[0]; // impossible
			
			solution.add(next);
		}
		return getSolution();
	}
	
	private int[] onePointSolution () {
		for(Interval i : list){
			if(i.start <= goal.start 
					&& i.stop >= goal.start) // goal.start == goal.stop
				return new int[] { i.index };
		}
		return new int[0];
	}

	private Interval findNext() {
		print("passing", nextLimit);
		
		Interval best = null;
		for( ; nextIndex<list.size(); nextIndex++){
			Interval current = list.get(nextIndex);

			if (current.start > nextLimit)
				break;
			
			if(best == null || current.stop > best.stop){
				best = current;
			}
			
		}
		if (best != null)
			nextLimit = best.stop;
		
		print("best", best);
		return best;
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
	
	/**
	 * Solver for https://kth.kattis.com/problems/intervalcover
	 */
	public static void main(String[] args) {
		if(args.length >= 1)
			debug = Boolean.parseBoolean(args[0]);
		
		Kattio io = new Kattio(System.in);
		do {
			IntervalCover instance = getNextInstance(io);
			solveAndPrintResult(instance, io);
		} while (io.hasMoreTokens());
		io.flush();
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
	
	
	private static boolean debug = false;

	private static void print(Object... args) {
		if (!debug)
			return;
		for (Object s : args){
			System.out.print(s);
			System.out.print(' ');
		}
		System.out.println();
	}
}
