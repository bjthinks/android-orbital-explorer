package com.transvect.orbitalexplorer;

import android.util.Log;

public class SymmetricTridiagonalMatrix {
    private static final String TAG = "SymmetricTridiagonalMat";

    int N;
    double[] diagonal;
    double[] offDiagonal;
    double[] component;

    public SymmetricTridiagonalMatrix(int N_) {
        N = N_;
        diagonal = new double[N_];
        offDiagonal = new double[N_ - 1];
        component = new double[N_];
        component[0] = 1.0;
    }

    public double getDiagonal(int i) {
        return diagonal[i];
    }

    public double getOffDiagonal(int i) {
        return offDiagonal[i];
    }

    public double getComponent(int i) {
        return component[i];
    }

    public void setDiagonal(int i, double x) {
        diagonal[i] = x;
    }

    public void setOffDiagonal(int i, double x) {
        offDiagonal[i] = x;
    }

    public void QRReduce() {
        int n = N;
        int fail = 0;
        while (n > 1) {
            QRReductionStep(n);
            ++fail;
            while (n > 1 && Math.abs(offDiagonal[n - 2]) < 1e-15 * Math.abs(diagonal[n - 1])) {
                --n;
                fail = 0;
            }
            if (fail > 32)
                throw new RuntimeException();
        }
    }

    private void QRReductionStep(int n) {
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
            if (Math.abs(lambda1) > Math.abs(lambda2))
                lambda = lambda1;
            else
                lambda = lambda2;
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
            double newComponent     = c * component[i] + s * component[i + 1];
            double newNextComponent = s * component[i] - c * component[i + 1];
            component[i] = newComponent;
            component[i + 1] = newNextComponent;
        }
    }

    public void print() {
        for (int i = 0; i < N; ++i)
            Log.d(TAG, "Diag " + i + ": " + diagonal[i]);
        for (int i = 0; i < N - 1; ++i)
            Log.d(TAG, "Off-diag " + i + ": " + offDiagonal[i]);
        for (int i = 0; i < N; ++i)
            Log.d(TAG, "EV 1st component " + i + ": " + component[i]);
    }
}
