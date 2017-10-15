package main.java;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;

public class Driver extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Two params are required- <input path> <output path>");
        }
        Job job = Job.getInstance(getConf());

        job.setJobName("Browser statistics");
        job.setJarByClass(Driver.class);

        job.setInputFormatClass(BSInputFormat.class);
        BSInputFormat.addInputPath(job, new Path(args[0]));

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        getConf().set("mapreduce.output.textoutputformat.separator", ",");

        job.setMapperClass(BSMapper.class);
        job.setReducerClass(BSReducer.class);

        //    job.setCombinerClass(IntSumReducer.class); TODO: делать или нет?

        boolean success = job.waitForCompletion(true);
        Counter counter = job.getCounters().findCounter(BS_COUNTER.MALFORMED_ROWS); //TODO: куда-то запихнуть счетчики.
        System.out.println("Malformed rows count:" + counter.getValue());
        return  success ? 1 : 0;
    }

}