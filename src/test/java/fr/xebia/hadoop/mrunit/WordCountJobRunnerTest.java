package fr.xebia.hadoop.mrunit;

import static fr.xebia.hadoop.mrunit.MapReduceTestUtils.buildExpectedOutput;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.types.Pair;
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

    private static final LongWritable KEY_INPUT = new LongWritable(1);
    private static final Text TEXT_INPUT = new Text("de dede dede de");
    private static final Pair<LongWritable, Text> PAIR_INPUT = new Pair<LongWritable, Text>(KEY_INPUT, TEXT_INPUT);

    private final MapReduceDriver<LongWritable, Text, Text, IntWritable, Text, IntWritable> wordCountMapReduce = MapReduceDriver
            .newMapReduceDriver();

    @Before
    public void setUp() {
        wordCountMapReduce.withMapper(new WordCountMapper());
        wordCountMapReduce.withReducer(new WordCountReducer());
    }

    @Test
    public void testMapReduce() {
        wordCountMapReduce.withInput(PAIR_INPUT)//
                .withOutput(buildExpectedOutput(new Text("de"), new IntWritable(2)))//
                .withOutput(buildExpectedOutput(new Text("dede"), new IntWritable(2)))//
                .runTest();
    }

    @Test
    public void testMapReduceWithCombiner() {
        wordCountMapReduce.withInput(PAIR_INPUT)//
                .withCombiner(new WordCountReducer())//
                .withOutput(buildExpectedOutput(new Text("de"), new IntWritable(2)))//
                .withOutput(buildExpectedOutput(new Text("dede"), new IntWritable(2)))//
                .runTest();
    }
}
