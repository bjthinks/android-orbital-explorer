package com.transvect.orbitalexplorer;

import android.util.Log;

public class SymmetricTridiagonalMatrix {
    private static final String TAG = "SymmetricTridiagonalMat";

    int mN;
    double[] mDiagonal;
    double[] mOffDiagonal;

    public SymmetricTridiagonalMatrix(int N) {
        mN = N;
        mDiagonal = new double[N];
        mOffDiagonal = new double[N - 1];
    }

    double getDiagonal(int i) {
        return mDiagonal[i];
    }

    double getOffDiagonal(int i) {
        return mOffDiagonal[i];
    }

    void setDiagonal(int i, double x) {
        mDiagonal[i] = x;
    }

    void setOffDiagonal(int i, double x) {
        mOffDiagonal[i] = x;
    }

    void QRReductionStep() {
        // Choose a "deflation" parameter lambda equal to the larger eigenvalue
        // of the lower-right 2x2 minor.
        double lambda;
        {
            double a = mDiagonal[mN - 2];
            double b = mOffDiagonal[mN - 2];
            double c = mDiagonal[mN - 1];
            double d = Math.sqrt((a - c) * (a - c) + 4.0 * b * b);
            double lambda1 = (a + c + d) / 2.0;
            double lambda2 = (a + c - d) / 2.0;
            if (Math.abs(lambda1) > Math.abs(lambda2))
                lambda = lambda1;
            else
                lambda = lambda2;
        }
        Log.d(TAG, "Lambda = " + lambda);

        double badElement = 0.0;
        for (int i = 0; i <= mN - 2; ++i) {

            double diagonal = mDiagonal[i] - lambda;
            double offDiagonal = mOffDiagonal[i];
            double nextDiagonal = mDiagonal[i + 1] - lambda;

            double s, c;
            if (i == 0) {
                c = diagonal;
                s = offDiagonal;
            } else {
                c = mOffDiagonal[i - 1];
                s = badElement;
            }
            double n = Math.sqrt(c * c + s * s);
            c /= n;
            s /= n;

            double newDiagonal     = c * c * diagonal + 2.0 * s * c * offDiagonal
                    + s * s * nextDiagonal;
            double newOffDiagonal  = s * c * diagonal + (s*s - c*c) * offDiagonal
                    - s * c * nextDiagonal;
            double newNextDiagonal = s * s * diagonal - 2.0 * s * c * offDiagonal
                    + c * c * nextDiagonal;

            if (i != 0)
                mOffDiagonal[i - 1] = c * mOffDiagonal[i - 1] + s * badElement;

            if (i != mN - 2) {
                badElement = s * mOffDiagonal[i + 1];
                mOffDiagonal[i + 1] *= -c;
            }

            mDiagonal[i] = newDiagonal + lambda;
            mOffDiagonal[i] = newOffDiagonal;
            mDiagonal[i + 1] = newNextDiagonal + lambda;
        }
    }

    public void print() {
        for (int i = 0; i < mN; ++i)
            Log.d(TAG, "Diag " + i + ": " + getDiagonal(i));
        for (int i = 0; i < mN - 1; ++i)
            Log.d(TAG, "Off-diag " + i + ": " + getOffDiagonal(i));
    }
}
