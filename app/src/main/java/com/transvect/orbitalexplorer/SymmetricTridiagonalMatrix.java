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
        double badElement = 0.0;

        for (int i = 0; i <= mN - 2; ++i) {
            double s, c;
            if (i == 0) {
                double n = Math.sqrt(mDiagonal[0] * mDiagonal[0]
                        + mOffDiagonal[0] * mOffDiagonal[0]);
                c = mDiagonal[0] / n;
                s = mOffDiagonal[0] / n;
            } else {
                double n = Math.sqrt(mOffDiagonal[i - 1] * mOffDiagonal[i - 1]
                        + badElement * badElement);
                c = mOffDiagonal[i - 1] / n;
                s = badElement / n;
            }

            double newDiagonal = c * c * mDiagonal[i] + 2.0 * s * c * mOffDiagonal[i]
                    + s * s * mDiagonal[i + 1];
            double newOffDiagonal = s * s * mOffDiagonal[i] - s * c * mDiagonal[i + 1];
            double newNextDiagonal = c * c * mDiagonal[i + 1] - s * c * mOffDiagonal[i];

            if (i != 0) {
                newOffDiagonal -= c * c * mOffDiagonal[i];
                newOffDiagonal += s * c * mDiagonal[i];
                mOffDiagonal[i - 1] = c * mOffDiagonal[i - 1] + s * badElement;
                newNextDiagonal += s * s * mDiagonal[i];
                newNextDiagonal -= s * c * mOffDiagonal[i]; // Yes, again
            }

            if (i != mN - 2) {
                badElement = s * mOffDiagonal[i + 1];
                mOffDiagonal[i + 1] *= -c;
            }

            mDiagonal[i] = newDiagonal;
            mDiagonal[i + 1] = newNextDiagonal;
            mOffDiagonal[i] = newOffDiagonal;
        }
    }

    public void print() {
        for (int i = 0; i < mN; ++i)
            Log.d(TAG, "Diag " + i + ": " + getDiagonal(i));
        for (int i = 0; i < mN - 1; ++i)
            Log.d(TAG, "Off-diag " + i + ": " + getOffDiagonal(i));
    }
}
