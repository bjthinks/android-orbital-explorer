package com.gputreats.orbitalexplorer;

class SymmetricTridiagonalMatrix {

    private final int size;
    private final double[] diagonal;
    private final double[] offDiagonal;
    private final double[] eigenvectorFirstComponent;

    SymmetricTridiagonalMatrix(int inSize) {
        size = inSize;
        diagonal = new double[size];
        offDiagonal = new double[size - 1];
        eigenvectorFirstComponent = new double[size];
        eigenvectorFirstComponent[0] = 1.0;
    }

    double getDiagonal(int i) {
        return diagonal[i];
    }

    double getComponent(int i) {
        return eigenvectorFirstComponent[i];
    }

    void setDiagonal(int i, double x) {
        diagonal[i] = x;
    }

    void setOffDiagonal(int i, double x) {
        offDiagonal[i] = x;
    }

    void doQRReduce() {
        int n = size;
        int fail = 0;
        while (n > 1) {
            doQRReductionStep(n);
            ++fail;
            while (n > 1 && Math.abs(offDiagonal[n - 2]) < 1.0e-15 * Math.abs(diagonal[n - 1])) {
                --n;
                fail = 0;
            }
            if (fail > 32)
                throw new RuntimeException("QR reduction failure");
        }
    }

    private void doQRReductionStep(int n) {
        // Choose a "deflation" parameter lambda equal to the larger eigenvalue
        // of the lower-right 2x2 minor.
        double lambda;
        {
            double a = diagonal[n - 2];
            double b = offDiagonal[n - 2];
            double c = diagonal[n - 1];
            double d = Math.sqrt((a - c) * (a - c) + 4.0 * b * b);
            double lambda1 = (a + c + d) / 2.0;
            double lambda2 = (a + c - d) / 2.0;
            lambda = Math.abs(lambda1) > Math.abs(lambda2) ? lambda1 : lambda2;
        }

        double badElement = 0.0;
        for (int i = 0; i <= n - 2; ++i) {

            double diag = diagonal[i] - lambda;
            double offDiag = offDiagonal[i];
            double nextDiag = diagonal[i + 1] - lambda;

            double s, c;
            if (i == 0) {
                c = diag;
                s = offDiag;
            } else {
                c = offDiagonal[i - 1];
                s = badElement;
            }
            double norm = Math.sqrt(c * c + s * s);
            c /= norm;
            s /= norm;

            double newDiagonal     = c * c * diag + 2.0 * s * c * offDiag
                    + s * s * nextDiag;
            double newOffDiagonal  = s * c * diag + (s*s - c*c) * offDiag
                    - s * c * nextDiag;
            double newNextDiagonal = s * s * diag - 2.0 * s * c * offDiag
                    + c * c * nextDiag;

            if (i != 0)
                offDiagonal[i - 1] = c * offDiagonal[i - 1] + s * badElement;

            if (i != n - 2) {
                badElement = s * offDiagonal[i + 1];
                offDiagonal[i + 1] *= -c;
            }

            diagonal[i] = newDiagonal + lambda;
            offDiagonal[i] = newOffDiagonal;
            diagonal[i + 1] = newNextDiagonal + lambda;

            // Also update the first components of the eigenvectors
            double newComponent
                    = c * eigenvectorFirstComponent[i]
                    + s * eigenvectorFirstComponent[i + 1];
            double newNextComponent
                    = s * eigenvectorFirstComponent[i]
                    - c * eigenvectorFirstComponent[i + 1];
            eigenvectorFirstComponent[i] = newComponent;
            eigenvectorFirstComponent[i + 1] = newNextComponent;
        }
    }
}
