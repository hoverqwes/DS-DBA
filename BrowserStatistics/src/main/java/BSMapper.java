package main.java;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class BSMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
    private final static LongWritable one = new LongWritable(1);
    private Pattern pattern = Pattern.compile("^(\\S+) \\S+ \\S+ \\[([^\\]]+)\\] \"([A-Z]+)[^\"]*\" \\d+ \\d+ \"[^\"]*\" \"([a-zA-Z0-9\\.\\/]*).*\"$");
    private Matcher matcher;
    private Text browser = new Text();

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] logs = value.toString().split("\\n");
        for (String log: logs) {
            matcher = pattern.matcher(log);
            if (matcher.find()) {
                browser.set(matcher.group(4));
                context.write(browser, one);
            } else {
                context.getCounter(BS_COUNTER.MALFORMED_ROWS).increment(1);
            }
        }
    }
}