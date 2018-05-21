package otomat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Otomat {
	private ArrayList<Character> State;
	private ArrayList<Character> Word;
	private Character Start;
	private ArrayList<Character> Finish;
	private HashMap<Character, Integer> map;
	private Graph<Character> table;

	public Otomat() {
		State = new ArrayList<Character>();
		Word = new ArrayList<Character>();
		Finish = new ArrayList<Character>();
		map = new HashMap<Character, Integer>();
		table = new Graph<Character>();
	}

	public Character getStart() {
		return Start;
	}

	public void setStart(Character start) {
		Start = start;
	}

	public ArrayList<Character> getFinish() {
		return Finish;
	}

	public void setFinish(ArrayList<Character> finish) {
		Finish = finish;
	}

	public ArrayList<Character> getState() {
		return State;
	}

	public ArrayList<Character> getWord() {
		return Word;
	}

	public ArrayList<Character> getDeadState() {
		ArrayList<Character> deadState = new ArrayList<Character>();
		for (Character state : State) {
			if (table.getVertex(map.get(state)).isEmpty() && !Finish.contains(state)) {
				deadState.add(state);
			}
		}
		return deadState;
	}

	public ArrayList<Character> getUnreachableState() {
		ArrayList<Character> unreachableState = new ArrayList<Character>();
		copyState(unreachableState, State);
		for (Character s1 : State) {
			for (Character s2 : State) {
				if (!s1.equals(s2)) {
					for (Character word : Word) {
						if (this.getDestState(s2, word)!=null)
							if (this.getDestState(s2, word).equals(s1) || Start.equals(s1)) {
								if (unreachableState.contains(s1)) {
									unreachableState.remove(s1);
								}
								break;
							}
					}
				}
			}
		}
		return unreachableState;
	}

	public void addTransition(Character src, Character dest, Character word) {
		if (!map.containsKey(src)) {
			map.put(src, State.size());
			State.add(src);
		}
		if (!map.containsKey(dest)) {
			map.put(dest, State.size());
			State.add(dest);
		}

		if (!Word.contains(word)) {
			Word.add(word);
		}
		table.addEdge(map.get(src), map.get(dest), word);
	}

	public Character getTransition(Character src, Character dest) {
		return table.getEdge(map.get(src), map.get(dest));
	}

	public Character getDestState(Character src, Character word) {
		int value = table.getVertexOfEdge(map.get(src), word);
		for (Map.Entry<Character, Integer> entry : map.entrySet()) {
			if (value == entry.getValue()) {
				return entry.getKey();
			}
		}
		return null;
	}

	public void printOtomat() {
		for (int i = 0; i < State.size(); i++) {
			System.out.println("Adjacency list of State: " + State.get(i));
			for (int j = 0; j < State.size(); j++) {
				Character word = table.getEdge(map.get(State.get(i)), map.get(State.get(j)));
				if (word != null) {
					System.out.print("State: " + State.get(j) + ", word: " + word + "\t");
				}
			}
			System.out.println();
		}
	}

	public boolean CheckAccept(String string) {
		char[] words = string.toCharArray();
		Character currentState = Start;
		for (char word : words) {
			Character w = new Character(word);
			for (int j = 0; j < State.size(); j++) {
				Character result = table.getEdge(map.get(currentState), map.get(State.get(j)));
				if (w.equals(result)) {
					currentState = State.get(j);
					break;
				}
			}
		}
		if (Finish.contains(currentState)) {
			return true;
		}
		return false;
	}

	public void MinimizeAutomata() {
		removeNonCoreState();
		ArrayList<ArrayList<Character>> partition = this.partitionFinishState();
		ArrayList<ArrayList<Character>> newPartition = new ArrayList<ArrayList<Character>>();
		newPartition = this.newPartition(partition);
		while (!partition.equals(newPartition)) {
			partition = newPartition;
			newPartition = this.newPartition(partition);
		}
		cleanPartition(partition);
		combineStateFromPartition(partition);
	}

	private void removeNonCoreState() {
		for (Character state : this.getDeadState()) {
			if (State.contains(state))
				State.remove(state);
		}
		for (Character state : this.getUnreachableState()) {
			if (State.contains(state))
				State.remove(state);
		}
	}

	private void combineStateFromPartition(ArrayList<ArrayList<Character>> partition) {
		for (ArrayList<Character> set : partition) {
			for (Character p : set) {
				System.out.print(p + " ");
			}
			System.out.println();
		}
		System.out.println();
		for (ArrayList<Character> set : partition) {
			if(set.size()>1) {
				System.out.print("Tap dinh ");
				for(Character state : set) {
					System.out.print(state+" ");
				}
				System.out.print("se tro thanh dinh "+set.get(0)+" trong otomat moi."+"\n");
				Character combineState = set.get(0);
				for(int i=1;i<set.size();i++) {
					table.combineVertex(map.get(combineState).intValue(),map.get(set.get(i)).intValue());
					State.remove(set.get(i));
				}
			}
		}
		
	}

	private ArrayList<ArrayList<Character>> partitionFinishState() {
		ArrayList<ArrayList<Character>> partition = new ArrayList<ArrayList<Character>>();
		ArrayList<Character> notfinish;
		ArrayList<Character> finish = this.getFinish();
		ArrayList<Character> list = new ArrayList<Character>();
		for (Character state : this.getState()) {
			if (!finish.contains(state)) {
				list.add(state);
			}
		}
		notfinish = list;
		partition.add(notfinish);
		partition.add(finish);
		return partition;
	}

	private ArrayList<ArrayList<Character>> newPartition(ArrayList<ArrayList<Character>> partition) {
		ArrayList<ArrayList<Character>> newPartition = new ArrayList<ArrayList<Character>>();
		copyPartition(newPartition, partition);
		for (ArrayList<Character> set : partition) {
			for (Character p : set) {
				for (Character q : set) {
					if (!q.equals(p)) {
						boolean separable = false;
						for (Character word : Word) {
							Character pdest = this.getDestState(p, word);
							Character qdest = this.getDestState(q, word);
							if(pdest!=null&&qdest!=null) {
								if (isDifferentPartition(pdest, qdest, partition)) {
									System.out.println("Tach " + p + " va " + q);
									System.out.println(
											"Do theo " + word + ", " + p + " ra " + pdest + " va " + q + " ra " + qdest);
									separable = true;
									break;
								} else {
									System.out.println("Hop nhat " + p + " va " + q);
									System.out.println(
											"Do theo " + word + ", " + p + " ra " + pdest + " va " + q + " ra " + qdest);
									separable = false;
								}
							}
						}
						if(separable) {
							separateState(p, q, newPartition, partition);
						} else {
							combineState(p, q, newPartition);
						}
					}
				}
			}
		}
		return newPartition;
	}

	private void copyPartition(ArrayList<ArrayList<Character>> newPartition,
			ArrayList<ArrayList<Character>> partition) {
		for (ArrayList<Character> set : partition) {
			ArrayList<Character> s = new ArrayList<Character>();
			for (Character state : set) {
				s.add(state);
			}
			newPartition.add(s);
		}
	}

	private void copyState(ArrayList<Character> newState, ArrayList<Character> State) {
		for (Character state : State) {
			newState.add(state);
		}
	}

	private void combineState(Character p, Character q, ArrayList<ArrayList<Character>> partition) {
		if (isDifferentPartition(p, q, partition)) {
			for (ArrayList<Character> set : partition) {
				if (!set.contains(p) && set.contains(q)) {
					set.remove(q);
				}
				if (set.contains(p) && !set.contains(q)) {
					set.add(q);
				}
			}
		}
	}

	private void separateState(Character p, Character q, ArrayList<ArrayList<Character>> newPartition, ArrayList<ArrayList<Character>> partition) {
		if (!isDifferentPartition(p, q, newPartition)) {
			ArrayList<Character> qlist = new ArrayList<Character>();
			qlist.add(q);
			for(ArrayList<Character> set : newPartition) {
				if(set.contains(p)&&set.contains(q)) {
					for(Character state : set) {
						if(!state.equals(p)&&!state.equals(q)) {
							for(Character word : Word) {
								Character statedest = this.getDestState(state, word);
								Character qdest = this.getDestState(q, word);
								if(statedest!=null&&qdest!=null) {
									if(isDifferentPartition(statedest, qdest, partition)) {
										qlist.remove(state);
										break;
									}else {
										if(!qlist.contains(state)) {
											qlist.add(state);
										}
									}
								}
							}
						}
					}
					for(Character remove : qlist) {
						set.remove(remove);
					}
					break;
				}
			}
			newPartition.add(qlist);
		}
	}

	private boolean isDifferentPartition(Character p, Character q, ArrayList<ArrayList<Character>> partition) {
		for (ArrayList<Character> set : partition) {
			for (Character state : set) {
				if (p.equals(state) && set.contains(q)) {
					return false;
				}
			}
		}
		return true;
	}

	private void cleanPartition(ArrayList<ArrayList<Character>> partition) {
		ArrayList<Integer> clean = new ArrayList<Integer>();
		int j = 0;
		for (int i = 0; i < partition.size(); i++) {
			ArrayList<Character> set = partition.get(i);
			if (set.isEmpty()) {
				clean.add(i - j);
				j++;
			}
		}
		for (Integer i : clean)
			partition.remove(i.intValue());
	}
}
