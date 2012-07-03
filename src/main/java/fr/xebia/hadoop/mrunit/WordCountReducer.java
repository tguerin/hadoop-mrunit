package fr.xebia.hadoop.mrunit;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public final class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    static enum WordsLength {
        STARTS_WITH_DIGIT, STARTS_WITH_LETTER, ALL;
    };

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException,
            InterruptedException {

        if (Character.isDigit(key.toString().charAt(0))) {
            context.getCounter(WordsLength.STARTS_WITH_DIGIT).increment(1);
        } else {
            context.getCounter(WordsLength.STARTS_WITH_LETTER).increment(1);
        }
        context.getCounter(WordsLength.ALL).increment(1);

        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        context.write(key, new IntWritable(sum));
    }
}