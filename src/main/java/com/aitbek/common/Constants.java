package com.aitbek.common;

public final class Constants {
    private Constants() {} // Утилитный класс

    public static final int MIN_WEIGHT = 1;
    public static final int MAX_WEIGHT = 10;

    public static final int SMALL_MIN_NODES = 6;
    public static final int SMALL_MAX_NODES = 10;
    public static final int MEDIUM_MIN_NODES = 10;
    public static final int MEDIUM_MAX_NODES = 20;
    public static final int LARGE_MIN_NODES = 20;
    public static final int LARGE_MAX_NODES = 50;

    public static final double SPARSE_DENSITY = 0.2;
    public static final double MEDIUM_DENSITY = 0.35;
    public static final double DENSE_DENSITY = 0.5;

    public static final String ALGO_SCC = "SCC";
    public static final String ALGO_TOPO = "TOPOLOGICAL_SORT";
    public static final String ALGO_DAG_SP = "DAG_SHORTEST_PATH";
    public static final String ALGO_DAG_LP = "DAG_LONGEST_PATH";

    public static final String[] CSV_HEADERS = {
            "dataset_id", "n", "m", "is_dag", "algo", "metric_name",
            "metric_value", "time_ns", "run_id", "seed", "notes"
    };
}