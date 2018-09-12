package com.syj.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FFMpegTest {

	private String ffmpegEXE;

	public FFMpegTest(String ffmpegEXE) {
		super();
		this.ffmpegEXE = ffmpegEXE;
	}

	public void convertor(String videoInputStream, String videoOutputStream) throws IOException {
		// ffmpeg -i input.mp4 output.avi
		List<String> command = new ArrayList<>();
		command.add(ffmpegEXE);
		command.add("-i");
		command.add(videoInputStream);
		command.add(videoOutputStream);
//		for(String c:command) {
//			System.out.print(c);
//		}
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();
		InputStream errorStream = process.getErrorStream();
		InputStreamReader reader = new InputStreamReader(errorStream);
		BufferedReader breader = new BufferedReader(reader);
		String line = "";
		while ((line = breader.readLine()) != null) {
			
		}
		
		if(breader!=null) {
			breader.close();
		}
		if(reader!=null) {
			reader.close();
		}
		if(errorStream!=null) {
			errorStream.close();
		}

		// 因为当我们在转换视频的时候，cpu和内存会被占用，
		// 被转换的一些临时碎片所占用，它在处理的时候会产生一些流，
		// inputstream和errorstream，当产生的流很多的时候会把我们当前线程卡住
		// 所以我需要读取这些流，也就是释放这些流避免资源浪费。

	}

	public static void main(String[] args) {

		FFMpegTest ffMpegTest = new FFMpegTest("E:\\soft\\ffmpeg\\bin\\ffmpeg.exe");
		try {
			ffMpegTest.convertor("E:\\soft\\ffmpeg\\bin\\spring.mp4", "E:\\soft\\ffmpeg\\bin\\winter.avi");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
