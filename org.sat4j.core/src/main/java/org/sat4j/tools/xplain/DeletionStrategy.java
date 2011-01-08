/*******************************************************************************
 * SAT4J: a SATisfiability library for Java Copyright (C) 2004-2008 Daniel Le Berre
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU Lesser General Public License Version 2.1 or later (the
 * "LGPL"), in which case the provisions of the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL, and not to allow others to use your version of
 * this file under the terms of the EPL, indicate your decision by deleting
 * the provisions above and replace them with the notice and other provisions
 * required by the LGPL. If you do not delete the provisions above, a recipient
 * may use your version of this file under the terms of the EPL or the LGPL.
 * 
 * Based on the original MiniSat specification from:
 * 
 * An extensible SAT solver. Niklas Een and Niklas Sorensson. Proceedings of the
 * Sixth International Conference on Theory and Applications of Satisfiability
 * Testing, LNCS 2919, pp 502-518, 2003.
 *
 * See www.minisat.se for the original solver in C++.
 * 
 *******************************************************************************/
package org.sat4j.tools.xplain;

import java.util.Map;
import java.util.Set;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;
import org.sat4j.specs.TimeoutException;

/**
 * An implementation of the deletion based minimization.
 * 
 * 
 * @since 2.1
 */
public class DeletionStrategy implements MinimizationStrategy {

	private boolean computationCanceled;

	public void cancelExplanationComputation() {
		computationCanceled = true;
	}

	public IVecInt explain(ISolver solver, Map<Integer, ?> constrs,
			IVecInt assumps) throws TimeoutException {
		computationCanceled = false;
		IVecInt encodingAssumptions = new VecInt(constrs.size()
				+ assumps.size());
		assumps.copyTo(encodingAssumptions);
		IVecInt firstExplanation = solver.unsatExplanation();
		if (solver.isVerbose()) {
			System.out.println(solver.getLogPrefix() + "initial unsat core "
					+ firstExplanation);
		}
		for (int i = 0; i < firstExplanation.size();) {
			if (assumps.contains(firstExplanation.get(i))) {
				firstExplanation.delete(i);
			} else {
				i++;
			}
		}
		Set<Integer> constraintsVariables = constrs.keySet();
		IVecInt remainingVariables = new VecInt(constraintsVariables.size());
		for (Integer v : constraintsVariables) {
			remainingVariables.push(v);
		}
		int p;
		for (IteratorInt it = firstExplanation.iterator(); it.hasNext();) {
			p = it.next();
			if (p < 0) {
				p = -p;
			}
			remainingVariables.remove(p);
		}

		IVecInt results = new VecInt(firstExplanation.size());
		remainingVariables.copyTo(encodingAssumptions);
		int unsatcorebegin = encodingAssumptions.size();
		firstExplanation.copyTo(encodingAssumptions);
		assert !solver.isSatisfiable(encodingAssumptions);
		int unsatcorelimit = encodingAssumptions.size() - 1;
		for (int i = unsatcorebegin; i <= unsatcorelimit; i++) {
			encodingAssumptions.set(i, -encodingAssumptions.get(i));
			if (solver.isVerbose()) {
				System.out.println(solver.getLogPrefix() + "checking "
						+ (-encodingAssumptions.get(i)) + " ...");
			}
			if (solver.isSatisfiable(encodingAssumptions)) {
				encodingAssumptions.set(i, -encodingAssumptions.get(i));
				results.push(-encodingAssumptions.get(i));
				if (solver.isVerbose()) {
					System.out.println(solver.getLogPrefix() + "mandatory.");
				}
			} else {
				if (solver.isVerbose()) {
					System.out.println(solver.getLogPrefix() + "not needed.");
				}
			}
		}
		return results;
	}

	@Override
	public String toString() {
		return "Deletion based minimization strategy";
	}
}