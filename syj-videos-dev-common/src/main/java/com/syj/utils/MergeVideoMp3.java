package com.syj.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoMp3 {

	private String ffmpegEXE;

	public MergeVideoMp3(String ffmpegEXE) {
		super();
		this.ffmpegEXE = ffmpegEXE;
	}

	public void convertor(String videoInputPath, String mp3InputPath, 
			double seconds, String videoOutputPath)
			throws IOException {
		// ffmpeg.exe -i spring.mp4 -i spring.mp3 -t 9 -y 新的视频.mp4
		List<String> command = new ArrayList<>();
		command.add(ffmpegEXE);

		command.add("-i");
		command.add(mp3InputPath);

		command.add("-i");
		command.add(videoInputPath);

		command.add("-t");
		command.add(String.valueOf(seconds));

		command.add("-y");
		command.add(videoOutputPath);
		for(String c:command) {
			System.out.print(c);
		}
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();
		InputStream errorStream = process.getErrorStream();
		InputStreamReader reader = new InputStreamReader(errorStream);
		BufferedReader breader = new BufferedReader(reader);
		String line = "";
		while ((line = breader.readLine()) != null) {

		}

		if (breader != null) {
			breader.close();
		}
		if (reader != null) {
			reader.close();
		}
		if (errorStream != null) {
			errorStream.close();
		}

		// 因为当我们在转换视频的时候，cpu和内存会被占用，
		// 被转换的一些临时碎片所占用，它在处理的时候会产生一些流，
		// inputstream和errorstream，当产生的流很多的时候会把我们当前线程卡住
		// 所以我需要读取这些流，也就是释放这些流避免资源浪费。

	}

	public static void main(String[] args) {

		MergeVideoMp3 ffMpegTest = new MergeVideoMp3("E:\\soft\\ffmpeg\\bin\\ffmpeg.exe");
		try {
			ffMpegTest.convertor("E:\\soft\\ffmpeg\\bin\\spring.mp4", "E:\\soft\\ffmpeg\\bin\\spring.mp3", 7.1,
					"E:\\soft\\ffmpeg\\bin\\这是通过java生成的视频.mp4");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
