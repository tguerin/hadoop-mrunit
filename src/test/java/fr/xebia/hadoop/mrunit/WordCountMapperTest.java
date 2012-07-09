package fr.xebia.hadoop.mrunit;

import static fr.xebia.hadoop.mrunit.MapReduceTestUtils.buildExpectedOutput;
import static fr.xebia.hadoop.mrunit.WordCountJobRunner.KEY_IGNORED_WORDS;
import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.types.Pair;
import org.junit.Test;

/**
 * Test class for {@link WordCountMapper}
 * 
 * @author Tom
 */
public class WordCountMapperTest {

    private static final IntWritable EXPECTED_COUNT = new IntWritable(1);

    private static final String ignoredWords = "word1;word2";

    private final MapDriver<LongWritable, Text, Text, IntWritable> wcMapDriver = MapDriver
            .newMapDriver(new WordCountMapper());

    @Test
    public void givenTextInput_shouldOutputEachWordAsKeyAndOneAsValue_noAssertionWithMrunitForOutput() throws Exception {
        // When
        List<Pair<Text, IntWritable>> outputs = wcMapDriver.withInput(new LongWritable(1), new Text("word1 word2 "))
                .run();

        // Then
        assertThat(outputs).hasSize(2);

        Pair<Text, IntWritable> firstOutput = outputs.get(0);
        assertThat(firstOutput).isEqualTo(buildExpectedOutput(new Text("word1"), EXPECTED_COUNT));

        Pair<Text, IntWritable> secondOutput = outputs.get(1);
        assertThat(secondOutput).isEqualTo(buildExpectedOutput(new Text("word2"), EXPECTED_COUNT));
    }

    @Test
    public void givenTextInput_shouldOutputEachWordAsKeyAndOneAsValue_assertionWithMrunitForOutput() throws Exception {
        wcMapDriver//
                .withInput(new LongWritable(1), new Text("word1 word2 "))//
                .withOutput(buildExpectedOutput(new Text("word1"), EXPECTED_COUNT))//
                .withOutput(buildExpectedOutput(new Text("word2"), EXPECTED_COUNT))//
                .runTest();
    }

    @Test
    public void givenTextInputWithIgnoredWordsInConf_shouldOutputEachWordIfNotIgnoredAsKeyAndOneAsValue()
            throws Exception {
        Configuration configuration = new Configuration();
        configuration.set(KEY_IGNORED_WORDS, ignoredWords);

        wcMapDriver.withConfiguration(configuration).withInput(new LongWritable(1), new Text("word1 word2 ")).runTest();
    }
}
