package fr.xebia.hadoop.mrunit;

import static fr.xebia.hadoop.mrunit.MapReduceTestUtils.buildExpectedOutput;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test class for {@linkplain WordCountJobRunner}
 * 
 * @author Tom
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class WordCountJobRunnerTest {

    private final MapReduceDriver<LongWritable, Text, Text, IntWritable, Text, IntWritable> wordCountMapReduce = MapReduceDriver
            .newMapReduceDriver();

    @Before
    public void setUp() {
        wordCountMapReduce.withMapper(new WordCountMapper());
        wordCountMapReduce.withReducer(new WordCountReducer());
    }

    @Test
    public void testMapReduce() {
        wordCountMapReduce//
                .withInput(new LongWritable(1), new Text("word1 word2 word2 word1"))//
                .withOutput(buildExpectedOutput(new Text("word1"), new IntWritable(2)))//
                .withOutput(buildExpectedOutput(new Text("word2"), new IntWritable(2)))//
                .runTest();
    }

    @Test
    public void testMapReduceWithCombiner() {
        wordCountMapReduce//
                .withInput(new LongWritable(1), new Text("word1 word2 word2 word1"))//
                .withCombiner(new WordCountReducer())//
                .withOutput(buildExpectedOutput(new Text("word1"), new IntWritable(2)))//
                .withOutput(buildExpectedOutput(new Text("word2"), new IntWritable(2)))//
                .runTest();
    }
}
