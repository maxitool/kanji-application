package com.example.kanjiapplication;

public class DrawDataKanji {

    public class InfoFeaturesKanji {
        public boolean isFilled;
        public int countFeatures;
        public int[] countPointsInFeatures;
        public float[][] endPointsOfFeatures; //xEnd - xStart, yEnd - yStart
        public int[][][] percentErrorsEndPoints; //%errors endPoints ([[xSmaller,xBigger],[ySmaller,yBigger]]) ; min = 0%, max = 100%
        public int[][] timesOfAnim; //times of animation gifs

        InfoFeaturesKanji() {
            isFilled = false;
            countFeatures = 0;
            countPointsInFeatures = new int[0];
            endPointsOfFeatures = new float[0][0];
            percentErrorsEndPoints = new int[0][0][0];
            timesOfAnim = new int[0][0];
        }
        InfoFeaturesKanji(int[] countPointsInFeatures, float[][] endPointsOfFeatures, int[][][] percentErrorsEndPoints, int[][] timesOfAnim) {
            this.isFilled = true;
            this.countPointsInFeatures = countPointsInFeatures;
            this.endPointsOfFeatures = endPointsOfFeatures;
            this.countFeatures = countPointsInFeatures.length;
            this.percentErrorsEndPoints = percentErrorsEndPoints;
            this.timesOfAnim = timesOfAnim;
        }
    }
    private final InfoFeaturesKanji ichi = new InfoFeaturesKanji(
            new int[] {1}, //count Points In Features
            new float[][] {{179.8f, -9.1f}}, //end Points Of Features
            new int[][][] {{{75, 10}, {20, 20}}}, //percentage of Errors of End Points
            new int[][] {{2800, 500}} //times of animation gifs
            );

