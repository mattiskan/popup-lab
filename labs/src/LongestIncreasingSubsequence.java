import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LongestIncreasingSubsequence {
	private static boolean debug = true;
	private final List<Integer> a;
	private final int n;
	private int[] last;
	private int[] indexes;

	public LongestIncreasingSubsequence(int n, List<Integer> a) {
		this.n = n;
		this.a = a;
	}

	private void prepare() {
		last = new int[n + 1];
		indexes = new int[n + 1];
		Arrays.fill(last, Integer.MAX_VALUE);
		last[0] = Integer.MIN_VALUE;
	}

	private ArrayList<Integer> solve() {
		prepare();

		for (int i = 0; i < n; i++) {
			int l = findLargest(i);
			if (last[l] > a.get(i)) {
				last[l] = a.get(i);
				indexes[l] = i;
			}
		}
		
		for (int i = 0; i < last.length; i++) {
			print(last[i], indexes[i]);
		}
		print("---");

		
		ArrayList<Integer> ans = new ArrayList<>();
		for (int i = 1; i < n + 1; i++) {
			if (last[i] == Integer.MAX_VALUE)
				break;
			ans.add(indexes[i]);
		}

		return ans;
	}

	private int findLargest(int i) {
		int l = Arrays.binarySearch(last, a.get(i));

		if (l < 0) {
			return (l + 1) * -1;
		}

		for (int j = l; j >= 0; j--) {
			if (last[j] < a.get(i)) {
				return j + 1;
			}
		}

		throw new IllegalStateException("We did not find any largest.");
	}

	/**
	 * Solver for https://kth.kattis.com/problems/intervalcover
	 */
	public static void main(String[] args) {
		if (args.length >= 1)
			debug = Boolean.parseBoolean(args[0]);
		Kattio io = new Kattio(System.in);
		do {
			int n = io.getInt();
			List<Integer> a = new ArrayList<>();

			for (int i = 0; i < n; i++) {
				a.add(io.getInt());
			}

			LongestIncreasingSubsequence lis = new LongestIncreasingSubsequence(
					n, a);
			printResult(lis.solve(), io);
		} while (io.hasMoreTokens());
		io.flush();
	}
	
	private static void printResult(List<Integer> result, Kattio io) {
		io.println(result.size());
		if(result.size() == 0)
			return;
		
		io.print(result.get(0));
		for(int i=1; i<result.size(); i++) {
			io.print(" ");
			io.print(result.get(i));
		}
		io.println();
	}

	private static void print(Object... args) {
		if (!debug)
			return;
		for (Object s : args) {
			System.out.print(s);
			System.out.print(' ');
		}
		System.out.println();
	}
}
