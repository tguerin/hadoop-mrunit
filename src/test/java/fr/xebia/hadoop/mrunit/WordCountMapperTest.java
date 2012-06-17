package fr.xebia.hadoop.mrunit;

import static fr.xebia.hadoop.mrunit.WordCountJobRunner.KEY_IGNORED_WORDS;
import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.types.Pair;
import org.junit.Test;

/**
 * Test class for {@link WordCountMapper}
 * 
 * @author tomtom
 */
public class WordCountMapperTest {

    private static final LongWritable KEY_INPUT = new LongWritable(1);
    private static final Text TEXT_INPUT = new Text("de dede de dedede dede dedede de de de dede de");
    private static final Pair<LongWritable, Text> PAIR_INPUT = new Pair<LongWritable, Text>(KEY_INPUT, TEXT_INPUT);

    private static final IntWritable EXPECTED_COUNT = new IntWritable(1);

    private static final String ignoredWords = "de;dede";

    private final Mapper<LongWritable, Text, Text, IntWritable> wcMapper = new WordCountMapper();
    private final MapDriver<LongWritable, Text, Text, IntWritable> wcMapDriver = new MapDriver<LongWritable, Text, Text, IntWritable>(
	    wcMapper);

    @Test
    public void givenTextInput_shouldOutputEachWordAsKeyAndOneAsValue_noAssertionWithMrunitForOutput() throws Exception {
	// When
	List<Pair<Text, IntWritable>> outputs = wcMapDriver.withInput(PAIR_INPUT).run();

	// Then
	Iterator<Pair<Text, IntWritable>> outputsIt = outputs.iterator();
	StringTokenizer tokenizer = new StringTokenizer(TEXT_INPUT.toString());
	while (tokenizer.hasMoreTokens()) {
	    Pair<Text, IntWritable> output = outputsIt.next();
	    assertThat(tokenizer.nextToken()).isEqualTo(output.getFirst().toString());
	    assertThat(EXPECTED_COUNT).isEqualTo(output.getSecond());
	}
    }

    @Test
    public void givenTextInput_shouldOutputEachWordAsKeyAndOneAsValue_assertionWithMrunitForOutput() throws Exception {
	// Given
	wcMapDriver.withInput(PAIR_INPUT);
	StringTokenizer tokenizer = new StringTokenizer(TEXT_INPUT.toString());
	while (tokenizer.hasMoreTokens()) {
	    wcMapDriver.withOutput(new Pair<Text, IntWritable>(new Text(tokenizer.nextToken()), EXPECTED_COUNT));
	}

	// When & Then
	wcMapDriver.runTest();
    }

    @Test
    public void givenTextInputWithIgnoredWordsInConf_shouldOutputEachWordIfNotIgnoredAsKeyAndOneAsValue()
	    throws Exception {
	// Given
	Configuration configuration = new Configuration();
	configuration.set(KEY_IGNORED_WORDS, ignoredWords);
	wcMapDriver.withInput(PAIR_INPUT).withConfiguration(configuration);

	StringTokenizer tokenizer = new StringTokenizer(TEXT_INPUT.toString());
	String token = null;
	List<String> ignoredWordsList = Arrays.asList(ignoredWords.split(";"));
	while (tokenizer.hasMoreTokens()) {
	    token = tokenizer.nextToken();
	    if (ignoredWordsList.contains(token)) {
		wcMapDriver.withOutput(new Pair<Text, IntWritable>(new Text(token), EXPECTED_COUNT));
	    }
	}

	// When & Then
	wcMapDriver.runTest();
    }
}
