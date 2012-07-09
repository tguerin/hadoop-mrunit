package fr.xebia.hadoop.mrunit;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static fr.xebia.hadoop.mrunit.WordCountJobRunner.KEY_IGNORED_WORDS;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public final class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    private static final IntWritable ONE = new IntWritable(1);
    private List<String> ignoredWords = newArrayList();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        String ignoredWordsFromConf = context.getConfiguration().get(KEY_IGNORED_WORDS);
        if (!isNullOrEmpty(ignoredWordsFromConf)) {
            ignoredWords = newArrayList(ignoredWordsFromConf.split(";"));
        }
    }

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer tokenizer = new StringTokenizer(value.toString());
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!ignoredWords.contains(token)) {
                context.write(new Text(token), ONE);
            }
        }
    }
}