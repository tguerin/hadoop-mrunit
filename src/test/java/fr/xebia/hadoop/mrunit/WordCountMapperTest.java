package fr.xebia.hadoop.mrunit;

import static fr.xebia.hadoop.mrunit.MapReduceTestUtils.buildExpectedOutput;
import static fr.xebia.hadoop.mrunit.WordCountJobRunner.KEY_IGNORED_WORDS;
import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

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
 * @author Tom
 */
public class WordCountMapperTest {

    private static final LongWritable KEY_INPUT = new LongWritable(1);
    private static final Text TEXT_INPUT = new Text("de dede ");
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
	assertThat(outputs).hasSize(2);

	Pair<Text, IntWritable> firstOutput = outputs.get(0);
	assertThat(firstOutput).isEqualTo(buildExpectedOutput(new Text("de"), EXPECTED_COUNT));

	Pair<Text, IntWritable> secondOutput = outputs.get(1);
	assertThat(secondOutput).isEqualTo(buildExpectedOutput(new Text("dede"), EXPECTED_COUNT));
    }

    @Test
    public void givenTextInput_shouldOutputEachWordAsKeyAndOneAsValue_assertionWithMrunitForOutput() throws Exception {
	// Given
	wcMapDriver.withInput(PAIR_INPUT)
		//
		.withOutput(buildExpectedOutput(new Text("de"), EXPECTED_COUNT))
		.withOutput(buildExpectedOutput(new Text("dede"), EXPECTED_COUNT));

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

	// When & Then
	wcMapDriver.runTest();
    }
}
