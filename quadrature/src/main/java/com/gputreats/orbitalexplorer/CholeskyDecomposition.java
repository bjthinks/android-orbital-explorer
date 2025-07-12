package com.gputreats.orbitalexplorer;

enum CholeskyDecomposition {
    ;

    static double[][] decompose(double[][] A, int N) {
        double[][] L = new double[N][N];
        double margin = 1.0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j <= i; j++) {
                double sum = 0;
                for (int k = 0; k < j; k++)
                    sum += L[i][k] * L[j][k];
                if (i == j) {
                    double marginOnThisStep = (A[i][i] - sum) / A[i][i];
                    if (marginOnThisStep < margin)
                        margin = marginOnThisStep;
                    L[i][j] = Math.sqrt(A[i][i] - sum);
                } else
                    L[i][j] = (A[i][j] - sum) / L[j][j];
            }
        }
        System.out.println("Cholesky margin = " + margin);
        if (margin <= 0.0)
            System.exit(1);
        return L;
    }
}
