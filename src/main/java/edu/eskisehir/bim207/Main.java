package edu.eskisehir.bim207;

import java.io.*;
import java.util.*;

public class Main {


    public Main(String fileName, int topN) throws IOException {
        //Open the given file
        File file = new File(fileName);
        //Read it line by line and also in the given file we need to convert "  "(double space) to " "(single space)
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s = br.readLine().replace("  ", " ");
        StringBuilder total = new StringBuilder(s);
        //I receive all text in single String
        while ((s = br.readLine()) != null)
            total.append(" ").append(s);
        //Split by white space and token them
        String[] tokens = total.toString().split(" ");
        //Removing punctuations and lowercase them
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].replaceAll("[,.':-]", "");
            tokens[i] = tokens[i].toLowerCase();
        }

        computeAvgLengthByFirstChar(tokens);
        Set pairs = calculateMinPairDist(tokens, topN);
        System.out.println("-----------------------------------------------------------");
        print(pairs, topN);

    }

    /**
     * First part of Homework
     */
    private void computeAvgLengthByFirstChar(String[] tokens) {

        //I need an Arraylist to hold first chars of every term in the given file
        ArrayList<Character> chars = new ArrayList<>();

        for (String token : tokens) {
            if (!chars.contains(token.charAt(0)))
                chars.add(token.charAt(0));
        }
        //And sort it
        Collections.sort(chars);

        //Temp is the value where the lengths of the words are added together.
        int temp = 0;
        //numberOfIf is a counter to counting number of word
        double numberOfIf = 0;
        //Linear search on chars and tokens and if first char of word match char array(chars),
        // then add together and count of them
        System.out.println("InitialChar   AverageLength");
        for (Character aChar : chars) {
            for (String token : tokens) {
                if (token.charAt(0) == aChar) {
                    temp += token.length();
                    numberOfIf++;
                }
            }
            //Lastly, divide them and print
            System.out.println(aChar + "             " + temp / numberOfIf);
            temp = 0;
            numberOfIf = 0;
        }
    }/** End of first part*/


    /**
     * Second part of Homework
     */
    private Set calculateMinPairDist(String[] tokens, int topN) {

        ArrayList<String> uniqArray = new ArrayList<>();
        ArrayList<Double> factor = new ArrayList<>();
        Set<String> pairs = new HashSet<>();

        for (int i = 0; i < tokens.length - 1; i++) {
            for (int j = i + 1; j < tokens.length; j++) {
                //This if statement checks that pairs is equal or not
                if (tokens[i].equals(tokens[j])) {
                    continue;
                }
                //This if statement checks duplicates
                else if (uniqArray.contains(tokens[i] + " " + tokens[j])) {
                    continue;
                }
                //If pairs are not duplicate in temp, then add them in temp by adding a whitespace between us
                //And calculate total min distance
                else {
                    uniqArray.add(tokens[i] + " " + tokens[j]);
                    double result = calculates_total_distance(tokens, tokens[i], tokens[j]);
                    //Add the result in factor
                    factor.add(result);
                }
            }
        }

        for (int i = 0; i < topN; i++) {
            String p = ("Pair{t1 = '" + uniqArray.get(maxIndexes(factor, topN).get(i)).split(" ")[0]) + "', t2 = '" +
                    uniqArray.get(maxIndexes(factor, topN).get(i)).split(" ")[1] +
                    "', factor = " + factor.get(maxIndexes(factor, topN).get(i)) + "}";
            pairs.add(p);
        }

        return pairs;
    }

    public ArrayList<Integer> maxIndexes(ArrayList<Double> factor, int topN) {

        //To store topN numbers' index
        ArrayList<Integer> topNIndex = new ArrayList<>();
        //Assume that first index is max
        double maxNumber = factor.get(0);
        //I copied factor
        ArrayList copyFactor = (ArrayList) factor.clone();
        //I need to find max factor of terms and add to copyFactor

        for (int i = 0; i < topN; i++) {
            for (int j = 1; j < factor.size(); j++) {
                if ((Double) copyFactor.get(j) > maxNumber) {
                    maxNumber = (double) copyFactor.get(j);
                }
            }
            int indexOfMax = copyFactor.indexOf(maxNumber);
            topNIndex.add(indexOfMax);
            copyFactor.remove(maxNumber);
            copyFactor.add(indexOfMax, 0.0);
            maxNumber = factor.get(0);
        }
        return topNIndex;
    }

    public double calculates_total_distance(String[] tokens, String term1, String term2) {
        //I need to find out how many times the word I want occurs in the text.
        double freq1 = num_of_term(tokens, term1);
        double freq2 = num_of_term(tokens, term2);
        double sum = 0;

        for (int i = 0; i < tokens.length; i++) {
            //I are searching tokens which is not equal term1
            //If it equals then skip it
            if (!tokens[i].equals(term1))
                continue;

            for (int j = i; j < tokens.length; j++) {
                //I are searching another term
                if (!tokens[j].equals(term2))
                    continue;
                //if I found then sum their indexes
                sum = sum + j - i;
                break;
            }
        }
        //And calculate the given formula
        return (freq1 * freq2) / (1.0 + Math.log(sum));
    }

    public int num_of_term(String[] tokens, String term) {
        ///This is that how many times occurs in text
        int num_of_term = 0;
        for (String token : tokens) {
            {
                if (token.equals(term))
                    num_of_term++;
            }
        }
        return num_of_term;


    }

    public void print(Set set, int topN) {
        //Convert to ArrayList from Set
        ArrayList<String> printedSet = new ArrayList<>(set);
        //I need an arraylist to store indexes
        ArrayList<String> indexes = new ArrayList<>();

        //I spilt printedSet by 4 part to obtain their indexes
        for (int i = 0; i < topN; i++) {
            String temp = (printedSet.get(i));
            String[] split = temp.split("=");
            indexes.add(split[3]);
        }
        //Sort them from large to small
        Collections.sort(indexes);
        Collections.reverse(indexes);
        //Lastly I need print sequentially
        for (int i = 0; i < topN; i++) {
            for (int j = 0; j < topN; j++) {
                String temp = printedSet.get(j);
                if (temp.contains(indexes.get(i))) {
                    System.out.println(printedSet.get(j));
                    printedSet.remove(printedSet.get(j));
                    printedSet.add(j, "-1");
                }
            }
        }
    }

    /**
     * End of second part
     */

    public static void main(String[] args) throws IOException {
        new Main(args[0], Integer.parseInt(args[1]));
    }


}