    private final InfoFeaturesKanji ni = new InfoFeaturesKanji(
            new int[] {1, 1}, //count Points In Features
            new float[][] {{97.8f, -10.9f}, {186.9f, -5.8f}}, //end Points Of Features
            new int[][][] {{{60, 20}, {30, 30}}, {{75, 10}, {20, 20}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji san = new InfoFeaturesKanji(
            new int[] {1, 1, 1}, //count Points In Features
            new float[][] {{107.9f, -7.9f}, {97.8f, -5.8f}, {176.8f, -7.6f}}, //end Points Of Features
            new int[][][] {{{75, 10}, {20, 20}}, {{75, 10}, {20, 20}}, {{75, 10}, {20, 20}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 6000}, {6800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji yon = new InfoFeaturesKanji(
            new int[] {1, 2, 1, 2, 1}, //count Points In Features
            new float[][] {{16.5f, 84f}, {144.8f, -12.7f}, {-8.6f, 97.5f}, {-32.5f, 49.8f},
                    {-2.5f, 27.2f}, {43.7f, 1.8f}, {123.4f, -13.7f}}, //end Points Of Features
            new int[][][] {{{40, 50}, {40, 40}}, {{50, 20}, {10, 10}}, {{10, 20}, {50, 30}},
                    {{50, 20}, {50, 20}}, {{50, 50}, {20, 20}}, {{50, 30}, {20, 20}}, {{50, 20}, {20, 30}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 6000}, {6800, 8000},
                    {8800, 10000}, {10800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji go = new InfoFeaturesKanji(
            new int[] {1, 1, 2, 1}, //count Points In Features
            new float[][] {{96f, -5.1f}, {-35f, 126f}, {89.9f, -5.3f},
                    {-0.4f, 62.8f}, {190.2f, -2.5f}}, //end Points Of Features
            new int[][][] {{{60, 30}, {15, 15}}, {{20, 40}, {50, 30}}, {{40, 40}, {15, 15}},
                    {{10, 10}, {50, 30}}, {{60, 10}, {10, 15}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 6000}, {6800, 8000}, {8800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji roku = new InfoFeaturesKanji(
            new int[] {1, 1, 1, 1}, //count Points In Features
            new float[][] {{6.1f, 46.2f}, {172.4f, -10.1f}, {-60.9f, 54.6f},
                    {54.2f, 52.8f}}, //end Points Of Features
            new int[][][] {{{10, 10}, {50, 30}}, {{70, 15}, {10, 10}}, {{70, 15}, {50, 15}},
                    {{70, 15}, {50, 15}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 6000}, {6800, 8000},
                    {8800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji nana = new InfoFeaturesKanji(
            new int[] {1, 2}, //count Points In Features
            new float[][] {{171.4f, -28.4f}, {3.5f, 136.4f}, {79.7f, 9.8f}}, //end Points Of Features
            new int[][][] {{{60, 15}, {30, 15}}, {{15, 15}, {50, 15}}, {{50, 30}, {30, 30}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji hachi = new InfoFeaturesKanji(
            new int[] {1, 2}, //count Points In Features
            new float[][] {{-51.5f, 74.4f}, {14.5f, 2.5f}, {74.7f, 92.4f}}, //end Points Of Features
            new int[][][] {{{50, 30}, {50, 40}}, {{80, 80}, {30, 30}}, {{50, 30}, {50, 20}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji kyuu = new InfoFeaturesKanji(
            new int[] {2, 4}, //count Points In Features
            new float[][] {{-17.3f, 94f}, {-51.3f, 77.2f}, {98.5f, -14.2f},
                    {-14.6f, 107.8f}, {76.4f, 5f}, {2.5f, -21.5f}}, //end Points Of Features
            new int[][][] {{{10, 15}, {10, 10}}, {{15, 15}, {30, 20}}, {{30, 30}, {10, 25}},
                    {{10, 15}, {50, 15}}, {{50, 20}, {10, 10}}, {{20, 20}, {80, 100}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji jyuu = new InfoFeaturesKanji(
            new int[] {1, 1}, //count Points In Features
            new float[][] {{186.3f, -5.1f}, {0.8f, 182.3f}}, //end Points Of Features
            new int[][][] {{{70, 15}, {10, 10}}, {{10, 10}, {70, 10}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji hyaku = new InfoFeaturesKanji(
            new int[] {1, 1, 1, 2, 1, 1}, //count Points In Features
            new float[][] {{178f, -6.8f}, {-25.9f, -45.5f}, {6.8f, 99.3f}, {99.3f, -5.1f},
                    {0f, 100.2f}, {91.4f, -1.8f}, {86.6f, -3.3f}}, //end Points Of Features
            new int[][][] {{{70, 15}, {10, 10}}, {{30, 90}, {70, 50}}, {{10, 10}, {50, 30}}, {{60, 30}, {10, 10}},
                    {{10, 10}, {50, 20}}, {{60, 30}, {10, 10}}, {{60, 30}, {10, 10}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 6000}, {6800, 8000},
                    {8800, 10000}, {10800, 12000}, {12800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji sen = new InfoFeaturesKanji(
            new int[] {1, 1, 1}, //count Points In Features
            new float[][] {{-101.2f, 32.5f}, {183f, -15.5f}, {2.8f, 168.6f}}, //end Points Of Features
            new int[][][] {{{30, 50}, {20, 10}}, {{60, 10}, {10, 10}}, {{10, 10}, {60, 10}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 6000}, {6800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji man = new InfoFeaturesKanji(
            new int[] {1, 3, 2}, //count Points In Features
            new float[][] {{164.6f, -3.3f}, {61.7f, 8.2f}, {-34.3f, 89.6f},
                    {-26f, -17f}, {-29.2f, 76.6f}, {-55.9f, 66.8f}}, //end Points Of Features
            new int[][][] {{{60, 10}, {10, 10}}, {{60, 30}, {10, 10}}, {{20, 10}, {50, 20}},
                    {{30, 50}, {60, 50}}, {{30, 30}, {20, 50}}, {{30, 30}, {20, 50}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 6000}, {6800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji en = new InfoFeaturesKanji(
            new int[] {1, 3, 1, 1}, //count Points In Features
            new float[][] {{5.1f, 159.2f}, {126.2f, -8.4f}, {0.2f, 171.9f},
                    {-23.2f, -14.1f}, {3.6f, 60.7f}, {110.5f, -7.6f}}, //end Points Of Features
            new int[][][] {{{10, 10}, {60, 20}}, {{40, 30}, {10, 10}}, {{10, 10}, {50, 10}},
                    {{100, 60}, {100, 70}}, {{10, 10}, {50, 30}}, {{40, 10}, {10, 15}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 6000}, {6800, 8000},
                    {8800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji kuchi = new InfoFeaturesKanji(
            new int[] {1, 2, 1}, //count Points In Features
            new float[][] {{13f, 84.6f}, {128.2f, -10.4f}, {-19.5f, 95f},
                    {95.2f, -8.4f}}, //end Points Of Features
            new int[][][] {{{25, 15}, {45, 30}}, {{50, 20}, {15, 10}}, {{10, 20}, {40, 25}},
                    {{40, 20}, {15, 10}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 6000}, {6800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji me = new InfoFeaturesKanji(
            new int[] {1, 2, 1, 1, 1}, //count Points In Features
            new float[][] {{4.3f, 156.4f}, {113f, -5.1f}, {-0.8f, 161.9f},
                    {90.9f, -5.1f}, {91.9f, 0f}, {106.4f, -2.5f}}, //end Points Of Features
            new int[][][] {{{10, 10}, {50, 15}}, {{50, 25}, {10, 10}}, {{10, 10}, {50, 15}},
                    {{25, 15}, {10, 10}}, {{25, 15}, {10, 10}}, {{25, 15}, {10, 10}}}, //percentage of Errors of End Points
            new int[][] {{2800, 4000}, {4800, 6000}, {6800, 8000},
                    {8800, 10000}, {10800, 500}} //times of animation gifs
    );

    private final InfoFeaturesKanji example = new InfoFeaturesKanji(
            new int[] {}, //count Points In Features
            new float[][] {}, //end Points Of Features
            new int[][][] {}, //percentage of Errors of End Points
            new int[][] {} //times of animation gifs
    );

    public InfoFeaturesKanji getInfoFeaturesKanji(String kanji) {
        switch (kanji) {
            case "一":
                return ichi;
            case "二":
                return ni;
            case "三":
                return san;
            case "四":
                return yon;
            case "五":
                return go;
            case "六":
                return roku;
            case "七":
                return nana;
            case "八":
                return hachi;
            case "九":
                return kyuu;
            case "十":
                return jyuu;
            case "百":
                return hyaku;
            case "千":
                return sen;
            case "万":
                return man;
            case "円":
                return en;
            case "口":
                return kuchi;
            case "目":
                return me;
            default:
                return new InfoFeaturesKanji();
        }
    }

    public String getRomajiKanji(String kanji) {
        switch (kanji) {
            case "一":
                return "ichi";
            case "二":
                return "ni";
            case "三":
                return "san";
            case "四":
                return "yon";
            case "五":
                return "go";
            case "六":
                return "roku";
            case "七":
                return "nana";
            case "八":
                return "hachi";
            case "九":
                return "kyuu";
            case "十":
                return "jyuu";
            case "百":
                return "hyaku";
            case "千":
                return "sen";
            case "万":
                return "man";
            case "円":
                return "en";
            case "口":
                return "kuchi";
            case "目":
                return "me";
            default:
                return "";
        }
    }
}
