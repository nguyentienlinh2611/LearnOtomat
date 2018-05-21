package otomat;

import java.util.ArrayList;

public class CKY {
	private ArrayList<String> N;
	private ArrayList<String> T;
	private ArrayList<ProductionRule> grammar;
	private String[][] table;
	private String startSymbol;

	private class ProductionRule {
		ArrayList<String> N;
		ArrayList<String> V;
		
		public ProductionRule(ArrayList<String> N, ArrayList<String> V) {
			this.N = N;
			this.V = V;
		}
		
		public String toString() {
			String str = "";
			for(String s : N) {
				str += s + " ";
			}
			str += "->";
			for(String s : V) {
				str += " " + s;
			}
			return str;
		}
	}
	
	public CKY(String input) {
		N = new ArrayList<String>();
		T = new ArrayList<String>();
		String arr[] = input.split("\\s+");
		for (int i = 0; i < arr.length; i++) {
			T.add(arr[i]);
		}
		grammar = new ArrayList<ProductionRule>();
		table = new String[arr.length][arr.length];
		for(int i=0;i<arr.length;i++) {
			for(int j=0;j<arr.length;j++) {
				table[i][j] = "-";
			}
		}
	}
	
	private void addTable(int row, int col, String N) {
		if(!table[row][col].contains(N)) {
			String cur = table[row][col];
			if(cur.equals("-"))
				cur = N;
			else 
				cur += ","+N;
			table[row][col] = cur;
		}
	}
	
	private void printTable() {
		for(int i=table.length-1;i>=0;i--) {
			for(int j=0;j<table.length;j++) {
				System.out.print(table[i][j] + "\t");
			}
			System.out.println();
		}
	}
	
	public void setStartSymbol(int value) {
		startSymbol = N.get(value);
		System.out.println("You have set start symbol is " + startSymbol);
	}
	
	public ArrayList<String> getN(){
		return N;
	}
	
	public ArrayList<String> getT(){
		return T;
	}

	public void addProductionRule(String rule) {
		ArrayList<String> pn = new ArrayList<String>();
		ArrayList<String> pv = new ArrayList<String>();
		ProductionRule p = new ProductionRule(pn, pv);
		String arr[] = rule.split("\\s+");
		int translateIndex = arr.length;
		for (int i = 0; i < arr.length; i++) {
			if (!arr[i].equals("->")) {
				if (isNonterminalSymbol(arr[i])) {
					if (!N.contains(arr[i])) {
						N.add(arr[i]);
					}
				}
			} else {
				translateIndex = i;
			}
			if(translateIndex < i) {
				pv.add(arr[i]);
			} else if(translateIndex > i){
				pn.add(arr[i]);
			}
		}
		grammar.add(p);
	}

	private boolean isNonterminalSymbol(String str) {
		if (Character.isUpperCase(str.charAt(0))) {
			for (int i = 1; i < str.length(); i++) {
				if (Character.isLowerCase(str.charAt(i)))
					return false;
			}
		} else {
			return false;
		}
		return true;
	}

	public boolean solve() {
		/*
		 * let the input be a string S consisting of n characters: a1 ... an.
		 * let the grammar contain r nonterminal symbols R1 ... Rr.
		 * This grammar contains the subset Rs which is the set of start symbols.
		 * let P[n,n,r] be an array of booleans. Initialize all elements of P to false.
		 * for each s = 1 to n
		 *   for each unit production Rr -> ai
		 *     set P[1,s,r] = true
		 * for each l = 2 to n -- Length of span
		 *   for each s = 1 to n-i+1 -- Start of span
		 *     for each p = 1 to i-1 -- Partition of span
		 *       for each production RA -> RB RC
		 *         if P[s,p,B] and P[s+p,l-p,C] then set P[s,l,A] = true
		 * if any of P[n,1,x] is true (x is iterated over the set s, where s are all the indices for Rs) then
		 *   S is member of language
		 * else
		 *   S is not member of language
		 */
		int n = T.size();
		for(int s=0;s<n;s++) {
			for (int i=0;i<grammar.size();i++) {
				ProductionRule productionRule = grammar.get(i);
				if(productionRule.V.size()==1 && productionRule.V.get(0).equals(T.get(s))) {
					addTable(0, s, productionRule.N.get(0));
				}
			}
		}
		for(int l=1;l<n;l++) {
			for(int s=0;s<n-l;s++) {
				for(int p=1;p<=l;p++) {
					for(int i=0;i<grammar.size();i++) {
						ProductionRule rule = grammar.get(i);
						if(rule.V.size()==2) {	
							String a = rule.N.get(0);
							String b = rule.V.get(0);
							String c = rule.V.get(1);
							if(isNonterminalSymbol(b)&&isNonterminalSymbol(c)) {
								if(table[p-1][s].contains(b)&&table[l-p][s+p].contains(c)) {
									addTable(l, s, a);
								}
							}
						}
					}
				}
			}
		}
		if(table[n-1][0].contains("S")) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		String input = "b b a b a a";
		CKY cky = new CKY(input);
		cky.addProductionRule("S -> A B");
		cky.addProductionRule("S -> B S1");
		cky.addProductionRule("S -> B VA");
		cky.addProductionRule("S1 -> A B");
		cky.addProductionRule("A -> B A");
		cky.addProductionRule("A -> a");
		cky.addProductionRule("B -> a a");
		cky.addProductionRule("B -> VA S1");
		cky.addProductionRule("B -> S1 VA");
		cky.addProductionRule("B -> S1 S1");
		cky.addProductionRule("B -> b");
		cky.addProductionRule("VA -> a");

		if(cky.solve())
			System.out.println("Xau "+input+" co the sinh ra duoc tu tap quy tac tren");
		else
			System.out.println("Xau "+input+" khong the sinh ra duoc tu tap quy tac tren");
		cky.printTable();
	}
}
