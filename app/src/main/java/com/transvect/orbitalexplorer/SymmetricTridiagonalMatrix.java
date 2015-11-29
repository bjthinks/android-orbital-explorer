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
        double badElement = 0.0;

        double lambda;
        {
            double a = mDiagonal[mN - 2];
            double b = mOffDiagonal[mN - 2];
            double c = mDiagonal[mN - 1];
            double d = Math.sqrt((a - c) * (a - c) + 4.0 * b * b);
            double lambda1 = (a + c + d) / 2.0;
            double lambda2 = (a + c - d) / 2.0;
            if (Math.abs(lambda1) < Math.abs(lambda2))
                lambda = lambda1;
            else
                lambda = lambda2;
        }
        Log.d(TAG, "Lambda = " + lambda);

        for (int i = 0; i <= mN - 2; ++i) {

            double s, c;
            if (i == 0) {
                double n = Math.sqrt((mDiagonal[0] - lambda) * (mDiagonal[0] - lambda)
                        + mOffDiagonal[0] * mOffDiagonal[0]);
                c = (mDiagonal[0] - lambda) / n;
                s = mOffDiagonal[0] / n;
            } else {
                double n = Math.sqrt(mOffDiagonal[i - 1] * mOffDiagonal[i - 1]
                        + badElement * badElement);
                c = mOffDiagonal[i - 1] / n;
                s = badElement / n;
            }

            double newDiagonal
                    = c * c * (mDiagonal[i] - lambda)
                    + 2.0 * s * c * mOffDiagonal[i]
                    + s * s * (mDiagonal[i + 1] - lambda);
            double newOffDiagonal
                    = s * c * (mDiagonal[i] - lambda)
                    + (s * s - c * c) * mOffDiagonal[i]
                    - s * c * (mDiagonal[i + 1] - lambda);
            double newNextDiagonal
                    = s * s * (mDiagonal[i] - lambda)
                    - ( 2.0 * s * c ) * mOffDiagonal[i]
                    + c * c * (mDiagonal[i + 1] - lambda);

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
