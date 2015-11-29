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

    void QRReduce(boolean log) {
        if (log) print();
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
            if (log) Log.d(TAG, " ");
            if (log) Log.d(TAG, "s = " + s);
            if (log) Log.d(TAG, "c = " + c);

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
            if (log) Log.d(TAG, " ");
            if (log) print();
            if (log) Log.d(TAG, "bad element = " + badElement);
        }

        {
            double c = mOffDiagonal[0] / Math.sqrt(mOffDiagonal[0] * mOffDiagonal[0]
                    + badElement * badElement);
            double s = badElement / Math.sqrt(mOffDiagonal[0] * mOffDiagonal[0]
                    + badElement * badElement);
            if (log) Log.d(TAG, " ");
            if (log) Log.d(TAG, "s = " + s);
            if (log) Log.d(TAG, "c = " + c);

            double newOffDiagonal0 = c * mOffDiagonal[0] + s * badElement;
            double newDiagonal1 = c * c * mDiagonal[1] + 2.0 * s * c * mOffDiagonal[1]
                    + s * s * mDiagonal[2];
            double newOffDiagonal1 = (s * s - c * c) * mOffDiagonal[1]
                    + s * c * (mDiagonal[1] - mDiagonal[2]);
            double newDiagonal2 = s * s * mDiagonal[1] - 2.0 * s * c * mOffDiagonal[1]
                    + c * c * mDiagonal[2];
            double newOffDiagonal2 = -c * mOffDiagonal[2];
            badElement = s * mOffDiagonal[2];
            mOffDiagonal[0] = newOffDiagonal0;
            mDiagonal[1] = newDiagonal1;
            mOffDiagonal[1] = newOffDiagonal1;
            mDiagonal[2] = newDiagonal2;
            mOffDiagonal[2] = newOffDiagonal2;
            if (log) Log.d(TAG, " ");
            if (log) print();
            if (log) Log.d(TAG, "bad element = " + badElement);
        }

        {
            double c = mOffDiagonal[1] / Math.sqrt(mOffDiagonal[1] * mOffDiagonal[1]
                    + badElement * badElement);
            double s = badElement / Math.sqrt(mOffDiagonal[1] * mOffDiagonal[1]
                    + badElement * badElement);
            if (log) Log.d(TAG, " ");
            if (log) Log.d(TAG, "s = " + s);
            if (log) Log.d(TAG, "c = " + c);

            double newOffDiagonal1 = c * mOffDiagonal[1] + s * badElement;
            double newDiagonal2 = c * c * mDiagonal[2] + 2.0 * s * c * mOffDiagonal[2]
                    + s * s * mDiagonal[3];
            double newOffDiagonal2 = (s * s - c * c) * mOffDiagonal[2]
                    + s * c * (mDiagonal[2] - mDiagonal[3]);
            double newDiagonal3 = s * s * mDiagonal[2] - 2.0 * s * c * mOffDiagonal[2]
                    + c * c * mDiagonal[3];
            // double newOffDiagonal3 = -c * mOffDiagonal[3];
            // badElement = s * mOffDiagonal[3];
            mOffDiagonal[1] = newOffDiagonal1;
            mDiagonal[2] = newDiagonal2;
            mOffDiagonal[2] = newOffDiagonal2;
            mDiagonal[3] = newDiagonal3;
            // mOffDiagonal[3] = newOffDiagonal3;
            if (log) Log.d(TAG, " ");
            if (log) print();
            // Log.d(TAG, "bad element = " + badElement);
        }

        if (log) Log.d(TAG, " ");
        if (log) Log.d(TAG, "END ROUND");
        if (log) Log.d(TAG, " ");
    }

    private void print() {
        for (int i = 0; i < mN; ++i)
            Log.d(TAG, "Diag " + i + ": " + getDiagonal(i));
        for (int i = 0; i < mN - 1; ++i)
            Log.d(TAG, "Off-diag " + i + ": " + getOffDiagonal(i));
    }
}
