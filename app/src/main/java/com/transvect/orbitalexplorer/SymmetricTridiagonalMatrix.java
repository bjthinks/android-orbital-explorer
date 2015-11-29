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

    void QRReduce() {
        // Start by left and right multiplying by the upper left 2x2 matrix
        // [ c  s ]
        // [ s -c ]
        // where (c,s) = (diag[0], offdiag[0]) / sqrt(diag[0]^2 + offdiag[0])
        double badElement;
        {
            double c = mDiagonal[0] / Math.sqrt(mDiagonal[0] * mDiagonal[0]
                    + mOffDiagonal[0] * mOffDiagonal[0]);
            double s = mOffDiagonal[0] / Math.sqrt(mDiagonal[0] * mDiagonal[0]
                    + mOffDiagonal[0] * mOffDiagonal[0]);

            double newDiagonal0 = c * c * mDiagonal[0] + 2.0 * s * c * mOffDiagonal[0]
                    + s * s * mDiagonal[1];
            double newOffDiagonal0 = s * s * mOffDiagonal[0] - s * c * mDiagonal[1];
            double newDiagonal1 = c * c * mDiagonal[1] - s * c * mOffDiagonal[0];
            double newOffDiagonal1 = -c * mOffDiagonal[1];
            badElement = s * mOffDiagonal[1]; // Position (0,2)
            mDiagonal[0] = newDiagonal0;
            mDiagonal[1] = newDiagonal1;
            mOffDiagonal[0] = newOffDiagonal0;
            mOffDiagonal[1] = newOffDiagonal1;
        }

        for (int i = 1; i <= 2; ++i) {
            double c = mOffDiagonal[i - 1] / Math.sqrt(mOffDiagonal[i - 1] * mOffDiagonal[i - 1]
                    + badElement * badElement);
            double s = badElement / Math.sqrt(mOffDiagonal[i - 1] * mOffDiagonal[i - 1]
                    + badElement * badElement);

            double newPriorOffDiagonal = c * mOffDiagonal[i - 1] + s * badElement;
            double newDiagonal = c * c * mDiagonal[i] + 2.0 * s * c * mOffDiagonal[i]
                    + s * s * mDiagonal[i + 1];
            double newOffDiagonal = (s * s - c * c) * mOffDiagonal[i]
                    + s * c * (mDiagonal[i] - mDiagonal[i + 1]);
            double newNextDiagonal = s * s * mDiagonal[i] - 2.0 * s * c * mOffDiagonal[i]
                    + c * c * mDiagonal[i + 1];
            double newNextOffDiagonal = 0.0;
            if (i != 2) {
                newNextOffDiagonal = -c * mOffDiagonal[i + 1];
                badElement = s * mOffDiagonal[i + 1];
            }
            mOffDiagonal[i - 1] = newPriorOffDiagonal;
            mDiagonal[i] = newDiagonal;
            mOffDiagonal[i] = newOffDiagonal;
            mDiagonal[i + 1] = newNextDiagonal;
            if (i != 2)
                mOffDiagonal[i + 1] = newNextOffDiagonal;
        }
    }

    public void print() {
        for (int i = 0; i < mN; ++i)
            Log.d(TAG, "Diag " + i + ": " + getDiagonal(i));
        for (int i = 0; i < mN - 1; ++i)
            Log.d(TAG, "Off-diag " + i + ": " + getOffDiagonal(i));
    }
}
