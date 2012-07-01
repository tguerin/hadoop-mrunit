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
    private final Text word = new Text();
    private List<String> ignoredWords = null;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
	super.setup(context);
	String ignoredWordsFromConf = context.getConfiguration().get(KEY_IGNORED_WORDS);
	if (!isNullOrEmpty(ignoredWordsFromConf)) {
	    ignoredWords = newArrayList(ignoredWordsFromConf.split(";"));
	}
    }

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
	String line = value.toString();
	StringTokenizer tokenizer = new StringTokenizer(line);
	String token = null;
	while (tokenizer.hasMoreTokens()) {
	    token = tokenizer.nextToken();
	    if (ignoredWords == null || !ignoredWords.contains(token)) {
		word.set(token);
		context.write(word, ONE);
	    }
	}
    }
}