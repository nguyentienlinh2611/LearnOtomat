package test;

import java.util.ArrayList;

import otomat.Otomat;

public class Main {

	public static void main(String[] args) {
		Otomat myOtomat = new Otomat();
		myOtomat.addTransition('A', 'B', '0');
		myOtomat.addTransition('A', 'F', '1');
		myOtomat.addTransition('B', 'C', '1');
		myOtomat.addTransition('B', 'G', '0');
		myOtomat.addTransition('C', 'C', '1');
		myOtomat.addTransition('C', 'A', '0');
		myOtomat.addTransition('D', 'C', '0');
		myOtomat.addTransition('D', 'G', '1');
		myOtomat.addTransition('E', 'F', '1');
		myOtomat.addTransition('E', 'H', '0');
		myOtomat.addTransition('F', 'C', '0');
		myOtomat.addTransition('F', 'G', '1');
		myOtomat.addTransition('G', 'E', '1');
		myOtomat.addTransition('G', 'G', '0');
		myOtomat.addTransition('H', 'G', '0');
		myOtomat.addTransition('H', 'C', '1');
		myOtomat.printOtomat();
		myOtomat.setStart('A');
		ArrayList<Character> finish = new ArrayList<Character>();
		finish.add('C');
		myOtomat.setFinish(finish);
		myOtomat.MinimizeAutomata();
		ArrayList<Character> unreachableState = myOtomat.getUnreachableState();
		System.out.println("Unreachable State: ");
		for(Character state : unreachableState) {
			System.out.print(state + " ");
		}
		System.out.println();
		ArrayList<Character> deadState = myOtomat.getDeadState();
		System.out.println("Dead State: ");
		for(Character state : deadState) {
			System.out.print(state + " ");
		}
		System.out.println();
		myOtomat.printOtomat();
	}

}
