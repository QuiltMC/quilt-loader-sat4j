/*******************************************************************************
 * SAT4J: a SATisfiability library for Java Copyright (C) 2004, 2012 Artois University and CNRS
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
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
 * Contributors:
 *   CRIL - initial API and implementation
 *******************************************************************************/
package org.sat4j.minisat.core;

import org.junit.Test;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.opt.MaxSatDecorator;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;
import org.sat4j.tools.OptToSatAdapter;

public class BugFatih {

    @Test
    public void testBugReport() throws ContradictionException, TimeoutException {
        ISolver theSolver = SolverFactory.newDefault();
        ModelIterator solver = new ModelIterator(new OptToSatAdapter(
                new MaxSatDecorator(SolverFactory.newDefault())));
        System.out.println("Taille de voc : " + solver.nVars());
        solver.newVar(3);
        solver.setExpectedNumberOfClauses(3);
        solver.addClause(new VecInt(new int[] { 1, 2 }));
        solver.addClause(new VecInt(new int[] { 1, 3 }));
        solver.addClause(new VecInt(new int[] { 2, 3 }));
        System.out.println("Taille de voc : " + solver.nVars());
        if (solver.isSatisfiable()) {
            System.out.println("Taille du mod??le : " + solver.model().length);
            for (int i = 1; i <= solver.model().length; i++) {
                System.out.print(solver.model(i) + " ");
            }
            System.out.println();
        }

        solver.reset();
        System.out.println("Taille de voc : " + solver.nVars());
        solver.newVar(2);
        solver.setExpectedNumberOfClauses(2);
        solver.addClause(new VecInt(new int[] { 1, 2 }));
        solver.addClause(new VecInt(new int[] { 2 }));
        System.out.println("Taille de voc : " + solver.nVars());
        if (solver.isSatisfiable()) {
            System.out.println("Taille du mod??le : " + solver.model().length);
            for (int i = 1; i <= solver.model().length; i++) {
                System.out.print(solver.model(i) + " ");
            }
            System.out.println();
        }
        System.out.println("The End.");

    }
}
