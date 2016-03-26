package com.gputreats.orbitalexplorer;

public class MaximumRadiusTable {

    private static double maximumRadiusTable[][] = {
            {
            },
            {
                    4.625,
            },
            {
                    14.125,
                    13.0,
            },
            {
                    28.0,
                    27.0,
                    24.625,
            },
            {
                    46.25,
                    45.25,
                    43.125,
                    39.25,
            },
            {
                    68.75,
                    67.875,
                    65.75,
                    62.375,
                    56.625,
            },
            {
                    95.5,
                    94.625,
                    92.625,
                    89.375,
                    84.5,
                    76.75,
            },
            {
                    126.5,
                    125.5,
                    123.5,
                    120.375,
                    115.875,
                    109.5,
                    99.5,
            },
            {
                    161.625,
                    160.625,
                    158.75,
                    155.625,
                    151.25,
                    145.375,
                    137.125,
                    125.0,
            }
    };

    public static double getMaximumRadius(int N, int L) {
        return maximumRadiusTable[N][L];
    }
}
