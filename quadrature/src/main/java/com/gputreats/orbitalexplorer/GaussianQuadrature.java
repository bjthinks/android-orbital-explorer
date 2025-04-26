package com.gputreats.orbitalexplorer;

/**
 * Given a weight function and a number of points, compute the nodes and weights for
 * a Gaussian quadrature rule.
 */
class GaussianQuadrature {

    private final double[] node, weight;

    double getNode(int i) {
        return node[i];
    }

    double getWeight(int i) {
        return weight[i];
    }

    GaussianQuadrature(Function weightFunction, int points, double minLength) {

        /*
         * STEP 0: Compute an offset for calculating the moments of the weight function.
         * I hoped this heuristic would increase the numerical stability of the Cholesky
         * decomposition, and it appears to, at least somewhat.
         * Goal: horizontally translate the power of x so that half of the weight function's
         * integral is on each side of its zero.
         */
        int approximateSteps = 256;
        double approximateStepSize = minLength / (double) approximateSteps;
        double approximateIntegral = 0.5 * weightFunction.eval(0) * approximateStepSize;
        int step;
        for (step = 1; step < approximateSteps; ++step)
            approximateIntegral += weightFunction.eval(approximateStepSize * (double) step)
                    * approximateStepSize;
        approximateIntegral += 0.5 * weightFunction.eval(minLength) * approximateStepSize;

        double halfIntegral = 0.5 * weightFunction.eval(0) * approximateStepSize;
        for (step = 1; step < approximateSteps; ++step) {
            halfIntegral += weightFunction.eval(approximateStepSize * (double) step)
                    * approximateStepSize;
            if (halfIntegral * 2.0 > approximateIntegral)
                break;
        }
        if (step == approximateSteps) {
            System.out.println("Couldn't figure out the offset");
            System.exit(1);
        }
        double offset = approximateStepSize * (double) step;
        System.out.println("length = " + minLength + " offset = " + offset);

        /*
         * STEP I: Compute the first 2N + 1 moments of the weight function.
         */

        double[] moments = new double[2 * points + 1];
        for (int i = 0; i < 2 * points + 1; ++i) {
            Function integrand = new Product(new Power(Polynomial.variableToThe(1)
                    .subtract(offset), i), weightFunction);
            moments[i] = Romberg.integrate(integrand, minLength);
        }

        /*
         * Step II: The moments implicitly form a Hankel matrix H, with
         * H[i][j] = moments[i + j]. We need the Cholesky decomposition of H.
         * The following algorithm, due to Phillips (1971), computes D*R,
         * where H = R^T * D * R, R upper unitriangular, D diagonal.
         * TODO PROBLEM: The Cholesky decomposition of H has entries tending to 0,
         * TODO PROBLEM: which makes the following logic numerically unstable.
         */

        /* Try computing the Cholesky decomposition of H using a tried-and-true
         * method from Wikipedia. Doesn't help. :(
         */
        double[][] H = new double[points + 1][points + 1];
        for (int i = 0; i < points + 1; ++i)
            for (int j = 0; j < points + 1; ++j)
                H[i][j] = moments[i + j];
        // Just print how close the matrix is to being non-positive definite.
        // Return value is deliberately ignored for now.
        CholeskyDecomposition.decompose(H, points + 1);

        // Here's the algorithm due to Phillips.
        double[] a = new double[points];
        double[] b = new double[points];
        double[][] c = new double[points + 1][2 * points + 1];
        System.arraycopy(moments, 0, c[0], 0, 2 * points + 1);
        //for (int j = 0; j < 2 * N + 1; ++j)
            //C[0][j] = moments[j];
        a[0] = c[0][1] / c[0][0];
        b[0] = 0.0;
        for (int i = 1; i < points + 1; ++i) {
            for (int j = i; j < 2 * points - i + 1; ++j) {
                c[i][j] = c[i - 1][j + 1] - a[i - 1] * c[i - 1][j];
                if (i > 1)
                    c[i][j] -= b[i - 1] * c[i - 2][j];
            }
            if (i < points) {
                a[i] = c[i][i + 1] / c[i][i] - c[i - 1][i] / c[i - 1][i - 1];
                b[i] = c[i][i] / c[i - 1][i - 1];
            }
        }
        // Now, (D * R)[i][j] = C[i][j]. Note that C has extra columns from the above
        // calculation, which we no longer need.

        /*
         * Step III: The matrix D * R = C above implicitly encodes the Cholesky decomposition
         * H = U^T * U, where U = sqrt(D) * R is upper triangular, via the trivial formula
         * U[i][j] = C[i][j] / sqrt(C[i][i]). Following section 4 of Golub & Welsch (1969),
         * we use U to construct the symmetric tridiagonal matrix J which encodes the three
         * term recurrence relations for the orthonormal polynomials (with respect to W).
         */

        // Transform C into U. We only need to do this for the diagonal and superdiagonal.
        for (int i = 0; i < points + 1; ++i) {
            c[i][i] = Math.sqrt(c[i][i]);
            if (i < points)
                c[i][i + 1] /= c[i][i];
        }
        SymmetricTridiagonalMatrix J = new SymmetricTridiagonalMatrix(points);
        J.setDiagonal(0, c[0][1] / c[0][0]);
        // TODO PROBLEM: Numerical instability causes the ratios below to have large error
        for (int i = 1; i < points; ++i)
            J.setDiagonal(i, c[i][i + 1] / c[i][i] - c[i - 1][i] / c[i - 1][i - 1]);
        for (int i = 0; i < points - 1; ++i)
            J.setOffDiagonal(i, c[i + 1][i + 1] / c[i][i]);

        /*
         * Step IV: Find the eigenvalues and first components of the eigenvectors of J.
         * They are the nodes and weights, respectively, of the Gaussian quadrature
         * rule.
         */

        J.doQRReduce();
        node = new double[points];
        weight = new double[points];
        for (int i = 0; i < points; ++i) {
            node[i] = J.getDiagonal(i) + offset;
            weight[i] = MyMath.fastpow(J.getComponent(i), 2) * moments[0];
        }

        // The nodes and weights will be nearly sorted, and there aren't very many of them,
        // so we pass them through an insertion sort.
        for (int i = 1; i < points; ++i) {
            for (int j = i; j > 0 && node[j - 1] > node[j]; --j) {
                double tempNode = node[j];
                node[j] = node[j - 1];
                node[j - 1] = tempNode;

                double tempWeight = weight[j];
                weight[j] = weight[j - 1];
                weight[j - 1] = tempWeight;
            }
        }
    }
}
