package com.gputreats.orbitalexplorer;

/**
 * Given a weight function W and a positive integer N, compute the nodes and weights for
 * an N-point Gaussian quadrature rule.
 */
class GaussianQuadrature {

    private final double[] node, weight;

    double getNode(int i) {
        return node[i];
    }

    double getWeight(int i) {
        return weight[i];
    }

    GaussianQuadrature(Function W, int N) {

        /*
         * STEP I: Compute the first 2N + 1 moments of the weight function.
         */

        double[] moments = new double[2 * N + 1];
        for (int i = 0; i < 2 * N + 1; ++i) {
            Function integrand = new Product(new Power(i), W);
            moments[i] = Romberg.integrate(integrand);
        }

        /*
         * Step II: The moments implicitly form a Hankel matrix H, with
         * H[i][j] = moments[i + j]. We need the Cholesky decomposition of H.
         * The following algorithm, due to Phillips (1971), computes D*R,
         * where H = R^T * D * R, R upper unitriangular, D diagonal.
         * TODO PROBLEM: The Cholesky decomposition of H has entries tending to 0,
         * TODO PROBLEM: which makes the following logic numerically unstable.
         */

        double[] a = new double[N];
        double[] b = new double[N];
        double[][] c = new double[N + 1][2 * N + 1];
        System.arraycopy(moments, 0, c[0], 0, 2 * N + 1);
        //for (int j = 0; j < 2 * N + 1; ++j)
            //C[0][j] = moments[j];
        a[0] = c[0][1] / c[0][0];
        b[0] = 0.0;
        for (int i = 1; i < N + 1; ++i) {
            for (int j = i; j < 2 * N - i + 1; ++j) {
                c[i][j] = c[i - 1][j + 1] - a[i - 1] * c[i - 1][j];
                if (i > 1)
                    c[i][j] -= b[i - 1] * c[i - 2][j];
            }
            if (i < N) {
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
        for (int i = 0; i < N + 1; ++i) {
            c[i][i] = Math.sqrt(c[i][i]);
            if (i < N)
                c[i][i + 1] /= c[i][i];
        }
        SymmetricTridiagonalMatrix J = new SymmetricTridiagonalMatrix(N);
        J.setDiagonal(0, c[0][1] / c[0][0]);
        // TODO PROBLEM: Numerical instability causes the ratios below to have large error
        for (int i = 1; i < N; ++i)
            J.setDiagonal(i, c[i][i + 1] / c[i][i] - c[i - 1][i] / c[i - 1][i - 1]);
        for (int i = 0; i < N - 1; ++i)
            J.setOffDiagonal(i, c[i + 1][i + 1] / c[i][i]);

        /*
         * Step IV: Find the eigenvalues and first components of the eigenvectors of J.
         * They are the nodes and weights, respectively, of the Gaussian quadrature
         * rule.
         */

        J.doQRReduce();
        node = new double[N];
        weight = new double[N];
        for (int i = 0; i < N; ++i) {
            node[i] = J.getDiagonal(i);
            weight[i] = MyMath.fastpow(J.getComponent(i), 2) * moments[0];
        }

        // The nodes and weights will be nearly sorted, and there aren't very many of them,
        // so we pass them through an insertion sort.
        for (int i = 1; i < N; ++i) {
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
