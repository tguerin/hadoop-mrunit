package fr.xebia.hadoop.mrunit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tomtom
 * 
 */
public class WordCountJobRunner extends Configured implements Tool {

    private static final Logger LOG = LoggerFactory.getLogger(WordCountJobRunner.class);

    public static final String KEY_IGNORED_WORDS = "keyIgnoredWords";

    private static final int OK_CODE = 0;
    private static final int KO_CODE = 1;

    public static void main(final String[] args) {
	try {
	    ToolRunner.run(new Configuration(), new WordCountJobRunner(), args);
	} catch (Exception e) {
	    LOG.error("Failed job", e);
	    System.exit(KO_CODE);
	}
	System.exit(OK_CODE);
    }

    @Override
    public int run(String[] args) throws Exception {
	Configuration configuration = new Configuration();

	Job job = new Job(configuration, "wordcount");

	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(IntWritable.class);

	job.setMapperClass(WordCountMapper.class);
	job.setReducerClass(WordCountReducer.class);

	job.setInputFormatClass(TextInputFormat.class);
	job.setOutputFormatClass(TextOutputFormat.class);

	FileInputFormat.addInputPath(job, new Path(args[0]));
	FileOutputFormat.setOutputPath(job, new Path(args[1]));

	return job.waitForCompletion(true) ? OK_CODE : KO_CODE;
    }

}
