package fr.xebia.hadoop.mrunit;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.apache.hadoop.mrunit.types.Pair;
import org.fest.assertions.Assertions;
import org.junit.Test;

import fr.xebia.hadoop.mrunit.WordCountReducer.WordsLength;

/**
 * Test class for {@link WordCountReducer}
 * 
 * @author tomtom
 */
public class WordCountReducerTest {

    private final Reducer<Text, IntWritable, Text, IntWritable> wcReducer = new WordCountReducer();
    private final ReduceDriver<Text, IntWritable, Text, IntWritable> wcReduceDriver = new ReduceDriver<Text, IntWritable, Text, IntWritable>(
	    wcReducer);

    @Test
    public void givenKeyWithIntWritableList_shouldDoTheSum_noAssertionWithMrunitForOutput() throws Exception {
	// Given
	Text firstKey = new Text("key1");
	wcReduceDriver.withInput(firstKey, buildValues(5));

	// When
	List<Pair<Text, IntWritable>> outputs = wcReduceDriver.run();

	// Then
	Assertions.assertThat(outputs.get(0)).isEqualTo(new Pair<Text, IntWritable>(firstKey, new IntWritable(5)));
    }

    @Test
    public void givenKeyWithIntWritableList_shouldDoTheSum_assertionWithMrunitForOutput() throws Exception {
	// Given
	Text firstKey = new Text("key1");
	wcReduceDriver//
		.withInput(firstKey, buildValues(5))//
		.withOutput(firstKey, new IntWritable(5));

	// When & Then
	wcReduceDriver.runTest();
    }

    private List<IntWritable> buildValues(int nbValues) {
	List<IntWritable> values = newArrayList();
	while (nbValues-- != 0) {
	    values.add(new IntWritable(1));
	}
	return values;
    }

    @Test
    public void givenKey_shouldIncrementStartsWithLetterAndAllCountersByOne() throws Exception {
	// Given
	Text firstKey = new Text("key1");
	wcReduceDriver//
		.withInput(firstKey, buildValues(5))//
		.withOutput(firstKey, new IntWritable(5))//
		.withCounter(WordsLength.STARTS_WITH_LETTER, 1)//
		.withCounter(WordsLength.STARTS_WITH_DIGIT, 0)//
		.withCounter(WordsLength.ALL, 1);

	// When & Then
	wcReduceDriver.runTest();
    }
}
