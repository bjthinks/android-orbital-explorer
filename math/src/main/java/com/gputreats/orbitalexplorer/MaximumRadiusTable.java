package com.gputreats.orbitalexplorer;

enum MaximumRadiusTable {
    ;

    private static final double[][] MAXIMUM_RADIUS_TABLE = {
            {
            },
            {
                    5.875,
            },
            {
                    17.0,
                    15.875,
            },
            {
                    32.75,
                    31.75,
                    29.375,
            },
            {
                    53.0,
                    52.0,
                    49.875,
                    45.875,
            },
            {
                    77.625,
                    76.75,
                    74.625,
                    71.25,
                    65.5,
            },
            {
                    106.75,
                    105.75,
                    103.75,
                    100.5,
                    95.625,
                    87.875,
            },
            {
                    140.125,
                    139.125,
                    137.125,
                    134.0,
                    129.5,
                    123.0,
                    113.125,
            },
            {
                    177.75,
                    176.875,
                    174.875,
                    171.75,
                    167.375,
                    161.375,
                    153.25,
                    141.125,
            }
    };

    static double getMaximumRadius(int qN, int qL) {
        return MAXIMUM_RADIUS_TABLE[qN][qL];
    }
}
