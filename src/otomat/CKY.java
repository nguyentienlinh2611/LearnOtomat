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
			for (String s : N) {
				str += s + " ";
			}
			str += "->";
			for (String s : V) {
				str += " " + s;
			}
			return str;
		}
	}

	public void convertToChomskyNorm() {
		removeStartStateOnRight();
		removeEpsilon();
		removeUnitProduction();
		removeMixed();
		removeLong();
	}

	private void removeLong() {
		ArrayList<ProductionRule> longRule = new ArrayList<ProductionRule>();
		for(ProductionRule productionRule : grammar) {
			if(productionRule.V.size()>2) {
				longRule.add(productionRule);
			}
		}
		int index = 0;
		for(ProductionRule productionRule : longRule) {
			String curNonterminal = productionRule.V.get(1);
			ArrayList<String> replace = new ArrayList<String>();
			replace.add(productionRule.V.get(1));
			for(int i=2;i<productionRule.V.size();i++) {
				replace.add(productionRule.V.get(i));
				String newNonterminal = String.valueOf((char)(index%26+65));
				while(N.contains(newNonterminal)) {
					index++;
					newNonterminal = String.valueOf((char)(index%26+65));
				}
				addProductionRule(newNonterminal+" -> "+curNonterminal+" "+productionRule.V.get(i));
				curNonterminal = newNonterminal;
			}
			replaceProductionRule(productionRule, replace, curNonterminal);
		}
	}

	private void removeMixed() {
		ArrayList<ProductionRule> mixedProductionRule = new ArrayList<ProductionRule>();
		ArrayList<String> terminal = new ArrayList<String>();
		for (ProductionRule productionRule : grammar) {
			for (String v : productionRule.V) {
				if (productionRule.V.size() > 1 && !isNonterminalSymbol(v)) {
					if (!mixedProductionRule.contains(productionRule))
						mixedProductionRule.add(productionRule);
					if (!terminal.contains(v))
						terminal.add(v);
				}
			}
		}
		for (String t : terminal) {
			String replace = "";
			ArrayList<String> ter = new ArrayList<String>();
			ter.add(t);
			boolean newProductionRuleAdded = false;
			for (ProductionRule mixed : mixedProductionRule) {
				if (mixed.V.contains(t)) {
					if(!newProductionRuleAdded) {
						replace = String.valueOf(Character.toUpperCase(t.charAt(0)));
						while (N.contains(replace)) {
							replace += "'";
						}
						addProductionRule(replace + " -> " + t);
						newProductionRuleAdded = true;
					}
					replaceProductionRule(mixed, ter, replace);
				}
			}
		}
	}

	private void replaceProductionRule(ProductionRule productionRule, ArrayList<String> t, String replace) {
		int start, end;
		do {
			start = 0;
			end = 0;
			if(t.get(0).equals(replace)) {
				break;
			}
			for (int i = 0; i < productionRule.V.size(); i++) {
				String v = productionRule.V.get(i);
				if (v.equals(t.get(0))) {
					start = i;
					end = i + t.size();
					if(t.size()>1) {
						for (int j = 1; j < t.size(); j++) {
							if (!productionRule.V.get(i + j).equals(t.get(j))) {
								start = 0;
								end = 0;
								break;
							}
						}
					}
				}
			}
			if(end-start>0) {
				for (int i = 0; i < end - start; i++) {
					productionRule.V.remove(start);
				}
				productionRule.V.add(start, replace);
			}
		}while(start!=0&&end!=0);
	}

	private void removeUnitProduction() {
		boolean done;
		do {
			done = true;
			ArrayList<ProductionRule> unitProductionRule = new ArrayList<ProductionRule>();
			for (ProductionRule productionRule : grammar) {
				if (productionRule.V.size() == 1 && isNonterminalSymbol(productionRule.V.get(0))) {
					unitProductionRule.add(productionRule);
					done = false;
				}
			}
			for (ProductionRule rule : unitProductionRule) {
				ProductionRule replaceRule;
				ArrayList<ProductionRule> productionRulesToAdd = new ArrayList<ProductionRule>();
				for (ProductionRule productionRule : grammar) {
					if (productionRule.N.contains(rule.V.get(0))) {
						replaceRule = new ProductionRule(rule.N, productionRule.V);
						productionRulesToAdd.add(replaceRule);
					}
				}
				grammar.addAll(productionRulesToAdd);
				grammar.remove(rule);
			}
		} while (!done);
		removeExistRule(grammar);
	}

	@SuppressWarnings("unchecked")
	private void removeEpsilon() {
		ArrayList<String> nullableState = new ArrayList<String>();
		ArrayList<String> nonnullableState = new ArrayList<String>();
		for (ProductionRule productionRule : grammar)
			for (String v : productionRule.V)
				if (v.equals("$"))
					nullableState.add(productionRule.N.get(0));

		boolean newNullableAdded;
		nonnullableState = (ArrayList<String>) N.clone();
		nonnullableState.removeAll(nullableState);

		do {
			newNullableAdded = false;
			for (ProductionRule productionRule : grammar)
				if (nonnullableState.contains(productionRule.N.get(0))) {
					ArrayList<String> nonterminal = new ArrayList<String>();
					for (String v : productionRule.V)
						if (isNonterminalSymbol(v))
							nonterminal.add(v);

					if (!nonterminal.isEmpty() && nullableState.containsAll(nonterminal)) {
						nonnullableState.remove(productionRule.N.get(0));
						nullableState.add(productionRule.N.get(0));
						newNullableAdded = true;
					}
				}
		} while (newNullableAdded);
		ArrayList<ProductionRule> productionRulesToAdd = new ArrayList<ProductionRule>();
		ArrayList<ProductionRule> productionRulesToRemove = new ArrayList<ProductionRule>();

		for (ProductionRule productionRule : grammar) {
			for (String nS : nullableState) {
				if (productionRule.V.contains(nS) && productionRule.V.size() != 1) {
					ProductionRule newProductionRule;
					ArrayList<String> newPRV = new ArrayList<String>();
					for (String v : productionRule.V)
						if (!v.equals(nS))
							newPRV.add(v);

					if (!newPRV.isEmpty()) {
						newProductionRule = new ProductionRule(productionRule.N, newPRV);
						productionRulesToAdd.add(newProductionRule);
						removeExistRule(productionRulesToAdd);
					}
				} else if (productionRule.V.contains("$")) {
					productionRulesToRemove.add(productionRule);
				}
			}
		}
		grammar.addAll(productionRulesToAdd);
		grammar.removeAll(productionRulesToRemove);
	}

	private void removeStartStateOnRight() {
		for (ProductionRule productionRule : grammar) {
			if (productionRule.V.contains(startSymbol)) {
				String newStartState = startSymbol;
				do
					newStartState += "'";
				while (N.contains(newStartState));
				addProductionRule(newStartState + " -> " + startSymbol);
				setStartSymbol(newStartState);
				break;
			}
		}
	}

	private void removeExistRule(ArrayList<ProductionRule> grammar) {
		ArrayList<ProductionRule> productionRulesToRemove = new ArrayList<ProductionRule>();
		productionRulesToRemove.add(null);
		for (int i = 0; i < grammar.size(); i++) {
			ProductionRule productionRule1 = grammar.get(i);
			for (int j = i + 1; j < grammar.size(); j++) {
				ProductionRule productionRule2 = grammar.get(j);
				if (productionRule1.N.get(0).equals(productionRule2.N.get(0))
						&& productionRule1.V.size() == productionRule2.V.size()) {
					boolean remove = true;
					for (int v = 0; v < productionRule1.V.size(); v++) {
						if (!productionRule1.V.get(v).equals(productionRule2.V.get(v)))
							remove = false;
					}
					if (remove)
						productionRulesToRemove.add(productionRule1);
				}
			}
			if (productionRule1.V.equals(productionRule1.N) && productionRule1.V.size() == 1) {
				productionRulesToRemove.add(productionRule1);
			}
		}
		grammar.removeAll(productionRulesToRemove);
	}

	public void printProductionRule() {
		for (String n : N) {
			boolean printed = false;
			for (ProductionRule productionRule : grammar) {
				if (productionRule.N.contains(n)) {
					if (!printed) {
						System.out.print(n + " -> ");
						printed = true;
					}
					for (String v : productionRule.V) {
						System.out.print(v + " ");
					}
					System.out.print("| ");
				}
			}
			System.out.println();
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
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr.length; j++) {
				table[i][j] = "-";
			}
		}
	}

	private void addTable(int row, int col, String N) {
		if (!table[row][col].contains(N)) {
			String cur = table[row][col];
			if (cur.equals("-"))
				cur = N;
			else
				cur += "," + N;
			table[row][col] = cur;
		}
	}

	private void printTable() {
		for (int i = table.length - 1; i >= 0; i--) {
			for (int j = 0; j < table.length; j++) {
				System.out.print(table[i][j] + "\t");
			}
			System.out.println();
		}
	}

	public void setStartSymbol(String state) {
		if (N.contains(state))
			startSymbol = state;
		else
			System.err.println("Trang thai dau vao la khong o trong tap trang thai");
	}

	public ArrayList<String> getN() {
		return N;
	}

	public ArrayList<String> getT() {
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
			if (translateIndex < i) {
				pv.add(arr[i]);
			} else if (translateIndex > i) {
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
		int n = T.size();
		for (int s = 0; s < n; s++) {
			for (int i = 0; i < grammar.size(); i++) {
				ProductionRule productionRule = grammar.get(i);
				if (productionRule.V.size() == 1 && productionRule.V.get(0).equals(T.get(s))) {
					addTable(0, s, productionRule.N.get(0));
				}
			}
		}
		for (int l = 1; l < n; l++) {
			for (int s = 0; s < n - l; s++) {
				for (int p = 1; p <= l; p++) {
					for (int i = 0; i < grammar.size(); i++) {
						ProductionRule rule = grammar.get(i);
						if (rule.V.size() == 2) {
							String a = rule.N.get(0);
							String b = rule.V.get(0);
							String c = rule.V.get(1);
							if (isNonterminalSymbol(b) && isNonterminalSymbol(c)) {
								if (table[p - 1][s].contains(b) && table[l - p][s + p].contains(c)) {
									addTable(l, s, a);
								}
							}
						}
					}
				}
			}
		}
		printTable();
		if (table[n - 1][0].contains(startSymbol)) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		String input = "b b a b a a";
		CKY cky = new CKY(input);
		cky.addProductionRule("S -> A S B");
		cky.addProductionRule("A -> a A S");
		cky.addProductionRule("A -> a");
		cky.addProductionRule("A -> $");
		cky.addProductionRule("B -> S b S");
		cky.addProductionRule("B -> A");
		cky.addProductionRule("B -> b b");

		cky.setStartSymbol("S");
		cky.printProductionRule();
		cky.convertToChomskyNorm();
		System.out.println();
		cky.printProductionRule();
	}
}
